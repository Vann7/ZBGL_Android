package com.cec.zbgl.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.cec.zbgl.activity.LoginActivity;
import com.cec.zbgl.db.DatabaseHelper;
import com.cec.zbgl.dto.UserDto;
import com.cec.zbgl.model.User;
import com.cec.zbgl.utils.DtoUtils;
import com.cec.zbgl.utils.ToastUtils;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.format.DefaultHashFormatFactory;
import org.apache.shiro.crypto.hash.format.Shiro1CryptFormat;
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
        user.setAppUpdate(true);
        user = ShiroService.shiroPwd(user);
        boolean flag =  user.save();
        return flag;
    }

    public List<User> checkUser(User user) {
        user = ShiroService.shiroPwd(user);
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

    public void deleteAll() {
        LitePal.deleteAll(User.class);
    }

    public void batchInsert(List<UserDto> uList) {
        User root = new User("root","123456");
        root = ShiroService.shiroPwd(root);
        root.save();//保存root用户信息
        for (UserDto userDto : uList) {
            User user = DtoUtils.toUser(userDto);
            user.save();
        }
    }
}
