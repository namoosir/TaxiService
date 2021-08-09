package ca.utoronto.utm.mcs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;

import org.json.JSONException;
import org.json.JSONObject;

public class Utils {

   public static HttpClient httpClient = HttpClient.newBuilder()
   .version(HttpClient.Version.HTTP_2)
   .connectTimeout(Duration.ofSeconds(10))
   .build();

   public static String convert(InputStream inputStream) throws IOException {
      try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
         return br.lines().collect(Collectors.joining(System.lineSeparator()));
      }
   }

   public static void error(int statusCode, JSONObject res, HttpExchange r, String s) throws IOException, JSONException {
      res.put("status", s);
      String response = res.toString();
      r.sendResponseHeaders(statusCode, response.length());
      OutputStream os = r.getResponseBody();
      os.write(response.getBytes());
      os.close();
   }
}
