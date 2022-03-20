package httpcli.adapter;

import httpcli.ResponseBody;

public class RespBodyResult implements RespBodyAdapter<HttpResult> {

    @Override
    public HttpResult parse(ResponseBody respBody) throws Exception {
      try {
        byte[] data = respBody.bytes();
        return new HttpResult(respBody, data);
      } finally {
        respBody.close();
      }
    }
    
}
