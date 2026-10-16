package com.placement;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Server {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
                new InetSocketAddress(PORT), 0);

        Eligibility eligibilityHandler = new Eligibility();

        server.createContext("/api/login", Login::handleLogin);
        server.createContext("/api/register", Login::handleRegister);
        server.createContext("/api/eligibility", eligibilityHandler);
        server.createContext("/api/companies", eligibilityHandler::handle);
        server.createContext("/api/results", Server::handleResults);
        server.createContext("/api/profile", Server::handleProfile);

        server.createContext("/", Server::handleStaticFile);

        server.start();

        System.out.println("=================================================");
        System.out.println("  PlaceReady - Placement Eligibility Platform");
        System.out.println("=================================================");
        System.out.println(" Server Started Successfully!");
        System.out.println(" Web Portal:  http://localhost:" + PORT);
        System.out.println(" Login Page:  http://localhost:" + PORT + "/login.html");
        System.out.println(" Dashboard:   http://localhost:" + PORT + "/dashboard.html");
        System.out.println(" Eligibility: http://localhost:" + PORT + "/eligibility.html");
        System.out.println(" Results:     http://localhost:" + PORT + "/result.html");
        System.out.println(" API Base:    http://localhost:" + PORT + "/api/");
        System.out.println("=================================================");
    }

    private static void handleResults(HttpExchange exchange)
            throws IOException {

        Eligibility.addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            Eligibility.sendJson(exchange, 405,
                    "{\"success\":false,\"message\":\"GET required\"}");
            return;
        }

        String query = exchange.getRequestURI().getRawQuery();
        String studentIdValue = null;

        if (query != null) {
            for (String part : query.split("&")) {
                String[] pair = part.split("=", 2);

                if (pair.length == 2 && pair[0].equals("studentId")) {
                    studentIdValue = URLDecoder.decode(
                            pair[1], StandardCharsets.UTF_8);
                    break;
                }
            }
        }

        if (studentIdValue == null || studentIdValue.isBlank()) {
            Eligibility.sendJson(exchange, 400,
                    "{\"success\":false,\"message\":\"studentId is required\"}");
            return;
        }

        final int studentId;

        try {
            studentId = Integer.parseInt(studentIdValue);
        } catch (NumberFormatException e) {
            Eligibility.sendJson(exchange, 400,
                    "{\"success\":false,\"message\":\"Invalid studentId\"}");
            return;
        }

        List<Result> results = Result.getResultsByStudent(studentId);
        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < results.size(); i++) {
            Result r = results.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{")
                .append("\"resultId\":").append(r.getResultId()).append(",")
                .append("\"studentId\":").append(r.getStudentId()).append(",")
                .append("\"companyId\":").append(r.getCompanyId()).append(",")
                .append("\"companyName\":\"")
                .append(Eligibility.escapeJson(r.getCompanyName())).append("\",")
                .append("\"jobRole\":\"")
                .append(Eligibility.escapeJson(r.getJobRole())).append("\",")
                .append("\"packageLpa\":").append(r.getPackageLpa()).append(",")
                .append("\"eligible\":").append(r.isEligible()).append(",")
                .append("\"studentCgpa\":").append(r.getStudentCgpa()).append(",")
                .append("\"studentBacklogs\":").append(r.getStudentBacklogs()).append(",")
                .append("\"studentAttendance\":").append(r.getStudentAttendance()).append(",")
                .append("\"resultDate\":\"")
                .append(Eligibility.escapeJson(r.getResultDate())).append("\"")
                .append("}");
        }

        json.append("]");
        Eligibility.sendJson(exchange, 200, json.toString());
    }

    private static void handleProfile(HttpExchange exchange)
            throws IOException {

        Eligibility.addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            Eligibility.sendJson(exchange, 405,
                    "{\"success\":false,\"message\":\"GET required\"}");
            return;
        }

        String query = exchange.getRequestURI().getRawQuery();
        String studentIdValue = null;

        if (query != null) {
            for (String part : query.split("&")) {
                String[] pair = part.split("=", 2);

                if (pair.length == 2 && pair[0].equals("studentId")) {
                    studentIdValue = URLDecoder.decode(
                            pair[1], StandardCharsets.UTF_8);
                    break;
                }
            }
        }

        if (studentIdValue == null || studentIdValue.isBlank()) {
            Eligibility.sendJson(exchange, 400,
                    "{\"success\":false,\"message\":\"studentId is required\"}");
            return;
        }

        String sql = "SELECT student_id, name, email, branch, cgpa, "
                + "backlogs, attendance FROM students WHERE student_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                Eligibility.sendJson(exchange, 500,
                        "{\"success\":false,\"message\":\"Database connection failed\"}");
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, Integer.parseInt(studentIdValue));

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String json = "{"
                                + "\"studentId\":" + rs.getInt("student_id") + ","
                                + "\"name\":\"" + Eligibility.escapeJson(rs.getString("name")) + "\","
                                + "\"email\":\"" + Eligibility.escapeJson(rs.getString("email")) + "\","
                                + "\"branch\":\"" + Eligibility.escapeJson(rs.getString("branch")) + "\","
                                + "\"cgpa\":" + rs.getDouble("cgpa") + ","
                                + "\"backlogs\":" + rs.getInt("backlogs") + ","
                                + "\"attendance\":" + rs.getDouble("attendance")
                                + "}";

                        Eligibility.sendJson(exchange, 200, json);
                    } else {
                        Eligibility.sendJson(exchange, 404,
                                "{\"success\":false,\"message\":\"Student not found\"}");
                    }
                }
            }
        } catch (NumberFormatException e) {
            Eligibility.sendJson(exchange, 400,
                    "{\"success\":false,\"message\":\"Invalid studentId\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            Eligibility.sendJson(exchange, 500,
                    "{\"success\":false,\"message\":\"Could not load profile\"}");
        }
    }

    private static void handleStaticFile(HttpExchange exchange)
            throws IOException {

        Eligibility.addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();

        if (path.startsWith("/api/")) {
            Eligibility.sendJson(exchange, 404,
                    "{\"success\":false,\"message\":\"API endpoint not found\"}");
            return;
        }

        if (path.equals("/") || path.isBlank()) {
            path = "/index.html";
        }

        Path candidate1 = Paths.get("frontend", path.substring(1));
        Path candidate2 = Paths.get("..", "frontend", path.substring(1));
        Path candidate3 = Paths.get(
                "Placement-Eligibility-Checker", "frontend", path.substring(1));

        Path targetFile = null;

        if (Files.isRegularFile(candidate1)) {
            targetFile = candidate1;
        } else if (Files.isRegularFile(candidate2)) {
            targetFile = candidate2;
        } else if (Files.isRegularFile(candidate3)) {
            targetFile = candidate3;
        }

        if (targetFile != null && Files.isRegularFile(targetFile)) {
            byte[] bytes = Files.readAllBytes(targetFile);

            exchange.getResponseHeaders().set(
                    "Content-Type", getMimeType(path));

            exchange.sendResponseHeaders(200, bytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } else {
            byte[] bytes = ("<h1>404 File Not Found</h1><p>Requested: "
                    + path + "</p>").getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                    "Content-Type", "text/html; charset=UTF-8");

            exchange.sendResponseHeaders(404, bytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private static String getMimeType(String path) {
        String lower = path.toLowerCase();

        if (lower.endsWith(".html")) return "text/html; charset=UTF-8";
        if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
        if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".ico")) return "image/x-icon";

        return "application/octet-stream";
    }
}