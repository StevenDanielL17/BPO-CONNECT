async function loadLandingSnapshot() {
    const openTicketsEl = document.getElementById("landing-open-tickets");
    const escalatedTicketsEl = document.getElementById("landing-escalated-tickets");
    const kbResultsEl = document.getElementById("landing-kb-results");
    const statusEl = document.getElementById("landing-fetch-status");

    if (!openTicketsEl || !escalatedTicketsEl || !kbResultsEl || !statusEl) {
        return;
    }

    statusEl.innerHTML = "Loading data from backend...";

    try {
        const [tickets, kbMatches] = await Promise.all([
            ApiClient.get("/tickets"),
            ApiClient.get("/kb/search?query=password")
        ]);

        const ticketList = Array.isArray(tickets) ? tickets : [];
        const kbList = Array.isArray(kbMatches) ? kbMatches : [];

        const escalatedCount = ticketList.filter((ticket) => {
            const priority = String(ticket?.priorityLevel || "").toLowerCase();
            const status = String(ticket?.status || "").toLowerCase();
            return priority === "high" || priority === "critical" || status === "escalated";
        }).length;

        openTicketsEl.textContent = String(ticketList.length);
        escalatedTicketsEl.textContent = String(escalatedCount);
        kbResultsEl.textContent = String(kbList.length);
        statusEl.innerHTML = "<strong>Connected:</strong> values fetched from /api endpoints.";
    } catch (error) {
        openTicketsEl.textContent = "--";
        escalatedTicketsEl.textContent = "--";
        kbResultsEl.textContent = "--";
        statusEl.textContent = `Backend fetch failed: ${error.message}`;
    }
}

const refreshLandingButton = document.getElementById("refreshLandingKpis");
if (refreshLandingButton) {
    refreshLandingButton.addEventListener("click", loadLandingSnapshot);
}

loadLandingSnapshot();
