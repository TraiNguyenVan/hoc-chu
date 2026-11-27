package vn.khoaikhoai.hocchu;

import java.nio.charset.StandardCharsets;

/** Exact, category-scoped mapping to packaged reviewed clips; never synthesizes speech. */
public final class ReviewedAudio {
    public interface Backend {
        boolean exists(String path);
        boolean start(String path, Runnable onError);
        void stop();
    }
    private final Backend backend;
    public ReviewedAudio(Backend backend) { this.backend = backend; }
    public static String path(int category, String label) {
        if (category < 0 || category > 2 || label == null || label.isEmpty()) throw new IllegalArgumentException("Invalid item");
        StringBuilder name = new StringBuilder();
        for (byte b : label.getBytes(StandardCharsets.UTF_8)) {
            int value = b & 255;
            name.append(Character.forDigit(value >>> 4, 16));
            name.append(Character.forDigit(value & 15, 16));
        }
        return "audio/" + category + "/" + name + ".mp3";
    }
    public boolean available(int category, String label) { return backend.exists(path(category, label)); }
    public boolean play(int category, String label, Runnable onError) {
        backend.stop();
        String path = path(category, label);
        return backend.exists(path) && backend.start(path, onError);
    }
    public void stop() { backend.stop(); }
}
