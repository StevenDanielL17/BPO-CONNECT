document.addEventListener("DOMContentLoaded", function() {
    const simulateCallBtn = document.getElementById("simulate-call-btn");
    const aniInput = document.getElementById("ani-input");
    const screenPopResult = document.getElementById("screen-pop-result");

    if (simulateCallBtn) {
        simulateCallBtn.addEventListener("click", async function() {
            const ani = aniInput.value;
            if (!ani) {
                showResult("Please enter a phone number.");
                return;
            }

            try {
                const customer = await ApiClient.post("/call", { ani });
                if (customer) {
                    showResult(`Screen Pop<br>Customer: ${customer.customerName}<br>Email: ${customer.email}<br>Status: ${customer.accountStatus}`);
                    document.getElementById("cust-id").value = customer.customerId;
                } else {
                    showResult("Unknown caller. Create a new customer profile.");
                    document.getElementById("cust-id").value = "NEW_CUST";
                }
            } catch (error) {
                console.error("Call simulation error:", error);
                showResult("Failed to simulate call.");
            }
        });
    }

    function showResult(message) {
        if (screenPopResult) {
            screenPopResult.innerHTML = `<strong>${message}</strong>`;
            screenPopResult.style.display = "block";
        }
    }
});