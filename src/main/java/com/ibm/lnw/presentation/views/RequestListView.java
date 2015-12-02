package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.RequestService;
import com.ibm.lnw.backend.domain.Request;
import com.ibm.lnw.presentation.AppUI;
import com.ibm.lnw.presentation.model.CustomAccessControl;
import com.ibm.lnw.presentation.ScreenSize;
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;

/**
 * A view that lists Customers in a Table and lets user to choose one for
 * editing. There is also RIA features like on the fly filtering.
 */
@CDIView("request-list")
@ViewMenuItem(icon = FontAwesome.LIST, title = "My requests", order = 2)
public class RequestListView extends MVerticalLayout implements View {

	@Inject
	private RequestService service;

	@Inject
	private CustomAccessControl accessControl;

	@Inject
	private RequestForm requestForm;

	// Introduce and configure some UI components used on this view
	MTable<Request> requestMTable = new MTable(Request.class).withFullWidth().
			withFullHeight();

	MHorizontalLayout mainContent = new MHorizontalLayout(requestMTable).
			withFullWidth().withMargin(false);

	TextField filter = new TextField();

	Header header = new Header("Customers").setHeaderLevel(2);

	Button addButton = new MButton(FontAwesome.EDIT, (clickEvent) -> addRequest());

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
					new MHorizontalLayout(header, filter, addButton)
							.expand(header)
							.alignAll(Alignment.MIDDLE_LEFT),
					mainContent
			);
			filter.setSizeUndefined();
		} else {
			addComponents(
					header,
					new MHorizontalLayout(filter, addButton)
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
			requestMTable.setBeans(new ArrayList<>(service.findAll()));
		}
		else if (filter.length == 1) {
			requestMTable.setBeans(new ArrayList<>(service.findAllByUser(filter[0])));
		}
		else {
			requestMTable.setBeans(new ArrayList<>(service.findAllByUserAndFilter(filter[0], filter[1])));
		}
	}

	void editRequest(Request request) {
		if (request != null) {
			openEditor(request);
		} else {
			closeEditor();
		}
	}

	void addRequest() {
		openEditor(new Request());
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
		if (!accessControl.isUserSignedIn()) {
			AppUI.getMenu().navigateTo("");
			Notification.show("User not signed", "Please sign in", Notification.Type.TRAY_NOTIFICATION);
		}
	}
}
