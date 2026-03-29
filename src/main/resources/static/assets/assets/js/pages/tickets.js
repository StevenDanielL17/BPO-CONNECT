document.addEventListener("DOMContentLoaded", function() {
    const createTicketBtn = document.getElementById("create-ticket-btn");
    const ticketForm = document.querySelector("form");
    const ticketResult = document.getElementById("ticket-result");

    if (createTicketBtn) {
        createTicketBtn.addEventListener("click", createTicket);
    }

    if (ticketForm) {
        ticketForm.addEventListener("submit", function(e) {
            e.preventDefault();
            createTicket();
        });
    }

    async function createTicket() {
        const customerId = document.getElementById("cust-id").value || "C001";
        const channel = document.getElementById("channel").value;
        const severity = document.getElementById("severity").value;
        const description = document.getElementById("description").value;

        if (!description) {
            showResult("Description is required.");
            return;
        }

        const currentUser = localStorage.getItem("currentUser") || "A101";
        const payload = {
            customerId,
            channel,
            severity,
            description,
            agentId: currentUser,
            referenceId: `REF-${Math.floor(Math.random() * 1000)}`
        };

        try {
            const ticket = await ApiClient.post("/tickets", payload);
            showResult(`Ticket Created<br>Ticket ID: ${ticket.ticketId}<br>Status: ${ticket.status}`);
            document.getElementById("description").value = "";
            loadTickets();
        } catch (error) {
            console.error("Ticket creation error:", error);
            showResult("Failed to create ticket.");
        }
    }

    async function loadTickets() {
        try {
            const tickets = await ApiClient.get("/tickets");
            renderQueue(tickets);
        } catch (error) {
            console.error("Failed to load tickets:", error);
        }
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
                <div class="card-actions">
                    ${t.status !== "Resolved" ? `<button class="btn btn-secondary small" onclick="updateStatus('${t.ticketId}', 'Resolved')">Resolve</button>` : ""}
                    ${t.status !== "Escalated" ? `<button class="btn btn-warning small" onclick="escalate('${t.ticketId}')">Escalate</button>` : ""}
                </div>
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

    function showResult(message) {
        if (ticketResult) {
            ticketResult.innerHTML = `<strong>${message}</strong>`;
            ticketResult.style.display = "block";
        }
    }

    // Initial load
    loadTickets();

    // Make functions global for onclick handlers
    window.updateStatus = async function(ticketId, status) {
        try {
            await ApiClient.put(`/tickets/${ticketId}/status`, { status });
            loadTickets();
        } catch (error) {
            console.error("Status update error:", error);
        }
    };

    window.escalate = async function(ticketId) {
        try {
            await ApiClient.post(`/tickets/${ticketId}/escalate`);
            loadTickets();
        } catch (error) {
            console.error("Escalation error:", error);
        }
    };
});