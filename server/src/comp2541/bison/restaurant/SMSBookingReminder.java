/**
 * 
 */
package comp2541.bison.restaurant;
import comp2541.bison.restaurant.data.Booking;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Account;

/**
 * Class to handle SMS reminders.
 * @author Jones Agwata
 */
public class SMSBookingReminder {
	/**
	 * Logger for this class.
	 */
	static Logger log = Logger.getLogger(SMSBookingReminder.class.getName());
	
	/**
	 * Twilio account ID.
	 */
	public static final String ACCOUNT_SID = "AC59a29a76b0f9acf0db6fe69f6c6edef2";
	/**
	 * Twilio authentication token.
	 */
	public static final String AUTH_TOKEN = "0231ce8ed1536f94a3c77b0ed4aeab65";
	
	/**
	 * Twilio API client.
	 */
	final TwilioRestClient client;
	
	/**
	 * Twilio account.
	 */
	final Account mainAccount;
	
	/**
	 * Booking that reminder will be sent for.
	 */
	private Booking pbooking;
	
	/**
	 * Create an SMS reminder for the booking.
	 * @param booking Booking.
	 */
	public SMSBookingReminder(Booking booking){
	
		pbooking = booking;
		
		client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
		mainAccount = client.getAccount();
		// Send text to notify successful booking
		sendMessage();
	}
	
	/**
	 * Sends a message to confirm booking if phone number was provided.
	 */
	public void sendMessage(){
		long unixSeconds = pbooking.getUnixStart();
		Date date = new Date(unixSeconds*1000L);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT")); 
		String dateTime = sdf.format(date);
				
		final SmsFactory messageFactory = mainAccount.getSmsFactory();
	    final List<NameValuePair> messageParams = new ArrayList<NameValuePair>();
	    messageParams.add(new BasicNameValuePair("To", pbooking.getPhoneNumber()));
	    messageParams.add(new BasicNameValuePair("From",  "+441471392050"));
	    messageParams.add(new BasicNameValuePair("Body", "Thank you for booking."+" We are pleased to confirm your booking on the "+ dateTime+ " for " + pbooking.getPartySize() + " people"));
	    try {
			messageFactory.create(messageParams);
			log.info("Text Message sent");
			
			
		} catch (TwilioRestException e) {
			log.fatal(e.getMessage());
		}
	}

	
}

