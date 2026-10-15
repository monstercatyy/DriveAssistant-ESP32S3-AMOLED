package com.driveassistant.v2;

import android.location.Location;
import android.util.Log;
import android.util.Xml;

import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GpxManager {
    private static final String TAG = "GpxManager";

    public static class GpxPoint {
        public double lat;
        public double lon;
        public double ele;
        public String name;

        public GpxPoint(double lat, double lon, double ele, String name) {
            this.lat = lat;
            this.lon = lon;
            this.ele = ele;
            this.name = name;
        }
    }

    public static RouteManager.RouteResult parseGpx(InputStream is, String defaultName) {
        List<GpxPoint> points = new ArrayList<>();
        String trackName = defaultName;

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(is, "UTF-8");

            int eventType = parser.getEventType();
            String currentTag = "";
            GpxPoint currentPoint = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    currentTag = parser.getName();
                    if ("trkpt".equalsIgnoreCase(currentTag) || "rtept".equalsIgnoreCase(currentTag) || "wpt".equalsIgnoreCase(currentTag)) {
                        String latStr = parser.getAttributeValue(null, "lat");
                        String lonStr = parser.getAttributeValue(null, "lon");
                        if (latStr != null && lonStr != null) {
                            try {
                                double lat = Double.parseDouble(latStr);
                                double lon = Double.parseDouble(lonStr);
                                currentPoint = new GpxPoint(lat, lon, 0, "");
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                } else if (eventType == XmlPullParser.TEXT) {
                    String text = parser.getText();
                    if (text != null) {
                        text = text.trim();
                        if (currentPoint != null) {
                            if ("ele".equalsIgnoreCase(currentTag) && !text.isEmpty()) {
                                try { currentPoint.ele = Double.parseDouble(text); } catch (Exception ignored) {}
                            } else if ("name".equalsIgnoreCase(currentTag) && !text.isEmpty()) {
                                currentPoint.name = text;
                            }
                        } else if ("name".equalsIgnoreCase(currentTag) && !text.isEmpty() && (trackName == null || trackName.equals(defaultName))) {
                            trackName = text;
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    String endTag = parser.getName();
                    if ("trkpt".equalsIgnoreCase(endTag) || "rtept".equalsIgnoreCase(endTag) || "wpt".equalsIgnoreCase(endTag)) {
                        if (currentPoint != null) {
                            points.add(currentPoint);
                            currentPoint = null;
                        }
                    }
                    currentTag = "";
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing GPX stream", e);
        }

        if (points.isEmpty()) {
            return null;
        }

        RouteManager.RouteResult result = new RouteManager.RouteResult();

        // 1. Build GeoJSON coordinates array: [[lon, lat], [lon, lat], ...]
        JSONArray coordsArray = new JSONArray();
        double totalDistance = 0.0;
        float[] distResult = new float[1];

        for (int i = 0; i < points.size(); i++) {
            GpxPoint pt = points.get(i);
            result.pathPoints.add(new double[]{pt.lat, pt.lon});
            JSONArray ptArr = new JSONArray();
            try {
                ptArr.put(pt.lon);
                ptArr.put(pt.lat);
                coordsArray.put(ptArr);
            } catch (Exception ignored) {}

            if (i > 0) {
                GpxPoint prev = points.get(i - 1);
                Location.distanceBetween(prev.lat, prev.lon, pt.lat, pt.lon, distResult);
                totalDistance += distResult[0];
            }
        }

        result.geoJsonCoordinates = coordsArray.toString();
        result.totalDistanceMeters = totalDistance;
        // Estimated duration based on average 35 km/h driving/riding pace
        result.totalDurationSeconds = (totalDistance / (35.0 * 1000.0 / 3600.0));

        // 2. Synthesize turn-by-turn steps from track geometry
        result.steps = synthesizeSteps(points, trackName);

        return result;
    }

    private static List<RouteManager.Step> synthesizeSteps(List<GpxPoint> points, String trackName) {
        List<RouteManager.Step> steps = new ArrayList<>();
        if (points.isEmpty()) return steps;

        // Step 0: Start
        RouteManager.Step startStep = new RouteManager.Step();
        startStep.maneuver = "straight";
        startStep.street = (trackName != null && !trackName.isEmpty()) ? trackName : "Start Route";
        startStep.startLat = points.get(0).lat;
        startStep.startLng = points.get(0).lon;
        steps.add(startStep);

        if (points.size() < 3) {
            // Very short track: just add arrival
            GpxPoint last = points.get(points.size() - 1);
            RouteManager.Step endStep = new RouteManager.Step();
            endStep.maneuver = "arrive";
            endStep.street = "Destination";
            endStep.startLat = last.lat;
            endStep.startLng = last.lon;
            startStep.distanceMeters = 50;
            steps.add(endStep);
            return steps;
        }

        // Sampling stride to detect significant angular changes rather than micro-jitter
        int sampleStride = Math.max(1, points.size() / 40);
        double cumulativeDist = 0;
        float[] distRes = new float[1];

        for (int i = sampleStride; i < points.size() - sampleStride; i += sampleStride) {
            GpxPoint pPrev = points.get(i - sampleStride);
            GpxPoint pCurr = points.get(i);
            GpxPoint pNext = points.get(i + sampleStride);

            Location.distanceBetween(pPrev.lat, pPrev.lon, pCurr.lat, pCurr.lon, distRes);
            cumulativeDist += distRes[0];

            double b1 = calculateBearing(pPrev.lat, pPrev.lon, pCurr.lat, pCurr.lon);
            double b2 = calculateBearing(pCurr.lat, pCurr.lon, pNext.lat, pNext.lon);
            double delta = (b2 - b1 + 540) % 360 - 180;

            String maneuver = null;
            if (delta > 35 && delta <= 115) {
                maneuver = "turn-right";
            } else if (delta > 115) {
                maneuver = "sharp-right";
            } else if (delta > 15 && delta <= 35) {
                maneuver = "slight-right";
            } else if (delta < -35 && delta >= -115) {
                maneuver = "turn-left";
            } else if (delta < -115) {
                maneuver = "sharp-left";
            } else if (delta < -15 && delta >= -35) {
                maneuver = "slight-left";
            }

            if (maneuver != null && cumulativeDist > 80) {
                RouteManager.Step prevStep = steps.get(steps.size() - 1);
                prevStep.distanceMeters = cumulativeDist;

                RouteManager.Step s = new RouteManager.Step();
                s.maneuver = maneuver;
                s.street = (pCurr.name != null && !pCurr.name.isEmpty()) ? pCurr.name : "GPX Waypoint " + steps.size();
                s.startLat = pCurr.lat;
                s.startLng = pCurr.lon;
                steps.add(s);
                cumulativeDist = 0;
            }
        }

        // Final Step: Destination
        GpxPoint lastPt = points.get(points.size() - 1);
        if (!steps.isEmpty()) {
            steps.get(steps.size() - 1).distanceMeters = Math.max(50, cumulativeDist);
        }

        RouteManager.Step finishStep = new RouteManager.Step();
        finishStep.maneuver = "arrive";
        finishStep.street = "Destination";
        finishStep.startLat = lastPt.lat;
        finishStep.startLng = lastPt.lon;
        finishStep.distanceMeters = 0;
        steps.add(finishStep);

        return steps;
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
}

