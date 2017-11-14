package com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.RxRetrofitApp;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoMaster;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoSession;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.ColourListBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.ColourListBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoyang on 2017/6/1.
 */

public class ColourListDbUtil {
    private static ColourListDbUtil db;
    private final static String dbName = "colourlist_db";
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;


    public ColourListDbUtil() {
        context = RxRetrofitApp.getApplication();
        openHelper = new DaoMaster.DevOpenHelper(context, dbName);

    }


    /**
     * 获取单例
     *
     * @return
     */
    public static ColourListDbUtil getInstance() {
        if (db == null) {
            synchronized (ColourListDbUtil.class) {
                if (db == null) {
                    db = new ColourListDbUtil();
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


    public void saveTime(ColourListBean info) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ColourListBeanDao downInfoDao = daoSession.getColourListBeanDao();
        downInfoDao.insert(info);
    }

    public void updateTime(ColourListBean info) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ColourListBeanDao downInfoDao = daoSession.getColourListBeanDao();
        downInfoDao.update(info);
    }

    public void deleteTime(long id) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ColourListBeanDao downInfoDao = daoSession.getColourListBeanDao();
        downInfoDao.deleteByKey(id);
    }

    public void deleteAll(){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ColourListBeanDao downInfoDao = daoSession.getColourListBeanDao();
        downInfoDao.deleteAll();
    }


    public List<ColourListBean> queryTimeBy(int flag) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ColourListBeanDao downInfoDao = daoSession.getColourListBeanDao();
        QueryBuilder<ColourListBean> qb = downInfoDao.queryBuilder();
        qb.where(ColourListBeanDao.Properties.Flag.eq(flag));
        List<ColourListBean> list = qb.list();
        if (list.isEmpty()) {
            return new ArrayList<>();
        } else {
            return list;
        }
    }

    public List<ColourListBean> queryTimeAll() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ColourListBeanDao downInfoDao = daoSession.getColourListBeanDao();
        QueryBuilder<ColourListBean> qb = downInfoDao.queryBuilder();
        return qb.list();
    }
}
