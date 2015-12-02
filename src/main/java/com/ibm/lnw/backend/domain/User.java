package com.ibm.lnw.backend.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by Jan Valentik on 11/20/2015.
 */

@NamedQueries({
		@NamedQuery(name="User.findAll",
				query="SELECT u FROM User u"),
		@NamedQuery(name="User.findByName",
				query="SELECT u FROM User u WHERE u.userName LIKE :filter"),
})
@Entity
public class User implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@NotNull(message = "IBM Intranet ID is required")
	@Pattern(regexp = ".*@([a-zA-Z]{2})\\.(ibm|IBM)\\.(com|COM)", message = "IBM Intranet ID is required")
	private String userName;

	@Size(min = 5, message = "Password must be at least 5 characters")
	private String password;

	@NotNull(message = "First name is required")
	private String firstName;

	@NotNull(message = "Last name is required")
	private String lastName;

	private UserRole userRole;


	public User() {
		password = lastName = firstName = userName = "";
		userRole = UserRole.Undefined;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			return ((User) obj).getUserName().equals(this.userName) && ((User) obj).getPassword().equals(this.password);
		}
		return false;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserRole getRole() {
		return userRole;
	}

	public void setRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public boolean isPersisted() {
		return id > 0;
	}

	@Override
	public String toString() {
		return userName + " " + password + " " + firstName + " " + lastName + " " + userRole;
	}
}
