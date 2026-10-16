package com.placement;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

public class Login {

    // Server ko start karne ke liye
    public static void main(String[] args) throws IOException {
        Server.main(args);
    }

    // LOGIN API
    public static void handleLogin(HttpExchange exchange)
            throws IOException {

        Eligibility.addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            Eligibility.sendJson(exchange, 405,
                    "{\"success\":false,\"message\":\"POST required\"}");
            return;
        }

        try {
            String body = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            Map<String, String> form = Eligibility.parseForm(body);

            String email = form.get("email");
            String password = form.get("password");

            if (email == null || email.isBlank()
                    || password == null || password.isBlank()) {
                Eligibility.sendJson(exchange, 400,
                        "{\"success\":false,\"message\":\"Email and password are required\"}");
                return;
            }

            Student student = authenticate(email.trim(), password);

            if (student == null) {
                Eligibility.sendJson(exchange, 401,
                        "{\"success\":false,\"message\":\"Invalid email or password\"}");
                return;
            }

            String json = "{\"success\":true,"
                    + "\"message\":\"Login successful!\","
                    + "\"studentId\":" + student.getStudentId() + ","
                    + "\"name\":\"" + Eligibility.escapeJson(student.getName()) + "\","
                    + "\"email\":\"" + Eligibility.escapeJson(student.getEmail()) + "\","
                    + "\"branch\":\"" + Eligibility.escapeJson(student.getBranch()) + "\","
                    + "\"cgpa\":" + student.getCgpa() + ","
                    + "\"backlogs\":" + student.getBacklogs() + ","
                    + "\"attendance\":" + student.getAttendance()
                    + "}";

            Eligibility.sendJson(exchange, 200, json);

        } catch (Exception e) {
            e.printStackTrace();
            Eligibility.sendJson(exchange, 500,
                    "{\"success\":false,\"message\":\"Server error during login\"}");
        }
    }

    // REGISTRATION API
    public static void handleRegister(HttpExchange exchange)
            throws IOException {

        Eligibility.addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            Eligibility.sendJson(exchange, 405,
                    "{\"success\":false,\"message\":\"POST required\"}");
            return;
        }

        try {
            String body = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            Map<String, String> form = Eligibility.parseForm(body);

            String name = form.get("name");
            String email = form.get("email");
            String password = form.get("password");
            String branch = form.get("branch");

            if (name == null || name.isBlank()
                    || email == null || email.isBlank()
                    || password == null || password.length() < 6
                    || branch == null || branch.isBlank()) {

                Eligibility.sendJson(exchange, 400,
                        "{\"success\":false,\"message\":\"Fill all required fields; password must be at least 6 characters\"}");
                return;
            }

            double cgpa = 0.0;
            int backlogs = 0;
            double attendance = 0.0;

            try {
                String value = form.get("cgpa");
                if (value != null && !value.isBlank()) {
                    cgpa = Double.parseDouble(value.trim());
                }

                value = form.get("backlogs");
                if (value != null && !value.isBlank()) {
                    backlogs = Integer.parseInt(value.trim());
                }

                value = form.get("attendance");
                if (value != null && !value.isBlank()) {
                    attendance = Double.parseDouble(value.trim());
                }
            } catch (NumberFormatException e) {
                Eligibility.sendJson(exchange, 400,
                        "{\"success\":false,\"message\":\"Enter valid academic details\"}");
                return;
            }

            if (cgpa < 0 || cgpa > 10
                    || backlogs < 0
                    || attendance < 0 || attendance > 100) {
                Eligibility.sendJson(exchange, 400,
                        "{\"success\":false,\"message\":\"Invalid CGPA, backlogs, or attendance\"}");
                return;
            }

            Student student = new Student(
                    name.trim(),
                    email.trim(),
                    password,
                    branch.trim(),
                    cgpa,
                    backlogs,
                    attendance
            );

            boolean registered = Register.registerStudent(student);

            if (registered) {
                Eligibility.sendJson(exchange, 201,
                        "{\"success\":true,\"message\":\"Account created successfully! You can now log in.\"}");
            } else {
                Eligibility.sendJson(exchange, 400,
                        "{\"success\":false,\"message\":\"Registration failed. This email may already be in use.\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Eligibility.sendJson(exchange, 500,
                    "{\"success\":false,\"message\":\"Server error during registration\"}");
        }
    }

    // DATABASE SE LOGIN CHECK
    private static Student authenticate(String email, String password) {

        String sql = "SELECT * FROM students "
                + "WHERE LOWER(email) = LOWER(?) AND password = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return null;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, email);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        Student student = new Student();

                        student.setStudentId(rs.getInt("student_id"));
                        student.setName(rs.getString("name"));
                        student.setEmail(rs.getString("email"));
                        student.setBranch(rs.getString("branch"));
                        student.setCgpa(rs.getDouble("cgpa"));
                        student.setBacklogs(rs.getInt("backlogs"));
                        student.setAttendance(rs.getDouble("attendance"));

                        return student;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}