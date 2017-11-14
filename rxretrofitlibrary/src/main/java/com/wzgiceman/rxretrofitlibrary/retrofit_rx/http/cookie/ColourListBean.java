package com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zhaoyang on 2017/6/1.
 */
@Entity
public class ColourListBean {
    @Id(autoincrement = true)
    private Long co_id;
    private String sn;
    private String name;
    private String mode;
    private String submode;
    private byte[] checkcode;
    private long size;
    private int type;
    private boolean favor;
    private int duration;
    private String describe;
    private byte[] icon;
    private int flag;
    private int songlight_verno;
    private String total_songlights;
    @Generated(hash = 1679112638)
    public ColourListBean(Long co_id, String sn, String name, String mode,
            String submode, byte[] checkcode, long size, int type, boolean favor,
            int duration, String describe, byte[] icon, int flag,
            int songlight_verno, String total_songlights) {
        this.co_id = co_id;
        this.sn = sn;
        this.name = name;
        this.mode = mode;
        this.submode = submode;
        this.checkcode = checkcode;
        this.size = size;
        this.type = type;
        this.favor = favor;
        this.duration = duration;
        this.describe = describe;
        this.icon = icon;
        this.flag = flag;
        this.songlight_verno = songlight_verno;
        this.total_songlights = total_songlights;
    }
    @Generated(hash = 1727480825)
    public ColourListBean() {
    }
    public Long getCo_id() {
        return this.co_id;
    }
    public void setCo_id(Long co_id) {
        this.co_id = co_id;
    }
    public String getSn() {
        return this.sn;
    }
    public void setSn(String sn) {
        this.sn = sn;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMode() {
        return this.mode;
    }
    public void setMode(String mode) {
        this.mode = mode;
    }
    public String getSubmode() {
        return this.submode;
    }
    public void setSubmode(String submode) {
        this.submode = submode;
    }
    public byte[] getCheckcode() {
        return this.checkcode;
    }
    public void setCheckcode(byte[] checkcode) {
        this.checkcode = checkcode;
    }
    public long getSize() {
        return this.size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public boolean getFavor() {
        return this.favor;
    }
    public void setFavor(boolean favor) {
        this.favor = favor;
    }
    public int getDuration() {
        return this.duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public String getDescribe() {
        return this.describe;
    }
    public void setDescribe(String describe) {
        this.describe = describe;
    }
    public byte[] getIcon() {
        return this.icon;
    }
    public void setIcon(byte[] icon) {
        this.icon = icon;
    }
    public int getFlag() {
        return this.flag;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }
    public int getSonglight_verno() {
        return this.songlight_verno;
    }
    public void setSonglight_verno(int songlight_verno) {
        this.songlight_verno = songlight_verno;
    }
    public String getTotal_songlights() {
        return this.total_songlights;
    }
    public void setTotal_songlights(String total_songlights) {
        this.total_songlights = total_songlights;
    }
}
