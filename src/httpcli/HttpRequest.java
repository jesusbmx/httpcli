package httpcli;

import java.io.IOException;
import java.nio.charset.Charset;

public class HttpRequest {
  /** Codificación predeterminada. */
  public static final Charset DEFAULT_ENCODING = Charset.forName("utf-8");

  /** Tiempo limite de espera por default. */
  public static final int DEFAULT_TIMEOUT = 2500 * 2 * 2;
  
  /** Metodo por default. */
  public static final String DEFAULT_METHOD = "GET";
  
  /** Metodo de la request: OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE. */
  String method = DEFAULT_METHOD;
  
  /** Url de nuestra request. */
  HttpUrl url;
  
  /** Lista de encabezados adicionales de HTTP para esta peticion. */
  Headers headers;
 
  /** Parametros de nuestra request. */
  RequestBody body;
  
  /** Etiqueta para identificar la request. */ 
  Object tag = HttpRequest.class;
  
  /** Tiempo limite de espera. */
  int timeoutMs = DEFAULT_TIMEOUT;
  
  /** Codificacion. */
  Charset charset = DEFAULT_ENCODING;

  boolean isDebug;
  
  HttpCli cli;
  
  public HttpRequest() {
  }
  
  public HttpRequest(String method, String url, RequestBody body) {
    setMethod(method).setUrl(url).setBody(body);
  }
  
  public HttpRequest(String method, String url) {
    setMethod(method).setUrl(url);
  }
  
  public HttpRequest(String method, HttpUrl url, RequestBody body) {
    setMethod(method).setUrl(url).setBody(body);
  }
  
  public HttpRequest(String method, HttpUrl url) {
    setMethod(method).setUrl(url);
  }
    
  public String getMethod() {
    return method;
  }
  public HttpRequest setMethod(String method) {
    this.method = method.toUpperCase();
    return this;
  }
  
  public HttpUrl getUrl() {
    return url;
  }
  public HttpRequest setUrl(HttpUrl url) {
    this.url = url;
    return this;
  }
  public HttpRequest setUrl(String url) {
    return setUrl(new HttpUrl(url));
  }
  
  public Headers getHeaders() {
    return headers;
  }
  public HttpRequest setHeaders(Headers headers) {
    this.headers = headers;
    return this;
  }
  public HttpRequest addHeader(String name, String value) {
    if (headers == null) headers = new Headers();
    headers.add(name, value);
    return this;
  }

  public RequestBody getBody() {
    return body;
  }
  public HttpRequest setBody(RequestBody body) {
    this.body = body;
    return this;
  }
  
  public int getTimeoutMs() {
    return timeoutMs;
  }
  public HttpRequest setTimeoutMs(int timeoutMs) {
    this.timeoutMs = timeoutMs;
    return this;
  }

  public Charset getCharset() {
    return charset;
  }
  public HttpRequest setCharset(Charset charset) {
    this.charset = charset;
    return this;
  }

  
  public Object getTag() {
    return tag;
  }
  public HttpRequest setTag(Object tag) {
    this.tag = tag;
    return this;
  }

  public boolean isDebug() {
    return isDebug;
  }
  public HttpRequest setDebug(boolean isDebug) {
    this.isDebug = isDebug;
    return this;
  }
  
  
  public boolean requiresRequestBody() {
    return method.equals("POST") || method.equals("PUT") || method.equals("PATCH");
  }

  public String urlParams() {
    if (!requiresRequestBody())
      if (body != null)
        if (body instanceof FormBody) 
          return HttpUrl.toUrl(url.url, (FormBody)body, charset);
    
    return url.toString(charset);
  }
  
  @Override public String toString() {
    return "Request@" + hashCode() + " " + method + " " + url + "\nheaders:\n" 
            + headers + "\nbody: " + body + "\n\ntag=" + tag + ", timeoutMs=" + timeoutMs 
            + ", charset=" + charset + "\n---";
  }

  public void d(String format, Object... args) {
    if (isDebug) System.out.printf("Request@" + hashCode() + ": " + format + "\n", args);
  }
  public void d(RequestBody rb) throws IOException {
    if (isDebug) {
      rb.writeTo(System.out, charset);
      System.out.println("\n");
    }
  }
}