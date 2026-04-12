document.addEventListener("DOMContentLoaded", function() {
    const qaResults = document.getElementById("qa-results");
    const statusBanner = document.getElementById("report-status");
    const refreshButton = document.getElementById("refresh-report-btn");
    const exportButton = document.getElementById("export-report-btn");

    if (refreshButton) {
        refreshButton.addEventListener("click", generateReport);
    }

    if (exportButton) {
        exportButton.addEventListener("click", exportCsv);
    }

    generateReport();

    async function generateReport() {
        const currentUser = await Auth.loadCurrentUser();
        const currentUserId = currentUser ? currentUser.userId || currentUser.email : "SYSTEM";

        try {
            const result = await ApiClient.post(`/reports/daily?userId=${encodeURIComponent(currentUserId)}`, {});
            showResults(result);
            if (statusBanner) {
                statusBanner.innerHTML = `<strong>Report ready:</strong> ${result}`;
            }
        } catch (error) {
            console.error("Report generation error:", error);
            showResults("Report generation failed.");
            if (statusBanner) {
                statusBanner.textContent = "Report refresh failed.";
            }
        }
    }

    async function exportCsv() {
        const currentUser = await Auth.loadCurrentUser();
        const currentUserId = currentUser ? currentUser.userId || currentUser.email : "SYSTEM";

        try {
            const csvText = await ApiClient.get(`/reports/daily/csv?userId=${encodeURIComponent(currentUserId)}`);
            const blob = new Blob([csvText], { type: "text/csv;charset=utf-8;" });
            const url = URL.createObjectURL(blob);
            const link = document.createElement("a");
            link.href = url;
            link.download = "bpo-service-connect-report.csv";
            document.body.appendChild(link);
            link.click();
            link.remove();
            URL.revokeObjectURL(url);
        } catch (error) {
            console.error("CSV export failed:", error);
            if (statusBanner) {
                statusBanner.textContent = "CSV export failed.";
            }
        }
    }

    function showResults(text) {
        if (qaResults) {
            qaResults.innerHTML = `<strong>${text}</strong>`;
            qaResults.style.display = "block";
        }
    }
});