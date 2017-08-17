package comp2541.bison.restaurant;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import comp2541.bison.restaurant.database.*;
import comp2541.bison.restaurant.handlers.*;

/**
 * This class handles all the requests from the client
 * respecting the Wiki document (See "Client-Server Communication").
 * @author Michele Cipriano, Iaroslav Khrypko
 */
public class RestaurantHandler extends AbstractHandler {
	/**
	 * Logger for this class.
	 */
	private static Logger log = Logger.getLogger(RestaurantHandler.class.getName());

	/**
	 * Database for the server.
	 */
	private Database restaurantDB;
	
	/**
	 * Handlers for this server.
	 */
	private Map<String, Class<? extends BaseHandler>> _handlers = new HashMap<String, Class<? extends BaseHandler>>();

	/**
	 * Constructor taking the name of the database.
	 * 
	 * @param dbString Name of the database to use.
	 */
	public RestaurantHandler(String dbString) {
		try {
			restaurantDB = new SQLiteDB(dbString);
			_handlers.put("OPTIONS *", OptionsHandler.class);
			_handlers.put("GET /menu", GetMenuHandler.class);
			_handlers.put("GET /tables", GetTablesHandler.class);

			_handlers.put("GET /bookings", GetBookingsHandler.class);
			_handlers.put("POST /bookings", PostBookingsHandler.class);

			_handlers.put("GET /orders", GetOrdersHandler.class);
			_handlers.put("POST /orders", PostOrdersHandler.class);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Cannot open the database.");
			System.exit(0);
		}
	}

	/**
	 * Handles all the requests from the clients.
	 * 
	 * @param target The target of the request - either a URI or a name.
	 * @param baseRequest The original unwrapped request object.
	 * @param request The request either as the Request object or a wrapper of that request.
	 * @param response The response as the Response object or a wrapper of that request.
	 * @throws IOException If unable to handle the request or response processing.
	 * @throws ServletException If unable to handle the request or response due to underlying servlet issue.
	 */
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// Below is necessary to pass CORS check
		String origin = request.getHeader("Origin");
		response.addHeader("Access-Control-Allow-Origin", origin);
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");
		response.addHeader("Access-Control-Allow-Headers", "Accept, Content-type");
		response.addHeader("Access-Control-Max-Age", "1728000");
		
		// Identify the necessary handler
		String method = request.getMethod();
		String uri = request.getRequestURI();
		String key = method + " " + uri;
		String wildcard = method + " *";
		
		log.debug("Incoming request: " + key);
		
		// Handle the request
		if(_handlers.containsKey(key) || _handlers.containsKey(wildcard)) {
			Class<? extends BaseHandler> cls = _handlers.containsKey(wildcard) ? _handlers.get(wildcard) : _handlers.get(key);
			log.debug("Request matched to handler: " + cls.getName());
			try {
				BaseHandler handler = cls.newInstance();
				handler.setRequest(request);
				handler.setResponse(response);
				handler.setDatabase(restaurantDB);
				handler.run();
				
				// The request has been handled correctly.
				baseRequest.setHandled(true);
			} catch (InstantiationException | IllegalAccessException e) {
				log.fatal("Failed to process the request", e);
			}
		}
	}
}