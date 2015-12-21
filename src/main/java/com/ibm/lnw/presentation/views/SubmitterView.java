package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.AttachmentService;
import com.ibm.lnw.backend.RequestService;
import com.ibm.lnw.backend.domain.Attachment;
import com.ibm.lnw.backend.domain.Contract;
import com.ibm.lnw.backend.domain.Request;
import com.ibm.lnw.backend.domain.RequestStatus;
import com.ibm.lnw.presentation.AppUI;
import com.ibm.lnw.presentation.model.CustomAccessControl;
import com.ibm.lnw.presentation.model.FileUploader;
import com.ibm.lnw.presentation.model.SendGridService;
import com.vaadin.cdi.CDIView;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jan Valentik on 11/15/2015.
 */
@CDIView("submitter-view")
@ViewMenuItem(icon = FontAwesome.ENVELOPE, title = "New request", order = ViewMenuItem.BEGINNING)
public class SubmitterView extends CustomComponent implements View {
	private Table table;
	private FieldGroup group;
	private Request request;
	private List<Attachment> attachments;
	private  HashMap<String, File> fileStorage;

	@Inject
	private CustomAccessControl accessControl;

	@Inject
	private RequestService requestService;

	@Inject
	private Contract contract;

	@Inject
	private AttachmentService attachmentService;


