package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.AttachmentService;
import com.ibm.lnw.backend.RequestService;
import com.ibm.lnw.backend.domain.Attachment;
import com.ibm.lnw.backend.domain.Request;
import com.ibm.lnw.backend.domain.RequestStatus;
import com.ibm.lnw.presentation.model.CustomAccessControl;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MDateField;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.fields.TypedSelect;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.*;
import java.util.List;

@Dependent
public class RequestForm extends AbstractForm<Request> {
	@Inject
	CustomAccessControl accessControl;

    @Inject
    AttachmentService attachmentService;

    @Inject
    RequestService requestService;

    @Inject
    @RequestEvent(RequestEvent.Type.REFRESH)
    javax.enterprise.event.Event<Request> refreshEvent;

	@Inject
	@RequestEvent(RequestEvent.Type.SAVE)
	javax.enterprise.event.Event<Request> saveEvent;

	TextField leadingWBS = new MTextField("WBS ID");
    TextField customerName = new MTextField("Last name");
    TextField contractNumber = new MTextField("Contract number");
    TextField services = new MTextField("Services");
    TextField pmaName = new MTextField("PMA name");
    TextField pexName = new MTextField("PE Name");
    DateField dateTimeStamp = new MDateField("Submitted on: ");
    TypedSelect<RequestStatus> status = new TypedSelect().withCaption("Request status");
    Button download = new MButton("Download").withDescription("Download attachments");

    @Override
    protected Component createContent() {
        setStyleName(ValoTheme.LAYOUT_CARD);
        MFormLayout form = new MFormLayout(leadingWBS,
                customerName,
                contractNumber,
                services,
                pmaName,
                pexName,
                status,
                dateTimeStamp).withFullWidth();
        if (accessControl.isUserInRole("Initiator")) {
            form.setEnabled(false);
        }
        else {
            form.setEnabled(true);
        }
	    return new MVerticalLayout(new Header("Request").setHeaderLevel(3),
                form, download,
                getToolbar())
                .withStyleName(ValoTheme.LAYOUT_CARD);
    }

    @PostConstruct
    void init() {
        setEagerValidation(true);
        status.setWidthUndefined();
        status.setOptions(RequestStatus.values());
        //StreamResource myResource = createResource();
        //FileDownloader fileDownloader = new FileDownloader(myResource);
        //fileDownloader.extend(download);
        setSavedHandler(e -> {
            try {
                requestService.saveOrPersist(e);
                saveEvent.fire(e);
            } catch (EJBException ex) {
                Notification.show("The request was concurrently edited "
                                + "by someone else. Your changes were discarded.",
                        Notification.Type.ERROR_MESSAGE);
                refreshEvent.fire(e);
            }
        });
        setResetHandler(e -> refreshEvent.fire(e));
    }

    @Override
    protected void adjustResetButtonState() {
        getResetButton().setEnabled(true);
        if (accessControl.isUserInRole("Initiator")) {
            getSaveButton().setVisible(false);
        } else {
            getSaveButton().setVisible(true);
            getSaveButton().setEnabled(true);
        }
    }

    private StreamResource createResource() {
        final String TEMP_FILE_DIR = new File(System.getProperty("java.io.tmpdir")).getPath();
        List<Attachment> attachments = attachmentService.findAllByRequestId(getEntity().getId());


        return new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                try {
                    FileOutputStream outputStream = new FileOutputStream(TEMP_FILE_DIR +
                            attachments.get(0).getFileName());
                    outputStream.write(attachments.get(0).getFileContent());
                    outputStream.close();
                    return new FileInputStream(TEMP_FILE_DIR + attachments.get(0).getFileName());

                }
                catch (IOException ex) {
                    ex.printStackTrace();
                    System.out.println("File not found");
                    return null;
                }
            }
        }, attachments.get(0).getFileName());
    }
}
