package com.ibm.lnw.backend.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jan Valentik on 11/15/2015.
 */
@NamedQueries({
		@NamedQuery(name="Request.findAll",
					query="SELECT r FROM Request r"),
		@NamedQuery(name="Request.findAllByUser",
					query="SELECT r FROM Request r WHERE r.submitterUserName=:filter"),
		@NamedQuery(name="Request.findAllByUserAndFilter",
					query = "SELECT r FROM Request r WHERE r.submitterUserName=:filter1 AND (LOWER(r.leadingWBS) LIKE :filter2 OR LOWER(r.customerName) LIKE :filter2)")
})
@Entity
public class Request implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String leadingWBS;
	private String customerName;
	private String contractNumber;
	private String services;
	private String pmaName;
	private String pexName;
	private String comments;
	private String submitterUserName;
	private Date dateTimeStamp;
	private RequestStatus status;

	public Request() {
		customerName = contractNumber = services = pmaName = pexName = comments = submitterUserName = leadingWBS ="";
		SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyy");
		dateTimeStamp = new Date();
		status = RequestStatus.Pending;
	}

		public String getSubmitterUserName() {
		return submitterUserName;
	}

	public void setSubmitterUserName(String submitterUserName) {
		this.submitterUserName = submitterUserName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services;
	}

	public String getPmaName() {
		return pmaName;
	}

	public void setPmaName(String pmaName) {
		this.pmaName = pmaName;
	}

	public String getPexName() {
		return pexName;
	}

	public void setPexName(String pexName) {
		this.pexName = pexName;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getDateTimeStamp() {
		return dateTimeStamp;
	}

	public String getLeadingWBS() {
		return leadingWBS;
	}

	public void setLeadingWBS(String leadingWBS) {
		this.leadingWBS = leadingWBS;
	}

	public int getId() {
		return id;
	}

	public RequestStatus getStatus() {
		return status;
	}

	public void setStatus(RequestStatus status) {
		this.status = status;
	}
}