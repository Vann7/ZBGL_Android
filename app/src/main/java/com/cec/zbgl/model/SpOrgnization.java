package com.cec.zbgl.model;

import java.util.Date;

/** 装备管理_（系统）组织结构  */
public class SpOrgnization {
    private java.lang.String id;
    private java.lang.String code;
    private java.lang.String parentCode;
    private java.lang.String value;
    private java.lang.String name;
    private boolean isSys;
    private java.util.Date createTime;
    private java.lang.String createrId;
    private boolean isValid;

    @Override
    public String toString() {
        return "SpOrgnization{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", parentCode='" + parentCode + '\'' +
                ", value='" + value + '\'' +
                ", name='" + name + '\'' +
                ", isSys=" + isSys +
                ", createTime=" + createTime +
                ", createrId='" + createrId + '\'' +
                ", isValid=" + isValid +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSys() {
        return isSys;
    }

    public void setSys(boolean sys) {
        isSys = sys;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreaterId() {
        return createrId;
    }

    public void setCreaterId(String createrId) {
        this.createrId = createrId;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
