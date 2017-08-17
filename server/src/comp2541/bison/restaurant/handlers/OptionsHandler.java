package comp2541.bison.restaurant.handlers;

import javax.servlet.http.HttpServletResponse;

/**
 * Handler for all the OPTIONS requests.
 * @author Iaroslav Khrypko
 */
public class OptionsHandler extends BaseHandler {

	@Override
	public void run() {
		try {
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println("");
		}
		catch(Exception e) {
			
		}
	}

}
