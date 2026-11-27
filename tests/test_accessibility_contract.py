"""Source-level guardrails only; actual Android accessibility needs device tests."""
from pathlib import Path
import re
import unittest

SOURCE = Path(__file__).resolve().parents[1] / 'app/src/main/java/vn/khoaikhoai/hocchu/MainActivity.java'


def method_body(source, signature):
    start = source.index(signature)
    opening = source.index('{', start)
    depth = 1
    for i in range(opening + 1, len(source)):
        if source[i] == '{':
            depth += 1
        elif source[i] == '}':
            depth -= 1
            if depth == 0:
                return source[opening + 1:i]
    raise AssertionError('unclosed method ' + signature)


class AccessibilitySourceContract(unittest.TestCase):
    def test_active_tab_sets_native_selected_state(self):
        source = SOURCE.read_text(encoding='utf-8')
        selected = method_body(source, 'private void selected(int n)')
        self.assertRegex(selected, r'navItems\[i\]\.setSelected\(i\s*==\s*n\)')
        for method, tab in [('private void showChart(int ignored)', '0'),
                            ('private void showQuizMenu()', '1'),
                            ('private void renderQuestion()', '1')]:
            self.assertIn('selected(' + tab + ')', method_body(source, method))

    def test_options_refresh_on_render_and_immediately_after_submission(self):
        source = SOURCE.read_text(encoding='utf-8')
        render = method_body(source, 'private void renderQuestion()')
        self.assertGreaterEqual(render.count('updateQuizOptions(grid)'), 2)
        self.assertLess(render.index('quiz.submit(option)'), render.index('updateQuizOptions(grid)', render.index('quiz.submit(option)')))
        update = method_body(source, 'private void updateQuizOptions(GridLayout grid)')
        self.assertRegex(update, r'\.setEnabled\(!quiz\.submitted\(\)\)')
        self.assertIn('quiz.selected()', update)
        self.assertIn('quiz.answer()', update)
        self.assertIn('setBackground', update)
        self.assertIn('setTextColor', update)
        self.assertIn('feedback.setText(', render)


if __name__ == '__main__':
    unittest.main()
