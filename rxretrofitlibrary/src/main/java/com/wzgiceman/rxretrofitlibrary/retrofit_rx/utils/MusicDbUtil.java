package com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.RxRetrofitApp;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoMaster;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoSession;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.FolderBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MusicListBeanDao;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MusicListSong;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MusicListSongDao;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MusicListSong;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MusicListSongDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhaifeng on 2017/4/6.
 */

public class MusicDbUtil {
    private static MusicDbUtil db;
    private final static String dbName = "music_db";
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;


    public MusicDbUtil() {
        context = RxRetrofitApp.getApplication();
        openHelper = new DaoMaster.DevOpenHelper(context, dbName);

    }


    /**
     * 获取单例
     *
     * @return
     */
    public static MusicDbUtil getInstance() {
        if (db == null) {
            synchronized (MusicDbUtil.class) {
                if (db == null) {
                    db = new MusicDbUtil();
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


    public void saveTime(MusicListSong info) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MusicListSongDao downInfoDao = daoSession.getMusicListSongDao();
        downInfoDao.insert(info);
    }

    public void updateTime(MusicListSong info) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MusicListSongDao downInfoDao = daoSession.getMusicListSongDao();
        downInfoDao.update(info);
    }

    public void deleteTime(long id) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MusicListSongDao downInfoDao = daoSession.getMusicListSongDao();
        downInfoDao.deleteByKey(id);
    }

    public void deleteAll(){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MusicListSongDao downInfoDao = daoSession.getMusicListSongDao();
        downInfoDao.deleteAll();
    }


    public List<MusicListSong> queryTimeBy(int flag) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MusicListSongDao downInfoDao = daoSession.getMusicListSongDao();
        QueryBuilder<MusicListSong> qb = downInfoDao.queryBuilder();
        qb.where(MusicListSongDao.Properties.Flag.eq(flag));
        List<MusicListSong> list = qb.list();
        if (list.isEmpty()) {
            return new ArrayList<>();
        } else {
            return list;
        }
    }

    public List<MusicListSong> queryTimeAll() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MusicListSongDao downInfoDao = daoSession.getMusicListSongDao();
        QueryBuilder<MusicListSong> qb = downInfoDao.queryBuilder();
        return qb.list();
    }


}
