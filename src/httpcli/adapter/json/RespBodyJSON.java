package httpcli.adapter.json;

import org.json.JSONArray;
import org.json.JSONObject;
import httpcli.ResponseBody;
import httpcli.adapter.RespBodyAdapter;
import httpcli.adapter.json.JSON;

public class RespBodyJSON implements RespBodyAdapter<JSON>{

    @Override
    public JSON parse(ResponseBody respBody) throws Exception {
        try {
            String json = respBody.string();
            return JSON.of(json);
        } finally {
            respBody.close();
        }
    }    
    
    public static class Object implements RespBodyAdapter<JSONObject>{

        @Override
        public JSONObject parse(ResponseBody respBody) throws Exception {
            try {
                String json = respBody.string();
                //System.out.println(json);
                return new JSONObject(json);
            } finally {
                respBody.close();
            }
        }    
    }

    public static class Array implements RespBodyAdapter<JSONArray>{

        @Override
        public JSONArray parse(ResponseBody respBody) throws Exception {
            try {
                String json = respBody.string();
                return new JSONArray(json);
            } finally {
                respBody.close();
            }
        }    
    }

}
