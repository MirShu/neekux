package meekux.grandar.com.meekuxpjxroject.adapter;

import java.util.ArrayList;

/**
 * Created by liuqk on 2017/5/19.
 */

 public class Room {
    private ArrayList<String> strings;
    private String roomName;

    public Room(String roomName){
        this.roomName = roomName;
    }

    public Room(ArrayList<String> strings, String roomName) {
        this.strings = strings;
        this.roomName = roomName;
    }

    public ArrayList<String> getStrings() {
        return strings;
    }

    public void setStrings(ArrayList<String> strings) {
        this.strings = strings;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @Override
    public String toString() {
        return "Room{" +
                "strings=" + strings +
                ", roomName='" + roomName + '\'' +
                '}';
    }
}
