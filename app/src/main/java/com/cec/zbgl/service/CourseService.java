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
//        LitePal.deleteAll(DeviceCourse.class);
        for (CourseDto courseDto : list) {
            DeviceCourse course = DtoUtils.toCourse(courseDto);
            course.setUpload(true);
            course.setValid(true);
            course.save();
        }
    }

    public void deleteAll() {
        LitePal.deleteAll(DeviceCourse.class);
    }

    public List<DeviceCourse> loadList() {

        List<DeviceCourse> course = LitePal.where("isValid = ?", "1")
                .find(DeviceCourse.class);
        return course;
    }

    public List<DeviceCourse> getAll() {
        return LitePal.where("isEdited = ? and isValid = ?", "1", "1").find(DeviceCourse.class);
    }

    public List<DeviceCourse> loadByDid(String deviceId, int type){
        List<DeviceCourse> courses = LitePal
                .where("deviceid = ? and coursetype = ? " +
                "and isValid = ?",deviceId, String.valueOf(type),"1")
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

    public int getCount() {
        return LitePal.where("isEdited = ? ", "1").count(DeviceCourse.class);
    }

    public List<DeviceCourse> loadByPage(int page) {

        List<DeviceCourse> list = LitePal.where("isEdited = ? and isValid = ? ", "1", "1")
                .order("id asc")
                .limit(20)
                .offset(page)
                .find(DeviceCourse.class);
        return list.subList(0, list.size() > 20 ? 20 : list.size());
    }

    public void deleteByDid(String deviceId) {
        List<DeviceCourse> cList = LitePal.where( "deviceId = ? and isValid = ?",
                deviceId, "1")
                .find(DeviceCourse.class);
        if (cList.size() > 0) {
            cList.stream().forEach(course -> {
                ContentValues values = new ContentValues();
                values.put("isValid", false);
                values.put("isEdited",true);
                LitePal.update(DeviceCourse.class, values,course.getId());
            });
        }
    }
}
