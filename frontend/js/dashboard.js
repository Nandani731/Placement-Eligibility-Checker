document.addEventListener("DOMContentLoaded", function () {

    const studentName = sessionStorage.getItem("studentName");
    const studentEmail = sessionStorage.getItem("studentEmail");

    // Login check
    if (!studentName || !studentEmail) {
        window.location.href = "login.html";
        return;
    }

    // Show student information
    const nameElement = document.getElementById("studentName");
    const emailElement = document.getElementById("studentEmail");

    if (nameElement) {
        nameElement.textContent = studentName;
    }

    if (emailElement) {
        emailElement.textContent = studentEmail;
    }

    // Check Eligibility button
    const eligibilityButton = document.getElementById("checkEligibilityBtn");

    if (eligibilityButton) {
        eligibilityButton.addEventListener("click", function () {
            window.location.href = "eligibility.html";
        });
    }

    // View Results button
    const resultsButton = document.getElementById("viewResultsBtn");

    if (resultsButton) {
        resultsButton.addEventListener("click", function () {
            window.location.href = "result.html";
        });
    }

    // Logout button
    const logoutButton = document.getElementById("logoutBtn");

    if (logoutButton) {
        logoutButton.addEventListener("click", function () {
            sessionStorage.removeItem("studentName");
            sessionStorage.removeItem("studentEmail");
            sessionStorage.removeItem("eligibilityResults");

            window.location.href = "login.html";
        });
    }

});