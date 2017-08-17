// List of all tables
var tables = [];

// Formatters
rivets.formatters.padZeroes = function (value) {
    return value < 10 ? "0" + value : value;
};
rivets.formatters.nth = function (value) {
    if(value > 10 && value < 20)
        return "th";

    value = value % 10;
    var map = ["th", "st", "nd", "rd", "th"];
    return map[Math.min(value, map.length - 1)];
};
rivets.formatters.time = function (value) {
    if(!(value instanceof Date))
        value = new Date(value * 1000);
    return rivets.formatters.padZeroes(value.getHours()) + ":" + rivets.formatters.padZeroes(value.getMinutes());
};
rivets.formatters.shortDate = function (value) {
    if(!(value instanceof Date))
        value = new Date(value * 1000);

    var months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
    return months[value.getMonth()] + " " + value.getDate() + rivets.formatters.nth(value.getDate());
};

// Identify the opening and closing UNIX times
var openTime = 10;
var closeTime = 20;
var openDateTime = new Date();
var closeDateTime = new Date();
openDateTime.setHours(openTime - 1);
openDateTime.setMinutes(59);
closeDateTime.setHours(closeTime - 1);
closeDateTime.setMinutes(59);
openDateTime = Math.ceil(openDateTime.getTime() / 1000);
closeDateTime = Math.ceil(closeDateTime.getTime() / 1000);

// Viewmodel for the main screen
var vmMainScreen = {};
vmMainScreen.now = new Date();

/**
 * Refresh the list of bookings.
 */
function refreshBooking() {
    server.bookings.retrieve(undefined, {startingTime: openDateTime, endingTime: closeDateTime}).then(
        function (result) {
            var bookings = result.bookings;

            // Date and time grid
            var granularity = 15;
            var timescale = [];
            var hours = [];
            // Create time intervals
            for (var hour = openTime; hour < closeTime; hour++) {
                // Save all the opening hours
                if (hours.indexOf(hour) == -1)
                    hours.push(hour);

                for (var minutes = 0; minutes < 60; minutes = minutes + 15) {
                    var interval = {hour: hour, mins: minutes, tables: []};
                    // For each interval add a table
                    tables.forEach(function (table) {
                        table = Object.assign({}, table);
                        interval.tables.push(table);
                        // For each table find appropriate bookings
                        table.bookings = bookings.filter(function (booking) {
                            var date = new Date(booking.date * 1000);
                            return booking.table == table.id && date.getHours() == hour && date.getMinutes() == minutes;
                        });
                    });
                    timescale.push(interval);
                }
            }

            // Create the model for the view
            vmMainScreen.tables = tables;
            vmMainScreen.hours = hours;
            vmMainScreen.granularity = granularity;
            vmMainScreen.timescale = timescale;
            vmMainScreen.bookings = bookings;
        }).catch(
        function (ex) {
            ex.errorMessage = ex.errorMessage || "An error occured";
            alert("Failed to load bookings: " + ex.errorMessage);
        });
}

/**
 * Create a new booking.
 * @param e Click event.
 */
function createBooking(e) {
    // Identify the grid slots
    var slot = e.target;
    var row = slot.parentNode;

    // Identify the time and date of a booking
    vmMainScreen.activeSlot.hour = row.getAttribute("hours");
    vmMainScreen.activeSlot.mins = row.getAttribute("mins");
    vmMainScreen.activeSlot.table = slot.getAttribute("table");

    // Ensure full booking can fit in before closing time
    if(closeTime - (parseInt(vmMainScreen.activeSlot.hour) + vmMainScreen.activeSlot.mins / 60) < 2)
        return;

    // Display booking creation form
    var bounds = slot.getBoundingClientRect();
    var form = document.getElementById("bookingCreation");
    var fBounds = form.getBoundingClientRect();
    if(bounds.top + fBounds.height < window.innerHeight)
        form.style.top = bounds.top + "px";
    else
        form.style.top = (window.innerHeight - fBounds.height) + "px";
    form.style.left = bounds.left + "px";
    form.style.width = bounds.width + "px";
    form.classList.remove("hidden");
}


/**
 * Cancel booking creation.
 * @param e Click event.
 */
function cancelBooking(e) {
    // Hide previously opened form
    var form = document.getElementById("bookingCreation");
    form.classList.add("hidden");
    // Reset the input
    form.reset();
}

/**
 * Finish booking creation.
 * @param e Click event.
 */
function confirmBooking(e) {
    // Retrieve the booking information
    var name = document.getElementById("name").value;
    var phone = document.getElementById("phone").value;
    var party = document.getElementById("party").value;
    var email = document.getElementById("email").value;
    // Create a booking object and send the information off
    party = parseInt(party);
    var today = new Date(openDateTime * 1000);
    var date = new Date(today.getFullYear(), today.getMonth(), today.getDate(), vmMainScreen.activeSlot.hour, vmMainScreen.activeSlot.mins, 0, 0);

    var booking = new Booking(name, phone, email, party, date, vmMainScreen.activeSlot.table);
    BookingService.make(booking).then(
        function (val) {
            alert("Booking created, customer reference is " + val.referenceNumber);
            refreshBooking();
            cancelBooking();
        }).catch(
        function (ex) {
            alert(ex.errorMessage);
            cancelBooking();
        });
}

/**
 * View the bookings before the current date.
 * @param e Click event.
 */
function bookingBefore(e) {
    // Do not allow viewing bookings in the past
    var now = Math.ceil(new Date().getTime() / 1000);
    if(closeDateTime - 86400 * 2 < now)
        document.getElementById("bookingBefore").classList.remove("show");
    if(closeDateTime - 86400 < now)
        return;

    // Advance the unix time by one day
    openDateTime -= 86400;
    closeDateTime -= 86400;
    vmMainScreen.now = new Date(vmMainScreen.now.getFullYear(), vmMainScreen.now.getMonth(), vmMainScreen.now.getDate() - 1, vmMainScreen.now.getHours(), vmMainScreen.now.getMinutes());
    refreshBooking();
}

/**
 * View the bookings after the current date.
 * @param e Click event.
 */
function bookingAfter(e) {
    // Do not allow viewing bookings in the past
    var now = Math.ceil(new Date().getTime() / 1000);
    if(closeDateTime - 86400 < now)
        document.getElementById("bookingBefore").classList.add("show");

    // Advance the unix time by one day
    openDateTime += 86400;
    closeDateTime += 86400;
    vmMainScreen.now = new Date(vmMainScreen.now.getFullYear(), vmMainScreen.now.getMonth(), vmMainScreen.now.getDate() + 1, vmMainScreen.now.getHours(), vmMainScreen.now.getMinutes());
    refreshBooking();
}

/**
 * Close the current application.
 * @param e Click event.
 */
function appClose(e) {
    close();
}

// Perform data binding
document.addEventListener("DOMContentLoaded", function() {
    // Setup view model
    refreshBooking();
    vmMainScreen.activeSlot = {
        hour: 0,
        mins: 0
    };
    server.tables.retrieve(undefined, {}).then(function (result) {
        tables = result.tables;
    });
    rivets.bind(document.getElementById("mainScreen"), vmMainScreen);

    // Periodically refresh the model
    setInterval(refreshBooking, 1000);
    setInterval(function () {
        var now = new Date();
        vmMainScreen.now.setHours(now.getHours());
        vmMainScreen.now.setMinutes(now.getMinutes());
    }, 500);
});