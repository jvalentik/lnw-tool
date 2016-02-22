package com.ibm.lnw.presentation.model;

import com.vaadin.server.*;
import com.vaadin.ui.AbstractComponent;

import java.io.*;
import java.util.logging.Logger;

/**
 * Created by Ján Valentík on 21. 2. 2016.
 */
public class CustomFileDownloader extends FileDownloader {

    /**
     *
     */
    private static final long serialVersionUID = 7914516170514586601L;
    private static final boolean DEBUG_MODE = true;

    private static final Logger logger = java.util.logging.Logger
            .getLogger(CustomFileDownloader.class.getName());

    private AbstractComponent extendedComponet;

    private AdvancedDownloaderListener dynamicDownloaderListener;
    private DownloaderEvent downloadEvent;

    public abstract class DownloaderEvent {

        /**
         *
         * @return
         */
        public abstract AbstractComponent getExtendedComponet();

        public abstract void setExtendedComponet(
                AbstractComponent extendedComponet);

    }

    public interface AdvancedDownloaderListener {
        /**
         * This method will be invoked just before the download starts. Thus, a
         * new file path can be set.
         *
         * @param downloadEvent
         */
        public void beforeDownload(DownloaderEvent downloadEvent);
    }

    public void fireEvent() {
        if (DEBUG_MODE) {
            logger.info("inside fireEvent");
        }
        if (this.dynamicDownloaderListener != null
                && this.downloadEvent != null) {
            if (DEBUG_MODE) {
                logger.info("beforeDownload is going to be invoked");
            }
            this.dynamicDownloaderListener.beforeDownload(this.downloadEvent);
        }
    }

    public void addAdvancedDownloaderListener(
            AdvancedDownloaderListener listener) {
        if (listener != null) {
            DownloaderEvent downloadEvent = new DownloaderEvent() {

                private AbstractComponent extendedComponet;

                @Override
                public void setExtendedComponet(
                        AbstractComponent extendedComponet) {
                    this.extendedComponet = extendedComponet;
                }

                @Override
                public AbstractComponent getExtendedComponet() {
                    // TODO Auto-generated method stub
                    return this.extendedComponet;
                }
            };
            downloadEvent
                    .setExtendedComponet(CustomFileDownloader.this.extendedComponet);
            this.dynamicDownloaderListener = listener;
            this.downloadEvent = downloadEvent;

        }
    }

    private static class FileResourceUtil {

        private String filePath;

        private String fileName = "";

        private File file;

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
            file = new File(filePath);

            if (file.exists() && !file.isDirectory()) {
                fileName = file.getName();
            }
        }

        /**
         * makes a stream resource
         *
         * @return {@code StreamResource}
         */
        @SuppressWarnings("serial")
        public StreamResource getResource() {
            return new StreamResource(new StreamResource.StreamSource() {

                @Override
                public InputStream getStream() {

                    if (filePath != null && file != null) {

                        if (file.exists() && !file.isDirectory()) {
                            try {
                                return new FileInputStream(file);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                return null;
                            }
                        } else {
                            return null;
                        }

                    }
                    return null;
                }

            }, FileResourceUtil.this.fileName) {
                @Override
                public String getFilename() {
                    return FileResourceUtil.this.fileName;
                }

            };
        }

    }

    private FileResourceUtil resource;

    private CustomFileDownloader(FileResourceUtil resource) {

        super(resource == null ? (resource = new FileResourceUtil())
                .getResource() : resource.getResource());

        CustomFileDownloader.this.resource = resource;
        System.out.println("created a new instance of resource : " + resource);
    }

    public CustomFileDownloader() {
        this(null);
    }

    /**
     * @return the current file path
     */
    public String getFilePath() {
        return resource.getFilePath();
    }

    /**
     * sets the path for the file for downloading
     *
     * @param filePath
     *            path of the file, i.e. path + file name with extension
     */
    public void setFilePath(String filePath) {

        if (resource != null && filePath != null) {
            this.resource.setFilePath(filePath);

        }
    }

    @Override
    public boolean handleConnectorRequest(VaadinRequest request,
                                          VaadinResponse response, String path) throws IOException {


        if (!path.matches("dl(/.*)?")) {
            // Ignore if it isn't for us
            return false;
        }
        VaadinSession session = getSession();

        session.lock();
        CustomFileDownloader.this.fireEvent();

        DownloadStream stream;

        try {
            Resource resource = getFileDownloadResource();
            if (!(resource instanceof ConnectorResource)) {
                return false;
            }
            stream = ((ConnectorResource) resource).getStream();

            if (stream.getParameter("Content-Disposition") == null) {
                // Content-Disposition: attachment generally forces download
                stream.setParameter("Content-Disposition",
                        "attachment; filename=\"" + stream.getFileName() + "\"");
            }

            // Content-Type to block eager browser plug-ins from hijacking
            // the file
            if (isOverrideContentType()) {
                stream.setContentType("application/octet-stream;charset=UTF-8");
            }

        } finally {
            session.unlock();
        }
        stream.writeResponse(request, response);
        return true;
    }
}
