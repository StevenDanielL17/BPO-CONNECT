let cachedUser = null;
let currentUserRequest = null;
const SESSION_USER_STORAGE_KEY = "bpoConnect.sessionUser";

const roleDescriptions = {
    agent: {
        title: "Agent",
        description: "Access calls, screen pop, ticket workspace, and knowledge base",
        redirectPath: "agent-home.html"
    },
    qa_lead: {
        title: "QA Lead",
        description: "Manage quality assurance and agent evaluations",
        redirectPath: "qa.html"
    },
    supervisor: {
        title: "Supervisor",
        description: "Monitor team performance and generate reports",
        redirectPath: "reports.html"
    },
    client: {
        title: "Client",
        description: "Track your tickets and interact with support",
        redirectPath: "client-portal.html"
    },
    admin: {
        title: "Admin",
        description: "Manage system configuration and user access",
        redirectPath: "admin-dashboard.html"
    }
};

function normalizeRoleKey(role) {
    const value = (role || "").toString().trim().toLowerCase();
    if (!value) {
        return "client";
    }
    if (value.includes("admin")) return "admin";
    if (value.includes("leader") || value.includes("supervisor")) return "supervisor";
    if (value.includes("qa") || value.includes("quality")) return "qa_lead";
    if (value.includes("agent")) return "agent";
    return "client";
}

function getRoleRedirectPath(role) {
    const roleKey = normalizeRoleKey(role);
    return roleDescriptions[roleKey]?.redirectPath || "index.html";
}

function sanitizeSessionUser(user) {
    if (!user) {
        return null;
    }

    const roleKey = normalizeRoleKey(user.roleKey || user.role || user.roleLabel);
    const roleLabel = user.roleLabel || user.role || roleDescriptions[roleKey]?.title || "Client";
    const displayName = user.displayName || user.username || user.name || user.email || "User";

    return {
        userId: user.userId || user.id || "",
        username: user.username || displayName,
        displayName,
        email: user.email || "",
        role: roleKey,
        roleLabel,
        lastLoginTime: user.lastLoginTime || null
    };
}

function storeSession(user) {
    cachedUser = sanitizeSessionUser(user);
    if (cachedUser) {
        sessionStorage.setItem(SESSION_USER_STORAGE_KEY, JSON.stringify(cachedUser));
    }
    return cachedUser;
}

function readStoredSession() {
    try {
        const rawSession = sessionStorage.getItem(SESSION_USER_STORAGE_KEY);
        return rawSession ? sanitizeSessionUser(JSON.parse(rawSession)) : null;
    } catch (error) {
        sessionStorage.removeItem(SESSION_USER_STORAGE_KEY);
        return null;
    }
}

async function loadCurrentUser(forceRefresh = false) {
    if (!forceRefresh && cachedUser) {
        return cachedUser;
    }

    if (!forceRefresh) {
        const storedUser = readStoredSession();
        if (storedUser) {
            cachedUser = storedUser;
            return cachedUser;
        }
    }

    if (!forceRefresh && currentUserRequest) {
        return currentUserRequest;
    }

    currentUserRequest = (async function () {
        try {
            const response = await ApiClient.get("/users/me");
            const user = response.user || response;
            cachedUser = sanitizeSessionUser(user);
            if (cachedUser) {
                sessionStorage.setItem(SESSION_USER_STORAGE_KEY, JSON.stringify(cachedUser));
            }
            return cachedUser;
        } catch (error) {
            cachedUser = readStoredSession();
            return cachedUser;
        } finally {
            currentUserRequest = null;
        }
    })();

    return currentUserRequest;
}

function checkAuthentication() {
    return cachedUser;
}

function getCurrentUserRole() {
    const user = checkAuthentication();
    return user ? user.role : null;
}

function logout() {
    cachedUser = null;
    sessionStorage.removeItem(SESSION_USER_STORAGE_KEY);
    ApiClient.post("/users/logout", {})
        .catch(function () {
            // Ignore API logout failure and still move user out of protected pages.
        })
        .finally(function () {
            window.location.href = "index.html";
        });
}

function updateUserDisplay(user) {
    const sessionUser = sanitizeSessionUser(user);
    if (!sessionUser) {
        return;
    }

    const userDisplay = document.getElementById("userDisplay");
    if (userDisplay) {
        userDisplay.textContent = `${sessionUser.displayName} (${roleDescriptions[sessionUser.role]?.title || sessionUser.roleLabel})`;
    }

    const userEmail = document.getElementById("userEmail");
    if (userEmail) {
        userEmail.textContent = sessionUser.email;
    }
}

function setupRoleBasedNav() {
    const user = checkAuthentication();
    if (!user) {
        return;
    }

    const navItems = document.querySelectorAll("[data-nav]");
    navItems.forEach(function (item) {
        const requiredRoles = item.getAttribute("data-roles");
        if (!requiredRoles) {
            return;
        }

        const allowedRoles = requiredRoles.split(",").map(function (role) {
            return role.trim();
        });

        if (!allowedRoles.includes(user.role)) {
            item.style.display = "none";
        }
    });
}

function requireRole(...allowedRoles) {
    loadCurrentUser().then(function (user) {
        if (!user) {
            window.location.href = "login.html";
            return;
        }

        if (allowedRoles.length && !allowedRoles.includes(user.role)) {
            window.location.href = getRoleRedirectPath(user.role);
            return;
        }

        updateUserDisplay(user);
    });
    return true;
}

function guardPageFromSession() {
    const page = document.body.getAttribute("data-page");
    const protectedPageRoles = {
        "agent-home": ["agent"],
        dashboard: ["supervisor"],
        calls: ["agent"],
        tickets: ["agent"],
        "knowledge-base": ["agent"],
        qa: ["qa_lead"],
        reports: ["supervisor"],
        "client-portal": ["client"],
        "admin-dashboard": ["admin"]
    };

    const publicPages = new Set(["landing", "login", "signup", "forgot-password", "reset-password"]);
    loadCurrentUser().then(function (user) {
        if (publicPages.has(page)) {
            if (user && page !== "reset-password") {
                window.location.href = getRoleRedirectPath(user.role);
            }
            return;
        }

        const allowedRoles = protectedPageRoles[page];
        if (allowedRoles) {
            if (!user) {
                window.location.href = "login.html";
                return;
            }
            if (!allowedRoles.includes(user.role)) {
                window.location.href = getRoleRedirectPath(user.role);
                return;
            }
            updateUserDisplay(user);
        }
    });
}

document.addEventListener("DOMContentLoaded", function () {
    guardPageFromSession();
    loadCurrentUser().then(function (user) {
        if (user) {
            setupRoleBasedNav();
            updateUserDisplay(user);
        }
    });
});

window.Auth = {
    storeSession,
    loadCurrentUser,
    checkAuthentication,
    getCurrentUserRole,
    logout,
    requireRole,
    getRoleRedirectPath,
    roleDescriptions
};
