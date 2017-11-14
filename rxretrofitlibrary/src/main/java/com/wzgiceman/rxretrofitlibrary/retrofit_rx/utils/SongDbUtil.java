package com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.RxRetrofitApp;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoMaster;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoSession;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.CookieResulteDao;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBeanDao;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.SongAddBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.SongAddBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhaifeng on 2017/4/6.
 * 自选列表分类详情--->列表
 */

public class SongDbUtil {
    private static SongDbUtil db;
    private final static String dbName = "songs_db";
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;


    public SongDbUtil() {
        context = RxRetrofitApp.getApplication();
        openHelper = new DaoMaster.DevOpenHelper(context, dbName);

    }


    /**
     * 获取单例
     *
     * @return
     */
    public static SongDbUtil getInstance() {
        if (db == null) {
            synchronized (SongDbUtil.class) {
                if (db == null) {
                    db = new SongDbUtil();
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


    public void saveTime(SongAddBean info) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SongAddBeanDao downInfoDao = daoSession.getSongAddBeanDao();
        downInfoDao.insert(info);
    }

    public void updateTime(SongAddBean info) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SongAddBeanDao downInfoDao = daoSession.getSongAddBeanDao();
        downInfoDao.update(info);
    }

    public void deleteTime(long id) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SongAddBeanDao downInfoDao = daoSession.getSongAddBeanDao();
        downInfoDao.deleteByKey(id);
    }


    public SongAddBean queryTimeBy(String url) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SongAddBeanDao downInfoDao = daoSession.getSongAddBeanDao();
        QueryBuilder<SongAddBean> qb = downInfoDao.queryBuilder();
        qb.where(CookieResulteDao.Properties.Url.eq(url));
        List<SongAddBean> list = qb.list();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public List<SongAddBean> queryTimeAllByValue(int fileId){
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SongAddBeanDao downInfoDao = daoSession.getSongAddBeanDao();
        QueryBuilder<SongAddBean> qb = downInfoDao.queryBuilder();
        qb.where(SongAddBeanDao.Properties.FileId.eq(fileId));
        List<SongAddBean> list = qb.list();
        if (list.isEmpty()) {
            return new ArrayList<>();
        } else {
            return list;
        }
    }

    public List<SongAddBean> queryTimeAll() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SongAddBeanDao downInfoDao = daoSession.getSongAddBeanDao();
        QueryBuilder<SongAddBean> qb = downInfoDao.queryBuilder();
        return qb.list();
    }
}
