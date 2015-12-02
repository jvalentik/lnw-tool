package com.ibm.lnw.presentation.model;

import com.ibm.lnw.backend.domain.User;
import com.vaadin.cdi.UIScoped;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jan Valentik on 11/27/2015.
 */
@UIScoped
public class UserInfo implements Serializable{
	private User user;
	private List<String> roles;

	public UserInfo() {
		roles =  new LinkedList<>();
		this.user = null;
	}

	public void setUser(User user) {
		this.user = user;
		roles.clear();
		if (user != null) {
			roles.add(user.getRole().toString());
		}
	}

	public User getUser() {
		return user;
	}

	public String getUserName() {
		if (user == null) {
			return "anonymous";
		}
		else {
			return user.getUserName();
		}
	}

	public List<String> getRoles() {
		return roles;
	}

}
