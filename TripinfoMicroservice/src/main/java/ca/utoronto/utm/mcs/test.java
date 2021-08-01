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


public class test implements HttpHandler {

    MongoClient client;
    MongoDatabase database;
    MongoCollection<Document> trip;

    public test() throws ClassNotFoundException {
        client = Utils.client;
        database = Utils.database;
        trip = Utils.trip;
     }

    @Override
    public void handle(HttpExchange r) throws IOException {
       try {
          if (r.getRequestMethod().equals("PUT")) {
             putRoad(r);
          }
       } catch (Exception e) {
          System.out.println("Error Occurred! Msg:   " + e);
       }
    }
    
    private void putRoad(HttpExchange r) throws IOException, JSONException {
        System.out.println("jere");

        Document newdoc = new Document()
        .append("title", "Ski Bloopers")
        .append("genres", Arrays.asList("Documentary", "Comedy"));

        System.out.println(newdoc.toJson());

        trip.insertOne(newdoc);

        System.out.println(newdoc.get("_id").toString());

        System.out.println("Success! Inserted document id: ");


    }
}