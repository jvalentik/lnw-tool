package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.RequestService;
import com.ibm.lnw.backend.domain.Request;
import com.ibm.lnw.presentation.AppUI;
import com.ibm.lnw.presentation.model.CustomAccessControl;
import com.ibm.lnw.presentation.views.events.RequestEvent;
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;

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

    CheckBox switchFilter = new CheckBox("Assigned to me");

	Header header = new Header("LNW Requests").setHeaderLevel(2);

	@PostConstruct
	public void init() {
        requestMTable.addMValueChangeListener(mValueChangeEvent -> editRequest(mValueChangeEvent.getValue()));
		filter.setInputPrompt("Filter requests...");
		filter.addTextChangeListener(textChangeEvent -> listRequests(textChangeEvent.getText()));
		layout();
		adjustTableColumns();
        AppUI.getCurrent().setResizeLazy(true);
		Page.getCurrent().addBrowserWindowResizeListener(browserWindowResizeEvent -> {
			adjustTableColumns();
			layout();
		});
		listRequests();
	}

	private void layout() {
		removeAllComponents();
		addComponents(new MHorizontalLayout(header,switchFilter, filter)
							.expand(header)
							.alignAll(Alignment.MIDDLE_LEFT),
					mainContent
			);
			filter.setSizeUndefined();
		setMargin(new MarginInfo(false, true, true, true));
		expand(mainContent);
        if (!accessControl.isUserInRole("Record_Owner")) {
            switchFilter.setVisible(false);
        }
        switchFilter.addValueChangeListener(valueChangeEvent -> listRequests());
	}

	private void adjustTableColumns() {
		requestMTable.setVisibleColumns(new Object[] {"leadingWBS", "customerName", "dateTimeStamp", "createdBy", "status"});
        requestMTable.setColumnHeaders(new String[] {"WBS", "Customer name", "Submitted on", "Requestor", "Current status"});

	}

    private void listRequests() {
        System.out.println("CurrentUser role INITIATOR: " + accessControl.isUserInRole("Initiator"));
        System.out.println(accessControl.getPrincipalName());
        if (accessControl.isUserInRole("Initiator")) {
            requestMTable.setBeans(new ArrayList<>(requestService.findAllByUser(accessControl.getPrincipalName())));
        }
        else {
            if (switchFilter.getValue()) {
                requestMTable.setBeans(new ArrayList<>(requestService.findAssigned(accessControl.getPrincipalName())));
            }
            requestMTable.setBeans(new ArrayList<>(requestService.findAll()));
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

	}
}
