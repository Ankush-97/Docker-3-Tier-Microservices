import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class App {
    private static final String DB_URL = "jdbc:mysql://db:3306/student_db?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "redhat";

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // API 1: Save User Data
        server.createContext("/save", exchange -> {
            try {
                addCorsHeaders(exchange);
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    exchange.close();
                    return;
                }

                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    String body = new String(exchange.getRequestBody().readAllBytes());
                    Map<String, String> params = parseFormData(body);

                    String name = params.getOrDefault("name", "");
                    String email = params.getOrDefault("email", "");
                    String contact = params.getOrDefault("contact", "");
                    String qualification = params.getOrDefault("qualification", "");

                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                        PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO users(name, email, contact, qualification) VALUES(?, ?, ?, ?)"
                        );
                        stmt.setString(1, name);
                        stmt.setString(2, email);
                        stmt.setString(3, contact);
                        stmt.setString(4, qualification);
                        stmt.executeUpdate();

                        String res = "User Registered Successfully!";
                        exchange.sendResponseHeaders(200, res.length());
                        exchange.getResponseBody().write(res.getBytes());
                    } catch (Exception e) {
                        String res = "DB Error: " + e.getMessage();
                        exchange.sendResponseHeaders(500, res.length());
                        exchange.getResponseBody().write(res.getBytes());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        });

        // API 2: Get All Users
        server.createContext("/users", exchange -> {
            try {
                addCorsHeaders(exchange);
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    exchange.close();
                    return;
                }

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT * FROM users ORDER BY id DESC");

                        StringBuilder json = new StringBuilder("[");
                        boolean first = true;
                        while (rs.next()) {
                            if (!first) json.append(",");
                            json.append("{")
                                .append("\"id\":").append(rs.getInt("id")).append(",")
                                .append("\"name\":\"").append(escapeJson(rs.getString("name"))).append("\",")
                                .append("\"email\":\"").append(escapeJson(rs.getString("email"))).append("\",")
                                .append("\"contact\":\"").append(escapeJson(rs.getString("contact"))).append("\",")
                                .append("\"qualification\":\"").append(escapeJson(rs.getString("qualification"))).append("\"")
                                .append("}");
                            first = false;
                        }
                        json.append("]");

                        byte[] responseBytes = json.toString().getBytes("UTF-8");
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, responseBytes.length);
                        exchange.getResponseBody().write(responseBytes);
                    } catch (Exception e) {
                        String res = "[]";
                        exchange.sendResponseHeaders(500, res.length());
                        exchange.getResponseBody().write(res.getBytes());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        });

        server.start();
    }

    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
    }

    private static Map<String, String> parseFormData(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.isEmpty()) return map;
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length > 1) {
                try {
                    map.put(kv[0], URLDecoder.decode(kv[1], "UTF-8"));
                } catch (Exception e) {
                    map.put(kv[0], kv[1]);
                }
            }
        }
        return map;
    }

    private static String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }
}
