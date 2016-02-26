package com.ibm.lnw.backend.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Jan Valentik on 11/30/2015.
 */
@NamedQueries(
        @NamedQuery(name="Attachment.findByMainRequest",
                query="SELECT a FROM Attachment a WHERE a.mainRequest.id=:filter")

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


    @ManyToOne
    @JoinColumn(name = "REQUEST_ID", referencedColumnName = "ID")
    private Request mainRequest;

	private String fileName;
	private String mimeType;
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

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Request getMainRequest() {
		return mainRequest;
	}

	public void setMainRequest(Request mainRequest) {
		this.mainRequest = mainRequest;
        mainRequest.addAttachment(this);
	}


}

