package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.io.OutputStream;

import org.json.*;
import org.neo4j.driver.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.neo4j.driver.Record;

import java.util.Arrays;

import static org.neo4j.driver.Values.parameters;

public class nearbyDriver implements HttpHandler {

    @Override
    public void handle(HttpExchange r) throws IOException {
        try {
            if (r.getRequestMethod().equals("GET")) {
                getDrivers(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDrivers(HttpExchange r) throws IOException, JSONException {
        int statusCode = 400;
        String requestURI = r.getRequestURI().toString();
        String[] uriSplitter = requestURI.split("/");

        if (uriSplitter.length != 3) {
            JSONObject data = new JSONObject();
            data.put("status", "BAD REQUEST");
            String response = data.toString();
            r.sendResponseHeaders(statusCode, response.length());
            OutputStream os = r.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return;
        }

        JSONObject res = new JSONObject();
        String uid = uriSplitter[2].split('?radius=')[0];
        String radiusString = uriSplitter[2].split('?radius=')[1];
        int radius;

        if (uid.isEmpty() || radius.isBlank()) {
            error(statusCode, res, r);
            return;
        }

        try {
            radius = Double.parseDouble(radiusString); //is acctually a double
        } catch(Exception e){
            error(statusCode, res,r);
            return;
        }

        //what to do???????
        String getDriverQuery = "MATCH (n: user {uid :$x}) RETURN n.longitude,n.latitude,n.street_at";

        try (Session session = Utils.driver.session()) {
            Result result = session.run(getDriverQuery, parameters("x", uid));
            if (result.hasNext()) {

                Record user = result.next();
                Double longitude = user.get("n.longitude").asDouble();
                Double latitude = user.get("n.latitude").asDouble();
                String street = user.get("n.street_at").asString();

                statusCode = 200;
                JSONObject data = new JSONObject();
                data.put("longitude", longitude);
                data.put("latitude", latitude);
                data.put("street", street);
                res.put("status", "OK");
                res.put("data", data);
                String response = res.toString();
                r.sendResponseHeaders(statusCode, response.length());
                OutputStream os = r.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                statusCode = 404;
                JSONObject data = new JSONObject();
                data.put("status", "NOT FOUND");
                String response = data.toString();
                r.sendResponseHeaders(statusCode, response.length());
                OutputStream os = r.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        } catch (Exception e) {
            statusCode = 500;
            JSONObject data = new JSONObject();
            data.put("status", "INTERNAL SERVER ERROR");
            String response = data.toString();
            r.sendResponseHeaders(statusCode, response.length());
            OutputStream os = r.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private void error(int statusCode, JSONObject res, HttpExchange r) throws IOException, JSONException {
        res.put("status", "BAD REQUEST");
        String response = res.toString();
        r.sendResponseHeaders(statusCode, response.length());
        OutputStream os = r.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}