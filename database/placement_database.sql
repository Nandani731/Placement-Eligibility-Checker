-- =======================================================
-- Placement Eligibility Checker Database Schema & Seeds
-- =======================================================

CREATE DATABASE IF NOT EXISTS placement_db;
USE placement_db;

-- 1. Students Table
CREATE TABLE IF NOT EXISTS students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    branch VARCHAR(50) NOT NULL DEFAULT 'CSE',
    cgpa DECIMAL(4,2) DEFAULT 0.00,
    backlogs INT DEFAULT 0,
    attendance DECIMAL(5,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Companies Table
CREATE TABLE IF NOT EXISTS companies (
    company_id INT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(100) NOT NULL UNIQUE,
    job_role VARCHAR(100) NOT NULL DEFAULT 'Software Engineer',
    package_lpa DECIMAL(5,2) NOT NULL DEFAULT 4.50,
    min_cgpa DECIMAL(4,2) NOT NULL,
    max_backlogs INT NOT NULL,
    min_attendance DECIMAL(5,2) NOT NULL,
    eligible_branches VARCHAR(255) NOT NULL DEFAULT 'ALL',
    location VARCHAR(100) NOT NULL DEFAULT 'Pan India',
    logo_icon VARCHAR(50) NOT NULL DEFAULT '🏢',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Eligibility Results / History Table
CREATE TABLE IF NOT EXISTS eligibility_results (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    company_id INT NOT NULL,
    eligible BOOLEAN NOT NULL,
    student_cgpa DECIMAL(4,2) DEFAULT 0.00,
    student_backlogs INT DEFAULT 0,
    student_attendance DECIMAL(5,2) DEFAULT 0.00,
    result_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES companies(company_id) ON DELETE CASCADE
);

-- 4. Seed Comprehensive Company Criteria (Top MNCs & Tech Giants)
INSERT INTO companies
    (company_name, job_role, package_lpa, min_cgpa, max_backlogs, min_attendance, eligible_branches, location, logo_icon)
VALUES
    ('Google', 'Associate Software Engineer', 28.50, 8.50, 0, 80.00, 'CSE, IT, ECE', 'Bangalore / Hyderabad', '🌐'),
    ('Microsoft', 'Software Development Engineer', 24.00, 8.00, 0, 75.00, 'CSE, IT, ECE', 'Hyderabad / Noida', '💻'),
    ('Amazon', 'SDE-1 (Graduate)', 22.00, 7.50, 0, 75.00, 'CSE, IT, ECE, EE', 'Bangalore / Pune', '📦'),
    ('Accenture', 'Associate Software Engineer', 6.50, 7.00, 0, 75.00, 'ALL', 'Pan India', '⚡'),
    ('TCS Ninja', 'Assistant System Engineer', 4.00, 6.00, 1, 70.00, 'ALL', 'Pan India', '🚀'),
    ('TCS Digital', 'Systems Engineer (Digital)', 7.50, 7.50, 0, 75.00, 'CSE, IT, ECE', 'Pan India', '✨'),
    ('Infosys', 'Systems Engineer Specialist', 6.25, 6.50, 0, 75.00, 'ALL', 'Bangalore / Pune / Mysore', '🔷'),
    ('Wipro', 'Project Engineer (Turbo)', 6.50, 6.50, 1, 70.00, 'ALL', 'Pan India', '🌈'),
    ('Cognizant', 'GenC Next Engineer', 6.75, 6.75, 0, 75.00, 'ALL', 'Chennai / Pune', '💡'),
    ('Capgemini', 'Analyst & Software Engineer', 5.75, 6.00, 0, 70.00, 'ALL', 'Mumbai / Bangalore', '🛡️'),
    ('Deloitte', 'Technology Consultant', 8.20, 7.20, 0, 75.00, 'CSE, IT, ECE', 'Hyderabad / Gurgaon', '🟢'),
    ('HCLTech', 'Graduate Engineer Trainee', 4.75, 6.00, 1, 65.00, 'ALL', 'Noida / Chennai', '⚙️')
ON DUPLICATE KEY UPDATE
    job_role = VALUES(job_role),
    package_lpa = VALUES(package_lpa),
    min_cgpa = VALUES(min_cgpa),
    max_backlogs = VALUES(max_backlogs),
    min_attendance = VALUES(min_attendance),
    eligible_branches = VALUES(eligible_branches),
    location = VALUES(location),
    logo_icon = VALUES(logo_icon);

-- 5. Seed Demo Students
INSERT INTO students
    (name, email, password, branch, cgpa, backlogs, attendance)
VALUES
    ('Rahul Sharma', 'rahul@example.com', 'rahul123', 'CSE', 7.80, 0, 82.00),
    ('Priya Patel', 'priya@example.com', 'priya123', 'IT', 8.60, 0, 88.50)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    branch = VALUES(branch),
    cgpa = VALUES(cgpa),
    backlogs = VALUES(backlogs),
    attendance = VALUES(attendance);

SELECT company_id, company_name, job_role, package_lpa, min_cgpa, max_backlogs, min_attendance FROM companies;