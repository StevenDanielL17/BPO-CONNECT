document.addEventListener("DOMContentLoaded", function() {
    const searchBtn = document.getElementById("search-kb-btn");
    const recentBtn = document.getElementById("kb-recent-btn");
    const createBtn = document.getElementById("kb-create-btn");
    const insertBtn = document.getElementById("kb-insert-btn");
    const copyBtn = document.getElementById("kb-copy-btn");
    const shareBtn = document.getElementById("kb-share-btn");
    const queryInput = document.getElementById("kb-query");
    const resultsEl = document.getElementById("kb-results");
    const statusEl = document.getElementById("kb-status");
    let articlesCache = [];
    let selectedArticle = null;

    if (searchBtn) {
        searchBtn.addEventListener("click", searchKB);
    }

    if (recentBtn) {
        recentBtn.addEventListener("click", loadRecentArticles);
    }

    if (createBtn) {
        createBtn.addEventListener("click", createArticle);
    }

    if (insertBtn) {
        insertBtn.addEventListener("click", insertIntoTicket);
    }

    if (copyBtn) {
        copyBtn.addEventListener("click", copyResolutionSteps);
    }

    if (shareBtn) {
        shareBtn.addEventListener("click", shareWithTeam);
    }

    if (queryInput) {
        queryInput.addEventListener("keypress", function(e) {
            if (e.key === "Enter") {
                searchKB();
            }
        });
    }

    loadRecentArticles();

    async function searchKB() {
        const query = queryInput.value.trim();
        if (!query) {
            await loadRecentArticles();
            return;
        }

        try {
            const articles = await ApiClient.get(`/kb/search?query=${encodeURIComponent(query)}`);
            articlesCache = Array.isArray(articles) ? articles : [];
            renderResults(articlesCache, `Showing ${articlesCache.length} article(s) for "${query}".`);
        } catch (error) {
            console.error("KB search error:", error);
            showStatus(error.message || "Knowledge base search failed.");
        }
    }

    async function loadRecentArticles() {
        try {
            const articles = await ApiClient.get("/kb/articles");
            articlesCache = Array.isArray(articles) ? articles : [];
            renderResults(articlesCache, `Loaded ${articlesCache.length} recent article(s).`);
        } catch (error) {
            console.error("KB recent load error:", error);
            showStatus(error.message || "Failed to load recent articles.");
        }
    }

    function renderResults(articles, message) {
        if (!resultsEl) return;

        if (message) {
            showStatus(message);
        }

        if (articles.length > 0) {
            const selectedStillVisible = selectedArticle && articles.some(function (article) {
                return article.articleId === selectedArticle.articleId;
            });
            if (!selectedStillVisible) {
                selectedArticle = articles[0];
            }

            resultsEl.innerHTML = articles.map(function (article) {
                const isSelected = selectedArticle && selectedArticle.articleId === article.articleId;
                return `
                    <article class="timeline-item" style="background:${isSelected ? '#f8fafc' : 'transparent'}; border-radius: 10px; padding: 12px;">
                        <div style="display:flex; justify-content:space-between; gap:12px; align-items:start;">
                            <div>
                                <strong>${escapeHtml(article.title || 'Untitled')}</strong><br>
                                <small>${escapeHtml(article.category || 'General')} ${article.articleId ? `| ${escapeHtml(article.articleId)}` : ''}</small>
                            </div>
                            <button class="btn btn-secondary" style="padding: 6px 10px; font-size: 0.85rem;" data-select-article="${escapeHtml(article.articleId || '')}">Select</button>
                        </div>
                        <p style="margin-top: 10px; color: #475569;">${escapeHtml(article.content || '')}</p>
                        <div class="actions" style="margin-top: 10px; gap: 8px; flex-wrap: wrap;">
                            <button class="btn btn-primary" style="padding: 6px 10px; font-size: 0.85rem;" data-insert-article="${escapeHtml(article.articleId || '')}">Insert into Ticket</button>
                            <button class="btn btn-secondary" style="padding: 6px 10px; font-size: 0.85rem;" data-copy-article="${escapeHtml(article.articleId || '')}">Copy Resolution Steps</button>
                            <button class="btn btn-secondary" style="padding: 6px 10px; font-size: 0.85rem;" data-share-article="${escapeHtml(article.articleId || '')}">Share with Team</button>
                        </div>
                    </article>
                `;
            }).join("");

            resultsEl.querySelectorAll("[data-select-article]").forEach(function (button) {
                button.addEventListener("click", function () {
                    const articleId = button.getAttribute("data-select-article");
                    selectArticle(articleId);
                });
            });

            resultsEl.querySelectorAll("[data-insert-article]").forEach(function (button) {
                button.addEventListener("click", function () {
                    insertIntoTicket(button.getAttribute("data-insert-article"));
                });
            });

            resultsEl.querySelectorAll("[data-copy-article]").forEach(function (button) {
                button.addEventListener("click", function () {
                    copyResolutionSteps(button.getAttribute("data-copy-article"));
                });
            });

            resultsEl.querySelectorAll("[data-share-article]").forEach(function (button) {
                button.addEventListener("click", function () {
                    shareWithTeam(button.getAttribute("data-share-article"));
                });
            });
        } else {
            resultsEl.innerHTML = '<div class="status-banner">No articles found.</div>';
            selectedArticle = null;
        }
    }

    function selectArticle(articleId) {
        const article = articlesCache.find(function (item) {
            return item.articleId === articleId;
        });
        if (article) {
            selectedArticle = article;
            showStatus(`Selected: ${article.title}`);
        }
    }

    async function createArticle() {
        const title = window.prompt("Article title:");
        if (!title) return;

        const category = window.prompt("Category:", "General") || "General";
        const tags = window.prompt("Tags (comma-separated):", category) || category;
        const content = window.prompt("Resolution steps / content:");
        if (!content) return;

        try {
            const article = await ApiClient.post("/kb/articles", { title, category, tags, content });
            selectedArticle = article;
            await loadRecentArticles();
            showStatus(`Article saved: ${article.title}`);
        } catch (error) {
            console.error("Create article error:", error);
            showStatus(error.message || "Unable to create article.");
        }
    }

    async function insertIntoTicket(articleId) {
        const article = resolveArticle(articleId);
        if (!article) {
            showStatus("Select an article first.");
            return;
        }

        sessionStorage.setItem("bpoConnect.ticketDraft", JSON.stringify({
            source: "Knowledge Base",
            title: article.title,
            content: article.content,
            category: article.category,
            tags: article.tags,
            customerId: "",
            channel: "general",
            severity: "Medium",
            description: `${article.title}\n\n${article.content}`
        }));
        window.location.href = "tickets.html#new-ticket";
    }

    async function copyResolutionSteps(articleId) {
        const article = resolveArticle(articleId);
        if (!article) {
            showStatus("Select an article first.");
            return;
        }

        const text = `${article.title}\n\n${article.content}`;
        try {
            if (navigator.clipboard && navigator.clipboard.writeText) {
                await navigator.clipboard.writeText(text);
            }
            showStatus(`Copied: ${article.title}`);
        } catch (error) {
            console.error("Copy error:", error);
            showStatus("Copy failed.");
        }
    }

    async function shareWithTeam(articleId) {
        const article = resolveArticle(articleId);
        if (!article) {
            showStatus("Select an article first.");
            return;
        }

        try {
            await ApiClient.post("/chat/messages", {
                recipientId: "L201",
                message: `KB share: ${article.title} - ${article.content}`
            });
            showStatus(`Shared with team: ${article.title}`);
        } catch (error) {
            console.error("Share error:", error);
            showStatus(error.message || "Unable to share article.");
        }
    }

    function resolveArticle(articleId) {
        if (!articleId) {
            return selectedArticle || articlesCache[0] || null;
        }

        return articlesCache.find(function (article) {
            return article.articleId === articleId;
        }) || selectedArticle || articlesCache[0] || null;
    }

    function showStatus(message) {
        if (statusEl) {
            statusEl.textContent = message;
        }
    }

    function escapeHtml(value) {
        return String(value ?? "")
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/\"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }
});