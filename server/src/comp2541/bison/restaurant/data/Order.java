package comp2541.bison.restaurant.data;

import org.json.JSONObject;

/**
 * Class managing orders of a booking (a table in the short period of time).
 * @author Michele Cipriano
 */
public class Order {
	/**
	 * Meal identifier.
	 */
	private int mealId;
	/**
	 * Booking identifier.
	 */
	private int bookingId;
	
	/**
	 * Useful constructor when you want to refer to an order
	 * without knowing the full datas of the meal and the
	 * booking.
	 * 
	 * @param pMealId Id of the meal.
	 * @param pBooking Id Id of the booking.
	 */
	public Order(int pMealId, int pBookingId) {
		mealId = pMealId;
		bookingId = pBookingId;
	}
	
	/**
	 * The object must include a "meal" and a "booking".
	 * 
	 * @param jsonObject A JSONObject object.
	 */
	public Order(JSONObject jsonObject) {
		mealId = jsonObject.getInt("meal");
		bookingId = jsonObject.getInt("booking");
	}

	public int getMealId() {
		return mealId;
	}
	
	public void setMealId(int mealId) {
		this.mealId = mealId;
	}

	public int getBookingId() {
		return bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}
	
	/**
	 * Convert this instance into a JSON object with all the corresponding data.
	 * @return JSON object.
	 */
	public JSONObject getJSONObject() {
		JSONObject jsonOrder = new JSONObject();
		
		jsonOrder.put("meal", mealId);
		jsonOrder.put("booking", bookingId);
		
		return jsonOrder;
	}
}
