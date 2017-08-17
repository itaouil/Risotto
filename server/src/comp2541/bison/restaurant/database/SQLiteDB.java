package comp2541.bison.restaurant.database;

import java.sql.*;
import java.util.ArrayList;
import org.apache.log4j.*;

import comp2541.bison.restaurant.data.Booking;
import comp2541.bison.restaurant.data.Meal;
import comp2541.bison.restaurant.data.Order;
import comp2541.bison.restaurant.data.Table;


/**
 * Java class that defines the methods of an SQLite database.
 * @author Ilyass Taouil.
 */

public class SQLiteDB extends Database {

	/**
	 * Logger file 
	 */
	static Logger log = Logger.getLogger(SQLiteDB.class.getName());

	/**
	 * String dbName
	 */
	private String dbName = null;

	/**
	 * SQLiteDB Constructor.
	 * @param dbName.
	 * @throws Exception.
	 */
	public SQLiteDB(String dbName) throws Exception {
		super(dbName);

		this.dbName = dbName;

		// Setting Up DB
		setUp();
	}

	/**
	 * Creates Database tables.
	 * @throws Exception.
	 */
	private void setUp() throws Exception {

		// Load the sqlite-JDBC driver
		Class.forName("org.sqlite.JDBC");

		// Queries
		String table = "CREATE TABLE IF NOT EXISTS RestaurantTable "	+
					   "(ID INTEGER PRIMARY KEY NOT NULL,"				+
					   " description TEXT NOT NULL,"					+
					   " size INTEGER NOT NULL"							+ 
					   ");";

		String booking = "CREATE TABLE IF NOT EXISTS Booking "					+
						 "(ID INTEGER PRIMARY KEY AUTOINCREMENT,"				+
						 " customerName TEXT NOT NULL,"							+
						 " phoneNumber TEXT NOT NULL,"							+ 
						 " email TEXT NOT NULL,"								+
						 " partySize INTEGER NOT NULL,"							+  
						 " unixStart NUMERIC NOT NULL,"							+
						 " unixEnd   NUMERIC NOT NULL,"							+
						 " tableID INTEGER,"									+
						 " FOREIGN KEY(tableID) REFERENCES RestaurantTable(ID)"	+
						 ");";

		String meal = "CREATE TABLE IF NOT EXISTS Meal "	+
					  "(ID INTEGER PRIMARY KEY NOT NULL,"	+
					  " name TEXT NOT NULL,"				+
					  " description TEXT NOT NULL,"			+ 
					  " price INTEGER NOT NULL,"			+  
					  " category TEXT NOT NULL"				+
					  ");";

		String order = "CREATE TABLE IF NOT EXISTS RestaurantOrder "	+
					   "(ID INTEGER PRIMARY KEY AUTOINCREMENT,"			+
					   " mealID INTEGER NOT NULL,"						+
					   " bookingID INTEGER NOT NULL,"					+ 
					   " FOREIGN KEY(mealID) REFERENCES Meal(ID),"		+
					   " FOREIGN KEY(bookingID) REFERENCES Booking(ID)"	+
					   ");";

		// Create connections
		try(Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			Statement stmt = conn.createStatement())
		{
			// Queries execution
			stmt.executeUpdate(table);
			stmt.executeUpdate(booking);
			stmt.executeUpdate(meal);
			stmt.executeUpdate(order);

			log.info("Tables created successfully");
		}
	}

