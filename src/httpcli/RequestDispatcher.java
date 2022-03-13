package httpcli;

/*import android.os.Process;*/

public class RequestDispatcher extends Thread {

  /** Cola de peticiones al servidor. */
  private final HttpCli cli;
  
  /** Es usado para decir que el hilo a muerto. */
  private volatile boolean quit = false;

  /**
   * @param cli cola de peticiones.
   */
  public RequestDispatcher(HttpCli cli) {
    this.cli = cli;
    setPriority(MIN_PRIORITY);
  }

  /**
   * Obliga al hilo a detenerce inmediatamente.
   */
  @Override public void interrupt() {
    quit = true;
    super.interrupt();
  }

  /**
   * Metodo que desarrolla un bucle que estara observando si existe una o varias
   * 'Request' en la cola, si hay una request la procesara por medio del objeto
   * 'NetworkConnection'.
   */
  @Override public void run() {
    /*Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);*/
    while (true) {
      HttpCall<?> request;
      
      try {
        // Toma y quita la peticion de la cola.
        request = cli.networkQueue().take();
      } catch (InterruptedException e) {
        // El hilo pudo haber sido interrumpido.
        if (quit) return;
        continue;
      }

      try {
        // Si la petición ya estaba cancelada, no funciona la petición de la red.
        if (request.isCancel()) continue;

        // Procesa la request.
        ResponseBody response = cli.execute(request.request());
        
        // Si la petición ya estaba cancelada, no funciona la petición de la red.
        if (request.isCancel()) {
          response.close();
          continue;
        }
         
        this.onResponse(request, 
                request.adapter().parse(response));
        
      } catch (Exception e) {
        // TODO: handle exception
        this.onFailure(request.callback(), e);
      
      } 
    }
  }
  
  /**
   * Metodo que se encarga de liverar la respuesta obtenida de la conexión.
   */
  public void onResponse(final HttpCall request, final Object result) {
    cli.executorDelivery().execute(new Runnable() {  
      
      @Override public void run() {
        HttpCallback callback = request.callback();
        if (callback != null) {
          try {
            callback.onResponse(result);
          } catch (Exception error) {
            callback.onFailure(error);
          } 
        }
        //request.atTheEnd(result);
      }
    });
  }

  /**
   * Metodo que se encarga de liverar el error obtenido de la conexión.
   */
  public void onFailure(final HttpCallback<?> callback, final Exception error) {
    cli.executorDelivery().execute(new Runnable() {
      
      @Override public void run() {
        if (callback != null) {
            callback.onFailure(error);
        }
      }
    });
  }

}