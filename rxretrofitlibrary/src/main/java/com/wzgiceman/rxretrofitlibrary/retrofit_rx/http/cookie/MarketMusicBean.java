package com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zhaoyang on 2017/5/28.
 */
@Entity
public class MarketMusicBean {
    @Id(autoincrement = true)
    private Long sid;
    private String sn;
    private String ip;
    private String name;
    private String date;
    private String path;
    private String model;
    @Generated(hash = 1654889023)
    public MarketMusicBean(Long sid, String sn, String ip, String name, String date,
            String path, String model) {
        this.sid = sid;
        this.sn = sn;
        this.ip = ip;
        this.name = name;
        this.date = date;
        this.path = path;
        this.model = model;
    }
    @Generated(hash = 1635376454)
    public MarketMusicBean() {
    }
    public Long getSid() {
        return this.sid;
    }
    public void setSid(Long sid) {
        this.sid = sid;
    }
    public String getSn() {
        return this.sn;
    }
    public void setSn(String sn) {
        this.sn = sn;
    }
    public String getIp() {
        return this.ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getModel() {
        return this.model;
    }
    public void setModel(String model) {
        this.model = model;
    }

}
