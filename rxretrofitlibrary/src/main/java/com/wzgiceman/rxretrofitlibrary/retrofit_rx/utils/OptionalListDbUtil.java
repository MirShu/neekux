package com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.RxRetrofitApp;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoMaster;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoSession;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.CookieResulteDao;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MusicListBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MusicListBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by xuhaifeng on 2017/4/6.
 */

public class OptionalListDbUtil {
    private static OptionalListDbUtil db;
    private final static String dbName = "optionalList_db";
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;


    public OptionalListDbUtil() {
        context= RxRetrofitApp.getApplication();
        openHelper = new DaoMaster.DevOpenHelper(context, dbName);

    }


    /**
     * 获取单例
     * @return
     */
    public static OptionalListDbUtil getInstance() {
        if (db == null) {
            synchronized (OptionalListDbUtil.class) {
                if (db == null) {
                    db = new OptionalListDbUtil();
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


    public void saveTime(MusicListBean info){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MusicListBeanDao downInfoDao = daoSession.getMusicListBeanDao();
        downInfoDao.insert(info);
    }

    public void updateTime(MusicListBean info){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MusicListBeanDao downInfoDao = daoSession.getMusicListBeanDao();
        downInfoDao.update(info);
    }

    public void deleteTime(long id){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MusicListBeanDao downInfoDao = daoSession.getMusicListBeanDao();
        downInfoDao.deleteByKey(id);
    }


    public MusicListBean queryTimeBy(String  url) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MusicListBeanDao downInfoDao = daoSession.getMusicListBeanDao();
        QueryBuilder<MusicListBean> qb = downInfoDao.queryBuilder();
        qb.where(CookieResulteDao.Properties.Url.eq(url));
        List<MusicListBean> list = qb.list();
        if(list.isEmpty()){
            return null;
        }else{
            return list.get(0);
        }
    }

    public List<MusicListBean> queryTimeAll() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MusicListBeanDao downInfoDao = daoSession.getMusicListBeanDao();
        QueryBuilder<MusicListBean> qb = downInfoDao.queryBuilder();
        return qb.list();
    }
}
