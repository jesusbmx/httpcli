package httpcli.adapter.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import httpcli.adapter.FactoryAdapter;
import httpcli.adapter.ReqBodyAdapter;
import httpcli.adapter.RespBodyAdapter;
import java.lang.reflect.Type;
import java.util.Map;

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

    @Override
    public <V> Map<String, Object> toMap(V src) {
        if (src == null) return null;
        Class<V> classOf = (Class<V>) src.getClass();
        
        TypeAdapter<V> adapter = gson.getAdapter(classOf);
        String json = adapter.toJson(src);
        
        Type empMapType = new TypeToken<Map<String, Object>>() {}.getType();
        return gson.fromJson(json, empMapType);
    }   
}
