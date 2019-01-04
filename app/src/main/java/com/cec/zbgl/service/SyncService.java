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
import com.cec.zbgl.utils.LogUtil;
import com.cec.zbgl.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
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
    private ServerService service;

    private OkHttpClient okHttpClient = new OkHttpClient();

    private List<DeviceDto> dList;
    private List<CourseDto> cList;
    private List<OrgnizationDto> oList;
    private List<UserDto> uList;
    private SyncDto syncDto;
    private Gson gson;
    private Activity mActivity;
    private String adress;
    private int port;



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



    public void socketSync(){
        service = new ServerService();
        ServerConfig config = service.load();
        OutputStream os = null;
        InputStream is = null;
        Socket socket = null;
        if (config != null && !config.getIp().equals("")) {
            adress = config.getIp();
            port = 8088;
        }else {
            EventBus.getDefault().post(new MessageEvent("刷新UI"));
            return;
        }

        try {
            socket = new Socket(adress, 8088);
            if (!socket.isConnected()) {
                EventBus.getDefault().post(new MessageEvent("刷新UI"));
                return;
            }
            os = socket.getOutputStream();
            is = socket.getInputStream();
            InputStreamReader reader = new InputStreamReader(is, "UTF-8");
//            OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
            PrintStream out = new PrintStream(os);
            sendMsg(out); //发送APP数据至服务器
            socket.shutdownOutput();

            BufferedReader bufferedReader = new BufferedReader(reader);
            receiveMsg(bufferedReader); //接受服务器端数据
            reader.close();
            is.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            EventBus.getDefault().post(new MessageEvent("刷新UI"));
        }
    }


    /**
     * 将app端数据发送至服务器
     * @param out
     */
    public void sendMsg(PrintStream out) {
        int flag = 0; int flag0 = 0; int count = 0; int count0 = 0;
        //备品信息
        count0 = deviceService.getCount();
        while (flag0 <= count0) {
            SyncDto syncDto = new SyncDto();
            List<DeviceDto> dDtoList = new ArrayList<>();
            List<DeviceInfo> deviceInfos = deviceService.loadByPage(flag0);
            deviceInfos.stream().forEach( deviceInfo -> {
                DeviceDto dto = DtoUtils.toDeviceDto(deviceInfo);
                dDtoList.add(dto);
            });
            syncDto.setdList(dDtoList);
            out.println(gson.toJson(syncDto));
            out.flush();
            flag0 += 20;
        }

        //教程信息
        count = courseService.getCount();
        flag = 0;
        while (flag <= count) {
            SyncDto syncDto = new SyncDto();
            List<CourseDto> cDtoList = new ArrayList<>();
            List<DeviceCourse> courses = courseService.loadByPage(flag);
            courses.stream().forEach( course -> {
                CourseDto dto = DtoUtils.toCourseDto(course);
                cDtoList.add(dto);
            });
            syncDto.setcList(cDtoList);
            out.println(gson.toJson(syncDto));
            out.flush();
            flag += 20;
        }
    }


    /**
     * 同步服务端数据
     * @param br
     * @throws IOException
     */
    public void receiveMsg(BufferedReader br) throws IOException {
        String result = "";

        //清空旧数据
        courseService.deleteAll();
        deviceService.deleteAll();
        orgsService.deleteAll();
        //插入服务端同步数据
        while ( (result = br.readLine()) != null) {
           SyncDto syncDto = gson.fromJson(result, SyncDto.class);
            if (syncDto.getcList() != null && syncDto.getcList().size() > 0) {
                cList = syncDto.getcList();
                courseService.batchInsert(cList);
                LogUtil.i("cList", String.valueOf(cList.size()));
            }
            if (syncDto.getdList() != null && syncDto.getdList().size() > 0) {
                dList = syncDto.getdList();
                deviceService.batchInsert(dList);
                LogUtil.i("dList", String.valueOf(dList.size()));
            }
            if (syncDto.getoList() != null && syncDto.getoList().size() > 0) {
                oList = syncDto.getoList();
                orgsService.batchInsert(oList);
                LogUtil.i("oList", String.valueOf(oList.size()));
            }
        }

        br.close();
    }

}
