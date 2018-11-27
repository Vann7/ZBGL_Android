package com.cec.zbgl.service;

import android.content.ContentValues;

import com.cec.zbgl.dto.CourseDto;
import com.cec.zbgl.dto.DeviceDto;
import com.cec.zbgl.model.DeviceCourse;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.utils.DtoUtils;

import org.litepal.LitePal;

import java.util.List;
import java.util.stream.Collectors;

public class CourseService {

    /**
     * 新增教程信息
     * @param course
     * @return
     */
    public boolean insert(DeviceCourse course){
        course.setUpload(false);
        course.setEdited(true);
        return course.save();
    }

    /**
     * 从服务器导入教程信息
     * @param course
     * @return
     */
    public boolean insertFromServer(DeviceCourse course) {
        return course.save();
    }

    /**
     * 批量插入服务器端数据
     * @param list
     */
    public void batchInsert(List<CourseDto> list) {
        LitePal.deleteAll(DeviceCourse.class);
        for (CourseDto courseDto : list) {
            DeviceCourse course = DtoUtils.toCourse(courseDto);
            course.setUpload(true);
            course.setValid(true);
            course.save();
        }


    }

    public List<DeviceCourse> loadList() {

        List<DeviceCourse> course = LitePal.where("isValid = ?", "1")
                .find(DeviceCourse.class);
        return course;
    }

    public List<DeviceCourse> getAll() {
        List<DeviceCourse> list = LitePal.findAll(DeviceCourse.class);
        return list.stream().filter(d -> d.isEdited() == true).collect(Collectors.toList());
    }

    public List<DeviceCourse> loadByDid(String deviceId, int type){
        List<DeviceCourse> courses = LitePal
                .where("deviceid = ? and coursetype = ? " +
                "and isvalid = ?",deviceId, String.valueOf(type),"1")
                .order("createTime DESC")
                .find(DeviceCourse.class);
        return courses;
    }

    public List<DeviceCourse> loadBySysid(String sysId, int type){
        List<DeviceCourse> courses = LitePal
                        .where("sysid = ? and coursetype = ? " +
                "and isvalid = ?",sysId, String.valueOf(type),"1").
                        order("createTime DESC").
                        find(DeviceCourse.class);
        return courses;
    }

    public int update(DeviceCourse course) {
        return course.update(course.getId());
    }

    public DeviceCourse getDevice(long id) {
        return LitePal.find(DeviceCourse.class, id);
    }

    public int delete(long id) {
        ContentValues values = new ContentValues();
        values.put("isEdited",true);
        values.put("isValid", false);
        return LitePal.update(DeviceCourse.class, values, id);
//        return LitePal.delete(DeviceCourse.class, id);
    }

}
