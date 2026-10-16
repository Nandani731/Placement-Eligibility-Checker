document.addEventListener("DOMContentLoaded", function () {

    const registerForm = document.getElementById("registerForm");
    const message = document.getElementById("message");

    if (!registerForm || !message) {
        console.error("Registration form not found.");
        return;
    }

    registerForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        const name = document.getElementById("name").value.trim();
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;
        const branch = document.getElementById("branch").value;

        if (!name || !email || !password || !branch) {
            message.textContent = "Please fill in all fields.";
            return;
        }

        if (password.length < 6) {
            message.textContent =
                "Password must be at least 6 characters.";
            return;
        }

        const formData = new URLSearchParams();

        formData.append("name", name);
        formData.append("email", email);
        formData.append("password", password);
        formData.append("branch", branch);

        message.textContent = "Registering...";

        try {

            const response = await fetch(
                "http://localhost:8080/api/register",
                {
                    method: "POST",
                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded;charset=UTF-8"
                    },
                    body: formData.toString()
                }
            );

            const data = await response.json();

            if (response.ok && data.success) {

                message.textContent =
                    "Registration successful! Redirecting to login...";

                registerForm.reset();

                setTimeout(function () {
                    window.location.href = "login.html";
                }, 1500);

            } else {

                message.textContent =
                    data.message || "Registration failed.";
            }

        } catch (error) {

            console.error("Registration error:", error);

            message.textContent =
                "Unable to connect to server. Please check that Java server is running.";
        }
    });

});