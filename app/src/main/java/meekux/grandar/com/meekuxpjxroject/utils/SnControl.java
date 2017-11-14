package meekux.grandar.com.meekuxpjxroject.utils;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.DeviceDbUtil;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import meekux.grandar.com.meekuxpjxroject.GlobalApplication;

/**
 * Created by liuqk on 2017/5/22.
 */

public class SnControl {
    private SnControl() {}
    private static SnControl single=null;
    public static SnControl getInstance() {
        if (single == null) {
            single = new SnControl();
        }
        return single;
    }
    private static HashMap<String, ArrayList<String>> snmap = new HashMap<>();
    private static ArrayList<String> toplight;
    private static ArrayList<String> facelight;
    private static ArrayList<String> longlight;
    private static ArrayList<String> alllight;
    public HashMap<String, ArrayList<String>> getSnmap(){
        toplight = new ArrayList<>();
        facelight = new ArrayList<>();
        longlight = new ArrayList<>();
        alllight = new ArrayList<>();
        List<MyDeviceBean> myDeviceBeen = DeviceDbUtil.getInstance().queryTimeAll();
        if (myDeviceBeen != null){
            for (MyDeviceBean myDeviceBean : myDeviceBeen) {
                String sn = myDeviceBean.getSn();
                Socket device = GlobalApplication.getInstance().getDevice(sn);
                if ( device != null){
                        //
                    alllight.add(sn);
                    if (myDeviceBean.getSn().substring(0,1).equals("P")&&myDeviceBean.getSn().substring(3,4).equals("A")) {
                        toplight.add(sn);
                    }
                        //
                    if (myDeviceBean.getSn().substring(0,1).equals("P")&&myDeviceBean.getSn().substring(3,4).equals("B")) {
                        facelight.add(sn);
                    }
                        //
                   if (!(myDeviceBean.getSn().contains(""))&&!(myDeviceBean.getSn().contains("B"))){
                        longlight.add(sn);
                    }
                }
            }
            //未来商场
            snmap.put("toplight", toplight);
            //未来教室
            snmap.put("facelight", facelight);
            //未来客厅
            snmap.put("longlight", longlight);
            //所有灯的类型
            snmap.put("alllight", alllight);
        }
        return snmap;
    }
    public void deleteAll(){
            snmap.clear();
    }
}
