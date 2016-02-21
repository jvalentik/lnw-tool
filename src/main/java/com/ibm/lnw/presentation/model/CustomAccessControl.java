package com.ibm.lnw.presentation.model;

import com.vaadin.cdi.access.AccessControl;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

/**
 * Created by Jan Valentik on 11/20/2015.
 */
@Alternative
public class CustomAccessControl extends AccessControl {

	@Inject
	private UserInfo userInfo;

	@Override
	public boolean isUserInRole(String s) {
		if (isUserSignedIn()) {
			for (String role : userInfo.getRoles()) {
				if (role.equals(s)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isUserSignedIn() {
		return userInfo.getUser() != null;
	}

	@Override
	public String getPrincipalName() {
		if (isUserSignedIn()) {
			return userInfo.getUser().getUserName();
		}
		return null;
	}

	public String getFirstName() {
		if (isUserSignedIn()) {
			return userInfo.getUser().getFirstName();
		}
		return null;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}
}
