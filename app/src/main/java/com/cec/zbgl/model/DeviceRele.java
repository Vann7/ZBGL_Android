package com.cec.zbgl.model;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 设备关联
 */
public class DeviceRele extends LitePalSupport implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String mId;
    private String deviceId;
    private String releDeviceId;
    private Date createTime;
    private boolean isValid;
    private boolean isUpload;

    public DeviceRele(){
    }

    public DeviceRele(String deviceId, String releDeviceId) {
        this.deviceId = deviceId;
        this.releDeviceId = releDeviceId;
        this.mId = UUID.randomUUID().toString();
        this.createTime = new Date();
        this.isValid = true;
        this.isUpload = true;
    }

    public DeviceRele(Long id, String mId, String releDeviceId, String deviceId) {
        this.id = id;
        this.mId = mId;
        this.releDeviceId = deviceId;
        this.deviceId = releDeviceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
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

    public Date getCreateTime() {
        return createTime;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;

    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    public void setReleDeviceId(String releDeviceId) {
        this.releDeviceId = releDeviceId;
    }
}
