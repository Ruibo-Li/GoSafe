package Publisher;

import java.util.Properties;
import java.util.concurrent.Callable;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmailRequest implements Callable<String> {
	
	private String recepient;
	private String name;
	private String subject;
	private String body;
	
	public SendEmailRequest(String recepient, String name, String subject, String body) {
		this.recepient = recepient;
		this.name = name;
		this.subject = subject;
		this.body = body;
	}

	public String call() throws Exception {
		final String username = "gosafeny@gmail.com";
		final String password = "";
 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("gosafeny@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(recepient));
			message.setSubject(subject);
			message.setText("Dear " + name + ",\n\n" + body + "\n\nPlease be cautious and travel safe!\n\nSincerely,\nThe GoSafe Team");
 
			Transport.send(message);
 
			String result = "Email successfully sent to " + recepient;
			System.out.println(result);
			return result;
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	
}
