package vn.khoaikhoai.hocchu;

/** Host-side geometry checks for chart touch targets at different widths. */
public final class BoardLayoutTest {
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    private static int columns(int width) throws Exception {
        try {
            Class<?> layout = Class.forName("vn.khoaikhoai.hocchu.MainActivity$BoardLayout");
            return (Integer) layout.getDeclaredMethod("columns", int.class).invoke(null, width);
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Responsive board layout is not implemented", e);
        }
    }
    public static void main(String[] args) throws Exception {
        for (int width : new int[]{160, 240, 320, 600, 800, 1200}) {
            int columns = columns(width);
            check(columns >= 1 && columns <= 16, "column range for " + width);
            check(width / columns >= 56, "minimum 56dp touch width for " + width);
        }
        check(columns(1200) == 16, "wide chart caps at 16 columns");
        check(columns(320) < columns(800), "responsive columns");
        System.out.println("PASS: chart adapts while preserving touch target width");
    }
}
