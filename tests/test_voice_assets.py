from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]

class VoiceAssetsTest(unittest.TestCase):
    def test_voice_sets_match_and_have_expected_counts(self):
        root = ROOT / "app/src/main/assets/audio"
        for cat, count in enumerate((29, 14, 142)):
            south = {p.name for p in (root / "nam" / str(cat)).glob("*.mp3")}
            north = {p.name for p in (root / "bac" / str(cat)).glob("*.mp3")}
            self.assertEqual(len(south), count)
            self.assertEqual(south, north)

    def test_persistence_and_settings_are_wired(self):
        source = (ROOT / "app/src/main/java/vn/khoaikhoai/hocchu/MainActivity.java").read_text()
        self.assertIn('getPreferences(MODE_PRIVATE).getString("voice","nam")', source)
        self.assertIn('putString("voice",voice).apply()', source)
        self.assertIn('reviewedAudio.setVoice(voice)', source)
        self.assertIn('Miền Nam – Diễm My', source)
        self.assertIn('Miền Bắc – Quỳnh Anh', source)
        self.assertIn('which==1?"bac":"nam"', source)

if __name__ == "__main__":
    unittest.main()
