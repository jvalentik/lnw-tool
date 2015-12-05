package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.RequestService;
import com.ibm.lnw.backend.domain.Request;
import com.ibm.lnw.backend.domain.RequestStatus;
import com.ibm.lnw.backend.domain.UserRole;
import com.ibm.lnw.presentation.model.CustomAccessControl;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.fields.TypedSelect;
import org.vaadin.viritin.form.AbstractForm;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class RequestForm extends AbstractForm<Request> {

    @Inject
    RequestService service;

	@Inject
	CustomAccessControl accessControl;

	@Inject
	@RequestEvent(RequestEvent.Type.EDIT)
	javax.enterprise.event.Event<Request> editEvent;

	TextField leadingWBS = new TextField("WBS ID");
    TextField customerName = new TextField("Last name");
    TextField contractNumber = new TextField("Contract number");
    TextField services = new TextField("Services");
    TextField pmaName = new TextField("PMA name");
    TextField pexName = new TextField("PE Name");
    TextArea comments = new TextArea("Comments");
    DateField dateTimeStamp = new DateField("Submitted on: ");
    TypedSelect<RequestStatus> status = new TypedSelect().withCaption("Request status");
	Button button = new Button("OK");


    @Override
    protected Component createContent() {
        setStyleName(ValoTheme.LAYOUT_CARD);
	    VerticalLayout layout = new VerticalLayout();
	    Label label = new Label("Request details ...");
	    label.setStyleName(ValoTheme.LABEL_H2);
        layout.addComponent(label);
	    layout.setComponentAlignment(label, Alignment.TOP_LEFT);
	    FormLayout formLayout = new FormLayout(leadingWBS,
											    customerName,
											    contractNumber,
											    services,
											    pmaName,
											    pexName,
											    comments,
			                                    status,
											    dateTimeStamp);
	    if (accessControl.isUserInRole(UserRole.INITIATOR.toString())) {
		    formLayout.setEnabled(false);
	    }
	    button.addClickListener(clickEvent -> editEvent.fire(getEntity()));
	    button.setStyleName(ValoTheme.BUTTON_PRIMARY);
	    formLayout.setStyleName(ValoTheme.LAYOUT_CARD);
	    layout.addComponent(formLayout);
	    layout.setComponentAlignment(formLayout, Alignment.MIDDLE_CENTER);
	    layout.addComponent(button);
	    layout.setComponentAlignment(button, Alignment.BOTTOM_CENTER);
	    return layout;
    }

    @PostConstruct
    void init() {
        setEagerValidation(true);
        status.setWidthUndefined();
        status.setOptions(RequestStatus.values());
    }
}
