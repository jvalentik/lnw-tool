package com.ibm.lnw.presentation;

import com.vaadin.cdi.server.VaadinCDIServlet;
import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

/**
 * Created by Jan Valentik on 12/2/2015.
 */
@WebServlet(urlPatterns = "/*")
public class CustomCDIServlet extends VaadinCDIServlet {
	@Override
	protected void servletInitialized() throws ServletException {
		super.servletInitialized();
		getService().addSessionInitListener(sessionInitEvent ->
				sessionInitEvent.getSession().addBootstrapListener(new BootstrapListener() {
					@Override
					public void modifyBootstrapFragment(BootstrapFragmentResponse bootstrapFragmentResponse) {
						log("Warning, fragments are not supported");
					}

					@Override
					public void modifyBootstrapPage(BootstrapPageResponse bootstrapPageResponse) {
						Document d = bootstrapPageResponse.getDocument();
						Element el = d.createElement("meta");
						el.attr("name", "viewport");
						el.attr("content", "user-scalable=no,initial-scale=1.0");
						d.getElementsByTag("head").get(0).appendChild(el);
					}
				}));
	}
}