	public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
		if (viewChangeEvent.getOldView().equals("login")) {
				Notification.show("Welcome " + accessControl.getFirstName(),
						"Start by typing in WBS", Notification.Type.TRAY_NOTIFICATION);
		}
	}

	@PostConstruct
	public void init() {
		AppUI.getMenu().setVisible(true);
		request = new Request();
		attachments = new LinkedList<>();
		fileStorage = new HashMap<>();
		request.setSubmitterUserName(accessControl.getPrincipalName());
		FileUploader fileUploader = new FileUploader(fileStorage);
		fileUploader.setEnabled(false);
		BeanItem<Request> item = new BeanItem<>(request);
		group = new FieldGroup(item);
		VerticalLayout parentLayout = new VerticalLayout();

		Field<?> leadingWBS = group.buildAndBind("WBS", "leadingWBS");
		leadingWBS.focus();
		leadingWBS.addValidator(new BeanValidator(Request.class, "leadingWBS"));
		leadingWBS.setRequired(true);
		leadingWBS.addValueChangeListener(valueChangeEvent -> {
			if (leadingWBS.isValid()) {
				try {
					contract.findContractByWBS(leadingWBS.toString());
				} catch (Exception ex) {
					ex.printStackTrace();
					Notification.show("WBS record not found", Notification.Type.WARNING_MESSAGE);
					return;
				}
				group.getField("customerName").setEnabled(true);
				((TextField) group.getField("customerName")).setValue(contract.getCustomerName());
				group.getField("contractNumber").setEnabled(true);
				((TextField) group.getField("contractNumber")).setValue(contract.getContractNumber());
				group.getField("services").setEnabled(true);
				((TextField) group.getField("services")).setValue(contract.getSapContract());
				group.getField("pmaName").setEnabled(true);
				((TextField) group.getField("pmaName")).setValue(contract.getPmaNotesId());
				group.getField("pexName").setEnabled(true);
				((TextField) group.getField("pexName")).setValue(contract.getPeNotes());
				table.setEnabled(true);
				group.getField("comments").setEnabled(true);
				fileUploader.setEnabled(true);
			}
		});

		Field<?> customerName = group.buildAndBind("customerName");
		customerName.addValidator(new StringLengthValidator("Customer name must be entered", 1, 50, false));
		customerName.setRequired(true);
		customerName.setEnabled(false);


		Field<?> contractNumber = group.buildAndBind("Contract Number (OCPS)", "contractNumber");
		contractNumber.addValidator(new StringLengthValidator("Contract number must entered", 1, 50, false));
		contractNumber.setRequired(true);
		contractNumber.setEnabled(false);


		Field<?> servicesContractNumber = group.buildAndBind("Services Contract Number", "services");
		servicesContractNumber.setBuffered(true);
		servicesContractNumber.setEnabled(false);


		Field<?> pmaName = group.buildAndBind("PMA Name", "pmaName");
		pmaName.setRequired(true);
		pmaName.setBuffered(true);
		pmaName.setEnabled(false);

		Field<?> pexName = group.buildAndBind("Project Executive", "pexName");
		contractNumber.setRequired(true);
		contractNumber.setBuffered(true);
		contractNumber.setEnabled(false);

		Field<?> comments = group.buildAndBind("Add Comments", "comments", TextArea.class);
		comments.setHeight("200px");
		comments.setBuffered(true);
		comments.setEnabled(false);


		fileUploader.setEnabled(false);
		fileUploader.setWidth("130px");
		fileUploader.setUploadButtonCaption("Browse ...");
		fileUploader.setRootDirectory(new File(System.getProperty("java.io.tmpdir")).getPath());
		fileUploader.setImmediate(true);

		table = new Table("Additional WBS and attachments");
		table.setEnabled(false);
		table.setHeight("200px");
		table.setWidth("180px");
		table.addContainerProperty("WBS", String.class, null);
		table.setColumnWidth("WBS", 120);
		table.addContainerProperty("Files", Integer.class, null);
		table.setFooterVisible(true);
		table.setColumnFooter("WBS", "Add more WBS...");
		table.addFooterClickListener((footerClickEvent) -> {
			AddEntryDialogView dialogView = new AddEntryDialogView(((TextField) group.getField("contractNumber"))
					.getValue(),table, attachments, contract);
			UI.getCurrent().addWindow(dialogView);

		});
		table.setRequired(true);
		table.setBuffered(true);

		Label headLabel = new Label("Create new request");
		headLabel.setWidth(null);
		headLabel.setStyleName(ValoTheme.LABEL_H1);


		parentLayout.addComponent(headLabel);
		parentLayout.setComponentAlignment(headLabel, Alignment.TOP_LEFT);

		Panel panel = new Panel("Request Details");
		panel.setHeight("80%");
		panel.setWidth("90%");

		GridLayout content = new GridLayout(3, 3);
		content.setSizeFull();
		content.setMargin(true);
		content.addComponent(leadingWBS, 0, 0);
		content.addComponent(customerName, 1, 0);
		content.addComponent(contractNumber, 2, 0);
		content.addComponent(servicesContractNumber, 0, 1);
		content.addComponent(pmaName, 1, 1);
		content.addComponent(pexName, 2, 1);
		content.addComponent(table, 0, 2);
		content.addComponent(comments, 1, 2);
		content.addComponent(fileUploader, 2, 2);
		panel.setContent(content);

		Button submit = new Button("Submit");
		submit.setStyleName(ValoTheme.BUTTON_PRIMARY);
		submit.addClickListener((clickEvent) -> submitRequest());
		submit.setDescription("Submit the form for creation");
		Button clear = new Button("Clear");
		clear.setDescription("Clear all the data");
		clear.addClickListener((clickEvent) -> clearData());
		HorizontalLayout buttonPanel = new HorizontalLayout();
		buttonPanel.setMargin(true);
		buttonPanel.setSpacing(true);
		buttonPanel.addComponent(submit);
		buttonPanel.addComponent(clear);
		parentLayout.addComponent(panel);
		parentLayout.setComponentAlignment(panel, Alignment.TOP_CENTER);
		parentLayout.setExpandRatio(panel, 1f);
		parentLayout.addComponent(buttonPanel);
		parentLayout.setComponentAlignment(buttonPanel, Alignment.BOTTOM_LEFT);
		setCompositionRoot(parentLayout);
	}

	private void submitRequest() {
		try {
			group.commit();
			request.setStatus(RequestStatus.OPEN);
			int requestId = requestService.persist(request);
			if (!fileStorage.isEmpty()) {
				fileStorage.forEach((k, v) -> {
					byte[] bytes = new byte[(int) v.length()];
					try {
						Attachment attachment = new Attachment();
						FileInputStream fileInputStream = new FileInputStream(v);
						fileInputStream.read(bytes);
						attachment.setWbsId(request.getLeadingWBS());
						attachment.setFileName(k.split("\\?")[0]);
						attachment.setMimeType(k.split("\\?")[1]);
						attachment.setFileContent(bytes);
						fileInputStream.close();
						attachments.add(attachment);
					} catch (IOException ex) {
						Notification.show("Saving file failed", Notification.Type.WARNING_MESSAGE);
					}
				});
			}
			attachments.forEach(attachment -> {
				attachment.setRequestId(requestId);
				attachmentService.persist(attachment);
			});

			SendGridService.sendEmail(request);
			Notification.show("Your request has been submitted", Notification.Type.TRAY_NOTIFICATION);
		}
		catch (FieldGroup.CommitException ex) {
			Notification.show("Mandatory fields not filled", "Please make sure all mandatory are filled",
					Notification.Type.WARNING_MESSAGE);
			return;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Notification.show("Email not sent", "Failed to send notification email", Notification.Type.WARNING_MESSAGE);
		}
		request = new Request();
		request.setSubmitterUserName(accessControl.getPrincipalName());
		group.clear();
		table.removeAllItems();

	}

	private void clearData() {
		group.clear();
		table.removeAllItems();
		request = new Request();
	}
}

