#!/bin/sh
# Compile/package real Android instrumentation tests; never contacts a device.
set -eu
cd "$(dirname "$0")/.."
SDK=${ANDROID_HOME:?Set ANDROID_HOME}
OUT=${BUILD_OUT:-build/accessibility-check}
BT="$SDK/build-tools/35.0.0"
PLATFORM="$SDK/platforms/android-35"
CP="$PLATFORM/android.jar:$PLATFORM/optional/android.test.runner.jar:$PLATFORM/optional/android.test.base.jar:$OUT/classes"
TEST_OUT="$OUT/instrumentation"
mkdir -p "$TEST_OUT/classes" "$TEST_OUT/dex"
javac -encoding UTF-8 --release 8 -classpath "$CP" -d "$TEST_OUT/classes" tests/androidTest/vn/khoaikhoai/hocchu/AccessibilityUiTest.java
"$BT/d8" --min-api 23 --lib "$PLATFORM/android.jar" --lib "$PLATFORM/optional/android.test.runner.jar" --lib "$PLATFORM/optional/android.test.base.jar" --output "$TEST_OUT/dex" "$TEST_OUT/classes/vn/khoaikhoai/hocchu/AccessibilityUiTest.class"
"$BT/aapt" package -f -M tests/androidTest/AndroidManifest.xml -I "$PLATFORM/android.jar" -F "$TEST_OUT/base.apk"
cp "$TEST_OUT/base.apk" "$TEST_OUT/unaligned.apk"
(cd "$TEST_OUT/dex" && "$BT/aapt" add ../unaligned.apk classes.dex)
"$BT/zipalign" -f 4 "$TEST_OUT/unaligned.apk" "$TEST_OUT/aligned.apk"
"$BT/apksigner" sign --ks "$OUT/debug.keystore" --ks-pass pass:android --key-pass pass:android --out "$TEST_OUT/accessibility-test.apk" "$TEST_OUT/aligned.apk"
"$BT/apksigner" verify "$TEST_OUT/accessibility-test.apk"
echo "Compiled and packaged: $TEST_OUT/accessibility-test.apk (not executed)"
