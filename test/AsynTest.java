
import httpcli.FormBody;
import httpcli.HttpCall;
import httpcli.HttpCallback;
import httpcli.HttpCli;
import httpcli.HttpRequest;
import httpcli.ResponseBody;

public class AsynTest {
    
  HttpCli cli = HttpCli.get()
            .setDebug(true)
  ;
    
  public HttpCall<ResponseBody> insert(
    String nombre, int edad, boolean soltera) {
      
    FormBody body = new FormBody()
        .add("nombre", nombre)
        .add("edad", edad)
        .add("soltera", soltera);
    
    HttpRequest request = new HttpRequest(
        "POST", "http://127.0.0.1/test.php", body);

    return cli.newCall(request, ResponseBody.class);
  }
  
   public void async() {
    HttpCall<ResponseBody> insert = insert(
            "Elizabéth Magaña", 22, true);
    
    insert.execute(new HttpCallback<ResponseBody>() {

      @Override
      public void onResponse(ResponseBody result) throws Exception {
        String str = result.string();
        System.out.println(str);
      }
        
      @Override
      public void onFailure(Exception e) {
        e.printStackTrace();
      }
    });
  }
  
//  public void async() {
//    HttpCall<ResponseBody> insert = insert(
//            "Elizabéth Magaña", 22, true);
//    
//    insert.execute(new HttpCallback<ResponseBody>() {
//
//      @Override
//      public void onResponse(ResponseBody result) throws Exception {
//        String str = result.string();
//        System.out.println(str);
//      }
//        
//      @Override
//      public void onFailure(Exception e) {
//        e.printStackTrace();
//      }
//    });
//  }
  
//  public void sync() {
//    HttpCall<ResponseBody> insert = insert(
//            "Elizabéth Magaña", 22, true);
//    
//    try (ResponseBody body = insert.execute()) {
//      System.out.println(body.string());
//    
//    } catch(Exception e) {
//      e.printStackTrace();
//    }
//  }
  
  public static void main(String[] args) {
    AsynTest test = new AsynTest();
     
    test.async();
  }
}
