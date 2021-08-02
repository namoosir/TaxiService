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

public class User implements HttpHandler{
    HttpClient httpClient;

    public User() throws ClassNotFoundException {
        httpClient = Utils.httpClient;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String url = "http://usermicroservice:8000" + exchange.getRequestURI().toString();
        JSONObject res = new JSONObject();

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            res = new JSONObject(response.body());
            String finalRes = res.toString();
            exchange.sendResponseHeaders(200, finalRes.length());
            OutputStream os = exchange.getResponseBody();
            os.write(finalRes.getBytes());
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
            Utils.error(500, res, exchange, "INTERNAL SERVER ERROR");
        }
        
    }

}
