package com.driveassistant.v2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.OpenableColumns;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import android.util.Log;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity implements BluetoothManager.StatusListener {
    private static final String NAV_CHANNEL_ID = "drive_assistant_nav_channel";
    private static final int NAV_NOTIFICATION_ID = 1001;
    private static final int REQUEST_CODE_GPX = 2002;

    // Bogo City Area default coordinates
    public static final double DEFAULT_BOGO_LAT = 11.0519;
    public static final double DEFAULT_BOGO_LNG = 124.0055;

    private WebView webView;
    private BluetoothManager btManager;
    private RouteManager routeManager;
    private LocationManager locationManager;
    private final AppLocationListener locationListener = new AppLocationListener(this);

    // Current GPS coordinates
    private double currentLat = DEFAULT_BOGO_LAT;
    private double currentLng = DEFAULT_BOGO_LNG;
    private float currentBearing = 0;
    private float currentSpeedKmh = 0;
    private boolean hasGpsFix = false;

    // Active Navigation & Route Simulation State
    private RouteManager.RouteResult activeRoute = null;
    private int currentStepIndex = 0;
    private boolean isNavigating = false;
    private boolean isSimulating = false;
    private float simulationSpeedKmh = 50.0f;
    private int simIndex = 0;
    private double simFraction = 0.0;
    private final Handler simHandler = new Handler(Looper.getMainLooper());
    private final SimulationRunnable simRunnable = new SimulationRunnable(this);
    private float lastTurnDist = -1.0f;
    private long lastTurnDistTimeMs = 0;
    private int lastRoutePointIndex = 0;
    private long lastNavPacketTimeMs = 0;
    private long lastVectorSendTimeMs = 0;
    private long lastNotifTimeMs = 0;
    private String lastSentManeuver = "";
    private String lastSentTurnDist = "";
    private double totalDistanceTraveledMeters = 0.0;
    private double lastOdoLat = 0.0;
    private double lastOdoLng = 0.0;
    private long lastWeatherPollTimeMs = 0;
    private String currentWeatherCond = "sun";
    private String currentWeatherTemp = "28°";
    private double currentDestLat = 0.0;
    private double currentDestLng = 0.0;
    private int offRouteCount = 0;
    private boolean isRerouting = false;

    private final ExecutorService tileExecutor = Executors.newSingleThreadExecutor();
    private volatile boolean isDownloadCancelled = false;

    public File getTilesDir() {
        File dir = new File(getFilesDir(), "map_tiles");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        routeManager = new RouteManager();
        routeManager.setContext(this);
        boolean isOffline = getSharedPreferences("DriveAssistantSettings", Context.MODE_PRIVATE)
                .getBoolean("offline_routing", false);
        routeManager.setOfflineMode(isOffline);

        boolean keepScreenOn = getSharedPreferences("DriveAssistantSettings", Context.MODE_PRIVATE)
                .getBoolean("keep_screen_on", true);
        setKeepScreenOn(keepScreenOn);

        btManager = new BluetoothManager(this, this);

        createNotificationChannel();
        setupWebView();
        checkPermissions();
        initLocation();
        btManager.startAutoConnect();
    }

    public void setKeepScreenOn(boolean keepOn) {
        if (keepOn) {
            getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        getSharedPreferences("DriveAssistantSettings", Context.MODE_PRIVATE)
                .edit().putBoolean("keep_screen_on", keepOn).apply();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && "ACTION_STOP_NAV".equals(intent.getAction())) {
            stopNavigationInternal();
            runOnUiThread(new StopNavJsRunnable(this));
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NAV_CHANNEL_ID,
                    "Drive Assistant Navigation",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Turn-by-turn maneuvers and ETA notification");
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setShowBadge(true);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
    }

    private void updateNavNotification(String maneuver, String distStr, String street, String etaStr, String remainingDistStr) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;

        Intent openIntent = new Intent(this, MainActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent openPendingIntent = PendingIntent.getActivity(this, 0, openIntent, flags);

        Intent exitIntent = new Intent(this, MainActivity.class);
        exitIntent.setAction("ACTION_STOP_NAV");
        PendingIntent exitPendingIntent = PendingIntent.getActivity(this, 1, exitIntent, flags);

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, NAV_CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        String title = "In " + distStr + " • " + formatManeuverTitle(maneuver);
        String content = street + " • ETA " + etaStr + " (" + remainingDistStr + ")";

        builder.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setSubText("Trip in Progress")
                .setContentIntent(openPendingIntent)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "End Route", exitPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        nm.notify(NAV_NOTIFICATION_ID, builder.build());
    }

    private void cancelNavNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancel(NAV_NOTIFICATION_ID);
        }
    }

    private static String formatManeuverTitle(String maneuver) {
        if (maneuver == null) return "Continue Straight";
        switch (maneuver) {
            case "turn-right": return "Turn Right";
            case "turn-left": return "Turn Left";
            case "slight-right": return "Bear Right";
            case "slight-left": return "Bear Left";
            case "sharp-right": return "Sharp Right";
            case "sharp-left": return "Sharp Left";
            case "roundabout": return "At Roundabout";
            case "uturn": return "Make U-Turn";
            case "arrive": return "Destination Ahead";
            default: return "Continue Straight";
        }
    }

    private void setupWebView() {
        FrameLayout root = new FrameLayout(this);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        webView = new WebView(this);
        webView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setDatabaseEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new AppWebViewClient(this));

        webView.addJavascriptInterface(new AndroidBridge(this), "AndroidBridge");
        webView.loadUrl("file:///android_asset/map.html");

        root.addView(webView);
        setContentView(root);
    }

    private static class AppWebViewClient extends WebViewClient {
        private final MainActivity act;
        AppWebViewClient(MainActivity act) { this.act = act; }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (request != null && request.getUrl() != null) {
                WebResourceResponse resp = handleTileIntercept(request.getUrl());
                if (resp != null) return resp;
            }
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (url != null) {
                WebResourceResponse resp = handleTileIntercept(Uri.parse(url));
                if (resp != null) return resp;
            }
            return super.shouldInterceptRequest(view, url);
        }

        private WebResourceResponse handleTileIntercept(Uri uri) {
            if (uri != null) {
                String host = uri.getHost();
                String path = uri.getPath();
                if (host != null && host.contains("openstreetmap.org") && path != null && path.endsWith(".png")) {
                    while (path.startsWith("/")) {
                        path = path.substring(1);
                    }
                    File tileFile = new File(act.getTilesDir(), path);
                    if (tileFile.exists() && tileFile.length() > 0) {
                        try {
                            return new WebResourceResponse("image/png", null, new FileInputStream(tileFile));
                        } catch (Exception ignored) {}
                    }
                }
            }
            return null;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            act.fetchInstantLocation(false);
            if (act.btManager != null) {
                act.webView.evaluateJavascript("setBluetoothState(" + act.btManager.isConnected() + ", 'AMOLED');", null);
            }
            act.updateRoutingModeInJs();
            act.updateOfflineStorageInfoInJs();
            act.updateKeepScreenOnInJs();
        }
    }

    // ====== LOCATION SERVICES ======
    public void fetchInstantLocation(boolean panMap) {
        if (isSimulating) return;
        if (locationManager == null) locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) return;

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location best = null;
            try {
                Location gps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location net = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (gps != null && net != null) {
                    best = (gps.getTime() > net.getTime()) ? gps : net;
                } else if (gps != null) {
                    best = gps;
                } else {
                    best = net;
                }
            } catch (Exception ignored) {}

            if (best != null) {
                onLocationChanged(best);
                if (panMap) {
                    final double lat = best.getLatitude();
                    final double lng = best.getLongitude();
                    runOnUiThread(new PanMapRunnable(this, lat, lng, 16));
                }
            }
        }
    }

    private void initLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) return;

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fetchInstantLocation(false);

            // Register GPS Provider
            try {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, locationListener);
                }
            } catch (Exception ignored) {}

            // Register Network Provider
            try {
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 2, locationListener);
                }
            } catch (Exception ignored) {}
        }
    }

    void onLocationChanged(Location loc) {
        if (loc == null) return;
        if (isSimulating && !"sim".equals(loc.getProvider())) return;

        if (lastOdoLat != 0.0 && lastOdoLng != 0.0) {
            float[] d = new float[1];
            Location.distanceBetween(lastOdoLat, lastOdoLng, loc.getLatitude(), loc.getLongitude(), d);
            if (d[0] > 0.05f && d[0] < 250.0f) {
                totalDistanceTraveledMeters += d[0];
            }
        }
        lastOdoLat = loc.getLatitude();
        lastOdoLng = loc.getLongitude();

        currentLat = loc.getLatitude();
        currentLng = loc.getLongitude();
        currentBearing = loc.hasBearing() ? loc.getBearing() : 0.0f;
        currentSpeedKmh = loc.hasSpeed() ? (loc.getSpeed() * 3.6f) : 0.0f;
        hasGpsFix = true;
        checkPollWeather(currentLat, currentLng);

        final double lat = currentLat;
        final double lng = currentLng;
        final float bearing = currentBearing;
        final float accuracy = loc.getAccuracy();
        final float speed = currentSpeedKmh;

        runOnUiThread(new UserLocationRunnable(this, lat, lng, bearing, accuracy, speed));

        // Turn-by-Turn Navigation Update Engine
        if (isNavigating && activeRoute != null && !activeRoute.steps.isEmpty()) {
            updateActiveNavigation();
        }
    }

    private void checkPollWeather(final double lat, final double lng) {
        long now = SystemClock.elapsedRealtime();
        // Poll once every 15 minutes (900,000 ms), or on first GPS fix
        if (lastWeatherPollTimeMs != 0 && (now - lastWeatherPollTimeMs < 15 * 60 * 1000L)) {
            if (btManager != null && btManager.isConnected() && currentWeatherCond != null) {
                btManager.sendWeatherPacket(currentWeatherCond, currentWeatherTemp);
            }
            return;
        }
        lastWeatherPollTimeMs = now;
        new Thread(new WeatherRunnable(this, lat, lng)).start();
    }

    private static class WeatherRunnable implements Runnable {
        private final MainActivity act;
        private final double lat;
        private final double lng;

        WeatherRunnable(MainActivity act, double lat, double lng) {
            this.act = act;
            this.lat = lat;
            this.lng = lng;
        }

        @Override public void run() {
            try {
                String urlStr = String.format(Locale.US,
                    "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&current=weather_code,temperature_2m,is_day",
                    lat, lng);
                java.net.URL url = new java.net.URL(urlStr);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(6000);
                conn.setReadTimeout(6000);
                if (conn.getResponseCode() == 200) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    reader.close();

                    org.json.JSONObject root = new org.json.JSONObject(sb.toString());
                    if (root.has("current")) {
                        org.json.JSONObject cur = root.getJSONObject("current");
                        int code = cur.optInt("weather_code", 0);
                        double temp = cur.optDouble("temperature_2m", 28.0);
                        int isDay = cur.optInt("is_day", 1);

                        String cond = (isDay == 0) ? "night" : "sun";
                        if (code == 0) {
                            cond = (isDay == 0) ? "night" : "sun";
                        } else if (code == 1 || code == 2) {
                            cond = (isDay == 0) ? "night_cloud" : "pcloudy";
                        } else if (code == 3) {
                            cond = "cloudy";
                        } else if (code == 45 || code == 48) {
                            cond = "fog";
                        } else if ((code >= 51 && code <= 67) || (code >= 80 && code <= 82)) {
                            cond = "rain";
                        } else if ((code >= 71 && code <= 77) || (code >= 85 && code <= 86)) {
                            cond = "snow";
                        } else if (code >= 95 && code <= 99) {
                            cond = "thunder";
                        }

                        act.currentWeatherCond = cond;
                        act.currentWeatherTemp = Math.round(temp) + "°";

                        Log.i("MainActivity", "Weather polled: " + cond + " " + act.currentWeatherTemp + " (isDay=" + isDay + ", code=" + code + ")");
                        if (act.btManager != null && act.btManager.isConnected()) {
                            act.btManager.sendWeatherPacket(act.currentWeatherCond, act.currentWeatherTemp);
                        }
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.w("MainActivity", "Weather fetch error: " + e.getMessage());
            }
        }
    }

    private void updateActiveNavigation() {
        if (activeRoute == null || activeRoute.steps.isEmpty()) return;
        if (isRerouting) return;

        // 1. Off-Route Detection & Auto-Reroute
        if (activeRoute.pathPoints != null && activeRoute.pathPoints.size() >= 2 && currentDestLat != 0.0) {
            float[] destDist = new float[1];
            Location.distanceBetween(currentLat, currentLng, currentDestLat, currentDestLng, destDist);
            if (destDist[0] > 35.0f) {
                double distToRoute = getDistanceToRouteMeters(currentLat, currentLng, activeRoute);
                if (distToRoute > 45.0) {
                    offRouteCount++;
                    Log.d("MainActivity", "Off-route detected: dist=" + Math.round(distToRoute) + "m, count=" + offRouteCount);
                    if (offRouteCount >= 3 && !isRerouting) {
                        triggerAutoReroute();
                        return;
                    }
                } else {
                    offRouteCount = 0;
                }
            }
        }

        // Ensure current step index is within bounds
        if (currentStepIndex >= activeRoute.steps.size()) {
            currentStepIndex = activeRoute.steps.size() - 1;
        }

        RouteManager.Step step = activeRoute.steps.get(currentStepIndex);
        float[] distResult = new float[1];
        Location.distanceBetween(currentLat, currentLng, step.startLat, step.startLng, distResult);
        float distToTurn = distResult[0];

        // Advance to next step when user reaches the intersection:
        // - At start leg (currentStepIndex == 0): advance when user is on track (within 25m)
        // - For intermediate steps: advance when within 10m of the turn point,
        //   or when passing through the turn (distance starts increasing while close < 25m)
        boolean passedTurn = (currentStepIndex > 0 && lastTurnDist > 0 && lastTurnDist < 25.0f && distToTurn > lastTurnDist + 3.0f);
        boolean reachedTurn = (currentStepIndex == 0) ? (distToTurn < 25.0f) : (distToTurn <= 10.0f);

        if ((reachedTurn || passedTurn) && currentStepIndex + 1 < activeRoute.steps.size()) {
            currentStepIndex++;
            step = activeRoute.steps.get(currentStepIndex);
            Location.distanceBetween(currentLat, currentLng, step.startLat, step.startLng, distResult);
            distToTurn = distResult[0];
            lastTurnDist = distToTurn;
            lastTurnDistTimeMs = SystemClock.elapsedRealtime();
        }

        // Remaining distance calculation
        double remainingDistanceMeters = distToTurn;
        for (int i = currentStepIndex + 1; i < activeRoute.steps.size(); i++) {
            remainingDistanceMeters += activeRoute.steps.get(i).distanceMeters;
        }

        // Remaining duration & ETA calculation
        // Default pace is 35 km/h if current speed is low, or current speed if cruising
        float paceKmh = (currentSpeedKmh > 10.0f) ? currentSpeedKmh : 35.0f;
        double remainingDurationSec = (remainingDistanceMeters / (paceKmh * 1000.0 / 3600.0));
        long etaMillis = System.currentTimeMillis() + (long) (remainingDurationSec * 1000);

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String etaStr = timeFormat.format(new Date(etaMillis));

        String turnDistStr = formatAdaptiveDistance(distToTurn, currentSpeedKmh, lastTurnDist, lastTurnDistTimeMs);
        String remDistStr = formatAdaptiveDistance(remainingDistanceMeters, currentSpeedKmh, -1.0f, 0);

        lastTurnDist = distToTurn;
        lastTurnDistTimeMs = SystemClock.elapsedRealtime();

        int remMin = Math.max(1, (int) Math.round(remainingDurationSec / 60.0));
        String remDurationStr = (remMin < 60) ? (remMin + " min") : ((remMin / 60) + " h " + (remMin % 60) + " m");

        // Next maneuver preview
        String nextNextManeuver = "";
        if (currentStepIndex + 1 < activeRoute.steps.size()) {
            RouteManager.Step next = activeRoute.steps.get(currentStepIndex + 1);
            nextNextManeuver = next.maneuver + "|" + next.street;
        }

        long now = SystemClock.elapsedRealtime();

        // 1. Update Android Ongoing System Notification (throttled to 1 Hz unless maneuver changes)
        if (now - lastNotifTimeMs >= 1000 || !step.maneuver.equals(lastSentManeuver)) {
            lastNotifTimeMs = now;
            updateNavNotification(step.maneuver, turnDistStr, step.street, etaStr, remDistStr);
        }

        // 2. Sync Nav Text to ESP32 AMOLED via Bluetooth LE (throttled to 2-3 Hz or on maneuver/dist change)
        int pct = (step.distanceMeters > 0) ? (int) (100 * (1.0f - (distToTurn / step.distanceMeters))) : 0;
        if (now - lastNavPacketTimeMs >= 350 || !step.maneuver.equals(lastSentManeuver) || !turnDistStr.equals(lastSentTurnDist)) {
            lastNavPacketTimeMs = now;
            lastSentManeuver = step.maneuver;
            lastSentTurnDist = turnDistStr;
            btManager.sendNavPacket(step.maneuver, turnDistStr, step.street, Math.max(0, Math.min(100, pct)), etaStr, remDistStr);
        }

        // 3. Stream 3D Vector Road Geometry at up to 20 Hz
        if (now - lastVectorSendTimeMs >= 45) {
            lastVectorSendTimeMs = now;
            updateRoadVectors();
        }

        // 3. Update In-App Navigation HUD
        final String m = step.maneuver;
        final String td = turnDistStr;
        final String st = step.street;
        final String rd = remDistStr;
        final String rdur = remDurationStr;
        final String eta = etaStr;
        final String nnm = nextNextManeuver;
        final float spd = currentSpeedKmh;

        runOnUiThread(new UpdateNavHudRunnable(this, m, td, st, rd, rdur, eta, nnm, spd));
    }

    private void triggerAutoReroute() {
        if (isRerouting) return;
        isRerouting = true;
        offRouteCount = 0;

        Log.i("MainActivity", "User off-route. Triggering auto-reroute to " + currentDestLat + ", " + currentDestLng);
        runOnUiThread(new ToastRunnable(this, "Off route. Re-routing..."));

        // Notify ESP32 AMOLED device immediately with reroute status:
        if (btManager != null && btManager.isConnected()) {
            btManager.sendNavPacket("reroute", "--", "Re-routing...", 0, "--", "--");
        }

        // Notify in-app HUD:
        runOnUiThread(new UpdateNavHudRunnable(this, "reroute", "--", "Re-routing...", "--", "--", "--", "", currentSpeedKmh));

        routeManager.calculateRoute(currentLat, currentLng, currentDestLat, currentDestLng, new NavCalculationCallback(this));
    }

    private double getDistanceToRouteMeters(double pLat, double pLng, RouteManager.RouteResult route) {
        if (route == null || route.pathPoints == null || route.pathPoints.size() < 2) {
            return 0.0;
        }
        List<double[]> pts = route.pathPoints;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < pts.size() - 1; i++) {
            double[] a = pts.get(i);
            double[] b = pts.get(i + 1);
            double dist = distanceToSegmentMeters(pLat, pLng, a[0], a[1], b[0], b[1]);
            if (dist < minDistance) {
                minDistance = dist;
            }
        }
        return minDistance;
    }

    private static double distanceToSegmentMeters(double pLat, double pLng,
                                                  double aLat, double aLng,
                                                  double bLat, double bLng) {
        double cosLat = Math.cos(Math.toRadians(aLat));
        double xB = (bLng - aLng) * 111139.0 * cosLat;
        double yB = (bLat - aLat) * 111139.0;
        double xP = (pLng - aLng) * 111139.0 * cosLat;
        double yP = (pLat - aLat) * 111139.0;

        double l2 = xB * xB + yB * yB;
        if (l2 < 0.001) {
            return Math.sqrt(xP * xP + yP * yP);
        }

        double t = (xP * xB + yP * yB) / l2;
        if (t < 0.0) t = 0.0;
        else if (t > 1.0) t = 1.0;

        double xC = t * xB;
        double yC = t * yB;
        double dx = xP - xC;
        double dy = yP - yC;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void updateRoadVectors() {
        if (!btManager.isConnected() || activeRoute == null || activeRoute.pathPoints == null || activeRoute.pathPoints.size() < 2) {
            return;
        }

        List<double[]> pts = activeRoute.pathPoints;
        int nPts = pts.size();

        // 1. Locate the closest polyline point near current vehicle position
        int searchStart = Math.max(0, lastRoutePointIndex - 5);
        int searchEnd = Math.min(nPts - 1, lastRoutePointIndex + 40);
        if (lastRoutePointIndex == 0 || searchStart >= searchEnd) {
            searchStart = 0;
            searchEnd = nPts - 1;
        }

        int closestIdx = searchStart;
        double minDistSq = Double.MAX_VALUE;
        for (int i = searchStart; i <= searchEnd; i++) {
            double[] p = pts.get(i);
            double dLat = p[0] - currentLat;
            double dLng = p[1] - currentLng;
            double dSq = dLat * dLat + dLng * dLng;
            if (dSq < minDistSq) {
                minDistSq = dSq;
                closestIdx = i;
            }
        }
        lastRoutePointIndex = closestIdx;

        // 2. Project route vertices ahead of the vehicle into vehicle coordinate space (meters):
        //    Z = forward along heading, X = lateral (+right, -left)
        double headingRad = Math.toRadians(currentBearing);
        double cosH = Math.cos(headingRad);
        double sinH = Math.sin(headingRad);
        double cosLat = Math.cos(Math.toRadians(currentLat));

        List<int[]> routePoints = new ArrayList<>();
        // Vehicle is anchored at (0, 0)
        routePoints.add(new int[]{0, 0});

        for (int i = closestIdx; i < nPts; i++) {
            double[] p = pts.get(i);
            double dLat = p[0] - currentLat;
            double dLng = p[1] - currentLng;
            double dNorth = dLat * 111139.0;
            double dEast = dLng * 111139.0 * cosLat;

            double z = dNorth * cosH + dEast * sinH;
            double x = dEast * cosH - dNorth * sinH;

            // Discard vertices behind the vehicle
            if (z < -3.0) continue;

            int[] last = routePoints.get(routePoints.size() - 1);
            double dx = x - last[0];
            double dz = z - last[1];
            double segLen = Math.sqrt(dx * dx + dz * dz);

            // Filter out dense redundant micro-segments (< 4 meters)
            if (segLen < 4.0 && routePoints.size() > 1) continue;

            if (z > 240.0) {
                // Interpolate to horizon at z = 240
                if (z > last[1]) {
                    double t = (240.0 - last[1]) / (z - last[1]);
                    int xHorizon = (int) Math.round(last[0] + t * (x - last[0]));
                    routePoints.add(new int[]{xHorizon, 240});
                }
                break;
            }

            routePoints.add(new int[]{(int) Math.round(x), (int) Math.round(z)});
            if (routePoints.size() >= 14) break; // Keep active path within budget
        }

        // If only vehicle anchor point exists, project straight forward
        if (routePoints.size() == 1) {
            routePoints.add(new int[]{0, 240});
        }

        List<String> segs = new ArrayList<>();
        // Add active route segments (type = 1: solid white)
        for (int i = 0; i < routePoints.size() - 1; i++) {
            int[] p0 = routePoints.get(i);
            int[] p1 = routePoints.get(i + 1);
            segs.add(p0[0] + "," + p0[1] + "," + p1[0] + "," + p1[1] + ",1");
        }

        // 3. Add non-route crossroads and side street branches (type = 0: double border road)
        // A) Upcoming turn intersection geometry
        int zTurnInt = -1;
        if (currentStepIndex < activeRoute.steps.size()) {
            RouteManager.Step step = activeRoute.steps.get(currentStepIndex);
            double sLat = step.startLat - currentLat;
            double sLng = step.startLng - currentLng;
            double sNorth = sLat * 111139.0;
            double sEast = sLng * 111139.0 * cosLat;
            int zTurn = (int) Math.round(sNorth * cosH + sEast * sinH);
            int xTurn = (int) Math.round(sEast * cosH - sNorth * sinH);
            zTurnInt = zTurn;

            if (zTurn >= 8 && zTurn <= 230) {
                String mn = step.maneuver != null ? step.maneuver.toLowerCase(Locale.US) : "";
                if (mn.contains("right")) {
                    // Straight continuation road not taken
                    segs.add(xTurn + "," + zTurn + "," + xTurn + "," + Math.min(240, zTurn + 110) + ",0");
                    // Left cross-arm (junction -> tail)
                    segs.add(xTurn + "," + zTurn + "," + (xTurn - 110) + "," + zTurn + ",0");
                } else if (mn.contains("left")) {
                    // Straight continuation road not taken
                    segs.add(xTurn + "," + zTurn + "," + xTurn + "," + Math.min(240, zTurn + 110) + ",0");
                    // Right cross-arm (junction -> tail)
                    segs.add(xTurn + "," + zTurn + "," + (xTurn + 110) + "," + zTurn + ",0");
                } else if (mn.contains("fork") || mn.contains("slight")) {
                    // Alternate branch (junction -> tail)
                    int forkX = (mn.contains("left")) ? (xTurn + 65) : (xTurn - 65);
                    segs.add(xTurn + "," + zTurn + "," + forkX + "," + Math.min(240, zTurn + 95) + ",0");
                } else if (mn.contains("roundabout")) {
                    // Cross arms representing other roundabout exits (junction -> tail)
                    segs.add((xTurn - 10) + "," + zTurn + "," + (xTurn - 85) + "," + zTurn + ",0");
                    segs.add(xTurn + "," + (zTurn + 15) + "," + xTurn + "," + Math.min(240, zTurn + 110) + ",0");
                }
            }
        }

        // B) Dynamic moving side streets & crossroads (tied to traveled distance)
        double gridSpacing = 65.0;
        double phase = totalDistanceTraveledMeters % gridSpacing;
        double firstZ = gridSpacing - phase;

        for (int k = 0; k < 4; k++) {
            double zSide = firstZ + k * gridSpacing;
            if (zSide < 6.0 || zSide > 225.0) continue;
            if (zTurnInt >= 0 && Math.abs(zSide - zTurnInt) < 20.0) continue; // avoid collision with turn

            // Interpolate lateral X along active route
            int xSide = 0;
            for (int j = 0; j < routePoints.size() - 1; j++) {
                int[] pA = routePoints.get(j);
                int[] pB = routePoints.get(j + 1);
                if (zSide >= pA[1] && zSide <= pB[1]) {
                    double t = (zSide - pA[1]) / (double) Math.max(1, pB[1] - pA[1]);
                    xSide = (int) Math.round(pA[0] + t * (pB[0] - pA[0]));
                    break;
                }
            }

            int pattern = Math.abs((int) Math.floor((totalDistanceTraveledMeters / gridSpacing) + k)) % 3;
            int zS = (int) Math.round(zSide);
            if (pattern == 0) {
                // Full cross street (both left and right tails fade away from active route)
                segs.add(xSide + "," + zS + "," + (xSide - 95) + "," + zS + ",0");
                segs.add(xSide + "," + zS + "," + (xSide + 95) + "," + zS + ",0");
            } else if (pattern == 1) {
                // T-junction Right
                segs.add(xSide + "," + zS + "," + (xSide + 95) + "," + zS + ",0");
            } else {
                // T-junction Left
                segs.add(xSide + "," + zS + "," + (xSide - 95) + "," + zS + ",0");
            }
            if (segs.size() >= 23) break;
        }

        // Cap at MAX_ROAD_SEGMENTS (24)
        int count = Math.min(segs.size(), 24);
        StringBuilder sb = new StringBuilder("vec|");
        sb.append(count).append("|").append((int) Math.round(currentSpeedKmh)).append("|");
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append(";");
            sb.append(segs.get(i));
        }

        btManager.sendVectorRoad(sb.toString());
    }

    private static class VectorRoadRunnable implements Runnable {
        private final MainActivity act;
        VectorRoadRunnable(MainActivity act) { this.act = act; }
        @Override public void run() { act.updateRoadVectors(); }
    }

    public static String formatAdaptiveDistance(double distMeters, float speedKmh, float lastDist, long lastTimeMs) {
        if (distMeters <= 0) return "0 m";

        // Determine effective speed from reported speed or distance change rate
        float effectiveSpeed = speedKmh;
        long now = SystemClock.elapsedRealtime();
        if (lastTimeMs > 0 && lastDist > 0) {
            long dtMs = now - lastTimeMs;
            if (dtMs >= 200 && dtMs <= 5000) {
                double deltaM = Math.abs(distMeters - lastDist);
                float derivedSpeed = (float) ((deltaM / (dtMs / 1000.0)) * 3.6);
                if (derivedSpeed > effectiveSpeed) {
                    effectiveSpeed = derivedSpeed;
                }
            }
        }

        // Above 1000m: display in km with 1 decimal place (100m resolution)
        if (distMeters >= 1000.0) {
            double km = Math.round(distMeters / 100.0) / 10.0;
            return String.format(Locale.US, "%.1f km", km);
        }

        // Quantize distance based on vehicle speed / rate of change:
        // - Slow moving (<= 15 km/h): 5m increments (900m -> 895m -> 890m ... 10m -> 5m)
        // - Moderate speed (15..45 km/h):
        //     >= 500m: 50m increments (900m -> 850m -> 800m)
        //     200..499m: 50m increments (350m -> 300m -> 250m)
        //     50..199m: 10m increments (190m -> 180m -> ... -> 50m)
        //     < 50m: 5m increments (45m -> 40m -> ... -> 5m)
        // - Fast moving (> 45 km/h):
        //     >= 400m: 100m increments (900m -> 800m -> 700m)
        //     150..399m: 50m increments (350m -> 300m -> 250m)
        //     40..149m: 10m increments (140m -> 130m -> ... -> 40m)
        //     < 40m: 5m increments (35m -> 30m -> ... -> 5m)
        int step;
        if (effectiveSpeed <= 15.0f) {
            step = 5;
        } else if (effectiveSpeed <= 45.0f) {
            if (distMeters >= 500.0) {
                step = 50;
            } else if (distMeters >= 200.0) {
                step = 50;
            } else if (distMeters >= 50.0) {
                step = 10;
            } else {
                step = 5;
            }
        } else {
            if (distMeters >= 400.0) {
                step = 100;
            } else if (distMeters >= 150.0) {
                step = 50;
            } else if (distMeters >= 40.0) {
                step = 10;
            } else {
                step = 5;
            }
        }

        int rounded = (int) (Math.round(distMeters / (double) step) * step);
        if (rounded >= 1000) {
            return "1.0 km";
        }
        if (rounded <= 0 && distMeters > 0) {
            rounded = (distMeters < 2.5) ? 0 : step;
        }
        return rounded + " m";
    }

    // ====== SIMULATION ENGINE ======
    public void startSimulation(float speedKmh) {
        if (activeRoute == null || activeRoute.pathPoints.size() < 2) {
            Toast.makeText(this, "Please calculate or import a route first to simulate", Toast.LENGTH_SHORT).show();
            return;
        }
        this.simulationSpeedKmh = speedKmh > 0 ? speedKmh : 50.0f;
        this.simIndex = 0;
        this.simFraction = 0.0;
        this.totalDistanceTraveledMeters = 0.0;
        this.lastOdoLat = 0.0;
        this.lastOdoLng = 0.0;
        this.isSimulating = true;
        this.isNavigating = true;
        this.currentStepIndex = 0;

        simHandler.removeCallbacks(simRunnable);
        simHandler.post(simRunnable);

        Toast.makeText(this, "Simulating route at " + (int) simulationSpeedKmh + " km/h", Toast.LENGTH_SHORT).show();
    }

    public void setSimulationSpeed(float speedKmh) {
        this.simulationSpeedKmh = speedKmh;
        Toast.makeText(this, "Simulation speed: " + (int) simulationSpeedKmh + " km/h", Toast.LENGTH_SHORT).show();
    }

    public void stopSimulation() {
        isSimulating = false;
        simHandler.removeCallbacks(simRunnable);
    }

    private void tickSimulation() {
        if (!isSimulating || activeRoute == null || activeRoute.pathPoints.size() < 2) {
            isSimulating = false;
            return;
        }

        List<double[]> pts = activeRoute.pathPoints;
        if (simIndex >= pts.size() - 1) {
            double[] last = pts.get(pts.size() - 1);
            Location endLoc = createSimLocation(last[0], last[1], currentBearing, 0.0f);
            onLocationChanged(endLoc);
            stopNavigationInternal();
            runOnUiThread(new StopNavJsRunnable(this));
            runOnUiThread(new ToastRunnable(this, "Simulation completed: Reached destination"));
            return;
        }

        // Distance moved in 0.05 sec tick (20 FPS simulation): (speedKmh * 1000 / 3600) * 0.05
        double dtSec = 0.05;
        double distToMove = (simulationSpeedKmh * 1000.0 / 3600.0) * dtSec;
        float[] segDist = new float[1];

        while (distToMove > 0 && simIndex < pts.size() - 1) {
            double[] p1 = pts.get(simIndex);
            double[] p2 = pts.get(simIndex + 1);
            Location.distanceBetween(p1[0], p1[1], p2[0], p2[1], segDist);
            double segRemaining = Math.max(0.1, segDist[0] * (1.0 - simFraction));

            if (distToMove >= segRemaining) {
                distToMove -= segRemaining;
                simIndex++;
                simFraction = 0.0;
            } else {
                simFraction += (distToMove / segDist[0]);
                distToMove = 0;
            }
        }

        if (simIndex >= pts.size() - 1) {
            simIndex = pts.size() - 1;
            simFraction = 0.0;
        }

        double[] currentPt;
        double bearing = currentBearing;
        if (simIndex < pts.size() - 1) {
            double[] p1 = pts.get(simIndex);
            double[] p2 = pts.get(simIndex + 1);
            double lat = p1[0] + (p2[0] - p1[0]) * simFraction;
            double lng = p1[1] + (p2[1] - p1[1]) * simFraction;
            bearing = calculateBearing(p1[0], p1[1], p2[0], p2[1]);
            currentPt = new double[]{lat, lng};
        } else {
            currentPt = pts.get(pts.size() - 1);
        }

        Location simLoc = createSimLocation(currentPt[0], currentPt[1], (float) bearing, (float) (simulationSpeedKmh / 3.6f));
        onLocationChanged(simLoc);

        if (isSimulating) {
            simHandler.postDelayed(simRunnable, 50);
        }
    }

    private Location createSimLocation(double lat, double lng, float bearing, float speedMps) {
        Location loc = new Location("sim");
        loc.setLatitude(lat);
        loc.setLongitude(lng);
        loc.setBearing(bearing);
        loc.setSpeed(speedMps);
        loc.setAccuracy(2.0f);
        loc.setTime(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            loc.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        return loc;
    }

    private static double calculateBearing(double lat1, double lon1, double lat2, double lon2) {
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaLambda = Math.toRadians(lon2 - lon1);
        double y = Math.sin(deltaLambda) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2) - Math.sin(phi1) * Math.cos(phi2) * Math.cos(deltaLambda);
        double theta = Math.atan2(y, x);
        return (Math.toDegrees(theta) + 360) % 360;
    }

    private void stopNavigationInternal() {
        isNavigating = false;
        lastTurnDist = -1.0f;
        lastTurnDistTimeMs = 0;
        lastRoutePointIndex = 0;
        stopSimulation();
        cancelNavNotification();
        btManager.sendNavPacket("arrive", "Arrived", "Trip Ended", 100);
        btManager.sendVectorRoad("vec|0|");
    }

    private static class AppLocationListener implements LocationListener {
        private final MainActivity act;
        AppLocationListener(MainActivity act) { this.act = act; }
        @Override public void onLocationChanged(Location loc) {
            if (act.isSimulating) return; // Prevent hardware GPS from teleporting during route simulation
            act.onLocationChanged(loc);
        }
        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override public void onProviderEnabled(String provider) {}
        @Override public void onProviderDisabled(String provider) {}
    }

    // ====== BLUETOOTH LISTENER ======
    @Override
    public void onConnectionStateChange(boolean connected, String deviceName) {
        runOnUiThread(new BtStateRunnable(this, connected, deviceName));
    }

    @Override
    public void onGattReady() {
        runOnUiThread(new GattReadyRunnable(this));
    }

    @Override
    public void onLog(String message) {
        runOnUiThread(new ToastRunnable(this, message));
    }

    // ====== JAVASCRIPT BRIDGE ======
    public static class AndroidBridge {
        private final MainActivity act;
        AndroidBridge(MainActivity act) { this.act = act; }

        @JavascriptInterface
        public void requestCurrentLocation(boolean isStart) {
            act.runOnUiThread(new RequestCurrentLocationRunnable(act, isStart));
        }

        @JavascriptInterface
        public void showSearchDialog(boolean isStart) {
            act.runOnUiThread(new ShowSearchDialogRunnable(act, isStart));
        }

        @JavascriptInterface
        public void reverseGeocode(double lat, double lng) {
            act.routeManager.reverseGeocode(lat, lng, new PinReverseGeocodeCallback(act));
        }

        @JavascriptInterface
        public void calculateRoute(double startLat, double startLng, double destLat, double destLng) {
            act.currentDestLat = destLat;
            act.currentDestLng = destLng;
            act.runOnUiThread(new ToastRunnable(act, "Calculating route..."));
            act.routeManager.calculateRoute(startLat, startLng, destLat, destLng, new NavCalculationCallback(act));
        }

        @JavascriptInterface
        public void startNavigation() {
            act.isNavigating = true;
            act.currentStepIndex = 0;
            act.lastTurnDist = -1.0f;
            act.lastTurnDistTimeMs = 0;
            if (act.activeRoute != null && !act.activeRoute.steps.isEmpty()) {
                act.updateActiveNavigation();
                act.runOnUiThread(new ToastRunnable(act, "Navigation started! Synced with ESP32 AMOLED"));
            }
        }

        @JavascriptInterface
        public void stopNavigation() {
            act.stopNavigationInternal();
            act.runOnUiThread(new ToastRunnable(act, "Navigation stopped"));
        }

        @JavascriptInterface
        public void clearRoute() {
            act.activeRoute = null;
            act.isNavigating = false;
            act.lastTurnDist = -1.0f;
            act.lastTurnDistTimeMs = 0;
            act.stopSimulation();
            act.cancelNavNotification();
            act.btManager.sendNavPacket("clear", "", "", 0);
            act.runOnUiThread(new ToastRunnable(act, "Route selection reset"));
        }

        @JavascriptInterface
        public void toggleBluetooth() {
            if (act.btManager.isConnected()) {
                act.btManager.disconnect();
            } else {
                act.btManager.startAutoConnect();
            }
        }

        @JavascriptInterface
        public void startSimulation(float speedKmh) {
            act.runOnUiThread(new StartSimRunnable(act, speedKmh));
        }

        @JavascriptInterface
        public void setSimulationSpeed(float speedKmh) {
            act.runOnUiThread(new SetSimSpeedRunnable(act, speedKmh));
        }

        @JavascriptInterface
        public void stopSimulation() {
            act.runOnUiThread(new StopSimRunnable(act));
        }

        @JavascriptInterface
        public void savePref(String key, String val) {
            act.getSharedPreferences("DriveAssistantRoutes", Context.MODE_PRIVATE)
               .edit().putString(key, val).apply();
        }

        @JavascriptInterface
        public String getPref(String key, String defaultVal) {
            return act.getSharedPreferences("DriveAssistantRoutes", Context.MODE_PRIVATE)
                      .getString(key, defaultVal);
        }

        @JavascriptInterface
        public void showToast(String msg) {
            act.runOnUiThread(new ToastRunnable(act, msg));
        }

        @JavascriptInterface
        public void setRoutingMode(boolean isOffline) {
            act.getSharedPreferences("DriveAssistantSettings", Context.MODE_PRIVATE)
               .edit().putBoolean("offline_routing", isOffline).apply();
            act.routeManager.setOfflineMode(isOffline);
            act.runOnUiThread(new ToastRunnable(act, isOffline ? "Routing Mode: Offline (Local Direct Vectors)" : "Routing Mode: Online (OSRM Service)"));
        }

        @JavascriptInterface
        public boolean isRoutingOffline() {
            return act.routeManager.isOfflineMode();
        }

        @JavascriptInterface
        public void setKeepScreenOn(boolean keepOn) {
            act.runOnUiThread(new SetKeepScreenOnRunnable(act, keepOn));
            act.runOnUiThread(new ToastRunnable(act, keepOn ? "Screen will stay awake during navigation" : "Standard screen timeout restored"));
        }

        @JavascriptInterface
        public boolean isKeepScreenOn() {
            return act.getSharedPreferences("DriveAssistantSettings", Context.MODE_PRIVATE)
                    .getBoolean("keep_screen_on", true);
        }

        @JavascriptInterface
        public void downloadRegion(double minLat, double minLng, double maxLat, double maxLng, int minZoom, int maxZoom) {
            act.startRegionDownload(minLat, minLng, maxLat, maxLng, minZoom, maxZoom);
        }

        @JavascriptInterface
        public void cancelRegionDownload() {
            act.cancelRegionDownload();
        }

        @JavascriptInterface
        public String getOfflineStorageInfo() {
            return act.getOfflineStorageInfoJson();
        }

        @JavascriptInterface
        public void clearOfflineTiles() {
            act.clearOfflineTiles();
        }

        @JavascriptInterface
        public void pickGpxFile() {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            String[] mimes = {"application/gpx+xml", "application/xml", "text/xml", "*/*"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimes);
            act.startActivityForResult(intent, REQUEST_CODE_GPX);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GPX && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            final Uri uri = data.getData();
            new Thread(new GpxLoadRunnable(this, uri)).start();
        }
    }

    private static class GpxLoadRunnable implements Runnable {
        private final MainActivity act;
        private final Uri uri;
        GpxLoadRunnable(MainActivity act, Uri uri) {
            this.act = act;
            this.uri = uri;
        }

        @Override
        public void run() {
            try {
                InputStream is = act.getContentResolver().openInputStream(uri);
                String fileName = "Imported GPX Route";
                try {
                    Cursor cursor = act.getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (nameIndex >= 0) fileName = cursor.getString(nameIndex);
                        cursor.close();
                    }
                } catch (Exception ignored) {}

                RouteManager.RouteResult result = GpxManager.parseGpx(is, fileName);
                if (is != null) is.close();

                if (result != null && !result.steps.isEmpty()) {
                    act.runOnUiThread(new GpxSuccessRunnable(act, result, fileName));
                } else {
                    act.runOnUiThread(new ToastRunnable(act, "No valid track or route points found in GPX"));
                }
            } catch (Exception e) {
                act.runOnUiThread(new ToastRunnable(act, "Failed to load GPX: " + e.getMessage()));
            }
        }
    }

    private static class GpxSuccessRunnable implements Runnable {
        private final MainActivity act;
        private final RouteManager.RouteResult result;
        private final String trackName;
        GpxSuccessRunnable(MainActivity act, RouteManager.RouteResult result, String trackName) {
            this.act = act;
            this.result = result;
            this.trackName = trackName;
        }
        @Override
        public void run() {
            act.activeRoute = result;
            act.currentStepIndex = 0;
            String firstManeuver = "Continue";
            String firstStreet = trackName;
            if (!result.steps.isEmpty()) {
                firstManeuver = result.steps.get(0).maneuver;
                firstStreet = result.steps.get(0).street;
            }
            act.webView.evaluateJavascript(
                    "displayRoute(" + result.geoJsonCoordinates + ", " + result.totalDistanceMeters + ", " +
                    result.totalDurationSeconds + ", '" + escapeJs(firstManeuver) + "', '" + escapeJs(firstStreet) + "');",
                    null
            );
            Toast.makeText(act, "GPX Loaded: " + trackName, Toast.LENGTH_SHORT).show();
        }
    }

    void onSearchPlaceSuccess(double lat, double lng, String displayName, boolean isStart) {
        runOnUiThread(new SearchPlaceSuccessRunnable(this, lat, lng, displayName, isStart));
    }

    void onSearchPlaceError(String error) {
        runOnUiThread(new ToastRunnable(this, error));
    }

    void onPinAddressFound(String displayName) {
        runOnUiThread(new AddressResolvedRunnable(this, displayName));
    }

    void onRouteCalculated(RouteManager.RouteResult result) {
        activeRoute = result;
        currentStepIndex = 0;
        lastRoutePointIndex = 0;
        lastTurnDist = -1.0f;
        lastTurnDistTimeMs = 0;
        offRouteCount = 0;
        isRerouting = false;

        if (result != null && result.pathPoints != null && !result.pathPoints.isEmpty()) {
            double[] lastPt = result.pathPoints.get(result.pathPoints.size() - 1);
            currentDestLat = lastPt[0];
            currentDestLng = lastPt[1];
        }

        String firstManeuver = "Continue";
        String firstStreet = "Road";
        if (result != null && !result.steps.isEmpty()) {
            firstManeuver = result.steps.get(0).maneuver;
            firstStreet = result.steps.get(0).street;
        }

        final String m = firstManeuver;
        final String s = firstStreet;

        runOnUiThread(new RouteSuccessRunnable(this, result, m, s));
        if (isNavigating) {
            updateActiveNavigation();
        }
    }

    void onRouteError(String error) {
        isRerouting = false;
        runOnUiThread(new ToastRunnable(this, error));
    }

    private static String escapeJs(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("'", "\\'").replace("\n", " ").replace("\r", "");
    }

    // ====== STATIC RUNNABLES & LISTENERS (Ensures NO D8 anonymous class bugs) ======
    private static class PanMapRunnable implements Runnable {
        private final MainActivity act;
        private final double lat, lng;
        private final int zoom;
        PanMapRunnable(MainActivity act, double lat, double lng, int zoom) {
            this.act = act; this.lat = lat; this.lng = lng; this.zoom = zoom;
        }
        @Override public void run() {
            act.webView.evaluateJavascript("panTo(" + lat + ", " + lng + ", " + zoom + ");", null);
        }
    }

    private static class UserLocationRunnable implements Runnable {
        private final MainActivity act;
        private final double lat, lng;
        private final float bearing, accuracy, speed;
        UserLocationRunnable(MainActivity act, double lat, double lng, float bearing, float accuracy, float speed) {
            this.act = act; this.lat = lat; this.lng = lng; this.bearing = bearing; this.accuracy = accuracy; this.speed = speed;
        }
        @Override public void run() {
            act.webView.evaluateJavascript("onUserLocation(" + lat + ", " + lng + ", " + bearing + ", " + accuracy + ", " + speed + ");", null);
        }
    }

    private static class UpdateNavHudRunnable implements Runnable {
        private final MainActivity act;
        private final String m, td, st, rd, rdur, eta, nnm;
        private final float spd;
        UpdateNavHudRunnable(MainActivity act, String m, String td, String st, String rd, String rdur, String eta, String nnm, float spd) {
            this.act = act; this.m = m; this.td = td; this.st = st; this.rd = rd; this.rdur = rdur; this.eta = eta; this.nnm = nnm; this.spd = spd;
        }
        @Override public void run() {
            act.webView.evaluateJavascript(
                    "updateNavHud('" + escapeJs(m) + "', '" + escapeJs(td) + "', '" + escapeJs(st) + "', '" +
                    escapeJs(rd) + "', '" + escapeJs(rdur) + "', '" + escapeJs(eta) + "', '" + escapeJs(nnm) + "', " + Math.round(spd) + ");",
                    null
            );
        }
    }

    private static class StopNavJsRunnable implements Runnable {
        private final MainActivity act;
        StopNavJsRunnable(MainActivity act) { this.act = act; }
        @Override public void run() {
            act.webView.evaluateJavascript("stopNavFromNative();", null);
        }
    }

    private static class BtStateRunnable implements Runnable {
        private final MainActivity act;
        private final boolean connected;
        private final String name;
        BtStateRunnable(MainActivity act, boolean connected, String name) {
            this.act = act; this.connected = connected; this.name = name;
        }
        @Override public void run() {
            act.webView.evaluateJavascript("setBluetoothState(" + connected + ", '" + (name != null ? name : "") + "');", null);
            if (connected && act.btManager != null && act.currentWeatherCond != null) {
                act.btManager.sendWeatherPacket(act.currentWeatherCond, act.currentWeatherTemp);
            }
        }
    }

    private static class GattReadyRunnable implements Runnable {
        private final MainActivity act;
        GattReadyRunnable(MainActivity act) { this.act = act; }
        @Override public void run() {
            if (act.currentWeatherCond != null && act.btManager != null && act.btManager.isConnected()) {
                act.btManager.sendWeatherPacket(act.currentWeatherCond, act.currentWeatherTemp);
                Log.i("MainActivity", "GATT ready: sent cached weather: " + act.currentWeatherCond + " " + act.currentWeatherTemp);
            }
            if (act.hasGpsFix) {
                act.checkPollWeather(act.currentLat, act.currentLng);
            }
        }
    }

    private static class ToastRunnable implements Runnable {
        private final MainActivity act;
        private final String msg;
        ToastRunnable(MainActivity act, String msg) { this.act = act; this.msg = msg; }
        @Override public void run() {
            Toast.makeText(act, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private static class RequestCurrentLocationRunnable implements Runnable {
        private final MainActivity act;
        private final boolean isStart;
        RequestCurrentLocationRunnable(MainActivity act, boolean isStart) { this.act = act; this.isStart = isStart; }
        @Override public void run() {
            act.fetchInstantLocation(true);
            if (act.hasGpsFix) {
                act.webView.evaluateJavascript("setLocationPoint(" + act.currentLat + ", " + act.currentLng + ", 'Current Location', " + isStart + ");", null);
            } else {
                // If no instant GPS fix yet, set to Bogo City default area
                act.webView.evaluateJavascript("setLocationPoint(" + act.currentLat + ", " + act.currentLng + ", 'Bogo City (Default)', " + isStart + ");", null);
                Toast.makeText(act, "Using default Bogo City location. Acquiring GPS fix...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class ShowSearchDialogRunnable implements Runnable {
        private final MainActivity act;
        private final boolean isStart;
        ShowSearchDialogRunnable(MainActivity act, boolean isStart) { this.act = act; this.isStart = isStart; }
        @Override public void run() {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setTitle(isStart ? "Search Starting Position" : "Search Destination");

            final EditText input = new EditText(act);
            input.setHint("e.g. Bogo City Hall, San Remigio, Medellin...");
            builder.setView(input);

            builder.setPositiveButton("Search", new SearchPositiveListener(act, input, isStart));
            builder.setNegativeButton("Cancel", new SearchNegativeListener());
            builder.show();
        }
    }

    private static class SearchPositiveListener implements DialogInterface.OnClickListener {
        private final MainActivity act;
        private final EditText input;
        private final boolean isStart;
        SearchPositiveListener(MainActivity act, EditText input, boolean isStart) {
            this.act = act; this.input = input; this.isStart = isStart;
        }
        @Override public void onClick(DialogInterface dialog, int which) {
            String query = input.getText().toString().trim();
            if (query.length() > 0) {
                Toast.makeText(act, "Searching...", Toast.LENGTH_SHORT).show();
                act.routeManager.searchPlace(query, new PlaceSearchCallback(act, isStart));
            }
        }
    }

    private static class SearchNegativeListener implements DialogInterface.OnClickListener {
        @Override public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    private static class SearchPlaceSuccessRunnable implements Runnable {
        private final MainActivity act;
        private final double lat, lng;
        private final String name;
        private final boolean isStart;
        SearchPlaceSuccessRunnable(MainActivity act, double lat, double lng, String name, boolean isStart) {
            this.act = act; this.lat = lat; this.lng = lng; this.name = name; this.isStart = isStart;
        }
        @Override public void run() {
            act.webView.evaluateJavascript("setLocationPoint(" + lat + ", " + lng + ", '" + escapeJs(name) + "', " + isStart + ");", null);
        }
    }

    private static class AddressResolvedRunnable implements Runnable {
        private final MainActivity act;
        private final String name;
        AddressResolvedRunnable(MainActivity act, String name) { this.act = act; this.name = name; }
        @Override public void run() {
            act.webView.evaluateJavascript("onAddressResolved('" + escapeJs(name) + "');", null);
        }
    }

    private static class RouteSuccessRunnable implements Runnable {
        private final MainActivity act;
        private final RouteManager.RouteResult result;
        private final String m, s;
        RouteSuccessRunnable(MainActivity act, RouteManager.RouteResult result, String m, String s) {
            this.act = act; this.result = result; this.m = m; this.s = s;
        }
        @Override public void run() {
            act.webView.evaluateJavascript(
                    "displayRoute(" + result.geoJsonCoordinates + ", " + result.totalDistanceMeters + ", " +
                    result.totalDurationSeconds + ", '" + escapeJs(m) + "', '" + escapeJs(s) + "');",
                    null
            );
        }
    }

    private static class PlaceSearchCallback implements RouteManager.GeocodeCallback {
        private final MainActivity act;
        private final boolean isStart;
        PlaceSearchCallback(MainActivity act, boolean isStart) { this.act = act; this.isStart = isStart; }
        @Override public void onSuccess(double lat, double lng, String displayName) { act.onSearchPlaceSuccess(lat, lng, displayName, isStart); }
        @Override public void onError(String error) { act.onSearchPlaceError(error); }
    }

    private static class PinReverseGeocodeCallback implements RouteManager.GeocodeCallback {
        private final MainActivity act;
        PinReverseGeocodeCallback(MainActivity act) { this.act = act; }
        @Override public void onSuccess(double lat, double lng, String displayName) { act.onPinAddressFound(displayName); }
        @Override public void onError(String error) {}
    }

    private static class NavCalculationCallback implements RouteManager.RouteCallback {
        private final MainActivity act;
        NavCalculationCallback(MainActivity act) { this.act = act; }
        @Override public void onSuccess(RouteManager.RouteResult result) { act.onRouteCalculated(result); }
        @Override public void onError(String error) { act.onRouteError(error); }
    }

    private static class SimulationRunnable implements Runnable {
        private final MainActivity act;
        SimulationRunnable(MainActivity act) { this.act = act; }
        @Override public void run() { act.tickSimulation(); }
    }

    private static class StartSimRunnable implements Runnable {
        private final MainActivity act;
        private final float speed;
        StartSimRunnable(MainActivity act, float speed) { this.act = act; this.speed = speed; }
        @Override public void run() { act.startSimulation(speed); }
    }

    private static class SetSimSpeedRunnable implements Runnable {
        private final MainActivity act;
        private final float speed;
        SetSimSpeedRunnable(MainActivity act, float speed) { this.act = act; this.speed = speed; }
        @Override public void run() { act.setSimulationSpeed(speed); }
    }

    private static class StopSimRunnable implements Runnable {
        private final MainActivity act;
        StopSimRunnable(MainActivity act) { this.act = act; }
        @Override public void run() { act.stopSimulation(); }
    }

    // ====== OFFLINE ROUTING & TILE STORAGE MANAGEMENT ======
    public void updateRoutingModeInJs() {
        runOnUiThread(new UpdateRoutingModeRunnable(this, routeManager.isOfflineMode()));
    }

    public void updateOfflineStorageInfoInJs() {
        runOnUiThread(new UpdateOfflineStorageRunnable(this, getOfflineStorageInfoJson()));
    }

    public void updateKeepScreenOnInJs() {
        boolean keepOn = getSharedPreferences("DriveAssistantSettings", Context.MODE_PRIVATE)
                .getBoolean("keep_screen_on", true);
        runOnUiThread(new UpdateKeepScreenOnRunnable(this, keepOn));
    }

    private static class SetKeepScreenOnRunnable implements Runnable {
        private final MainActivity act;
        private final boolean keepOn;
        SetKeepScreenOnRunnable(MainActivity act, boolean keepOn) { this.act = act; this.keepOn = keepOn; }
        @Override public void run() { act.setKeepScreenOn(keepOn); }
    }

    private static class UpdateKeepScreenOnRunnable implements Runnable {
        private final MainActivity act;
        private final boolean keepOn;
        UpdateKeepScreenOnRunnable(MainActivity act, boolean keepOn) { this.act = act; this.keepOn = keepOn; }
        @Override public void run() {
            act.webView.evaluateJavascript("onKeepScreenOnLoaded(" + keepOn + ");", null);
        }
    }

    public void startRegionDownload(double minLat, double minLng, double maxLat, double maxLng, int minZoom, int maxZoom) {
        isDownloadCancelled = false;
        tileExecutor.execute(new RegionDownloadRunnable(this, minLat, minLng, maxLat, maxLng, minZoom, maxZoom));
    }

    public void cancelRegionDownload() {
        isDownloadCancelled = true;
    }

    public String getOfflineStorageInfoJson() {
        int count = 0;
        long bytes = 0;
        File dir = getTilesDir();
        File[] zDirs = dir.listFiles();
        if (zDirs != null) {
            for (File zd : zDirs) {
                if (zd.isDirectory()) {
                    File[] xDirs = zd.listFiles();
                    if (xDirs != null) {
                        for (File xd : xDirs) {
                            if (xd.isDirectory()) {
                                File[] tiles = xd.listFiles();
                                if (tiles != null) {
                                    for (File t : tiles) {
                                        if (t.isFile() && t.getName().endsWith(".png")) {
                                            count++;
                                            bytes += t.length();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        double mb = Math.round((bytes / (1024.0 * 1024.0)) * 10.0) / 10.0;
        return "{\"count\":" + count + ",\"sizeMb\":" + mb + "}";
    }

    public void clearOfflineTiles() {
        tileExecutor.execute(new ClearTilesRunnable(this));
    }

    public static int lon2tile(double lon, int zoom) {
        return (int) Math.floor((lon + 180.0) / 360.0 * (1 << zoom));
    }

    public static int lat2tile(double lat, int zoom) {
        double latRad = Math.toRadians(lat);
        return (int) Math.floor((1.0 - Math.log(Math.tan(latRad) + 1.0 / Math.cos(latRad)) / Math.PI) / 2.0 * (1 << zoom));
    }

    private static class UpdateRoutingModeRunnable implements Runnable {
        private final MainActivity act;
        private final boolean isOffline;
        UpdateRoutingModeRunnable(MainActivity act, boolean isOffline) { this.act = act; this.isOffline = isOffline; }
        @Override public void run() {
            act.webView.evaluateJavascript("onRoutingModeLoaded(" + isOffline + ");", null);
        }
    }

    private static class UpdateOfflineStorageRunnable implements Runnable {
        private final MainActivity act;
        private final String json;
        UpdateOfflineStorageRunnable(MainActivity act, String json) { this.act = act; this.json = json; }
        @Override public void run() {
            act.webView.evaluateJavascript("onOfflineStorageInfoLoaded(" + json + ");", null);
        }
    }

    private static class DownloadProgressRunnable implements Runnable {
        private final MainActivity act;
        private final int current, total;
        private final boolean isDone;
        DownloadProgressRunnable(MainActivity act, int current, int total, boolean isDone) {
            this.act = act; this.current = current; this.total = total; this.isDone = isDone;
        }
        @Override public void run() {
            act.webView.evaluateJavascript("onRegionDownloadProgress(" + current + ", " + total + ", " + isDone + ");", null);
        }
    }

    private static class ClearTilesRunnable implements Runnable {
        private final MainActivity act;
        ClearTilesRunnable(MainActivity act) { this.act = act; }
        @Override public void run() {
            deleteRecursive(act.getTilesDir());
            act.getTilesDir().mkdirs();
            act.runOnUiThread(new ToastRunnable(act, "Offline map storage cleared"));
            act.updateOfflineStorageInfoInJs();
        }
    }

    private static void deleteRecursive(File f) {
        if (f == null || !f.exists()) return;
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                for (File child : files) deleteRecursive(child);
            }
        }
        f.delete();
    }

    private static class RegionDownloadRunnable implements Runnable {
        private final MainActivity act;
        private final double minLat, minLng, maxLat, maxLng;
        private final int minZoom, maxZoom;
        RegionDownloadRunnable(MainActivity act, double minLat, double minLng, double maxLat, double maxLng, int minZoom, int maxZoom) {
            this.act = act;
            this.minLat = minLat;
            this.minLng = minLng;
            this.maxLat = maxLat;
            this.maxLng = maxLng;
            this.minZoom = minZoom;
            this.maxZoom = maxZoom;
        }
        @Override
        public void run() {
            List<int[]> tiles = new ArrayList<>();
            for (int z = minZoom; z <= maxZoom; z++) {
                int xMin = Math.max(0, lon2tile(minLng, z));
                int xMax = Math.min((1 << z) - 1, lon2tile(maxLng, z));
                if (xMin > xMax) { int tmp = xMin; xMin = xMax; xMax = tmp; }

                int yMin = Math.max(0, lat2tile(maxLat, z));
                int yMax = Math.min((1 << z) - 1, lat2tile(minLat, z));
                if (yMin > yMax) { int tmp = yMin; yMin = yMax; yMax = tmp; }

                for (int x = xMin; x <= xMax; x++) {
                    for (int y = yMin; y <= yMax; y++) {
                        tiles.add(new int[]{z, x, y});
                    }
                }
            }

            final int total = tiles.size();
            if (total == 0) {
                act.runOnUiThread(new ToastRunnable(act, "No tiles in selected region"));
                act.runOnUiThread(new DownloadProgressRunnable(act, 0, 0, true));
                return;
            }
            if (total > 2500) {
                act.runOnUiThread(new ToastRunnable(act, "Region too large (" + total + " tiles). Maximum is 2500."));
                act.runOnUiThread(new DownloadProgressRunnable(act, 0, total, true));
                return;
            }

            File baseDir = act.getTilesDir();
            int downloaded = 0;
            byte[] buffer = new byte[8192];

            for (int i = 0; i < total; i++) {
                if (act.isDownloadCancelled) {
                    act.runOnUiThread(new ToastRunnable(act, "Region download cancelled"));
                    act.runOnUiThread(new DownloadProgressRunnable(act, downloaded, total, true));
                    return;
                }

                int[] t = tiles.get(i);
                int z = t[0], x = t[1], y = t[2];
                File tileFile = new File(baseDir, z + "/" + x + "/" + y + ".png");

                if (tileFile.exists() && tileFile.length() > 0) {
                    downloaded++;
                } else {
                    File parent = tileFile.getParentFile();
                    if (parent != null && !parent.exists()) parent.mkdirs();

                    File tmpFile = new File(baseDir, z + "/" + x + "/" + y + ".png.tmp");
                    HttpURLConnection conn = null;
                    InputStream is = null;
                    FileOutputStream fos = null;
                    try {
                        URL url = new URL("https://tile.openstreetmap.org/" + z + "/" + x + "/" + y + ".png");
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestProperty("User-Agent", "DriveAssistant-v2/3.1");
                        conn.setConnectTimeout(4000);
                        conn.setReadTimeout(4000);
                        if (conn.getResponseCode() == 200) {
                            is = conn.getInputStream();
                            fos = new FileOutputStream(tmpFile);
                            int read;
                            while ((read = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, read);
                            }
                            fos.flush();
                            fos.close();
                            fos = null;
                            if (tmpFile.renameTo(tileFile)) {
                                downloaded++;
                            }
                        }
                    } catch (Exception ignored) {
                        if (tmpFile.exists()) tmpFile.delete();
                    } finally {
                        try { if (is != null) is.close(); } catch (Exception ignored) {}
                        try { if (fos != null) fos.close(); } catch (Exception ignored) {}
                        if (conn != null) conn.disconnect();
                    }

                    try {
                        Thread.sleep(12);
                    } catch (InterruptedException ignored) {}
                }

                if (i % 2 == 0 || i == total - 1) {
                    act.runOnUiThread(new DownloadProgressRunnable(act, downloaded, total, false));
                }
            }

            act.runOnUiThread(new DownloadProgressRunnable(act, downloaded, total, true));
            act.runOnUiThread(new ToastRunnable(act, "Downloaded " + downloaded + " / " + total + " tiles for offline use"));

            // Download road vectors for offline routing
            try {
                String overpassQ = "[out:json][timeout:25];way[\"highway\"](" + minLat + "," + minLng + "," + maxLat + "," + maxLng + ");out geom;";
                URL opUrl = new URL("https://overpass-api.de/api/interpreter?data=" + URLEncoder.encode(overpassQ, "UTF-8"));
                HttpURLConnection opConn = (HttpURLConnection) opUrl.openConnection();
                opConn.setRequestProperty("User-Agent", "DriveAssistant-v2/3.1");
                opConn.setConnectTimeout(6000);
                opConn.setReadTimeout(12000);
                if (opConn.getResponseCode() == 200) {
                    File roadDir = new File(act.getFilesDir(), "offline_roads");
                    if (!roadDir.exists()) roadDir.mkdirs();
                    File roadFile = new File(roadDir, "roads_" + Math.round(minLat * 100) + "_" + Math.round(minLng * 100) + ".json");
                    InputStream opIs = opConn.getInputStream();
                    FileOutputStream opFos = new FileOutputStream(roadFile);
                    int rRead;
                    while ((rRead = opIs.read(buffer)) != -1) {
                        opFos.write(buffer, 0, rRead);
                    }
                    opFos.flush();
                    opFos.close();
                    opIs.close();
                    Log.i("MainActivity", "Saved offline road network: " + roadFile.getName());
                }
                opConn.disconnect();
            } catch (Exception e) {
                Log.w("MainActivity", "Overpass road network fetch error: " + e.getMessage());
            }

            act.updateOfflineStorageInfoInJs();
        }
    }

    // ====== PERMISSIONS ======
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms;
            if (Build.VERSION.SDK_INT >= 33) { // Android 13+
                perms = new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.POST_NOTIFICATIONS
                };
            } else {
                perms = new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                };
            }
            requestPermissions(perms, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            boolean granted = false;
            for (int r : grantResults) {
                if (r == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                    break;
                }
            }
            if (granted) {
                initLocation();
                fetchInstantLocation(false);
                btManager.startAutoConnect();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSimulation();
        cancelNavNotification();
        if (btManager != null) btManager.disconnect();
        if (locationManager != null) locationManager.removeUpdates(locationListener);
    }
}
