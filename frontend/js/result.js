document.addEventListener("DOMContentLoaded", function () {
    const summary = document.getElementById("resultSummary");
    const results = document.getElementById("results");
    const noResults = document.getElementById("noResults");

    // Check whether required HTML elements exist
    if (!summary || !results || !noResults) {
        console.error("Result page elements not found!");
        return;
    }

    // Get saved eligibility results
    const savedResults = sessionStorage.getItem("eligibilityResults");

    // Show message if no results are available
    if (!savedResults) {
        noResults.style.display = "block";
        return;
    }

    try {
        const data = JSON.parse(savedResults);

        // Display student name
        const summaryText = document.createElement("p");
        summaryText.textContent = "Student: Demo Student";
        summary.appendChild(summaryText);

        // Display student details
        const details = document.createElement("p");
        details.textContent =
            "CGPA: " + data.cgpa +
            " | Backlogs: " + data.backlogs +
            " | Attendance: " + data.attendance + "%";
        summary.appendChild(details);

        // Display number of eligible companies
        const eligibleCount = document.createElement("p");
        eligibleCount.textContent =
            "Eligible Companies: " + data.eligibleCount +
            " out of " + data.companies.length;
        summary.appendChild(eligibleCount);

        // Display date and time of eligibility check
        if (data.checkedAt) {
            const checkedAt = document.createElement("p");
            checkedAt.textContent = "Checked on: " + data.checkedAt;
            summary.appendChild(checkedAt);
        }

        // Display company results
        const heading = document.createElement("h3");
        heading.textContent = "Eligibility Results";
        results.appendChild(heading);

        data.companies.forEach(function (company) {
            const card = document.createElement("div");
            card.className = "dashboard-card";

            const name = document.createElement("h3");
            name.textContent = company.name;

            const status = document.createElement("p");
            status.textContent = company.eligible
                ? "You are eligible!"
                : "You are not eligible.";

            const requirements = document.createElement("p");
            requirements.textContent =
                "Minimum CGPA: " + company.minCgpa +
                " | Maximum backlogs: " + company.maxBacklogs +
                " | Minimum attendance: " + company.minAttendance + "%";

            card.appendChild(name);
            card.appendChild(status);
            card.appendChild(requirements);

            results.appendChild(card);
        });

    } catch (error) {
        console.error("Could not load saved results:", error);
        noResults.textContent =
            "Unable to load results. Please check your eligibility again.";
        noResults.style.display = "block";
    }
});