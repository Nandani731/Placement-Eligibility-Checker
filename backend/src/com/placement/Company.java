package com.placement;

public class Company {

    private int id;
    private String companyName;
    private String jobRole;
    private double packageLpa;
    private double minCgpa;
    private int maxBacklogs;
    private double minAttendance;
    private String eligibleBranches;
    private String location;
    private String logoIcon;

    public Company() {
    }

    public Company(int id, String companyName, String jobRole, double packageLpa,
                   double minCgpa, int maxBacklogs, double minAttendance,
                   String eligibleBranches, String location, String logoIcon) {
        this.id = id;
        this.companyName = companyName;
        this.jobRole = jobRole;
        this.packageLpa = packageLpa;
        this.minCgpa = minCgpa;
        this.maxBacklogs = maxBacklogs;
        this.minAttendance = minAttendance;
        this.eligibleBranches = eligibleBranches;
        this.location = location;
        this.logoIcon = logoIcon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobRole() {
        return jobRole != null ? jobRole : "Software Engineer";
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public double getPackageLpa() {
        return packageLpa;
    }

    public void setPackageLpa(double packageLpa) {
        this.packageLpa = packageLpa;
    }

    public double getMinCgpa() {
        return minCgpa;
    }

    public void setMinCgpa(double minCgpa) {
        this.minCgpa = minCgpa;
    }

    public int getMaxBacklogs() {
        return maxBacklogs;
    }

    public void setMaxBacklogs(int maxBacklogs) {
        this.maxBacklogs = maxBacklogs;
    }

    public double getMinAttendance() {
        return minAttendance;
    }

    public void setMinAttendance(double minAttendance) {
        this.minAttendance = minAttendance;
    }

    public String getEligibleBranches() {
        return eligibleBranches != null ? eligibleBranches : "ALL";
    }

    public void setEligibleBranches(String eligibleBranches) {
        this.eligibleBranches = eligibleBranches;
    }

    public String getLocation() {
        return location != null ? location : "Pan India";
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLogoIcon() {
        return logoIcon != null ? logoIcon : "🏢";
    }

    public void setLogoIcon(String logoIcon) {
        this.logoIcon = logoIcon;
    }

    public boolean isBranchEligible(String branch) {
        if (branch == null || branch.isBlank()) {
            return true;
        }
        String branches = getEligibleBranches().toUpperCase();
        if (branches.contains("ALL")) {
            return true;
        }
        return branches.contains(branch.trim().toUpperCase());
    }

    public boolean checkEligibility(double cgpa, int backlogs, double attendance) {
        return cgpa >= minCgpa
                && backlogs <= maxBacklogs
                && attendance >= minAttendance;
    }

    public boolean checkEligibility(double cgpa, int backlogs, double attendance, String branch) {
        return checkEligibility(cgpa, backlogs, attendance) && isBranchEligible(branch);
    }
}