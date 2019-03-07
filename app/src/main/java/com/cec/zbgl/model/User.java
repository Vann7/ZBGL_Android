package com.cec.zbgl.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * 用户信息
 */
public class User extends LitePalSupport implements Serializable {
    private int id;
    private String mId;
    @Column(nullable = false)
    private String name;
    private String password;
    private boolean isEdited; //是否已编辑
    private boolean appUpdate; //是否可以更新信息

    public User(){}

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public boolean isAppUpdate() {
        return appUpdate;
    }

    public void setAppUpdate(boolean appUpdate) {
        this.appUpdate = appUpdate;
    }
}
