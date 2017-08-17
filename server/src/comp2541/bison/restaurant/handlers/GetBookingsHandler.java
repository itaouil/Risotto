package comp2541.bison.restaurant.handlers;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import comp2541.bison.restaurant.data.Booking;

/**
 * Handler for loading the bookings within the given date range.
 * @author Michele Cipriano
 */
public class GetBookingsHandler extends BaseHandler {
	@Override
	public void run() {
		try {
			long startTime = Long.parseLong(request.getParameter("startingTime"));
			long endTime = Long.parseLong(request.getParameter("endingTime"));
			
			// Get all the bookings from time to time
			ArrayList<Booking> bookings = database.getBookings(startTime, endTime);
			
			// Build the JSON message
			JSONObject jsonResponse = new JSONObject();
			JSONArray jsonBookingsArray = new JSONArray();
			for (Booking b : bookings) {
				jsonBookingsArray.put(b.getJSONObject());
			}
			jsonResponse.put("bookings", jsonBookingsArray);

			// Send OK and list of bookings
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println(jsonResponse.toString());
		}
		catch(Exception e) {
			e.printStackTrace();

			JSONObject jsonError = new JSONObject();
			jsonError.put("errorMessage", "The request cannot be satisfied");

			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			try {
				response.getWriter().println(jsonError.toString());
			} catch (IOException iex) { }
		}
	}
}
