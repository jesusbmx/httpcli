
package form;

import httpcli.HttpCli;
import httpcli.RequestBody;
import java.nio.charset.Charset;
import java.util.Date;

public class FormBodyTest {
    
    HttpCli cli = HttpCli.get();
    
    public void run() throws Exception {
        Post post = new Post();
        post.id = 7;
        post.dateCreated = new Date();
        post.title = "My Title";
        post.author = "My Author";
        post.url = "http://127.0.0.1";
        post.body = "My body";
        
        RequestBody requestBody = cli.multipartBody(post);
        requestBody.writeTo(System.out, Charset.forName("UTF-8"));
    }
    
    public static void main(String[] args) throws Exception {
        FormBodyTest formBodyTest = new FormBodyTest();
        
        formBodyTest.run();
    }
}
