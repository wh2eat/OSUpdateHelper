package com.idatachina.www.osupdatehelper;

/**
 * Created by wanghang on 2017/11/30.
 */

public class OsUpdateDescriber {

    private String fileName;
    private long fileSize;
    private String targetVersion;
    private String updateLog;

    public  OsUpdateDescriber()
    {

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(String targetVersion) {
        this.targetVersion = targetVersion;
    }

    public String getUpdateLog() {
        return updateLog;
    }

    public void setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
    }

    @Override
    public String toString() {
        return "OsUpdateDescriber{" +
                "fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", targetVersion='" + targetVersion + '\'' +
                ", updateLog='" + updateLog + '\'' +
                '}';
    }
}
