document.addEventListener("DOMContentLoaded", function () {
    const signupForm = document.getElementById("signupForm");
    const signupError = document.getElementById("signupError");
    const signupSuccess = document.getElementById("signupSuccess");

    if (!signupForm) {
        return;
    }

    signupForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        const name = document.getElementById("name").value.trim();
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;
        const confirmPassword = document.getElementById("confirmPassword").value;

        if (signupError) {
            signupError.style.display = "none";
            signupError.textContent = "";
        }
        if (signupSuccess) {
            signupSuccess.style.display = "none";
            signupSuccess.textContent = "";
        }

        if (password !== confirmPassword) {
            if (signupError) {
                signupError.textContent = "Passwords do not match.";
                signupError.style.display = "block";
            }
            return;
        }

        try {
            const response = await ApiClient.post("/users/register", { name, email, password });
            const user = response.user || response;
            const sessionUser = Auth.storeSession(user);

            if (signupSuccess) {
                signupSuccess.textContent = "Account created successfully. Redirecting to your portal...";
                signupSuccess.style.display = "block";
            }

            window.location.href = Auth.getRoleRedirectPath(sessionUser.role);
        } catch (error) {
            console.error("Signup error:", error);
            if (signupError) {
                signupError.textContent = error.message || "Unable to create account.";
                signupError.style.display = "block";
            }
        }
    });
});
