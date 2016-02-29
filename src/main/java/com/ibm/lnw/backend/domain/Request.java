package com.ibm.lnw.backend.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jan Valentik on 11/15/2015.
 */
@NamedQueries({
		@NamedQuery(name="Request.findAll",
				query="SELECT r FROM Request r"),
		@NamedQuery(name="Request.findAllByUser",
				query="SELECT r FROM Request r WHERE r.createdBy.userName=:filter"),
		@NamedQuery(name="Request.findAllByUserAndFilter",
				query = "SELECT r FROM Request r WHERE r.createdBy.userName=:filter1 AND (LOWER(r.leadingWBS) LIKE" +
						" :filter2 OR LOWER(r.customerName) LIKE :filter2)"),
		@NamedQuery(name="Request.findByID", query = "SELECT r FROM Request r WHERE r.id=:filter"),
		@NamedQuery(name="Request.findByFilter", query = "SELECT r FROM Request r WHERE LOWER(r.leadingWBS) LIKE " +
				":filter OR LOWER(r.customerName) LIKE :filter"),
    @NamedQuery(name = "Request.findAssigned", query = "SELECT r FROM Request r WHERE r.pmaName =:filter")
})
@Entity
@Table(name = "REQUEST")
public class Request implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

	@NotNull
	@Pattern(regexp = "([a-zA-Z]\\.)+(\\w{5}\\.)+(\\d{3})", message = "WBS must be in format x.xxxxx.xxx")
	private String leadingWBS;
	@NotNull
	private String customerName;
	@NotNull
	private String contractNumber;

	private String services;
	@NotNull
	private String pmaName;
	private String pexName;
	private String comments;

	@ManyToOne
    @JoinColumn(name = "CREATEDBY_ID", referencedColumnName = "ID")
    private User createdBy;

	private String lastModifiedBy;

	@Temporal(TemporalType.DATE)
	private Date modifiedOn;

	@Temporal(TemporalType.DATE)
	private Date dateTimeStamp;

	@Enumerated(EnumType.ORDINAL)
	private RequestStatus status;

	public Request() {
		customerName = contractNumber = services = pmaName = pexName = comments = leadingWBS ="";
		dateTimeStamp = new Date();
		modifiedOn = dateTimeStamp;
		status = RequestStatus.Open;
	}



   	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName.trim();
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber.trim();
	}

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services.trim();
	}

	public String getPmaName() {
		return pmaName;
	}

	public void setPmaName(String pmaName) {
		this.pmaName = pmaName.trim();
	}

	public String getPexName() {
		return pexName;
	}

	public void setPexName(String pexName) {
		this.pexName = pexName.trim();
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments.trim();
	}

	public Date getDateTimeStamp() {
		return dateTimeStamp;
	}

	public String getLeadingWBS() {
		return leadingWBS;
	}

	public void setLeadingWBS(String leadingWBS) {
		this.leadingWBS = leadingWBS.trim();
	}

    public void setDateTimeStamp(Date dateTimeStamp) {
        this.dateTimeStamp = dateTimeStamp;
    }

    public RequestStatus getStatus() {
		return status;
	}

	public void setStatus(RequestStatus status) {
		this.status = status;
	}

    public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

	public void setId(long id) {
		this.id = id;
	}

    public long getId() {
        return id;
    }

    public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

}
