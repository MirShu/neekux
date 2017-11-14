package meekux.grandar.com.meekuxpjxroject.entity;

import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by liuqk on 2017/5/24.
 */

public class Sonsorroom {
    private String roomname;
    private String roomnameadd;
    private ImageView speech;
    private ImageView see;
    private ArrayList<String> sn;

    public Sonsorroom(String roomname, String roomnameadd,ArrayList<String> sn) {
        this.roomname = roomname;
        this.roomnameadd = roomnameadd;
        this.sn= sn;
    }

    public Sonsorroom(String roomname, String roomnameadd, ImageView speech, ImageView see, ArrayList<String> sn) {
        this.sn = sn;
        this.roomname = roomname;
        this.roomnameadd = roomnameadd;
        this.speech = speech;
        this.see = see;
    }

    @Override
    public String toString() {
        return "Sonsorroom{" +
                "roomname='" + roomname + '\'' +
                ", roomnameadd='" + roomnameadd + '\'' +
                ", speech=" + speech +
                ", see=" + see +
                '}';
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public String getRoomnameadd() {
        return roomnameadd;
    }

    public void setRoomnameadd(String roomnameadd) {
        this.roomnameadd = roomnameadd;
    }

    public ImageView getSpeech() {
        return speech;
    }

    public void setSpeech(ImageView speech) {
        this.speech = speech;
    }

    public ImageView getSee() {
        return see;
    }

    public void setSee(ImageView see) {
        this.see = see;
    }
}
