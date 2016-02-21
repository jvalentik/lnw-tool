package com.ibm.lnw.presentation.model;

import com.ibm.lnw.backend.domain.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import java.time.LocalDate;


/**
 * Created by Jan Valentik on 11/29/2015.
 */
public class SendGridService {
    private static final String API_KEY = "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";

	public static void sendResetLink(String userToReset) throws Exception {
		SendGrid sendGrid = new SendGrid(API_KEY);
		SendGrid.Email email = new SendGrid.Email();
		email.addTo(userToReset);
		email.setFrom("lnwtool@sk.ibm.com");
		email.setSubject("Link to reset password to LNW Tool");
		String hash = MD5Hash.encrypt(LocalDate.now().toString()) + "id=" + MD5Hash.encrypt(userToReset);
		System.out.println(hash);
		email.setHtml("<body><p><h4>If you did not request to reset password to LNW Tool, please ignore this " +
				"email</h4></p>" +
				"<p><a href=\"http://lnwtool.eu-gb.mybluemix.net/?pwd_reset="
				+ hash + "\">Click here to reset your LNW Tool password</a></p><body>");
		try {
			SendGrid.Response response = sendGrid.send(email);
			System.out.println(response.getMessage());
		}
		catch (SendGridException ex) {
			ex.printStackTrace();
		}
	}

	public static void sendEmail(Request request) throws Exception {
		SendGrid sendGrid = new SendGrid(API_KEY);
		SendGrid.Email email = new SendGrid.Email();
		email.addTo(request.getPmaName());
		email.setFrom("lnwtool@sk.ibm.com");
		email.setSubject(request.getCreatedBy().getUserName() + " sent you a new request in the LNW Tool");
		email.setHtml("<body><p>There is a new request for you in the LNW Tool --> " +
				"<a href=\"http://lnwtool.eu-gb.mybluemix.net/?request_id=" + request.getId() + "\">Link to this request</a></p>" +
				"<a href=\"http://lnwtool.eu-gb.mybluemix.net\">Link to LNW Tool</a>" +
				"<p><h4>Request details</h4></p>" +
				"<p>Customer: " + request.getCustomerName() + "</p>" +
				"<p>Contract no.: " + request.getContractNumber() + "</p>" +
				"<p>OCPS: " + request.getServices() + "</p>" +
				"<p>PE: " + request.getPexName() + "</p></body>");
		try {
			SendGrid.Response response = sendGrid.send(email);
			System.out.println(response.getMessage());
		}
		catch (SendGridException ex) {
			ex.printStackTrace();
		}
	}

}
