import os, subprocess, shutil, glob, zipfile

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.abspath(os.path.join(BASE_DIR, "..", ".."))
BUILD_DIR = os.path.join(BASE_DIR, "build")

AAPT2 = r"C:\Users\cycoy\AppData\Local\Android\Sdk\build-tools\34.0.0\aapt2.exe"
D8 = r"C:\Users\cycoy\AppData\Local\Android\Sdk\build-tools\34.0.0\d8.bat"
ZIPALIGN = r"C:\Users\cycoy\AppData\Local\Android\Sdk\build-tools\34.0.0\zipalign.exe"
ANDROID_JAR = r"C:\Users\cycoy\AppData\Local\Android\Sdk\platforms\android-34\android.jar"
SIGNER = r"C:\Users\cycoy\.gemini\antigravity\brain\4df866c0-15c1-426a-aad8-ff24ada139ad\scratch\uber-apk-signer.jar"

if os.path.exists(BUILD_DIR):
    shutil.rmtree(BUILD_DIR)

os.makedirs(os.path.join(BUILD_DIR, "gen"), exist_ok=True)
os.makedirs(os.path.join(BUILD_DIR, "classes"), exist_ok=True)
os.makedirs(os.path.join(BUILD_DIR, "dex"), exist_ok=True)
os.makedirs(os.path.join(BUILD_DIR, "signed"), exist_ok=True)

# 1. Compile resources
print("1. Compiling Android resources...")
res_compiled = os.path.join(BUILD_DIR, "res_compiled.zip")
subprocess.check_call([AAPT2, "compile", "--dir", os.path.join(BASE_DIR, "res"), "-o", res_compiled])

# 2. Link resources & assets
print("2. Linking resources and packaging assets...")
unaligned_apk = os.path.join(BUILD_DIR, "unaligned.apk")
manifest_xml = os.path.join(BASE_DIR, "AndroidManifest.xml")
assets_dir = os.path.join(BASE_DIR, "assets")
subprocess.check_call([
    AAPT2, "link",
    "-I", ANDROID_JAR,
    "--manifest", manifest_xml,
    "-A", assets_dir,
    "--java", os.path.join(BUILD_DIR, "gen"),
    "-o", unaligned_apk,
    res_compiled,
    "--auto-add-overlay"
])

# 3. Compile Java sources
print("3. Compiling Java sources...")
java_files = glob.glob(os.path.join(BASE_DIR, "src", "com", "driveassistant", "v2", "*.java")) + \
             glob.glob(os.path.join(BUILD_DIR, "gen", "**", "*.java"), recursive=True)

subprocess.check_call([
    "javac", "--release", "8",
    "-cp", ANDROID_JAR,
    "-d", os.path.join(BUILD_DIR, "classes")
] + java_files)

# 4. Dex classes with D8
print("4. Dexing classes into classes.dex...")
classes_jar = os.path.join(BUILD_DIR, "classes.jar")
subprocess.check_call(["jar", "cf", classes_jar, "-C", os.path.join(BUILD_DIR, "classes"), "."])
subprocess.check_call([
    D8, "--min-api", "26",
    "--output", os.path.join(BUILD_DIR, "dex"),
    classes_jar
])

# 5. Pack classes.dex into APK
print("5. Packaging classes.dex into APK...")
packaged_apk = os.path.join(BUILD_DIR, "packaged.apk")
shutil.copyfile(unaligned_apk, packaged_apk)
with zipfile.ZipFile(packaged_apk, "a") as z:
    z.write(os.path.join(BUILD_DIR, "dex", "classes.dex"), "classes.dex")

# 6. Zipalign
print("6. Aligning APK...")
aligned_apk = os.path.join(BUILD_DIR, "DriveAssistant-v2-aligned.apk")
subprocess.check_call([ZIPALIGN, "-v", "-p", "4", packaged_apk, aligned_apk])

# 7. Sign APK
print("7. Signing APK...")
subprocess.check_call([
    "java", "-jar", SIGNER,
    "--apks", aligned_apk,
    "--out", os.path.join(BUILD_DIR, "signed"),
    "--skipZipAlign"
])

# 8. Deploy final APK
signed_apks = glob.glob(os.path.join(BUILD_DIR, "signed", "*debugSigned.apk"))
if signed_apks:
    dest1 = os.path.join(PROJECT_ROOT, "android", "DriveAssistant-v2.apk")
    shutil.copyfile(signed_apks[0], dest1)
    brain_dir = r"C:\Users\cycoy\.gemini\antigravity\brain\9a306cc2-39a2-4862-8374-d4b0e4b92f6a"
    if os.path.isdir(brain_dir):
        dest2 = os.path.join(brain_dir, "DriveAssistant-v2.apk")
        shutil.copyfile(signed_apks[0], dest2)
    print(f"BUILD SUCCESS: {dest1} ({os.path.getsize(dest1)} bytes)")

    # 9. Auto-install to connected device if available
    try:
        dev_out = subprocess.check_output(["adb", "devices"], timeout=5).decode()
        devices = [line.split()[0] for line in dev_out.strip().splitlines()[1:] if "\tdevice" in line]
        if devices:
            target = devices[0]
            print(f"Auto-installing to {target}...")
            subprocess.check_call(["adb", "-s", target, "install", "-r", dest1], timeout=15)
            subprocess.call(["adb", "-s", target, "shell", "am", "start", "-n", "com.driveassistant.v2/.MainActivity"], timeout=5)
            print("Auto-install and launch completed.")
    except Exception as e:
        print(f"Auto-install skipped: {e}")


