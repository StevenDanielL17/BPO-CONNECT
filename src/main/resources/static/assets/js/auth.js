// Authentication and Authorization System

// Role descriptions and access info
const roleDescriptions = {
    agent: {
        title: 'Agent',
        description: 'Access calls, screen pop, ticket workspace, and knowledge base',
        redirectPath: 'calls.html'
    },
    qa_lead: {
        title: 'QA Lead',
        description: 'Manage quality assurance and agent evaluations',
        redirectPath: 'qa.html'
    },
    supervisor: {
        title: 'Supervisor',
        description: 'Monitor team performance and generate reports',
        redirectPath: 'reports.html'
    },
    client: {
        title: 'Client',
        description: 'Track your tickets and interact with support',
        redirectPath: 'client-portal.html'
    },
    admin: {
        title: 'Admin',
        description: 'Manage system configuration and user access',
        redirectPath: 'admin-dashboard.html'
    }
};

// Update role information on selection
function updateRoleInfo() {
    const roleSelect = document.getElementById('role');
    const roleInfoDiv = document.getElementById('roleInfo');
    
    if (roleSelect.value) {
        const role = roleDescriptions[roleSelect.value];
        roleInfoDiv.innerHTML = `<strong>${role.title}:</strong> ${role.description}`;
        roleInfoDiv.style.display = 'block';
    } else {
        roleInfoDiv.style.display = 'none';
    }
}

// Handle login form submission
function setupLoginForm() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const role = document.getElementById('role').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
        
        // Reset error messages
        document.querySelectorAll('.error-msg').forEach(el => el.style.display = 'none');
        
        // Validate inputs
        let isValid = true;
    
    if (!role) {
        document.getElementById('roleError').style.display = 'block';
        isValid = false;
    }
    if (!email || !email.includes('@')) {
        document.getElementById('emailError').style.display = 'block';
        isValid = false;
    }
    if (!password || password.length < 6) {
        document.getElementById('passwordError').style.display = 'block';
        isValid = false;
    }
    
    if (!isValid) return;
    
    // Simulate authentication (in real app, call backend API)
    const userData = {
        role: role,
        email: email,
        name: email.split('@')[0],
        loginTime: new Date().toISOString()
    };
    
    // Store in localStorage
    localStorage.setItem('bpoConnectUser', JSON.stringify(userData));
    
    // Redirect to role-specific dashboard
    const redirectPath = roleDescriptions[role].redirectPath;
    window.location.href = redirectPath;
        });
    }
}

// Check if user is logged in
function checkAuthentication() {
    const user = localStorage.getItem('bpoConnectUser');
    return user ? JSON.parse(user) : null;
}

// Get current user role
function getCurrentUserRole() {
    const user = checkAuthentication();
    return user ? user.role : null;
}

// Logout function
function logout() {
    localStorage.removeItem('bpoConnectUser');
    window.location.href = 'index.html';
}

// Check access for protected pages
function requireRole(...allowedRoles) {
    const user = checkAuthentication();
    
    if (!user) {
        // No user logged in, redirect to login
        window.location.href = 'login.html';
        return;
    }
    
    if (!allowedRoles.includes(user.role)) {
        // User doesn't have access, redirect to appropriate dashboard
        const redirectPath = roleDescriptions[user.role].redirectPath;
        window.location.href = redirectPath;
        return;
    }
    
    // Access granted - update user info in page if element exists
    updateUserDisplay(user);
}

// Update user display info in navigation
function updateUserDisplay(user) {
    const userDisplay = document.getElementById('userDisplay');
    if (userDisplay) {
        userDisplay.innerHTML = `${user.name} (${roleDescriptions[user.role].title})`;
    }
    
    const userEmail = document.getElementById('userEmail');
    if (userEmail) {
        userEmail.innerHTML = user.email;
    }
}

// Setup role-based navigation
function setupRoleBasedNav() {
    const user = checkAuthentication();
    if (!user) return;
    
    const navItems = document.querySelectorAll('[data-nav]');
    navItems.forEach(item => {
        const requiredRoles = item.getAttribute('data-roles');
        if (requiredRoles) {
            const roles = requiredRoles.split(',').map(r => r.trim());
            if (!roles.includes(user.role)) {
                item.style.display = 'none';
            }
        }
    });
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    setupRoleBasedNav();
    setupLoginForm();
    const user = checkAuthentication();
    if (user) {
        updateUserDisplay(user);
    }
});
