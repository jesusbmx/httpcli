package httpcli.adapter.jackson;

import com.fasterxml.jackson.databind.ObjectWriter;
import httpcli.RequestBody;
import httpcli.adapter.ReqBodyAdapter;

public class ReqBodyJackson<T> implements ReqBodyAdapter<T> {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";
    //private static final Charset UTF_8 = Charset.forName("UTF-8");
    
    public final ObjectWriter adapter;

    public ReqBodyJackson(ObjectWriter adapter) {
        this.adapter = adapter;
    }
  
    @Override
    public RequestBody parse(T value) throws Exception {
        byte[] bytes = adapter.writeValueAsBytes(value);
        return RequestBody.create(MEDIA_TYPE, bytes, true);
    }
      
}
