package ca.utoronto.utm.mcs;
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
import java.time.Duration;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/*
Please Write Your Tests For CI/CD In This Class. 
You will see these tests pass/fail on github under github actions.
*/
public class AppTest {

   static String need_id = "";
   static HttpClient httpClient = HttpClient.newBuilder()
   .version(HttpClient.Version.HTTP_2)
   .connectTimeout(Duration.ofSeconds(10))
   .build();

   private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
      var builder = new StringBuilder();
      for (Map.Entry<Object, Object> entry : data.entrySet()) {
          if (builder.length() > 0) {
              builder.append("&");
          }
          builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
          builder.append("=");
          builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
      }
      System.out.println(builder.toString());
      return HttpRequest.BodyPublishers.ofString(builder.toString());
  }

  @BeforeAll
   public static void initialSetup() throws IOException, InterruptedException, JSONException {
      String url = "http://localhost:8004/trip/confirm";
      
      Map<Object, Object> data2 = new HashMap<>();
        data2 = new HashMap<>();
        data2.put("driver", 2);
        data2.put("passenger", 1);
        data2.put("startTime", 1234650729);
  
      HttpRequest request = HttpRequest.newBuilder()
      .POST(buildFormDataFromMap(data2))
      .uri(URI.create(url))
      .build();
  
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  
      JSONObject jsonObject = new JSONObject(response.body());
      need_id = jsonObject.getString("data");
   }

  

   @Test
   public void confirmBadRequestTest() throws IOException, InterruptedException, JSONException {      
      String url = "http://localhost:8004/trip/confirm";
      Map<Object, Object> data = new HashMap<>();
         data = new HashMap<>();
         data.put("driverasdda", 2);
         data.put("passenger", 1);
         data.put("startTime", 1234650729);
      HttpRequest request = HttpRequest.newBuilder()
      .POST(buildFormDataFromMap(data))
      .uri(URI.create(url))
      .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      assertEquals(response.statusCode(), 400);      
   }

   @Test
   public void confirmCorrectBodyTest() throws IOException, InterruptedException {      
    String url = "http://localhost:8004/trip/confirm";
    Map<Object, Object> data = new HashMap<>();
      data = new HashMap<>();
      data.put("driver", 2);
      data.put("passenger", 1);
      data.put("startTime", 1234650729);

    HttpRequest request = HttpRequest.newBuilder()
    .POST(buildFormDataFromMap(data))
    .uri(URI.create(url))
    .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 200); 
      
   }

   @Test
   public void TripReqtBadTest() throws IOException, InterruptedException {
      String url = "http://localhost:8004/trip/request";
      Map<Object, Object> data = new HashMap<>();
      data = new HashMap<>();
      data.put("uida", 2);
      data.put("startTime", 1234650729);
      
      HttpRequest request = HttpRequest.newBuilder().POST(buildFormDataFromMap(data)).uri(URI.create(url)).build();    
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  
      assertEquals(response.statusCode(), 400);        
   }

   @Test
   public void TripReq200Test() throws IOException, InterruptedException{
      
    String url = "http://localhost:8004/trip/request";
    Map<Object, Object> data = new HashMap<>();
    data = new HashMap<>();
    data.put("uid", 1);
    data.put("radius", 50000);
    
    HttpRequest request = HttpRequest.newBuilder().POST(buildFormDataFromMap(data)).uri(URI.create(url)).build();    
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 200);   
   }

   @Test
   public void tripBadRequestTest() throws IOException, InterruptedException, JSONException {      
      String url = "http://localhost:8004/trip/" + need_id;
      Map<Object, Object> data = new HashMap<>();
         data = new HashMap<>();
         data.put("distance", 2);
         data.put("endTime", 1);
         data.put("timeElapsed", 3);
      HttpRequest request = HttpRequest.newBuilder()
      .method("PATCH", buildFormDataFromMap(data))
      .uri(URI.create(url))
      .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      assertEquals(response.statusCode(), 400);      
   }

   @Test
   public void tripCorrectBodyTest() throws IOException, InterruptedException {      
    String url = "http://localhost:8004/trip/" + need_id;
    Map<Object, Object> data = new HashMap<>();
      data = new HashMap<>();
      data.put("distance", 2);
      data.put("endTime", 1);
      data.put("timeElapsed", "1234650729");
      data.put("discount", 0);
      data.put("totalCost", 50.0);
      data.put("driverCost", 20.0);

    HttpRequest request = HttpRequest.newBuilder()
    .method("PATCH", buildFormDataFromMap(data))
    .uri(URI.create(url))
    .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 200); 
      
   }

   @Test    
   public void PassengerBadTest() throws IOException, InterruptedException {
      String url = "http://localhost:8004/trip/passenger/50000";
      
      HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();    
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  
      assertEquals(response.statusCode(), 404);        
   }

   @Test
   public void Passenger200Test() throws IOException, InterruptedException{
      
    String url = "http://localhost:8004/trip/passenger/1";

    
    HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();    
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 200);   
   }
   
   @Test
   public void DriverBadTest() throws IOException, InterruptedException {
      String url = "http://localhost:8004/trip/driver/50000";
      
      HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();    
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  
      assertEquals(response.statusCode(), 404);        
   }

   @Test
   public void Driver200Test() throws IOException, InterruptedException{
      
    String url = "http://localhost:8004/trip/driver/2";

    
    HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();    
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 200);   
   }

   @Test
   public void DriverTimeBadTest() throws IOException, InterruptedException {
      String url = "http://localhost:8004/trip/driverTime/50000";
      
      HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();    
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  
      assertEquals(response.statusCode(), 404);        
   }

   @Test
   public void DriverTime200Test() throws IOException, InterruptedException{
      
    String url = "http://localhost:8004/trip/driver/" + need_id;

    
    HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();    
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 200);   
   }
}
