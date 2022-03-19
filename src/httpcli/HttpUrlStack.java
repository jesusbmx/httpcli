package httpcli;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import httpcli.io.IOUtils;
import java.io.FilterInputStream;

public class HttpUrlStack implements HttpStack { 
  
  /**
   * Abre una conexión HTTP a intenert apartir de una petición.
   *
   * @param src conección.
   *
   * @return una conexión HTTP abierta.
   *
   * @throws java.io.IOException
   */
  public HttpURLConnection open(URL src) throws IOException {
    HttpURLConnection conn = (HttpURLConnection) src.openConnection();
    // Workaround for the M release HttpURLConnection not observing the
    // HttpURLConnection.setFollowRedirects() property.
    // https://code.google.com/p/android/issues/detail?id=194495
    conn.setInstanceFollowRedirects(HttpURLConnection.getFollowRedirects());
    return conn;
  }

  /**
   * Abre una conexión HTTP a intenert apartir de una petición.
   *
   * @param request petición a intenert.
   *
   * @return una conexión HTTP abierta.
   *
   * @throws java.io.IOException
   */
  public HttpURLConnection open(HttpRequest request) throws IOException {
    String url = request.urlParams();
    request.d("%s %s", request.getMethod(), url);
    URL src = new URL(url);
    HttpURLConnection conn = open(src);
    conn.setConnectTimeout(request.getTimeoutMs());
    conn.setReadTimeout(request.getTimeoutMs());
    conn.setUseCaches(Boolean.FALSE);
    conn.setDoInput(Boolean.TRUE);
    return conn;
  }

  /**
   * Manda una lista de encabezados adicionales de HTTP para esta petición.
   *
   * @param conn HTTP
   * @param request peticion
   *
   * @throws IOException
   */
  public void writeHeaders(HttpURLConnection conn, HttpRequest request)
  throws IOException {
    Headers headers = request.getHeaders();
    if (headers != null) {
      request.d("Headers:\n%s", headers);
      for (int i = 0, size = headers.size(); i < size; i++) {
        conn.addRequestProperty(headers.name(i), headers.value(i));
      }
    }
  }
  
  /**
   * Escribe el cuerpo de de esta petición.
   *
   * @param conn HTTP
   * @param request peticion
   *
   * @throws IOException
   */
  public void writeBody(HttpURLConnection conn, HttpRequest request) 
  throws IOException {
    String method = request.getMethod();
    conn.setRequestMethod(method);

    if (request.requiresRequestBody()) {
      // Write request to server.
      writeTo(conn, request);
    }
  }

  /**
   * Metodo que se encargar de eviar los datos a internet por medio de una
   * escritura de streams.
   *
   * @param conn conexión abierta a internet
   * @param request peticion
   *
   * @throws java.io.IOException
   */
  public void writeTo(HttpURLConnection conn, HttpRequest request) throws IOException {
    RequestBody requestBody = request.getBody();
    if (requestBody != null) {
      String contentType = requestBody.contentType(request.getCharset());
      request.d("Body %s:", contentType);  
        
      // Setup connection:
      conn.setDoOutput(Boolean.TRUE);
      conn.addRequestProperty(Headers.CONTENT_TYPE, contentType);

      // Length:
      long contentLength = requestBody.contentLength(request.getCharset());
      conn.setFixedLengthStreamingMode((int) contentLength);

      // Write params:
      BufferedOutputStream bos = null;
      try {
        bos = new BufferedOutputStream(conn.getOutputStream());
        request.d(requestBody);
        requestBody.writeTo(bos, request.getCharset());
      } finally {
        IOUtils.closeQuietly(bos);
      }
    }
  }
  
  public ResponseBody getResponse(HttpURLConnection conn, HttpRequest request) 
  throws IOException {
    int responseCode = conn.getResponseCode();
    if (responseCode == -1) {
      // -1 is returned by getResponseCode() if the response code could not be retrieved.
      // Signal to the caller that something was wrong with the connection.
      throw new IOException("Could not retrieve response code from HttpUrlConnection.");
    }
    
    ResponseBody response = new ResponseBody(getInputStream(conn));
    response.request = request;
    response.code = responseCode;
    response.headers = Headers.of(conn.getHeaderFields());
    response.contentLength = conn.getContentLength();
    response.contentEncoding = conn.getContentEncoding();
    response.contentType = conn.getContentType();
    request.d("Response %s:\nHeaders:\n%s\nBody: %s", 
            response.code, response.headers, response.contentType);
    
    return response;
  }
  
  static InputStream getInputStream(final HttpURLConnection hurlc) {
    InputStream inputStream;
    try {
      inputStream = hurlc.getInputStream();
    } catch(IOException e) {
      inputStream = hurlc.getErrorStream();
    }
    return new FilterInputStream(inputStream) {
      @Override public void close() throws IOException {
        super.close();
        hurlc.disconnect();
      }
    };
  }
  
  /**
   * Ejecuta una petición.
   *
   * @param request petición a ejecutar
   *
   * @return el resultado de la petición realizada
   * 
   * @throws java.io.IOException
   */
  @Override public ResponseBody execute(HttpRequest request) throws IOException {
    HttpURLConnection conn = null;
    try {
      conn = open(request);
      writeHeaders(conn, request);
      writeBody(conn, request);
      return getResponse(conn, request);
     
    } catch (IOException e) {
      if (conn != null) conn.disconnect();
      throw e;
    }
  }
  
}