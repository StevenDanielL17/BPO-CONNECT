document.addEventListener("DOMContentLoaded", function() {
    const generateReportBtn = document.getElementById("generate-report-btn");
    const qaResults = document.getElementById("qa-results");

    if (generateReportBtn) {
        generateReportBtn.addEventListener("click", generateReport);
    }

    async function generateReport() {
        const currentUser = localStorage.getItem("currentUser") || "A101";

        try {
            const result = await ApiClient.post(`/reports/daily?userId=${currentUser}`);
            showResults(`Report Engine<br>${result}`);
        } catch (error) {
            console.error("Report generation error:", error);
            showResults("Report generation failed.");
        }
    }

    function showResults(html) {
        if (qaResults) {
            qaResults.innerHTML = `<strong>${html}</strong>`;
            qaResults.style.display = "block";
        }
    }
});