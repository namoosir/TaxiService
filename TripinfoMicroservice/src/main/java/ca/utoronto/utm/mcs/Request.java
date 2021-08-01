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
import java.sql.*;
import java.util.*;

import org.bson.Document;

import java.util.Arrays;
import java.util.HashMap;

import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
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

        if(!req.has("uid") || !req.has("radius")) Utils.error(statusCode, res, r, "BAD REQUEST");

        try {
            uid = req.getString("uid");
            radius = Integer.parseInt(req.getString("radius"));

        } catch (Exception e) {
            Utils.error(statusCode, res, r, "BAD REQUEST");
            return;
        }

        String url = "http://localhost:8000/location/nearbyDriver/" + uid + "?radius=" + radius;
        System.out.println("the url is: " + url);

        try {

            HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(url))
/*                 .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
            .header("Content-Type", "application/json") */
            .build();

            System.out.println("abot to send reqesut");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // print status code
            System.out.println(response.statusCode());

            // print response body
            System.out.println(response.body());
            // print status code
            System.out.println(response.statusCode());

            // print response body
            System.out.println(response.body());
            
        } catch (Exception e) {
            System.out.println("error\n");
        }
/*    
        String response = res.toString();
        r.sendResponseHeaders(statusCode, response.length());
        OutputStream os = r.getResponseBody();
        os.write(response.getBytes());
        os.close(); */


    }
}