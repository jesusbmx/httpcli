package httpcli.adapter;

import httpcli.ResponseBody;
import java.io.ByteArrayInputStream;

public class HttpResult extends ResponseBody {
    
    public final byte[] data;

    public HttpResult(ResponseBody body, byte[] data) {
        super(new ByteArrayInputStream(data));
        this.data = data;
        this.request = body.request;
        this.code = body.code;
        this.headers = body.headers;
        this.contentLength = body.contentLength;
        this.contentEncoding = body.contentEncoding;
        this.contentType = body.contentType;
    }
}
