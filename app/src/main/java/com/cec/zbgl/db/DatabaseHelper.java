package com.cec.zbgl.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import com.cec.zbgl.common.Constant;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper mDatabaseHelper = null;
    private Context mContext;

    /**
     * 新建用户信息表
     */
    public static final String CREATE_USER = "create table if not exists user(" +
            "id integer primary key," +
            "name varchar(32)," +
            "password varchar(32))";

    /**
     * 新建组织机构表
     */
    public static final String CREATE_ORGS = "create table if not exists sporgnization("
            + "id varchar(36)  primary key,"
            + "code varchar(64),"
            + "parentCode varchar(64),"
            + "value varchar(64),"
            + "name varchar(128),"
            + "isSys boolean,"
            + "createTime date,"
            + "createrId varchar(36),"
            + "isValid boolean)";

    /**
     * 新建设备信息表
     */
    public static final String CREATE_DEVICE = "create table if not exists deviceinfo("
            +"id varchar(36) primary key,"
            +"name varchar(128),"
            +"type int,"
            +"belongSys varchar(64),"
            +"location varchar(128),"
            +"count int,"
            +"status int,"
            +"description varchar(500),"
            +"createrId varchar(36),"
            +"createrName varchar(128),"
            +"createTime date,"
            +"isValid boolean,"
            +"isUpload boolean)";

    /**
     * 新建设备教程表
     */
    public static final String CREATE_COURSE = "create table if not exists devicecourse("
            +"id varchar(36) primary key,"
            +"deviceId varchar(36),"
            +"sysId varchar(36),"
            +"name varchar(128),"
            +"deviceType int,"
            +"courseType int,"
            +"location varchar(128),"
            +"description varchar(500),"
            +"createrId varchar(36),"
            +"createrName varchar(128),"
            +"createTime date,"
            +"isValid boolean,"
            +"isUpload boolean)";

    public static final String CREATE_PAD = "";


    public static DatabaseHelper getInstance(Context context) {
        if (mDatabaseHelper == null) {
            synchronized (DatabaseHelper.class) {
                if (mDatabaseHelper == null) {
                    mDatabaseHelper = new DatabaseHelper(context);
                }
            }
        }
        return mDatabaseHelper;
    }

    public DatabaseHelper(Context context) {
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
        this.mContext = context;
    }

    /**
     * 当前数据库创建时毁掉的函数
     * @param db 数据库对象
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_ORGS);
        db.execSQL(CREATE_DEVICE);
        db.execSQL(CREATE_COURSE);
        Toast.makeText(mContext,"Create succeed",Toast.LENGTH_SHORT).show();

    }

    /**
     * 当数据库版本更新时回调的函数
     * @param db 数据库对象
     * @param oldVersion 数据库旧版本
     * @param newVersion 数据库新版本
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:

            default:
                break;
        }
    }
}
