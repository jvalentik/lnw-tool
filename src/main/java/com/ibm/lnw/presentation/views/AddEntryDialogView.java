package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.domain.Request;
import com.vaadin.data.Item;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.easyuploads.FileBuffer;
import org.vaadin.easyuploads.MultiFileUpload;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Jan Valentik on 11/15/2015.
 */
public class AddEntryDialogView extends Window {
	private static final String TEMP_FILE_DIR = new File(System.getProperty("java.io.tmpdir")).getPath();
	private TextField wbsField;
	private HashMap<String, File> fileStorage;
	private Request request;

	public AddEntryDialogView(Request request, Table table, HashMap<String, File> fileStorage) {
		wbsField = new TextField("WBS");
		this.fileStorage = fileStorage;
		this.request = request;
		init(this.request, table);
	}

	private void init(Request request, Table table) {
		Button addButton = new Button("Add");
		addButton.setWidth("90px");
		addButton.setEnabled(false);
		wbsField = new TextField("WBS");
		wbsField.addTextChangeListener((valueChangeEvent) -> addButton.setEnabled(true));
		wbsField.setRequired(true);
		setCaption("Add Entry");
		setModal(true);
		GridLayout parentLayout = new GridLayout(2, 2);
		parentLayout.setMargin(true);
		parentLayout.setSpacing(true);
		parentLayout.setSizeFull();
		center();
		setWidth("500px");
		setHeight("300px");
		setContent(parentLayout);
		parentLayout.addComponent(wbsField, 0, 0);
		MultiFileUpload uploadField = new MultiFileUpload() {
			@Override
			protected void handleFile(File file, String fileName,
			                          String mimeType, long length) {
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
		};
		uploadField.setWidth("130px");
		uploadField.setUploadButtonCaption("Browse ...");
		uploadField.setRootDirectory(TEMP_FILE_DIR);
		uploadField.setImmediate(true);
		parentLayout.addComponent(uploadField, 1, 0);
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setMargin(true);
		buttons.setSpacing(true);

		Button cancelButton = new Button("Cancel");
		addButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		addButton.addClickListener((clickEvent) -> {
			if (wbsField.getValue().equals("")) {
				Notification.show("WBS is mandatory", "Please make sure you entered WBS", Notification.Type.WARNING_MESSAGE);
			}
			else {
				if (request.getLeadingWBS().equals("")) {
					request.setLeadingWBS(wbsField.getValue());
				}

				Object newItemId = table.addItem();
				Item nextRow = table.getItem(newItemId);
				nextRow.getItemProperty("WBS").setValue(wbsField.getValue());
				nextRow.getItemProperty("Files").setValue(fileStorage.size());
				close();
			}
		});
		cancelButton.addClickListener((clickEvent) -> {
			fileStorage.forEach((k, v) -> v.delete());
			close();
		});
		buttons.addComponent(addButton);
		buttons.addComponent(cancelButton);
		parentLayout.addComponent(buttons, 0, 1);
	}


}
