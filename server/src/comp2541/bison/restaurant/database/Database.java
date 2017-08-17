package comp2541.bison.restaurant.database;

import java.util.ArrayList;

import comp2541.bison.restaurant.data.Booking;
import comp2541.bison.restaurant.data.Meal;
import comp2541.bison.restaurant.data.Order;
import comp2541.bison.restaurant.data.Table;

/**
 * Database abstract class that defines the general usage of the database.
 * @author Ilyass Taouil
 */
public abstract class Database {

	/**
	 * Create a new database.
	 * @param dbName Name for the new database.
	 */
	public Database(String dbName){}

	/**
	 * Insert a booking into the database.
	 * @param booking Booking object.
	 * @return referenceID Booking reference.
	 * @throws Exception Exception thrown in case of DB failure.
	 */
	public abstract int insertBooking(Booking booking) throws Exception;

	/**
	 * Insert an order into the database.
	 * @param order Order object.
	 * @throws Exception Exception thrown in case of DB failure.
	 */
	public abstract void insertOrder(Order order) throws Exception;

	/**
	 * Retrieve the bookings within the given date/time range.
	 * @param startTime Unix start time.
	 * @param endTime Unix end time.
	 * @return ArrayList<Booking> List of bookings that match the criteria.
	 * @throws Exception Exception thrown in case of DB failure.
	 */
	public abstract ArrayList<Booking> getBookings(long startTime, long endTime) throws Exception;

	/**
	 * Retrieve all the available tables in the given date/time range.
	 * @param startTime Unix start time.
	 * @param endTime Unix end time.
	 * @return ArrayList<Table> List of tables that match the criteria
	 * @throws Exception Exception thrown in case of DB failure.
	 */
	public abstract ArrayList<Table> getAvailableTables(long startTime, long endTime) throws Exception;

	/**
	 * Retrieve all the available meals.
	 * @return ArrayList<Meal> List of meals orderable.
	 * @throws Exception Exception thrown in case of DB failure.
	 */
	public abstract ArrayList<Meal> getMeals() throws Exception;

	/**
	 * Retrieve the meals ordered in the given booking.
	 * @param booking Booking.
	 * @return ArrayList<Meal> List of meals order in the booking.
	 * @throws Exception Exception thrown in case of DB failure.
	 */
	public abstract ArrayList<Meal> getOrderedMeals(Booking booking) throws Exception;

	/**
	 * Retrieve all the tables in the restaurant.
	 * @return ArrayList<Tables> List of all the tables.
	 * @throws Exception Exception thrown in case of DB failure.
	 */
	public abstract ArrayList<Table> getTables() throws Exception;

	/**
	 * Remove all the ordered meals within the given booking.
	 * @param booking Booking.
	 * @throws Exception Exception thrown in case of DB failure.
	 */
	public abstract void removeAllOrders(Booking booking) throws Exception;

}