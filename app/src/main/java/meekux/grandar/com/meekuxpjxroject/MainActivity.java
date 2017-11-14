package meekux.grandar.com.meekuxpjxroject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.socks.library.KLog;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.DeviceDbUtil;

import java.util.ArrayList;

import meekux.grandar.com.meekuxpjxroject.fragment.ControlFragment;
import meekux.grandar.com.meekuxpjxroject.fragment.HomeFragment;
import meekux.grandar.com.meekuxpjxroject.fragment.LightFragment;
import meekux.grandar.com.meekuxpjxroject.fragment.ManulControlFragment;
import meekux.grandar.com.meekuxpjxroject.fragment.MusicFragment;
import meekux.grandar.com.meekuxpjxroject.fragment.SensorFragment;
import meekux.grandar.com.meekuxpjxroject.songdata.database.GrandarUtils;
import meekux.grandar.com.meekuxpjxroject.utils.NetSocket;
import meekux.grandar.com.meekuxpjxroject.utils.SProject;
import meekux.grandar.com.meekuxpjxroject.utils.SharedPutils;
import meekux.grandar.com.meekuxpjxroject.utils.Utility;

public class MainActivity extends Activity {
    public Fragment homeFragment;//首页
    public Fragment lightcontrolFragment;//布灯
    public SensorFragment sensorFragment;//传感器
    public Fragment musicFragment;//光曲编辑
    public Fragment controlFragment;//控制
    public ManulControlFragment manulcontrol;
    private long mExitTime;
    private RadioButton home;
    private RadioButton lightcontrol;
    private RadioButton control;
    private RadioButton music;
    private RadioButton sensor;
    private GrandarUtils.ServiceToken token;
    private RadioGroup radioGroup;
    public static int nProgressVolume = 60;
    public static int nProgressLight = 40;
    public SProject sproject = null;   //注意其构造函数
    private Boolean first = true;
    private ListView menu_listview;
    public static NetSocket netSocket = null;
    public Utility utility = null;
    public static Handler mMainHandler = null;
    private static Runnable runnableXintiao = null;   //发送心跳包的定时器
    int initStep = 0;
    long lastMsgTimeStamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inti();
        setContentView(R.layout.main);
        token = GrandarUtils.bindToService(this);
        init();
        initNetwork();
    }

    private void inti() {
//        SharedPreferences sp = getSharedPreferences("deviceSn", Context.MODE_PRIVATE);
//        String deviceSn = sp.getString("sn", null);
//

//        ArrayList<String> strings = new ArrayList<>();
//        strings.add("R5d0");
//        strings.add("L3ch");
//        strings.add("L7c0");
//        GlobalApplication.getInstance().setStringss(strings);

    }

    private void initNetwork() {
        //2:实用功能类初始化
        utility = new Utility(this);
        //3:项目初始化
        sproject = new SProject();   //注意其构造函数
        sproject.strUserName = "游客";
        sproject.level = 0;
        //4:心跳发送定时器
        runnableXintiao = new Runnable() {
            @Override
            public void run() {
                netSocket.sendXintiao();
                mMainHandler.postDelayed(this, 5000);
            }
        };

        //5:主消息循环句柄初始化
        mMainHandler = new Handler() {
            public void handleMessage(Message msg)      //内部消息循环函数，类似MFC消息处理机制
            {
                // Log.i("MainActivity", "Got an incoming message ----  "+ (String) msg.obj);

                switch (msg.what) {
                    case 1: {
                        String str = (String) msg.obj;
                        netSocket.processReplyMsg((String) msg.obj);
                    }
                    break;
                    case 0xa0: {
                        initStep = 1;
                        //   progressDialog = ProgressDialog.show(MainActivity.this, "Loading...", "Please wait...", true, false);
//                        progressDialog = new ProgressDialog(MainActivity.this);
//                        progressDialog.setMessage("正在连接服务器，请稍等...");
//                        progressDialog.setCancelable(false);
//                        progressDialog.setCanceledOnTouchOutside(false);
//                        progressDialog.show();

                        //               String strPrj = checkProjectHistory();   //判断上次运行是否已经连接项目

                        //strPrj = "珠海十字门";

//                        if(strPrj.equals("noProject")==true)
//                        {
                        netSocket.sendRequestProjectList();
                        //    }
//                        else
//                        {
                        sproject.name = "中国尊";
                        Message toMain = mMainHandler.obtainMessage();
                        toMain.what = 0xa1;
                        mMainHandler.sendMessage(toMain);       //通知主线程，可以请求播放列表和PK区域了

                        //   sendHistoryLoginInfo();   //发送历史登陆信息去登陆
//                        }
                    }
                    break;
                    case 0xa1: {
                        initStep = 2;

                        //  progressDialog.setMessage("正在获取PD场景播放数据，请稍等...");

                        setTitle(sproject.name);
                        netSocket.sendRequestPDList();
                    }
                    break;
                    case 0xa2: {
                        initStep = 3;

                        //  progressDialog.setMessage("正在获取PK开关灯数据，请稍等...");
                        netSocket.sendRequestPKList();
                    }
                    break;
                    case 0xb0: {
                        if (initStep != 4) {
                            initStep = 4;
                            //   progressDialog.dismiss();   //退出提示框

                            netSocket.sendLastChatMsgTimeStamp(lastMsgTimeStamp);

                            Log.i("MainActivity", "sendLastChatMsgTimeStamp");
                            //netSocket.sendXintiao();
                            mMainHandler.postDelayed(runnableXintiao, 5000);   //5000毫秒后触发心跳循环,保证接收历史消息
                        }
                    }
                }

                //    super.handleMessage(msg);
            }
        };

        //sdk 4.0后一定要加，权限相关,除了此处还需要在AndroidManifest.xml中添加相应权限语句,否则网络发送会报错
        //原因是4.0之后，网络发送不允许从activity主线程发送，需要另开线程或者服务
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());


        //6:网络控制类初始化
        netSocket = new NetSocket(this);

