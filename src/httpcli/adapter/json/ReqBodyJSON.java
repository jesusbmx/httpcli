package httpcli.adapter.json;

import httpcli.RequestBody;
import httpcli.adapter.ObjectAdapter;
import httpcli.adapter.ReqBodyAdapter;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class ReqBodyJSON implements ReqBodyAdapter<JSON> {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Override
    public RequestBody parse(JSON obj) throws Exception {
      String json = obj.toString();
      return RequestBody.create(MEDIA_TYPE, json);
    }
    
    public static class Obj implements ReqBodyAdapter<JSONObject> {

        @Override
        public RequestBody parse(JSONObject obj) throws Exception {
            String json = obj.toString();
            return RequestBody.create(MEDIA_TYPE, json);
        }
    }

    public static class Array implements ReqBodyAdapter<JSONArray> {

        @Override
        public RequestBody parse(JSONArray obj) throws Exception {
            String json = obj.toString();
            return RequestBody.create(MEDIA_TYPE, json);
        }
    }
    
    public static class Any<T> implements ReqBodyAdapter<T> {
        public final Class<T> classOf;

        public Any(Class<T> classOf) {
            this.classOf = classOf;
        }
        
        @Override
        public RequestBody parse(T obj) throws Exception {
            ObjectAdapter<T> adapter = ObjectAdapter.get(classOf);
            
            Map<String, Object> vars = adapter.toMap(obj);
            JSONObject json = new JSONObject();
            for (Map.Entry<String, Object> entry : vars.entrySet()) {
                json.putOpt(entry.getKey(), entry.getValue());
            }
            
            String content = json.toString();
            return RequestBody.create(MEDIA_TYPE, content);
        }
    }
}
