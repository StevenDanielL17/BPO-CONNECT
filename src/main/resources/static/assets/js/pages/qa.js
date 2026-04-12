document.addEventListener("DOMContentLoaded", function() {
    const loadEvaluationsBtn = document.getElementById("load-evaluations-btn");
    const qaSearchAgentIdInput = document.getElementById("qa-search-agent-id");
    const qaAgentIdInput = document.getElementById("qa-agent-id");
    const qaResults = document.getElementById("qa-results");
    const submitButton = document.getElementById("submit-qa-btn");
    const flagButton = document.getElementById("flag-qa-btn");
    const statusBanner = document.getElementById("qa-status");
    const refreshButton = document.getElementById("refresh-qa-btn");

    if (loadEvaluationsBtn) {
        loadEvaluationsBtn.addEventListener("click", loadEvaluations);
    }

    if (refreshButton) {
        refreshButton.addEventListener("click", loadEvaluations);
    }

    if (submitButton) {
        submitButton.addEventListener("click", submitEvaluation);
    }

    if (flagButton) {
        flagButton.addEventListener("click", flagLatestEvaluation);
    }

    async function loadEvaluations() {
        const currentUser = Auth.checkAuthentication();
        const agentId = (qaSearchAgentIdInput && qaSearchAgentIdInput.value) || (qaAgentIdInput && qaAgentIdInput.value) || (currentUser ? currentUser.userId || currentUser.email : "A101");

        try {
            const evals = await ApiClient.get(`/evaluations/${agentId}`);
            renderEvaluations(evals);
            if (statusBanner) {
                statusBanner.innerHTML = `<strong>Loaded:</strong> ${evals.length} evaluation(s) for ${agentId}.`;
            }
        } catch (error) {
            console.error("Evaluations load error:", error);
            showResults("Unable to load evaluations.");
            if (statusBanner) {
                statusBanner.textContent = "Evaluation lookup failed.";
            }
        }
    }

    function renderEvaluations(evals) {
        if (!qaResults) return;

        if (evals.length > 0) {
            const html = `
                <table class="table">
                    <thead>
                        <tr><th>Ticket</th><th>Score</th><th>Feedback</th><th>Status</th></tr>
                    </thead>
                    <tbody>
                        ${evals.map(function (entry) {
                            return `<tr>
                                <td>${entry.ticketId || "-"}</td>
                                <td>${entry.score ?? 0}</td>
                                <td>${entry.feedback || "-"}</td>
                                <td>${entry.flaggedForCoaching ? "Flagged" : "Reviewed"}</td>
                            </tr>`;
                        }).join("")}
                    </tbody>
                </table>`;
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

    async function submitEvaluation() {
        const payload = {
            ticketId: document.getElementById("qa-ticket-id").value.trim(),
            agentId: document.getElementById("qa-agent-id").value.trim(),
            evaluatorId: document.getElementById("qa-evaluator-id").value.trim() || "QA",
            score: document.getElementById("qa-score").value,
            feedback: document.getElementById("qa-feedback").value.trim()
        };

        try {
            const evaluation = await ApiClient.post("/evaluations", payload);
            if (statusBanner) {
                statusBanner.innerHTML = `<strong>Saved:</strong> Evaluation ${evaluation.evalId} recorded.`;
            }
            await loadEvaluations();
        } catch (error) {
            console.error("QA submission error:", error);
            if (statusBanner) {
                statusBanner.textContent = error.message || "Unable to save evaluation.";
            }
        }
    }

    async function flagLatestEvaluation() {
        const ticketId = document.getElementById("qa-ticket-id").value.trim();
        if (!ticketId) {
            if (statusBanner) {
                statusBanner.textContent = "Enter a ticket ID before flagging for coaching.";
            }
            return;
        }

        try {
            const agentId = document.getElementById("qa-agent-id").value.trim();
            const evals = await ApiClient.get(`/evaluations/${agentId}`);
            const latest = evals[0];
            if (!latest) {
                if (statusBanner) {
                    statusBanner.textContent = "No evaluation found to flag.";
                }
                return;
            }

            await ApiClient.post(`/evaluations/${latest.evalId}/flag`, {
                coachingNotes: "Flagged from QA dashboard"
            });
            if (statusBanner) {
                statusBanner.innerHTML = `<strong>Coaching:</strong> Evaluation ${latest.evalId} flagged.`;
            }
            await loadEvaluations();
        } catch (error) {
            console.error("Flag for coaching error:", error);
            if (statusBanner) {
                statusBanner.textContent = error.message || "Unable to flag evaluation.";
            }
        }
    }
});