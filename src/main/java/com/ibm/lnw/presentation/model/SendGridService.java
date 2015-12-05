package com.ibm.lnw.presentation.model;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.lnw.backend.domain.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import java.util.Map;
import java.util.Set;


/**
 * Created by Jan Valentik on 11/29/2015.
 */
public class SendGridService {
	private static String userName;
	private static String password;



	public static void sendEmail(Request request) throws Exception {
		VCAPParser();
		if(userName == null || password == null) {
			throw new Exception("VCAP was not parsed correctly");
		}

		SendGrid sendGrid = new SendGrid(userName, password);
		SendGrid.Email email = new SendGrid.Email();
		email.addTo(request.getSubmitterUserName());
		email.setFrom("lnwtool@sk.ibm.com");
		email.setSubject(request.getSubmitterUserName() + " sent you a new request in the LNW Tool");
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

	private static void VCAPParser() {
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		System.out.println(VCAP_SERVICES);
		String serviceName = null;

		if (VCAP_SERVICES != null) {
			// parse the VCAP JSON structure
			JsonObject obj = (JsonObject) new JsonParser().parse(VCAP_SERVICES);
			Map.Entry<String, JsonElement> dbEntry = null;
			Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
			for (Map.Entry<String, JsonElement> eachEntry : entries) {
				System.out.println(eachEntry.getKey());
				if (eachEntry.getKey().toUpperCase().equals("SENDGRID")) {
					dbEntry = eachEntry;
					break;
				}
			}
			if (dbEntry == null) {
				throw new RuntimeException("Could not find sendgrid key in VCAP_SERVICES env variable");
			}

			obj = (JsonObject) ((JsonArray) dbEntry.getValue()).get(0);
			serviceName = (String) dbEntry.getKey();
			System.out.println("Service Name - " + serviceName);

			obj = (JsonObject) obj.get("credentials");

			userName = obj.get("username").getAsString();
			password = obj.get("password").getAsString();
		}
	}
}
