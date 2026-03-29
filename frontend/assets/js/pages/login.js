document.addEventListener("DOMContentLoaded", function() {
    const loginForm = document.querySelector("form");
    if (!loginForm) return;

    loginForm.addEventListener("submit", async function(e) {
        e.preventDefault();

        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;

        try {
            const result = await ApiClient.post("/users/login", { username, password });
            if (result === "Login Successful") {
                localStorage.setItem("currentUser", username);
                window.location.href = "dashboard.html";
            } else {
                alert("Login failed. Please check your credentials.");
            }
        } catch (error) {
            console.error("Login error:", error);
            alert("Login failed. Please try again.");
        }
    });
});