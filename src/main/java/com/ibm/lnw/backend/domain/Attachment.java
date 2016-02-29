package com.ibm.lnw.backend.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Jan Valentik on 11/30/2015.
 */
@NamedQueries(
        @NamedQuery(name="Attachment.findByMainRequest",
                query="SELECT a FROM Attachment a WHERE a.mainRequest=:filter")

)

@Entity
@Table(name = "ATTACHMENT")
public class Attachment implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
   	@Column(name = "ID")
    private long id;

	@Lob
	private byte[] fileContent;


    private long  mainRequest;

	private String fileName;
	private String wbsId;

	public long getId() {
		return id;
	}

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getWbsId() {
		return wbsId;
	}

	public void setWbsId(String wbsId) {
		this.wbsId = wbsId;
	}

	public long getMainRequest() {
		return mainRequest;
	}

	public void setMainRequest(long mainRequest) {
		this.mainRequest = mainRequest;
	}


}

