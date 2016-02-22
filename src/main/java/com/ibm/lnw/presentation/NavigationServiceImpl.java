package com.ibm.lnw.presentation;

import com.ibm.lnw.backend.domain.User;
import com.ibm.lnw.presentation.model.CustomAccessControl;
import com.ibm.lnw.presentation.views.events.LoginEvent;
import com.ibm.lnw.presentation.views.events.NavigationEvent;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.cdi.NormalUIScoped;
import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Notification;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Created by Ján Valentík on 21. 2. 2016.
 */
@NormalUIScoped
public class NavigationServiceImpl implements NavigationService {

    @Inject
    private CDIViewProvider viewProvider;


    @Inject
    private CustomAccessControl accessControl;

    @Inject
    private AppUI ui;

    @Inject
    private javax.enterprise.event.Event<NavigationEvent> navigationEvent;

    @PostConstruct
    public void initialize() {
        if (ui.getNavigator() == null) {
            Navigator navigator = new Navigator(ui, ui);
            navigator.addProvider(viewProvider);
        }
    }

    public void onNavigationEvent(@Observes NavigationEvent event) {
        try {
            ui.getNavigator().navigateTo(event.getNavigateTo());
        } catch (Exception e) {
            handleNavigationError();
        }
    }

    private void handleNavigationError() {
        Notification.show("Unfortunatelly, you are not authorized to perform this action", Notification.Type.WARNING_MESSAGE);
    }

    @Override
    public void onSuccessfulLoginEvent(@Observes @LoginEvent(LoginEvent.Type.LOGIN_SUCCEEDED)User user) {
        accessControl.getUserInfo().setUser(user);
        Navigator navigator = new Navigator(ui, ui.viewMenuLayout.getMainContent());
        navigator.addProvider(viewProvider);
        AppUI.getCurrent().setContent(ui.getViewMenuLayout());
        AppUI.getMenu().setVisible(true);
        navigationEvent.fire(new NavigationEvent("submitter-view"));
    }
}
