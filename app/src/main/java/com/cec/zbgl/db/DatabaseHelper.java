package com.cec.zbgl.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import com.cec.zbgl.common.Constant;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper mDatabaseHelper = null;
    private Context mContext;

    public static final String CREATE_USER = "create table if not exists user(" +
            "id integer primary key," +
            "name varchar(32)," +
            "password varchar(32))";

    public static final String CREATE_DEVICE = "";

    public static final String CREATE_COURSE = "";

    public static final String CREATE_ORGNIZATION= "";

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
