package com.cec.zbgl.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cec.zbgl.db.DatabaseHelper;
import com.cec.zbgl.dto.DeviceDto;
import com.cec.zbgl.dto.DeviceReleDto;
import com.cec.zbgl.model.DeviceCourse;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.model.DeviceRele;
import com.cec.zbgl.model.SpOrgnization;
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


    /**
     * 加载更多装备信息(自定义查询)
     * @param page
     * @param belongSys
     * @param type
     * @param status
     * @return
     */
    public List<DeviceInfo> loadMoreList(int page, String belongSys, List<String> type, List<String> status) {
        int count = 0;
        if (page != 0) {
            count = 20 * page;
        }
        List<DeviceInfo> list = new ArrayList<>();
        if (type.size() != 0)  {
            for (String column : type) {
                if (status.size() != 0) {
                    for (String column2 : status) {
                        List<DeviceInfo> temps = LitePal.where("type = ? and status = ?" +
                                " and belongSys = ? " + "and isValid = ?", column, column2, belongSys, "1")
                                .limit(20)
                                .offset(count)
                                .order("createTime DESC")
                                .find(DeviceInfo.class);
                        list.addAll(temps);
                    }
                } else {

                  /*  List<SpOrgnization> pCode = LitePal.where("mId = ? and isValid = ?", belongSys, "1")
                            .find(SpOrgnization.class);
                    List<SpOrgnization> sysIds = LitePal.where("parentCode = ? and isValid = ?", pCode.get(0).getCode())
                            .find(SpOrgnization.class);

                    sysIds.stream().forEach( org -> {
                        List<DeviceInfo> temps = LitePal.where("type = ? " +
                                " and belongSys = ? " + "and isValid = ?", column,  belongSys, "1")
                                .limit(20)
                                .offset(count)
                                .order("createTime DESC")
                                .find(DeviceInfo.class);
                        list.addAll(temps);
                    });*/

                    List<DeviceInfo> temps = LitePal.where("type = ? " +
                            " and belongSys = ? " + "and isValid = ?", column,  belongSys, "1")
                            .limit(20)
                            .offset(count)
                            .order("createTime DESC")
                            .find(DeviceInfo.class);
                    list.addAll(temps);
                }
            }
        } else {
            if (status.size() != 0) {
                for (String column : status) {
                    List<DeviceInfo> temps = LitePal.where("status = ? and belongSys = ?" +
                            " and isValid = ?", column, belongSys,"1")
                            .limit(20)
                            .offset(count)
                            .order("createTime DESC")
                            .find(DeviceInfo.class);
                    list.addAll(temps);
                }
            } else {
                List<DeviceInfo> temps = LitePal.where("belongSys = ? and isValid = ?", belongSys,"1")
                        .limit(20)
                        .offset(count)
                        .order("createTime DESC")
                        .find(DeviceInfo.class);
                list.addAll(temps);
            }
        }
        return list;
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
     * 批量插入服务器端数据 (如果数据已存在 则进行更新操作)
     * @param list
     */
    public void batchInsert(List<DeviceDto> list) {

        for (DeviceDto deviceDto : list) {
            deviceDto.setUpload(true);
            DeviceInfo device = null;
            try {
                device = DtoUtils.toDevice(deviceDto);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
           String mId = device.getmId();
           List<DeviceInfo> temp =  LitePal.where("mId = ?", mId).find(DeviceInfo.class);

           if (temp.size() > 0) {
               if (device.isValid()) { //如果该设备存在，并且标识为true，则进行更新操作
                   for (DeviceInfo d : temp) {
                       d.setName(device.getName());
                       d.setUpload(true);
                       d.setCode(device.getCode());
                       d.setImage(device.getImage());
                       d.setBelongSys(device.getBelongSys());
                       d.setCreaterName(device.getCreaterName());
                       d.setCreaterId(device.getCreaterId());
                       d.setCount(device.getCount());
                       d.setStatus(device.getStatus());
                       d.setDescription(device.getDescription());
                       d.setType(device.getType());
                       d.update(d.getId());
                   }
               } else { //如果该设备存在，并且标识为false，则进行删除操作
                   temp.stream().forEach(d -> {
                       d.delete();
                       CourseService courseService = new CourseService();
                       courseService.deleteByDid(d.getmId()); //删除设备关联教程信息
                   });
               }
           } else {
               if(device.isValid() == true) {
                   device.save();
               }
           }

        }

    }

    public void deleteAll() {
        LitePal.deleteAll(DeviceInfo.class);
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

    public int hasEdited(DeviceInfo device) {
        ContentValues values = new ContentValues();
        values.put("isEdited",false);
        values.put("isUpload", false);
        values.put("isValid", true);
        return LitePal.update(DeviceInfo.class, values, device.getId());
    }

    public DeviceInfo getDevice(long id) {
        return LitePal.find(DeviceInfo.class, id);
    }

    public DeviceInfo getDeviceByMid(String mid) {
       if (mid == null) return null;
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


    public List<DeviceInfo> searchBySys(String belongSys) {
        List<DeviceInfo> list = LitePal
                .where("belongSys = ? and isValid = ?", belongSys,"1")
                .limit(20)
                .order("createTime DESC")
                .find(DeviceInfo.class);
        return list;
    }

    public int getCount() {
        return LitePal.where("isEdited = ? ", "1")
                .count(DeviceInfo.class);
    }


    public List<DeviceInfo> loadByPage(int page) {
        List<DeviceInfo> list = LitePal.where("isEdited = ? ", "1")
                .order("id desc")
                .limit(20)
                .offset(page)
                .find(DeviceInfo.class);
        return list;
    }

    public boolean releDevice(String deviceId, String releDeviceId) {
        DeviceRele deviceRele = new DeviceRele(deviceId, releDeviceId);
        return deviceRele.save();
    }

    public int unReleDevice(long id) {
        ContentValues values = new ContentValues();
        values.put("isValid", false);
        values.put("isUpload", true);
        return LitePal.update(DeviceRele.class, values, id);
//       return LitePal.delete(DeviceRele.class, id);
    }


    /**
     * 加载关联设备列表
     * @param deviceId
     * @return
     */
    public List<DeviceRele> getDeviceReleList(String deviceId) {
        List<DeviceRele> resultList = new ArrayList<>();

            List<DeviceRele> list = LitePal.where("isValid = ? and deviceId = ? "
                    , "1", deviceId)
                    .find(DeviceRele.class);
            resultList.addAll(list);

            List<DeviceRele> list2 =  LitePal.where("isValid = ? and releDeviceId = ? "
                    , "1", deviceId)
                    .find(DeviceRele.class);
            list2.stream().forEach( rele2 -> {
                DeviceRele rele0 = new DeviceRele(rele2.getId(),rele2.getmId(),rele2.getReleDeviceId(), rele2.getDeviceId());
                resultList.add(rele0);
            });
        return resultList;
    }

    public int getCountbyRele() {
       return LitePal.count(DeviceRele.class);
    }

    public List<DeviceRele> getSyncReleList() {
        List<DeviceRele> list = LitePal.where("isUpload = ?", "1")
                .find(DeviceRele.class);
        return list;
    }

    public DeviceInfo getDevicebyRele(Long id) {
        DeviceRele rele = LitePal.find(DeviceRele.class, id);
        if (rele == null) return null;
        List<DeviceInfo> list = LitePal.where("isValid = ? and mId = ?", "1", rele.getReleDeviceId())
                .find(DeviceInfo.class);
        return (list.size() == 0) ? null : list.get(0);
    }

    /**
     * 清空已有装备关联数据
     */
    public void deleteReleAll() {
        LitePal.deleteAll(DeviceRele.class);
    }

    /**
     * 批量插入装备关联数据
     * @param rList
     */
    public void batchInsertRele(List<DeviceReleDto> rList) {
        for (DeviceReleDto releDto : rList) {
            DeviceRele rele = DtoUtils.toRele(releDto);
            rele.setUpload(false);
            rele.save();
        }
    }

    /**
     * 删除与该id相关的设备关联关系
     * @param id
     */
    public void unReleDevices(String id) {
        List<DeviceRele> list1 = LitePal.where("deviceId = ?", id)
                .find(DeviceRele.class);
        ContentValues values = new ContentValues();
        values.put("isValid", false);
        list1.stream().forEach(rele -> {
            LitePal.update(DeviceRele.class, values, rele.getId());
        });
        List<DeviceRele> list2 = LitePal.where("releDeviceId = ?", id)
                .find(DeviceRele.class);
        list2.stream().forEach(rele -> {
            LitePal.update(DeviceRele.class, values, rele.getId());
        });
    }

    public boolean hasRele(String id) {
        List<DeviceRele> list1 = LitePal.where("deviceId = ? and isValid = ?", id, "1")
                .find(DeviceRele.class);
        List<DeviceRele> list2 = LitePal.where("releDeviceId = ? and isValid = ?", id, "1")
                .find(DeviceRele.class);
        if (list1.size() > 0 || list2.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public List<DeviceInfo> loadPadAll() {
        return LitePal.select("mId","createTime","isValid")
                .find(DeviceInfo.class);
    }
}
