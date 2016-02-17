package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.UserService;
import com.ibm.lnw.backend.domain.User;
import com.ibm.lnw.presentation.model.SendGridService;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.List;

/**
 * Created by Jan Valentik on 12/5/2015.
 */
public class ResetPassword extends Window {

	public ResetPassword(UserService userService) {
		setWidth("300px");
		setHeight("200px");
		HorizontalLayout buttons = new HorizontalLayout();
		VerticalLayout parentLayout = new VerticalLayout();
		Button okButton = new Button("Submit");
		TextField userName = new TextField("Enter your IBM Intranet ID");
		userName.setRequired(true);
		userName.addValidator(new BeanValidator(User.class, "userName"));
		userName.addValueChangeListener(valueChangeEvent -> {
			if (userName.isValid()) {
				okButton.setEnabled(true);
			}
		});

		okButton.addClickListener(clickEvent -> {
			List<User> users = userService.findByUserName(userName.getValue());
			if (!users.isEmpty()) {
				try {
					SendGridService.sendResetLink(userName.getValue());
				}
				catch (Exception ex) {
					ex.printStackTrace();
					Notification.show("Failed to send email", Notification.Type.WARNING_MESSAGE);
					close();
				}
				Notification.show("Link where you can reset password has been " +
						"sent to your email", Notification.Type.TRAY_NOTIFICATION);
				close();
			}
			else {
				Notification.show("User not registered", Notification.Type.WARNING_MESSAGE);
				close();
			}

		});
		okButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		okButton.setEnabled(false);
		Button cancel = new Button("Cancel");
		cancel.addClickListener(clickEvent -> close());
		buttons.addComponent(okButton);
		buttons.addComponent(cancel);
		buttons.setMargin(true);
		buttons.setSpacing(true);
		parentLayout.addComponent(userName);
		parentLayout.setComponentAlignment(userName, Alignment.TOP_CENTER);
		parentLayout.addComponent(buttons);
		parentLayout.setMargin(true);
		parentLayout.setSpacing(true);
		parentLayout.setSizeFull();
		setContent(parentLayout);
		userName.focus();
		center();
		setModal(true);
		setCaption("Reset password");
	}
}
