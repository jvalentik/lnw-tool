package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.UserService;
import com.ibm.lnw.backend.domain.User;
import com.ibm.lnw.backend.domain.UserRole;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.fields.MPasswordField;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.fields.TypedSelect;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * Created by Jan Valentik on 12/21/2015.
 */
@Dependent
public class UserForm extends AbstractForm<User> {
	@Inject
	UserService userService;

	@Inject
	@UserEvent(UserEvent.Type.SAVE)
	javax.enterprise.event.Event<User> saveEvent;

	@Inject
	@UserEvent(UserEvent.Type.REFRESH)
	javax.enterprise.event.Event<User> refreshEvent;

	@Inject
	@UserEvent(UserEvent.Type.DELETE)
	javax.enterprise.event.Event<User> deleteEvent;

	TextField firstName = new MTextField("First name");
	TextField lastName = new MTextField("Last name");
	TextField userName = new MTextField("Intranet ID");
    PasswordField password = new MPasswordField("Password");
	TypedSelect<UserRole> userRole = new TypedSelect().withCaption("Type of access");
    private String initialPassword;



	@Override
	protected Component createContent() {
		setStyleName(ValoTheme.LAYOUT_CARD);
		return new MVerticalLayout(new Header("Edit user...").setHeaderLevel(3),
				new MFormLayout(firstName, lastName, userName, password, userRole).withFullWidth(), getToolbar())
                .withStyleName
				(ValoTheme.LAYOUT_CARD);
	}

	@PostConstruct
	void init() {
        initialPassword = getEntity().getPassword();
		setEagerValidation(true);
		userRole.setWidthUndefined();
		userRole.setOptions(UserRole.values());
		setSavedHandler(user -> {
				try {
                    /*if (!MD5Hash.encrypt(password.getValue()).equals(getEntity().getPassword())) {
                        user.setPassword(MD5Hash.encrypt(password.getValue()));
                    }*/
                    userService.saveOrPersist(user);
					saveEvent.fire(user);
				}
				catch (EJBException ex) {
					Notification.show("The user was edited at the same time by someone else. Your changes were " +
							"discarded", Notification.Type.WARNING_MESSAGE);
					refreshEvent.fire(user);
				}


		});
		setResetHandler(user -> refreshEvent.fire(user));
		setDeleteHandler(user -> {
			userService.deleteEntity(user);
			deleteEvent.fire(user);
		});
	}

	@Override
	protected void adjustResetButtonState() {
		getResetButton().setEnabled(true);
		getDeleteButton().setEnabled(getEntity() != null && getEntity().isPersisted());
	}
}
