package com.ibm.lnw.backend.domain;

/**
 * Created by Jan Valentik on 11/28/2015.
 */
public enum UserRole {
	ADMINISTRATOR, INITIATOR, MANAGER, RECORD_OWNER;

	public static UserRole getRole(String role) {
		switch (role.toLowerCase()) {
			case "administrator":
				return ADMINISTRATOR;
			case "initiator":
				return INITIATOR;
			case "manager":
				return MANAGER;
			case "record owner":
				return RECORD_OWNER;
			default:
				return null;
		}
	}
}


