(function () {
    const page = document.body.getAttribute("data-page");
    if (!page) {
        return;
    }

    const selectors = ["[data-nav]", "[data-mobile-nav]"];
    selectors.forEach(function (selector) {
        document.querySelectorAll(selector).forEach(function (node) {
            if (node.getAttribute("data-route") === page) {
                node.classList.add("active");
                node.setAttribute("aria-current", "page");
            }
        });
    });
})();
