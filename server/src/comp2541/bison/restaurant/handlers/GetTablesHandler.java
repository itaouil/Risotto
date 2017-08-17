package comp2541.bison.restaurant.handlers;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import comp2541.bison.restaurant.data.Table;

/**
 * Handler for loading the tables in the restaurant.
 * @author Michele Cipriano
 */
public class GetTablesHandler extends BaseHandler {
	@Override
	public void run() {
		try {
			// Get all the bookings from time to time:
			ArrayList<Table> tables = database.getTables();

			// Build the JSON message:
			JSONObject jsonResponse = new JSONObject();
			JSONArray jsonTablesArray = new JSONArray();
			for (Table t : tables) {
				jsonTablesArray.put(t.getJSONObject());
			}
			jsonResponse.put("tables", jsonTablesArray);

			// Send OK and list of bookings:
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
