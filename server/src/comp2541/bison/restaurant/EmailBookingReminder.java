package comp2541.bison.restaurant;
import comp2541.bison.restaurant.data.Booking;
import java.util.Date;
import java.util.TimeZone;
import java.lang.Math;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.sendgrid.*;
import com.sendgrid.SendGrid.Email;

/**
 * Class to handle Email notifications.
 * @author Jones Agwata
 */
public class EmailBookingReminder {
	/**
	 * Logger for this class.
	 */
	static Logger log = Logger.getLogger(EmailBookingReminder.class.getName());
	
	/**
	 * Booking that reminder will be sent for.
	 */
	private Booking pbooking;
	
	/**
	 * SendGrid API library.
	 */
	public SendGrid sendgrid  = new SendGrid("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");
	
	/**
	 * Create an email reminder for the booking.
	 * @param booking Booking.
	 */
	public EmailBookingReminder(Booking booking){
		pbooking=booking;
		// Send Email to notify of successful Booking
		sendEmail();
		
		// Set three hour allowance and set reminder
		int scheduleAllowance = Math.toIntExact(pbooking.getUnixStart()-(3600*3));
		scheduleReminder(scheduleAllowance);
	}
	
	/**
	 * Send an email to the customer if email was provided.
	 */
	public void sendEmail(){
		long unixSeconds = pbooking.getUnixStart();
		Date date = new Date(unixSeconds*1000L);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT")); 
		String dateTime = sdf.format(date);
		
		Email email = new Email();
		email.addTo(pbooking.getEmail());
		email.addToName(pbooking.getCustomerName());
		email.setFrom("restaurantserver@mail.com");
		email.setSubject("Booking Confirmation");
		email.setText("Thank you for booking. We are pleased to confirm your booking on the "+ dateTime + " for " + pbooking.getPartySize() + " people");
		try {
			sendgrid.send(email);
			log.info("Email Sent");
		} catch (SendGridException e) {
			log.fatal(e.getMessage());
		}
	}
	
	/**
	 * Schedule an email reminder to be sent on the given time.
	 * @param time Unix time of sent.
	 */
	public void scheduleReminder(int time){
		
		long unixSeconds = pbooking.getUnixStart();
		Date date = new Date(unixSeconds*1000L);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT")); 
		String dateTime = sdf.format(date);
		
		Email email = new Email();
		email.addTo(pbooking.getEmail());
		email.addToName(pbooking.getCustomerName());
		email.setFrom("restaurantserver@mail.com");
		email.setSubject("Booking Reminder");
		email.setText("This is an automatic booking reminder. We would like to remind you of your booking on the "+ dateTime + " for " + pbooking.getPartySize() + " people");
		email.setSendAt(time);
		
		try {
			sendgrid.send(email);
			log.info("Reminder Email Scheduled");
		} catch (SendGridException e) {
			log.fatal(e.getMessage());
		}
	}

}
