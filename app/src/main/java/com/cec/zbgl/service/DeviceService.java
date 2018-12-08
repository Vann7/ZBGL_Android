package com.cec.zbgl.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cec.zbgl.db.DatabaseHelper;
import com.cec.zbgl.dto.DeviceDto;
import com.cec.zbgl.model.DeviceCourse;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.utils.DtoUtils;

import org.litepal.LitePal;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeviceService {
    private Context mContext;
    private DatabaseHelper helper;
    private SQLiteDatabase db;


    /**
     * 新增装备信息
     * @param device
     * @return
     */
    public boolean insert(DeviceInfo device){
        device.setUpload(false);
        device.setEdited(true);
        return device.save();
    }

    public List<DeviceInfo> loadList(int page,String belongSys) {
        if (belongSys != "") {
            List<DeviceInfo> devices = LitePal
                    .where("isValid = ? and belongSys = ?", "1", belongSys)
                    .limit(20)
                    .offset(10 * page)
                    .order("createTime DESC")
                    .find(DeviceInfo.class);
            return devices;
        } else {
            List<DeviceInfo> devices = LitePal
                    .where("isValid = ?", "1")
                    .limit(20)
                    .offset(10 * page)
                    .order("createTime DESC")
                    .find(DeviceInfo.class);
            return devices;
        }

    }

    public List<DeviceInfo> loadBySys(String belongSys) {
        List<DeviceInfo> devices = LitePal
                .where("isValid = ? and belongSys = ?",
                "1",belongSys)
                .limit(20)
                .order("createTime DESC")
                .find(DeviceInfo.class);
        return devices;
    }

    /**
     * 批量插入服务器端数据
     * @param list
     */
    public void batchInsert(List<DeviceDto> list) {
        LitePal.deleteAll(DeviceInfo.class);
        for (DeviceDto deviceDto : list) {
            deviceDto.setUpload(true);
            DeviceInfo device = null;
            try {
                device = DtoUtils.toDevice(deviceDto);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            device.save();
        }

    }

    public List<DeviceInfo> getAll() {
        List<DeviceInfo> list = LitePal.findAll(DeviceInfo.class);
        return list.stream().filter( d -> d.isEdited() == true).collect(Collectors.toList());
    }

    public int update(DeviceInfo device) {
        device.setUpload(true);
        device.setEdited(true);
        return device.update(device.getId());
    }

    public DeviceInfo getDevice(long id) {
        return LitePal.find(DeviceInfo.class, id);
    }

    public DeviceInfo getDeviceByMid(String mid) {
       DeviceInfo deviceInfo = LitePal.where("mid = ?", mid).findFirst(DeviceInfo.class);
       return deviceInfo;
    }

    public int delete(long id) {
        ContentValues values = new ContentValues();
        values.put("isEdited",true);
        values.put("isValid", false);
        return LitePal.update(DeviceInfo.class, values, id);

    }

    public boolean check(String mid) {
       return LitePal.isExist(DeviceInfo.class, "mid = ?" , mid);
    }

    public List<DeviceInfo> searchByName(String name) {
        List<DeviceInfo> list = LitePal.where("name like ?", "%"+name+"%")
                .find(DeviceInfo.class);
        return list;
    }

    public List<DeviceInfo> filterByType(List<String> type, List<String> status, String belongSys) {
        List<DeviceInfo> list = new ArrayList<>();
        if (type.size() != 0)  {
            for (String column : type) {
                if (status.size() != 0) {
                    for (String column2 : status) {
                        List<DeviceInfo> temps = LitePal.where("type = ? and status = ?" +
                                " and belongSys = ? " + "and isValid = ?", column, column2, belongSys, "1")
                                .find(DeviceInfo.class);
                        list.addAll(temps);
                    }
                } else {
                    List<DeviceInfo> temps = LitePal.where("type = ? " +
                            " and belongSys = ? " + "and isValid = ?", column,  belongSys, "1")
                            .find(DeviceInfo.class);
                    list.addAll(temps);
                }
            }
        } else {
            if (status.size() != 0) {
                for (String column : status) {
                    List<DeviceInfo> temps = LitePal.where("status = ? and belongSys = ?" +
                            " and isValid = ?", column, belongSys,"1")
                            .find(DeviceInfo.class);
                    list.addAll(temps);
                }
            } else {
                List<DeviceInfo> temps = LitePal.where("belongSys = ? and isValid = ?", belongSys,"1")
                        .find(DeviceInfo.class);
                list.addAll(temps);
            }
        }
        return list;
    }

    public List<DeviceInfo> searchBySys(String belongSys) {
        List<DeviceInfo> list = LitePal
                .where("belongSys = ? and isValid = ?", belongSys,"1")
                .limit(20)
                .find(DeviceInfo.class);
        return list;
    }

}
