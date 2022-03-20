package httpcli.adapter;

import httpcli.SimpleResponseBody;
import httpcli.ResponseBody;

public class RespBodyRB implements RespBodyAdapter<ResponseBody> {

    @Override
    public ResponseBody parse(ResponseBody respBody) throws Exception {
      try {
        byte[] data = respBody.bytes();
        return new SimpleResponseBody(respBody, data);
      } finally {
        respBody.close();
      }
    }
    
}
