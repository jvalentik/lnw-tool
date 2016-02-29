package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.RequestService;
import com.ibm.lnw.backend.domain.Request;
import com.ibm.lnw.backend.domain.RequestStatus;
import com.ibm.lnw.presentation.model.CustomAccessControl;
import com.ibm.lnw.presentation.views.events.RequestEvent;
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@CDIView(value = "request-list", supportsParameters = true)
@ViewMenuItem(icon = FontAwesome.LIST, title = "Requests", order = 2)
public class RequestListView extends MVerticalLayout implements View {

	@Inject
	private RequestService requestService;

	@Inject
	private CustomAccessControl accessControl;

	@Inject
	private RequestForm requestForm;

	MTable<Request> requestMTable = new MTable(Request.class).withFullWidth().
			withFullHeight();

	MHorizontalLayout mainContent = new MHorizontalLayout(requestMTable).
			withFullWidth().withMargin(false);

	TextField filter = new TextField();

    OptionGroup filterSettings = new OptionGroup("Filter by:");

	Header header = new Header("LNW Requests").setHeaderLevel(2);

    private boolean allRequests;

	@PostConstruct
	public void init() {
        requestMTable.addMValueChangeListener(mValueChangeEvent -> editRequest(mValueChangeEvent.getValue()));
		filter.setInputPrompt("Filter requests...");
		filter.addTextChangeListener(textChangeEvent -> listRequests(textChangeEvent.getText()));
        filterSettings.addItems( Boolean.TRUE , Boolean.FALSE );
        filterSettings.setItemCaption( Boolean.TRUE , "Assigned to me" );
        filterSettings.setItemCaption( Boolean.FALSE , "All requests" );
        filterSettings.setValue( Boolean.FALSE );
        filterSettings.addValueChangeListener(valueChangeEvent -> {
            listRequests();
        });
		layout();
		adjustTableColumns();
       	listRequests();
	}

	private void layout() {
		removeAllComponents();
		addComponents(new MHorizontalLayout(header,filterSettings, filter)
							.expand(header)
							.alignAll(Alignment.MIDDLE_LEFT),
					mainContent
			);
			filter.setSizeUndefined();
		setMargin(new MarginInfo(false, true, true, true));
		expand(mainContent);
        if (!accessControl.isUserInRole("Record_Owner")) {
            filterSettings.setVisible(false);
        }

	}

	private void adjustTableColumns() {
		requestMTable.setVisibleColumns(new Object[] {"leadingWBS", "customerName", "dateTimeStamp", "createdBy",
                "pmaName", "status"});
        requestMTable.setColumnHeaders(new String[] {"WBS", "Customer name", "Submitted on", "Requestor","Assigned",
                "Current status"});

	}

    private void listRequests() {
        if (accessControl.isUserInRole("Initiator")) {
            requestMTable.setBeans(new ArrayList<>(requestService.findAllByUser(accessControl.getPrincipalName())));
        }
        else {
            if (filterSettings.getValue() == Boolean.TRUE) {
                requestMTable.setBeans(new ArrayList<>(requestService.findAssigned(accessControl.getPrincipalName())));
            }
            else {
                requestMTable.setBeans(new ArrayList<>(requestService.findAll()));
            }
        }
    }
	private void listRequests(String  filter) {
        if (filter == null || filter.isEmpty()) {
            listRequests();
            return;
        }
        if (accessControl.isUserInRole("Initiator")) {
            requestMTable.setBeans(new ArrayList<>(requestService.findAllByUserAndFilter(accessControl
                    .getPrincipalName(), filter)));
        }
        else {
            requestMTable.setBeans(new ArrayList<>(requestService.findByFilter(filter)));
        }

    }


	void editRequest(Request request) {
		if (request != null) {
			openEditor(request);
		} else {
			closeEditor();
		}
	}

	private void openEditor(Request request) {
		requestForm.setEntity(request);
        if (accessControl.isUserInRole("Initiator") && request.getStatus() != RequestStatus.Clarification) {
            requestForm.setEditable(false);
        }
        if (accessControl.isUserInRole("Viewer")) {
            requestForm.setEditable(false);
        }
		mainContent.addComponent(requestForm);
        requestForm.focusFirst();
	}

	private void closeEditor() {
		mainContent.removeComponent(requestForm);
	}

	void saveRequest(@Observes @RequestEvent(RequestEvent.Type.SAVE) Request request) {
		listRequests();
		closeEditor();
	}

    void resetRequest(@Observes @RequestEvent(RequestEvent.Type.REFRESH) Request request) {
        listRequests();
        closeEditor();
    }


	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
       if (event.getParameters().contains("?request_id=")) {
            List<Request> foundRequests = requestService.findByID(Integer.parseInt(event.getParameters().split("=")[1]));
            if (!foundRequests.isEmpty()) {
                editRequest(foundRequests.get(0));
            }
            else {
                Notification.show("Request not found", "The request you were trying to open is inaccessible to you",
                        Notification.Type.WARNING_MESSAGE);
            }

        }

	}


}
