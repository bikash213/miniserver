package io.logik.basicServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class ServerDesign {
    public static void main(String[] args) throws IOException {
        // Create a new HTTP server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Define a single handler for all contexts
        server.createContext("/", new MainHandler());

        // Start the server
        server.setExecutor(null); // creates a default executor
        server.start();

        System.out.println("Server started on port 8080");
    }

    // Utility class to store handlers
    static class HandlerRegistry {
        static final Map<String, HttpHandler> handlers = new HashMap<>();

        static void registerHandler(String path, HttpHandler handler) {
            handlers.put(path, handler);
            System.out.println("registered path"+path);
        }
    }

    // Main handler to route requests to specific handlers
    static class MainHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            System.out.println("recievied request with path"+path);
            HttpHandler handler = HandlerRegistry.handlers.get(path);
            if (handler != null) {
                handler.handle(exchange);
            } else {
                new RootHandler().handle(exchange);
            }
        }
    }

    // Handler to serve the HTML form
    static class RootHandler implements HttpHandler {
        static {
            HandlerRegistry.registerHandler("/", new RootHandler());
            SubmitHandler submitHandler = new SubmitHandler();
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "<html><body>"
                    + "<form method='POST' action='/submit'>"
                    + "Hi Enter Your name <input type='text' name='input'>"
                    + "<input type='submit' value='Submit'>"
                    + "</form>"
                    + "</body></html>";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // Handler to process form submission
    static class SubmitHandler implements HttpHandler {
        static {
            HandlerRegistry.registerHandler("/submit", new SubmitHandler());
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange.getRequestBody());
                String input = parseFormData(body);

                String response = "<html><body>"
                        + "Hello " + input + "! How can I help you ?" 
                        + "<br><a href='/'>Go back</a>"
                        + "</body></html>";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        private String readRequestBody(InputStream is) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            return body.toString();
        }

        private String parseFormData(String formData) {
            String[] keyValue = formData.split("=");
            return keyValue.length > 1 ? keyValue[1].replace('+', ' ') : "";
        }
    }
}
