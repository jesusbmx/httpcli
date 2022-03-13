package httpcli.adapter;

import httpcli.HttpRequest;
import httpcli.ResponseBody;

public class RespBodyString implements RespBodyAdapter<String> {

    @Override public String parse(ResponseBody respBody) throws Exception {
       HttpRequest request = respBody.request;
       return respBody.string(request.getCharset());
    }
}
