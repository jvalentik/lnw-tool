package com.ibm.lnw.presentation.views;

/**
 * Created by Jan Valentik on 12/2/2015.
 */

import com.vaadin.cdi.UIScoped;
import com.vaadin.server.Responsive;
import com.vaadin.ui.CssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@UIScoped
public class ViewMenuLayout extends MHorizontalLayout {

	@Inject
	ViewMenu viewMenu;
	CssLayout content = new CssLayout();

	public CssLayout getMainContent() {
		return content;
	}

	@PostConstruct
	void init() {
		setSpacing(false);
		setSizeFull();
        content.setPrimaryStyleName("valo-content");
		content.addStyleName("v-scrollable");
		content.setSizeFull();

		addComponents(viewMenu, content);
		expand(content);
		addAttachListener(attachEvent -> Responsive.makeResponsive(getUI()));
	}

	public ViewMenu getViewMenu() {
		return viewMenu;
	}

}
