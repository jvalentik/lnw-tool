package com.ibm.lnw.presentation.model;

import com.vaadin.ui.Notification;
import org.vaadin.easyuploads.FileBuffer;
import org.vaadin.easyuploads.MultiFileUpload;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Jan Valentik on 12/5/2015.
 */
public class FileUploader extends MultiFileUpload {
	private HashMap<String, File> fileStorage;

	public FileUploader(HashMap<String, File> fileStorage) {
		this.fileStorage = fileStorage;
	}

	@Override
	protected void handleFile(File file, String fileName, String mimeType, long length) {
		fileStorage.put(fileName + "?" + mimeType, file);
		System.out.println(fileName + " " + mimeType);
		Notification.show(fileName + " uploaded successfully", Notification.Type.TRAY_NOTIFICATION);
	}

	@Override
	protected FileBuffer createReceiver() {
		FileBuffer receiver = super.createReceiver();
		receiver.setDeleteFiles(false);
		return receiver;
	}

	public HashMap<String, File> getFileStorage() {
		return fileStorage;
	}
}
