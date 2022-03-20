
import httpcli.HttpCli;

public class SimpleCli {
    
    public static void main(String[] args) throws Exception {
        HttpCli cli = HttpCli.get()
                .setDebug(true);
        
        String result = cli.post(
                "http://127.0.0.1/test.php"
            )
            .execute()
            .string();
        
        System.out.println(result);
    }
}
