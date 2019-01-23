package com.cec.zbgl.model;

import com.cec.zbgl.utils.tree.annotation.TreeNodeId;
import com.cec.zbgl.utils.tree.annotation.TreeNodeLabel;
import com.cec.zbgl.utils.tree.annotation.TreeNodePid;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Date;

/** 装备管理_（系统）组织结构  */
public class SpOrgnization extends LitePalSupport implements Serializable {
    private long id;
    private String mId;
    @TreeNodeId
    private java.lang.String code;
    @TreeNodePid
    private java.lang.String parentCode;
    private java.lang.String value;
    @TreeNodeLabel
    private java.lang.String name;
    private boolean isSys;
    private java.util.Date createTime;
    private java.lang.String createrId;
    private String description;
    private byte[] image;
    private boolean isValid;
    private boolean isUpload; //是否已上传服务器

    public SpOrgnization() {}

    public SpOrgnization(String code, String parentCode, String name) {
        this.code = code;
        this.parentCode = parentCode;
        this.name = name;
    }

    @Override
    public String toString() {
        return "SpOrgnization{" +
                "id=" + id +
                ", mId='" + mId + '\'' +
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }
}
