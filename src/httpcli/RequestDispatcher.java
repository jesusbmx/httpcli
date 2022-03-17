package httpcli;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*import android.os.Process;*/

public class RequestDispatcher implements ThreadFactory {
  /** Numero de despachadores que atenderan las peticiones de la red. */
  static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;
  /** Singleton de la clase. */
  private static RequestDispatcher instance;

  /** Livera las respuestas al hilo de la UI. */
  private Executor executorDelivery;
  
  /** Ejecuta las llamadas "Call". */
  private ExecutorService executorService;
  
  public RequestDispatcher(ExecutorService executorService) {
    this.executorService = executorService;
  }

  public RequestDispatcher() {
  }
  
  public synchronized static RequestDispatcher get() {
    if (instance == null) {
      instance = new RequestDispatcher();
    }
    return instance;
  }
  
  @Override public Thread newThread(Runnable runnable) {
    Thread result = new Thread(runnable, "HttpCli RequestDispatcher");
    result.setPriority(Thread.MIN_PRIORITY);
    return result;
  }
  
  public synchronized ExecutorService executorService() {
    if (executorService == null) {
      executorService = new ThreadPoolExecutor(
              DEFAULT_NETWORK_THREAD_POOL_SIZE, DEFAULT_NETWORK_THREAD_POOL_SIZE
              , 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), this);
    }
    return executorService;
  }
  public void setExecutorService(ExecutorService es) {
    executorService = es;
  }
  
  /** Ejecuta la llamada en la cola de peticiones. */
  public synchronized boolean execute(AsyncHttpCall<?> task) { 
    if (task.isCancelled() || task.isDone()) return false;
    // Propone una tarea Runnable para la ejecución y devuelve un Futuro.
    task.future = executorService().submit(task);
    return true;
  }
    
  public Executor executorDelivery() {
    if (executorDelivery == null) {
      executorDelivery = Platform.get();
    }
    return executorDelivery;
  }
  public void setExecutorDelivery(Executor executor) {
    executorDelivery = executor;
  }
  
  public void delivery(Runnable runnable) {
    executorDelivery().execute(runnable);
  }
  
  /**
   * Metodo que se encarga de liverar la respuesta obtenida, al hilo de la UI.
   */
  public <V> void onResponse(final HttpCallback<V> callback, final V result) {
    delivery(new Runnable() {  
      @Override public void run() {
        try {
          callback.onResponse(result);
        } catch (Exception error) {
          callback.onFailure(error);
        }
      }
    });
  }

  /**
   * Metodo que se encarga de liverar el error obtenido, al hilo de la UI.
   */
  public void onFailure(final HttpCallback<?> callback, final Exception error) {
    delivery(new Runnable() {
      @Override public void run() {
        callback.onFailure(error);
      }
    });
  }



}