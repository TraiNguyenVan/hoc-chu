# Reviewed offline audio

The private evaluation build contains 185 provisional Viettel clips: 29 alphabet items, 14 red-row items and 142 vần. All use Quỳnh Anh at speed 0.8. The user approved the `an` sample; the other clips have not been individually approved. Pronunciation and educational use need qualified review, especially stopped vần, diphthongs and rare vần. Viettel's terms for bundling the output remain unconfirmed. The app does not call Viettel, Google, a device TTS, or any network service; never put credentials in it.

The original cut-based experiments in `voice-samples/candidate-viettel-individual/README.md` are no longer bundled. Input text, hashes and review status are in `voice-samples/viettel-category-0-1-manifest.tsv`, `voice-samples/viettel-rime-candidates/manifest.tsv`, and `voice-samples/viettel-unresolved-candidates/manifest.tsv`; five initial vần are documented below. Red-row `ia`, `ua`, `ưa` use whole-sound prompts, rather than the separate candidate readings in the inventory. Sound quality and pronunciation on device still need human assessment; these are not educator-reviewed recordings.

## Add or replace recordings

After obtaining the required rights and qualified review, place one reviewed MP3 for the **exact** displayed item in `app/src/main/assets/audio/<category>/<hex>.mp3`, then rebuild and install the new APK. Category is `0` (chữ cái), `1` (chữ ghép), or `2` (vần). `<hex>` is lowercase hexadecimal of the item's exact UTF-8 bytes (not its pronunciation, not a transliteration, and not its array position). For example:

- chữ cái `a` → `audio/0/61.mp3`
- vần `ăn` → `audio/2/c4836e.mp3`

To compute a name on a system with Python: `python3 -c 'print("ăn".encode("utf-8").hex())'`. Preserve the exact label and category; verify each clip speaks the intended item, including whether it teaches letter name or phonics sound. There is no manifest that makes a clip reviewed: whoever supplies it is responsible for rights and human review, including dialect. Record provenance, license, reviewer, date, and pronunciation decisions separately, without credentials. Partial sets are supported: missing clips show “Âm đọc chưa có”; decode/playback errors show “Âm đọc không phát được”. There is no substitute clip or speech fallback.

The build script includes assets with MP3 files stored uncompressed for Android `AssetManager.openFd`. Clips are packaged in the APK, not downloaded at runtime; replacement requires a rebuild and app update. Do not use user-supplied MP3 files as authoritative merely because they decode. Test every supplied clip on the intended tablet offline, including replay, screen exit/rotation and interruptions. This integration has not yet been tested with reviewed sound or on the target device.

## Tests

Run `sh tests/run_audio_tests.sh` with a JDK on PATH. These host-side tests use a filesystem-backed asset double and a MediaPlayer double; they do not prove decoding, audibility, device lifecycle, or UI layout. `python3 tests/test_audio_packaging.py` exercises the actual manual build and inspects a signed test APK with a temporary non-audio fixture; its Docker mount and SDK path are specific to this environment. The fixture is never included in the product APK.
