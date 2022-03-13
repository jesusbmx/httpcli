package httpcli.adapter.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import httpcli.adapter.FactoryAdapter;
import httpcli.adapter.ReqBodyAdapter;
import httpcli.adapter.RespBodyAdapter;

public class GsonFactoryAdapter extends FactoryAdapter {

    public final Gson gson;

    public GsonFactoryAdapter(Gson gson) {
        this.gson = gson;
    }
    
    @Override
    public <V> RespBodyAdapter<V> newOtherRespBodyAdapter(final Class<V> classOf) {
        TypeAdapter<V> adapter = gson.getAdapter(classOf);
        return new RespBodyGson<V>(gson, adapter);
    }

    @Override
    public <V> ReqBodyAdapter<V> newOtherReqBodyAdapter(final Class<V> classOf) {
        TypeAdapter<V> adapter = gson.getAdapter(classOf);
        return new ReqBodyGson<V>(gson, adapter);
    }
 
}
