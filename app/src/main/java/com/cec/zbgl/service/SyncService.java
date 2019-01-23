package com.cec.zbgl.service;

import android.app.Activity;
import android.content.Intent;
import android.support.constraint.solver.LinearSystem;

import com.cec.zbgl.dto.CourseDto;
import com.cec.zbgl.dto.DeviceDto;
import com.cec.zbgl.dto.DeviceReleDto;
import com.cec.zbgl.dto.OrgnizationDto;
import com.cec.zbgl.dto.SyncDto;
import com.cec.zbgl.dto.UserDto;
import com.cec.zbgl.event.MessageEvent;
import com.cec.zbgl.model.DeviceCourse;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.model.DeviceRele;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private List<DeviceReleDto> rList;
    private List<UserDto> uList;
    private SyncDto syncDto;
    private Gson gson;
    private Activity mActivity;
    private String adress;
    private int port;
    private List<String> mList;



    public SyncService(Activity activity){
        deviceService = new DeviceService();
        courseService = new CourseService();
        orgsService = new OrgsService();
        userService = new UserService();
        gson = new Gson();
        syncDto = new SyncDto();
        mActivity = activity;
        mList = new ArrayList<>();
    }


    /**
     * 通过RESTFUL进行数据同步
     * 暂弃用
     */
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


    /**
     * 通过socket进行数据同步
     */
    public void socketSync(List<String> mList){
        service = new ServerService();
        this.mList = mList;
        ServerConfig config = service.load();
        OutputStream os = null;
        InputStream is = null;
        Socket socket = null;
        if (config != null && !config.getIp().equals("")) {
            adress = config.getIp();
            port = 8088;
        }else {
            EventBus.getDefault().post(new MessageEvent("failed"));
            return;
        }

        try {
            socket = new Socket(adress, 8088);
            if (!socket.isConnected()) {
                EventBus.getDefault().post(new MessageEvent("failed"));
                return;
            }
            os = socket.getOutputStream();
            is = socket.getInputStream();
            InputStreamReader reader = new InputStreamReader(is, "UTF-8");
            PrintStream out = new PrintStream(os);
            sendMsg(out); //发送APP数据至服务器
            socket.shutdownOutput();

            BufferedReader bufferedReader = new BufferedReader(reader);
            receiveMsg(bufferedReader); //接受服务器端数据
            reader.close();
            is.close();
            out.close();
            EventBus.getDefault().post(new MessageEvent("succeed"));
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new MessageEvent("failed"));
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    /**
     * 将app端数据发送至服务器
     * @param out
     */
    public void sendMsg(PrintStream out) {

        if (mList.size() > 0) {
            SyncDto syncDto = new SyncDto();
            syncDto.setmList(mList);
            out.println(gson.toJson(syncDto));
            out.flush();
        }
        int flag = 0; int flag0 = 0; int count = 0; int count0 = 0;

        //发送设备信息
        if (mList.contains("device")) {
            //待同步备品信息总数
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

            //设备关联信息
            int count1 = deviceService.getCountbyRele();
            List<DeviceReleDto> rDtoList = new ArrayList<>();
            List<DeviceRele> releList = deviceService.getSyncReleList();
            releList.stream().forEach(rele -> {
                DeviceReleDto releDto = DtoUtils.toReleDto(rele);
                rDtoList.add(releDto);
            });
            syncDto.setrList(rDtoList);
            out.println(gson.toJson(syncDto));
            out.flush();
        }

        //发送教程信息
        if (mList.contains("course")) {
            //待同步教程信息总数
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

        if (mList.contains("orgnization")) {
            int count2 = orgsService.getCount();
            int flag2 = 0;
            while (flag2 <= count2) {
                SyncDto syncDto = new SyncDto();
                List<OrgnizationDto> oDtoList = new ArrayList<>();
                List<SpOrgnization> oList = orgsService.loadByPage(flag2);
                oList.stream().forEach( org -> {
                    OrgnizationDto dto = DtoUtils.toOrgDto(org);
                    oDtoList.add(dto);
                });
                syncDto.setoList(oDtoList);
                out.println(gson.toJson(syncDto));
                out.flush();
                flag2 += 20;
            }
        }
    }


    /**
     * 同步服务端数据
     * @param br
     * @throws IOException
     */
    public void receiveMsg(BufferedReader br) throws IOException {
        String result = "";
        if (mList.contains("orgnization")) {
            orgsService.deleteAll();
        }

        if (mList.contains("device")) {
            deviceService.deleteAll();
            deviceService.deleteReleAll();
        }

        if (mList.contains("course")) {
            courseService.deleteAll();
        }

        if (mList.contains("user")) {
            //TODO 删除人员信息
        }

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
            if(syncDto.getuList() != null && syncDto.getuList().size() > 0) {
                uList = syncDto.getuList();
                //Todo 添加人员批量插入接口
            }
            if (syncDto.getrList() != null && syncDto.getrList().size() > 0) {
                rList = syncDto.getrList();
                deviceService.batchInsertRele(rList);
            }
        }

        br.close();
    }

}
