import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ItemServer {
    // Our "database" - a simple map from ID to item name
    private static Map<String, String> items = new HashMap<>();
    private static int nextId = 1;

    public static void main(String[] args) throws IOException {
        // Add some sample data
        items.put("1", "Laptop");
        items.put("2", "Keyboard");
        items.put("3", "Mouse");
        nextId = 4;

        HttpServer server = HttpServer.create(
            new InetSocketAddress(4001), 0
        );

        server.createContext("/items", new ItemsHandler());
        server.createContext("/health", new HealthHandler());
        server.start();
        System.out.println("Server running on http://localhost:4001");
    }
    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "ok";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    static class ItemsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            System.out.println(method + " " + path);

            if (method.equals("GET")) {
                handleGet(exchange, path);
                return;
            } else if (method.equals("POST") && path.equals("/items")) {
                handlePost(exchange);
                return;
            } else {
                sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}");
                return;
            }
        } // end handle
        private void handlePost(HttpExchange exchange) throws IOException {
            // Read the request body
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes());
            is.close();

            if (body.isEmpty()) {
                sendResponse(exchange, 400, "{\"error\": \"Item name is required\"}");
                return;
            }

            // Create new item with next available ID
            String id = String.valueOf(nextId++);
            String name = body.trim();
            items.put(id, name);

            // Return the created item as JSON
            String json = "{\"id\": \"" + id + "\", \"name\": \"" + name + "\"}";
            sendResponse(exchange, 201, json);
        }    
        private void handleGet(HttpExchange exchange, String path) throws IOException {
            if (path.equals("/items")) {
                // Build JSON array
                StringBuilder sb = new StringBuilder();
                sb.append("[\n");
                boolean first = true;
                for (Map.Entry<String, String> entry : items.entrySet()) {
                    if (!first) {
                        sb.append(",\n");
                    }
                    sb.append(" {\"id\": \"")
                      .append(entry.getKey())
                      .append("\", \"name\": \"")
                      .append(entry.getValue())
                      .append("\"}");
                    first = false;
                    }
                sb.append("\n]");
                sendResponse(exchange, 200, sb.toString());
            } else if (path.matches("/items/\\d+")) {
                // Get single item - extract ID from path
                String id = path.substring(7); // Remove "/items"
                String item = items.get(id);

                if (item != null) {
                    // Return JSON instead of plain text
                    String json = "{\"id\": \"" + id + "\", \"name\": \"" + item + "\"}";
                    sendResponse(exchange, 200, json);
                } else {
                    // item not found
                    sendResponse(exchange, 404, "{\"error\": \"Item not found\"}");
                }
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Not Found\"}");
            }
    }
        private void sendResponse(HttpExchange exchange, int code,
                String body) throws IOException {
                    // Tell clients this is JSON
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    byte[] bytes = body.getBytes();
                    exchange.sendResponseHeaders(code, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
            }
    }
}