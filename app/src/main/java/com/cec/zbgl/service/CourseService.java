package com.cec.zbgl.service;

import com.cec.zbgl.model.DeviceCourse;

import org.litepal.LitePal;

import java.util.List;

public class CourseService {


    public boolean insert(DeviceCourse course){
        return course.save();
    }

    public List<DeviceCourse> loadList() {
        List<DeviceCourse> course = LitePal.findAll(DeviceCourse.class);
        return course;
    }

    public List<DeviceCourse> loadByDid(String deviceId, int type){
        List<DeviceCourse> courses = LitePal.where("deviceid = ? and coursetype = ? " +
                "and isvalid = ?",deviceId, String.valueOf(type),"1").find(DeviceCourse.class);
        return courses;
    }

    public List<DeviceCourse> loadBySysid(String sysId, int type){
        List<DeviceCourse> courses = LitePal.where("sysid = ? and coursetype = ? " +
                "and isvalid = ?",sysId, String.valueOf(type),"1").find(DeviceCourse.class);
        return courses;
    }

    public int update(DeviceCourse course) {
        return course.update(course.getId());
    }

    public DeviceCourse getDevice(long id) {
        return LitePal.find(DeviceCourse.class, id);
    }

    public int delete(long id) {
        return LitePal.delete(DeviceCourse.class, id);
    }

}
