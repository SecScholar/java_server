import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SimpleServer {
    public static void main(String[] args) throws IOException {
        // Create server listening on port 8000
        HttpServer server = HttpServer.create(
            new InetSocketAddress(8000), 0
        );
        // Register a handler for the /hello path
        server.createContext("/hello", new HelloHandler());
       
        // Start the server
        server.start();
        System.out.println("Server started on port 8000");
    }
}
class HelloHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "Hello, World!";
        
        // Send response headers (200 = OK, response length )
        exchange.sendResponseHeaders(200, response.length());
        
        // Write respone body
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}