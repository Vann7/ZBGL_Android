package com.cec.zbgl;

import android.app.Application;
import android.content.Context;

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
