document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("resetPasswordForm");
    const resetError = document.getElementById("resetError");
    const resetSuccess = document.getElementById("resetSuccess");
    const tokenInput = document.getElementById("token");

    if (!form) {
        return;
    }

    const queryToken = new URLSearchParams(window.location.search).get("token");
    if (queryToken && tokenInput) {
        tokenInput.value = queryToken;
    }

    form.addEventListener("submit", async function (event) {
        event.preventDefault();

        const token = tokenInput.value.trim();
        const newPassword = document.getElementById("newPassword").value;
        const confirmPassword = document.getElementById("confirmPassword").value;

        if (resetError) {
            resetError.style.display = "none";
            resetError.textContent = "";
        }
        if (resetSuccess) {
            resetSuccess.style.display = "none";
            resetSuccess.textContent = "";
        }

        if (newPassword !== confirmPassword) {
            if (resetError) {
                resetError.textContent = "Passwords do not match.";
                resetError.style.display = "block";
            }
            return;
        }

        try {
            const response = await ApiClient.post("/users/reset-password", {
                token,
                newPassword,
                confirmPassword
            });

            if (resetSuccess) {
                resetSuccess.textContent = response.message || "Password updated successfully.";
                resetSuccess.style.display = "block";
            }

            setTimeout(function () {
                window.location.href = "login.html";
            }, 1200);
        } catch (error) {
            console.error("Reset password error:", error);
            if (resetError) {
                resetError.textContent = error.message || "Unable to reset password.";
                resetError.style.display = "block";
            }
        }
    });
});
