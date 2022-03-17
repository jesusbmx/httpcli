## HttpCli

HttpCli es una librería **HTTP** para Android y Java, que facilita la creación de peticiones **HTTP** como: GET, POST, HEAD, OPTIONS, PUT, DELETE y TRACE; hacia servidores externos. [Descargar .jar](https://github.com/JesusBetaX/HttpCli/raw/master/dist/httpcli.jar) o [Ver demo](https://github.com/JesusBetaX/WebServiceDemo) 

## Ejemplos

### init
```java
HttpCli cli = HttpCli.get()
    .setDebug(true);
```

### GET
```java
String get() throws Exception {
  HttpRequest request = new HttpRequest(
       "GET", "https://api.github.com/users/defunkt");

  return cli.execute(request, String.class);
}
```

### POST
```java
String post(int id, String name, boolean active) throws Exception {
  FormBody reqBody = new FormBody()
          .add("id", id)
          .add("name", name)
          .add("active", active);
  
  HttpRequest request = new HttpRequest(
      "POST", "http://127.0.0.1/test.php", reqBody);

  return cli.execute(request, String.class);
}
```

### DELETE
```java
String delete(int id) throws Exception {
  FormBody body = new FormBody()
      .add("id", id);

  // http://127.0.0.1/test.php?id=<id>
  HttpRequest request = new HttpRequest(
      "DELETE", "http://127.0.0.1/test.php", body);

  return cli.execute(request, String.class);
}
```

### DOWLOAD
```java
File download() throws Exception {
  HttpRequest request = new HttpRequest(
      "GET", "https://github.com/JesusBetaX/Restlight/raw/master/dist/httpcli.jar")
      .setTimeoutMs(20000);
  
  RespBodyFile adapter = new RespBodyFile(
      "C:\\Users\\Jesus\\Downloads\\httpcli.jar");
  
  return cli.execute(request, adapter);
  //return cli.execute(request, File.class);
}
```

### UPLOAD
```java
String upload() throws Exception { 
  MultipartBody body = new MultipartBody()
    .addParam("nombre", "Elizabéth Magaña")
    .addFile("img", new File("C:\\Users\\jesus\\Pictures\\420089-Kycb_1600x1200.jpg"));

  HttpRequest request = new HttpRequest(
    "POST", "http://127.0.0.1/test.php", body);

  return cli.execute(request, String.class);
}
```

### Simple Request
```java
String run() throws Exception {
  HttpUrl url = new HttpUrl("https://api.github.com/users/defunkt");

  HttpRequest request = new HttpRequest("GET", url);

  try (ResponseBody body = cli.execute(request)) {
    //body.code {200}
    //body.headers {
    //    Keep-Alive: timeout=5, max=98
    //    Server: Apache/2.4.25 (Win32) OpenSSL/1.0.2j PHP/5.6.30
    //    Connection: Keep-Alive
    //}
    //body.in {InputStream}
    //body.bytes() {byte[]}  => body.close()
    return body.string(); // => body.close()
  }
}
```


## [Llamadas asincrónicas y sincrónicas] 

Preparamos la solicitud

```java
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
```

### Asíncrono

Envía de manera asíncrona la petición y notifica a tu aplicación con un callback cuando una respuesta regresa. Ya que esta petición es asíncrona, Restligth maneja la ejecución en el hilo de fondo para que el hilo de la 
UI principal no sea bloqueada o interfiera con esta.

```java
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
```

### Síncrono

Envíe sincrónicamente la solicitud y devuelva su respuesta.

```java
HttpCall<ResponseBody> insert = insert(
            "Elizabéth Magaña", 22, true);
    
try (ResponseBody body = insert.execute()) {
    System.out.println(body.string());
    
} catch(Exception e) {
    e.printStackTrace();
}
```

## [GSON](https://github.com/google/gson) 

En tu **build.gradle** necesitarás agregar las dependencias para **GSON**:

```groovy
dependencies {
  ...
  compile 'com.google.code.gson:gson:2.4'
}
```


Ahora estamos listos para comenzar a escribir un código. Lo primero que querremos hacer es definir nuestro modelo **Post**
Cree un nuevo archivo llamado **Post.java** y defina la clase **Post** de esta manera:

```java
public class Post {
  
  @SerializedName("id")
  public long ID;
    
  @SerializedName("date")
  public Date dateCreated;
 
  public String title;
  public String author;
  public String url;
  public String body;
}
```


Creemos una nueva instancia de **GSON** antes de llamar a la request. También necesitaremos establecer un formato de fecha personalizado en la instancia **GSON** para manejar las fechas que devuelve la API:

Definimos las interacciones de la base de datos. Pueden incluir una variedad de métodos de consulta.:

```java
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
    // RequestBody reqBody = cli.formBody(p);
    // RequestBody reqBody = cli.multipartBody(p);
    
    HttpRequest request = new HttpRequest(
            "POST", "http://127.0.0.1/test.php", reqBody);
    
    return cli.newCall(request, String.class);
  }
}
```

Programa la solicitud para ser ejecutada en segundo plano. Ideal para aplicaciones android. 
Envía de manera asíncrona la petición y notifica a tu aplicación con un callback cuando una respuesta regresa.
```java
...
PostDao dao = new PostDao();
    
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
```

## [Jackson](https://github.com/FasterXML/jackson) 

Ahora estamos listos para comenzar a escribir un código. Lo primero que querremos hacer es definir nuestro modelo **Post**
Cree un nuevo archivo llamado **Post.java** y defina la clase **Post** de esta manera:

```java
public class Post {
    
  @JsonProperty("id")
  public long id;

  @JsonProperty("date")
  public Date dateCreated;

  @JsonProperty("title")
  public String title;
  
  @JsonProperty("author")
  public String author;
  
  @JsonProperty("url")
  public String url;
  
  @JsonProperty("body")
  public String body;
}
```


Creemos una nueva instancia de **ObjectMapper** antes de llamar a la request. También necesitaremos establecer un formato de fecha personalizado en la instancia **ObjectMapper** para manejar las fechas que devuelve la API:

Definimos las interacciones de la base de datos. Pueden incluir una variedad de métodos de consulta.:

```java
public class PostDao {
  
  HttpCli cli = HttpCli.get()
          .setDebug(true);  
    
  public PostDao() {
    ObjectMapper mapper = new ObjectMapper()
            .setDateFormat(new SimpleDateFormat("M/d/yy hh:mm a"))
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
    cli.setFactory(new JacksonFactoryAdapter(mapper));
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
}
```

Programa la solicitud para ser ejecutada en segundo plano. Ideal para aplicaciones android. 
Envía de manera asíncrona la petición y notifica a tu aplicación con un callback cuando una respuesta regresa.
```java
...
PostDao dao = new PostDao();
    
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
```

## [JSON](https://github.com/stleary/JSON-java)
(https://www.json.org/json-en.html)

Para android no es necesario descar [org.json jar](https://github.com/stleary/JSON-java)
Para otras plataformas de java como java swing si es necesario.

```java
public HttpCall<JSONObject> insert(
    String nombre, int edad, boolean soltera) {
      
    FormBody reqBody = new FormBody()
        .add("nombre", nombre)
        .add("edad", edad)
        .add("soltera", soltera);

    HttpRequest request = new HttpRequest(
        "POST", "http://127.0.0.1/test.php", reqBody);
        
    return cli.newCall(request, JSONObject.class);
}
```

```java
HttpCall<JSONObject> insert = insert(
    "Elizabéth Magaña", 22, true);
        
try {
    JSONObject json = insert.execute();
    System.out.println(json.toString(1));
            
} catch (Exception e) {
    e.printStackTrace();
}
```


License
=======

    Copyright 2022 JesusBetaX, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
