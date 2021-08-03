package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
   public static void initialSetup() throws IOException, InterruptedException {
       
       String url = "http://UserMicroservice:8004/user/register";

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


       url = "http://locationmicroservice:8004/location/user";

       data = new HashMap<>();
       data.put("uid", "1");
       data.put("is_driver", "b@com");
       data.put("password", "bsecret");

       request = HttpRequest.newBuilder().PUT(buildFormDataFromMap(data)).uri(URI.create(url)).build();

       httpClient.send(request, HttpResponse.BodyHandlers.ofString());


      
   }

   @Test
   public void nearbyDriverBadRequestTest() {
      int uid = 1;
      
      String url = "http://locationmicroservice:8000/location/nearbyDriver/" + uid;

      HttpRequest request = HttpRequest.newBuilder()
      .GET()
      .uri(URI.create(url))
      .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

          JSONObject jsonObject = new JSONObject(response.body());

          if(response.statusCode()!=200){
              String errorResponse = jsonObject.getString("status");
              res.put("data", new ArrayList<String>());
              Utils.error(response.statusCode(), res, r, errorResponse);
              return;
          }

          Iterator<?> keys = jsonObject.keys();

          while(keys.hasNext()) {
              String key = (String)keys.next();
              if (!key.equals("status")){
                  finalBody.add(key);
              }
          }
      
      
   }

   @Test
   public void nearbyDriverOneInRadius() {
      
   }

   @Test
   public void navigationBadRequestTest() {
      
   }

   @Test
   public void navigation200Test() {
      assertTrue(true);
   }
}
