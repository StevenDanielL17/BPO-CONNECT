document.addEventListener("DOMContentLoaded", function() {
    const simulateCallBtn = document.getElementById("simulate-call-btn");
    const aniInput = document.getElementById("ani-input");
    const custIdInput = document.getElementById("cust-id");
    const screenPopResult = document.getElementById("screen-pop-result");
    const liveQueueSummary = document.getElementById("live-queue-summary");
    const liveQueueList = document.getElementById("live-queue-list");
    const openTicketBtn = document.getElementById("open-ticket-btn");
    const openKbBtn = document.getElementById("open-kb-btn");
    const transferBtn = document.getElementById("transfer-btn");
    const escalateBtn = document.getElementById("escalate-btn");
    let activeQueueTickets = [];
    let selectedQueueTicketId = null;

    if (simulateCallBtn) {
        simulateCallBtn.addEventListener("click", async function() {
            const ani = aniInput.value.trim();
            if (!ani) {
                showResult("Please enter a phone number.");
                return;
            }

            try {
                const customer = await ApiClient.post("/call", { ani });
                if (customer) {
                    renderScreenPop(customer);
                } else {
                    showResult("Unknown caller. Create a new customer profile.");
                    if (custIdInput) {
                        custIdInput.value = "NEW_CUST";
                    }
                }
            } catch (error) {
                console.error("Call simulation error:", error);
                showResult("Failed to simulate call.");
            }
        });
    }

    if (openTicketBtn) {
        openTicketBtn.addEventListener("click", function () {
            const currentCustomerId = custIdInput ? custIdInput.value.trim() : "";
            const draft = {
                source: "Calls",
                customerId: currentCustomerId,
                channel: "voice",
                severity: "Medium",
                description: `Follow-up from live call${currentCustomerId ? ` for ${currentCustomerId}` : ""}`
            };
            sessionStorage.setItem("bpoConnect.ticketDraft", JSON.stringify(draft));
            window.location.href = "tickets.html";
        });
    }

    if (openKbBtn) {
        openKbBtn.addEventListener("click", function () {
            window.location.href = "knowledge-base.html";
        });
    }

    if (transferBtn) {
        transferBtn.addEventListener("click", function () {
            transferSelectedTicket();
        });
    }

    if (escalateBtn) {
        escalateBtn.addEventListener("click", function () {
            escalateSelectedTicket();
        });
    }

    loadLiveQueue();

    function showResult(message) {
        if (screenPopResult) {
            screenPopResult.innerHTML = `<strong>${message}</strong>`;
        }
    }

    function renderScreenPop(customer) {
        if (custIdInput) {
            custIdInput.value = customer.customerId || "NEW_CUST";
        }

        const nameEl = document.getElementById("screen-pop-name");
        const emailEl = document.getElementById("screen-pop-email");
        const accountEl = document.getElementById("screen-pop-account");
        const ticketEl = document.getElementById("screen-pop-last-ticket");

        if (nameEl) nameEl.textContent = customer.customerName || "Unknown customer";
        if (emailEl) emailEl.textContent = customer.email || "No email on file";
        if (accountEl) accountEl.textContent = customer.accountStatus || "Active";
        if (ticketEl) ticketEl.textContent = customer.lastTicketId || "No prior ticket";

        showResult(`Live screen pop loaded for ${customer.customerName || customer.customerId || "caller"}`);
    }

    async function loadLiveQueue() {
        if (!liveQueueList) {
            return;
        }

        try {
            const stats = await ApiClient.get("/queue/stats");
            const queueItems = Array.isArray(stats.activeTickets) ? stats.activeTickets : [];
            activeQueueTickets = queueItems;
            selectedQueueTicketId = queueItems.length > 0 ? queueItems[0].ticketId : null;

            if (liveQueueSummary) {
                liveQueueSummary.textContent = `Waiting: ${stats.waitingCount || 0} | In progress: ${stats.inProgressCount || 0} | Escalated: ${stats.escalatedCount || 0}`;
            }

            if (queueItems.length === 0) {
                liveQueueList.innerHTML = '<div class="status-banner">No live calls waiting.</div>';
                return;
            }

            liveQueueList.innerHTML = queueItems.slice(0, 5).map(function (ticket) {
                return `
                    <article class="queue-card">
                        <div class="queue-card-head">
                            <strong>${ticket.ticketId || "Ticket"}</strong>
                            <span class="tag ${getSeverityClass(ticket.severity)}">${ticket.severity || "General"}</span>
                        </div>
                        <p>Customer: ${ticket.customerId || "Unassigned"}</p>
                        <p>Channel: ${ticket.channel || "Voice"}</p>
                        <p>Status: <strong>${ticket.status || "New"}</strong></p>
                        <div class="actions">
                            <button class="btn btn-secondary" style="padding: 6px 10px; font-size: 0.85rem;" data-select-call-ticket="${ticket.ticketId || ""}">Select</button>
                            <button class="btn btn-warning" style="padding: 6px 10px; font-size: 0.85rem;" data-transfer-call-ticket="${ticket.ticketId || ""}">Transfer</button>
                            <button class="btn btn-danger" style="padding: 6px 10px; font-size: 0.85rem;" data-escalate-call-ticket="${ticket.ticketId || ""}">Escalate</button>
                        </div>
                    </article>
                `;
            }).join("");

            liveQueueList.querySelectorAll("[data-select-call-ticket]").forEach(function (button) {
                button.addEventListener("click", function () {
                    selectedQueueTicketId = button.getAttribute("data-select-call-ticket");
                    showResult(`Selected ticket ${selectedQueueTicketId}`);
                });
            });

            liveQueueList.querySelectorAll("[data-transfer-call-ticket]").forEach(function (button) {
                button.addEventListener("click", function () {
                    selectedQueueTicketId = button.getAttribute("data-transfer-call-ticket");
                    transferSelectedTicket();
                });
            });

            liveQueueList.querySelectorAll("[data-escalate-call-ticket]").forEach(function (button) {
                button.addEventListener("click", function () {
                    selectedQueueTicketId = button.getAttribute("data-escalate-call-ticket");
                    escalateSelectedTicket();
                });
            });
        } catch (error) {
            console.error("Queue load error:", error);
            liveQueueList.innerHTML = '<div class="status-banner">Unable to load live queue.</div>';
        }
    }

    async function transferSelectedTicket() {
        const ticket = resolveSelectedTicket();
        if (!ticket) {
            showResult("No active ticket found. Create a ticket first.");
            sessionStorage.setItem("bpoConnect.ticketDraft", JSON.stringify({
                source: "Calls",
                customerId: custIdInput ? custIdInput.value.trim() : "",
                channel: "voice",
                severity: "Medium",
                description: "Live call transfer follow-up"
            }));
            window.location.href = "tickets.html";
            return;
        }

        const targetAgentId = window.prompt("Enter the target agent ID for transfer:", "A102");
        if (!targetAgentId) {
            return;
        }

        try {
            await ApiClient.post(`/tickets/${ticket.ticketId}/transfer`, { targetAgentId });
            showResult(`Ticket ${ticket.ticketId} transferred to ${targetAgentId}.`);
            loadLiveQueue();
        } catch (error) {
            console.error("Transfer error:", error);
            showResult(error.message || "Transfer failed.");
        }
    }

    async function escalateSelectedTicket() {
        const ticket = resolveSelectedTicket();
        if (!ticket) {
            showResult("No active ticket found. Create a ticket first.");
            sessionStorage.setItem("bpoConnect.ticketDraft", JSON.stringify({
                source: "Calls",
                customerId: custIdInput ? custIdInput.value.trim() : "",
                channel: "voice",
                severity: "High",
                description: "Live call escalation follow-up"
            }));
            window.location.href = "tickets.html";
            return;
        }

        try {
            await ApiClient.post(`/tickets/${ticket.ticketId}/escalate`, {});
            showResult(`Ticket ${ticket.ticketId} escalated.`);
            loadLiveQueue();
        } catch (error) {
            console.error("Escalation error:", error);
            showResult(error.message || "Escalation failed.");
        }
    }

    function resolveSelectedTicket() {
        if (selectedQueueTicketId) {
            const selectedTicket = activeQueueTickets.find(function (ticket) {
                return ticket.ticketId === selectedQueueTicketId;
            });
            if (selectedTicket) {
                return selectedTicket;
            }
        }

        return activeQueueTickets.length > 0 ? activeQueueTickets[0] : null;
    }

    function getSeverityClass(severity) {
        const value = (severity || "").toLowerCase();
        if (value === "critical") return "critical";
        if (value === "high") return "high";
        if (value === "medium") return "medium";
        return "low";
    }
});