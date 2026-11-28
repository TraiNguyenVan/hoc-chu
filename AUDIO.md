# Offline audio

The supplied v2 two-voice archive provides 185 clips per voice: 29 letters, 14 combined sounds and 142 rhymes. Voice choices are Miền Nam – Diễm My (default) and Miền Bắc – Quỳnh Anh. The archive's generation script identifies the supplementary `r` clips as Phương Ly (South) and Thanh Phương (North). Selection persists in activity preferences; existing `nam` and `bac` values are unchanged. No network or TTS fallback is used.

Files are bundled as `audio/<nam|bac>/<category>/<hex>.mp3`, where category is 0, 1 or 2 and hex is the exact UTF-8 item label. Files are stored uncompressed for `AssetManager.openFd`. Each voice is sourced from its archive manifest. Presence and decoding do not establish pronunciation, pedagogical accuracy, rights or human approval. These require separate review; test playback on the target device offline.

Run `sh tests/run_audio_tests.sh` for host logic and `python3 tests/test_audio_packaging.py` for the manual APK packaging fixture.
