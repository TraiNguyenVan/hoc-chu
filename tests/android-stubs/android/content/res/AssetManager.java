package android.content.res;
import java.io.*;
import java.nio.file.*;
public class AssetManager {
    private final Path root;
    public AssetManager(Path root) { this.root = root; }
    public AssetFileDescriptor openFd(String path) throws IOException { return new AssetFileDescriptor(new FileInputStream(root.resolve(path).toFile())); }
}
