package httpcli;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SimpleResponseBody extends ResponseBody {
    
    public final byte[] data;

    public SimpleResponseBody(ResponseBody body, byte[] data) {
        super(new ByteArrayInputStream(data));
        this.data = data;
        this.request = body.request;
        this.code = body.code;
        this.headers = body.headers;
        this.contentLength = body.contentLength;
        this.contentEncoding = body.contentEncoding;
        this.contentType = body.contentType;
    }

    @Override public byte[] bytes() throws IOException {
        return data;
    }
}
