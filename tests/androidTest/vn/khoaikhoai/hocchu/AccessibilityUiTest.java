package vn.khoaikhoai.hocchu;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.GridLayout;
import android.widget.TextView;
import java.lang.reflect.Field;

/** Instrumentation tests for real Android View/accessibility behavior. Not host mocks. */
@SuppressWarnings("deprecation")
public class AccessibilityUiTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public AccessibilityUiTest() { super(MainActivity.class); }

    private View find(View root, String text) {
        if (root instanceof TextView && text.contentEquals(((TextView) root).getText())) return root;
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                View result = find(group.getChildAt(i), text);
                if (result != null) return result;
            }
        }
        return null;
    }
    private View text(String value) {
        View view = find(getActivity().getWindow().getDecorView(), value);
        assertNotNull("Missing text " + value, view);
        return view;
    }
    private void click(View view) throws Throwable {
        runTestOnUiThread(() -> assertTrue(view.performClick()));
        getInstrumentation().waitForIdleSync();
    }
    private void selected(View view, boolean expected) throws Throwable {
        runTestOnUiThread(() -> {
            AccessibilityNodeInfo info = view.createAccessibilityNodeInfo();
            try { assertEquals(expected, info.isSelected()); } finally { info.recycle(); }
        });
    }
    private void enterQuiz() throws Throwable {
        click(text("Luyện tập"));
        View category = text("Chữ cái");
        click((View) category.getParent().getParent());
    }
    private Quiz quiz() throws Exception {
        Field field = MainActivity.class.getDeclaredField("quiz");
        field.setAccessible(true);
        return (Quiz) field.get(getActivity());
    }
    private GridLayout options() throws Exception {
        Field field = MainActivity.class.getDeclaredField("content");
        field.setAccessible(true);
        ViewGroup content = (ViewGroup) field.get(getActivity());
        for (int i = 0; i < content.getChildCount(); i++)
            if (content.getChildAt(i) instanceof GridLayout) return (GridLayout) content.getChildAt(i);
        throw new AssertionError("Missing options grid");
    }
    private void enabled(GridLayout grid, boolean expected) throws Throwable {
        runTestOnUiThread(() -> {
            assertEquals(4, grid.getChildCount());
            for (int i = 0; i < grid.getChildCount(); i++) {
                AccessibilityNodeInfo info = grid.getChildAt(i).createAccessibilityNodeInfo();
                try { assertEquals(expected, info.isEnabled()); } finally { info.recycle(); }
            }
        });
    }
    public void testTabsExposeSelectedStateAcrossScreens() throws Throwable {
        selected(text("Bảng học"), true);
        selected(text("Luyện tập"), false);
        click(text("Luyện tập"));
        selected(text("Bảng học"), false);
        selected(text("Luyện tập"), true);
        click((View) text("Chữ cái").getParent().getParent());
        selected(text("Luyện tập"), true);
        click(text("Bảng học"));
        selected(text("Bảng học"), true);
        selected(text("Luyện tập"), false);
    }
    private void answer(boolean correct) throws Throwable {
        enterQuiz();
        Quiz q = quiz();
        GridLayout grid = options();
        enabled(grid, true);
        int index = q.options().indexOf(q.answer());
        if (!correct) index = (index + 1) % 4;
        View choice = grid.getChildAt(index);
        click(choice);
        enabled(grid, false);
        assertNotNull(text(correct ? "Đúng rồi, giỏi quá!" : "Chưa đúng. Đáp án là: " + q.answer().label));
        int score = q.score();
        LearningData.Item selected = q.selected();
        runTestOnUiThread(() -> grid.getChildAt(0).performClick());
        assertEquals(score, q.score());
        assertSame(selected, q.selected());
        click(text("Tiếp theo  →"));
        enabled(options(), true);
    }
    public void testCorrectAnswerLocksOptionsKeepsFeedbackAndNextUnlocks() throws Throwable { answer(true); }
    public void testWrongAnswerLocksOptionsKeepsFeedbackAndNextUnlocks() throws Throwable { answer(false); }
}
