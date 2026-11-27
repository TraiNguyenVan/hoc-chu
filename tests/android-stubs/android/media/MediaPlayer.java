package android.media;
import java.io.FileDescriptor;
public class MediaPlayer {
    public interface OnPreparedListener { void onPrepared(MediaPlayer player); }
    public interface OnCompletionListener { void onCompletion(MediaPlayer player); }
    public interface OnErrorListener { boolean onError(MediaPlayer player, int what, int extra); }
    public static MediaPlayer last; public static boolean failConstructor; public boolean started, released;
    private OnPreparedListener prepared; private OnCompletionListener completion; private OnErrorListener error;
    public MediaPlayer() { if (failConstructor) throw new RuntimeException("Cannot initialize decoder"); last = this; }
    public void setDataSource(FileDescriptor fd,long offset,long length) {}
    public void setOnPreparedListener(OnPreparedListener listener) { prepared=listener; }
    public void setOnCompletionListener(OnCompletionListener listener) { completion=listener; }
    public void setOnErrorListener(OnErrorListener listener) { error=listener; }
    public void prepareAsync() {}
    public void start() { started=true; }
    public void release() { released=true; }
    public void prepared() { prepared.onPrepared(this); }
    public void complete() { completion.onCompletion(this); }
    public void error() { error.onError(this,1,0); }
}
