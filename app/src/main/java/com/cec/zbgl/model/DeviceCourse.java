package com.cec.zbgl.model;

import java.util.Date;

/** 备品备件_设备教程 */
public class DeviceCourse {
    private String id;
    private String deviceId;
    private String sysId;
    private String name;
    private int deviceType;
    private int courseType;
    private String createrId;
    private String createName;
    private Date createTime;
    private String location;
    private String description;
    private boolean isValid;

    @Override
    public String toString() {
        return "DeviceCourse{" +
                "id='" + id + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", sysId='" + sysId + '\'' +
                ", name='" + name + '\'' +
                ", deviceType=" + deviceType +
                ", courseType=" + courseType +
                ", createrId='" + createrId + '\'' +
                ", createName='" + createName + '\'' +
                ", createTime=" + createTime +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", isValid=" + isValid +
                '}';
    }

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

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getCourseType() {
        return courseType;
    }

    public void setCourseType(int courseType) {
        this.courseType = courseType;
    }

    public String getCreaterId() {
        return createrId;
    }

    public void setCreaterId(String createrId) {
        this.createrId = createrId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
