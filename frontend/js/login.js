document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("loginForm");
    const message = document.getElementById("message");

    if (!loginForm || !message) {
        console.error("Login form elements not found.");
        return;
    }

    loginForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;

        if (!email || !password) {
            message.textContent = "Please enter email and password.";
            return;
        }

        message.textContent = "Logging in...";

        try {
            const response = await fetch("http://localhost:8080/api/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
                },
                body: new URLSearchParams({
                    email: email,
                    password: password
                }).toString()
            });

            const data = await response.json();

            if (response.ok && data.success) {
                message.textContent =
                    "Login successful! Welcome, " + data.name + ".";

                // Student details save karna
                sessionStorage.setItem("studentId", String(data.studentId));
                sessionStorage.setItem("studentName", data.name);
                sessionStorage.setItem("studentEmail", data.email);

                // Dashboard open karna
                setTimeout(function () {
                    window.location.href = "dashboard.html";
                }, 1000);

            } else {
                message.textContent =
                    data.message || "Login failed. Please try again.";
            }

        } catch (error) {
            console.error("Login error:", error);
            message.textContent =
                "Backend se connection nahi ho raha. Please check the server.";
        }
    });
});