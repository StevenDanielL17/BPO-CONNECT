document.addEventListener("DOMContentLoaded", function() {
    const loadEvaluationsBtn = document.getElementById("load-evaluations-btn");
    const qaAgentIdInput = document.getElementById("qa-agent-id");
    const qaResults = document.getElementById("qa-results");

    if (loadEvaluationsBtn) {
        loadEvaluationsBtn.addEventListener("click", loadEvaluations);
    }

    async function loadEvaluations() {
        const agentId = qaAgentIdInput.value || localStorage.getItem("currentUser") || "A101";

        try {
            const evals = await ApiClient.get(`/evaluations/${agentId}`);
            renderEvaluations(evals);
        } catch (error) {
            console.error("Evaluations load error:", error);
            showResults("Unable to load evaluations.");
        }
    }

    function renderEvaluations(evals) {
        if (!qaResults) return;

        if (evals.length > 0) {
            const html = evals.map(entry => `<strong>Score: ${entry.score}</strong><br>Feedback: ${entry.feedback}<hr>`).join("");
            showResults(html);
        } else {
            showResults("No evaluations found for this agent.");
        }
    }

    function showResults(html) {
        if (qaResults) {
            qaResults.innerHTML = html;
            qaResults.style.display = "block";
        }
    }
});