package httpcli.adapter;

import httpcli.ResponseBody;

public class RespBody implements RespBodyAdapter<ResponseBody> {

    @Override
    public ResponseBody parse(ResponseBody respBody) throws Exception {
      return respBody;
    }
    
}
