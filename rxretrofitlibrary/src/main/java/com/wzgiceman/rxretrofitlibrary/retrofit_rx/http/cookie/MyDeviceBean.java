package com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xiaomingbai on 2017/4/10 16:50
 * Function 设备基本信息
 */
@Entity
public class MyDeviceBean {
    @Id(autoincrement = true)
    private Long sid;
    private String sn;
    private String ip;
    private String name;
    private String date;
    private String path;
    private String model;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Generated(hash = 656753236)
    private int version;
    public MyDeviceBean(Long sid, String sn, String ip, String name, String date,
            String path, String model) {
        this.sid = sid;
        this.sn = sn;
        this.ip = ip;
        this.name = name;
        this.date = date;
        this.path = path;
        this.model = model;
    }
    @Generated(hash = 384286097)
    public MyDeviceBean() {
    }

    @Generated(hash = 1919701379)
    public MyDeviceBean(Long sid, String sn, String ip, String name, String date,
            String path, String model, int version) {
        this.sid = sid;
        this.sn = sn;
        this.ip = ip;
        this.name = name;
        this.date = date;
        this.path = path;
        this.model = model;
        this.version = version;
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
