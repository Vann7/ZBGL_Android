package com.cec.zbgl.model;

import org.apache.commons.net.ftp.FTPFile;

public class FileFtp extends FTPFile {

    private boolean isDownload;

    public FileFtp() {
        super();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileFtp) {
            FTPFile file = (FTPFile) obj;
            if (((FileFtp) obj).getName() != null) {
                return this.getName() == file.getName();
            }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }
}
