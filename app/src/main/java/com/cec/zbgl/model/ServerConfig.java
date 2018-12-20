package com.cec.zbgl.model;

import org.litepal.crud.LitePalSupport;

/**
 * 服务器信息配置类
 */
public class ServerConfig extends LitePalSupport {

    private long id;
    private String ip;
    private int port;
    private String hostName;
    private int serverPort;
    private String password;

    public ServerConfig() {
    }

    public ServerConfig(String ip, int port, String hostName) {
        this.ip = ip;
        this.port = port;
        this.hostName = hostName;
        this.hostName = hostName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
