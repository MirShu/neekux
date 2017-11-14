package com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.RxRetrofitApp;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoMaster;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoSession;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.CookieResulteDao;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by xuhaifeng on 2017/4/6.
 */

public class DeviceDbUtil {
    private static DeviceDbUtil db;
    private final static String dbName = "devices_db";
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;


    public DeviceDbUtil() {
        context = RxRetrofitApp.getApplication();
        openHelper = new DaoMaster.DevOpenHelper(context, dbName);

    }


    /**
     * 获取单例
     *
     * @return
     */
    public static DeviceDbUtil getInstance() {
        if (db == null) {
            synchronized (DeviceDbUtil.class) {
                if (db == null) {
                    db = new DeviceDbUtil();
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


    public void saveTime(MyDeviceBean info) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MyDeviceBeanDao downInfoDao = daoSession.getMyDeviceBeanDao();
        downInfoDao.insert(info);
    }

    public void updateTime(MyDeviceBean info) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MyDeviceBeanDao downInfoDao = daoSession.getMyDeviceBeanDao();
        downInfoDao.update(info);
    }

    public void deleteTime(long id) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MyDeviceBeanDao downInfoDao = daoSession.getMyDeviceBeanDao();
        downInfoDao.deleteByKey(id);
    }

    public void deleteAll(){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MyDeviceBeanDao downInfoDao = daoSession.getMyDeviceBeanDao();
        downInfoDao.deleteAll();
    }


    public MyDeviceBean queryTimeBy(String Sn) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MyDeviceBeanDao downInfoDao = daoSession.getMyDeviceBeanDao();
        QueryBuilder<MyDeviceBean> qb = downInfoDao.queryBuilder();
        qb.where(MyDeviceBeanDao.Properties.Sn.eq(Sn));
        List<MyDeviceBean> list = qb.list();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public List<MyDeviceBean> queryTimeAll() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MyDeviceBeanDao downInfoDao = daoSession.getMyDeviceBeanDao();
        QueryBuilder<MyDeviceBean> qb = downInfoDao.queryBuilder();
        return qb.list();
    }
    public String  queryPathBy(String Sn) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MyDeviceBeanDao downInfoDao = daoSession.getMyDeviceBeanDao();
        QueryBuilder<MyDeviceBean> qb = downInfoDao.queryBuilder();
        qb.where(MyDeviceBeanDao.Properties.Sn.eq(Sn));
        List<MyDeviceBean> list = qb.list();
        if (list.isEmpty()) {
            return "";
        } else {
            return list.get(0).getPath();
        }
    }

}
