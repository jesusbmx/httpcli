package httpcli;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import httpcli.io.IOUtils;

public abstract class RequestBody {

  public abstract String contentType(Charset charset) throws IOException;

  public abstract long contentLength(Charset charset) throws IOException;

  public abstract void writeTo(OutputStream out, Charset charset) throws IOException;
  
  public static RequestBody create(final String contentType, final String content) {
    return new RequestBody() {
      @Override public String contentType(Charset charset) throws IOException {
        return generateContentType(contentType, charset);
      }
      @Override public long contentLength(Charset charset) throws IOException {
        return content.getBytes(charset).length;
      }
      @Override public void writeTo(OutputStream out, Charset charset) throws IOException {
        out.write(content.getBytes(charset));
      }
    };
  }
  
  public static RequestBody create(final String contentType, final byte[] content,
          final boolean encoding) {
    return new RequestBody() {
      @Override public String contentType(Charset charset) throws IOException {
        return (encoding) ? generateContentType(contentType, charset) : contentType;
      }
      @Override public long contentLength(Charset charset) throws IOException {
        return content.length;
      }
      @Override public void writeTo(OutputStream out, Charset charset) throws IOException {
        out.write(content);
      }
    };
  }
  
  public static RequestBody create(final String contentType, final File content,
          final boolean encoding) {
    return new RequestBody() {
      @Override public String contentType(Charset charset) throws IOException {
        return (encoding) ? generateContentType(contentType, charset) : contentType;
      }
      @Override public long contentLength(Charset charset) throws IOException {
        return content.length();
      }
      @Override public void writeTo(OutputStream out, Charset charset) throws IOException {
        IOUtils.copy(content, out);
      }
    };
  }
  
  public static String generateContentType(String contentType, Charset charset) {
    if (!contentType.contains("; charset=")) {
      String charsetName = charset.name();
      return new StringBuilder(contentType.length() + 10 + charsetName.length())
              .append(contentType).append("; charset=").append(charsetName)
              .toString();
    }
    return contentType;
  }
}
