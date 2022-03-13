package httpcli.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IOUtils {

  public static final ByteArrayPool POOL = new ByteArrayPool(4096);

  private IOUtils() {
  }
  
  public static void copy(File file, OutputStream out) throws IOException {
    FileInputStream in = null;
    byte[] buffer = null;
    try {
      in = new FileInputStream(file);
      int bytesAvailable = in.available();
      
      int maxBufferSize = 1024 * 1024;
      int bufferSize = Math.min(bytesAvailable, maxBufferSize);
      buffer = POOL.getBuf(bufferSize);
      
      int count = in.read(buffer, 0, bufferSize);
      while (count > 0) {
        out.write(buffer, 0, bufferSize);
        bytesAvailable = in.available();
        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        count = in.read(buffer, 0, bufferSize);
      }
    } finally {
      POOL.returnBuf(buffer);
      closeQuietly(in);
    }
  }

  public static void copy(InputStream source, OutputStream out) throws IOException {
    if (source == null) throw new IOException("source == null");
    byte[] buffer = POOL.getBuf(1024);
    try {
      int count;
      while ((count = source.read(buffer)) != -1) {
        out.write(buffer, 0, count);
      }
    } finally {
      POOL.returnBuf(buffer);
    }
  }
  
  public static PoolingByteArrayOutputStream arrayOutputStream() {
    return new PoolingByteArrayOutputStream(POOL);
  }
  public static PoolingByteArrayOutputStream arrayOutputStream(int size) {
    return new PoolingByteArrayOutputStream(POOL, size);
  }
  
  public static byte[] toByteArray(InputStream source) throws IOException {
    return toByteArray(source, 1024);
  }
  public static byte[] toByteArray(InputStream source, int size) throws IOException {
    if (source == null) throw new IOException("source == null");
    PoolingByteArrayOutputStream bytes
            = new PoolingByteArrayOutputStream(POOL, source.available());
    byte[] buffer = POOL.getBuf(size);
    try {
      int count;
      while ((count = source.read(buffer)) != -1) {
        bytes.write(buffer, 0, count);
      }
      return bytes.toByteArray();
    } finally {
      POOL.returnBuf(buffer);
      bytes.close();
    }
  }

  public static void closeQuietly(Closeable closeable) {
    if (closeable == null) return;
    try {
      closeable.close();
    } catch (IOException ignore) {
      // empty
    }
  }
}