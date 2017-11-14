package com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zhaoyang on 2017/6/1.
 */
@Entity
public class ColourBean {
    @Id(autoincrement = true)
    private Long co_id;
    private String path;
    private String path_name;
    private int flag;
    private int img;
    private String title;
    private int img_icon;
    @Generated(hash = 1490840016)
    public ColourBean(Long co_id, String path, String path_name, int flag, int img,
            String title, int img_icon) {
        this.co_id = co_id;
        this.path = path;
        this.path_name = path_name;
        this.flag = flag;
        this.img = img;
        this.title = title;
        this.img_icon = img_icon;
    }
    @Generated(hash = 1405423767)
    public ColourBean() {
    }
    public Long getCo_id() {
        return this.co_id;
    }
    public void setCo_id(Long co_id) {
        this.co_id = co_id;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getPath_name() {
        return this.path_name;
    }
    public void setPath_name(String path_name) {
        this.path_name = path_name;
    }
    public int getFlag() {
        return this.flag;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }
    public int getImg() {
        return this.img;
    }
    public void setImg(int img) {
        this.img = img;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getImg_icon() {
        return this.img_icon;
    }
    public void setImg_icon(int img_icon) {
        this.img_icon = img_icon;
    }
}
