package vn.khoaikhoai.hocchu;
public class AudioPresenterTest {
    private static class Backend implements ReviewedAudio.Backend {
        boolean exists, fail; Runnable onError; int starts, stops;
        public boolean exists(String path) { return exists; }
        public boolean start(String path, Runnable error) { starts++; onError = error; return !fail; }
        public void stop() { stops++; }
    }
    private static class Screen implements AudioPresenter.View {
        boolean enabled; String text;
        public void show(boolean enabled, String text) { this.enabled=enabled; this.text=text; }
    }
    private static void check(boolean b, String s) { if (!b) throw new AssertionError(s); }
    public static void main(String[] args) {
        Backend b = new Backend(); Screen s = new Screen(); ReviewedAudio audio = new ReviewedAudio(b);
        AudioPresenter p = new AudioPresenter(audio, 2, "ăn", s);
        check(!s.enabled && s.text.contains("chưa có"), "Missing audio clearly unavailable");
        p.replay(); check(b.starts == 0, "Missing audio is not played");
        b.exists = true; p = new AudioPresenter(audio, 2, "ăn", s);
        check(s.enabled && s.text.contains("Nghe"), "Available clip has replay action");
        p.replay(); p.replay(); check(b.starts == 2, "Replay triggers each request");
        b.onError.run(); check(!s.enabled && s.text.contains("không phát"), "Asynchronous playback error shown");
        p.replay(); check(b.starts == 2, "Error disables playback");
        b.fail = true; p = new AudioPresenter(audio, 2, "ăn", s); p.replay();
        check(!s.enabled && s.text.contains("không phát"), "Synchronous playback error shown");
        int before = b.stops; p.close(); check(b.stops == before+1, "Close stops playback");
        System.out.println("PASS: unavailable, replay and error presentation");
    }
}
