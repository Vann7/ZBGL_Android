package com.cec.zbgl.model;

import java.io.Serializable;

/** 终端pad信息  */
public class Pad implements Serializable {
    private java.lang.String id;
    private java.lang.String name;
    private java.lang.String code;
    private java.lang.String userId;
    private java.lang.String userName;
    private java.lang.String departmentId;
    private java.lang.String deviceMsg;
    private boolean isValid;
    private int version;

    @Override
    public String toString() {
        return "Pad{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", departmentId='" + departmentId + '\'' +
                ", deviceMsg='" + deviceMsg + '\'' +
                ", isValid=" + isValid +
                ", version=" + version +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDeviceMsg() {
        return deviceMsg;
    }

    public void setDeviceMsg(String deviceMsg) {
        this.deviceMsg = deviceMsg;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
