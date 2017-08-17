package comp2541.bison.restaurant;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.ThreadPool;

/**
 * The server for the restaurant software.
 * 
 * @author Michele Cipriano
 *
 */
public class RestaurantServer extends Server {

	/**
	 * Basic constructor with port and thread management.
	 * 
	 * @param port Port where the server will be launched.
	 * @param pool Type of thread pool, how the different clients will be handled.
	 */
	public RestaurantServer(int port, ThreadPool pool) {
		// Uses a ThreadPool allow multiple threads.
		super(pool);
		
		// Add a ServerConnector with specified port to the server.
		ServerConnector serverConnector = new ServerConnector(this);
		serverConnector.setPort(port);
		this.addConnector(serverConnector);
	}
}
