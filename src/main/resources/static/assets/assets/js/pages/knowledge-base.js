document.addEventListener("DOMContentLoaded", function() {
    const searchBtn = document.getElementById("search-kb-btn");
    const queryInput = document.getElementById("kb-query");
    const resultsEl = document.getElementById("kb-results");

    if (searchBtn) {
        searchBtn.addEventListener("click", searchKB);
    }

    if (queryInput) {
        queryInput.addEventListener("keypress", function(e) {
            if (e.key === "Enter") {
                searchKB();
            }
        });
    }

    async function searchKB() {
        const query = queryInput.value;
        if (!query) return;

        try {
            const articles = await ApiClient.get(`/kb/search?query=${encodeURIComponent(query)}`);
            renderResults(articles);
        } catch (error) {
            console.error("KB search error:", error);
            showResults("Knowledge base search failed.");
        }
    }

    function renderResults(articles) {
        if (!resultsEl) return;

        if (articles.length > 0) {
            const html = articles.map(a => `<strong>${a.title}</strong><br>${a.content}<hr>`).join("");
            showResults(html);
        } else {
            showResults("No articles found.");
        }
    }

    function showResults(html) {
        if (resultsEl) {
            resultsEl.innerHTML = html;
            resultsEl.style.display = "block";
        }
    }
});