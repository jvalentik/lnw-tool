package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.UserService;
import com.ibm.lnw.backend.domain.User;
import com.ibm.lnw.backend.domain.UserRole;
import com.ibm.lnw.presentation.model.MD5Hash;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.security.NoSuchAlgorithmException;

/**
 * Created by Jan Valentik on 11/25/2015.
 */
public class AddUserView extends Window implements Property.ValueChangeListener{
	private PasswordField repeatPassword;
	private BeanFieldGroup fields;
	private boolean success;
	private UserService userService;
	private Button okButton;

	public AddUserView(UserService userService) {
		success = false;
		this.userService = userService;
		init();
	}

	private void init() {
		addCloseListener((closeEvent) -> {
			if (success) {
				Notification.show("Access granted", "Your access has been created. Please login",
						Notification.Type.TRAY_NOTIFICATION);
			}
		});
		User user = new User();
		fields = new BeanFieldGroup(User.class);
		fields.setItemDataSource(user);
		FormLayout parentLayout = new FormLayout();
		parentLayout.setMargin(true);
		parentLayout.setSpacing(true);
		Field<?> firstName = fields.buildAndBind("firstName");
		firstName.setRequired(true);
		Field<?> lastName = fields.buildAndBind("lastName");
		lastName.setRequired(true);
		Field<?> userName = fields.buildAndBind("IBM Intranet ID", "userName");
		userName.setRequired(true);
		Field<?> password = (fields.buildAndBind("Password", "password", PasswordField.class));
		password.setRequired(true);
		repeatPassword = new PasswordField("Repeat password");
		repeatPassword.setRequired(true);
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setMargin(true);
		buttonLayout.setSpacing(true);
		okButton = new Button("Save");
		//okButton.setEnabled(false);
		okButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		Button cancelButton = new Button("Cancel");
		okButton.addClickListener((clickEvent) -> {
			if (!repeatPassword.getValue().equals(password.getValue())) {
				Notification.show("Passwords don't match", "Please enter the same passwords",
						Notification.Type.WARNING_MESSAGE);
			} else {
				try {
					if (userService.findByName(((TextField) userName).getValue()).isEmpty()) {
						fields.commit();
						user.setUserName(user.getUserName().toLowerCase().trim());
						user.setPassword(MD5Hash.encrypt(user.getPassword()));
						user.setUserRole(UserRole.Initiator);
						userService.saveOrPersist(user);
						success = true;
					}
					else {
						Notification.show("User already exists ", Notification.Type.WARNING_MESSAGE);
						return;

					}
				} catch (NoSuchAlgorithmException  nex) {
					 Notification.show("Saving failed", "Saving data to database faile", Notification.Type.ERROR_MESSAGE);
				}
				catch (FieldGroup.CommitException ex) {
					Notification.show("Mandatory fields not filled", "Please make sure all mandatory are filled",
							Notification.Type.WARNING_MESSAGE);
					return;
				}
				close();
			}
		});
		cancelButton.addClickListener((clickEvent) -> close());
		buttonLayout.addComponent(okButton);
		buttonLayout.addComponent(cancelButton);
		parentLayout.addComponent(firstName);
		parentLayout.addComponent(lastName);
		parentLayout.addComponent(userName);
		parentLayout.addComponent(password);
		parentLayout.addComponent(repeatPassword);
		parentLayout.addComponent(buttonLayout);
		okButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		firstName.focus();
		setCaption("Request access");
		setWidth("400px");
		center();
		setContent(parentLayout);
	}


	@Override
	public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
		if (fields.isValid()) {
			okButton.setEnabled(true);
		}
		else {
			okButton.setEnabled(false);
		}

	}
}