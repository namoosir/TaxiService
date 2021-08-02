package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
//import java.util.Iterator;

public class Router implements HttpHandler{

    HttpClient httpClient;

    public Router() throws ClassNotFoundException {
        httpClient = Utils.httpClient;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] uri = exchange.getRequestURI().toString().split("/");
        String url = "";
        JSONObject res = new JSONObject();

        switch (uri[1]) {
            case "location":
                url = "http://locationmicroservice:8000";
                break;
            case "trip":
                url = "http://tripinfomicroservice:8000";
                break;
            case "user":
                url = "http://usermicroservice:8000";
                break;
            default:
            Utils.error(400, res, exchange, "BAD REQUEST");
        }
        
        url += exchange.getRequestURI().toString();
        System.out.println("URL is " + url);

        try {
            
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            res = new JSONObject(response.body());


            String myResponse = res.toString();
            exchange.sendResponseHeaders(response.statusCode(), myResponse.length());
            OutputStream os = exchange.getResponseBody();
            os.write(myResponse.getBytes());
            os.close();  
         

        } catch (Exception e) {
            e.printStackTrace();
            Utils.error(500, res, exchange, "INTERNAL SERVER ERROR");
        }
        
    }

}