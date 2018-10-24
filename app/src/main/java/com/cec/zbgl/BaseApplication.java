package com.cec.zbgl;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import org.litepal.LitePal;

public class BaseApplication extends Application {
    public final static String TAG = "BaseApplication";
    public final static boolean DEBUG = true;
    private static BaseApplication application;
    private static int mainTid;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        LitePal.initialize(this);
        mainTid = android.os.Process.myTid();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        if (Build.VERSION.SDK_INT >= 18){

            builder.detectFileUriExposure();
        }
        StrictMode.setVmPolicy(builder.build());
    }

    /**
     * 获取application
     *
     * @return
     */
    public static Context getApplication() {
        return application;
    }

    /**
     * 获取主线程
     * @return
     */
    public static int getMainTid(){
        return mainTid;
    }

    /**
     * 退出应用程序
     */
    public static void quitApplication() {
        System.exit(0);
    }


}
