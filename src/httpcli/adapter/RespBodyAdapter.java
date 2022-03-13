package httpcli.adapter;

import httpcli.ResponseBody;

public interface RespBodyAdapter<T> {
    
    public T parse(ResponseBody respBody) throws Exception;
}
