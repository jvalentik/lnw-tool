package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.UserService;
import com.ibm.lnw.backend.domain.User;
import com.ibm.lnw.presentation.AppUI;
import com.ibm.lnw.presentation.model.CustomAccessControl;
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Created by Jan Valentik on 12/21/2015.
 */
@CDIView("admin-view")
@ViewMenuItem(icon = FontAwesome.USERS, title = "Administrator", order = 3)
public class AdminView extends MVerticalLayout implements View {
	@Inject
	CustomAccessControl accessControl;

	@Inject
	UserService userService;

	@Inject
	UserForm userForm;

	MTable<User> userMTable = new MTable(User.class).withFullWidth().withFullHeight();
	MHorizontalLayout mainContent = new MHorizontalLayout(userMTable).withFullWidth().withMargin(false);
	Header header = new Header("Users").setHeaderLevel(2);

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
		/*if (!accessControl.isUserInRole("ADMINISTRATOR")) {
			Notification.show("You are not entitled to access this view", Notification.Type.WARNING_MESSAGE);
			Navigator navigator = UI.getCurrent().getNavigator();
			navigator.navigateTo("login");
		}   */

	}

	@PostConstruct
	public void init() {
		userMTable.addMValueChangeListener(mValueChangeEvent -> editUser(mValueChangeEvent.getValue()));
		layout();
		adjustTableColumns();
		UI.getCurrent().setResizeLazy(true);
		Page.getCurrent().addBrowserWindowResizeListener(browserWindowResizeEvent -> {
			adjustTableColumns();
			layout();
		});
		listUsers();
	}

	private void layout() {
		removeAllComponents();
		addComponents(new MHorizontalLayout(header).expand(header).alignAll(Alignment.MIDDLE_LEFT),
				mainContent);
		setMargin(new MarginInfo(false, true, true, true));
		expand(mainContent);
	}

	private void adjustTableColumns() {
		userMTable.setVisibleColumns(new Object[]{"firstName", "lastName", "role"});
		userMTable.setColumnHeaders(new String[]{"First name", "Last name", "Type of access"});
	}

	private void listUsers() {
		userMTable.setBeans(new ArrayList<>(userService.findAll()));
	}

	void editUser(User user) {
		if (user != null) {
			openEditor(user);
		} else {
			closeEditor();
		}
	}

	private void openEditor(User user) {
		userForm.setEntity(user);
		mainContent.addComponent(userForm);
		userForm.focusFirst();
	}

	private void closeEditor() {
		if (userForm.getParent() == mainContent) {
			mainContent.removeComponent(userForm);
		} else {
			AppUI.get().getContentLayout().replaceComponent(userForm, this);
		}
	}

	void saveCustomer(@Observes @UserEvent(UserEvent.Type.EDIT) User user) {
		listUsers();
		closeEditor();
	}
}
