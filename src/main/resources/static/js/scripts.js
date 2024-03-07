/**
 * Display commission fees if amount > 0€.
 * @param amount
 */

function calculateFees(amount) {
    const feesId = document.getElementById('fees')
console.log(amount)
    if (amount > 0) {
        const fees = (amount * 0.005).toFixed(2);
        feesId.innerText = `Frais de commission de ${fees} €`;
    } else {
        feesId.innerText = ``;
    }
}
