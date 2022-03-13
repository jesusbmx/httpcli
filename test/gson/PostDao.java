package gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import httpcli.HttpCall;
import httpcli.HttpCli;
import httpcli.HttpRequest;
import httpcli.RequestBody;
import httpcli.adapter.gson.GsonFactoryAdapter;
// import com.squareup.okhttp.OkHttpClient;
// import java.io.IOException;
// import java.net.HttpURLConnection;
// import java.net.URL;
// import restlight.BasicHttpStack;

public class PostDao {
  
  HttpCli cli = HttpCli.get()
          .setDebug(true);  
    
  public PostDao() {
    Gson gson = new GsonBuilder()
            .setDateFormat("M/d/yy hh:mm a")
            .create();
    
    cli.setFactory(new GsonFactoryAdapter(gson));
  }

  public HttpCall<Post[]> getPosts() {
    HttpRequest request = new HttpRequest(
        "GET", "https://kylewbanks.com/rest/posts.json");

    return cli.newCall(request, Post[].class);
  }
  
  public HttpCall<String> insert(Post p) {
    RequestBody reqBody = cli.requestBody(p);
    
    HttpRequest request = new HttpRequest(
            "POST", "http://127.0.0.1/test.php", reqBody);
    
    return cli.newCall(request, String.class);
  }

  //  BasicHttpStack stack = new BasicHttpStack() {  
  //    final OkHttpClient client = new OkHttpClient();
  //    @Override
  //    public HttpURLConnection open(URL src) throws IOException {
  //      return client.open(src);
  //    }
  //  };
}
