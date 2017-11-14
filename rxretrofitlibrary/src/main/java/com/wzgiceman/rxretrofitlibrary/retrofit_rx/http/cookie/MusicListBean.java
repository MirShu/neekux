package com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xuhaifeng on 2017/4/7.
 */

@Entity
public class MusicListBean {

    @Id(autoincrement = true)
    private Long id;
    private String name;
    private int count;
    private int imgId;
    @Generated(hash = 1838815723)
    public MusicListBean(Long id, String name, int count, int imgId) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.imgId = imgId;
    }
    @Generated(hash = 1918926376)
    public MusicListBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getCount() {
        return this.count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public int getImgId() {
        return this.imgId;
    }
    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
}
