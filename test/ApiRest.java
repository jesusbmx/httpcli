import httpcli.FormBody;
import httpcli.HttpRequest;
import httpcli.HttpCli;
import httpcli.HttpUrl;
import httpcli.MultipartBody;
import httpcli.ResponseBody;
import httpcli.adapter.RespBodyFile;
import java.io.File;

public class ApiRest {
    
    HttpCli cli = HttpCli.get()
            .setDebug(true)
    ;
    
//    String run() throws Exception {
//        HttpRequest request = new HttpRequest(
//            "GET", "https://api.github.com/users/defunkt");
//
//        try (ResponseBody body = cli.execute(request)) {
//            //body.code [200];
//            //body.headers [
//            //   Keep-Alive: timeout=5, max=98
//            //    Server: Apache/2.4.25 (Win32) OpenSSL/1.0.2j PHP/5.6.30
//            //    Connection: Keep-Alive
//            //    Content-Length: 2245
//            //    Date: Sun, 13 Mar 2022 15:01:53 GMT
//            //    Content-Type: text/html; charset=UTF-8
//            //    X-Powered-By: PHP/5.6.30
//            //];
//            //body.in [InputStream];
//            return body.string();
//        }
//    }
    
    String get() throws Exception {
        HttpUrl url = new HttpUrl("https://api.github.com/users/defunkt");
        
        HttpRequest request = new HttpRequest("GET", url);

        return cli.execute(request, String.class);
    }
    
    String post(int id, String name, boolean active) throws Exception {
        FormBody reqBody = new FormBody()
                .add("id", id)
                .add("name", name)
                .add("active", active);
        
        HttpRequest request = new HttpRequest(
            "POST", "http://127.0.0.1/test.php", reqBody);

        return cli.execute(request, String.class);
    }
    
    String delete(int id) throws Exception {
        FormBody body = new FormBody()
            .add("id", id);

        HttpRequest request = new HttpRequest(
            "DELETE", "http://127.0.0.1/test.php", body);

        return cli.execute(request, String.class);
    }
    
    File download() throws Exception {
        HttpRequest request = new HttpRequest(
            "GET", "https://github.com/HttpCli/Restlight/raw/master/dist/httpcli.jar")
            .setTimeoutMs(5000 * 2 * 2);
        
        RespBodyFile adapter = new RespBodyFile(
            "C:\\Users\\Jesus\\Downloads\\httpcli.jar");
        
        return cli.execute(request, adapter);
        //return cli.execute(request, File.class);
    }
    
    String upload() throws Exception { 
        MultipartBody body = new MultipartBody()
          .addParam("nombre", "Elizabéth Magaña")
          .addFile("img", new File("C:\\Users\\jesus\\Pictures\\420089-Kycb_1600x1200.jpg"));
    
        HttpRequest request = new HttpRequest(
          "POST", "http://127.0.0.1/test.php", body);

        return cli.execute(request, String.class);
   }
    
    public static void main(String[] args) {
        ApiRest api = new ApiRest();
     
        try {
            
            String get = api.get();
            System.out.println(get);
            
            String post = api.post(1, "My name", true);
            System.out.println(post);
            
            String delete = api.delete(1);
            System.out.println(delete);
            
            File down = api.download();
            System.out.println(down);
            
            String up = api.upload();
            System.out.println(up);
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
