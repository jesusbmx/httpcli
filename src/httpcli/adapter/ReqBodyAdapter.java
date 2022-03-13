package httpcli.adapter;

import httpcli.RequestBody;

public interface ReqBodyAdapter<T> {
    
    public RequestBody parse(T obj);
}
