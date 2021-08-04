package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.time.Duration;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/*
Please Write Your Tests For CI/CD In This Class. 
You will see these tests pass/fail on github under github actions.
*/
public class AppTest {

   static HttpClient httpClient = HttpClient.newBuilder()
   .version(HttpClient.Version.HTTP_2)
   .connectTimeout(Duration.ofSeconds(10))
   .build();

   private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) throws JSONException{
      JSONObject jsonb = new JSONObject();
      for (Map.Entry<Object, Object> entry : data.entrySet()) {
          jsonb.put(entry.getKey().toString(), entry.getValue());

      }
      return HttpRequest.BodyPublishers.ofString(jsonb.toString());
   }

   @BeforeAll
   public static void initialSetup() throws IOException, InterruptedException, JSONException {
    String url = "http://localhost:8004/user/register";
    Map<Object, Object> data = new HashMap<>();
      data = new HashMap<>();
      data.put("name", "abc");
      data.put("email", "doodoo");
      data.put("password", "doodoowater");

    HttpRequest request = HttpRequest.newBuilder()
    .POST(buildFormDataFromMap(data))
    .uri(URI.create(url))
    .build();

    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

   }

   @Test
   public void registerBadTest() throws IOException, InterruptedException, JSONException {      
      String url = "http://localhost:8004/user/register";
      Map<Object, Object> data = new HashMap<>();
         data = new HashMap<>();
         data.put("name", "bob");
         data.put("email", "asd");
         data.put("passwo", "asd");
      HttpRequest request = HttpRequest.newBuilder()
      .POST(buildFormDataFromMap(data))
      .uri(URI.create(url))
      .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      assertEquals(response.statusCode(), 400);      
   }

   @Test
   public void registergoodTest() throws IOException, InterruptedException,JSONException {      
    String url = "http://localhost:8004/user/register";
    Map<Object, Object> data = new HashMap<>();
      data = new HashMap<>();
      data.put("name", "15");
      data.put("email", "asd");
      data.put("password", "asdd");

    HttpRequest request = HttpRequest.newBuilder()
    .POST(buildFormDataFromMap(data))
    .uri(URI.create(url))
    .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 200); 
      
   }

   @Test
   public void loginBadTest() throws IOException, InterruptedException, JSONException {      
      String url = "http://localhost:8004/user/login";
      Map<Object, Object> data = new HashMap<>();
         data = new HashMap<>();
         data.put("name", "bob");
         data.put("email", "asd");
         data.put("passwo", "asd");
      HttpRequest request = HttpRequest.newBuilder()
      .POST(buildFormDataFromMap(data))
      .uri(URI.create(url))
      .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      assertEquals(response.statusCode(), 400);      
   }

   @Test
   public void logingoodTest() throws IOException, InterruptedException, JSONException {      
    String url = "http://localhost:8004/user/login";
    Map<Object, Object> data = new HashMap<>();
      data = new HashMap<>();
      data.put("email", "doodoo");
      data.put("password", "doodoowater");

    HttpRequest request = HttpRequest.newBuilder()
    .POST(buildFormDataFromMap(data))
    .uri(URI.create(url))
    .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 200); 
      
   }
}
