const API_BASE = '/api';
let currentUser = "A101"; // Default agent from DataInitializer

async function login() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const resBox = document.getElementById('login-result');

    try {
        const response = await fetch(`${API_BASE}/users/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        const result = await response.text();
        
        resBox.style.display = 'block';
        resBox.innerHTML = `<strong>${result}</strong>`;
        
        if (result === "Login Successful") {
            currentUser = username;
            document.getElementById('current-user').innerText = username;
        }
    } catch (e) {
        console.error(e);
        alert("Login failed.");
    }
}

async function searchKB() {
    const query = document.getElementById('kb-query').value;
    const resBox = document.getElementById('kb-results');

    try {
        const response = await fetch(`${API_BASE}/kb/search?query=${query}`);
        const articles = await response.json();
        
        resBox.style.display = 'block';
        if (articles.length > 0) {
            resBox.innerHTML = articles.map(a => `<strong>${a.title}</strong><br>${a.content}<hr>`).join('');
        } else {
            resBox.innerHTML = "No articles found.";
        }
    } catch (e) {
        console.error(e);
        alert("KB search failed.");
    }
}

async function simulateCall() {
    const ani = document.getElementById('ani-input').value;
    const resBox = document.getElementById('screen-pop-result');
    
    if (!ani) {
        alert("Please enter a phone number");
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/call`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ ani: ani })
        });
        const data = await response.text();
        
        if(data) {
            const customer = JSON.parse(data);
            resBox.style.display = 'block';
            resBox.innerHTML = `<strong>Screen Pop!</strong><br>Customer: ${customer.customerName}<br>Email: ${customer.email}<br>Status: ${customer.accountStatus}`;
            document.getElementById('cust-id').value = customer.customerId;
        } else {
            resBox.style.display = 'block';
            resBox.innerHTML = `<strong>Unknown Caller</strong><br>Please create a new customer profile.`;
            document.getElementById('cust-id').value = 'NEW_CUST';
        }
    } catch (e) {
        console.error(e);
        alert("Failed to simulate call.");
    }
}

async function createTicket() {
    const customerId = document.getElementById('cust-id').value || "C001";
    const channel = document.getElementById('channel').value;
    const severity = document.getElementById('severity').value;
    const description = document.getElementById('description').value;

    if (!description) {
        alert("Description is required.");
        return;
    }

    const payload = {
        customerId: customerId,
        channel: channel,
        severity: severity,
        description: description,
        agentId: currentUser,
        referenceId: "REF-" + Math.floor(Math.random() * 1000)
    };

    try {
        const response = await fetch(`${API_BASE}/tickets`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await response.json();
        
        const resBox = document.getElementById('ticket-result');
        resBox.style.display = 'block';
        resBox.innerHTML = `<strong>Ticket Created Successfully!</strong><br>Ticket ID: ${data.ticketId}<br>Status: ${data.status}`;
        
        document.getElementById('description').value = '';
        loadTickets();
    } catch (e) {
        console.error(e);
        alert("Failed to create ticket.");
    }
}

async function loadTickets() {
    try {
        const response = await fetch(`${API_BASE}/tickets`);
        const tickets = await response.json();
        
        const tbody = document.querySelector('#tickets-table tbody');
        tbody.innerHTML = '';
        
        tickets.forEach(t => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${t.ticketId}</td>
                <td>${t.customerId}</td>
                <td>${t.channel}</td>
                <td>${t.severity}</td>
                <td><strong>${t.status}</strong></td>
                <td>
                    ${t.status !== 'Resolved' && t.status !== 'Escalated' ? `<button class="btn-small btn-resolve" onclick="updateStatus('${t.ticketId}', 'Resolved')">Resolve</button>` : ''}
                    ${t.status !== 'Escalated' ? `<button class="btn-small" onclick="escalate('${t.ticketId}')">Escalate</button>` : ''}
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        console.error(e);
    }
}

async function updateStatus(ticketId, status) {
    try {
        await fetch(`${API_BASE}/tickets/${ticketId}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: status })
        });
        loadTickets();
    } catch (e) {
        console.error(e);
    }
}

async function escalate(ticketId) {
    try {
        await fetch(`${API_BASE}/tickets/${ticketId}/escalate`, {
            method: 'POST'
        });
        loadTickets();
    } catch (e) {
        console.error(e);
    }
}

async function generateReport() {
    try {
        const response = await fetch(`${API_BASE}/reports/daily?userId=${currentUser}`, {
            method: 'POST'
        });
        const result = await response.text();
        alert(result);
    } catch (e) {
        console.error(e);
    }
}

async function loadEvaluations() {
    const agentId = document.getElementById('qa-agent-id').value;
    const resBox = document.getElementById('qa-results');

    try {
        const response = await fetch(`${API_BASE}/evaluations/${agentId}`);
        const evals = await response.json();
        
        resBox.style.display = 'block';
        if (evals.length > 0) {
            resBox.innerHTML = evals.map(e => `<strong>Score: ${e.score}</strong><br>Feedback: ${e.feedback}<hr>`).join('');
        } else {
            resBox.innerHTML = "No evaluations found for this agent.";
        }
    } catch (e) {
        console.error(e);
    }
}

// Initial load
loadTickets();
