package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.UserService;
import com.ibm.lnw.backend.domain.User;
import com.ibm.lnw.backend.domain.UserRole;
import com.ibm.lnw.presentation.AppUI;
import com.ibm.lnw.presentation.model.CustomAccessControl;
import com.ibm.lnw.presentation.model.UserInfo;
import com.vaadin.cdi.CDIView;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.Navigator;
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
import java.util.List;

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
	private UserInfo currentUser;

	@Inject
	private CustomAccessControl accessControl;

	public void enter(ViewChangeListener.ViewChangeEvent event) {
		AppUI.getMenu().setVisible(false);
		params = event.getParameters();
		System.out.println("View Param: " + params);
		if (accessControl.isUserSignedIn()) {
			Notification.show("Log out", "You have been logged out", Notification.Type.TRAY_NOTIFICATION);
		}
		currentUser.setUser(null);
		username.focus();
	}

	@PostConstruct
	private void init() {
		AppUI.getMenu().setVisible(false);
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
			UI.getCurrent().addWindow(dialog);
		});
		buttons.addComponent(forgottenPwd);
		return layout;
	}

	private void login() {
		System.out.println("View param in login: " + params);
		User currentUser = new User();
		currentUser.setUserName(username.getValue().toLowerCase().trim());
		currentUser.setPassword(password.getValue());
		currentUser.setUserRole(UserRole.Initiator);
		this.currentUser.setUser(currentUser);
		List<User> userList = userService.findByUserName(currentUser.getUserName().toLowerCase());
		if (!userList.isEmpty()) {
			User tempUser = userList.get(0);
			if (currentUser.equals(tempUser)) {
				this.currentUser.setUser(tempUser);
				System.out.println("User found");
				if (params.contains("?request_id=")) {
					System.out.println("Navigating to: " + "request-list/?request_id=" + params.split("=")[1]);
					Navigator navigator = UI.getCurrent().getNavigator();
					navigator.navigateTo("request-list/?request_id=" + params.split("=")[1]);
				}
				else {
					AppUI.getMenu().navigateTo("submitter-view");
				}
			} else {
				this.currentUser.setUser(null);
				Notification notification = new Notification("Login failed", "Please check your username and password and" +
						" try again", Notification.Type.HUMANIZED_MESSAGE);
				notification.setDelayMsec(2000);
				notification.show(Page.getCurrent());
			}
		}
		else {
			this.currentUser.setUser(null);
			Notification notification = new Notification("Login failed", "Please check your username and password and" +
					" try again", Notification.Type.HUMANIZED_MESSAGE);
			notification.setDelayMsec(2000);
			notification.show(Page.getCurrent());
		}
	}

	private void requestAccess() {
		username.setValue("");
		password.setValue("");
		UI.getCurrent().addWindow(new AddUserView(userService));
	}
}

