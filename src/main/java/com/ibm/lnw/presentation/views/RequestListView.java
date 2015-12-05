package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.RequestService;
import com.ibm.lnw.backend.domain.Request;
import com.ibm.lnw.presentation.AppUI;
import com.ibm.lnw.presentation.ScreenSize;
import com.ibm.lnw.presentation.model.CustomAccessControl;
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
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

	Header header = new Header("Customers").setHeaderLevel(2);

	@PostConstruct
	public void init() {
        requestMTable.addMValueChangeListener(mValueChangeEvent -> editRequest(mValueChangeEvent.getValue()));
		filter.setInputPrompt("Filter requests...");
		filter.addTextChangeListener(textChangeEvent -> listRequests(textChangeEvent.getText()));
		layout();
		adjustTableColumns();
        UI.getCurrent().setResizeLazy(true);
		Page.getCurrent().addBrowserWindowResizeListener(browserWindowResizeEvent -> {
			adjustTableColumns();
			layout();
		});
		listRequests(accessControl.getPrincipalName());
	}

	private void layout() {
		removeAllComponents();
		if (ScreenSize.getScreenSize() == ScreenSize.LARGE) {
			addComponents(
					new MHorizontalLayout(header, filter)
							.expand(header)
							.alignAll(Alignment.MIDDLE_LEFT),
					mainContent
			);
			filter.setSizeUndefined();
		} else {
			addComponents(
					header,
					new MHorizontalLayout(filter)
							.expand(filter)
							.alignAll(Alignment.MIDDLE_LEFT),
					mainContent
			);
		}
		setMargin(new MarginInfo(false, true, true, true));
		expand(mainContent);
	}

	private void adjustTableColumns() {
		if (ScreenSize.getScreenSize() == ScreenSize.LARGE) {
			requestMTable.setVisibleColumns(new Object[] {"leadingWBS", "customerName", "dateTimeStamp", "status"});
			requestMTable.setColumnHeaders(new String[] {"WBS", "Customer name", "Submitted on", "Current status"});
		} else {
			// Only show one (generated) column with combined first + last name
			if (requestMTable.getColumnGenerator("WBScustomer") == null) {
				requestMTable.addGeneratedColumn("WBSCustomer", (table, object1, object2) -> {
					Request request = (Request) object1;
					return request.getLeadingWBS() + " " + request.getCustomerName();
				});
			}
			if (ScreenSize.getScreenSize() == ScreenSize.MEDIUM) {
				requestMTable.setVisibleColumns(new Object[] {"WBSCustomer", "status"});
				requestMTable.setColumnHeaders("WBS Customer", "Current status");
			} else {
				requestMTable.setVisibleColumns(new Object[] {"WBSCustomer"});
				requestMTable.setColumnHeaders("WBS Customer");
			}
		}
	}

	private void listRequests(String ... filter) {
		if (filter == null) {
			requestMTable.setBeans(new ArrayList<>(requestService.findAll()));
		}
		else if (filter.length == 1) {
			requestMTable.setBeans(new ArrayList<>(requestService.findAllByUser(filter[0])));
		}
		else {
			requestMTable.setBeans(new ArrayList<>(requestService.findAllByUserAndFilter(filter[0], filter[1])));
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
		if (ScreenSize.getScreenSize() == ScreenSize.LARGE) {
			mainContent.addComponent(requestForm);
			requestForm.focusFirst();
		} else {
			AppUI.get().getContentLayout().replaceComponent(this, requestForm);
		}
	}

	private void closeEditor() {
		if (requestForm.getParent() == mainContent) {
			mainContent.removeComponent(requestForm);
		} else {
			AppUI.get().getContentLayout().replaceComponent(requestForm, this);
		}
	}

	void saveCustomer(@Observes @RequestEvent(RequestEvent.Type.EDIT) Request request) {
		listRequests(accessControl.getPrincipalName());
		closeEditor();
	}


	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		System.out.println("Entered request-list view");
		AppUI.getMenu().setVisible(true);
		if (event.getParameters().contains("?request_id=")) {
			List<Request> foundRequests = requestService.findByID(Integer.parseInt(event.getParameters().split("=")[1]));
			if (!foundRequests.isEmpty() && foundRequests.get(0).getSubmitterUserName().equals(accessControl.getPrincipalName())) {
				editRequest(foundRequests.get(0));
			}
			else {
				Notification.show("Request not found", "The request you were trying to open is inaccessible to you",
						Notification.Type.WARNING_MESSAGE);
				AppUI.getMenu().navigateTo("login");
			}

		}
		if (!accessControl.isUserSignedIn()) {
			AppUI.getMenu().navigateTo("");
			Notification.show("User not signed", "Please sign in", Notification.Type.TRAY_NOTIFICATION);
		}
	}
}
