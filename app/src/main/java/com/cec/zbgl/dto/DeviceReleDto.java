package com.cec.zbgl.dto;

import java.util.Date;

public class DeviceReleDto {
    private String id;
    private String deviceId;
    private String releDeviceId;
    private Date createTime;
    private boolean isValid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getReleDeviceId() {
        return releDeviceId;
    }

    public void setReleDeviceId(String releDeviceId) {
        this.releDeviceId = releDeviceId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
