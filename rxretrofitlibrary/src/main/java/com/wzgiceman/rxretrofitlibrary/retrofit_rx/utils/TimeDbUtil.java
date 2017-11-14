package com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.RxRetrofitApp;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoMaster;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoSession;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.CookieResulteDao;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.TimeBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.TimeBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by xuhaifeng on 2017/4/6.
 */

public class TimeDbUtil {
    private static TimeDbUtil db;
    private final static String dbName = "colck_db";
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;


    public TimeDbUtil() {
        context= RxRetrofitApp.getApplication();
        openHelper = new DaoMaster.DevOpenHelper(context, dbName);

    }


    /**
     * 获取单例
     * @return
     */
    public static TimeDbUtil getInstance() {
        if (db == null) {
            synchronized (TimeDbUtil.class) {
                if (db == null) {
                    db = new TimeDbUtil();
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


    public void saveTime(TimeBean info){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        TimeBeanDao downInfoDao = daoSession.getTimeBeanDao();
        downInfoDao.insert(info);
    }

    public void updateTime(TimeBean info){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        TimeBeanDao downInfoDao = daoSession.getTimeBeanDao();
        downInfoDao.update(info);
    }

    public void deleteTime(long id){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        TimeBeanDao downInfoDao = daoSession.getTimeBeanDao();
        downInfoDao.deleteByKey(id);
    }


    public TimeBean queryTimeBy(String  url) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        TimeBeanDao downInfoDao = daoSession.getTimeBeanDao();
        QueryBuilder<TimeBean> qb = downInfoDao.queryBuilder();
        qb.where(CookieResulteDao.Properties.Url.eq(url));
        List<TimeBean> list = qb.list();
        if(list.isEmpty()){
            return null;
        }else{
            return list.get(0);
        }
    }

    public List<TimeBean> queryTimeAll() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        TimeBeanDao downInfoDao = daoSession.getTimeBeanDao();
        QueryBuilder<TimeBean> qb = downInfoDao.queryBuilder();
        return qb.list();
    }
}
