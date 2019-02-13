package com.cec.zbgl.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cec.zbgl.db.DatabaseHelper;
import com.cec.zbgl.dto.OrgnizationDto;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.model.SpOrgnization;
import com.cec.zbgl.utils.DtoUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

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


    public int update(SpOrgnization org) {
        org.setUpload(true);
        return org.update(org.getId());
    }

    public SpOrgnization getOrg(String mId){
        List<SpOrgnization> orgs = LitePal.
                where("isValid = ? and mId = ?", "1", mId)
                .find(SpOrgnization.class);
        return (orgs.size()==0)? new SpOrgnization() : orgs.get(0);
    }

    public void deleteAll() {
        LitePal.deleteAll(SpOrgnization.class);
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
                .select("mid","name")
                .where("isValid = ?", "1")
                .order("code ASC")
                .find(SpOrgnization.class);
        return list;
    }

    public String getName(String mid) {
        List<SpOrgnization> orgs = LitePal
                .select("name")
                .where("mid = ?", mid)
                .find(SpOrgnization.class);
       return  (orgs.size() == 0) ? "" :orgs.get(0).getName();
    }

    public String getmId(String name) {
        List<SpOrgnization> orgs = LitePal
                .select("mid")
                .where("name = ?", name)
                .find(SpOrgnization.class);
       return  (orgs.size() == 0) ? "" :orgs.get(0).getmId();
    }

    public List<SpOrgnization> getAll() {
        List<SpOrgnization> list = LitePal.findAll(SpOrgnization.class);
        return list.stream().filter( org -> org.isUpload() == true).collect(Collectors.toList());
    }


    public int getCount() {
        return LitePal.count(SpOrgnization.class);
    }

    public List<SpOrgnization> loadByPage(int page) {
        List<SpOrgnization> list = LitePal.where("isUpload = ? ", "1")
                .order("id asc")
                .limit(20)
                .offset(page)
                .find(SpOrgnization.class);
        return list.subList(0, list.size() > 20 ? 20 : list.size());
    }

    public List<SpOrgnization> searchByName(String name) {
        List<SpOrgnization> list = LitePal.where("name like ?", "%"+name+"%")
                .find(SpOrgnization.class);
        return list;

    }
}
