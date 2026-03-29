document.addEventListener("DOMContentLoaded", function() {
    const currentUser = localStorage.getItem("currentUser") || "A101";
    const userEl = document.getElementById("current-user");
    if (userEl) userEl.textContent = currentUser;

    loadDashboardData();

    async function loadDashboardData() {
        try {
            const tickets = await ApiClient.get("/tickets");
            updateKPIs(tickets);
            renderQueue(tickets);
        } catch (error) {
            console.error("Failed to load dashboard data:", error);
        }
    }

    function updateKPIs(tickets) {
        const openTickets = tickets.filter(t => t.status !== "Resolved").length;
        const escalated = tickets.filter(t => t.status === "Escalated").length;

        const openEl = document.getElementById("kpi-open");
        const escalatedEl = document.getElementById("kpi-escalated");

        if (openEl) openEl.textContent = openTickets;
        if (escalatedEl) escalatedEl.textContent = escalated;
    }

    function renderQueue(tickets) {
        const stack = document.getElementById("queue-stack");
        if (!stack) return;

        stack.innerHTML = "";

        const active = tickets.filter(t => t.status !== "Resolved").slice(0, 5);
        active.forEach((t, idx) => {
            const card = document.createElement("article");
            card.className = `queue-card ${idx === 0 ? "top" : ""}`;
            card.innerHTML = `
                <div class="queue-card-head">
                    <strong>${t.ticketId}</strong>
                    <span class="tag ${getSeverityClass(t.severity)}">${t.severity}</span>
                </div>
                <p>Customer: ${t.customerId}</p>
                <p>Channel: ${t.channel} | Status: <strong>${t.status}</strong></p>
            `;
            stack.appendChild(card);
        });
    }

    function getSeverityClass(severity) {
        const value = (severity || "").toLowerCase();
        if (value === "critical") return "critical";
        if (value === "high") return "high";
        return "";
    }
});