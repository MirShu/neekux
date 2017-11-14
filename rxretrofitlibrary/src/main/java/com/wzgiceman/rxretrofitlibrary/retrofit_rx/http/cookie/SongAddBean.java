package com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xiaomingbai on 2017/4/12 17:12
 * Function 最爱歌曲分类列表
 */
@Entity
public class SongAddBean {
    @Id(autoincrement = true)
    private Long db_id;
    private String name;
    private String mode;
    private String submode;
    private String describe;
    private int song_id;
    private boolean isFavorite;
    private byte[] icon_array;
    private int fileId;
    @Generated(hash = 740213483)
    public SongAddBean(Long db_id, String name, String mode, String submode,
            String describe, int song_id, boolean isFavorite, byte[] icon_array,
            int fileId) {
        this.db_id = db_id;
        this.name = name;
        this.mode = mode;
        this.submode = submode;
        this.describe = describe;
        this.song_id = song_id;
        this.isFavorite = isFavorite;
        this.icon_array = icon_array;
        this.fileId = fileId;
    }
    @Generated(hash = 1855488520)
    public SongAddBean() {
    }
    public Long getDb_id() {
        return this.db_id;
    }
    public void setDb_id(Long db_id) {
        this.db_id = db_id;
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
    public String getDescribe() {
        return this.describe;
    }
    public void setDescribe(String describe) {
        this.describe = describe;
    }
    public int getSong_id() {
        return this.song_id;
    }
    public void setSong_id(int song_id) {
        this.song_id = song_id;
    }
    public boolean getIsFavorite() {
        return this.isFavorite;
    }
    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
    public byte[] getIcon_array() {
        return this.icon_array;
    }
    public void setIcon_array(byte[] icon_array) {
        this.icon_array = icon_array;
    }
    public int getFileId() {
        return this.fileId;
    }
    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

}
