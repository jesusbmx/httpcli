package httpcli.adapter;

import org.json.JSONArray;
import org.json.JSONObject;
import httpcli.ResponseBody;
import httpcli.adapter.json.JSON;

public class RespBodyJSON implements RespBodyAdapter<JSON>{

    @Override
    public JSON parse(ResponseBody respBody) throws Exception {
       String json = respBody.string();
       return JSON.of(json);
    }    
    
    public static class Object implements RespBodyAdapter<JSONObject>{

        @Override
        public JSONObject parse(ResponseBody respBody) throws Exception {
            String json = respBody.string();
            //System.out.println(json);
            return new JSONObject(json);
        }    
    }

    public static class Array implements RespBodyAdapter<JSONArray>{

        @Override
        public JSONArray parse(ResponseBody respBody) throws Exception {
            String json = respBody.string();
            return new JSONArray(json);
        }    
    }

}
