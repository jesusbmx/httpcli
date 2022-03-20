
import httpcli.HttpCall;
import httpcli.HttpCallback;
import httpcli.HttpCli;
import httpcli.HttpRequest;
import httpcli.ResponseBody;

public class SimpleAsync {

    public static void main(String[] args) throws Exception {
        HttpCli cli = HttpCli.get()
                .setDebug(true);
        
        HttpRequest request = new HttpRequest(
                "GET", "http://127.0.0.1/test.php");
        
        HttpCall<ResponseBody> call = cli.newCall(request);
        
        call.execute(new HttpCallback<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody result) throws Exception {
              String str = result.as(String.class);
              System.out.println(str);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
