package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
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

    public Confirm() throws ClassNotFoundException {
        client = Utils.client;
        database = Utils.database;
        trip = Utils.trip;
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

        //if (req.has("driver") && req.has("passenger") && req.has("startTime"))
    }
}