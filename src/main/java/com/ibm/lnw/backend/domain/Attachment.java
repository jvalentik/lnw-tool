package com.ibm.lnw.backend.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Jan Valentik on 11/30/2015.
 */
@NamedQueries(
		@NamedQuery(name="Attachment.findAllByFilter",
				query="SELECT a FROM Attachment a WHERE a.requestId=:filter")
)
@Entity
public class Attachment implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Lob
	private byte[] fileContent;

	private String fileName;
	private String mimeType;
	private int requestId;
	private String wbsId;

	public long getId() {
		return id;
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

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public String getWbsId() {
		return wbsId;
	}

	public void setWbsId(String wbsId) {
		this.wbsId = wbsId;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
}
