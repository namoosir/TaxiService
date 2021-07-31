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


public class Request implements HttpHandler {

    MongoClient client;
    MongoDatabase database;
    MongoCollection<Document> trip;

    public Request() throws ClassNotFoundException {
        client = Utils.client;
        database = Utils.database;
        trip = Utils.trip;
     }

    @Override
    public void handle(HttpExchange r) throws IOException {
       try {
          if (r.getRequestMethod().equals("POST")) {
             handlePost(r);
          }
       } catch (Exception e) {
          System.out.println("Error Occurred! Msg:   " + e);
       }
    }
    
    private void handlePost(HttpExchange r) throws IOException, JSONException {
        String body = Utils.convert(r.getRequestBody());
        JSONObject res = new JSONObject();
        JSONObject req = new JSONObject(body);
        int statusCode = 400;
        if (req.has("roadName1") && req.has("roadName2") && req.has("hasTraffic") && req.has("time")) {
            try (Session session = Utils.driver.session()) {
                String road1 = req.getString("roadName1");
                String road2 = req.getString("roadName2");
                Boolean isTraffic = req.getBoolean("hasTraffic");
                int time = req.getInt("time");
                String preparedStatement = "MATCH (r1:road {name: $x}), (r2:road {name: $y}) "
                    + "CREATE (r1) -[r:ROUTE_TO {travel_time: $z, is_traffic: $u}]->(r2) RETURN type(r)";
                Result result = session.run(preparedStatement,
                    parameters("x", road1, "y", road2, "u", isTraffic, "z", time));
                if (result.hasNext()) {
                // relationship created
                statusCode = 200;
                res.put("status", "OK");
                } else {
                statusCode = 500;
                res.put("status", "INTERNAL SERVER ERROR");
                }
            } catch (Exception e) {
                statusCode = 500;
                res.put("status", "INTERNAL SERVER ERROR");
            }
        } else {
            res.put("status", "BAD REQUEST");
        }
        String response = res.toString();
        r.sendResponseHeaders(statusCode, response.length());
        OutputStream os = r.getResponseBody();
        os.write(response.getBytes());
        os.close();


    }
}