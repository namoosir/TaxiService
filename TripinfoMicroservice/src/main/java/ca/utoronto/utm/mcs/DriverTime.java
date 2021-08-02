package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.sql.*;
import java.util.Iterator;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DriverTime implements HttpHandler {

    MongoClient client;
    MongoDatabase database;
    MongoCollection<Document> trip;
    HttpClient httpClient;

    public DriverTime() throws ClassNotFoundException {
        client = Utils.client;
        database = Utils.database;
        trip = Utils.trip;
        httpClient = Utils.httpClient;
    }

    @Override
    public void handle(HttpExchange r) throws IOException {
        try {
            if (r.getRequestMethod().equals("GET")) {
                getTrips(r);
            }
        } catch (Exception e) {
            System.out.println("Error Occurred! Msg:   " + e);
        }
    }

    private void getTrips(HttpExchange r) throws IOException, JSONException {
        int statusCode = 400;
        String requestURI = r.getRequestURI().toString();
        String[] uriSplitter = requestURI.split("/");
        JSONObject res = new JSONObject();
        String driverUid = "";
        String passengerUid = "";
        int total_time;


        if (uriSplitter.length != 4) {
            Utils.error(statusCode, res, r, "BAD REQUEST");
            return;
        }

        String id = uriSplitter[3];

        if (id.isEmpty()) {
            Utils.error(statusCode, res, r, "BAD REQUEST");
            return;
        }


        try {
            BasicDBObject query = new BasicDBObject();
            query.put("_id", id);
    
            FindIterable<Document> docs = trip.find(query);

            if (docs == null) {
                Utils.error(404, res, r, "NO TRIPS FOUND");
                return;
            }

            for(Document doc : docs) {
                driverUid = doc.get("driver").toString();
                passengerUid = doc.get("passenger").toString();
            }

            String url = "http://locationmicroservice:8000/location/navigation/" + driverUid + "passengerUid=" + passengerUid;

            HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(url))
            .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonObject = new JSONObject(response.body());

            if(response.statusCode()!=200){
                String errorResponse = jsonObject.getString("status");
                Utils.error(response.statusCode(), res, r, errorResponse);
                return;
            }

            total_time = jsonObject.getInt("total_time");

            JSONObject res2 = new JSONObject();
            res2.put("arrival_time", total_time);

            res.put("data", res2.toString());
            res.put("status", "OK");
            String myResponse = res.toString();
            r.sendResponseHeaders(200, myResponse.length());
            OutputStream os = r.getResponseBody();
            os.write(myResponse.getBytes());
            os.close();  

            
        } catch (Exception e) {
            Utils.error(500, res, r, "INTERNAL SERVER ERROR");
        }
    }
}