package comp2541.bison.restaurant.handlers;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import comp2541.bison.restaurant.data.Booking;
import comp2541.bison.restaurant.data.Meal;

/**
 * Handler for loading the orders within the given range.
 * @author Michele Cipriano
 */
public class GetOrdersHandler extends BaseHandler {
	@Override
	public void run() {
		try {
			int bookingId = Integer.parseInt(request.getParameter("id"));
			
			// Get list of ordered meals from Database
			ArrayList<Meal> orderedMeals = database.getOrderedMeals(new Booking(bookingId));
			
			// Transform ArrayList<Meal> in a JSONObject object
			// of the form specified in the Wiki for "GET /orders"
			JSONObject jsonResponse = new JSONObject();
			JSONArray jsonMealsArray = new JSONArray();
			
			for (Meal meal : orderedMeals) {
				jsonMealsArray.put(meal.getJSONObject());
			}
			jsonResponse.put("meals", jsonMealsArray);
			
			// Send OK and list of meals
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println(jsonResponse.toString());
		} catch (Exception e) {
			e.printStackTrace();

			JSONObject jsonError = new JSONObject();
			jsonError.put("errorMessage", "The request cannot be satisfied");

			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			try {
				response.getWriter().println(jsonError.toString());
			} catch (IOException iex) {
			}
		}
	}
}
