package com.ibm.lnw.presentation;

import com.ibm.lnw.backend.domain.User;
import com.ibm.lnw.presentation.views.ViewMenu;
import com.ibm.lnw.presentation.views.ViewMenuLayout;
import com.ibm.lnw.presentation.views.events.LoginEvent;
import com.ibm.lnw.presentation.views.events.NavigationEvent;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.cdi.CDIUI;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.UI;

import javax.inject.Inject;

@CDIUI("")
@Theme("mytheme")
@Title("LNW Tool")
@Widgetset("AppWidgetset")
@PreserveOnRefresh
public class AppUI extends UI {

    @Inject
    private javax.enterprise.event.Event<NavigationEvent> navigationEvent;

    @Inject
    @LoginEvent(LoginEvent.Type.LOGIN_SUCCEEDED)
    private javax.enterprise.event.Event<User> authenticatedUser;

    @Inject
    protected ViewMenuLayout viewMenuLayout;


    @Override
    protected void init(VaadinRequest vaadinRequest) {
        if (VaadinSession.getCurrent().getAttribute("CURRENT_USER") != null) {
            authenticatedUser.fire((User)VaadinSession.getCurrent().getAttribute("CURRENT_USER"));
        }
        else {
            AppUI.getMenu().setVisible(false);
            navigationEvent.fire(new NavigationEvent("login"));
        }

    }

    public ViewMenuLayout getViewMenuLayout() {
        return viewMenuLayout;
    }

    public CssLayout getContentLayout() {
        return viewMenuLayout.getMainContent();
    }

    public static ViewMenu getMenu() {
        return ((AppUI) UI.getCurrent()).getViewMenuLayout().getViewMenu();
    }

    public static AppUI getInstance() {
        return (AppUI) UI.getCurrent();
    }


}