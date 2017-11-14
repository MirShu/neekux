package com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.RxRetrofitApp;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoMaster;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoSession;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.FolderBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.FolderBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by zhaoyang on 2017/5/26.
 */

public class FolderUtil {
    private static FolderUtil db;
    private final static String dbName = "folder_db";
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;


    public FolderUtil() {
        context = RxRetrofitApp.getApplication();
        openHelper = new DaoMaster.DevOpenHelper(context, dbName);

    }


    /**
     * 获取单例
     *
     * @return
     */
    public static FolderUtil getInstance() {
        if (db == null) {
            synchronized (FolderUtil.class) {
                if (db == null) {
                    db = new FolderUtil();
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


    public void saveTime(FolderBean info) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        FolderBeanDao downInfoDao = daoSession.getFolderBeanDao();
        downInfoDao.insert(info);
    }

    public void updateTime(FolderBean info) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        FolderBeanDao downInfoDao = daoSession.getFolderBeanDao();
        downInfoDao.update(info);
    }

    public void deleteTime(long id) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        FolderBeanDao downInfoDao = daoSession.getFolderBeanDao();
        downInfoDao.deleteByKey(id);
    }

    public void deleteAll(){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        FolderBeanDao downInfoDao = daoSession.getFolderBeanDao();
        downInfoDao.deleteAll();
    }


    public FolderBean queryTimeBy(int flag) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        FolderBeanDao downInfoDao = daoSession.getFolderBeanDao();
        QueryBuilder<FolderBean> qb = downInfoDao.queryBuilder();
        qb.where(FolderBeanDao.Properties.Flag.eq(flag));
        List<FolderBean> list = qb.list();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public List<FolderBean> queryTimeAll() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        FolderBeanDao downInfoDao = daoSession.getFolderBeanDao();
        QueryBuilder<FolderBean> qb = downInfoDao.queryBuilder();
        return qb.list();
    }
}
