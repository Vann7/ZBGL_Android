package com.cec.zbgl.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/** 备品备件_设备教程 */
public class DeviceCourse extends LitePalSupport implements Serializable {
    public static final int TYPE_ONE = 1;
    public static final int TYPE_TWO = 2;
    public static final int TYPE_THREE = 3;
    public static final int TYPE_ITEM = 101;


    private long id;
//    @Column(unique = true, nullable = false)
    private String mId;
    private String deviceId;
    private String sysId;
    private String name;
    private int deviceType;
    private int courseType;
    private String createrId;
    private String createrName;
    private Date createTime;
    private String location;
    private String description;
    private boolean isValid;
    private byte[] image; //压缩图片
    private byte[] image_full;  //原尺寸图片
    @Column(ignore = true)
    private String message;   //当为标题时显示标题信息(不存数据库)
    @Column(ignore = true)
    private boolean isTitle; //判断是否为标题(不存数据库)
    private boolean isUpload; //是否已上传服务器
    private boolean isEdited; //是否已编辑


    public DeviceCourse() {
    }

    public DeviceCourse(String mId, String name, int courseType, String description,boolean isTitle,String message) {
        this.mId = mId;
        this.name = name;
        this.courseType = courseType;
        this.description = description;
        this.isTitle = isTitle;
        this.message = message;
        this.deviceType = deviceType;
    }

    public DeviceCourse(String mId, String name, int courseType, String description) {
        this.mId = mId;
        this.name = name;
        this.courseType = courseType;
        this.description = description;
        this.deviceType = deviceType;
    }


    public DeviceCourse(boolean isTitle, String message,int type) {
        this.message = message;
        this.isTitle = isTitle;
        this.courseType = type;
    }

    @Override
    public String toString() {
        return "DeviceCourse{" +
                "id=" + id +
                ", mId='" + mId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", sysId='" + sysId + '\'' +
                ", name='" + name + '\'' +
                ", deviceType=" + deviceType +
                ", courseType=" + courseType +
                ", createrId='" + createrId + '\'' +
                ", createrName='" + createrName + '\'' +
                ", createTime=" + createTime +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", isValid=" + isValid +
                ", image=" + Arrays.toString(image) +
                ", image_full=" + Arrays.toString(image_full) +
                ", message='" + message + '\'' +
                ", isTitle=" + isTitle +
                ", isUpload=" + isUpload +
                ", isEdited=" + isEdited +
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getImage_full() {
        return image_full;
    }

    public void setImage_full(byte[] image_full) {
        this.image_full = image_full;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
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
}
