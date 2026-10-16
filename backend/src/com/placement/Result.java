package com.placement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Result {

    private int resultId;
    private int studentId;
    private int companyId;
    private String companyName;
    private String jobRole;
    private double packageLpa;
    private double minCgpa;
    private int maxBacklogs;
    private double minAttendance;
    private boolean eligible;
    private double studentCgpa;
    private int studentBacklogs;
    private double studentAttendance;
    private String resultDate;

    public Result() {
    }

    public Result(int studentId, int companyId, boolean eligible,
                  double studentCgpa, int studentBacklogs, double studentAttendance) {
        this.studentId = studentId;
        this.companyId = companyId;
        this.eligible = eligible;
        this.studentCgpa = studentCgpa;
        this.studentBacklogs = studentBacklogs;
        this.studentAttendance = studentAttendance;
    }

    public int getResultId() { return resultId; }
    public void setResultId(int resultId) { this.resultId = resultId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }

    public double getPackageLpa() { return packageLpa; }
    public void setPackageLpa(double packageLpa) { this.packageLpa = packageLpa; }

    public double getMinCgpa() { return minCgpa; }
    public void setMinCgpa(double minCgpa) { this.minCgpa = minCgpa; }

    public int getMaxBacklogs() { return maxBacklogs; }
    public void setMaxBacklogs(int maxBacklogs) { this.maxBacklogs = maxBacklogs; }

    public double getMinAttendance() { return minAttendance; }
    public void setMinAttendance(double minAttendance) { this.minAttendance = minAttendance; }

    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }

    public double getStudentCgpa() { return studentCgpa; }
    public void setStudentCgpa(double studentCgpa) { this.studentCgpa = studentCgpa; }

    public int getStudentBacklogs() { return studentBacklogs; }
    public void setStudentBacklogs(int studentBacklogs) { this.studentBacklogs = studentBacklogs; }

    public double getStudentAttendance() { return studentAttendance; }
    public void setStudentAttendance(double studentAttendance) { this.studentAttendance = studentAttendance; }

    public String getResultDate() { return resultDate; }
    public void setResultDate(String resultDate) { this.resultDate = resultDate; }

    public static boolean saveResult(int studentId, int companyId, boolean eligible,
                                     double cgpa, int backlogs, double attendance) {
        String sql = "INSERT INTO eligibility_results "
                + "(student_id, company_id, eligible, student_cgpa, student_backlogs, student_attendance) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, companyId);
            ps.setBoolean(3, eligible);
            ps.setDouble(4, cgpa);
            ps.setInt(5, backlogs);
            ps.setDouble(6, attendance);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Could not save eligibility result: " + e.getMessage());
            return false;
        }
    }

    public static List<Result> getResultsByStudent(int studentId) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.result_id, r.student_id, r.company_id, r.eligible, "
                + "r.student_cgpa, r.student_backlogs, r.student_attendance, r.result_date, "
                + "c.company_name, c.job_role, c.package_lpa, c.min_cgpa, c.max_backlogs, c.min_attendance "
                + "FROM eligibility_results r "
                + "JOIN companies c ON r.company_id = c.company_id "
                + "WHERE r.student_id = ? "
                + "ORDER BY r.result_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Result res = new Result();
                    res.setResultId(rs.getInt("result_id"));
                    res.setStudentId(rs.getInt("student_id"));
                    res.setCompanyId(rs.getInt("company_id"));
                    res.setEligible(rs.getBoolean("eligible"));
                    res.setStudentCgpa(rs.getDouble("student_cgpa"));
                    res.setStudentBacklogs(rs.getInt("student_backlogs"));
                    res.setStudentAttendance(rs.getDouble("student_attendance"));
                    res.setResultDate(rs.getString("result_date"));
                    res.setCompanyName(rs.getString("company_name"));
                    res.setJobRole(rs.getString("job_role"));
                    res.setPackageLpa(rs.getDouble("package_lpa"));
                    res.setMinCgpa(rs.getDouble("min_cgpa"));
                    res.setMaxBacklogs(rs.getInt("max_backlogs"));
                    res.setMinAttendance(rs.getDouble("min_attendance"));
                    list.add(res);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching results for student " + studentId + ": " + e.getMessage());
        }
        return list;
    }
}