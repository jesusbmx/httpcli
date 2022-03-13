package httpcli;

import httpcli.adapter.RespBodyAdapter;

public class HttpCallAdapter<T> implements HttpCall<T> {

  final HttpCli cli;
  final HttpRequest request;
  final RespBodyAdapter<T> adapter;
  HttpCallback<T> callback;

  public HttpCallAdapter(HttpCli cli, HttpRequest request, RespBodyAdapter<T> adapter) {
    this.cli = cli;
    this.request = request;
    this.adapter = adapter;
  }
  
  @Override public void execute(HttpCallback<T> callback) {
    this.callback = callback;
    cli.enqueue(this);
  }
  
  @Override public T execute() throws Exception {
    return cli.execute(request, adapter);
  }
  
  @Override public HttpRequest request() {
    return request;
  }

  @Override public HttpCallback<T> callback() {
    return callback;
  }
  
  @Override public RespBodyAdapter<T> adapter() {
    return adapter;
  }
  
  @Override public void cancel() {
    request.cancel();
  }
  
  @Override public boolean isCancel() {
    return request.isCanceled();
  }
}
