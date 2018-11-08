package com.cec.zbgl.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;


public class TimeUtils {

    private static StringBuilder mFormatBuilder;
    private static Formatter mFormatter;

    public static String getTimeFromStamp(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(timeStamp));
    }

    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(Calendar.getInstance().getTime());
    }

    public static long getCurrentTimeStamp() {
        return System.currentTimeMillis();
    }

    public static String parseTime(String complexTime) {
        return complexTime;
    }

    public static String stringForTime(int timeMs) {
        //转换成字符串的时间
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs/1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds/60)%60;
        int hours = totalSeconds/3600;
        mFormatBuilder.setLength(0);
        if(hours>0){
            return mFormatter.format("%d:%02d:%02d",hours,minutes,seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d",minutes,seconds).toString();
        }
    }
}
