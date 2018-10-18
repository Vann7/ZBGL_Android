package com.cec.zbgl.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cec.zbgl.db.DatabaseHelper;

public class DeviceInfoService {
    private Context mContext;
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public DeviceInfoService(Context mContext) {
        this.mContext = mContext;
        helper = DatabaseHelper.getInstance(mContext);
        db = helper.getWritableDatabase();
    }

}
