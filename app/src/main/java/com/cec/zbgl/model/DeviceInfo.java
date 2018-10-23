package com.cec.zbgl.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Date;

/** 装备管理_设备库存信息 */
public class DeviceInfo extends LitePalSupport implements Serializable {
    @Column(nullable = false)
    private java.lang.String id;
    private java.lang.String name;
    private int type;
    private java.lang.String location;
    private int count;
    private int status;
    private String belongSys;
    private java.lang.String description;
    private java.lang.String createrId;
    private java.lang.String createrName;
    private java.util.Date createTime;
    private boolean isValid;

    public DeviceInfo() {
    }

    public DeviceInfo(String name, String belongSys, String location) {
        this.name = name;
        this.belongSys = belongSys;
        this.location = location;
    }

    
    public DeviceInfo(String name, int type, String location) {
        this.name = name;
        this.type = type;
        this.location = location;
    }



    @Override
    public String toString() {
        return "DeviceInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", location='" + location + '\'' +
                ", count=" + count +
                ", status=" + status +
                ", belongSys='" + belongSys + '\'' +
                ", description='" + description + '\'' +
                ", createrId='" + createrId + '\'' +
                ", createrName='" + createrName + '\'' +
                ", createTime=" + createTime +
                ", isValid=" + isValid +
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBelongSys() {
        return belongSys;
    }

    public void setBelongSys(String belongSys) {
        this.belongSys = belongSys;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreaterId() {
        return createrId;
    }

    public void setCreaterId(String createrId) {
        this.createrId = createrId;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
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
