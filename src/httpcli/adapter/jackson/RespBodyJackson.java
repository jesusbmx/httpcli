package httpcli.adapter.jackson;

import com.fasterxml.jackson.databind.ObjectReader;
import httpcli.ResponseBody;
import httpcli.adapter.RespBodyAdapter;

public class RespBodyJackson<T> implements RespBodyAdapter<T> {

    public final ObjectReader adapter;

    public RespBodyJackson(ObjectReader adapter) {
        this.adapter = adapter;
    }
    
    @Override
    public T parse(ResponseBody respBody) throws Exception {
        try {
            return adapter.readValue(respBody.charStream());
        } finally {
            respBody.close();
        }
    }
    
}
