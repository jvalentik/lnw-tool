package com.ibm.lnw.presentation;

import com.ibm.lnw.presentation.views.ViewMenu;
import com.ibm.lnw.presentation.views.ViewMenuLayout;
import com.ibm.lnw.presentation.views.events.NavigationEvent;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.cdi.CDIUI;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.UI;

import javax.inject.Inject;

@CDIUI("")
@Theme("mytheme")
@Title("LNW Tool")
@Widgetset("AppWidgetset")
public class AppUI extends UI {

    @Inject
    private javax.enterprise.event.Event<NavigationEvent> navigationEvent;

    @Inject
    protected ViewMenuLayout viewMenuLayout;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        AppUI.getMenu().setVisible(false);
        navigationEvent.fire(new NavigationEvent("login"));
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


}