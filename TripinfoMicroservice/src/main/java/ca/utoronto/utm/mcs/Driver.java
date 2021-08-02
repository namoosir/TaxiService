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

public class Driver implements HttpHandler {

    MongoClient client;
    MongoDatabase database;
    MongoCollection<Document> trip;
    HttpClient httpClient;

    public Driver() throws ClassNotFoundException {
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
        JSONObject res2 = new JSONObject();
        String statusResponse;
        String statusCodeString;
        ArrayList<JSONObject> finalAns = new ArrayList<JSONObject>();


        if (uriSplitter.length != 4) {
            Utils.error(statusCode, res, r, "BAD REQUEST");
            return;
        }

        String uid = uriSplitter[3];

        if (uid.isEmpty()) {
            Utils.error(statusCode, res, r, "BAD REQUEST");
            return;
        }

        try {
            BasicDBObject query = new BasicDBObject();
            query.put("driver", uid);
    
            FindIterable<Document> docs = trip.find(query);
    
            if (docs.first() == null) {
                statusCodeString = "404";
                statusResponse = "NO TRIPS FOUND";
            }
            else{
                statusCodeString = "200";
                statusResponse = "OK";
            }
    
            for(Document doc : docs) {
                JSONObject cur = new JSONObject();
                cur.put("_id", doc.getObjectId("_id").toString());
                cur.put("distance", doc.getInteger("distance"));
                cur.put("totalCost", doc.getDouble("totalCost"));
                cur.put("discount", doc.getDouble("discount"));
                cur.put("startTime", doc.getInteger("startTime"));
                cur.put("endTime", doc.getInteger("endTime"));
                cur.put("TimeElapsed", doc.getString("timeElapsed"));
                cur.put("driver", doc.getString("driver"));
                finalAns.add(cur);
            }
     
            res2.put("trips", finalAns);
            res.put("data", res2);
            res.put(statusCodeString, statusResponse);
            String response = res.toString();
            r.sendResponseHeaders(statusCode, response.length());
            OutputStream os = r.getResponseBody();
            os.write(response.getBytes());
            os.close();
            
        } catch (Exception e) {
            Utils.error(500, res, r, "INTERNAL SERVER ERROR");
        }
    }
}