package com.cec.zbgl.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/** 装备管理_设备库存信息 */
public class DeviceInfo extends LitePalSupport implements Serializable {
    private long id;
//    @Column(unique = true, nullable = false)
    private String mId;
    private java.lang.String name;
    private String type;
    private java.lang.String location;
    private int count;
    private String status;
    private String belongSys;
    private java.lang.String description;
    private java.lang.String createrId;
    private java.lang.String createrName;
    private java.util.Date createTime;
    private byte[] image;
    private boolean isValid;
    private boolean isUpload; //是否已上传服务器
    private boolean isEdited; //是否已编辑

    //(与组织机构共享查询)标识
    @Column(ignore = true)
    private String code; //组织机构代码
    @Column(ignore = true)
    private int searchType; //默认为false, true为组织机构

    public DeviceInfo() {}

//    public DeviceInfo(String name, String belongSys, String location) {
//        this.name = name;
//        this.belongSys = belongSys;
//        this.location = location;
//    }

    public DeviceInfo(String name, Integer searchType) {
        this.name = name;
        this.searchType = searchType;
    }

    
    public DeviceInfo(String name, String type, String location) {
        this.name = name;
        this.type = type;
        this.location = location;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "id=" + id +
                ", mId='" + mId + '\'' +
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
                ", image=" + Arrays.toString(image) +
                ", isValid=" + isValid +
                ", isUpload=" + isUpload +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getSearchType() {
        return searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }
}
