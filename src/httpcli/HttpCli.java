package httpcli;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import httpcli.adapter.FactoryAdapter;
import httpcli.adapter.RespBodyAdapter;

public class HttpCli implements HttpStack {
  /** Numero de despachadores que atenderan las peticiones de la red. */
  static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;
  
  private static HttpCli instance;
  
  /** Cola de peticiones que se procesaran a la red. */
  private BlockingQueue<HttpCall<?>> networkQueue;
  
  /** Procesara las peticiones a internet. */
  protected final HttpStack httpStack;
  
  /** Hilo que atendera la cola. */
  protected final Thread[] dispatchers;

  /** Puente que comunica las tareas con el hilo principal. */
  private Executor executorDelivery;
 
  /** Fabrica para los adaptadores. */
  private FactoryAdapter factoryAdapter;

  /** Modo debug. */
  private boolean debug;
  
  public HttpCli(HttpStack stack, int threadPoolSize) {
    dispatchers = new Thread[threadPoolSize];
    executorDelivery = Platform.get();
    httpStack = stack;
  }
 
  private HttpCli(HttpStack stack) {
    this(stack, DEFAULT_NETWORK_THREAD_POOL_SIZE);
  }
  
  public static HttpCli get() {
    if (instance == null) 
      instance = new HttpCli(new HttpUrlStack());
 
    return instance;
  }
  
  public static void set(HttpCli restlight) {
    if (instance != null) 
        instance.stop();
    
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
  public BlockingQueue<HttpCall<?>> networkQueue() {
    if (networkQueue == null) {
       networkQueue = new LinkedBlockingQueue<HttpCall<?>>();
       start();
    }
    return networkQueue;
  }  
  
  public HttpStack stack() {
    return httpStack;
  }

  public Executor executorDelivery() {
    return executorDelivery;
  }
  
  public HttpCli setExecutorDelivery(Executor executor) {
    executorDelivery = executor;
    return this;
  }

  public FactoryAdapter factory() {
    if (factoryAdapter == null)
        factoryAdapter = new FactoryAdapter();
    return factoryAdapter;
  }

  public HttpCli setFactory(FactoryAdapter factoryAdapter) {
    this.factoryAdapter = factoryAdapter;
    return this;
  }
  
  /**
   * Inicia los hilos que atendera la cola de peticiones.
   */
  public void start() {
    stop();
    for (int i = 0; i < dispatchers.length; i++) {
      dispatchers[i] = new RequestDispatcher(this);
      dispatchers[i].start();
    }
  }

  /**
   * Obliga a detener todos los hilos.
   */
  public void stop() {
    for (int i = 0; i < dispatchers.length; i++) {
      if (dispatchers[i] != null) {
        dispatchers[i].interrupt();
        dispatchers[i] = null;
      }
    }
  }
   
  /**
   * Envía de manera asíncrona la petición y notifica a tu aplicación con un
   * callback cuando una respuesta regresa. Ya que esta petición es asíncrona,
   * la ejecución se maneja en un hilo de fondo para que el hilo de la UI
   * principal no sea bloqueada o interfiera con esta.
   * 
   * @param request petición a realizar
   */
  public <V> HttpCall<V> enqueue(HttpCall<V> request) {
    networkQueue().add(request);
    return request;
  }
  
  /**
   * Elimina una Peticion a la cola de despacho.
   *
   * @param request La peticion a remover
   * @return La peticion removida
   */
  public <V> HttpCall<V> remove(HttpCall<V> request) {
    networkQueue.remove(request);
    return request;
  }
  
  /**
   * Cancela todas las peticiones en esta cola.
   */
  public synchronized void cancelAll() {
    for (HttpCall<?> request : networkQueue()) {
      request.cancel();
    }
  }
  
  /**
   * Cancela todas las peticiones de esta cola con la etiqueta dada.
   */
  public synchronized void cancelAll(final Object tag) {
    for (HttpCall<?> request : networkQueue()) {
      if (request.request().getTag() == tag) {
        request.cancel();
      }
    }
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
    return new HttpCallAdapter<V>(this, request, adapter);
  }
  
  public <V> HttpCall<V> newCall(HttpRequest request, Class<V> classOf) {
    return newCall(request, factory().respBodyAdapter(classOf));
  }

  public <V> RequestBody requestBody(V src) {
    return factory().requestBody(src);
  }
}