package com.cec.zbgl.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.cec.zbgl.activity.LoginActivity;
import com.cec.zbgl.db.DatabaseHelper;
import com.cec.zbgl.model.User;
import com.cec.zbgl.utils.ToastUtils;

import org.litepal.LitePal;

import java.util.List;
import java.util.Random;

public class UserService {

    private Context mContext;
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public UserService(Context context) {
        mContext = context;
//        helper = DatabaseHelper.getInstance(mContext);
//        db = helper.getWritableDatabase();
        db = LitePal.getDatabase();
    }

    public void create() {

    }

    public boolean insert(User user) {

/*
        ContentValues values = new ContentValues();
        values.put("id",1);
        values.put("name","tony");
        values.put("password","123456");
        long l =  db.insert("user",null,values);*/
        SQLiteDatabase db2 = LitePal.getDatabase();
        Random random = new Random();
        user.setId(random.nextInt(100));
        boolean flag =  user.save();
        if (flag) {
            ToastUtils.showShort("新增成功");
        }else {
            ToastUtils.showShort("新增失败");
        }

        return flag;
    }

    public int checkUser(User user) {
        List<User> u = LitePal.where("name = ? and password = ?",user.getName(),user.getPassword()).find(User.class);
        if (u.size() > 0){
            return 1;
        }else {
            return 0;
        }
//        Cursor cursor = db.rawQuery("select * from user where name= '" + user.getName()+"' and password = '" + user.getPassword()+"'",null);
//        if (cursor.getCount() > 0) {
//            return 1;
//        }else {
//            return 0;
//        }

    }

}
