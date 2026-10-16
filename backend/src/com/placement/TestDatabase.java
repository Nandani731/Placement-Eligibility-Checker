package com.placement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDatabase {

    public static void main(String[] args) {
        System.out.println("Testing database connection to placement_db...");

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.err.println("Database connection failed: Connection is null.");
                return;
            }

            System.out.println("Database connected successfully!\n");

            String sql = "SELECT company_name, job_role, package_lpa, min_cgpa, max_backlogs, min_attendance FROM companies ORDER BY package_lpa DESC";

            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                System.out.println("==========================================================================");
                System.out.printf("%-15s %-30s %-10s %-8s %-10s %-10s%n", "Company", "Role", "Package", "Min CGPA", "Max Back", "Min Att%");
                System.out.println("==========================================================================");

                while (rs.next()) {
                    System.out.printf("%-15s %-30s %-10s %-8.2f %-10d %-10.1f%n",
                            rs.getString("company_name"),
                            rs.getString("job_role"),
                            rs.getDouble("package_lpa") + " LPA",
                            rs.getDouble("min_cgpa"),
                            rs.getInt("max_backlogs"),
                            rs.getDouble("min_attendance"));
                }
                System.out.println("==========================================================================");
            }

            // Test students
            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT student_id, name, email, branch, cgpa FROM students")) {
                System.out.println("\nRegistered Students:");
                while (rs.next()) {
                    System.out.println(" • #" + rs.getInt("student_id") + " " + rs.getString("name")
                            + " (" + rs.getString("email") + ") | Branch: " + rs.getString("branch")
                            + " | CGPA: " + rs.getDouble("cgpa"));
                }
            }

        } catch (Exception e) {
            System.err.println("Error testing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
