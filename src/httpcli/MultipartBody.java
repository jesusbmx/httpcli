/**
 * ====================================================================
 * Codificacion {@code multipart/form-data}.
 * Contenido mixto POST (datos binarios y caracter) RFC2388.
 * ====================================================================
 */
package httpcli;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import httpcli.io.IOUtils;

public class MultipartBody extends RequestBody {
  /** Espacio colon */
  public static final byte[] COLON_SPACE = {':', ' '};
  
  /** Linea separadora. */
  public static final byte[] CR_LF = {'\r', '\n'};
  
  /** Dos Guiones. */
  public static final byte[] TWO_DASHES = {'-', '-'};

  /** Lista de datos. */
  protected final List<Part> parts;
  
  /** Variable requerida para : multipart/form-data. */
  protected final String boundary;
  
  
  public MultipartBody(List<Part> parts, String boundary) {
    this.parts = parts;
    this.boundary = boundary;
  }
  
  public MultipartBody(String boundary) {
    this(new ArrayList<Part>(), boundary);
  }
  
  public MultipartBody() {
    this(Long.toOctalString(System.currentTimeMillis()));
  }
  
  public MultipartBody(Map<String, Object> map) {
    this();
    addMap(map);
  } 
  
  
  public String boundary() {
    return boundary;
  }
  
  @Override public String contentType(Charset charset) {
    return "multipart/form-data; boundary=" + boundary;
  }
  
  @Override public long contentLength(Charset charset) throws IOException {
    long len = 0;
    for (int i = 0; i < parts.size(); i++) {
      Part part = parts.get(i);
      len += part.body.contentLength(charset);
    }
    ByteArrayOutputStream baos = null;
    try {
      baos = IOUtils.arrayOutputStream();
      doWrite(baos, charset, Boolean.FALSE);
      return baos.size() + len;
    } finally {
      IOUtils.closeQuietly(baos);
    }
  }

  @Override public void writeTo(OutputStream out, Charset charset) throws IOException {
    doWrite(out, charset, Boolean.TRUE);
  }
  
  private void doWrite(OutputStream out, Charset charset, boolean write) throws IOException {
    byte[] boundaryToCharArray = boundary.getBytes();

    for (Part part : parts) {      
      out.write(TWO_DASHES);
      out.write(boundaryToCharArray);
      out.write(CR_LF);

      // Write Format Multipart Header:
      for (int i = 0, size = part.headers.size(); i < size; i++) {
        out.write(part.headers.name(i).getBytes(charset));
        out.write(COLON_SPACE);
        out.write(part.headers.value(i).getBytes(charset));
        out.write(CR_LF);
      }
      out.write(Headers.CONTENT_TYPE.getBytes(charset));
      out.write(COLON_SPACE);
      out.write(part.body.contentType(charset).getBytes(charset));
      out.write(CR_LF);
      
      // Write Body:
      out.write(CR_LF);
      if (write) part.body.writeTo(out, charset);
      out.write(CR_LF);
    }

    // End of multipart/form-data.
    out.write(TWO_DASHES);
    out.write(boundaryToCharArray);
    out.write(TWO_DASHES);
    out.write(CR_LF);
  }

  public MultipartBody addPart(Part bodyPart) {
    parts.add(bodyPart);
    return this;
  }
  
  public MultipartBody addBody(String name, RequestBody body) {
    return addPart(Part.createFormData(name, body));
  }
  
  public MultipartBody addBody(RequestBody body) {
    return addPart(new Part(body, new Headers()));
  }
  
  public MultipartBody addParam(String name, Object value) {
    String newValue = value == null ? "" : value.toString();
    RequestBody body = RequestBody.create("text/plain", newValue);
    return addPart(Part.createFormData(name, body));
  }

  public MultipartBody addFile(String name, File file) {
    return addFile(name, file, file.getName());
  }
  
  public MultipartBody addFile(String name, File file, String filename) {
    RequestBody body = RequestBody.create("application/octet-stream", file, Boolean.FALSE);
    return addPart(Part.createFormData(name, filename, body));
  }
  
  public MultipartBody addFile(String name, byte[] value, String filename) {
    RequestBody body = RequestBody.create("application/octet-stream", value, Boolean.FALSE);
    return addPart(Part.createFormData(name, filename, body));
  }
  
  public MultipartBody addObject(String name, Object value) {
    if (value == null) 
      addParam(name, value);
    else if (value instanceof File) 
      addFile(name, (File) value);
    else if (value instanceof Part) 
      addPart((Part) value);
    else if (value instanceof RequestBody) 
      addBody(name, (RequestBody) value);
    else
      addParam(name, value);
    
    return this;
  }
  
  public MultipartBody addMap(Map<String, Object> map) {
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      addObject(entry.getKey(), entry.getValue());
    }
    return this;
  }

  public List<Part> parts() {
    return parts;
  }
  
  public static class Part {

    final RequestBody body;
    final Headers headers;

    public Part(RequestBody body, Headers headers) {
      this.body = body;
      this.headers = headers;
    }

    public Headers headers() {
      return headers;
    }

    public RequestBody body() {
      return body;
    }
    
    public static Part createFormData(String name, RequestBody body) {
      Headers headers = Headers.of(
              Headers.CONTENT_DISPOSITION, String.format("form-data; name=\"%s\"", name),
              // .add(Headers.CONTENT_TYPE, String.format("text/plain; charset=%s", charset.name()))
              Headers.CONTENT_TRANSFER_ENCODING, "8bit");
      return new Part(body, headers);
    }

    public static Part createFormData(String name, String filename, RequestBody body) {
      Headers headers = Headers.of(
              Headers.CONTENT_DISPOSITION, String.format("form-data; name=\"%s\"; filename=\"%s\"", name, filename),
              // .add(Headers.CONTENT_TYPE, "application/octet-stream")
              Headers.CONTENT_TRANSFER_ENCODING, "binary");
      return new Part(body, headers);
    }
  }
  
  
}