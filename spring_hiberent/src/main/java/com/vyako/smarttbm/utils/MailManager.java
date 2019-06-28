package com.vyako.smarttbm.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import com.vyako.smarttbm.do_other.BaseResponseModel;
import com.vyako.smarttbm.entity.EmailSetting;

/**
 * this class used to sent an email
 * 
 * @author Sid
 * 
 */
@Component
public class MailManager {
	protected static final Logger LOG = Logger.getLogger("SmartTBM>>>>>>");

	public static JavaMailSender javaMailSender = null;
	@Autowired
	protected SessionFactory sessionFactory;

	// private final String SPACE_DELIMITOR = " ";

	// /**
	// * this method helps send an email with attachment to receiver
	// *
	// * @param receiverMailAddress
	// * -> receiver mail address
	// * @param filePath
	// * -> the path of the file(attachment)
	// * @return -> boolean value which indicated whether the mail send or not
	// * @throws UnsupportedEncodingException
	// */
	// public boolean sendEmailWithAttachment(Invoice invoice, String filePath)
	// throws UnsupportedEncodingException {
	//
	// try {
	//
	// // create the mail body text
	// String mailText = createInvoiceMailBodyText(invoice);
	//
	// SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
	//
	// simpleMailMessage
	// .setFrom("account.invoice@dynamiccarcarrying.com.au");
	//
	// String[] receiverMail = { invoice.getAddress().getEmail() };
	// simpleMailMessage.setTo(receiverMail);
	//
	// simpleMailMessage.setSubject("Invoice from Dynamic Car Carrying #"
	// + orderID);
	//
	// simpleMailMessage.setText(mailText);
	//
	// System.out.println(">>>>>>>>>>>File path " + filePath);
	//
	// FileSystemResource file = new FileSystemResource(new File(filePath));
	//
	// MimeMessage message = mailSender.createMimeMessage();
	//
	// // message.setText(invoiceMailBodyText, "text/html");
	//
	// MimeMessageHelper helper = new MimeMessageHelper(message, true);
	// helper.setFrom(simpleMailMessage.getFrom());
	// helper.setTo(simpleMailMessage.getTo());
	// helper.setSubject(simpleMailMessage.getSubject());
	//
	// // second parameter true is allow to set the content bosy in html
	// // format
	// helper.setText(simpleMailMessage.getText(), true);
	//
	// helper.addAttachment("INV_" + invoice.getInvoice_number() + ".pdf",
	// file);
	//
	// mailSender.send(message);
	//
	// return true;
	//
	// } catch (MailSendException e) {
	// e.printStackTrace();
	// return false;
	// } catch (MessagingException e) {
	// throw new MailParseException(e);
	// } catch (Exception e) {
	// e.printStackTrace();
	// return false;
	// }
	//
	// }
	//
	// private String createInvoiceMailBodyText(Invoice invoice) {
	//
	// System.out.println(">>>>>>>>>>>>>invoice " + invoice);
	//
	// // set order ID of the Invoice
	// orderID = invoice.getOrders().get(0).getOrder_id();
	//
	// // // create content for receiver address detail
	// // String receiverAddressDetail = "Dear "
	// // + invoice.getAddress().getFirst_name() + " "
	// // + invoice.getAddress().getLast_name() + ","
	// // + NEXT_LINE_DELIMITOR + "A new Invoice #"
	// // + invoice.getInvoice_number()
	// // + " has been generated for your Job #" + orderID
	// // + ", please find the attachment." + NEXT_LINE_DELIMITOR
	// // + NEXT_LINE_DELIMITOR;
	// //
	// // // create content for invoice summary
	// // String invoiceSummaryDetail = "<b>Invoice Summary:</b>"
	// // + NEXT_LINE_DELIMITOR + NEXT_LINE_DELIMITOR
	// // + generateInvoice.orderInformation + NEXT_LINE_DELIMITOR
	// // + NEXT_LINE_DELIMITOR + "<b>Shipping Amount:</b>"
	// // + SPACE_DELIMITOR + "$" + invoice.getOrders().get(0).getPrice()
	// // + NEXT_LINE_DELIMITOR + "<b>10% GST:</b>" + SPACE_DELIMITOR
	// // + "$" + invoice.getRate() + NEXT_LINE_DELIMITOR
	// // + "<b>Net Amount (incl. GST):</b>" + SPACE_DELIMITOR + "$"
	// // + invoice.getTotal_price() + NEXT_LINE_DELIMITOR
	// // + NEXT_LINE_DELIMITOR;
	//
	// // company contact details
	// String companyContactDetail =
	// // "Please feel free to contact us in case of any queries."
	// NEXT_LINE_DELIMITOR + NEXT_LINE_DELIMITOR + NEXT_LINE_DELIMITOR
	// + "Kind Regards," + NEXT_LINE_DELIMITOR + "Accounts"
	// + NEXT_LINE_DELIMITOR + "Dynamic Car Carrying"
	// + NEXT_LINE_DELIMITOR + "(w) 07 3216 4626"
	// + NEXT_LINE_DELIMITOR + "(f) 07 3216 4735"
	// + NEXT_LINE_DELIMITOR
	// + "<a href=http://www.dynamiccarcarrying.com.au>"
	// + "www.dynamiccarcarrying.com.au" + "</a>"
	// + NEXT_LINE_DELIMITOR;
	//
	// // String invoiceMailBodyText = receiverAddressDetail
	// // + invoiceSummaryDetail + companyContactDetail;
	//
	// // MimeMessage mimeMessage = new MimeMessage(null)
	//
	// return companyContactDetail;
	// }

