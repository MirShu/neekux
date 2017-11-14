package com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xuhaifeng on 2017/4/6.
 */

@Entity
public class TimeBean {

    @Id(autoincrement = true)
    private Long time_id;
    private int hours;
    private int minutes;
    private String repeat;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;
    private String lighttune;
    private byte[] data;
    private boolean sleep;
    private boolean turnon;
    private String brightness;
    private String voice;
    private boolean isturn;
    @Generated(hash = 287980157)
    public TimeBean(Long time_id, int hours, int minutes, String repeat,
            boolean monday, boolean tuesday, boolean wednesday, boolean thursday,
            boolean friday, boolean saturday, boolean sunday, String lighttune,
            byte[] data, boolean sleep, boolean turnon, String brightness,
            String voice, boolean isturn) {
        this.time_id = time_id;
        this.hours = hours;
        this.minutes = minutes;
        this.repeat = repeat;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        this.lighttune = lighttune;
        this.data = data;
        this.sleep = sleep;
        this.turnon = turnon;
        this.brightness = brightness;
        this.voice = voice;
        this.isturn = isturn;
    }
    @Generated(hash = 1700076046)
    public TimeBean() {
    }
    public Long getTime_id() {
        return this.time_id;
    }
    public void setTime_id(Long time_id) {
        this.time_id = time_id;
    }
    public int getHours() {
        return this.hours;
    }
    public void setHours(int hours) {
        this.hours = hours;
    }
    public int getMinutes() {
        return this.minutes;
    }
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
    public String getRepeat() {
        return this.repeat;
    }
    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }
    public boolean getMonday() {
        return this.monday;
    }
    public void setMonday(boolean monday) {
        this.monday = monday;
    }
    public boolean getTuesday() {
        return this.tuesday;
    }
    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }
    public boolean getWednesday() {
        return this.wednesday;
    }
    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }
    public boolean getThursday() {
        return this.thursday;
    }
    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }
    public boolean getFriday() {
        return this.friday;
    }
    public void setFriday(boolean friday) {
        this.friday = friday;
    }
    public boolean getSaturday() {
        return this.saturday;
    }
    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }
    public boolean getSunday() {
        return this.sunday;
    }
    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }
    public String getLighttune() {
        return this.lighttune;
    }
    public void setLighttune(String lighttune) {
        this.lighttune = lighttune;
    }
    public byte[] getData() {
        return this.data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    public boolean getSleep() {
        return this.sleep;
    }
    public void setSleep(boolean sleep) {
        this.sleep = sleep;
    }
    public boolean getTurnon() {
        return this.turnon;
    }
    public void setTurnon(boolean turnon) {
        this.turnon = turnon;
    }
    public String getBrightness() {
        return this.brightness;
    }
    public void setBrightness(String brightness) {
        this.brightness = brightness;
    }
    public String getVoice() {
        return this.voice;
    }
    public void setVoice(String voice) {
        this.voice = voice;
    }
    public boolean getIsturn() {
        return this.isturn;
    }
    public void setIsturn(boolean isturn) {
        this.isturn = isturn;
    }




}
