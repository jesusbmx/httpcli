package httpcli.adapter;

import httpcli.adapter.json.RespBodyJSON;
import httpcli.adapter.json.JSON;
import httpcli.FormBody;
import httpcli.MultipartBody;
import httpcli.RequestBody;
import httpcli.ResponseBody;
import java.util.HashMap;
import java.io.File;
import java.util.Iterator;
import java.util.Map;

public class FactoryAdapter {
    /** Singleton de la clase. */
    private static FactoryAdapter instance;

    /** Cache de adaptadores para las respuestas. */
    protected final Map<Class, RespBodyAdapter> respBodyAdapters = 
            new HashMap<Class, RespBodyAdapter>();
    
    /** Cache de adaptadores para las peticiones. */
    protected final Map<Class, ReqBodyAdapter> reqBodyAdapters = 
            new HashMap<Class, ReqBodyAdapter>();

    public FactoryAdapter() {
    }
    
    public static FactoryAdapter get() {
        if (instance == null)
            instance = new FactoryAdapter();
        return instance;
    }
    
    public <V> RespBodyAdapter<V> respBodyAdapter(Class<V> classOf) {
      RespBodyAdapter<V> adapter = getRespBodyAdapter(classOf);
      if (adapter == null) {
        adapter = newRespBodyAdapter(classOf);        
        setRespBodyAdapter(classOf, adapter);
      }
      return adapter;
    }
    
    public <V> RespBodyAdapter<V> getRespBodyAdapter(Class<V> classOf) {
        return respBodyAdapters.get(classOf);
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
          return (RespBodyAdapter<V>) new RespBodyRB();
      
      return newOtherRespBodyAdapter(classOf);
    }
    
    public <V> RespBodyAdapter<V> newOtherRespBodyAdapter(Class<V> classOf) {
        throw new RuntimeException("No adapter found for class '"+classOf+"'");
    }
    
    public <V> ReqBodyAdapter<V> reqBodyAdapter(Class<V> classOf) {
      ReqBodyAdapter<V> adapter = getReqBodyAdapter(classOf);
      if (adapter == null) {
        adapter = newReqBodyAdapter(classOf);        
        setReqBodyAdapter(classOf, adapter);
      }
      return adapter;
    }
 
    public <V> ReqBodyAdapter<V> getReqBodyAdapter(Class<V> classOf) {
        return reqBodyAdapters.get(classOf);
    }
    
    public <V> FactoryAdapter setReqBodyAdapter(Class<V> classOf, ReqBodyAdapter<V> adapter) {
        reqBodyAdapters.put(classOf, adapter);
        return this;
    }
    
    public <V> ReqBodyAdapter<V> newReqBodyAdapter(Class<V> classOf) {
      return newOtherReqBodyAdapter(classOf);
    }
    
    public <V> ReqBodyAdapter<V> newOtherReqBodyAdapter(Class<V> classOf) {
        throw new RuntimeException("No adapter found for class '"+classOf+"'");
    }

    public <V> RequestBody requestBody(V src) {
        if (src == null) return null;
        try {
          Class<V> classOf = (Class<V>) src.getClass();
          ReqBodyAdapter<V> adapter = reqBodyAdapter(classOf);
          return adapter.parse(src);
        } catch(Exception e) {
          throw new RuntimeException(e.getMessage(), e);
        }
    }

    public <V> FormBody formBody(V src) {
        if (src == null) return null;
        return new FormBody(map(src));
    }
    
    public <V> MultipartBody multipartBody(V src) {
        if (src == null) return null;
        return new MultipartBody(map(src));
    }

    public <V> Map<String, Object> map(V src) {
        if (src == null) return null;
        
        if (src instanceof Map) {
            Map map = (Map) src;
            Iterator itkeys = map.keySet().iterator();
            Map<String, Object> r = new HashMap<String, Object>(map.size());
            while (itkeys.hasNext()) {
                Object key = itkeys.next();
                r.put(String.valueOf(key), map.get(key));
            }
            return r;
        }
        
        return toMap(src);
     }
    
    public <V> Map<String, Object> toMap(V src) {
        if (src == null) return null;
        try {
            Class<V> classOf = (Class<V>) src.getClass();
            ObjectAdapter<V> adapter = ObjectAdapter.get(classOf);
            return adapter.toMap(src);
          } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
