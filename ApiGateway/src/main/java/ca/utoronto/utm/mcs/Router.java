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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;


public class Router implements HttpHandler{

    HttpClient httpClient;
    HttpResponse response;

    public Router() throws ClassNotFoundException {
        httpClient = Utils.httpClient;
    }

    private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) throws JSONException {
        JSONObject jsonb = new JSONObject();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            jsonb.put(entry.getKey().toString(), entry.getValue());

        }
        return HttpRequest.BodyPublishers.ofString(jsonb.toString());
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

        //HttpRequest.newBuilder().method(method, bodyPublisher)

        url += exchange.getRequestURI().toString();
        System.out.println("URL is " + url);

        try {
            if(exchange.getRequestMethod().equals("GET")){
                HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
                System.out.print("about to go in");
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    
                res = new JSONObject(response.body());
    
                String myResponse = res.toString();
                exchange.sendResponseHeaders(response.statusCode(), myResponse.length());
                OutputStream os = exchange.getResponseBody();
                os.write(myResponse.getBytes());
                os.close();  

            }else{
                Map<Object, Object> data = new HashMap<>();
                String body = Utils.convert(exchange.getRequestBody());
                JSONObject req = new JSONObject(body);

                Iterator<?> keys = req.keys();
                System.out.println(req.toString());
                 
                while(keys.hasNext()) {
                    String key = (String)keys.next();
                    data.put(key, req.get(key));
                    System.out.println(key);
                    System.out.println(req.get(key));
                }
                HttpRequest request =  HttpRequest.newBuilder().uri(URI.create(url)).method(exchange.getRequestMethod(), buildFormDataFromMap(data)).build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    
                res = new JSONObject(response.body());
    
                String myResponse = res.toString();
                exchange.sendResponseHeaders(response.statusCode(), myResponse.length());
                OutputStream os = exchange.getResponseBody();
                os.write(myResponse.getBytes());
                os.close();   
            }         

        } catch (Exception e) {
            e.printStackTrace();
            Utils.error(500, res, exchange, "INTERNAL SERVER ERROR");
        }
        
    }

}
