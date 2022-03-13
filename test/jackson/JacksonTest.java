package jackson;

import httpcli.HttpCall;
import httpcli.HttpCallback;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class JacksonTest {

  PostDao dao = new PostDao();  
    
  public void list() {
    HttpCall<Post[]> call = dao.getPosts(); 
    
    call.execute(new HttpCallback<Post[]>() {
      @Override
      public void onResponse(Post[] result) throws Exception {
        List<Post> list = Arrays.asList(result);
        for (Post post : list) {
          System.out.println(post.title);
        }
      }
      @Override
      public void onFailure(Exception e) {
        e.printStackTrace(System.out);
      }
    });
  }  
  
  public void insert() {
    Post post = new Post();
    post.id = 7;
    post.dateCreated = new Date();
    post.title = "My Title";
    post.author = "My Author";
    post.url = "http://127.0.0.1";
    post.body = "My body";
    
    HttpCall<String> call = dao.insert(post); 
    
    call.execute(new HttpCallback<String>() {
      @Override
      public void onResponse(String result) throws Exception {
          System.out.println(result);
      }
      @Override
      public void onFailure(Exception e) {
        e.printStackTrace(System.out);
      }
    });
  }
    
  public static void main(String[] args) {
    JacksonTest gsonTest = new JacksonTest();
    gsonTest.list();
    //gsonTest.insert();
  }
}
