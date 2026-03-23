const API_BASE = "/api";
let currentUser = "A101";
let sceneContext = null;
let scrollVelocity = 0;
let lastScrollY = 0;
let lastScrollTime = performance.now();

function showResult(id, html) {
    const box = document.getElementById(id);
    box.style.display = "block";
    box.innerHTML = html;
}

function setUser(name) {
    currentUser = name;
    const userEl = document.getElementById("current-user");
    if (userEl) {
        userEl.innerText = name;
    }
}

async function login() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    try {
        const response = await fetch(`${API_BASE}/users/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });
        const result = await response.text();
        showResult("login-result", `<strong>${result}</strong>`);

        if (result === "Login Successful") {
            setUser(username);
        }
    } catch (e) {
        console.error(e);
        showResult("login-result", "<strong>Login failed.</strong>");
    }
}

async function searchKB() {
    const query = document.getElementById("kb-query").value;

    try {
        const response = await fetch(`${API_BASE}/kb/search?query=${encodeURIComponent(query)}`);
        const articles = await response.json();

        if (articles.length > 0) {
            showResult("kb-results", articles.map((a) => `<strong>${a.title}</strong><br>${a.content}<hr>`).join(""));
        } else {
            showResult("kb-results", "No articles found.");
        }
    } catch (e) {
        console.error(e);
        showResult("kb-results", "Knowledge base search failed.");
    }
}

async function simulateCall() {
    const ani = document.getElementById("ani-input").value;
    if (!ani) {
        showResult("screen-pop-result", "Please enter a phone number.");
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/call`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ ani })
        });
        const data = await response.text();

        if (data) {
            const customer = JSON.parse(data);
            showResult("screen-pop-result", `<strong>Screen Pop</strong><br>Customer: ${customer.customerName}<br>Email: ${customer.email}<br>Status: ${customer.accountStatus}`);
            document.getElementById("cust-id").value = customer.customerId;
        } else {
            showResult("screen-pop-result", "<strong>Unknown caller.</strong><br>Create a new customer profile.");
            document.getElementById("cust-id").value = "NEW_CUST";
        }
    } catch (e) {
        console.error(e);
        showResult("screen-pop-result", "Failed to simulate call.");
    }
}

async function createTicket() {
    const customerId = document.getElementById("cust-id").value || "C001";
    const channel = document.getElementById("channel").value;
    const severity = document.getElementById("severity").value;
    const description = document.getElementById("description").value;

    if (!description) {
        showResult("ticket-result", "Description is required.");
        return;
    }

    const payload = {
        customerId,
        channel,
        severity,
        description,
        agentId: currentUser,
        referenceId: `REF-${Math.floor(Math.random() * 1000)}`
    };

    try {
        const response = await fetch(`${API_BASE}/tickets`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        const data = await response.json();

        showResult("ticket-result", `<strong>Ticket Created</strong><br>Ticket ID: ${data.ticketId}<br>Status: ${data.status}`);
        document.getElementById("description").value = "";
        loadTickets();
    } catch (e) {
        console.error(e);
        showResult("ticket-result", "Failed to create ticket.");
    }
}

function severityClass(level) {
    const value = (level || "").toLowerCase();
    if (value === "critical") {
        return "critical";
    }
    if (value === "high") {
        return "high";
    }
    return "";
}

function bindDragToAnswer(card, ticketId, status) {
    let startX = 0;
    let dragging = false;

    card.addEventListener("pointerdown", (e) => {
        if (status === "Resolved") {
            return;
        }
        startX = e.clientX;
        dragging = true;
        card.classList.add("dragging");
        card.setPointerCapture(e.pointerId);
    });

    card.addEventListener("pointermove", (e) => {
        if (!dragging) {
            return;
        }
        const deltaX = Math.max(0, e.clientX - startX);
        card.style.transform = `translateX(${Math.min(deltaX, 140)}px)`;
    });

    card.addEventListener("pointerup", async (e) => {
        if (!dragging) {
            return;
        }
        dragging = false;
        const deltaX = e.clientX - startX;
        card.classList.remove("dragging");
        card.style.transform = "";

        if (deltaX > 110 && status !== "Resolved") {
            await updateStatus(ticketId, "InProgress");
            loadTickets();
        }
    });
}

function renderQueue(tickets) {
    const stack = document.getElementById("queue-stack");
    stack.innerHTML = "";

    const active = tickets.filter((t) => t.status !== "Resolved");
    const escalated = tickets.filter((t) => t.status === "Escalated").length;
    const kpiOpen = document.getElementById("kpi-open");
    const kpiEscalated = document.getElementById("kpi-escalated");
    if (kpiOpen) {
        kpiOpen.textContent = String(active.length);
    }
    if (kpiEscalated) {
        kpiEscalated.textContent = String(escalated);
    }

    tickets.forEach((t, idx) => {
        const card = document.createElement("article");
        card.className = `queue-card ${idx === 0 ? "top" : ""}`;
        card.innerHTML = `
            <div class="queue-card-head">
                <strong>${t.ticketId}</strong>
                <span class="tag ${severityClass(t.severity)}">${t.severity}</span>
            </div>
            <p>Customer: ${t.customerId}</p>
            <p>Channel: ${t.channel} | Status: <strong>${t.status}</strong></p>
            <div class="card-actions">
                ${t.status !== "Resolved" ? `<button class="neon-btn small" onclick="updateStatus('${t.ticketId}', 'Resolved')">Resolve</button>` : ""}
                ${t.status !== "Escalated" ? `<button class="neon-btn warning small" onclick="escalate('${t.ticketId}')">Escalate</button>` : ""}
            </div>
        `;

        bindDragToAnswer(card, t.ticketId, t.status);
        stack.appendChild(card);
    });
}

async function loadTickets() {
    try {
        const response = await fetch(`${API_BASE}/tickets`);
        const tickets = await response.json();
        renderQueue(tickets);
    } catch (e) {
        console.error(e);
    }
}

async function updateStatus(ticketId, status) {
    try {
        await fetch(`${API_BASE}/tickets/${ticketId}/status`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ status })
        });
    } catch (e) {
        console.error(e);
    }
}

