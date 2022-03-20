
import httpcli.HttpCall;
import httpcli.HttpCallback;
import httpcli.HttpCli;
import httpcli.HttpRequest;
import httpcli.adapter.HttpResult;

public class SimpleAsync {

    public static void main(String[] args) throws Exception {
        HttpCli cli = HttpCli.get()
                .setDebug(true);
        
        HttpRequest request = new HttpRequest(
                "GET", "http://127.0.0.1/test.php");
        
        HttpCall<HttpResult> call = cli.newCall(request);
        
        call.execute(new HttpCallback<HttpResult>() {
            @Override
            public void onResponse(HttpResult result) throws Exception {
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
