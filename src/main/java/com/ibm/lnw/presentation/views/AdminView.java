package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.UserService;
import com.ibm.lnw.backend.domain.User;
import com.ibm.lnw.presentation.AppUI;
import com.ibm.lnw.presentation.model.CustomAccessControl;
import com.ibm.lnw.presentation.views.events.UserEvent;
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Created by Jan Valentik on 12/21/2015.
 */
@CDIView("admin-view")
@RolesAllowed(value = {"Administrator"})
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
    TextField filter = new TextField();
    Button addButton = new MButton(FontAwesome.EDIT, clickEvent -> addUser());

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

	}

	@PostConstruct
	public void init() {
        System.out.println("AdminView init");
		userMTable.addMValueChangeListener(mValueChangeEvent -> editUser(mValueChangeEvent.getValue()));
        filter.setInputPrompt("Filter users...");
        filter.addTextChangeListener(textChangeEvent -> listUsers(textChangeEvent.getText()));
		layout();
		adjustTableColumns();
		listUsers();
	}

	private void layout() {
		removeAllComponents();
		addComponents(new MHorizontalLayout(header, filter, addButton).expand(header).alignAll(Alignment.MIDDLE_LEFT),
				mainContent);
		setMargin(new MarginInfo(false, true, true, true));
		expand(mainContent);
	}

	private void adjustTableColumns() {
		userMTable.setVisibleColumns(new Object[]{"firstName", "lastName", "userRole"});
		userMTable.setColumnHeaders(new String[]{"First name", "Last name", "Type of access"});
	}

	private void listUsers() {
		userMTable.setBeans(new ArrayList<>(userService.findAll()));
	}

    private void listUsers(String filterString) {
        userMTable.setBeans(new ArrayList<>(userService.findByName(filterString)));


	}

	void editUser(User user) {
		if (user != null) {
            System.out.println("Editing user: " + user);
			openEditor(user);
		} else {
			closeEditor();
		}
	}

    void addUser() {
        openEditor(new User());
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
			AppUI.getInstance().getContentLayout().replaceComponent(userForm, this);
		}
	}

	void saveUser(@Observes @UserEvent(UserEvent.Type.SAVE) User user) {
		listUsers();
		closeEditor();
	}

    void resetUser(@Observes @UserEvent(UserEvent.Type.REFRESH) User user) {
        listUsers();
        closeEditor();
    }

    void deleteUser(@Observes @UserEvent(UserEvent.Type.DELETE) User user) {
        closeEditor();
        listUsers();
    }
}
