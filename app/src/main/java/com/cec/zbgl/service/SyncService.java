package com.cec.zbgl.service;

import android.app.Activity;
import android.content.Intent;

import com.cec.zbgl.dto.CourseDto;
import com.cec.zbgl.dto.DeviceDto;
import com.cec.zbgl.dto.OrgnizationDto;
import com.cec.zbgl.dto.SyncDto;
import com.cec.zbgl.dto.UserDto;
import com.cec.zbgl.event.MessageEvent;
import com.cec.zbgl.model.DeviceCourse;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.model.ServerConfig;
import com.cec.zbgl.model.SpOrgnization;
import com.cec.zbgl.model.User;
import com.cec.zbgl.utils.DtoUtils;
import com.cec.zbgl.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SyncService {

    private DeviceService deviceService;
    private CourseService courseService;
    private OrgsService orgsService;
    private UserService userService;

    private OkHttpClient okHttpClient = new OkHttpClient();

    private List<DeviceDto> dList;
    private List<CourseDto> cList;
    private List<OrgnizationDto> oList;
    private List<UserDto> uList;
    private SyncDto syncDto;
    private Gson gson;
    private Activity mActivity;



    public SyncService(Activity activity){
        deviceService = new DeviceService();
        courseService = new CourseService();
        orgsService = new OrgsService();
        userService = new UserService();
        gson = new Gson();
        syncDto = new SyncDto();
        mActivity = activity;
    }


    public void syncData() {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        ServerService service = new ServerService();
        ServerConfig config = service.load();
        String ip = config.getIp();
        String port = String.valueOf(config.getPort());
        String url ="";
        if (ip != null && port != null) {
          url = "http://"+ip + ":" + port + "/sync/data";
        } else {
          url = "http://172.18.3.253:8081/sync/data";
        }
//        String url = "http://172.18.3.253:8081/sync/data";
        String json = collectData();
        String requestBody = json;
        final Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, requestBody))
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("failed........");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

//                byte[] bytes = response.body().bytes();
//                String json = new String(bytes,"gbk");
                syncDto = gson.fromJson(response.body().string(), SyncDto.class);

                if (syncDto.getcList() != null && syncDto.getcList().size() > 0) {
                    cList = syncDto.getcList();
                    courseService.batchInsert(cList);
                    System.out.println("finish - cList");
                }
                if (syncDto.getdList() != null && syncDto.getdList().size() > 0) {
                    dList = syncDto.getdList();
                    deviceService.batchInsert(dList);
                    System.out.println("finish - dList");
                }
                if (syncDto.getoList() != null && syncDto.getoList().size() > 0) {
                    oList = syncDto.getoList();
                    orgsService.batchInsert(oList);
                    System.out.println("finish - oList");
                }

                response.body().close();
                EventBus.getDefault().post(new MessageEvent("刷新UI"));
//                Intent intent = new Intent("MY_BROADCAST");
//                mActivity.sendBroadcast(intent);
            }
        });
    }


    public String collectData() {
        SyncDto syncDto = new SyncDto();
        List<DeviceDto> dDtoList = new ArrayList<>();
        List<DeviceInfo> deviceInfoList = deviceService.getAll();
        for (DeviceInfo device : deviceInfoList) {
            DeviceDto dto = DtoUtils.toDeviceDto(device);
            dDtoList.add(dto);
        }
        syncDto.setdList(dDtoList);
        List<CourseDto> cDtoList = new ArrayList<>();
        List<DeviceCourse> courseList = courseService.getAll();
        for (DeviceCourse course : courseList) {
            CourseDto dto = DtoUtils.toCourseDto(course);
            cDtoList.add(dto);
        }
        syncDto.setcList(cDtoList);
        syncDto.setuList(new ArrayList<>());

        return gson.toJson(syncDto);
    }
}