	/**
	 * Insert a new booking in the Database.
	 * @param booking.
	 * @throws Exception.
	 */
	@Override
	public synchronized int insertBooking(Booking booking) throws Exception {

		int ref = -1;

		// Queries
		String maxID = "SELECT MAX(ID) FROM Booking;";

		String insert = "INSERT INTO Booking(customerName, phoneNumber, email, partySize, unixStart, unixEnd, tableID) " +
						"VALUES(?, ?, ?, ?, ?, ?, ?);";																	

		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			PreparedStatement pstmt = conn.prepareStatement(insert);
			Statement stmt = conn.createStatement())
		{
			// Prepared Statement insertion
			pstmt.setString(1, booking.getCustomerName());
			pstmt.setString(2, booking.getPhoneNumber());
			pstmt.setString(3, booking.getEmail());
			pstmt.setInt(4, booking.getPartySize());
			pstmt.setLong(5, booking.getUnixStart());
			pstmt.setLong(6, booking.getUnixEnd());
			pstmt.setInt(7, booking.getTable().getId());

			// Queries execution
			pstmt.executeUpdate();
			ResultSet rs = stmt.executeQuery(maxID);

			// Read Result Set
			if(rs.next()) {
				ref = rs.getInt(1);
			}

			log.info("booking inserted");

			return ref;
		}
	}

	/**
	 * Inserts new order in the Database.
	 * @param order (Order object).
	 * @throws Exception.
	 */
	@Override
	public synchronized void insertOrder(Order order) throws Exception {

		// Queries
		String insert = "INSERT INTO RestaurantOrder(mealID, bookingID)" +
						"VALUES(?, ?);";

		String mealID = "SELECT ID FROM Meal " +
						"WHERE ID = ?;";

		String bookingID = "SELECT ID FROM Booking " +
						   "WHERE ID = ?;";

		//Create Connections
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			PreparedStatement pstmt1  = conn.prepareStatement(insert);
			PreparedStatement pstmt2 = conn.prepareStatement(mealID);
			PreparedStatement pstmt3 = conn.prepareStatement(bookingID))
		{
			// Prepared Statement insertion
			pstmt1.setInt(1, order.getMealId());
			pstmt1.setInt(2, order.getBookingId());
			pstmt2.setInt(1, order.getMealId());
			pstmt3.setInt(1, order.getBookingId());

			// Queries execution
			ResultSet rs1 = pstmt2.executeQuery();
			ResultSet rs2 = pstmt3.executeQuery();

			// Read Result Set
			if(rs1.next() && rs2.next()) {
				pstmt1.executeUpdate();
			}
			else {
				throw new IllegalArgumentException("mealID and/or bookingID invalid");
			}
		}
	}

	/**
	 * Returns all bookings happening between startTime and endTime.
	 * @param startTime.
	 * @param endTime.
	 */
	@Override
	public synchronized ArrayList<Booking> getBookings(long startTime, long endTime) throws Exception {

		ArrayList<Booking> bookings = new ArrayList<>();

		// Query
		String retrieve = "SELECT Booking.*, RestaurantTable.description, RestaurantTable.size FROM"																+
						  " Booking INNER JOIN RestaurantTable" 										+
						  " ON Booking.tableID = RestaurantTable.ID"	 								+
						  " WHERE unixStart >= "	+	startTime	+	" AND unixStart < "	+	endTime	+
						  " OR unixEnd > "		+	startTime	+	" AND unixEnd <= "	+	endTime	+
						  " OR unixStart <= "		+	startTime	+	" AND unixEnd >= "	+	endTime	+
						  " ;";

		// Create Connections
		try(Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			Statement stmt = conn.createStatement())
		{
			// Query execution
			ResultSet rs = stmt.executeQuery(retrieve);

			// Read Result Set
			while(rs.next()) {

				int referenceNumber = rs.getInt("ID");
				String customerName = rs.getString("customerName");
				String phoneNumber = rs.getString("phoneNumber");
				String email = rs.getString("email");
				int partySize = rs.getInt("partySize");
				long unixStart = rs.getLong("unixStart");
				long unixEnd = rs.getLong("unixEnd");
				int tableID = rs.getInt("tableID");
				String description = rs.getString("description");
				int size = rs.getInt("size");

				// Create table object
				Table table = new Table(tableID, description, size);

				// Create Booking object 
				Booking booking = new Booking(referenceNumber, 
						customerName, 
						phoneNumber, 
						email, 
						partySize, 
						unixStart, 
						unixEnd,
						table);

				bookings.add(booking);
			}

			log.info("Booking objects retrieved from database");

			return bookings;
		}
	}

	/**
	 * Returns all available tables between a period of time.
	 * @param startTime.
	 * @param endTime.
	 */
	@Override
	public synchronized ArrayList<Table> getAvailableTables(long startTime, long endTime) throws Exception {

		ArrayList<Table> availableTables = new ArrayList<>();

		// Query
		String retrieve = "SELECT * FROM RestaurantTable"														+
						  " except"																				+
						  " SELECT RestaurantTable.* FROM"														+
						  " RestaurantTable INNER JOIN Booking" 												+
						  " ON RestaurantTable.ID = Booking.tableID" 											+
						  " WHERE Booking.unixStart >= " + startTime + " AND Booking.unixStart < " + endTime 	+
						  " OR Booking.unixEnd > "+ startTime + " AND Booking.unixEnd <= " + endTime 			+
						  " OR Booking.unixStart < " + startTime + " AND Booking.unixEnd > " + endTime			+
						  ";";

		// Create Connections
		try(Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			Statement stmt = conn.createStatement())
		{
			// Query execution
			ResultSet rs = stmt.executeQuery(retrieve);

			// Read Result Set
			while(rs.next()) {

				int referenceNumber = rs.getInt("ID");
				String description = rs.getString("description");
				int size = rs.getInt("size");

				//Create Table object for each table available
				Table table = new Table(referenceNumber, description, size);
				availableTables.add(table);
			}

			log.info("ResturantTable objects retrieved from database");

			return availableTables;
		}
	}

	/**
	 * Returns all meals stored in the Database.
	 * @throws Exception.
	 */
	@Override
	public synchronized ArrayList<Meal> getMeals() throws Exception {

		ArrayList<Meal> meals = new ArrayList<>();

		// Query
		String mealsQuery = "SELECT * FROM Meal;";

		try(Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			Statement stmt = conn.createStatement())
		{
			// Query execution
			ResultSet rs = stmt.executeQuery(mealsQuery);

			// Read Result Set
			while(rs.next()) {

				int referenceNumber = rs.getInt("ID");
				String name = rs.getString("name");
				String description = rs.getString("description");
				int price = rs.getInt("price");
				String category = rs.getString("category");

				//Create Meal instance for each meal 
				Meal meal = new Meal(referenceNumber, name, description, price, category);
				meals.add(meal);
			}

			log.info("Meal objects retrieved from database");

			return meals;
		}
	}

	/**
	 * Returns all meals ordered by a booking.
	 * @param booking (Booking object).
	 * @throws Exception.
	 */
	@Override
	public synchronized ArrayList<Meal> getOrderedMeals(Booking booking) throws Exception {

		ArrayList<Meal> orderedMeals = new ArrayList<>();

		// Query
		String orders = "SELECT Meal.* FROM" 									+
						" RestaurantOrder JOIN Meal" 							+
						" ON RestaurantOrder.mealID = Meal.ID JOIN" 			+
						" Booking ON RestaurantOrder.bookingID = Booking.ID"	+
						" WHERE (RestaurantOrder.bookingID = ?);";

		// Create Connections
		try(Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			PreparedStatement pstmt = conn.prepareStatement(orders))
		{
			// Prepared Statement setting
			pstmt.setInt(1, booking.getReferenceNumber());

			// Query execution
			ResultSet rs = pstmt.executeQuery();

			// Read Result Set
			while(rs.next()) {

				int referenceNumber = rs.getInt("ID");
				String name = rs.getString("name");
				String description = rs.getString("description");
				int price = rs.getInt("price");
				String category = rs.getString("category");

				//Create Meal instance 
				Meal meal = new Meal(referenceNumber, name, description, price, category);
				orderedMeals.add(meal);
			}

			log.info("Meal objects retrieved from database");

			return orderedMeals;
		}
	}

	/**
	 * Returns all tables in the Database.
	 * @throws Exception.
	 */
	@Override
	public synchronized ArrayList<Table> getTables() throws Exception {

		ArrayList<Table> tables = new ArrayList<>();

		// Query
		String tableQuery = "SELECT * FROM RestaurantTable;";

		// Create Connections
		try(Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			Statement stmt = conn.createStatement())
		{
			// Query execution
			ResultSet rs = stmt.executeQuery(tableQuery);

			// Read Result Set
			while(rs.next()) {

				int referenceNumber = rs.getInt("ID");
				String description = rs.getString("description");
				int size = rs.getInt("size");

				//Create Table instance 
				Table table = new Table(referenceNumber, description, size);
				tables.add(table);
			}

			log.info("Table objects retrieved from RestaurantTable");

			return tables;
		}
	}

	/**
	 * Removes all orders for a booking.
	 * @param booking.
	 * @throws Exception.
	 */
	@Override
	public synchronized void removeAllOrders(Booking booking) throws Exception {

		// Query
		String delete = "DELETE FROM RestaurantOrder " 										+
						"WHERE RestaurantOrder.bookingID = " + booking.getReferenceNumber()	+
						";";

		// Create Connections
		try(Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			Statement stmt = conn.createStatement())
		{
			// Query execution
			stmt.executeUpdate(delete);

			log.info("Orders removed");
		}
	}
}