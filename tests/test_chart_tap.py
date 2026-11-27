from pathlib import Path
import unittest


class ChartTapTest(unittest.TestCase):
    def test_chart_tap_plays_audio_without_opening_dialog(self):
        source = (Path(__file__).resolve().parents[1] / 'app/src/main/java/vn/khoaikhoai/hocchu/MainActivity.java').read_text(encoding='utf-8')
        self.assertIn('cell.setOnClickListener(v->playCell(cell,itemCategory,item.label));', source)
        self.assertNotIn('showItem(item)', source)


if __name__ == '__main__':
    unittest.main()
