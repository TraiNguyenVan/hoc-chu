package vn.khoaikhoai.hocchu;

/** Presents the availability and playback errors for one item. */
public final class AudioPresenter {
    public interface View { void show(boolean enabled, String text); }
    private final ReviewedAudio audio;
    private final int category;
    private final String label;
    private final View view;
    private boolean enabled;
    public AudioPresenter(ReviewedAudio audio, int category, String label, View view) {
        this.audio = audio; this.category = category; this.label = label; this.view = view;
        enabled = audio.available(category, label);
        view.show(enabled, enabled ? "▶  Nghe lại" : "Âm đọc chưa có");
    }
    public void replay() {
        if (!enabled) return;
        if (!audio.play(category, label, this::error)) error();
    }
    private void error() { enabled = false; view.show(false, "Âm đọc không phát được"); }
    public void close() { enabled = false; audio.stop(); }
}
