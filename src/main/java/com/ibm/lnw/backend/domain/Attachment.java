package com.ibm.lnw.backend.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Jan Valentik on 11/30/2015.
 */

@Entity
public class Attachment implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Lob
	private byte[] fileContent;

	@ManyToOne
	private Request mainRequest;

	private String fileName;
	private String mimeType;
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

	public void setId(Long id) {
		this.id = id;
	}

	public Request getMainRequest() {
		return mainRequest;
	}

	public void setMainRequest(Request mainRequest) {
		this.mainRequest = mainRequest;
	}
}
