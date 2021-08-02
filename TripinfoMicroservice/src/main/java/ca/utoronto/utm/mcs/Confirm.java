package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.sql.*;
import java.util.Iterator;
import org.bson.Document;

import java.util.Arrays;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Confirm implements HttpHandler {

   MongoClient client;
   MongoDatabase database;
   MongoCollection<Document> trip;
   HttpClient httpClient;

   public Confirm() throws ClassNotFoundException {
      client = Utils.client;
      database = Utils.database;
      trip = Utils.trip;
      httpClient = Utils.httpClient;
   }

   @Override
   public void handle(HttpExchange r) throws IOException {
      try {
         if (r.getRequestMethod().equals("POST")) {
            addTrip(r);
         }
      } catch (Exception e) {
         System.out.println("Error Occurred! Msg:   " + e);
      }
   }

   private void addTrip(HttpExchange r) throws IOException, JSONException {
      String body = Utils.convert(r.getRequestBody());
      JSONObject res = new JSONObject();
      JSONObject req = new JSONObject(body);
      int statusCode = 400;
      String uidDriver;
      String uidPassenger;
      int startTime;

      if (!req.has("driver") || !req.has("passenger") || !req.has("startTime")){
         Utils.error(statusCode, res, r, "BAD REQUEST");
         return;
      }


      try {
         uidDriver = req.getJSONObject("driver").getString("uid");
         uidPassenger = req.getJSONObject("passenger").getString("uid");
         startTime = Integer.parseInt(req.getString("startTime"));
         if(String.valueOf(startTime).length()!=10) throw new Exception();
      } catch (Exception e) {
         Utils.error(statusCode, res, r, "BAD REQUEST");
         return;
      }

      try {
         Document newdoc = new Document()
         .append("driver", uidDriver)
         .append("passenger", uidPassenger)
         .append("startTime", startTime)
         .append("distance", -1)
         .append("totalCost", -1)
         .append("endTime", -1)
         .append("timeElapsed", -1)
         .append("driverPayout", -1)
         .append("discount", -1);
   
         System.out.println(newdoc.toJson());
   
         trip.insertOne(newdoc);
         
         String myResponse = newdoc.get("_id").toString();
         res.put("data", myResponse);
         res.put("status", "OK");
         String response = res.toString();
         r.sendResponseHeaders(200, response.length());
         OutputStream os = r.getResponseBody();
         os.write(response.getBytes());
         os.close();  
         
      } catch (Exception e) {
         Utils.error(statusCode, res, r, "INTERNAL SERVER ERROR");
      }


   }
}