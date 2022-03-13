package httpcli;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HttpUrl {
  final String url;
  final List<String> paths;
  final List<String> keys;
  final List<Object> values;

  public HttpUrl(String url, List<String> paths , List<String> keys, List<Object> values) {
    this.url = url;
    this.paths = paths;
    this.keys = keys;
    this.values = values;
  }
  
  public HttpUrl(String url) {
    this(url, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<Object>());
  }
  
  public String getUrl() {
    return url;
  }
  
  public HttpUrl addFormBody(FormBody body) {
    for (int i = 0; i < body.size(); i++) {
      addQueryParameter(body.key(i), body.value(i));
    }
    return this;
  }
  
  public HttpUrl addPathSegment(String path) {
    paths.add(path);
    return this;
  }
  
  public String path(int index) {
    return paths.get(index);
  }
  
  public String urlPaths(Charset chrst) throws IOException {
    if (paths.isEmpty()) return url;
    StringBuilder sb = new StringBuilder(url);
    if (!url.endsWith("/")) sb.append('/');
    for (int i = 0; i < paths.size(); i++) {
      if (i > 0) sb.append('/');
      sb.append(URLEncoder.encode(path(i), chrst.name()));
    }
    return sb.toString();
  }
  
  public int size() {
    return keys.size();
  }
  
  public HttpUrl addQueryParameter(String key, Object value) {
    keys.add(key);
    values.add(value);
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
    
  public String encodedUrlParams(Charset chrst) throws IOException {
    StringBuilder sb = new StringBuilder();
    for (int i = 0, size = size(); i < size; i++) {
      if (i > 0) sb.append('&');
      sb.append(URLEncoder.encode(key(i), chrst.name()));
      sb.append('=');
      sb.append(URLEncoder.encode(valueStr(i), chrst.name()));
    }
    return sb.toString();
  }
  
  public static String toUrl(String url, FormBody body, Charset charset) {
    return new HttpUrl(url, Collections.EMPTY_LIST, body.keys, body.values)
            .toString(charset);
  }
  
  public String toString(Charset charset) {
    try {
      String rurl = urlPaths(charset);
      if (size() == 0) return rurl;
    
      String encodedParams = encodedUrlParams(charset);

      if (encodedParams.length() > 0) 
        return new StringBuilder(rurl.length() + 1 + encodedParams.length())
            .append(rurl)
            .append(rurl.endsWith("?") ? '&' : '?')
            .append(encodedParams).toString();
      
    } catch(IOException ignore) {
      // empty
    }
    
    return url;
  }
  
  @Override public String toString() {
    return toString(HttpRequest.DEFAULT_ENCODING);
  }
}
