package comp2541.bison.restaurant;


import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;

/**
 * Main class for the server. Here the server is launched and connected
 * with the HTTP data handler and the log handler.
 * @author Michele Cipriano
 */
public class Main {
	/**
	 * Logger for this class.
	 */
	private static Logger log = Logger.getLogger(Main.class.getName());

	/**
	 * The program starts from here.
	 * @param args Command-line arguments.
	 */
	public static void main(String[] args) {
		try {
			// Create a server
			int port = 8080;
			RestaurantServer restaurantServer = new RestaurantServer(port, new ExecutorThreadPool());
			
			// Create request handler
			HandlerCollection handlers = new HandlerCollection();
			RequestLogHandler requestLogHandler = new RequestLogHandler();
			RestaurantHandler restaurantHandler = new RestaurantHandler("restaurantdatabase.db");
			handlers.setHandlers(new Handler[]{requestLogHandler, restaurantHandler });
			restaurantServer.setHandler(handlers);
			
			//Creates log file for requests to the server
			NCSARequestLog requestLog = new NCSARequestLog("./restaurant.request.log");
			requestLog.setRetainDays(90);
			requestLog.setAppend(true);
			requestLog.setExtended(false);
			requestLog.setLogTimeZone("GMT");
			requestLogHandler.setRequestLog(requestLog);
			
			restaurantServer.start();
			log.info("Server started at " + port);
			restaurantServer.join();
		} catch (Exception e) {
			log.fatal(e.getMessage(), e);
		}
	}
}