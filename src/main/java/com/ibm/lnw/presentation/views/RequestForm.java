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
import org.vaadin.viritin.fields.MTextArea;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.fields.TypedSelect;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Dependent
public class RequestForm extends AbstractForm<Request> {
	@Inject
	CustomAccessControl accessControl;

    @Inject
    RequestService requestService;

    @Inject
    AttachmentService attachmentService;

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
    TextArea comments = new MTextArea("Comments").withFullWidth();
    TypedSelect<RequestStatus> status = new TypedSelect().withCaption("Request status");
    Button downloadButton = new MButton("Download").withDescription("Download attachments");
    MFormLayout form;
    final CustomFileDownloader downloader = new CustomFileDownloader();

    @Override
    protected Component createContent() {
        setStyleName(ValoTheme.LAYOUT_CARD);
        comments.setHeight("50px");
        form = new MFormLayout(leadingWBS,
                customerName,
                contractNumber,
                services,
                pmaName,
                pexName,
                comments,
                status,
                dateTimeStamp).withFullWidth();

	    return new MVerticalLayout(new Header("Request").setHeaderLevel(3),
                form,  new MHorizontalLayout(getToolbar(), downloadButton))
                .withStyleName(ValoTheme.LAYOUT_CARD);
    }

    @PostConstruct
    void init() {
        setEagerValidation(true);
        status.setWidthUndefined();
        status.setOptions(RequestStatus.values());
        downloader.addAdvancedDownloaderListener(downloadEvent -> {
            final String TEMP_FILE_DIR = new File(System.getProperty("java.io.tmpdir")).getPath();
            List<Attachment> attachments = attachmentService.findAllByMainRequest(getEntity().getId());
            List<String> toBeZipped = new ArrayList<>();
            attachments.forEach(attachment1 -> {
                File newAttachment = new File(TEMP_FILE_DIR + File.separator + attachment1.getFileName());
                toBeZipped.add(attachment1.getFileName());
                FileOutputStream outputStream;
                try {
                    outputStream = new FileOutputStream(newAttachment);
                    outputStream.write(attachment1.getFileContent());
                    outputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.out.println(TEMP_FILE_DIR + "/" + attachment1.getFileName() + " wasn't found");
                }
            });
            try{
                byte[] buffer = new byte[1024];
                FileOutputStream fos = new FileOutputStream(TEMP_FILE_DIR + "/" + "attachments.zip");
                ZipOutputStream zos = new ZipOutputStream(fos);
                for(String file : toBeZipped){
                    System.out.println("File Added : " + file);
                    ZipEntry ze= new ZipEntry(file);
                    zos.putNextEntry(ze);
                    FileInputStream in =
                            new FileInputStream(TEMP_FILE_DIR + File.separator + file);
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    in.close();
                }
                zos.closeEntry();
                zos.close();
                System.out.println("Done");
            }catch(IOException ex){
                ex.printStackTrace();
            }
            downloader.setFilePath(TEMP_FILE_DIR + "/" + "attachments.zip");

        });
        downloader.extend(downloadButton);

        setSavedHandler(e -> {
            try {
                e.setModifiedOn(new Date());
                e.setLastModifiedBy(accessControl.getPrincipalName());
                if (e.getStatus() == RequestStatus.Clarification || e.getStatus() == RequestStatus.Rejected) {

                }
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

    public void setEditable(boolean editable) {
        if (editable) {
            form.setEnabled(true);
        }
        else {
            form.setEnabled(false);
        }
    }



    @Override
    protected void adjustResetButtonState() {
        getResetButton().setEnabled(true);
        if (accessControl.isUserInRole("Initiator") || accessControl.isUserInRole("Viewer")) {
            getSaveButton().setVisible(false);
        } else {
            getSaveButton().setVisible(true);
            getSaveButton().setEnabled(true);
        }
    }


}