	public void initJavaMailSender() {
		JavaMailSenderImpl javaMailSenderNew = new JavaMailSenderImpl();

		Session session = sessionFactory.openSession();
		List<EmailSetting> emailSettingList = new ArrayList<EmailSetting>();
		try {

			Criteria createCriteria = session.createCriteria(EmailSetting.class);
			emailSettingList = createCriteria.list();
			session.close();
		} catch (Exception e) {
			session.close();
			e.printStackTrace();
		}
		// Using gmail
		javaMailSenderNew.setHost(emailSettingList.get(0).getHost());
		javaMailSenderNew.setPort(emailSettingList.get(0).getPort());
		javaMailSenderNew.setUsername(emailSettingList.get(0).getEmail());
		javaMailSenderNew.setPassword(emailSettingList.get(0).getEmail_pwd());

		String adminEmail = emailSettingList.get(0).getEmail();

		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.starttls.enable", "true");
		
		// this is the port number of local network
		javaMailProperties.put("mail.smtp.socketFactory.port", "25");
		//javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		//javaMailProperties.put("mail.smtp.socketFactory.fallback", "false");
		
		// added to solve Could not convert socket to TLS.
		//javaMailProperties.put("mail.smtp.ssl.enable", "true");
		javaMailProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

		javaMailProperties.put("mail.smtp.auth", "true");
		javaMailProperties.put("mail.transport.protocol", "smtp");
		javaMailProperties.put("mail.debug", "true");// Prints out everything on
														// screen
		javaMailSenderNew.setJavaMailProperties(javaMailProperties);

		javaMailSender = javaMailSenderNew;

	}

	public JavaMailSender getTempJavaMailSender(EmailSetting emailSetting) {
		JavaMailSenderImpl javaMailSenderNew = new JavaMailSenderImpl();

		// Using gmail
		javaMailSenderNew.setHost(emailSetting.getHost());
		javaMailSenderNew.setPort(emailSetting.getPort());
		javaMailSenderNew.setUsername(emailSetting.getEmail());
		javaMailSenderNew.setPassword(emailSetting.getEmail_pwd());

		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.starttls.enable", "true");

		// this is the port number of local network
		javaMailProperties.put("mail.smtp.socketFactory.port", "25");

		javaMailProperties.put("mail.smtp.auth", "true");
		javaMailProperties.put("mail.transport.protocol", "smtp");
		javaMailProperties.put("mail.debug", "true");// Prints out everything on
														// screen
		javaMailSenderNew.setJavaMailProperties(javaMailProperties);

		return javaMailSenderNew;

	}

	public BaseResponseModel testEmail(EmailSetting emailSetting) throws UnsupportedEncodingException {
		try {

			JavaMailSender tempJavaMailSender = getTempJavaMailSender(emailSetting);

			String adminEmail = ((JavaMailSenderImpl) tempJavaMailSender).getUsername();

			final String[] receiverIds = { emailSetting.getMail_to() };
			MimeMessagePreparator preparator = getMessagePreparator(adminEmail, receiverIds, "Test Mail",
					emailSetting.getMail_body());

			tempJavaMailSender.send(preparator);
			System.out.print("## is email sent = true");

			return formResponseModel(true, "Mail status", "Send");

		} catch (MailSendException e) {
			e.printStackTrace();
			System.out.print("## is email sent = false");
			LOG.error(e);

			return formResponseModel(false, "Mail status", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.print("## is email sent = false");
			LOG.error(e);

			return formResponseModel(false, "Mail status", e.getMessage());
		}
	}

	private BaseResponseModel formResponseModel(boolean isSuccess, String msg, Object responseObj) {
		BaseResponseModel baseResponseModel = new BaseResponseModel();
		baseResponseModel.setSuccess(isSuccess);
		baseResponseModel.setMsg(msg);
		baseResponseModel.setResponse(responseObj);
		return baseResponseModel;
	}

	/**
	 * this method helps send an email with simple text only
	 * 
	 * @param receiverMailAddress
	 *            -> receiver mail address
	 * @param filePath
	 *            -> the path of the file(attachment)
	 * @return -> boolean value which indicated whether the mail send or not
	 * @throws UnsupportedEncodingException
	 */
	public boolean sendEmail(String[] receiverMailAddress, String subject, String mailText)
			throws UnsupportedEncodingException {
		try {

			if (javaMailSender == null) {
				initJavaMailSender();
			}

			String adminEmail = ((JavaMailSenderImpl) javaMailSender).getUsername();
			MimeMessagePreparator preparator = getMessagePreparator(adminEmail, receiverMailAddress, subject, mailText);

			javaMailSender.send(preparator);
			return true;

		} catch (MailSendException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private MimeMessagePreparator getMessagePreparator(String adminEmail, final String[] to, final String subject,
			final String body) {

		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage) throws Exception {

				mimeMessage.setFrom(adminEmail);

				InternetAddress[] address = new InternetAddress[to.length];
				for (int i = 0; i < to.length; i++) {
					address[i] = new InternetAddress(to[i]);
				}
				mimeMessage.setRecipients(Message.RecipientType.TO, address);
				// mimeMessage.setText(body);
				mimeMessage.setSubject(subject);
				mimeMessage.setContent(body, "text/html; charset=utf-8");
			}
		};
		return preparator;
	}

}
