package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.AttachmentService;
import com.ibm.lnw.backend.RequestService;
import com.ibm.lnw.backend.domain.Attachment;
import com.ibm.lnw.backend.domain.Contract;
import com.ibm.lnw.backend.domain.Request;
import com.ibm.lnw.backend.domain.RequestStatus;
import com.ibm.lnw.presentation.AppUI;
import com.ibm.lnw.presentation.model.CustomAccessControl;
import com.ibm.lnw.presentation.model.SendGridService;
import com.vaadin.cdi.CDIView;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
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

/**
 * Created by Jan Valentik on 11/15/2015.
 */
@CDIView("main-form")
@ViewMenuItem(icon = FontAwesome.ENVELOPE, title = "New request", order = ViewMenuItem.BEGINNING)
public class MainFormView extends CustomComponent implements View {
	private Table table;
	private FieldGroup group;
	private Request request;
	private HashMap<String, HashMap<String, File> > fileContainer;

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
						"Start by adding WBS", Notification.Type.TRAY_NOTIFICATION);
		}
	}

	@PostConstruct
	public void init() {
		AppUI.getMenu().setVisible(true);
		request = new Request();
		fileContainer = new HashMap<>();
		request.setSubmitterUserName(accessControl.getPrincipalName());
		BeanItem<Request> item = new BeanItem<>(request);
		group = new FieldGroup(item);
		VerticalLayout parentLayout = new VerticalLayout();

		Field<?> customerName = group.buildAndBind("customerName");
		customerName.addValidator(new StringLengthValidator("Customer name must be entered", 1, 50, false));
		customerName.setRequired(true);


		Field<?> contractNumber = group.buildAndBind("Contract Number (OCPS)", "contractNumber");
		contractNumber.addValidator(new StringLengthValidator("Contract number must entered", 1, 50, false));
		contractNumber.setRequired(true);


		Field<?> servicesContractNumber = group.buildAndBind("Services Contract Number", "services");
		servicesContractNumber.setBuffered(true);

		Field<?> pmaName = group.buildAndBind("PMA Name", "pmaName");
		contractNumber.setRequired(true);
		contractNumber.setBuffered(true);

		Field<?> pexName = group.buildAndBind("Project Executive", "pexName");
		contractNumber.setRequired(true);
		contractNumber.setBuffered(true);

		Field<?> comments = group.buildAndBind("Add Comments", "comments", TextArea.class);
		comments.setHeight("200px");
		comments.setBuffered(true);
		table = new Table("WBS and attachments");
		table.setHeight("200px");
		table.setWidth("180px");
		table.addContainerProperty("WBS", String.class, null);
		table.setColumnWidth("WBS", 120);
		table.addContainerProperty("Files", Integer.class, null);
		table.setFooterVisible(true);
		table.setColumnFooter("WBS", "Click to add");
		table.addFooterClickListener((footerClickEvent) -> {
			HashMap<String, File> files = new HashMap<>();
			AddEntryDialogView dialogView = new AddEntryDialogView(request, table, files);
			UI.getCurrent().addWindow(dialogView);
			dialogView.addCloseListener(closeEvent -> {
				System.out.println("AddView closed");
				if (!request.getLeadingWBS().equals("")) {
					try {
						contract.findContractByWBS(request.getLeadingWBS());
						Item lastRow = table.getItem(table.lastItemId());
						fileContainer.put(lastRow.getItemProperty("WBS").getValue().toString(), files);
					}
					catch (Exception ex) {
						ex.printStackTrace();
						clearData();
						request = new Request();
						Notification.show("WBS record not found", Notification.Type.WARNING_MESSAGE);
						return;
					}
					((TextField) group.getField("customerName")).setValue(contract.getCustomerName());
					((TextField) group.getField("contractNumber")).setValue(contract.getContractNumber());
					((TextField) group.getField("services")).setValue(contract.getSapContract());
					((TextField) group.getField("pmaName")).setValue(contract.getPmaNotesId());
					((TextField) group.getField("pexName")).setValue(contract.getPeNotes());
				}
			});
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
		content.addComponent(customerName, 0, 0);
		content.addComponent(contractNumber, 1, 0);
		content.addComponent(servicesContractNumber, 2, 0);
		content.addComponent(pmaName, 0, 1);
		content.addComponent(pexName, 1, 1);
		content.addComponent(table, 0, 2);
		content.addComponent(comments, 1, 2);
		panel.setContent(content);

		Button submit = new Button("Submit");
		submit.setStyleName(ValoTheme.BUTTON_PRIMARY);
		submit.addClickListener((clickEvent) -> submitRequest());
		submit.setDescription("Submit the form for creation");
		Button reset = new Button("Reset");
		reset.setDescription("Reset changes");
		reset.addClickListener((clickEvent) -> group.discard());
		Button clear = new Button("Clear");
		clear.setDescription("Clear all the data");
		clear.addClickListener((clickEvent) -> clearData());
		HorizontalLayout buttonPanel = new HorizontalLayout();
		buttonPanel.setMargin(true);
		buttonPanel.setSpacing(true);
		buttonPanel.addComponent(submit);
		buttonPanel.addComponent(reset);
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
			request.setStatus(RequestStatus.Pending);
			int requestId = requestService.persist(request);
			fileContainer.forEach((v, k) -> {
				k.forEach((l, m) -> {
					Attachment attachment = new Attachment();
					byte[] bytes = new byte[(int) m.length()];
					try {
						FileInputStream fileInputStream = new FileInputStream(m);
						fileInputStream.read(bytes);
						attachment.setWbsId(request.getLeadingWBS());
						attachment.setRequestId(requestId);
						attachment.setFileName(l.split("\\?")[0]);
						attachment.setMimeType(l.split("\\?")[1]);
						attachment.setFileContent(bytes);
						fileInputStream.close();
						attachmentService.persist(attachment);
					}
					catch (IOException ex) {
						ex.printStackTrace();
					}

				});
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
	}
}

