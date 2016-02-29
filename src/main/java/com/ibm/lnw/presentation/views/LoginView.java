package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.UserService;
import com.ibm.lnw.backend.domain.User;
import com.ibm.lnw.presentation.AppUI;
import com.ibm.lnw.presentation.model.CustomAccessControl;
import com.ibm.lnw.presentation.model.UserInfo;
import com.ibm.lnw.presentation.views.events.LoginEvent;
import com.ibm.lnw.presentation.views.events.NavigationEvent;
import com.vaadin.cdi.CDIView;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.teemu.VaadinIcons;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created by Jan Valentik on 11/20/2015.
 */
@CDIView(value = "login", supportsParameters = true)
@ViewMenuItem(icon = FontAwesome.SIGN_IN, title = "Log in / Log out", order = ViewMenuItem.END)
public class LoginView extends CustomComponent implements View {
	private TextField username;
	private PasswordField password;
	private Button login;
	private String params;

	@Inject
	private UserService userService;

	@Inject
	private CustomAccessControl accessControl;

    @Inject
    private UserInfo userInfo;

	@Inject
	@LoginEvent(LoginEvent.Type.LOGIN_SUCCEEDED)
	private javax.enterprise.event.Event<User> authenticatedUser;

    @Inject
    private javax.enterprise.event.Event<NavigationEvent> navigationEvent;

	public void enter(ViewChangeListener.ViewChangeEvent event) {
		params = event.getParameters();
		System.out.println("View Param: " + params);
		if (accessControl.isUserSignedIn()) {
            AppUI.getMenu().setVisible(false);
			Notification.show("Log out", "You have been logged out", Notification.Type.TRAY_NOTIFICATION);
            userInfo.setUser(null);
            AppUI.getInstance().getSession().setAttribute("CURRENT_USER", null);
		}
		username.focus();
	}

	@PostConstruct
	private void init() {
		username = new TextField();
		password = new PasswordField();
		login = new Button();
		login.addClickListener(clickEvent -> login());
		addStyleName("login-screen");
		CssLayout parentLayout = new CssLayout();
		parentLayout.addStyleName("login-screen");
		Component loginForm = buildLoginForm();
		VerticalLayout centeringLayout = new VerticalLayout();
		centeringLayout.setStyleName("centering-layout");
		centeringLayout.addComponent(loginForm);
		centeringLayout.setComponentAlignment(loginForm,
				Alignment.MIDDLE_CENTER);

		// information text about logging in
		CssLayout loginInformation = buildLoginInformation();

		parentLayout.addComponent(centeringLayout);
		parentLayout.addComponent(loginInformation);
		loginInformation.setSizeUndefined();
		centeringLayout.setSizeUndefined();
		parentLayout.setSizeUndefined();
		Responsive.makeResponsive(parentLayout);
		setCompositionRoot(parentLayout);
	}

	private CssLayout buildLoginInformation() {
		CssLayout loginInformation = new CssLayout();
		loginInformation.setStyleName("login-information");
		Label loginInfoText = new Label("<h1>Welcome</h1>"
				+ "Please sign-in. If you don't have access, click at the link bellow to request it",
				ContentMode.HTML);
		loginInformation.addComponent(loginInfoText);
		Button requestAccess = new Button("Request access");
		requestAccess.addStyleName(ValoTheme.BUTTON_LINK);
		requestAccess.addClickListener((clickEvent) -> requestAccess());
		loginInformation.addComponent(requestAccess);
		return loginInformation;
	}

	private FormLayout buildLoginForm() {
		FormLayout layout = new FormLayout();
		layout.addStyleName("login-form");
		layout.setMargin(true);
		layout.addComponent(username);
		username.setCaption("IBM Intranet ID");
		username.setWidth(15, Unit.EM);
		username.setDescription("Enter your email");
		username.setIcon(VaadinIcons.USER);
		layout.addComponent(password);
		password.setCaption("Password");
		password.setWidth(15, Unit.EM);
		password.setDescription("Enter your password");
		password.setIcon(VaadinIcons.KEY);
		CssLayout buttons = new CssLayout();
		buttons.setStyleName("buttons");
		layout.addComponent(buttons);
		buttons.addComponent(login);
		login.setCaption("Login");
		login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		login.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		Button forgottenPwd = new Button("Forgot passsword?");
		forgottenPwd.setDescription("Click to reset your password");
		forgottenPwd.addStyleName(ValoTheme.BUTTON_LINK);
		forgottenPwd.addClickListener(clickEvent ->  {
			ResetPassword dialog = new ResetPassword(userService);
			AppUI.getCurrent().addWindow(dialog);
		});
		buttons.addComponent(forgottenPwd);
		return layout;
	}

	private void login() {
		User foundUser = userService.findByUserName(username.getValue().toLowerCase().trim());
		User newUser = new User();
        newUser.setUserName(username.getValue().trim());
        newUser.setPassword(password.getValue().trim());
        if (foundUser != null) {
			if (foundUser.equals(newUser)) {
                authenticatedUser.fire(foundUser);
                String requestParam = "";
                if (params.contains("?request_id=")) {
                    requestParam = "/?request_id=" + params.split("=")[1];
                }
                navigationEvent.fire(new NavigationEvent("request-list" + requestParam));

			} else {
				Notification notification = new Notification("Login failed", "Please check your username and password and" +
						" try again", Notification.Type.HUMANIZED_MESSAGE);
				notification.setDelayMsec(2000);
				notification.show(Page.getCurrent());
			}
		}
		else {
			Notification notification = new Notification("Login failed", "Please check your username and password and" +
					" try again", Notification.Type.HUMANIZED_MESSAGE);
			notification.setDelayMsec(2000);
			notification.show(Page.getCurrent());
		}
	}

	private void requestAccess() {
		username.setValue("");
		password.setValue("");
		AppUI.getCurrent().addWindow(new AddUserView(userService));
	}
}

