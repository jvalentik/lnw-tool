package com.ibm.lnw.presentation.views;

import com.ibm.lnw.presentation.model.CustomAccessControl;
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.inject.Inject;

/**
 * Created by Jan Valentik on 12/21/2015.
 */
@CDIView("admin-view")
@ViewMenuItem(icon = FontAwesome.USERS, title = "Administrator", order = 3)
public class AdminView extends MVerticalLayout implements View {
	@Inject
	CustomAccessControl accessControl;

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
		if (!accessControl.isUserInRole("ADMINISTRATOR")) {
			Notification.show("You are not entitled to access this view", Notification.Type.WARNING_MESSAGE);
			Navigator navigator = UI.getCurrent().getNavigator();
			navigator.navigateTo("login");

		}

	}
}
