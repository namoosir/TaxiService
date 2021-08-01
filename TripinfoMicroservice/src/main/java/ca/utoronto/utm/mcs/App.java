package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class App {
   static int PORT = 8000;

   public static void main(String[] args) throws IOException, ClassNotFoundException {
      HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
      server.createContext("/hi", new test());
      server.createContext("/trip/request", new Request()); // POST /trip/request SHOULD BE GET, ASK A QUEWRION ABOU ZTHIS
      // server.createContext("/trip/confirm", new confirm());
      // server.createContext("/trip/", new trip());
      // server.createContext("/trip/passenger/", new passenger());
      // server.createContext("/trip/driver/", new driver());
      // server.createContext("/trip/driverTime/", new driverTime());
      server.start();
      System.out.printf("Server started on port %d...\n", PORT);
   }
}
