## HttpCli

HttpCli es una librería **HTTP** para Android y Java, que facilita la creación de peticiones **HTTP** como: GET, POST, HEAD, OPTIONS, PUT, DELETE y TRACE; hacia servidores externos. [Descargar .jar](https://github.com/JesusBetaX/HttpCli/raw/master/dist/httpcli.jar) o [Ver demo](https://github.com/JesusBetaX/WebServiceDemo) 

## Ejemplos

### INIT
```java
HttpCli cli = HttpCli.get()
    .setDebug(true);
```

### Simple Request
```java
String run() throws Exception {
  HttpRequest request = new HttpRequest(
        "GET", "https://api.github.com/users/defunkt");

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

### GET
```java
String get() throws Exception {
  HttpUrl url = new HttpUrl("https://api.github.com/users/defunkt");
  
  HttpRequest request = new HttpRequest("GET", url);

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

  HttpRequest request = new HttpRequest(
      "DELETE", "http://127.0.0.1/test.php", body);

  return cli.execute(request, String.class);
}
```

### DOWLOAD
```java
File download() throws Exception {
  HttpRequest request = new HttpRequest(
      "GET", "https://github.com/JesusBetaX/Restlight/raw/master/dist/restlight.jar")
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

License
=======

    Copyright 2018 JesusBetaX, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
