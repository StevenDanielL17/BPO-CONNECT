document.addEventListener("DOMContentLoaded", function () {
    const refreshButton = document.getElementById("agent-home-refresh");

    if (refreshButton) {
        refreshButton.addEventListener("click", loadAgentHome);
    }

    loadAgentHome();

    async function loadAgentHome() {
        const summaryEl = document.getElementById("agent-home-summary");
        const queueEl = document.getElementById("agent-home-queue");
        const guidanceEl = document.getElementById("agent-home-guidance");

        try {
            const [queueStats, articles] = await Promise.all([
                ApiClient.get("/queue/stats"),
                ApiClient.get("/kb/articles")
            ]);

            document.getElementById("agent-home-waiting").textContent = String(queueStats.waitingCount || 0);
            document.getElementById("agent-home-progress").textContent = String(queueStats.inProgressCount || 0);
            document.getElementById("agent-home-escalated").textContent = String(queueStats.escalatedCount || 0);
            document.getElementById("agent-home-kb-count").textContent = String(Array.isArray(articles) ? articles.length : 0);

            if (summaryEl) {
                summaryEl.textContent = `Queue ready. ${queueStats.waitingCount || 0} waiting, ${queueStats.inProgressCount || 0} in progress, ${queueStats.escalatedCount || 0} escalated.`;
            }

            const activeTickets = Array.isArray(queueStats.activeTickets) ? queueStats.activeTickets : [];
            queueEl.innerHTML = activeTickets.length === 0
                ? '<div class="status-banner">No active tickets in the queue.</div>'
                : activeTickets.slice(0, 5).map(function (ticket) {
                    return `
                        <article class="queue-card">
                            <div class="queue-card-head">
                                <strong>${escapeHtml(ticket.ticketId || 'Ticket')}</strong>
                                <span class="tag ${getSeverityClass(ticket.severity)}">${escapeHtml(ticket.severity || 'General')}</span>
                            </div>
                            <p>Customer: ${escapeHtml(ticket.customerId || 'Unassigned')}</p>
                            <p>Status: <strong>${escapeHtml(ticket.status || 'New')}</strong></p>
                        </article>
                    `;
                }).join("");

            guidanceEl.innerHTML = [
                `
                    <div class="timeline-item">
                        <strong>Calls first</strong>
                        <div>Use the live queue and screen pop to identify the customer, then create or reopen the ticket.</div>
                    </div>
                `,
                `
                    <div class="timeline-item">
                        <strong>Knowledge base</strong>
                        <div>Search KB articles and insert the right fix into the ticket creation flow.</div>
                    </div>
                `,
                `
                    <div class="timeline-item">
                        <strong>Escalation path</strong>
                        <div>Escalate or transfer the active ticket from the Calls page when the issue needs a different owner.</div>
                    </div>
                `
            ].join("");
        } catch (error) {
            console.error("Agent home load error:", error);
            if (summaryEl) {
                summaryEl.textContent = error.message || "Unable to load agent snapshot.";
            }
            if (queueEl) {
                queueEl.innerHTML = '<div class="status-banner">Agent queue unavailable.</div>';
            }
        }
    }

    function getSeverityClass(severity) {
        const value = (severity || "").toLowerCase();
        if (value === "critical") return "critical";
        if (value === "high") return "high";
        if (value === "medium") return "medium";
        return "low";
    }

    function escapeHtml(value) {
        return String(value ?? "")
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/\"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }
});