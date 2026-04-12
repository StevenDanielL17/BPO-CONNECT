document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("loginForm");
    const loginError = document.getElementById("loginError");
    const demoButtons = document.querySelectorAll("[data-demo-login]");

    if (!loginForm) {
        return;
    }

    loginForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;

        if (loginError) {
            loginError.textContent = "";
            loginError.style.display = "none";
        }

        try {
            const response = await ApiClient.post("/users/login", { email, password });
            const user = response.user || response;
            const sessionUser = Auth.storeSession(user);
            window.location.href = Auth.getRoleRedirectPath(sessionUser.role);
        } catch (error) {
            console.error("Login error:", error);
            if (loginError) {
                loginError.textContent = error.message || "Invalid email or password.";
                loginError.style.display = "block";
            }
        }
    });

    demoButtons.forEach(function (button) {
        button.addEventListener("click", async function () {
            if (loginError) {
                loginError.textContent = "";
                loginError.style.display = "none";
            }

            const email = button.getAttribute("data-email");
            const password = button.getAttribute("data-password");

            try {
                const response = await ApiClient.post("/users/login", { email, password });
                const user = response.user || response;
                const sessionUser = Auth.storeSession(user);
                window.location.href = Auth.getRoleRedirectPath(sessionUser.role);
            } catch (error) {
                console.error("Demo login error:", error);
                if (loginError) {
                    loginError.textContent = error.message || "Demo login failed.";
                    loginError.style.display = "block";
                }
            }
        });
    });
});