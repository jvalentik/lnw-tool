package com.ibm.lnw.presentation;

import com.ibm.lnw.presentation.views.ViewMenu;
import com.ibm.lnw.presentation.views.ViewMenuLayout;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import javax.inject.Inject;

@CDIUI("")
@Theme("mytheme")
@Title("LNW Tool")
@Widgetset("AppWidgetset")
public class AppUI extends UI {

    @Inject
    protected CDIViewProvider viewProvider;

    @Inject
    protected ViewMenuLayout viewMenuLayout;

    @Override
    protected void init(VaadinRequest request) {
        System.out.println("Attribute: " + request.getAttribute("request_id"));
	    System.out.println("Parameter" + request.getParameter("request_id"));
        Navigator navigator = new Navigator(this, viewMenuLayout.
                getMainContent()) {

            @Override
            public void navigateTo(String navigationState) {
                try {
                    super.navigateTo(navigationState);
                } catch (Exception e) {
                    handleNavigationError(navigationState, e);
                }
            }

        };
        navigator.addProvider(viewProvider);
        setContent(viewMenuLayout);
        if (request.getParameter("request_id") == null) {
	        System.out.println("Navigating to login: no param");
	        navigator.navigateTo("login");
        }
	    else {
	        System.out.println("Navigating to login: param " + request.getParameter("request_id"));
	        navigator.navigateTo("login/?request_id=" + request.getParameter("request_id"));
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

    public static AppUI get() {
        return (AppUI) UI.getCurrent();

    }

    protected void handleNavigationError(String navigationState, Exception e) {
        Notification.show(
                "The requested view (" + navigationState + ") was not available, "
                        + "entering default screen.", Notification.Type.WARNING_MESSAGE);
        if (navigationState != null && !navigationState.isEmpty()) {
            getNavigator().navigateTo("");
        }
        getSession().getErrorHandler().error(new com.vaadin.server.ErrorEvent(e));
    }


}