package comp2541.bison.restaurant.handlers;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import comp2541.bison.restaurant.database.Database;

/**
 * Abstract request handler.
 * @author Iaroslav Khrypko, Michele Cipriano
 */
public class BaseHandler implements Runnable {
	/**
	 * Request made to this handler.
	 */
	protected HttpServletRequest request;
	
	/**
	 * Response made to be sent to the client.
	 */
	protected HttpServletResponse response;
	
	/**
	 * Database behind the server.
	 */
	protected Database database;
	
	/**
	 * Set the request to be processed by the handler.
	 * @param request Request.
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	/**
	 * Set the response to be sent by the handler.
	 * @param response Response.
	 */
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	/**
	 * Set the database to be used by the handler.
	 * @param database Database.
	 */
	public void setDatabase(Database database) {
		this.database = database;
	}
	
	/**
	 * Get the body of the request.
	 * @return Request body as a JSON object.
	 * @throws IOException Failed to read the request.
	 */
	public JSONObject getBody() throws IOException {
		BufferedReader requestBodyBR = request.getReader(); // Reader for the body of the HTTP message
		StringBuilder sb = new StringBuilder();				// Auxiliary object to tranform body to JSON
		String line;										// String used to read from BufferedReader

		// Reading the body
		while ((line = requestBodyBR.readLine()) != null) {
			sb.append(line);
		}

		// StringBuilder to JSONObject
		JSONObject jsonBody = new JSONObject(sb.toString());
		
		return jsonBody;
	}

	@Override
	public void run() { }
}
