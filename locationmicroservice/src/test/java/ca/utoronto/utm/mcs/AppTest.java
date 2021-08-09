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


/*
Please Write Your Tests For CI/CD In This Class. 
You will see these tests pass/fail on github under github actions.
*/
public class AppTest {

    static HttpClient httpClient = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)
    .connectTimeout(Duration.ofSeconds(10))
    .build();

    private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) throws JSONException {
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
       data.put("name", "a");
       data.put("email", "a@com");
       data.put("password", "secret");
       HttpRequest request = HttpRequest.newBuilder().POST(buildFormDataFromMap(data)).uri(URI.create(url)).build();
       httpClient.send(request, HttpResponse.BodyHandlers.ofString());

       data = new HashMap<>();
       data.put("name", "b");
       data.put("email", "b@com");
       data.put("password", "bsecret");
       request = HttpRequest.newBuilder().POST(buildFormDataFromMap(data)).uri(URI.create(url)).build();
       httpClient.send(request, HttpResponse.BodyHandlers.ofString());


       url = "http://localhost:8004/user/1";
       data = new HashMap<>();
       data.put("is_driver", true);
       data.put("rides", 3);
       request = HttpRequest.newBuilder().uri(URI.create(url)).method("PATCH", buildFormDataFromMap(data)).build();
       httpClient.send(request, HttpResponse.BodyHandlers.ofString());

       url = "http://localhost:8004/location/user";
       data = new HashMap<>();
       data.put("uid", "1");
       data.put("is_driver", false);
       request = HttpRequest.newBuilder().PUT(buildFormDataFromMap(data)).uri(URI.create(url)).build();
       httpClient.send(request, HttpResponse.BodyHandlers.ofString());

       url = "http://localhost:8004/location/user";
       data = new HashMap<>();
       data.put("uid", "2");
       data.put("is_driver", true);
       request = HttpRequest.newBuilder().PUT(buildFormDataFromMap(data)).uri(URI.create(url)).build();
       httpClient.send(request, HttpResponse.BodyHandlers.ofString());

       url = "http://localhost:8004/location/1";
       data = new HashMap<>();
       data.put("longitude", 0);
       data.put("latitude", 0);
       data.put("street", "s1");
       request = HttpRequest.newBuilder().uri(URI.create(url)).method("PATCH", buildFormDataFromMap(data)).build();
       httpClient.send(request, HttpResponse.BodyHandlers.ofString());

       url = "http://localhost:8004/location/2";
       data = new HashMap<>();
       data.put("longitude", 1);
       data.put("latitude", 1);
       data.put("street", "s2");
       request = HttpRequest.newBuilder().uri(URI.create(url)).method("PATCH", buildFormDataFromMap(data)).build();
       httpClient.send(request, HttpResponse.BodyHandlers.ofString());

       url = "http://localhost:8004/location/road";
       data = new HashMap<>();
       data.put("roadName", "s1");
       data.put("hasTraffic", true);
       request = HttpRequest.newBuilder().PUT(buildFormDataFromMap(data)).uri(URI.create(url)).build();
       httpClient.send(request, HttpResponse.BodyHandlers.ofString());

       url = "http://localhost:8004/location/hasRoute";
       data = new HashMap<>();
       data.put("roadName1", "s2");
       data.put("roadName2", "s1");
       data.put("hasTraffic", "true");
       data.put("time", 12);
       request = HttpRequest.newBuilder().POST(buildFormDataFromMap(data)).uri(URI.create(url)).build();
       httpClient.send(request, HttpResponse.BodyHandlers.ofString());


       url = "http://localhost:8004/location/confirm";
       data = new HashMap<>();
       data.put("driver", "2");
       data.put("passenger", "1");
       data.put("startTime", 1234650789);
       request = HttpRequest.newBuilder().POST(buildFormDataFromMap(data)).uri(URI.create(url)).build();
       HttpResponse<String> response= httpClient.send(request, HttpResponse.BodyHandlers.ofString());

       JSONObject jsonObject = new JSONObject(response.body());
       String id = jsonObject.getString("data");


       url = "http://localhost:8004/trip/" + id;
       data = new HashMap<>();
       data.put("distance", 10);
       data.put("endTime", 1230456789);
       data.put("timeElapsed", "00:12:00");
       data.put("totalCost", 16.25);
       data.put("driverPayout", 10.20);
       request = HttpRequest.newBuilder().uri(URI.create(url)).method("PATCH", buildFormDataFromMap(data)).build();
       httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      
   }

   @Test
   public void nearbyDriverBadRequestTest() throws IOException, InterruptedException, JSONException {
      int uid = 1;
      
      String url = "http://localhost:8004/location/nearbyDriver/" + uid;

      HttpRequest request = HttpRequest.newBuilder()
      .GET()
      .uri(URI.create(url))
      .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      assertEquals(response.statusCode(), 400);      
   }

   @Test
   public void nearbyDriverOneInRadiusTest() throws IOException, InterruptedException {
    int uid = 1;
      
    String url = "http://localhost:8004/location/nearbyDriver/" + uid + "?radius=" + "50000";

    HttpRequest request = HttpRequest.newBuilder()
    .GET()
    .uri(URI.create(url))
    .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 200); 
      
   }

   @Test
   public void navigationBadRequestTest() throws IOException, InterruptedException {
      int uid = 2;
      
      String url = "http://localhost:8004/location/navigation" + uid;

      HttpRequest request = HttpRequest.newBuilder()
      .GET()
      .uri(URI.create(url))
      .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      assertEquals(response.statusCode(), 400);      
   }

   @Test
   public void navigation200Test() throws IOException, InterruptedException{
    int uid = 2;
      
    String url = "http://localhost:8004/navigation/" + uid + "?passengerUid=" + "1";

    HttpRequest request = HttpRequest.newBuilder()
    .GET()
    .uri(URI.create(url))
    .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 400);   
   }
}
