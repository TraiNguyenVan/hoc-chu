package vn.khoaikhoai.hocchu;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import java.io.IOException;

/** Opens only bundled, uncompressed audio assets; one player at a time. */
public final class AndroidAudioBackend implements ReviewedAudio.Backend {
    private final AssetManager assets;
    private MediaPlayer player;
    public AndroidAudioBackend(AssetManager assets) { this.assets = assets; }
    @Override public boolean exists(String path) {
        try (AssetFileDescriptor ignored = assets.openFd(path)) { return true; }
        catch (IOException | RuntimeException e) { return false; }
    }
    @Override public boolean start(String path, Runnable onError) {
        stop();
        MediaPlayer next;
        try { next = new MediaPlayer(); }
        catch (RuntimeException e) { return false; }
        player = next;
        try (AssetFileDescriptor fd = assets.openFd(path)) {
            next.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            next.setOnPreparedListener(p -> { if (player == p) { try { p.start(); } catch (RuntimeException e) { stop(); onError.run(); } } });
            next.setOnCompletionListener(p -> { if (player == p) stop(); });
            next.setOnErrorListener((p, what, extra) -> { if (player == p) { stop(); onError.run(); } return true; });
            next.prepareAsync();
            return true;
        } catch (IOException | RuntimeException e) {
            stop(); return false;
        }
    }
    @Override public void stop() { if (player != null) { MediaPlayer old = player; player = null; old.release(); } }
}
