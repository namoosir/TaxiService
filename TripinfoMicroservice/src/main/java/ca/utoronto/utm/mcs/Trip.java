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
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class Trip implements HttpHandler {

    MongoClient client;
    MongoDatabase database;
    MongoCollection<Document> trip;
    HttpClient httpClient;

    public Trip() throws ClassNotFoundException {
        client = Utils.client;
        database = Utils.database;
        trip = Utils.trip;
        httpClient = Utils.httpClient;
     }

    @Override
    public void handle(HttpExchange r) throws IOException {
       try {
          if (r.getRequestMethod().equals("PATCH")) {
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

        String[] uri = r.getRequestURI().toString().split("/");
        
        if (uri.length != 3) {
            Utils.error(statusCode, res, r, "BAD REQUEST");
            return;
        }

        uid = uri[2];
        
        if (uid.isEmpty()) {
            Utils.error(statusCode, res, r, "BAD REQUEST");
            return;
        }

        if(!req.has("distance") || !req.has("endTime") || !req.has("timeElapsed") || !req.has("discount") || !req.has("totalCost") || !req.has("driverPayout")) {
            Utils.error(statusCode, res, r, "BAD REQUEST");
            return;
        }

        int distance;
        Double totalCost;
        Double discount;
        int endTime;
        String timeElapsed;    
        Double driverPayout;    

        try {
            distance = Integer.parseInt(req.getString("distance"));
            totalCost = Double.parseDouble(req.getString("totalCost"));
            discount = Double.parseDouble(req.getString("discount"));
            endTime = Integer.parseInt(req.getString("endTime"));
            timeElapsed = req.getString("timeElapsed");
            driverPayout = Double.parseDouble(req.getString("driverPayout"));
        } catch (Exception e) {
            Utils.error(statusCode, res, r, "BAD REQUEST");
            return;
        }

        try {
            BasicDBObject query = new BasicDBObject();
            System.out.println("\n\n\n" + uid);
            ObjectId id = new ObjectId(uid);
            query.put("_id", id);
    
            FindIterable<Document> docs = trip.find(query); 
            
            if (docs.first() == null) {
                Utils.error(404, res, r, "NO TRIPS FOUND");
                return;
            }

            for (Document doc : docs) {
                doc.put("distance", distance);
                doc.put("totalCost", totalCost);
                doc.put("discount", discount);
                doc.put("endTime", endTime);
                doc.put("timeElapsed", timeElapsed);
                doc.put("driverPayout", driverPayout);
            }

            res.put("status", "OK");

            String myResponse = res.toString();
            r.sendResponseHeaders(200, myResponse.length());
            OutputStream os = r.getResponseBody();
            os.write(myResponse.getBytes());
            os.close();  
            
        } catch (Exception e) {
            e.printStackTrace();
            Utils.error(500, res, r, "INTERNAL SERVER ERROR");
        }
    }
}