#!/bin/sh
set -eu
SDK=${ANDROID_HOME:?Set ANDROID_HOME}
BT="$SDK/build-tools/35.0.0"
JAR="$SDK/platforms/android-35/android.jar"
OUT=${BUILD_OUT:-build/manual}
rm -rf "$OUT/classes" "$OUT/dex"
mkdir -p "$OUT/classes" "$OUT/dex"
rm -f "$OUT"/*.apk "$OUT"/*.zip
if [ -d app/src/main/assets ]; then
  "$BT/aapt" package -f -M app/src/main/AndroidManifest.xml -I "$JAR" -A app/src/main/assets -0 mp3 -F "$OUT/base.apk"
else
  "$BT/aapt" package -f -M app/src/main/AndroidManifest.xml -I "$JAR" -F "$OUT/base.apk"
fi
javac -encoding UTF-8 --release 8 -classpath "$JAR" -d "$OUT/classes" app/src/main/java/vn/khoaikhoai/hocchu/*.java
"$BT/d8" --min-api 23 --lib "$JAR" --output "$OUT/dex" "$OUT/classes"/vn/khoaikhoai/hocchu/*.class
cp "$OUT/base.apk" "$OUT/unaligned.apk"
(cd "$OUT/dex" && "$BT/aapt" add "../unaligned.apk" classes.dex)
"$BT/zipalign" -f 4 "$OUT/unaligned.apk" "$OUT/aligned.apk"
if [ ! -f "$OUT/debug.keystore" ]; then keytool -genkeypair -keystore "$OUT/debug.keystore" -storepass android -keypass android -alias androiddebugkey -dname 'CN=Android Debug,O=Prototype,C=VN' -keyalg RSA -keysize 2048 -validity 10000 >/dev/null 2>&1; fi
"$BT/apksigner" sign --ks "$OUT/debug.keystore" --ks-pass pass:android --key-pass pass:android --out "$OUT/app-debug.apk" "$OUT/aligned.apk"
"$BT/apksigner" verify "$OUT/app-debug.apk"
echo "APK ready: $OUT/app-debug.apk"
