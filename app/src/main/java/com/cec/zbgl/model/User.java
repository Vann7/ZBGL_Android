package com.cec.zbgl.model;

import java.io.Serializable;

/**
 * 用户信息
 */
public class User implements Serializable {
    private String name;
    private String password;

    public User(){}

    public User(String name, String password) {
        this.name = name;
        this.password = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
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
}
