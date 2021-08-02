package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.io.OutputStream;

import org.json.*;
import org.neo4j.driver.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Path.Segment;

import jdk.jshell.execution.Util;

import java.util.ArrayList;
import java.util.Arrays;

import static org.neo4j.driver.Values.parameters;

public class navigation implements HttpHandler {

    @Override
    public void handle(HttpExchange r) throws IOException {
        try {
            if (r.getRequestMethod().equals("GET")) {
                getRoute(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getRoute(HttpExchange r) throws IOException, JSONException {
        int statusCode = 400;
        String requestURI = r.getRequestURI().toString();
        String[] uriSplitter = requestURI.split("/");
        JSONObject res = new JSONObject();
        JSONObject res2 = new JSONObject();
        JSONObject resFinal = new JSONObject();
        
        if (uriSplitter.length != 4) {
            Utils.error(statusCode, res, r, "BAD REQUEST");
            return;
        }
        
        String driverUid = uriSplitter[3].split("\\?passengerUid=")[0];
        String passengerUid = uriSplitter[3].split("\\?passengerUid=")[1];

        if (driverUid.isEmpty() || passengerUid.isBlank()) {
            Utils.error(statusCode, res, r, "BAD REQUEST");
            return;
        }
                                                                                                    
        String routeQuery = "MATCH (start:road {name: $x}), (end:road {name: $y}) MATCH p=(start)-[*]->(end) WITH p,reduce(s = 0, r IN relationships(p) | s + r.travel_time) AS dist RETURN p, dist ORDER BY dist ASC LIMIT(1)";
        
        String getUserQuery = "MATCH (n:user {uid:$z}) RETURN n.street_at";
        String getDriverQuery = "MATCH (n:user {uid:$z}) RETURN n.street_at";


        try (Session session = Utils.driver.session()) {
            ArrayList<JSONObject> path = new ArrayList<JSONObject>();

            Result r2 = session.run(getUserQuery, parameters("z", passengerUid));
            Result r1 = session.run(getDriverQuery, parameters("z", driverUid));
        
            if (!r1.hasNext() || !r2.hasNext()) {
                Utils.error(404, res, r, "USER NOT FOUND");
                return;
            }
            
            System.out.println(passengerUid);
            System.out.println(driverUid);
            
            String road2 = r2.next().get("n.street_at").asString();
            String road1 = r1.next().get("n.street_at").asString();

            Result roadCheckResult = session.run(routeQuery, parameters("x", road1, "y", road2));

            if (roadCheckResult.list().isEmpty()) Utils.error(404, res, r, "ROUTE NOT FOUND");

            roadCheckResult = session.run(routeQuery, parameters("x", road1, "y", road2));

            int total_time = roadCheckResult.next().get("dist").asInt();

            roadCheckResult = session.run(routeQuery, parameters("x", road1, "y", road2));

            Path a = roadCheckResult.next().get("p").asPath();

            Segment hi = null;

            

            for (Segment segment : a) { 
                if(segment.end().get("name").asString() == road2) break;
                JSONObject curRoad = new JSONObject();   
                curRoad.put("street", segment.start().get("name").asString());                
                curRoad.put("time", segment.relationship().get("travel_time").asInt());
                curRoad.put("traffic", segment.relationship().get("is_traffic").asBoolean());
                path.add(curRoad);  
                hi = segment;      
            }

            JSONObject curRoad = new JSONObject(); 
            curRoad.put("street", hi.start().get("name").asString()); 
            curRoad.put("time", hi.relationship().get("travel_time").asInt());
            curRoad.put("traffic", hi.relationship().get("is_traffic").asBoolean());
            curRoad.put("street", hi.end().get("name").asString());                

            
            path.add(curRoad);

            res2.put("route",path);
            res2.put("total_time", total_time);

            resFinal.put("data", res2);
            resFinal.put("status", "OK");

            String response = resFinal.toString();
            r.sendResponseHeaders(200, response.length());
            OutputStream os = r.getResponseBody();
            os.write(response.getBytes());
            os.close();  

            session.close(); 
            
        } catch(Exception e) {
            e.printStackTrace();
            Utils.error(500, res, r, "INTERNAL SERVER ERROR");
        }

    }
    
}