//        dbManager = new DBManager(MainActivity.this);
//
//        dbManager.readRecentlyMsg(sproject);

        int count = sproject.listMsg.size();
        if (count > 0) {
            for (int i = count - 1; i >= 0; i--)              //从最后一条记录开始判断
            {
                SProject.SProject_MSG msg = sproject.listMsg.get(i);
                //if(msg.dir==0)
                {
                    lastMsgTimeStamp = msg.timeStamp;
                    break;
                }
            }
        }
    }


    private void init() {

        selectItem();
        manulcontrol = new ManulControlFragment();
        radioGroup = findViewById(R.id.main_tab_group);
        home = findViewById(R.id.home);
        lightcontrol = findViewById(R.id.lightcontrol);
        control = findViewById(R.id.control);
        music = findViewById(R.id.music);
        sensor = findViewById(R.id.sensor);
        menu_listview = findViewById(R.id.menu_listview);
        onclick();
        menu_listview.setOnItemClickListener((adapterView, view, i, l) -> {
            switch (i) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:

                    break;
            }
        });

    }

    //点击事件
    private void onclick() {
        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            switch (i) {
                case R.id.home: //主页
                    if (homeFragment == null) {
                        homeFragment = new HomeFragment();
                        if (!homeFragment.isAdded()) {
                            transaction.add(R.id.container, homeFragment, "homeFragment");
                        }
                    } else if (homeFragment.isHidden()) {
                        transaction.show(homeFragment);
                    }
                    final Drawable drawablehome = getResources().getDrawable(R.mipmap.homepagetrue);
                    home.setCompoundDrawablesWithIntrinsicBounds(null, drawablehome, null, null);
                    final Drawable lightcontrolfalse = getResources().getDrawable(R.mipmap.lightcontrolfalse);
                    lightcontrol.setCompoundDrawablesWithIntrinsicBounds(null, lightcontrolfalse, null, null);
                    final Drawable drawablecontrol = getResources().getDrawable(R.mipmap.sensorfalse);
                    sensor.setCompoundDrawablesWithIntrinsicBounds(null, drawablecontrol, null, null);
                    final Drawable drawablemusic = getResources().getDrawable(R.mipmap.songlightfalse);
                    music.setCompoundDrawablesWithIntrinsicBounds(null, drawablemusic, null, null);
                    final Drawable drawablemy = getResources().getDrawable(R.mipmap.controlfalse);
                    control.setCompoundDrawablesWithIntrinsicBounds(null, drawablemy, null, null);
                    show(transaction);
                    break;
                case R.id.lightcontrol:  //布灯
                    if (lightcontrolFragment == null) {
                        lightcontrolFragment = new LightFragment();
                        if (!lightcontrolFragment.isAdded()) {
                            transaction.add(R.id.container, lightcontrolFragment, "lightcontrolFragment");
                        }
                    } else if (lightcontrolFragment.isHidden()) {
                        transaction.show(lightcontrolFragment);
                    }
                    final Drawable drawabl = getResources().getDrawable(R.mipmap.homepagefalse);
                    home.setCompoundDrawablesWithIntrinsicBounds(null, drawabl, null, null);
                    final Drawable lightcontroltrue = getResources().getDrawable(R.mipmap.lightcontroltrue);
                    lightcontrol.setCompoundDrawablesWithIntrinsicBounds(null, lightcontroltrue, null, null);
                    final Drawable drawablecontro = getResources().getDrawable(R.mipmap.sensorfalse);
                    sensor.setCompoundDrawablesWithIntrinsicBounds(null, drawablecontro, null, null);
                    final Drawable drawablemusi = getResources().getDrawable(R.mipmap.songlightfalse);
                    music.setCompoundDrawablesWithIntrinsicBounds(null, drawablemusi, null, null);
                    final Drawable drawablem = getResources().getDrawable(R.mipmap.controlfalse);
                    control.setCompoundDrawablesWithIntrinsicBounds(null, drawablem, null, null);
                    show(transaction);
                    break;
                case R.id.sensor:  //传感器
                    if (sensorFragment == null) {
                        ArrayList<MyDeviceBean> myDeviceBeen = (ArrayList<MyDeviceBean>) DeviceDbUtil.getInstance().queryTimeAll();
                        ArrayList<String> arrayList = new ArrayList<>();
                        for (MyDeviceBean DeviceBean : myDeviceBeen) {
                            arrayList.add(DeviceBean.getSn());
                        }
                        sensorFragment = new SensorFragment(arrayList);
                        if (!sensorFragment.isAdded()) {
                            transaction.add(R.id.container, sensorFragment, "sensorFragment");
                        }
                    } else if (sensorFragment.isHidden()) {
                        transaction.show(sensorFragment);
                    }
                    final Drawable drawab = getResources().getDrawable(R.mipmap.homepagefalse);
                    home.setCompoundDrawablesWithIntrinsicBounds(null, drawab, null, null);
                    final Drawable lightcontro = getResources().getDrawable(R.mipmap.lightcontrolfalse);
                    lightcontrol.setCompoundDrawablesWithIntrinsicBounds(null, lightcontro, null, null);
                    final Drawable drawablec = getResources().getDrawable(R.mipmap.sensortrue);
                    sensor.setCompoundDrawablesWithIntrinsicBounds(null, drawablec, null, null);
                    final Drawable drawa = getResources().getDrawable(R.mipmap.songlightfalse);
                    music.setCompoundDrawablesWithIntrinsicBounds(null, drawa, null, null);
                    final Drawable drawable = getResources().getDrawable(R.mipmap.controlfalse);
                    control.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                    show(transaction);
                    break;
                case R.id.music:  //光曲编辑
                    if (musicFragment == null) {
                        musicFragment = new MusicFragment();
                        if (!musicFragment.isAdded()) {
                            transaction.add(R.id.container, musicFragment, "musicFragment");
                        }
                    } else if (musicFragment.isHidden()) {
                        transaction.show(musicFragment);
                    }
                    final Drawable dra = getResources().getDrawable(R.mipmap.homepagefalse);
                    home.setCompoundDrawablesWithIntrinsicBounds(null, dra, null, null);
                    final Drawable lightcontr = getResources().getDrawable(R.mipmap.lightcontrolfalse);
                    lightcontrol.setCompoundDrawablesWithIntrinsicBounds(null, lightcontr, null, null);
                    final Drawable drawaa = getResources().getDrawable(R.mipmap.sensorfalse);
                    sensor.setCompoundDrawablesWithIntrinsicBounds(null, drawaa, null, null);
                    final Drawable drawaee = getResources().getDrawable(R.mipmap.songlighttrue);
                    music.setCompoundDrawablesWithIntrinsicBounds(null, drawaee, null, null);
                    final Drawable drawablee = getResources().getDrawable(R.mipmap.controlfalse);
                    control.setCompoundDrawablesWithIntrinsicBounds(null, drawablee, null, null);
                    show(transaction);
                    break;
                case R.id.control:   //控制
                    if (controlFragment == null) {
                        controlFragment = new ControlFragment();
                        if (!controlFragment.isAdded()) {
                            transaction.add(R.id.container, controlFragment, "controlFragment");
                        }
                    } else if (controlFragment.isHidden()) {
                        transaction.show(controlFragment);
                    }
                    final Drawable draq = getResources().getDrawable(R.mipmap.homepagefalse);
                    home.setCompoundDrawablesWithIntrinsicBounds(null, draq, null, null);
                    final Drawable lightcontrq = getResources().getDrawable(R.mipmap.lightcontrolfalse);
                    lightcontrol.setCompoundDrawablesWithIntrinsicBounds(null, lightcontrq, null, null);
                    final Drawable drawaaq = getResources().getDrawable(R.mipmap.sensorfalse);
                    sensor.setCompoundDrawablesWithIntrinsicBounds(null, drawaaq, null, null);
                    final Drawable drawaeeq = getResources().getDrawable(R.mipmap.songlightfalse);
                    music.setCompoundDrawablesWithIntrinsicBounds(null, drawaeeq, null, null);
                    final Drawable drawableeq = getResources().getDrawable(R.mipmap.controltrue);
                    control.setCompoundDrawablesWithIntrinsicBounds(null, drawableeq, null, null);
                    show(transaction);
                    break;
            }
            transaction.commit();
        });
    }


    private void show(FragmentTransaction transaction) {
        if (homeFragment != null && homeFragment.isVisible()) {
            transaction.hide(homeFragment);
        }
        if (lightcontrolFragment != null && lightcontrolFragment.isVisible()) {
            transaction.hide(lightcontrolFragment);
        }
        if (sensorFragment != null && sensorFragment.isVisible()) {
            transaction.hide(sensorFragment);
        }
        if (musicFragment != null && musicFragment.isVisible()) {
            transaction.hide(musicFragment);
        }
        if (controlFragment != null && controlFragment.isVisible()) {
            transaction.hide(controlFragment);
        }
    }


    private void selectItem() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, homeFragment)
                .commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GrandarUtils.unbindFromService(token);

    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        KLog.e(GrandarUtils.getService());
//        if (hasFocus && first) {
//            first = false;
//            List<MyDeviceBean> list = DeviceDbUtil.getInstance().queryTimeAll();
//            KLog.e(list.size());
//            if (list.size() > 0) {
//                KLog.e("");
//                GrandarUtils.startTcpClient(list, true);
//            }
//        }
//    }


    ////////////////////////////////////////////////////////////////////////////
    public boolean isEmulator()   //判断当前运行机器是否为模拟器
    {
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            if (imei != null && imei.equals("000000000000000")) {
                return true;
            }
            return (Build.MODEL.equals("sdk")) || (Build.MODEL.equals("google_sdk"));
        } catch (Exception ioe) {

        }
        return false;
    }

    public void playEffect(int mode, int list, int act, int ctrl) {
        netSocket.sendPlayPD(mode, list, act, ctrl);
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(getApplication(), R.string.tow_exit, Toast.LENGTH_LONG).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            SharedPutils sharedPutils = new SharedPutils();
            sharedPutils.putString(MainActivity.this, "deviceSn", null);
            KLog.e("deploySn接收到的Sn号:" + "已指空值");
            System.exit(0);
        }
    }
}
