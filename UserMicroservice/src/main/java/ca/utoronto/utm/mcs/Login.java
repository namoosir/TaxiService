package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.util.Iterator;

public class Login implements HttpHandler {
    public Connection connection;

    public Login() throws ClassNotFoundException, SQLException {
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

    public void handlePOST(HttpExchange r) throws IOException, JSONException, SQLException {

        try {
            String body = Utils.convert(r.getRequestBody());
            JSONObject deserialized = new JSONObject(body);
            JSONObject res = new JSONObject();

            if (!deserialized.has("email") || !deserialized.has("password")) {
                res.put("status", "BAD REQUEST");
                String response = res.toString();
                r.sendResponseHeaders(400, response.length());
                OutputStream os = r.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            String email = deserialized.getString("email");
            String password = Utils.hash(deserialized.getString("password"));

            String prepare = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement ps = this.connection.prepareStatement(prepare);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            boolean found = rs.next();

            if (!found) {
                res.put("status", "NOT FOUND");
                String response = res.toString();
                r.sendResponseHeaders(404, response.length());
                // Writing response body
                OutputStream os = r.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                res.put("status", "OK");
                String response = res.toString();
                r.sendResponseHeaders(200, response.length());
                // Writing response body
                OutputStream os = r.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }

        } catch (Exception e) {
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