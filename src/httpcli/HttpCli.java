package httpcli;

import java.io.IOException;
import httpcli.adapter.FactoryAdapter;
import httpcli.adapter.RespBodyAdapter;

public class HttpCli implements HttpStack {
  /** Singleton de la clase. */
  private static HttpCli instance;
  
  /** Cola de peticiones que se procesaran a la red. */
  private RequestDispatcher dispatcher;
  
  /** Procesara las peticiones a internet. */
  protected final HttpStack httpStack;
 
  /** Fabrica para los adaptadores. */
  private FactoryAdapter factoryAdapter;

  /** Modo debug. */
  private boolean debug;
  
  public HttpCli(HttpStack stack) {
    httpStack = stack;
  }
 
  public HttpCli() {
    this(new HttpUrlStack());
  }
  
  public static HttpCli get() {
    if (instance == null) 
      instance = new HttpCli();
    return instance;
  }
  
  public static void set(HttpCli restlight) {
    instance = restlight;
  }

  public boolean isDebug() {
    return debug;
  }
  
  public HttpCli setDebug(boolean b) {
    debug = b;
    return this;
  }
  
  /**
   * @return La cola de despacho.
   */
  public RequestDispatcher dispatcher() {
    if (dispatcher == null)
        dispatcher = RequestDispatcher.get();
    return dispatcher;
  }

  public HttpCli setDispatcher(RequestDispatcher dispatcher) {
    this.dispatcher = dispatcher;
    return this;
  }
  
  public HttpStack stack() {
    return httpStack;
  }

  public FactoryAdapter factory() {
    if (factoryAdapter == null)
        factoryAdapter = FactoryAdapter.get();
    return factoryAdapter;
  }

  public HttpCli setFactory(FactoryAdapter factoryAdapter) {
    this.factoryAdapter = factoryAdapter;
    return this;
  }
  
  /**
   * Envíe sincrónicamente la solicitud y devuelva su respuesta.
   * 
   * @param request petición a realizar
   * 
   * @return una respuesta para el tipo de petición realizada
   * 
   * @throws java.io.IOException si se produjo un problema al hablar con el
   * servidor
   */
  public ResponseBody execute(HttpRequest request) throws IOException {
    if (debug) request.setDebug(debug);
    return stack().execute(request);
  }

  public <V> V execute(HttpRequest request, RespBodyAdapter<V> adapter) throws Exception {
    ResponseBody response = null;
    try {
      response = execute(request);
      return adapter.parse(response);
      
    } catch(Exception e) {
      if (response != null)
        response.close();
      
      throw e;
    }
  }
  
  public <V> V execute(HttpRequest request, Class<V> classOf) throws Exception {
    return execute(request, factory().respBodyAdapter(classOf));
  }
 
  /**
   * Crea una invocación de un método que envía una solicitud a un servidor web 
   * y devuelve una respuesta.
   * 
   * @param <V>
   * @param request petición a realizar
   * @param adapter adaptador para parsear la respuesta
   * 
   * @return una llamada
   */
  public <V> HttpCall<V> newCall(HttpRequest request, RespBodyAdapter<V> adapter) {
    return new AsyncHttpCall<V>(this, request, adapter);
  }
  
  public <V> HttpCall<V> newCall(HttpRequest request, Class<V> classOf) {
    return newCall(request, factory().respBodyAdapter(classOf));
  }

  public <V> RequestBody requestBody(V src) {
    return factory().requestBody(src);
  }
  
  public <V> FormBody formBody(V src) {
    return factory().formBody(src);
  }
  
  public <V> MultipartBody multipartBody(V src) {
    return factory().multipartBody(src);
  }
}