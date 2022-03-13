package httpcli;

public interface HttpCallback<T> {
  /**
   * Se llama cuando se recibe una respuesta.
   *
   * @param result respuesta obtenida
   * @throws java.lang.Exception error al recibir la respueta
   */
  void onResponse(T result) throws Exception;
  
  /**
   * Método de devolución de llamada que indica que se ha producido un error con
   * el error proporcionado código y mensaje opcional legible por el usuario.
   * @param e error causado
   */
  void onFailure(Exception e);
}
