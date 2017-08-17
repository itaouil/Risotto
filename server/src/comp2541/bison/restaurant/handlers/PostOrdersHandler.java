package comp2541.bison.restaurant.handlers;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import comp2541.bison.restaurant.data.Booking;
import comp2541.bison.restaurant.data.Order;

/**
 * Handler for ordering meals in the booking.
 * @author Michele Cipriano
 */
public class PostOrdersHandler extends BaseHandler {
	@Override
	public void run() {
		try {
			JSONArray jsonArrayOrder = getBody().getJSONArray("orders");   // Orders in the request.
			ArrayList<Order> unsatisfiedOrders = new ArrayList<Order>();  // Unsatisfied orders to send back
			boolean requestFullySatisfied = true; // Are all the requests satisfied?

			// Cleans all the orders made previously.
			for (int i = 0; i<jsonArrayOrder.length();i++) {
				JSONObject jsonOrder = (JSONObject) jsonArrayOrder.get(i);
				Order order = new Order(jsonOrder);
				Booking booking = new Booking(order.getBookingId());
				database.removeAllOrders(booking);
			}
			
			// Insert all the orders into the database
			for (int i = 0; i<jsonArrayOrder.length();i++) {
				// jsonOrder must be a JSONObject as specified in the Wiki,
				// otherwise the structure of the JSON message is not correct.
				JSONObject jsonOrder = (JSONObject) jsonArrayOrder.get(i);
				Order order = new Order(jsonOrder);

				try {
					database.insertOrder(order);
				} catch (Exception e) {
					unsatisfiedOrders.add(order);
					requestFullySatisfied = false;
				}

			}

			if (requestFullySatisfied) {
				// Send OK to the client.
				response.setStatus(HttpServletResponse.SC_OK);
				response.getWriter().println("");
			} else {
				// Send JSON error message to the client with unsatisfied orders.
				JSONObject jsonError = new JSONObject();
				JSONArray jsonUnsatisfiedOrders = new JSONArray();

				// Construct a JSONArray including unsatisfied orders
				for (Order order : unsatisfiedOrders) {
					jsonUnsatisfiedOrders.put(order.getJSONObject());
				}

				jsonError.put("errorMessage", "Some of the requested orders cannot be satisfied.");
				jsonError.put("orders", jsonUnsatisfiedOrders);

				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println(jsonError.toString());
			}


		} catch (Exception e) {
			// Send JSON error message to the client
			JSONObject jsonError = new JSONObject();
			jsonError.put("errorMessage", "The request cannot be satisfied");

			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			try {
				response.getWriter().println(jsonError.toString());
			} catch (IOException iex) {
			}

			e.printStackTrace();
		}
	}
}
