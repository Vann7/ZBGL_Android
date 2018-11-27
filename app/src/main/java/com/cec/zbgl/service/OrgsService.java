package com.cec.zbgl.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cec.zbgl.db.DatabaseHelper;
import com.cec.zbgl.dto.OrgnizationDto;
import com.cec.zbgl.model.SpOrgnization;
import com.cec.zbgl.utils.DtoUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class OrgsService {
    private Context mContext;
    private DatabaseHelper helper;
    private SQLiteDatabase db;


    public OrgsService(Context mcontext){
        this.mContext = mContext;
//        helper = DatabaseHelper.getInstance(mContext);
//        db = helper.getWritableDatabase();
        db = LitePal.getDatabase();
    }

    public OrgsService(){}

    public boolean insert(SpOrgnization org) {
        boolean flag = false;
//        List<SpOrgnization> orgs = new ArrayList<>();
//        SpOrgnization org = new SpOrgnization("1","0","根目录1");
//        org.setmId(UUID.randomUUID().toString());
//        orgs.add(org);
//        org = new SpOrgnization("2","0","根目录2");
//        org.setmId(UUID.randomUUID().toString());
//        orgs.add(org);
//        org = new SpOrgnization("3","0","根目录3");
//        org.setmId(UUID.randomUUID().toString());
//        orgs.add(org);
//        org = new SpOrgnization("4","0","根目录4");
//        org.setmId(UUID.randomUUID().toString());
//        orgs.add(org);
//        org = new SpOrgnization("4","1","根目录1-1");
//        org.setmId(UUID.randomUUID().toString());
//        orgs.add(org);
//        org = new SpOrgnization("5","1","根目录1-2");
//        org.setmId(UUID.randomUUID().toString());
//        orgs.add(org);
//        for (SpOrgnization o : orgs) {
//            flag = o.save();
//        }
        flag = org.save();
        return flag;
    }

    /**
     * 批量插入服务器端数据
     * @param list
     */
    public void batchInsert(List<OrgnizationDto> list) {
        LitePal.deleteAll(SpOrgnization.class);
        for (OrgnizationDto orgDto : list) {
            SpOrgnization org = DtoUtils.toOrgnization(orgDto);
            org.save();
        }

    }

    public List<SpOrgnization> loadList() {
        List<SpOrgnization> orgs = LitePal.
                where("isValid = ?", "1")
                .order("code ASC")
                .find(SpOrgnization.class);
        return orgs;
    }

    public List<SpOrgnization> loadNames() {
        List<SpOrgnization> list = LitePal
                .select("name")
                .where("isValid = ?", "1")
                .order("code ASC")
                .find(SpOrgnization.class);
        return list;
    }

}
