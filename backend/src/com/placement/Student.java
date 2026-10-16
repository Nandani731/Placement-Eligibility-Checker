package com.placement;

public class Student {

    private int studentId;
    private String name;
    private String email;
    private String password;
    private String branch;
    private double cgpa;
    private int backlogs;
    private double attendance;
    private String createdAt;

    public Student() {
    }

    public Student(String name, String email, String password,
                   String branch, double cgpa, int backlogs,
                   double attendance) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.branch = branch;
        this.cgpa = cgpa;
        this.backlogs = backlogs;
        this.attendance = attendance;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBranch() {
        return branch != null ? branch : "CSE";
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public int getBacklogs() {
        return backlogs;
    }

    public void setBacklogs(int backlogs) {
        this.backlogs = backlogs;
    }

    public double getAttendance() {
        return attendance;
    }

    public void setAttendance(double attendance) {
        this.attendance = attendance;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}