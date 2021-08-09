package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.*;
import java.util.*;

import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class Request implements HttpHandler {

    MongoClient client;
    MongoDatabase database;
    MongoCollection<Document> trip;
    HttpClient httpClient;

    public Request() throws ClassNotFoundException {
        client = Utils.client;
        database = Utils.database;
        trip = Utils.trip;
        httpClient = Utils.httpClient;
     }

    @Override
    public void handle(HttpExchange r) throws IOException {
       try {
          if (r.getRequestMethod().equals("POST")) {
             handleTripReq(r);
          }
       } catch (Exception e) {
          System.out.println("Error Occurred! Msg:   " + e);
       }
    }
    
    private void handleTripReq(HttpExchange r) throws IOException, JSONException {
        String body = Utils.convert(r.getRequestBody());
        JSONObject res = new JSONObject();
        JSONObject req = new JSONObject(body);
        int statusCode = 400;
        String uid;
        int radius;
        ArrayList<String> finalBody = new ArrayList<String>();

        if(!req.has("uid") || !req.has("radius")){
            res.put("data", new ArrayList<String>());
            Utils.error(statusCode, res, r, "BAD REQUEST");
            return;
        }

        try {
            uid = req.getString("uid");
            radius = Integer.parseInt(req.getString("radius"));

        } catch (Exception e) {
            res.put("data", new ArrayList<String>());
            Utils.error(statusCode, res, r, "BAD REQUEST");
            return;
        }
        
   
        String url = "http://locationmicroservice:8000/location/nearbyDriver/" + uid + "?radius=" + radius;

        try {

            HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(url))
/*                 .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
            .header("Content-Type", "application/json") */
            .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonObject = new JSONObject(response.body());

            if(response.statusCode()!=200){
                String errorResponse = jsonObject.getString("status");
                res.put("data", new ArrayList<String>());
                Utils.error(response.statusCode(), res, r, errorResponse);
                return;
            }   
            JSONObject data = jsonObject.getJSONObject("data");
            Iterator<?> keys = data.keys();

            while(keys.hasNext()) {
                String key = (String)keys.next();
                finalBody.add(key);
            }

            res.put("data", finalBody);
            res.put("status", "OK");
            String myResponse = res.toString();
            r.sendResponseHeaders(200, myResponse.length());
            OutputStream os = r.getResponseBody();
            os.write(myResponse.getBytes());
            os.close();  
            
        } catch (Exception e) {
            res.put("data", new ArrayList<String>());
            Utils.error(500, res, r, "INTERNAL SERVER ERROR");
        }
    }
}