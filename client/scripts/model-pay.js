// Build the viewmodel
var vmPay = {};
vmPay.total = 0;

// Show months
var i;
vmPay.months = [];
for(i = 1; i <= 12; i++)
    vmPay.months.push(i);

// Show years
vmPay.years = [];
var currentYear = new Date().getFullYear();
for(i = currentYear; i < currentYear + 20; i++)
    vmPay.years.push(i);

// Databind the viewmodel
document.addEventListener("DOMContentLoaded", function() {
    rivets.bind(document.getElementById("amount"), vmMenu);
    rivets.bind(document.getElementById("expiryDate"), vmPay);
});

/**
 * Display payment form.
 */
function displayPayment() {
    document.getElementById("payScreen").classList.add("show");
    document.getElementById("payForm").reset();
}

/**
 * Discard payment.
 */
function discardPayment() {
    document.getElementById("payScreen").classList.remove("show");
    document.getElementById("payForm").reset();
}

/**
 * Confirm payment.
 */
function confirmPayment() {
    document.getElementById("payScreen").classList.remove("show");
    document.getElementById("payForm").reset();
}

/**
 * Ensure that users only enter alpha.
 * @param event Event.
 */
function onlyAlpha(event) {
    return (event.charCode >= 97 && event.charCode <= 122) || event.charCode==32;
}

/**
 * Ensure that users only enter numbers.
 * @param event Event.
 */
function onlyNumber(event) {
    return event.charCode >= 48 && event.charCode <= 57;
}

/**
 * Verify the entered card number.
 * @param event Event.
 */
function verifyCard(event) {
    var target = event.currentTarget;

    var icon = document.getElementById("cardVerify").classList;
    if(target.value.length == 16)
        if(luhnChk(target.value))
            icon.add("show");
        else
            icon.remove("show");
    else
        icon.remove("show");
}

/**
 * Verify the entered CCV.
 * @param event Event.
 */
function verifyCCV(event) {
    var target = event.currentTarget;

    var icon = document.getElementById("ccvVerify").classList;
    if (target.value.length >= 3)
        icon.add("show");
    else
        icon.remove("show");
}

/*
 * Luhn validation algorithm implementation by Shirtless Kirk (used under WTFPL).
 * https://gist.github.com/ShirtlessKirk/2134376
 */
function luhnChk(ccNum) {
    var
        len = ccNum.length,
        bit = 1,
        sum = 0,
        val,
        arr = [0, 2, 4, 6, 8, 1, 3, 5, 7, 9];

    while (len) {
        val = parseInt(ccNum.charAt(--len), 10);
        sum += (bit ^= 1) ? arr[val] : val;
    }

    return sum && sum % 10 === 0;
}