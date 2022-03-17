
package httpcli;

import httpcli.adapter.RespBodyAdapter;

public interface HttpCall<T> {
  /**
   * Envíe de forma asíncrona la solicitud y notifique su respuesta o si se 
   * produjo un error al hablar con el servidor, al crear la solicitud o al 
   * procesar la respuesta.
   * @param callback devolución de llamada
   */
  void execute(HttpCallback<T> callback);
  
  /**
   * Envía sincrónicamente la solicitud y devuelva su respuesta.
   */
  T execute() throws Exception;
  
  /**
   * Devuelve la petición de la invocación del método.
   * @return petición
   */
  HttpRequest request();
  
  /**
   * Obtiene el handler de esta peticion
   * @return 
   */
  HttpCallback<T> callback();
  
  /**
   * Devuelve el adaptador para la respuesta obtenida de la peticion.
   * @return 
   */
  RespBodyAdapter<T> adapter();
  
  /**
   * Trata de cancelar la ejecución de esta tarea.
   *
   * @param mayInterruptIfRunning {@code true} valida se el hilo que se ejecuta 
   * debe ser interrumpido; de lo contrario, se permiten tareas en curso
   * hasta completar.
   */
  boolean cancel(boolean mayInterruptIfRunning);
  
   /** Devuelve <tt>true</tt> si esta tarea estaba cancelada. */
  boolean isCancelled();

  /** Devuelve <tt>true</tt> si esta tarea se completó. */
  boolean isDone();
}
