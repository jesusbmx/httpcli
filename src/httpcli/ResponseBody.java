package httpcli;

import httpcli.adapter.RespBodyAdapter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import httpcli.io.IOUtils;

public class ResponseBody implements Closeable {
  public HttpRequest request;
  public int code;  
  public Headers headers;
  public String contentEncoding;
  public String contentType;
  public int contentLength;
  public boolean closed;
  public final InputStream in;
  
  public ResponseBody(InputStream in) {
    this.in = in;
  }
  
  public Reader charStream(Charset charset) throws IOException {
    return new InputStreamReader(in, charset);
  }
  
  public Reader charStream() throws IOException {
    return new InputStreamReader(in, request.charset);
  }
  
  public byte[] bytes() throws IOException {
    try {
      return IOUtils.toByteArray(in);
    } finally {
      close();
    }
  }
  
  public String string(Charset charset) throws IOException {
    byte[] data = bytes();
    return new String(data, charset);
  }
  
  public String string() throws IOException {
    return string(request.charset);
  }
  
  public void writeTo(OutputStream out) throws IOException {
    try {
      IOUtils.copy(in, out);
    } finally {
      close();
    }
  }
  
  public <V> V as(RespBodyAdapter<V> adapter) throws Exception {
    return adapter.parse(this);
  }
  
  public <V> V as(Class<V> classOf) throws Exception {
    if (request.cli == null) 
        throw new NullPointerException("This request does not have any HttpCli");
    return as(request.cli.factory().respBodyAdapter(classOf));
  }
  
  @Override public void close() {
    if (!closed) {
      closed = Boolean.TRUE;
      IOUtils.closeQuietly(in);
    }
  }   
}
