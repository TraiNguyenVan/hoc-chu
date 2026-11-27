#!/bin/sh
set -eu
cd "$(dirname "$0")/.."
OUT=$(mktemp -d "${TMPDIR:-/opt/data/cache/scratch}/hocchu-audio-tests.XXXXXXXX")
trap 'rm -rf "$OUT"' EXIT
javac -encoding UTF-8 -d "$OUT" \
  tests/android-stubs/android/content/res/*.java tests/android-stubs/android/media/*.java \
  app/src/main/java/vn/khoaikhoai/hocchu/ReviewedAudio.java \
  app/src/main/java/vn/khoaikhoai/hocchu/AndroidAudioBackend.java \
  app/src/main/java/vn/khoaikhoai/hocchu/AudioPresenter.java \
  tests/AudioTest.java tests/AndroidAudioBackendTest.java tests/AudioPresenterTest.java
for test in AudioTest AndroidAudioBackendTest AudioPresenterTest; do
  java -cp "$OUT" "vn.khoaikhoai.hocchu.$test"
done
