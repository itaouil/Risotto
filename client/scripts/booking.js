/**
 * Create a new booking object.
 * @param customerName Name of the person booking.
 * @param phoneNumber Phone number of the person booking.
 * @param emailAddress Email address of the person booking.
 * @param date Booking date.
 * @param partySize Party size.
 * @param table Table ID.
 * @constructor
 */
var Booking = function (customerName, phoneNumber, emailAddress, partySize, date, table) {
    /**
     * Booking date.
     */
    this.date = date;
    if(this.date instanceof Date)
        this.date = this.date.getTime() / 1000;
    /**
     * How many people booked.
     */
    this.partySize = partySize;
    /**
     * Name of the person booking.
     */
    this.customerName = customerName;
    /**
     * Phone Number of the person booking.
     */
    this.phoneNumber = phoneNumber;
    /**
     * Email Address of the person booking.
     */
    this.emailAddress = emailAddress;
    /**
     * Target table ID.
     */
    this.table = table;
};

/**
 * Service for managing bookings.
 */
var BookingService = {
    /**
     * Make a new booking
     * @param booking Booking instance.
     * @returns Promise that represents the request.
     */
    make: function (booking) {
        return server.bookings.create(booking);
    }
};