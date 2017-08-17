// Viewmodel for the report screen
var vmMonthSummary = {};
var vmDaySummary = {};
vmMonthSummary.weeks = [];

function month(index) {
    return ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"][index];
}

function dow(dayOfWeek) {
    return (dayOfWeek + 6) % 7;
}

function reportDateRange(firstTime, lastTime) {
    return new Promise(function (resolve, reject) {
        server.bookings.retrieve(undefined, {startingTime: firstTime, endingTime: lastTime}).then(function (result) {
            // Load all the bookings
            var bookings = result.bookings;
            // Load all the orders
            var promises = bookings.map(function (booking) {
                return server.orders.retrieve(null, {id: booking.referenceNumber});
            });
            Promise.all(promises).then(function (orders) {
                for (var i = 0; i < orders.length; i++) {
                    bookings[i].order = orders[i];
                    orders[i].total = orders[i].meals.reduce(function (total, meal) {
                        return total + meal.price;
                    }, 0);
                }

                resolve(bookings);
            });
        });
    });
}

/**
 * Create a report for a given month.
 * @param date Date.
 */
function reportMonth(date) {
    var i;
    vmMonthSummary.date = date;
    vmMonthSummary.month = month(date.getMonth());
    var firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
    var lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);
    var firstTime = Math.ceil(firstDay.getTime() / 1000);
    var lastTime = Math.ceil(lastDay.getTime() / 1000);

    reportDateRange(firstTime, lastTime).then(function (bookings) {
        // Create the first week
        var firstDow = dow(firstDay.getDay());
        var week = [];
        vmMonthSummary.weeks = [week];
        vmMonthSummary.total = 0;
        for (i = 0; i < firstDow; i++) {
            week.push({
                day: "",
                total: "",
                empty: true
            });
        }

        // Create following weeks
        for (i = 1; i <= lastDay.getDate(); i++) {
            // Identify unix time for start and end of the day
            var startTime = Math.ceil(new Date(lastDay.getFullYear(), lastDay.getMonth(), i, 0, 1).getTime() / 1000);
            var endTime = Math.ceil(new Date(lastDay.getFullYear(), lastDay.getMonth(), i, 23, 59).getTime() / 1000);
            // Calculate total takings of the day
            var total = bookings
                .filter(function (booking) {
                    return booking.date > startTime && booking.date < endTime
                })
                .reduce(function (total, booking) {
                    return total + booking.order.total
                }, 0);
            vmMonthSummary.total += total;

            // Record day and total taking
            week.push({
                day: i,
                total: total,
                empty: total == 0
            });

            // Switch to the next week upon reaching Sunday
            if (dow(new Date(date.getFullYear(), date.getMonth(), i).getDay()) == 6) {
                week = [];
                vmMonthSummary.weeks.push(week);
            }
        }

        // Create the last week
        var lastDow = dow(lastDay.getDay());
        for (i = lastDow; i < 6; i++) {
            week.push({
                day: "",
                total: "",
                empty: true
            });
        }
    });
}

/**
 * Open a report screen.
 * @param e Event.
 */
function reportOpen(e) {
    document.getElementById("reportScreen").classList.add("show");
    reportMonth(new Date());
}

/**
 * Update the date on the report screen to the previous month.
 * @param e Event.
 */
function reportBefore(e) {
    vmMonthSummary.date.setMonth(vmMonthSummary.date.getMonth() - 1);
    reportMonth(vmMonthSummary.date);
}

/**
 * Update the date on the report screen to the next month.
 * @param e Event.
 */
function reportAfter(e) {
    vmMonthSummary.date.setMonth(vmMonthSummary.date.getMonth() + 1);
    reportMonth(vmMonthSummary.date);
}

/**
 * Close a report screen.
 * @param e Event.
 */
function reportClose(e) {
    document.getElementById("reportScreen").classList.remove("show");
}

/**
 * Open a day summary popup.
 * @param e Event.
 */
function dayOpen(e) {
    // Retrieve the context
    var day = e.currentTarget.querySelector(".day").textContent;
    var total = e.currentTarget.querySelector(".total").textContent;
    if(total.trim() == "")
        return;

    // Build the view model and display it
    vmDaySummary.dayOfMonth = day;
    vmDaySummary.month = vmMonthSummary.month;
    vmDaySummary.total = total;

    var date = vmMonthSummary.date;
    var firstDay = new Date(date.getFullYear(), date.getMonth(), day);
    var lastDay = new Date(date.getFullYear(), date.getMonth(), day, 23, 59);
    var firstTime = Math.ceil(firstDay.getTime() / 1000);
    var lastTime = Math.ceil(lastDay.getTime() / 1000);
    reportDateRange(firstTime, lastTime).then(function (bookings) {
        bookings = bookings.sort(function (a, b) {
            return a.date - b.date;
        });
        vmDaySummary.bookings = bookings;
        bookings.forEach(function (booking) {
            // Identify the booking table
            var table = vmMainScreen.tables.filter(function(table) { return booking.table == table.id })[0];
            booking.table = table;

            // Group meals and count total meals ordered
            var ids = booking.order.meals.map(function (meal) { return meal.id })
                .filter(function (e, i, arr) { return arr.lastIndexOf(e) === i; });
            var items = ids.map(function (id) {
                var meals = booking.order.meals.filter(function (meal) { return meal.id == id; });
                meals[0].count = meals.length;
                meals[0].price *= meals.length;
                return meals[0];
            });
            booking.items = items;
        });

        document.getElementById("daySummary").classList.add("show");
    });
}

/**
 * Close a day summary popup.
 * @param e Event.
 */
function dayClose(e) {
    document.getElementById("daySummary").classList.remove("show");
}

// Perform data binding
document.addEventListener("DOMContentLoaded", function() {
    // Setup view model
    rivets.bind(document.getElementById("monthSummary"), vmMonthSummary);
    rivets.bind(document.getElementById("daySummary"), vmDaySummary);
});