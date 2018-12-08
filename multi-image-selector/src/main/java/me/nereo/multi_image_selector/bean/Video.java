package me.nereo.multi_image_selector.bean;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.Serializable;

public class Video implements Serializable {
    private int id;
    public String path;
    public String name;
    public long time;
    public Bitmap bitmap;
    private String thumbPath;
    private boolean isUpload;



    public Video() {
    }

    public Video(int id, String path, String name) {
        this.id = id;
        this.path = path;
        this.name = name;
    }

    public Video(int id, String path, String name, Bitmap bitmap) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.bitmap = bitmap;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof Video) {
          Video mVideo = (Video) o;
          if (((Video) o).getName() != null) {
              return this.id == mVideo.getId()
                      && this.name == mVideo.name
                      && this.time == mVideo.getTime()
                      && this.bitmap == mVideo.bitmap
                      && this.thumbPath == mVideo.thumbPath;
          }
          if (((Video) o).getPath() != null) {

          }
          if (((Video) o).getThumbPath() != null) {

          }
          return this.id == mVideo.getId()
                  && this.name.equals(mVideo.name)
                  && this.time == mVideo.getTime()
                  && this.bitmap == mVideo.bitmap
                  && this.thumbPath.equals(mVideo.thumbPath);
      }
        return super.equals(o);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }
}
