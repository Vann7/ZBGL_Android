package me.nereo.multi_image_selector.event;

import java.util.List;

import me.nereo.multi_image_selector.bean.Video;

/**
 * 消息事件类
 */
public class VideoEvent {
    private String message;
    private List<Video> list;

    public VideoEvent(String message) {
        this.message = message;
    }
    public VideoEvent(List<Video> list) {
        this.list = list;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Video> getList() {
        return list;
    }

    public void setList(List<Video> list) {
        this.list = list;
    }
}
