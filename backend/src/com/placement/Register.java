package com.placement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Register {

    public static boolean registerStudent(Student student) {
        if (student == null) {
            System.err.println("Registration failed: Student data is missing.");
            return false;
        }

        if (student.getName() == null || student.getName().isBlank()
                || student.getEmail() == null || student.getEmail().isBlank()
                || student.getPassword() == null || student.getPassword().isBlank()
                || student.getBranch() == null || student.getBranch().isBlank()) {
            System.err.println("Registration failed: Required fields are missing.");
            return false;
        }

        String sql = "INSERT INTO students "
                + "(name, email, password, branch, cgpa, backlogs, attendance) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.err.println("Registration failed: Database connection unavailable.");
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, student.getName().trim());
                ps.setString(2, student.getEmail().trim().toLowerCase());
                ps.setString(3, student.getPassword());
                ps.setString(4, student.getBranch());
                ps.setDouble(5, Math.max(0.0, student.getCgpa()));
                ps.setInt(6, Math.max(0, student.getBacklogs()));
                ps.setDouble(7, Math.max(0.0, student.getAttendance()));

                int rows = ps.executeUpdate();
                return rows == 1;
            }

        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState()) || e.getErrorCode() == 1062) {
                System.err.println("Registration failed: Email is already registered (" + student.getEmail() + ")");
            } else {
                System.err.println("Database error during registration: " + e.getMessage());
            }
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected registration error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}