async function escalate(ticketId) {
    try {
        await fetch(`${API_BASE}/tickets/${ticketId}/escalate`, { method: "POST" });
        loadTickets();
    } catch (e) {
        console.error(e);
    }
}

async function generateReport() {
    try {
        const response = await fetch(`${API_BASE}/reports/daily?userId=${currentUser}`, { method: "POST" });
        const result = await response.text();
        showResult("qa-results", `<strong>Report Engine</strong><br>${result}`);
    } catch (e) {
        console.error(e);
    }
}

async function loadEvaluations() {
    const agentId = document.getElementById("qa-agent-id").value || currentUser;

    try {
        const response = await fetch(`${API_BASE}/evaluations/${agentId}`);
        const evals = await response.json();

        if (evals.length > 0) {
            showResult("qa-results", evals.map((entry) => `<strong>Score: ${entry.score}</strong><br>Feedback: ${entry.feedback}<hr>`).join(""));
        } else {
            showResult("qa-results", "No evaluations found for this agent.");
        }
    } catch (e) {
        console.error(e);
        showResult("qa-results", "Unable to load evaluations.");
    }
}

function initTerminalMode() {
    const terminalToggle = document.getElementById("terminal-toggle");
    const effectsToggle = document.getElementById("effects-toggle");

    terminalToggle?.addEventListener("change", () => {
        document.body.classList.toggle("terminal-mode", terminalToggle.checked);
    });

    effectsToggle?.addEventListener("change", () => {
        document.body.classList.toggle("effects-on", effectsToggle.checked);
    });
    document.body.classList.add("effects-on");
}

function initMetricCharge() {
    const bars = document.querySelectorAll(".metric-fill");
    const io = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
            if (!entry.isIntersecting) {
                return;
            }
            const target = Number(entry.target.getAttribute("data-target") || "0");
            entry.target.style.width = `${Math.min(target, 100)}%`;
            io.unobserve(entry.target);
        });
    }, { threshold: 0.6 });

    bars.forEach((bar) => io.observe(bar));
}

function initParallaxTowers() {
    const towers = [...document.querySelectorAll(".kpi-tower")];
    if (!towers.length) {
        return;
    }

    const update = () => {
        const y = window.scrollY;
        towers.forEach((tower) => {
            const speed = Number(tower.dataset.speed || "0.3");
            tower.style.transform = `translateY(${Math.round(-y * speed * 0.12)}px)`;
        });
    };

    window.addEventListener("scroll", update, { passive: true });
    update();
}

function updateScrollVelocity() {
    const now = performance.now();
    const y = window.scrollY;
    const dy = Math.abs(y - lastScrollY);
    const dt = Math.max(16, now - lastScrollTime);
    scrollVelocity = Math.min(2600, (dy / dt) * 1000);

    lastScrollY = y;
    lastScrollTime = now;
    document.documentElement.style.setProperty("--motion-blur", `${Math.min(2.8, scrollVelocity / 1000)}px`);
}

function initPhoneHandset() {
    const phone = document.getElementById("phone-handset");
    if (!phone) {
        return;
    }

    window.addEventListener("scroll", () => {
        if (window.scrollY > 120) {
            phone.classList.add("visible");
        } else {
            phone.classList.remove("visible");
        }
    }, { passive: true });

    document.addEventListener("mousemove", (e) => {
        const rect = phone.getBoundingClientRect();
        const cx = rect.left + rect.width / 2;
        const cy = rect.top + rect.height / 2;
        const rx = (e.clientY - cy) / 22;
        const ry = -(e.clientX - cx) / 22;
        phone.style.transform = `rotateX(${Math.max(-12, Math.min(12, rx))}deg) rotateY(${Math.max(-12, Math.min(12, ry))}deg)`;
    });

    phone.addEventListener("click", () => {
        phone.classList.remove("ringing");
        void phone.offsetWidth;
        phone.classList.add("ringing");
    });
}

