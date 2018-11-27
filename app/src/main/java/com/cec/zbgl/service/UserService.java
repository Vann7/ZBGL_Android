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
//        db = LitePal.getDatabase();
    }

    public UserService(){}

    public void create() {

    }

    public boolean insert(User user) {

/*
        ContentValues values = new ContentValues();
        values.put("id",1);
        values.put("name","tony");
        values.put("password","123456");
        long l =  db.insert("user",null,values);*/
//        SQLiteDatabase db2 = LitePal.getDatabase();
        Random random = new Random();
        user.setId(random.nextInt(100));
        boolean flag =  user.save();
        return flag;
    }

    public List<User> checkUser(User user) {
        List<User> list = LitePal.where("name = ? and password = ?",user.getName(),user.getPassword()).find(User.class);
        return list;
    }

    public List<User> getALl() {
        return LitePal.findAll(User.class);
    }

    public int updatePassword(User user) {
        ContentValues values = new ContentValues();
        values.put("password",user.getPassword());
       int flag =  LitePal.update(User.class, values, user.getId());
       return flag;
    }

}
