package httpcli.adapter;

import httpcli.ResponseBody;

public class RespBodyString implements RespBodyAdapter<String> {

    @Override public String parse(ResponseBody respBody) throws Exception {
        try {
            return respBody.string();
        } finally {
            respBody.close();
        }
    }
}
