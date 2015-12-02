package com.ibm.lnw.backend.domain;

/**
 * Created by Jan Valentik on 11/28/2015.
 */
public enum UserRole {
	Sender, Processor, Manager, Undefined;

	public static UserRole getRole(String role) {
		switch (role.toLowerCase()) {
			case "sender":
				return Sender;
			case "processor":
				return Processor;
			case "manager":
				return Manager;
			default:
				return Undefined;
		}
	}
}


