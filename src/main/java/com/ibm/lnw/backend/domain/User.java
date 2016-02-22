package com.ibm.lnw.backend.domain;

import com.ibm.lnw.presentation.model.MD5Hash;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by Jan Valentik on 11/20/2015.
 */
@NamedQueries({
		@NamedQuery(name="User.findAll",
				query="SELECT u FROM User u"),
		@NamedQuery(name = "User.findByUserName", query = "SELECT u FROM User u WHERE LOWER(u.userName) LIKE :filter"),
		@NamedQuery(name="User.findByName",
				query="SELECT u FROM User u WHERE LOWER(u.firstName) LIKE :filter OR LOWER(u.lastName) LIKE :filter"),
})

@Entity
public class User implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

	@NotNull(message = "IBM Intranet ID is required")
	@Pattern(regexp = ".*@([a-zA-Z]{2})\\.(ibm|IBM)\\.(com|COM)", message = "IBM Intranet ID is required")
	private String userName;

	@Size(min = 5, message = "Password must be at least 5 characters")
	private String password;

	@NotNull(message = "First name is required")
	private String firstName;

	@NotNull(message = "Last name is required")
	private String lastName;

	@Enumerated(EnumType.ORDINAL)
	private UserRole userRole;

	@Enumerated(EnumType.ORDINAL)
	private Permissions permissions;

	@OneToMany(mappedBy = "createdBy")
	private Set<Request> requestSet;


	public User() {
		password = lastName = firstName = userName = "";
		userRole = UserRole.Initiator;
		permissions = Permissions.Create;

	}

	public void setId(long id) {
		this.id = id;
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

	public Long getId() {
		return id;
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
		this.password = MD5Hash.encrypt(password);
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public Set<Request> getRequestSet() {
		return requestSet;
	}

	public void setRequestSet(Set<Request> requestSet) {
		this.requestSet = requestSet;
	}

	public Permissions getPermissions() {
		return permissions;
	}

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}

	public boolean isPersisted() {
		return id > 0;
	}

	@Override
	public String toString() {
		return userName + " " + password + " " + firstName + " " + lastName + " " + userRole;
	}
}
