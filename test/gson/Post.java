package gson;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Post {
  @SerializedName("id")
  public long id;

  @SerializedName("date")
  public Date dateCreated;

  @SerializedName("title")
  public String title;
  
  @SerializedName("author")
  public String author;
  
  @SerializedName("url")
  public String url;
  
  @SerializedName("body")
  public String body;
}
