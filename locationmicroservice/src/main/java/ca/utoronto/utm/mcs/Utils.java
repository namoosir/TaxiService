package ca.utoronto.utm.mcs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.*;


import org.neo4j.driver.*;

public class Utils {
   public static String uriDb = "bolt://neo4j:7687";
   public static Driver driver = GraphDatabase.driver(uriDb, AuthTokens.basic("neo4j", "123456")); 

/*    public static String uriDb = "bolt://localhost:7687";
   public static Driver driver = GraphDatabase.driver(uriDb, AuthTokens.basic("neo4j","1234")); */

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
