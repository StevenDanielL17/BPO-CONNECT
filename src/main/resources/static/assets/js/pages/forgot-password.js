document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("forgotPasswordForm");
    const forgotError = document.getElementById("forgotError");
    const forgotSuccess = document.getElementById("forgotSuccess");
    const resetTokenBox = document.getElementById("resetTokenBox");

    if (!form) {
        return;
    }

    form.addEventListener("submit", async function (event) {
        event.preventDefault();

        const email = document.getElementById("email").value.trim();

        if (forgotError) {
            forgotError.style.display = "none";
            forgotError.textContent = "";
        }
        if (forgotSuccess) {
            forgotSuccess.style.display = "none";
            forgotSuccess.textContent = "";
        }
        if (resetTokenBox) {
            resetTokenBox.style.display = "none";
            resetTokenBox.textContent = "";
        }

        try {
            const response = await ApiClient.post("/users/forgot-password", { email });
            const token = response.resetToken;

            if (forgotSuccess) {
                forgotSuccess.textContent = response.message || "Reset token generated.";
                forgotSuccess.style.display = "block";
            }

            if (resetTokenBox) {
                resetTokenBox.innerHTML = `Reset token: <strong>${token}</strong><br><a href="reset-password.html?token=${encodeURIComponent(token)}">Open reset page</a>`;
                resetTokenBox.style.display = "block";
            }
        } catch (error) {
            console.error("Forgot password error:", error);
            if (forgotError) {
                forgotError.textContent = error.message || "Unable to generate reset token.";
                forgotError.style.display = "block";
            }
        }
    });
});
