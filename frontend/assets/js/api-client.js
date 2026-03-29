const API_BASE = (window.BPO_API_BASE || "/api").replace(/\/$/, "");

class ApiClient {
    static async post(endpoint, data) {
        const response = await fetch(`${API_BASE}${endpoint}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });
        if (!response.ok) throw new Error(`API error: ${response.status}`);
        return response.json();
    }

    static async get(endpoint) {
        const response = await fetch(`${API_BASE}${endpoint}`);
        if (!response.ok) throw new Error(`API error: ${response.status}`);
        return response.json();
    }

    static async put(endpoint, data) {
        const response = await fetch(`${API_BASE}${endpoint}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });
        if (!response.ok) throw new Error(`API error: ${response.status}`);
        return response;
    }
}

window.ApiClient = ApiClient;