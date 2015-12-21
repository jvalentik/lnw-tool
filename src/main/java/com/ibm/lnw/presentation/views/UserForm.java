package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.UserService;
import com.ibm.lnw.backend.domain.User;
import com.ibm.lnw.backend.domain.UserRole;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
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

	TextField firstName = new TextField("First name");
	TextField lastName = new TextField("Last name");
	TextField userName = new TextField("Intranet ID");
	TypedSelect<UserRole> status = new TypedSelect().withCaption("Type of access");



	@Override
	protected Component createContent() {
		setStyleName(ValoTheme.LAYOUT_CARD);
		return new MVerticalLayout(new Header("Edit user...").setHeaderLevel(3),
				new MFormLayout(firstName, lastName, userName, status).withFullWidth(), getToolbar()).withStyleName
				(ValoTheme.LAYOUT_CARD);
	}

	@PostConstruct
	void init() {
		setEagerValidation(true);
		status.setWidthUndefined();
		status.setOptions(UserRole.values());
		setSavedHandler(user -> {
				try {
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
