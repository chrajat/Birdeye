/**
 * 
 */
package common.utilities.reporting;

import common.launchsetup.Config;
import org.apache.commons.io.FileUtils;
import org.testng.Reporter;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

public class SendEmail {

	private final static String userName = new Config().fetchEmailDetails("email");
	private final static String password = new Config().fetchEmailDetails("pass");
	private final static String steadFastUserName = new Config().fetchEmailDetails("steadFastEmail");
	private final static String SteadFastPassword = new Config().fetchEmailDetails("steadFastPassword");
	private final static String SteadFastUserNameSendFrom = new Config().fetchEmailDetails("steadFastUserName");
	private static final String usernameSendFrom = "ET Automation Report <doNotReply>";

	public void sendEmail(String subject, String filename, String sendToKey) {
		String recipient = "";
		String emailType = "";
		try {
			recipient = new Config().fetchConfig(new File("./maven.properties"), sendToKey);
			emailType = new Config().fetchConfig(new File("./maven.properties"), "emailType");
		} catch (Exception e) {
			recipient = "nityananda.ghosh@timesinternet.in";
			emailType = "gmail";
		}
		if (emailType.equalsIgnoreCase("nMail")) {

			String usernameSendFrom = "ET Automation Report <doNotReply>";

			System.out.println("Sending from times internet smtp");

			boolean flag = false;
			for (int j = 1; j <= 10; j++) {
				Properties props = new Properties();
				props.put("mail.smtp.host", "nmailer.indiatimes.com");
				props.put("mail.smtp.socketFactory.port", "25");
				System.out.println("j => " + j);

				Session session = Session.getDefaultInstance(props);

				// compose message

				try {
					MimeMessage message = new MimeMessage(session);
					message.setFrom(new InternetAddress("et.automation@timesinternet.in", usernameSendFrom));
					message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
					message.setSubject(subject);

					String mess = "";
					try {
						mess = FileUtils.readFileToString(new File(filename), "UTF-8");
					} catch (IOException e) {
						e.printStackTrace();
					}
					// text with html
					message.setContent(mess, "text/html");

					// send Mail
					Transport.send(message);

					System.out.println("Sending mail " + recipient);
					flag = true;
				} catch (Exception e) {
					System.out.println("Trying attempt " + j + "\n" + e.getMessage());
				}
				if (flag) {
					break;
				}
			}
		} else if (emailType.equalsIgnoreCase("steadFast")) {

			System.out.println("Sending from times internet steadfast smtp");

			boolean flag = false;
			for (int j = 1; j <= 10; j++) {
				Properties props = new Properties();
				props.put("mail.smtp.host", "mum-relay.tilc.in");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.port", "587");
				props.put("mail.smtp.auth", "true");
				props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
				System.out.println("j => " + j);

				Session session = Session.getInstance(props, new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(steadFastUserName, SteadFastPassword);
					}
				});
				try {
					Message message = new MimeMessage(session);
					message.setFrom(new InternetAddress(SteadFastUserNameSendFrom, usernameSendFrom));
					message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
					message.setSubject(subject);

					String mess = "";
					try {
						mess = FileUtils.readFileToString(new File(filename), "UTF-8");
					} catch (IOException e) {
						e.printStackTrace();
					}
					message.setContent(mess, "text/html");

					System.out.println("Going to send mail");
					Reporter.log("Going to send mail");
					Transport.send(message);
					System.out.println("Email sent successfully  ");
					Reporter.log("Message Sending Done!!");
					flag = true;
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Trying attempt " + j + "\n" + e.getMessage());
				}
				if (flag) {
					break;
				}
			}
		} else {
			System.out.println("Sending from gmail smtp");
			boolean flag = false;
			for (int j = 1; j <= 10; j++) {
				Properties props = new Properties();
				props.put("mail.smtp.host", "smtp.gmail.com");
				props.put("mail.smtp.socketFactory.port", "465");
				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.port", "465");

				Session session = Session.getInstance(props, new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(userName, password);
					}
				});
				try {
					Message message = new MimeMessage(session);
					message.setFrom(new InternetAddress(userName));
					message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
					message.setSubject(subject);

					String mess = "";
					try {
						mess = FileUtils.readFileToString(new File(filename), "UTF-8");
					} catch (IOException e) {
						e.printStackTrace();
					}
					message.setContent(mess, "text/html");

					System.out.println("Going to send mail");
					Reporter.log("Going to send mail");
					Transport.send(message);
					System.out.println("Email sent successfully  ");
					Reporter.log("Message Sending Done!!");
					flag = true;
				} catch (Exception e) {
					System.out.println("Trying attempt " + j + "\n" + e.getMessage());
				}
				if (flag) {
					break;
				}
			}
		}

	}

	public void sendEmailWithHtmlReport(String reportName, String bodymessage, String recipients) {
		String recipient = "";
		String emailType = "";
		try {
			recipient = new Config().fetchConfig(new File("./maven.properties"), "recipient");
			emailType = new Config().fetchConfig(new File("./maven.properties"), "emailType");
		} catch (Exception e) {
			recipient = "nityananda.Ghosh@timesinternet.in";
			emailType = "gmail";
		}
		if (emailType.equalsIgnoreCase("nMail")) {

			System.out.println("Sending from times internet smtp");

			boolean flag = false;
			for (int j = 1; j <= 10; j++) {
				Properties props = new Properties();
				props.put("mail.smtp.host", "nmailer.indiatimes.com");
				props.put("mail.smtp.socketFactory.port", "25");
				System.out.println("j => " + j);

				Session session = Session.getDefaultInstance(props);

				// compose message

				try {
					MimeMessage message = new MimeMessage(session);
					message.setFrom(new InternetAddress("et.automation@timesinternet.in", usernameSendFrom));
					message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
					message.setSubject(reportName);

					// text with html
					message.setContent(bodymessage, "text/html");

					// send Mail
					Transport.send(message);

					System.out.println("Sending mail " + recipient);
					flag = true;
				} catch (Exception e) {
					System.out.println("Trying attempt " + j + "\n" + e.getMessage());
				}
				if (flag) {
					break;
				}
			}
		} else {
			boolean flag = false;
			for (int j = 1; j <= 10; j++) {
				Properties props = new Properties();
				props.put("mail.smtp.host", "smtp.gmail.com");
				props.put("mail.smtp.socketFactory.port", "465");
				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.port", "465");

				Session session = Session.getInstance(props, new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(userName, password);
					}
				});
				try {
					Message message = new MimeMessage(session);
					message.setFrom(new InternetAddress(userName));
					System.out.println("Mail will be sent to " + recipient);
					message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
					message.setSubject("Test report for " + reportName);

					// Create the message part
					BodyPart messageBodyPart = new MimeBodyPart();
					BodyPart messageBodyPart1 = new MimeBodyPart();
					messageBodyPart.setText("Hi," + "\nPlease find below report !\n" + bodymessage);
					// Create a multipar message
					Multipart multipart = new MimeMultipart();

					// Set text message part
					multipart.addBodyPart(messageBodyPart);

					// Part two is attachment
					messageBodyPart = new MimeBodyPart();
					String filename = System.getProperty("user.dir") + "/" + reportName + ".html";

					DataSource source = new FileDataSource(filename);
					messageBodyPart.setDataHandler(new DataHandler(source));
					// filename
					messageBodyPart.setFileName(reportName + ".html");
					multipart.addBodyPart(messageBodyPart);

					// Send the complete message parts
					message.setContent(multipart);
					String mess = "";
					try {
						mess = FileUtils.readFileToString(new File(filename), "UTF-8");
					} catch (IOException e) {
						e.printStackTrace();
					}
					messageBodyPart1.setContent(mess, "text/html; charset=utf-8");
					multipart.addBodyPart(messageBodyPart1);

					System.out.println("Going to send mail");
					Reporter.log("Going to send mail");
					Transport.send(message);
					flag = true;
				} catch (Exception e) {
					System.out.println("Trying attempt " + j + "\n" + e.getMessage());
				}
				if (flag) {
					System.out.println("Message Sending Done!!");
					Reporter.log("Message Sending Done!!");
					break;
				}
			}
		}
	}

	public void sendEmailWithHtmlReportandImage(String reportName, String image1, String image2, String bodymessage,
			String recipients) {
		image1 = System.getProperty("user.dir") + image1;
		image2 = System.getProperty("user.dir") + image2;
		for (int j = 1; j <= 10; j++) {
			Properties props = new Properties();
			props.put("mail.smtp.host", "nmailer.indiatimes.com");
			props.put("mail.smtp.socketFactory.port", "25");
			System.out.println("j => " + j);

			Session session = Session.getDefaultInstance(props);

			// compose message

			boolean flag = false;
			try {
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress("et.automation@timesinternet.in", usernameSendFrom));

				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("lavish.mehta@timesinternet.in"));
				message.setSubject("Test report for " + reportName + " | " + new Date());

				// Create the message part
				BodyPart messageBodyPart = new MimeBodyPart();
				BodyPart messageBodyPart1 = new MimeBodyPart();
				BodyPart messageBodyPart2 = new MimeBodyPart();
				BodyPart messageBodyPart3 = new MimeBodyPart();
				String htmlText = "<H1>Hello</H1><img src=\"cid:image\">";
				messageBodyPart.setText("Hi," + "\nPlease find below report !\n" + bodymessage);
				// Create a multipar message
				Multipart multipart = new MimeMultipart();

				// Set text message part
				multipart.addBodyPart(messageBodyPart);

				// Part two is attachment
				messageBodyPart = new MimeBodyPart();
				String filename = System.getProperty("user.dir") + "/" + reportName + ".html";

				DataSource source = new FileDataSource(filename);
				messageBodyPart.setDataHandler(new DataHandler(source));
				// filename
				messageBodyPart.setFileName(reportName + ".html");
				multipart.addBodyPart(messageBodyPart);

				multipart.addBodyPart(messageBodyPart1);

				DataSource imageDS1 = new FileDataSource(image1);
				messageBodyPart2.setDataHandler(new DataHandler(imageDS1));
				messageBodyPart2.setHeader("Content-ID", "<image>");
				multipart.addBodyPart(messageBodyPart2);
				messageBodyPart3.setContent(htmlText, "text/html");

				DataSource imageDS2 = new FileDataSource(image2);
				messageBodyPart3.setDataHandler(new DataHandler(imageDS2));
				messageBodyPart3.setHeader("Content-ID", "<image>");
				multipart.addBodyPart(messageBodyPart3);
				// Send the complete message parts
				message.setContent(multipart);
				String mess = "";
				try {
					mess = FileUtils.readFileToString(new File(filename), "UTF-8");
				} catch (IOException e) {
					e.printStackTrace();
				}
				messageBodyPart1.setContent(mess, "text/html; charset=utf-8");
				message.setContent(multipart);

				System.out.println("Going to send mail");
				Reporter.log("Going to send mail");
				Transport.send(message);
				flag = true;
			} catch (Exception e) {
				System.out.println("Trying attempt " + j + "\n" + e.getMessage());
			}
			if (flag) {
				System.out.println("Message Sending Done!!");
				Reporter.log("Message Sending Done!!");
				break;
			}
		}
	}

	public static void sendCustomEmail(String[] sendTo, String emailSubject, Object messsageBody, File... fileNames) {

		boolean flag = false;
		for (int j = 1; j <= 10; j++) {
			Properties props = new Properties();
			// props.put("mail.smtp.host", "nmailer.indiatimes.com");
			// props.put("mail.smtp.socketFactory.port", "25");
			// System.out.println("j => " + j);
			//
			// Session session = Session.getDefaultInstance(props);
			props.put("mail.smtp.host", "mum-relay.tilc.in");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.port", "587");
			props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
			
			System.out.println("j => " + j);

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(steadFastUserName, SteadFastPassword);
				}
			});

			// compose message

			try {
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(SteadFastUserNameSendFrom, usernameSendFrom));
				for (int a = 0; a < sendTo.length; a++) {
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(sendTo[a]));
				}
				message.setSubject(emailSubject);

				BodyPart msgBody1 = new MimeBodyPart();
				msgBody1.setContent(messsageBody, "text/html");

				// create multipart object and add MimeBodyPart object to
				// this object
				Multipart multiPart = new MimeMultipart();
				multiPart.addBodyPart(msgBody1);

				// create MimeBodyPart object and set DataHandler object
				for (File fileName : fileNames) {
					System.out.println("File:" + fileName);
					if (!fileName.toString().isEmpty() && fileName != null) {
						BodyPart msgBody2 = new MimeBodyPart();
						DataSource source = new FileDataSource(fileName);
						msgBody2.setDataHandler(new DataHandler(source));
						msgBody2.setFileName(fileName.getName());
						multiPart.addBodyPart(msgBody2);
					}
				}

				// set multipart object to the message object
				message.setContent(multiPart);

				// send Mail
				Transport.send(message);

				System.out.println("Email sent successfully to " + Arrays.toString(sendTo));
				flag = true;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Trying attempt " + j + "\n" + e.getMessage());
			}
			if (flag) {
				break;
			}
		}
	}

	public static void sendCustomEmailWithoutFile(String[] sendTo, String emailSubject, Object messsageBody) {

		boolean flag = false;
		for (int j = 1; j <= 10; j++) {
			Properties props = new Properties();
			// props.put("mail.smtp.host", "nmailer.indiatimes.com");
			// props.put("mail.smtp.socketFactory.port", "25");
			// System.out.println("j => " + j);
			//
			// Session session = Session.getDefaultInstance(props);
			props.put("mail.smtp.host", "mum-relay.tilc.in");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");
			
			System.out.println("j => " + j);

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(steadFastUserName, SteadFastPassword);
				}
			});

			// compose message

			try {
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(SteadFastUserNameSendFrom, usernameSendFrom));
				for (int a = 0; a < sendTo.length; a++) {
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(sendTo[a]));
				}
				message.setSubject(emailSubject);

				BodyPart msgBody1 = new MimeBodyPart();
				msgBody1.setContent(messsageBody, "text/html");

				// create multipart object and add MimeBodyPart object to
				// this object
				Multipart multiPart = new MimeMultipart();
				multiPart.addBodyPart(msgBody1);

				// set multipart object to the message object
				message.setContent(multiPart);

				// send Mail
				Transport.send(message);

				System.out.println("Email sent successfully to " + Arrays.toString(sendTo));
				flag = true;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Trying attempt " + j + "\n" + e.getMessage());
			}
			if (flag) {
				break;
			}
		}
	}
}
