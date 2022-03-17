package httpcli;

import httpcli.adapter.RespBodyAdapter;
import java.util.concurrent.Future;

public class AsyncHttpCall<T> 
   implements HttpCall<T>, HttpCallback<T>, Runnable {

  final RequestDispatcher dispatcher;
  HttpCallback<T> callback;
  Future future;
  boolean cancel;
  boolean running = false;
  
  final HttpCli cli;
  final HttpRequest request;
  final RespBodyAdapter<T> adapter;

  public AsyncHttpCall(HttpCli cli, HttpRequest request, RespBodyAdapter<T> adapter) {
    this.cli = cli;
    this.request = request;
    this.adapter = adapter;
    this.dispatcher = cli.dispatcher();
  }
  
  @Override public HttpRequest request() {
    return this.request;
  }

  @Override public HttpCallback<T> callback() {
    return this.callback;
  }
  
  @Override public RespBodyAdapter<T> adapter() {
    return this.adapter;
  }
  
   @Override public boolean isCancelled() {
    return (this.future != null) ? this.future.isCancelled() : this.cancel;
  }

  @Override public boolean isDone() {
    return (this.future != null) ? this.future.isDone() : false;
  }

  public boolean isRunning() {
    return this.running;
  }

  @Override public void execute(HttpCallback<T> callback) {
    this.callback = callback;
    this.running = this.dispatcher.execute(this);
  }
  
  @Override public T execute() throws Exception {
    return this.cli.execute(request, adapter);
  }
  
  @Override public boolean cancel(boolean mayInterruptIfRunning) {
    this.running = false;
    if (this.future != null) { 
      return this.future.cancel(mayInterruptIfRunning);
    } else { 
      this.cancel = true;
      return false;
    }
  }
  
  @Override public void onResponse(T result) throws Exception {
    if (this.callback != null) this.callback.onResponse(result);
  }

  @Override public void onFailure(Exception e) {
    if (this.callback != null) this.callback.onFailure(e);
  }

 @Override public void run() {
    try {
      T result = this.execute();
      this.dispatcher.onResponse(this, result);
    } catch (Exception e) {
      this.dispatcher.onFailure(this, e);
    }
    this.running = false;
  }
  
  public void delivery(Runnable run) {
    this.dispatcher.delivery(run);
  }
}
