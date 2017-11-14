package com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.RxRetrofitApp;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoMaster;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoSession;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.SchoolMusicBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.SchoolMusicBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by zhaoyang on 2017/5/28.
 */

public class SchoolMusicDbUtil {
    private static SchoolMusicDbUtil db;
    private final static String dbName = "school_db";
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;


    public SchoolMusicDbUtil() {
        context = RxRetrofitApp.getApplication();
        openHelper = new DaoMaster.DevOpenHelper(context, dbName);

    }


    /**
     * 获取单例
     *
     * @return
     */
    public static SchoolMusicDbUtil getInstance() {
        if (db == null) {
            synchronized (SchoolMusicDbUtil.class) {
                if (db == null) {
                    db = new SchoolMusicDbUtil();
                }
            }
        }
        return db;
    }


    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }


    public void saveTime(SchoolMusicBean info) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SchoolMusicBeanDao downInfoDao = daoSession.getSchoolMusicBeanDao();
        downInfoDao.insert(info);
    }

    public void updateTime(SchoolMusicBean info) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SchoolMusicBeanDao downInfoDao = daoSession.getSchoolMusicBeanDao();
        downInfoDao.update(info);
    }

    public void deleteTime(long id) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SchoolMusicBeanDao downInfoDao = daoSession.getSchoolMusicBeanDao();
        downInfoDao.deleteByKey(id);
    }

    public void deleteAll(){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SchoolMusicBeanDao downInfoDao = daoSession.getSchoolMusicBeanDao();
        downInfoDao.deleteAll();
    }


    public SchoolMusicBean queryTimeBy(String name) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SchoolMusicBeanDao downInfoDao = daoSession.getSchoolMusicBeanDao();
        QueryBuilder<SchoolMusicBean> qb = downInfoDao.queryBuilder();
        qb.where(SchoolMusicBeanDao.Properties.Name.eq(name));
        List<SchoolMusicBean> list = qb.list();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public List<SchoolMusicBean> queryTimeAll() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SchoolMusicBeanDao downInfoDao = daoSession.getSchoolMusicBeanDao();
        QueryBuilder<SchoolMusicBean> qb = downInfoDao.queryBuilder();
        return qb.list();
    }
    public String  queryPathBy(String Sn) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SchoolMusicBeanDao downInfoDao = daoSession.getSchoolMusicBeanDao();
        QueryBuilder<SchoolMusicBean> qb = downInfoDao.queryBuilder();
        qb.where(SchoolMusicBeanDao.Properties.Sn.eq(Sn));
        List<SchoolMusicBean> list = qb.list();
        if (list.isEmpty()) {
            return "";
        } else {
            return list.get(0).getPath();
        }
    }

}
