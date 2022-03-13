package httpcli.adapter;

import httpcli.ResponseBody;

public class RespBodyString implements RespBodyAdapter<String> {

    @Override public String parse(ResponseBody respBody) throws Exception {
       return respBody.string();
    }
}
