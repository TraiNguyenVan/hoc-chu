package vn.khoaikhoai.hocchu;

import android.content.res.AssetManager;
import android.media.MediaPlayer;
import java.nio.file.Files;
import java.nio.file.Path;

public class AndroidAudioBackendTest {
    private static void check(boolean b, String s) { if (!b) throw new AssertionError(s); }
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("audio-backend-");
        try {
            AssetManager assets = new AssetManager(dir);
            AndroidAudioBackend backend = new AndroidAudioBackend(assets);
            ReviewedAudio audio = new ReviewedAudio(backend);
            String path = ReviewedAudio.path(2, "ăn");
            check(!audio.available(2, "ăn"), "Missing file unavailable");
            Path file = dir.resolve(path); Files.createDirectories(file.getParent()); Files.write(file, new byte[]{1,2,3});
            check(audio.available(2, "ăn"), "Real file becomes available");
            final int[] errors = {0};
            check(audio.play(2, "ăn", () -> errors[0]++), "Existing file opens");
            MediaPlayer first = MediaPlayer.last; first.prepared();
            check(first.started, "Prepared player starts");
            check(audio.play(2, "ăn", () -> errors[0]++), "Replay opens file again");
            check(first.released, "Replay releases prior player");
            MediaPlayer second = MediaPlayer.last; second.error();
            check(second.released && errors[0] == 1, "Async error releases and reports failure");
            check(audio.play(2, "ăn", () -> errors[0]++), "Retry opens file");
            MediaPlayer third = MediaPlayer.last; third.prepared(); third.complete();
            check(third.released, "Completion releases player");
            audio.play(2, "ăn", () -> errors[0]++); MediaPlayer fourth = MediaPlayer.last;
            audio.stop(); check(fourth.released, "Stop releases player");
            fourth.error(); check(errors[0] == 1, "Stale error ignored");
            MediaPlayer.failConstructor = true;
            check(!audio.play(2, "ăn", () -> errors[0]++), "Player initialization failure must not crash");
            MediaPlayer.failConstructor = false;
            System.out.println("PASS: Android backend with filesystem-backed asset and player test doubles");
        } finally { try (java.util.stream.Stream<Path> paths = Files.walk(dir)) { paths.sorted(java.util.Comparator.reverseOrder()).forEach(p -> { try { Files.delete(p); } catch (Exception e) { throw new RuntimeException(e); } }); } }
    }
}
