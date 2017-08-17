package comp2541.bison.restaurant.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import comp2541.bison.restaurant.data.Menu;

/**
 * Handler for loading the menu.
 * @author Michele Cipriano
 */
public class GetMenuHandler extends BaseHandler {
	@Override
	public void run() {
		try {
			// Build a menu object from an ArrayList<Meal>
			Menu menu = new Menu("", database.getMeals());

			// Send OK and menu
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println(menu.getJSONObject());

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
