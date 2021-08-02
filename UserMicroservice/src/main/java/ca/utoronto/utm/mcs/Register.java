package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.util.Iterator;

public class Register implements HttpHandler {
    public Connection connection;

    public Register() throws ClassNotFoundException, SQLException {
        String url = "jdbc:postgresql://postgres:5432/root";
        Class.forName("org.postgresql.Driver");
        this.connection = DriverManager.getConnection(url, "root", "123456");
    }

    @Override
    public void handle(HttpExchange r) throws IOException {

        try {
            if (r.getRequestMethod().equals("POST")) {
                handlePOST(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handlePOST(HttpExchange r) throws JSONException, IOException {

        try {
            String body = Utils.convert(r.getRequestBody());
            JSONObject deserialized = new JSONObject(body);

            if (!(deserialized.has("name") && deserialized.has("email") && deserialized.has("password"))) {
                JSONObject res = new JSONObject();
                res.put("status", "BAD REQUEST");
                String response = res.toString();
                r.sendResponseHeaders(400, response.length());
                OutputStream os = r.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            String name = deserialized.getString("name");
            String email = deserialized.getString("email");
            String password = Utils.hash(deserialized.getString("password"));

            String prepare =
                "INSERT INTO Users (redeemedCoupons, availableCoupons, email, prefer_name, password, rides) VALUES (ARRAY[]::integer[],ARRAY[]::integer[], ?, ?, ?, ?)";

            PreparedStatement ps = this.connection.prepareStatement(prepare);
            ps.setString(1, email);
            ps.setString(2, name);
            ps.setString(3, password);     
            ps.setInt(4, 0);

            int a = ps.executeUpdate();

            if (a > 0) {
                JSONObject res = new JSONObject();
                res.put("status", "OK");
                String response = res.toString();
                r.sendResponseHeaders(200, response.length());
                OutputStream os = r.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                JSONObject res = new JSONObject();
                res.put("status", "INTERNAL SERVER ERROR");
                String response = res.toString();
                r.sendResponseHeaders(500, response.length());
                OutputStream os = r.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject res = new JSONObject();
            res.put("status", "INTERNAL SERVER ERROR");
            String response = res.toString();
            r.sendResponseHeaders(500, response.length());
            // Writing response body
            OutputStream os = r.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
