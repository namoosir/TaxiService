package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.io.OutputStream;

import org.json.*;
import org.neo4j.driver.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.neo4j.driver.Record;

import java.util.List;

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
        JSONObject res = new JSONObject();
        
        if (uriSplitter.length != 4) {
            res.put("data", new JSONObject());
            Utils.error(statusCode, res, r, "BAD REQUEST");
            return;
        }
        String uid = uriSplitter[3].split("\\?radius=")[0];
        String radiusString = uriSplitter[3].split("\\?radius=")[1];
        int radius;

        System.out.println(uid);      
        System.out.println(radiusString);     

        if (uid.isEmpty() || radiusString.isBlank()) {
            res.put("data", new JSONObject());
            Utils.error(statusCode, res, r, "BAD REQUEST");
            return;
        }

        try {
            radius = Integer.parseInt(radiusString); 
        } catch(Exception e){
            res.put("data", new JSONObject());
            Utils.error(statusCode, res, r, "BAD REQUEST");
            return;
        }
        
        String getUserQuery = "MATCH (n:user {uid:$z}) RETURN n.longitude,n.latitude,n.street_at";
        String getLocationQuery = "MATCH (l:user {is_driver:$a}) WHERE distance( point({longitude: l.longitude, latitude: l.latitude}), point({ latitude: $x, longitude: $y})) < $z RETURN l";
        
        try (Session session = Utils.driver.session()) {
            Result userRes = session.run(getUserQuery, parameters("z", uid));
            
            if (userRes.hasNext()) {

                Record user = userRes.next();
                Double longitude = user.get("n.longitude").asDouble();
                Double latitude = user.get("n.latitude").asDouble();

                Result locationRes = session.run(getLocationQuery, parameters("a", true, "x", latitude, "y", longitude, "z", radius*1000));

                if (locationRes.hasNext()) {
                    JSONObject res2 = new JSONObject();
                    List<Record> drivers = locationRes.list();
                    for (Record record : drivers) {
                        JSONObject d = new JSONObject();
                        String id = record.get("l").get("uid").asString();
                        Double longitudeString = record.get("l").get("longitude").asDouble();
                        Double latitudeString = record.get("l").get("latitude").asDouble();
                        String streetString = record.get("l").get("street_at").asString();
                        
                        d.put("longitude",longitudeString);
                        d.put("latitude", latitudeString);
                        d.put("street", streetString);
                        res2.put(id, d);                        
                    }  
                    res.put("data", res2);   
                    res.put("status", "OK");

                    String response = res.toString();
                    r.sendResponseHeaders(200, response.length());
                    OutputStream os = r.getResponseBody();
                    os.write(response.getBytes());
                    os.close();     
                }
                else {
                    res.put("data", new JSONObject());
                    Utils.error(404, res, r, "NO NEARBY DRIVERS FOUND");
                }
            } else {
                res.put("data", new JSONObject());
                Utils.error(404, res, r, "USER NOT FOUND");
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.put("data", new JSONObject());
            Utils.error(500, res, r, "INTERNAL SERVER ERROR");
        }
    }
}