function initThreeScene() {
    const reduced = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    const mobile = window.matchMedia("(max-width: 760px)").matches;
    const mount = document.getElementById("hero-canvas");

    if (reduced || mobile || !window.THREE || !mount) {
        document.body.classList.add("reduced-motion");
        mount.innerHTML = "<div style='padding: 2rem; font-family: Rajdhani, sans-serif; color: #00ff41;'>WebGL fallback active</div>";
        return;
    }

    const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
    const camera = new THREE.PerspectiveCamera(55, mount.clientWidth / mount.clientHeight, 0.1, 100);
    const scene = new THREE.Scene();
    renderer.setSize(mount.clientWidth, mount.clientHeight);
    renderer.setPixelRatio(Math.min(2, window.devicePixelRatio));
    mount.appendChild(renderer.domElement);

    camera.position.set(0, 0.2, 4.8);

    const ambient = new THREE.AmbientLight(0xffffff, 0.35);
    const point = new THREE.PointLight(0xff00ff, 1.4, 18);
    point.position.set(2, 2, 3);
    scene.add(ambient, point);

    const hubGroup = new THREE.Group();
    scene.add(hubGroup);

    const globe = new THREE.Mesh(
        new THREE.SphereGeometry(1, 28, 28),
        new THREE.MeshBasicMaterial({ color: 0x00ff41, wireframe: true })
    );
    hubGroup.add(globe);

    const headset = new THREE.Mesh(
        new THREE.TorusGeometry(1.35, 0.07, 18, 80),
        new THREE.MeshBasicMaterial({ color: 0xff00ff, wireframe: true })
    );
    headset.rotation.x = Math.PI / 2.25;
    hubGroup.add(headset);

    const orbitGroup = new THREE.Group();
    scene.add(orbitGroup);

    for (let i = 0; i < 9; i += 1) {
        const node = new THREE.Mesh(
            new THREE.SphereGeometry(0.07, 12, 12),
            new THREE.MeshStandardMaterial({ color: i % 2 ? 0x00ff41 : 0xffb800, emissive: i % 2 ? 0x00ff41 : 0xffb800, emissiveIntensity: 0.3 })
        );
        const angle = (Math.PI * 2 * i) / 9;
        node.position.set(Math.cos(angle) * 2.1, Math.sin(angle * 1.2) * 1.15, Math.sin(angle) * 1.1);
        orbitGroup.add(node);
    }

    const clock = new THREE.Clock();
    let targetSpin = 0;
    let tabVisible = true;

    const renderFrame = () => {
        if (!tabVisible) {
            requestAnimationFrame(renderFrame);
            return;
        }

        const dt = clock.getDelta();
        targetSpin += (window.scrollY * 0.00009 - targetSpin) * 0.06;
        hubGroup.rotation.y += 0.35 * dt + targetSpin;
        hubGroup.rotation.x = Math.sin(performance.now() * 0.00065) * 0.2;
        orbitGroup.rotation.y += dt * (0.55 + scrollVelocity / 1200);
        orbitGroup.rotation.z += dt * (0.18 + scrollVelocity / 3000);
        renderer.render(scene, camera);
        requestAnimationFrame(renderFrame);
    };

    renderFrame();

    document.addEventListener("visibilitychange", () => {
        tabVisible = document.visibilityState === "visible";
    });

    window.addEventListener("resize", () => {
        if (!mount.clientWidth || !mount.clientHeight) {
            return;
        }
        camera.aspect = mount.clientWidth / mount.clientHeight;
        camera.updateProjectionMatrix();
        renderer.setSize(mount.clientWidth, mount.clientHeight);
    });

    sceneContext = { renderer };
}

function initScrollFx() {
    window.addEventListener("scroll", updateScrollVelocity, { passive: true });
    updateScrollVelocity();

    if (window.gsap && window.ScrollTrigger) {
        gsap.registerPlugin(ScrollTrigger);
        gsap.from(".hero-title", {
            opacity: 0,
            y: 55,
            duration: 1,
            ease: "power2.out"
        });

        gsap.utils.toArray(".deck-panel").forEach((panel) => {
            gsap.from(panel, {
                opacity: 0,
                y: 24,
                duration: 0.65,
                scrollTrigger: {
                    trigger: panel,
                    start: "top 88%"
                }
            });
        });
    }
}

function initCursorGlow() {
    document.addEventListener("mousemove", (e) => {
        const x = `${(e.clientX / window.innerWidth) * 100}%`;
        const y = `${(e.clientY / window.innerHeight) * 100}%`;
        document.documentElement.style.setProperty("--cursor-x", x);
        document.documentElement.style.setProperty("--cursor-y", y);
    });
}

window.addEventListener("beforeunload", () => {
    if (sceneContext?.renderer) {
        sceneContext.renderer.dispose();
    }
});

initTerminalMode();
initCursorGlow();
initMetricCharge();
initParallaxTowers();
initPhoneHandset();
initScrollFx();
initThreeScene();
loadTickets();

window.login = login;
window.searchKB = searchKB;
window.simulateCall = simulateCall;
window.createTicket = createTicket;
window.loadTickets = loadTickets;
window.updateStatus = updateStatus;
window.escalate = escalate;
window.generateReport = generateReport;
window.loadEvaluations = loadEvaluations;
