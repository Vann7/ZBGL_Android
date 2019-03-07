package com.cec.zbgl.dto;

public class UserDto {

    private String id;
    private String name;
    private String password;
    private boolean appUpdate;

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAppUpdate() {
        return appUpdate;
    }

    public void setAppUpdate(boolean appUpdate) {
        this.appUpdate = appUpdate;
    }
}
