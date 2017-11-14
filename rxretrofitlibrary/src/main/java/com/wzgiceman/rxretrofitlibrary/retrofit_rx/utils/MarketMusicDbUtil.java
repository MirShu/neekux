package com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.RxRetrofitApp;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoMaster;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoSession;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MarketMusicBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MarketMusicBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoyang on 2017/5/28.
 */

public class MarketMusicDbUtil {
    private static MarketMusicDbUtil db;
    private final static String dbName = "market_db";
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;


    public MarketMusicDbUtil() {
        context = RxRetrofitApp.getApplication();
        openHelper = new DaoMaster.DevOpenHelper(context, dbName);

    }


    /**
     * 获取单例
     *
     * @return
     */
    public static MarketMusicDbUtil getInstance() {
        if (db == null) {
            synchronized (MarketMusicDbUtil.class) {
                if (db == null) {
                    db = new MarketMusicDbUtil();
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


    public void saveTime(MarketMusicBean info) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MarketMusicBeanDao downInfoDao = daoSession.getMarketMusicBeanDao();
        downInfoDao.insert(info);
    }

    public void updateTime(MarketMusicBean info) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MarketMusicBeanDao downInfoDao = daoSession.getMarketMusicBeanDao();
        downInfoDao.update(info);
    }

    public void deleteTime(long id) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MarketMusicBeanDao downInfoDao = daoSession.getMarketMusicBeanDao();
        downInfoDao.deleteByKey(id);
    }

    public void deleteAll(){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MarketMusicBeanDao downInfoDao = daoSession.getMarketMusicBeanDao();
        downInfoDao.deleteAll();
    }


    public MarketMusicBean queryTimeBy(String name) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MarketMusicBeanDao downInfoDao = daoSession.getMarketMusicBeanDao();
        QueryBuilder<MarketMusicBean> qb = downInfoDao.queryBuilder();
        qb.where(MarketMusicBeanDao.Properties.Name.eq(name));
        List<MarketMusicBean> list = qb.list();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public List<MarketMusicBean> queryTimeAll() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MarketMusicBeanDao downInfoDao = daoSession.getMarketMusicBeanDao();
        QueryBuilder<MarketMusicBean> qb = downInfoDao.queryBuilder();
        return qb.list();
    }
}
