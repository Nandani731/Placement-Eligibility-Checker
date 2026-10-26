document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("eligibilityForm");
    const button = document.getElementById("checkEligibility");
    const message = document.getElementById("message");
    const resultsBox = document.getElementById("results");

    if (!form || !button || !message || !resultsBox) {
        console.error("Eligibility HTML elements are missing.");
        return;
    }

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const cgpaText = document.getElementById("cgpa").value;
        const backlogsText = document.getElementById("backlogs").value;
        const attendanceText = document.getElementById("attendance").value;

        const cgpa = Number(cgpaText);
        const backlogs = Number(backlogsText);
        const attendance = Number(attendanceText);

        if (
            cgpaText === "" || backlogsText === "" || attendanceText === "" ||
            !Number.isFinite(cgpa) || cgpa < 0 || cgpa > 10 ||
            !Number.isInteger(backlogs) || backlogs < 0 ||
            !Number.isFinite(attendance) ||
            attendance < 0 || attendance > 100
        ) {
            message.textContent =
                "Enter CGPA (0–10), whole-number backlogs, and attendance (0–100).";
            return;
        }

        button.disabled = true;
        message.textContent = "Checking eligibility...";
        resultsBox.replaceChildren();

        try {
            const response = await fetch("http://localhost:8080/api/eligibility");

            if (!response.ok) {
                throw new Error("API returned HTTP " + response.status);
            }

            const companies = await response.json();

            if (!Array.isArray(companies) || companies.length === 0) {
                throw new Error("No companies returned by the API.");
            }

            const results = companies.map(company => ({
            companyId: company.company_id,
            name: company.name,
            min_cgpa: Number(company.min_cgpa),
            max_backlogs: Number(company.max_backlogs),
            min_attendance: Number(company.min_attendance),
        eligible:
        cgpa >= Number(company.min_cgpa) &&
        backlogs <= Number(company.max_backlogs) &&
        attendance >= Number(company.min_attendance)
}));

            results.forEach(result => {
                const card = document.createElement("div");
                card.style.padding = "12px";
                card.style.margin = "8px 0";
                card.style.border = "1px solid #ccc";
                card.style.borderRadius = "8px";

                const title = document.createElement("strong");
                title.textContent = result.name;

                const status = document.createElement("p");
                status.textContent = result.eligible
                    ? "Eligible"
                    : "Not Eligible";

                card.append(title, status);
                resultsBox.appendChild(card);
            });

            sessionStorage.setItem("eligibilityResults", JSON.stringify({
                cgpa, backlogs, attendance, results
            }));

            message.textContent = "Eligibility check completed.";

        } catch (error) {
            console.error("Eligibility check failed:", error);
            message.textContent =
                "Eligibility check failed: " + error.message;
        } finally {
            button.disabled = false;
        }
    });
});
