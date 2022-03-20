package httpcli.adapter;

import httpcli.BytesResponseBody;
import httpcli.ResponseBody;

public class RespBodyRB implements RespBodyAdapter<ResponseBody> {

    @Override
    public ResponseBody parse(ResponseBody respBody) throws Exception {
      try {
        byte[] data = respBody.bytes();
        return new BytesResponseBody(respBody, data);
      } finally {
        respBody.close();
      }
    }
    
}
