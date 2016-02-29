package com.ibm.lnw.presentation;

import com.ibm.lnw.backend.UserService;
import com.ibm.lnw.backend.domain.User;
import com.ibm.lnw.presentation.model.MD5Hash;
import com.ibm.lnw.presentation.views.ResetPasswordForm;
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
import java.time.LocalDate;
import java.util.List;

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

    @Inject
    private UserService userService;


    @Override
    protected void init(VaadinRequest vaadinRequest) {
        if (vaadinRequest.getParameter("pwd_reset") != null) {
            if (vaadinRequest.getParameter("pwd_reset").split("id=")[0].equals(MD5Hash.encrypt(LocalDate.now().toString()))) {
                List<User> userList = userService.findAll();
                for (User user : userList) {
                    if (MD5Hash.encrypt(user.getUserName()).equals(vaadinRequest.getParameter("pwd_reset").split
                            ("id=")[1])) {
                        ResetPasswordForm form = new ResetPasswordForm(userService, user.getUserName());
                        AppUI.getCurrent().addWindow(form);
                        navigationEvent.fire(new NavigationEvent("login"));
                        break;
                    }
                }
            }
        }
        String requestParam = "";
        if (vaadinRequest.getParameter("request_id") != null) {
            requestParam = "/?request_id=" + vaadinRequest.getParameter("request_id");
        }

        if (VaadinSession.getCurrent().getAttribute("CURRENT_USER") != null) {
            authenticatedUser.fire((User)VaadinSession.getCurrent().getAttribute("CURRENT_USER"));
            navigationEvent.fire(new NavigationEvent("request-list" + requestParam));
        }
        else {
            AppUI.getMenu().setVisible(false);
            navigationEvent.fire(new NavigationEvent("login" + requestParam));
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