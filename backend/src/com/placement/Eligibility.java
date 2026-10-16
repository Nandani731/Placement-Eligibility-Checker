package com.placement;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Eligibility implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405,
                    "{\"success\":false,\"message\":\"Method not allowed\"}");
            return;
        }

        String sql = "SELECT company_id, company_name, min_cgpa, "
                + "max_backlogs, min_attendance FROM companies";

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                sendJson(exchange, 500,
                        "{\"success\":false,\"message\":\"Database connection failed\"}");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    if (!first) {
                        json.append(",");
                    }

                    json.append("{")
                            .append("\"company_id\":")
                            .append(rs.getInt("company_id")).append(",")
                            .append("\"name\":\"")
                            .append(escapeJson(rs.getString("company_name")))
                            .append("\",")
                            .append("\"min_cgpa\":")
                            .append(rs.getBigDecimal("min_cgpa")).append(",")
                            .append("\"max_backlogs\":")
                            .append(rs.getInt("max_backlogs")).append(",")
                            .append("\"min_attendance\":")
                            .append(rs.getBigDecimal("min_attendance"))
                            .append("}");

                    first = false;
                }
            }

            json.append("]");
            sendJson(exchange, 200, json.toString());

        } catch (SQLException e) {
            e.printStackTrace();

            sendJson(exchange, 500,
                    "{\"success\":false,\"message\":\"Could not load companies\"}");
        }
    }

    public static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers", "Content-Type");
    }

    public static void sendJson(
            HttpExchange exchange, int statusCode, String json)
            throws IOException {

        addCorsHeaders(exchange);

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static Map<String, String> parseForm(String body) {
        Map<String, String> form = new HashMap<>();

        if (body == null || body.isBlank()) {
            return form;
        }

        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);

            String key = URLDecoder.decode(
                    parts[0], StandardCharsets.UTF_8);

            String value = parts.length > 1
                    ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                    : "";

            form.put(key, value);
        }

        return form;
    }

    public static String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}