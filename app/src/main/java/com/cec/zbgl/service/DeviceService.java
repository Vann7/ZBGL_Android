package com.cec.zbgl.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cec.zbgl.db.DatabaseHelper;
import com.cec.zbgl.model.DeviceCourse;
import com.cec.zbgl.model.DeviceInfo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class DeviceService {
    private Context mContext;
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public boolean insert(DeviceInfo device){
        return device.save();
    }

    public List<DeviceInfo> loadLost() {
        List<DeviceInfo> devices = LitePal.where("isValid = ?", "1")
                .find(DeviceInfo.class);
        return devices;
    }

    public int update(DeviceInfo device) {
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
                List<DeviceInfo> temps = LitePal.where("type = ?", column)
                        .find(DeviceInfo.class);
                list.addAll(temps);
            }
        }
        if (status.size() != 0) {
            for (String column : status) {
                List<DeviceInfo> temps = LitePal.where("status = ?", column)
                        .find(DeviceInfo.class);
                list.addAll(temps);
            }
        }
        List<DeviceInfo> temps = LitePal.where("belongSys = ? and isValid = ?", belongSys,"1")
                    .find(DeviceInfo.class);
        list.addAll(temps);
        return list;
    }

    public List<DeviceInfo> searchBySys(String belongSys) {
        List<DeviceInfo> list = LitePal.where("belongSys = ? and isValid = ?", belongSys,"1")
                .find(DeviceInfo.class);
        return list;
    }

}
