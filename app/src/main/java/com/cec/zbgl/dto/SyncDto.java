package com.cec.zbgl.dto;


import java.util.List;

public class SyncDto {

    private List<CourseDto> cList;
    private List<DeviceDto> dList;
    private List<OrgnizationDto> oList;
    private List<UserDto> uList;

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
}
