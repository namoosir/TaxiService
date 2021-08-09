package ca.utoronto.utm.mcs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


import org.bson.Document;


import java.io.OutputStream;
import java.net.http.HttpClient;
import java.time.Duration;

import com.sun.net.httpserver.HttpExchange;
import org.json.*;



public class Utils {

   public static MongoClient client = MongoClients.create("mongodb://root:123456@mongodb:27017");
   
   //public static MongoClient client = MongoClients.create("mongodb://localhost:27017");
   public static MongoDatabase database = client.getDatabase("trip");
   public static MongoCollection<Document> trip = database.getCollection("trip2");

   public static  HttpClient httpClient = HttpClient.newBuilder()
   .version(HttpClient.Version.HTTP_2)
   .connectTimeout(Duration.ofSeconds(10))
   .build();

   public static String convert(InputStream inputStream) throws IOException {
      try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
         return br.lines().collect(Collectors.joining(System.lineSeparator()));
      }
   }

   public static boolean isNumeric(String str) {
      try {
         Double.parseDouble(str);
         return true;
      } catch (NumberFormatException e) {
         return false;
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
