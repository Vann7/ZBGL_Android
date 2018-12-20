package com.cec.zbgl.service;

import com.cec.zbgl.model.ServerConfig;

import org.litepal.LitePal;

import java.util.List;

public class ServerService {

    public boolean insert(ServerConfig config) {
        boolean flag = config.save();
        return flag;
    }

    public int update(ServerConfig config) {
       int flag = config.update(1);
       return flag;
    }

    public ServerConfig load() {
        ServerConfig config = LitePal.find(ServerConfig.class, 1);
        return config;
    }

    public List<ServerConfig> loadList() {
        return LitePal.findAll(ServerConfig.class);
    }

}
