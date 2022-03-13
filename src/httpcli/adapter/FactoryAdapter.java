package httpcli.adapter;

import httpcli.ResponseBody;
import java.util.HashMap;
import httpcli.json.JSON;
import java.io.File;

public class FactoryAdapter {

    private final HashMap<Class, RespBodyAdapter> respBodyAdapters = 
            new HashMap<Class, RespBodyAdapter>();
    
    public <V> ReqBodyAdapter<V> reqBodyAdapter(Class<V> classOf) {
      throw new RuntimeException("No adapter found for class '"+classOf+"'");
    }
    
    public <V> RespBodyAdapter<V> respBodyAdapter(Class<V> classOf) {
      RespBodyAdapter<V> adapter = respBodyAdapters.get(classOf);
      if (adapter == null) {
        adapter = newRespBodyAdapter(classOf);        
        setRespBodyAdapter(classOf, adapter);
      }
      return adapter;
    }
    
    public <V> FactoryAdapter setRespBodyAdapter(Class<V> classOf, RespBodyAdapter<V> adapter) {
        respBodyAdapters.put(classOf, adapter);
        return this;
    }
    
    public <V> RespBodyAdapter<V> newRespBodyAdapter(Class<V> classOf) {
      String name = classOf.getCanonicalName();

      if (classOf == String.class) 
          return (RespBodyAdapter<V>) new RespBodyString();
      if (name.equals("org.json.JSONObject")) 
          return (RespBodyAdapter<V>) new RespBodyJSON.Object();
      if (name.equals("org.json.JSONArray")) 
          return (RespBodyAdapter<V>) new RespBodyJSON.Array();
      if (classOf == JSON.class) 
          return (RespBodyAdapter<V>) new RespBodyJSON();
      if (classOf == File.class)
          return (RespBodyAdapter<V>) new RespBodyFile();
      if (classOf == ResponseBody.class)
          return (RespBodyAdapter<V>) new RespBody();
      
      return newOtherRespBodyAdapter(classOf);
    }
    
    public <V> RespBodyAdapter<V> newOtherRespBodyAdapter(Class<V> classOf) {
        throw new RuntimeException("No adapter found for class '"+classOf+"'");
    }
}
