package com.cec.zbgl.dto;


import com.cec.zbgl.model.DeviceRele;

import java.util.List;

public class SyncDto {

    private List<CourseDto> cList;
    private List<DeviceDto> dList;
    private List<OrgnizationDto> oList;
    private List<UserDto> uList;
    private List<String> mList;
    private List<DeviceReleDto> rList;
    private List<CourseDto> cAllList; //pad端全部教程信息
    private List<DeviceDto> dAllList; //pad端全部装备信息

    public List<CourseDto> getcList() {
        return cList;
    }

    public void setcList(List<CourseDto> cList) {
        this.cList = cList;
    }

    public List<DeviceDto> getdList() {
        return dList;
    }

    public void setdList(List<DeviceDto> dList) {
        this.dList = dList;
    }

    public List<OrgnizationDto> getoList() {
        return oList;
    }

    public void setoList(List<OrgnizationDto> oList) {
        this.oList = oList;
    }

    public List<UserDto> getuList() {
        return uList;
    }

    public void setuList(List<UserDto> uList) {
        this.uList = uList;
    }

    public List<DeviceReleDto> getrList() {
        return rList;
    }

    public void setrList(List<DeviceReleDto> rList) {
        this.rList = rList;
    }

    public List<String> getmList() {
        return mList;
    }

    public void setmList(List<String> mList) {
        this.mList = mList;
    }

    public List<CourseDto> getcAllList() {
        return cAllList;
    }

    public void setcAllList(List<CourseDto> cAllList) {
        this.cAllList = cAllList;
    }

    public List<DeviceDto> getdAllList() {
        return dAllList;
    }

    public void setdAllList(List<DeviceDto> dAllList) {
        this.dAllList = dAllList;
    }
}
