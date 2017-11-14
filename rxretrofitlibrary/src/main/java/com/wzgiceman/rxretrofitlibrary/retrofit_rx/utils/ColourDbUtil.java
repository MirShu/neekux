package com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.RxRetrofitApp;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoMaster;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoSession;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.ColourBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.ColourBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by zhaoyang on 2017/6/1.
 */

public class ColourDbUtil {
    private static ColourDbUtil db;
    private final static String dbName = "colour_db";
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;


    public ColourDbUtil() {
        context = RxRetrofitApp.getApplication();
        openHelper = new DaoMaster.DevOpenHelper(context, dbName);

    }


    /**
     * 获取单例
     *
     * @return
     */
    public static ColourDbUtil getInstance() {
        if (db == null) {
            synchronized (ColourDbUtil.class) {
                if (db == null) {
                    db = new ColourDbUtil();
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


    public void saveTime(ColourBean info) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ColourBeanDao downInfoDao = daoSession.getColourBeanDao();
        downInfoDao.insert(info);
    }

    public void updateTime(ColourBean info) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ColourBeanDao downInfoDao = daoSession.getColourBeanDao();
        downInfoDao.update(info);
    }

    public void deleteTime(long id) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ColourBeanDao downInfoDao = daoSession.getColourBeanDao();
        downInfoDao.deleteByKey(id);
    }

    public void deleteAll(){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ColourBeanDao downInfoDao = daoSession.getColourBeanDao();
        downInfoDao.deleteAll();
    }


    public ColourBean queryTimeBy(int flag) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ColourBeanDao downInfoDao = daoSession.getColourBeanDao();
        QueryBuilder<ColourBean> qb = downInfoDao.queryBuilder();
        qb.where(ColourBeanDao.Properties.Flag.eq(flag));
        List<ColourBean> list = qb.list();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public List<ColourBean> queryTimeAll() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ColourBeanDao downInfoDao = daoSession.getColourBeanDao();
        QueryBuilder<ColourBean> qb = downInfoDao.queryBuilder();
        return qb.list();
    }
}
