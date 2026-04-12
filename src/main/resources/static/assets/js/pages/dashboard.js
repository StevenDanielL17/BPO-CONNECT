document.addEventListener("DOMContentLoaded", async function () {
    const currentUser = await Auth.loadCurrentUser();
    const userEl = document.getElementById("current-user");
    if (userEl) userEl.textContent = currentUser ? currentUser.displayName : "Guest";

    const chatSendButton = document.getElementById("dashboard-chat-send");
    const chatRefreshButton = document.getElementById("dashboard-chat-refresh");

    if (chatSendButton) {
        chatSendButton.addEventListener("click", sendTeamMessage);
    }

    if (chatRefreshButton) {
        chatRefreshButton.addEventListener("click", loadTeamChat);
    }

    loadDashboardData();
    loadTeamChat();

    async function loadDashboardData() {
        try {
            const [tickets, queueStats] = await Promise.all([
                ApiClient.get("/tickets"),
                ApiClient.get("/queue/stats")
            ]);
            updateKPIs(tickets, queueStats);
            renderQueue(queueStats);
            renderTrendSummary(tickets, queueStats);
        } catch (error) {
            console.error("Failed to load dashboard data:", error);
        }
    }

    function updateKPIs(tickets, queueStats) {
        const openTickets = queueStats?.openCount ?? tickets.filter(ticket => ticket.status !== "Resolved").length;
        const escalated = queueStats?.escalatedCount ?? tickets.filter(ticket => ticket.status === "Escalated").length;

        const openEl = document.getElementById("kpi-open");
        const escalatedEl = document.getElementById("kpi-escalated");
        const summaryEl = document.getElementById("queue-summary");

        if (openEl) openEl.textContent = String(openTickets);
        if (escalatedEl) escalatedEl.textContent = String(escalated);
        if (summaryEl) {
            summaryEl.textContent = `Waiting: ${queueStats?.waitingCount ?? openTickets} | In progress: ${queueStats?.inProgressCount ?? 0} | Escalated: ${escalated}`;
        }
    }

    function renderQueue(queueStats) {
        const stack = document.getElementById("queue-stack");
        if (!stack) return;

        const activeTickets = Array.isArray(queueStats?.activeTickets) ? queueStats.activeTickets : [];
        if (activeTickets.length === 0) {
            stack.innerHTML = '<div class="status-banner">No active queue items right now.</div>';
            return;
        }

        stack.innerHTML = activeTickets.slice(0, 5).map(function (ticket, index) {
            return `
                <article class="queue-card ${index === 0 ? "top" : ""}">
                    <div class="queue-card-head">
                        <strong>${escapeHtml(ticket.ticketId)}</strong>
                        <span class="tag ${getSeverityClass(ticket.severity)}">${escapeHtml(ticket.severity || "General")}</span>
                    </div>
                    <p>Customer: ${escapeHtml(ticket.customerId || "Unassigned")}</p>
                    <p>Channel: ${escapeHtml(ticket.channel || "General")} | Status: <strong>${escapeHtml(ticket.status || "New")}</strong></p>
                </article>
            `;
        }).join("");
    }

    function renderTrendSummary(tickets, queueStats) {
        const summaryEl = document.getElementById("team-trend-summary");
        if (!summaryEl) {
            return;
        }

        const totalTickets = tickets.length;
        const activeTickets = queueStats?.waitingCount ?? tickets.filter(ticket => ticket.status !== "Resolved" && ticket.status !== "Closed").length;
        const escalations = queueStats?.escalatedCount ?? tickets.filter(ticket => ticket.status === "Escalated").length;
        summaryEl.textContent = `Active: ${activeTickets} | Total: ${totalTickets} | Escalated: ${escalations}`;
    }

    async function loadTeamChat() {
        const feed = document.getElementById("dashboard-chat-feed");
        if (!feed) {
            return;
        }

        try {
            const messages = await ApiClient.get("/chat/messages");
            if (!Array.isArray(messages) || messages.length === 0) {
                feed.innerHTML = '<div class="status-banner">No internal messages yet.</div>';
                return;
            }

            feed.innerHTML = messages.slice(0, 5).map(function (message) {
                return `
                    <div class="timeline-item">
                        <strong>${escapeHtml(message.senderId || "Unknown")}</strong> to ${escapeHtml(message.recipientId || "Unknown")}
                        <div>${escapeHtml(message.message || "")}</div>
                        <small>${message.createdAt ? new Date(message.createdAt).toLocaleString() : ""}</small>
                    </div>
                `;
            }).join("");
        } catch (error) {
            console.error("Failed to load team chat:", error);
            feed.innerHTML = '<div class="status-banner">Chat feed unavailable.</div>';
        }
    }

    async function sendTeamMessage() {
        const recipientInput = document.getElementById("dashboard-chat-recipient");
        const messageInput = document.getElementById("dashboard-chat-message");

        const recipientId = recipientInput ? recipientInput.value.trim() : "";
        const message = messageInput ? messageInput.value.trim() : "";

        if (!recipientId || !message) {
            return;
        }

        try {
            await ApiClient.post("/chat/messages", { recipientId, message });
            if (messageInput) {
                messageInput.value = "";
            }
            await loadTeamChat();
        } catch (error) {
            console.error("Failed to send team message:", error);
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
        return String(value)
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#39;");
    }
});