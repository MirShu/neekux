package meekux.grandar.com.meekuxpjxroject;

import android.app.Activity;
import android.app.Application;
import android.os.Environment;

import com.iflytek.cloud.SpeechUtility;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.RxRetrofitApp;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.FolderBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MusicListSong;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.FolderUtil;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.MusicDbUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import meekux.grandar.com.meekuxpjxroject.songdata.database.MyUtil;
import meekux.grandar.com.meekuxpjxroject.utils.SharedPreferencesUtils;
import meekux.grandar.com.meekuxpjxroject.utils.ToastUtils;


/**
 * Created by baixiaoming on 2017/3/8 18:34
 * Function 全局应用，工具类初始化
 */
public class GlobalApplication extends Application {
    public static String EXPORTFILEPATH = Environment.getExternalStorageDirectory() + "/NEW_MEEKUX/Candle/";
    File filenewFile = new File(EXPORTFILEPATH);
    private boolean hasConnect;
    List<FolderBean> list;
    private ArrayList<MusicListSong> data;
    private ArrayList<String> Stringss;

    public ArrayList<String> getStringss() {
        return Stringss;
    }

    public void setStringss(ArrayList<String> stringss) {
        Stringss = stringss;
    }

    public ArrayList<MusicListSong> getData() {
        return data;
    }

    public void setData(ArrayList<MusicListSong> data) {
        this.data = data;
    }

    @Override
    public void onCreate() {
        SpeechUtility.createUtility(GlobalApplication.this, "appid=" + getString(R.string.app_id));
        super.onCreate();
        RxRetrofitApp.init(this);
        SharedPreferencesUtils.init(getApplicationContext());
        List<MusicListSong> list = MusicDbUtil.getInstance().queryTimeAll();
        if (list.size() == 0) {
            initUtils();
        }
        initSongLightFile();
    }

    private void initUtils() {
        SharedPreferencesUtils.init(getApplicationContext());
        ToastUtils.init(getApplicationContext());
        new Thread(() -> initData()).start();
    }

    private int playCount;
    private boolean stopSendFrame = false;
    private List<Activity> mList = new LinkedList<>();
    private static GlobalApplication instance;

    public GlobalApplication() {
    }

    public synchronized static GlobalApplication getInstance() {
        if (null == instance) {
            instance = new GlobalApplication();
        }
        return instance;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    private final Object mControllersListLock = new Object();
    private Map<String, Socket> mDevicesList = new HashMap<>();

    public boolean isStopSendFrame() {
        return stopSendFrame;
    }

    public void setStopSendFrame(boolean stopSendFrame) {
        this.stopSendFrame = stopSendFrame;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public void removeDevice(String sn) {
        Iterator iterator = mDevicesList.keySet().iterator();
        while (iterator.hasNext()) {
            try {
                String key = (String) iterator.next();
                if (sn.equals(key)) {
                    mDevicesList.remove(key);
                }
            } catch (Exception e) {
            }
        }
    }

    //添加设备
    public void addDevice(String sn, Socket socket) {
        synchronized (mControllersListLock) {
            mDevicesList.put(sn, socket);
        }
    }

    public int getDeviceSize() {
        synchronized (mControllersListLock) {
            return mDevicesList.size();
        }
    }

    public Socket getDevice(String sn) {
        synchronized (mControllersListLock) {
            return mDevicesList.get(sn);
        }
    }

    public void setConnect(boolean bool) {
        hasConnect = bool;
    }

    public boolean getConnect() {
        return hasConnect;
    }

    private void initData() {
        String fileDir = EXPORTFILEPATH;
        FolderBean bean = new FolderBean();
        bean.setPath(fileDir);
        bean.setFlag(1);
        bean.setPath_name("璀璨霓虹");
        bean.setImg(R.mipmap.green_icon);
        bean.setTitle("美好生活");
        bean.setImg_icon(R.mipmap.music_icon);
        FolderUtil.getInstance().saveTime(bean);
        getFiles(1, bean.getPath());
    }

    private static void getFiles(int flag, String path) {
        try {
            File[] allFiles = new File(path).listFiles();
            for (int i = 0; i < allFiles.length; i++) {
                File file = allFiles[i];
                if (file.isFile()) {
                    String fileS = file.getName().substring(
                            file.getName().length() - 4);
                    if (fileS.toLowerCase().equals(".ils")) {
                        MusicListSong bean = new MusicListSong();
                        bean.setName(file.getName());
                        bean.setCheckcode(MyUtil.getSongLightMd5(file.getAbsolutePath()));
                        bean.setSize(file.length());
                        bean.setType(MyUtil.getSongLightType(file.getAbsolutePath()));
                        bean.setFavor(false);
                        bean.setFlag(flag);
                        bean.setDuration(MyUtil.getSongLightTime(file.getAbsolutePath()));
                        bean.setDescribe(MyUtil.getSongLightDescr(file.getAbsolutePath()));
                        bean.setIcon(MyUtil.getSongLightIcon(file.getAbsolutePath()));
                        bean.setSonglight_verno(MyUtil.getSongLightVerno(file.getAbsolutePath()));
                        MusicDbUtil.getInstance().saveTime(bean);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void copyBigDataToSD(String strOutFileName) throws IOException {
        filenewFile.mkdirs();
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(EXPORTFILEPATH + strOutFileName);
        myInput = this.getAssets().open(strOutFileName);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }
        myOutput.flush();
        myInput.close();
        myOutput.close();
    }


    public void initSongLightFile() {
        try {
            copyBigDataToSD("manul01.ils");
            copyBigDataToSD("manul02.ils");
            copyBigDataToSD("manul03.ils");
            copyBigDataToSD("manul04.ils");
            copyBigDataToSD("manul05.ils");
            copyBigDataToSD("manul06.ils");
            copyBigDataToSD("manul07.ils");
            copyBigDataToSD("manul08.ils");
            copyBigDataToSD("manul09.ils");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}