package httpcli.adapter;

import org.json.JSONArray;
import org.json.JSONObject;
import httpcli.HttpRequest;
import httpcli.ResponseBody;
import httpcli.json.JSON;

public class RespBodyJSON implements RespBodyAdapter<JSON>{

    @Override
    public JSON parse(ResponseBody respBody) throws Exception {
       HttpRequest request = respBody.request;
       String json = respBody.string(request.getCharset());
       return JSON.of(json);
    }    
    
    public static class Object implements RespBodyAdapter<JSONObject>{

        @Override
        public JSONObject parse(ResponseBody respBody) throws Exception {
            HttpRequest request = respBody.request;
            String json = respBody.string(request.getCharset());
            //System.out.println(json);
            return new JSONObject(json);
        }    
    }

    public static class Array implements RespBodyAdapter<JSONArray>{

        @Override
        public JSONArray parse(ResponseBody respBody) throws Exception {
            HttpRequest request = respBody.request;
            String json = respBody.string(request.getCharset());
            return new JSONArray(json);
        }    
    }

}
