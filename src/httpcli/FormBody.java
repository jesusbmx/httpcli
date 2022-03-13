/**
 * ====================================================================
 * Codificacion {@code application-www-www-form-urlencoded}.
 * Los valores son codificados en tuplas de valores llaves separados 
 * por '&', con un '='  entre la llave y el valor.
 * ====================================================================
 */
package httpcli;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import httpcli.io.IOUtils;

public class FormBody extends RequestBody {

  protected final List<String> keys = new ArrayList<String>();
  protected final List<Object> values = new ArrayList<Object>();

  public FormBody() {
  }

  public FormBody(Map<String, Object> map) {
    addMap(map);
  } 
  
  /**
   * @param charset
   * @return el tipo de contenido para POST o PUT.
   */
  @Override public String contentType(Charset charset) {
    return "application/x-www-form-urlencoded; charset=" + charset.name();
  }
  
  @Override public long contentLength(Charset charset) throws IOException {
    ByteArrayOutputStream baos = null;
    try {
      baos = IOUtils.arrayOutputStream();
      writeTo(baos, charset);
      return baos.size();
    } finally {
      IOUtils.closeQuietly(baos);
    }
  }

  @Override public void writeTo(OutputStream out, Charset chrst) throws IOException {
    for (int i = 0, size = size(); i < size; i++) {
      if (i > 0) out.write('&');
      out.write(URLEncoder.encode(key(i), chrst.name()).getBytes());
      out.write('=');
      out.write(URLEncoder.encode(valueStr(i), chrst.name()).getBytes());
    }
  }
  
  public String encodedUrlParams(Charset chrst) throws IOException {
    ByteArrayOutputStream baos = null;
    try {
      baos = IOUtils.arrayOutputStream();
      writeTo(baos, chrst);
      return new String(baos.toByteArray(), chrst);
    } finally {
      IOUtils.closeQuietly(baos);
    }
  }
  
  public int size() {
    return keys.size();
  }
  
  public FormBody add(String key, Object value) {
    keys.add(key);
    values.add(value);
    return this;
  }
  
  public FormBody addMap(Map<String, Object> map) {
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      add(entry.getKey(), entry.getValue());
    }
    return this;
  }

  public String key(int index) {
    return keys.get(index);
  }
  
  public Object value(int index) {
    return values.get(index);
  }
  
  public String valueStr(int index) {
    Object value = value(index);
    return value == null ? "" : value.toString();
  }

  @Override public String toString() {
    try {
      return encodedUrlParams(HttpRequest.DEFAULT_ENCODING);
    } catch(Exception e) {
      return "";
    }
  }
}
