const API_BASE = (window.BPO_API_BASE || "/api").replace(/\/$/, "");

async function readResponseBody(response) {
    const contentType = response.headers.get("content-type") || "";
    if (contentType.includes("application/json")) {
        return response.json();
    }
    return response.text();
}

function buildErrorMessage(status, body) {
    if (body && typeof body === "object") {
        return body.message || body.error || `API error: ${status}`;
    }
    if (typeof body === "string" && body.trim()) {
        return body;
    }
    return `API error: ${status}`;
}

class ApiClient {
    static async post(endpoint, data) {
        const response = await fetch(`${API_BASE}${endpoint}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify(data)
        });
        const body = await readResponseBody(response);
        if (!response.ok) throw new Error(buildErrorMessage(response.status, body));
        return body;
    }

    static async get(endpoint) {
        const response = await fetch(`${API_BASE}${endpoint}`, {
            credentials: "include"
        });
        const body = await readResponseBody(response);
        if (!response.ok) throw new Error(buildErrorMessage(response.status, body));
        return body;
    }

    static async put(endpoint, data) {
        const response = await fetch(`${API_BASE}${endpoint}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify(data)
        });
        const body = await readResponseBody(response);
        if (!response.ok) throw new Error(buildErrorMessage(response.status, body));
        return body;
    }

    static async patch(endpoint, data) {
        const response = await fetch(`${API_BASE}${endpoint}`, {
            method: "PATCH",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: data === undefined ? undefined : JSON.stringify(data)
        });
        const body = await readResponseBody(response);
        if (!response.ok) throw new Error(buildErrorMessage(response.status, body));
        return body;
    }

    static async delete(endpoint) {
        const response = await fetch(`${API_BASE}${endpoint}`, {
            method: "DELETE",
            credentials: "include"
        });
        const body = await readResponseBody(response);
        if (!response.ok) throw new Error(buildErrorMessage(response.status, body));
        return body;
    }
}

window.ApiClient = ApiClient;