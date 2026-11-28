package vn.khoaikhoai.hocchu;

import java.util.ArrayList;
import java.util.List;

public class AudioTest {
    private static void check(boolean ok, String message) { if (!ok) throw new AssertionError(message); }
    private static class Backend implements ReviewedAudio.Backend {
        List<String> clips = new ArrayList<>(); String current; int starts, stops; boolean fail;
        public boolean exists(String path) { return clips.contains(path); }
        public boolean start(String path, Runnable onError) { starts++; if (fail) return false; current = path; return true; }
        public void stop() { stops++; current = null; }
    }
    public static void main(String[] args) {
        Backend backend = new Backend(); ReviewedAudio audio = new ReviewedAudio(backend);
        check("audio/nam/0/61.mp3".equals(ReviewedAudio.path(0, "a")), "ASCII name");
        check("audio/nam/2/c4836e.mp3".equals(ReviewedAudio.path(2, "ăn")), "UTF-8 exact name");
        check(!ReviewedAudio.path(0, "a").equals(ReviewedAudio.path(1, "a")), "Categories must not collide");
        check(!audio.available(0, "a"), "Missing clip unavailable");
        check(!audio.play(0, "a", () -> {}), "Missing clip cannot play");
        check(backend.starts == 0, "No fallback or wrong audio");
        String first = ReviewedAudio.path(0, "a"); backend.clips.add(first);
        check(audio.available(0, "a"), "Existing clip available");
        check(audio.play(0, "a", () -> {}), "Play existing clip");
        check(first.equals(backend.current), "Play exact clip");
        check(audio.play(0, "a", () -> {}), "Replay existing clip");
        check(backend.starts == 2 && backend.stops >= 2, "Replay replaces prior playback");
        String second = ReviewedAudio.path(2, "ăn"); backend.clips.add(second);
        check(audio.play(2, "ăn", () -> {}), "Play another clip");
        check(second.equals(backend.current), "Second item has its own clip");
        audio.stop(); check(backend.current == null, "Stop releases playback");
        String north = ReviewedAudio.path("bac", 0, "a"); backend.clips.add(north);
        audio.setVoice("bac"); check(audio.play(0,"a",()->{}), "North clip plays"); check(north.equals(backend.current), "Selected voice used");
        audio.setVoice("nam"); check(backend.current == null, "Switch stops playback");
        backend.fail = true; check(!audio.play(0, "a", () -> {}), "Playback failure is unavailable");
        boolean rejected = false; try { ReviewedAudio.path(3, "a"); } catch (IllegalArgumentException e) { rejected = true; }
        check(rejected, "Invalid category rejected");
        rejected = false; try { ReviewedAudio.path(0, ""); } catch (IllegalArgumentException e) { rejected = true; }
        check(rejected, "Empty label rejected");
        System.out.println("PASS: audio exact mapping, availability, replay, switching, stop and errors");
    }
}
