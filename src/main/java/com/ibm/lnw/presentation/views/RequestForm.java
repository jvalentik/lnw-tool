package com.ibm.lnw.presentation.views;

import com.ibm.lnw.backend.AttachmentService;
import com.ibm.lnw.backend.RequestService;
import com.ibm.lnw.backend.domain.Attachment;
import com.ibm.lnw.backend.domain.Request;
import com.ibm.lnw.backend.domain.RequestStatus;
import com.ibm.lnw.presentation.model.CustomAccessControl;
import com.ibm.lnw.presentation.model.CustomFileDownloader;
import com.ibm.lnw.presentation.views.events.RequestEvent;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Set;

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
    TextField contractNumber = new MTextField("Contract number (OCPS)");
    TextField services = new MTextField("Services Contract Number");
    TextField pmaName = new MTextField("PMA name");
    TextField pexName = new MTextField("PE Name");
    DateField dateTimeStamp = new MDateField("Submitted on: ");
    TypedSelect<RequestStatus> status = new TypedSelect().withCaption("Request status");
    Button downloadButton = new MButton("Download").withDescription("Download attachments");
    MFormLayout form;
    final CustomFileDownloader downloader = new CustomFileDownloader();

    @Override
    protected Component createContent() {
        setStyleName(ValoTheme.LAYOUT_CARD);
        form = new MFormLayout(leadingWBS,
                customerName,
                contractNumber,
                services,
                pmaName,
                pexName,
                status,
                dateTimeStamp).withFullWidth();
        adjustFormState();
	    return new MVerticalLayout(new Header("Request").setHeaderLevel(3),
                form, downloadButton,
                getToolbar())
                .withStyleName(ValoTheme.LAYOUT_CARD);
    }

    @PostConstruct
    void init() {
        setEagerValidation(true);
        status.setWidthUndefined();
        status.setOptions(RequestStatus.values());
        downloader.addAdvancedDownloaderListener(downloadEvent -> {
            final String TEMP_FILE_DIR = new File(System.getProperty("java.io.tmpdir")).getPath();
            Set<Attachment> attachments = getEntity().getAttachmentSet();
            Attachment attachment = attachments.iterator().next();
            File newAttachment = new File(TEMP_FILE_DIR + "/" + attachment.getFileName());
            FileOutputStream outputStream;
            try {
                outputStream = new FileOutputStream(newAttachment);
                outputStream.write(attachment.getFileContent());
                downloader.setFilePath(TEMP_FILE_DIR + "/" + attachment.getFileName());
            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println(TEMP_FILE_DIR + "/" + attachment.getFileName() + " wasn't found");
            }
            System.out.println("Starting download by button ");
        });
        downloader.extend(downloadButton);

        setSavedHandler(e -> {
            try {
                e.setModifiedOn(new Date());
                e.setLastModifiedBy(accessControl.getPrincipalName());
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
        if (accessControl.isUserInRole("Initiator") && getEntity().getStatus() != RequestStatus.Clarification) {
            getSaveButton().setVisible(false);
        } else {
            getSaveButton().setVisible(true);
            getSaveButton().setEnabled(true);
        }
    }

    private void adjustFormState() {
        switch (accessControl.getUserInfo().getUser().getUserRole()) {
            case Initiator:
                if (getEntity().getStatus() == RequestStatus.Clarification) {
                    form.setEnabled(true);
                } else {
                    form.setEnabled(false);
                }
                break;
            case Viewer:
                form.setEnabled(false);
                break;
            default:
                form.setEnabled(true);
        }

    }



    /*private StreamResource createResource() {
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
    }*/
}
