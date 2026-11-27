package android.content.res;
import java.io.*;
public class AssetFileDescriptor implements AutoCloseable {
    private final FileInputStream stream;
    AssetFileDescriptor(FileInputStream stream) { this.stream = stream; }
    public FileDescriptor getFileDescriptor() throws IOException { return stream.getFD(); }
    public long getStartOffset() { return 0; }
    public long getLength() throws IOException { return stream.getChannel().size(); }
    public void close() throws IOException { stream.close(); }
}
