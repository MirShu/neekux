package meekux.grandar.com.meekuxpjxroject.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.Holder;
import com.socks.library.KLog;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MarketMusicBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.SchoolMusicBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.DeviceDbUtil;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.MarketMusicDbUtil;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.SchoolMusicDbUtil;

import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import meekux.grandar.com.meekuxpjxroject.GlobalApplication;
import meekux.grandar.com.meekuxpjxroject.MainActivity;
import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.activity.ColorActivity;
import meekux.grandar.com.meekuxpjxroject.activity.MyMessageActivity;
import meekux.grandar.com.meekuxpjxroject.activity.SearchDeviceActivity;
import meekux.grandar.com.meekuxpjxroject.activity.SettingActivity;
import meekux.grandar.com.meekuxpjxroject.activity.WifiContactActivity;
import meekux.grandar.com.meekuxpjxroject.adapter.MymenuAdapter;
import meekux.grandar.com.meekuxpjxroject.adapter.Room;
import meekux.grandar.com.meekuxpjxroject.entity.Menu;
import meekux.grandar.com.meekuxpjxroject.service.PlayLightSongListService;
import meekux.grandar.com.meekuxpjxroject.songdata.database.GrandarUtils;
import meekux.grandar.com.meekuxpjxroject.songdata.database.MyConstant;

import static meekux.grandar.com.meekuxpjxroject.R.id.gradview;
import static meekux.grandar.com.meekuxpjxroject.R.id.lefttextview0;
import static meekux.grandar.com.meekuxpjxroject.R.id.lefttextview1;
import static meekux.grandar.com.meekuxpjxroject.R.id.openlight0;
import static meekux.grandar.com.meekuxpjxroject.R.id.openlight1;
import static meekux.grandar.com.meekuxpjxroject.R.id.openlight2;

/**
 * Created by @author:xuqunwang on 17/1/4.
 * desceription:
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private boolean allrooomopenboolen = false;
    private View view;
    private DrawerLayout draerlayout;
    int[] menuimageString = new int[]{R.mipmap.myadddevice, R.mipmap.myfinddevice,
            R.mipmap.mypange, R.mipmap.mymessage, R.mipmap.mysetting};//图片
    MymenuAdapter mymenuAdapter;
    private ListView listview;
    List<Menu> menulist;//菜单组
    String[] menutitleString = new String[]{"添加设备", "搜索设备", "我的钱包", "我的消息", "设置"};//
    private ImageView img_off;
    private ConvenientBanner conventbanner;
    private ArrayList<Integer> localImages;
    private ImageView allroomopen;
    private ImageView allroomclose;
    private ImageView allroombritness;
    private ImageView sound;
    private int screenWidth;
    public AudioManager audiomanage;
    private int maxVolume, currentVolume;
    private boolean allroombritnessboolean = false;
    private List<MarketMusicBean> marketMusicBeen;
    private List<SchoolMusicBean> schoolMusicBeen;
    private static ArrayList<String> toplight;
    private static ArrayList<String> facelight;
    private LinearLayout alltextviewsn;
    private TextView progressstexttt;
    private List<MyDeviceBean> myDeviceBeen;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        intis();
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        view = inflater.inflate(R.layout.newhomefragmement, null);
        intiView(view);
        alltextviewsn = view.findViewById(R.id.alltextviewsn);
        alltextviewsn.setOnClickListener(this);
        //开灯
        allroomopen = view.findViewById(R.id.allroomopen);
        //关灯
        allroomclose = view.findViewById(R.id.allroomclose);
        //亮度
        allroombritness = view.findViewById(R.id.allroombritness);
        //音量
        sound = view.findViewById(R.id.sound);
        allroomopen.setOnClickListener(this);
        allroomclose.setOnClickListener(this);
        allroombritness.setOnClickListener(this);
        sound.setOnClickListener(this);
        draerlayout = getActivity().findViewById(R.id.drawerLayout);
        localImages = new ArrayList<>();
        listview = getActivity().findViewById(R.id.menu_listview);
        conventbanner = view.findViewById(R.id.main_activity_vp);

//        homeBaseAdapter = new HomeBaseAdapter(getActivity(), (ArrayList<Room>) homeList, screenWidth, mediaPlayer01, audiomanage, maxVolume, currentVolume);
//        gradview.setAdapter(homeBaseAdapter);
        menulist = new ArrayList<>();
        menulist.add(new Menu(menutitleString, menuimageString));
        mymenuAdapter = new MymenuAdapter(getActivity(), menulist);
        listview.setAdapter(mymenuAdapter);
        img_off = view.findViewById(R.id.icon_drawer);
        img_off.setOnClickListener(v -> {
            draerlayout.openDrawer(GravityCompat.START);
            new Thread(() -> {
                //  GrandarUtils.sendMusicToLight();
            }).start();
        });
        inteDate();
        OnItemClick();
        return view;
    }

    private void intiView(View view) {
        LinearLayout lefttextview0 = view.findViewById(R.id.lefttextview0);
        LinearLayout lefttextview1 = view.findViewById(R.id.lefttextview1);
        LinearLayout lefttextview2 = view.findViewById(R.id.lefttextview2);
        lefttextview0.setOnClickListener(this);
        lefttextview1.setOnClickListener(this);
        lefttextview2.setOnClickListener(this);
        LinearLayout openlight0 = view.findViewById(R.id.openlight0);
        LinearLayout openlight1 = view.findViewById(R.id.openlight1);
        LinearLayout openlight2 = view.findViewById(R.id.openlight2);
        LinearLayout closelight0 = view.findViewById(R.id.closelight0);
        LinearLayout closelight1 = view.findViewById(R.id.closelight1);
        LinearLayout closelight2 = view.findViewById(R.id.closelight2);
        LinearLayout light0 = view.findViewById(R.id.light0);
        LinearLayout light1 = view.findViewById(R.id.light1);
        LinearLayout light2 = view.findViewById(R.id.light2);
        LinearLayout song0 = view.findViewById(R.id.song0);
        LinearLayout song1 = view.findViewById(R.id.song1);
        LinearLayout song2 = view.findViewById(R.id.song2);
        openlight0.setOnClickListener(this);
        openlight1.setOnClickListener(this);
        openlight2.setOnClickListener(this);
        closelight0.setOnClickListener(this);
        closelight1.setOnClickListener(this);
        closelight2.setOnClickListener(this);
        light0.setOnClickListener(this);
        light1.setOnClickListener(this);
        light2.setOnClickListener(this);
        song0.setOnClickListener(this);
        song1.setOnClickListener(this);
        song2.setOnClickListener(this);
    }

    private void intis() {
        toplight = new ArrayList<>();
        facelight = new ArrayList<>();
        marketMusicBeen = MarketMusicDbUtil.getInstance().queryTimeAll();
        for (MarketMusicBean marketMusicBean : marketMusicBeen) {
            toplight.add(marketMusicBean.getSn());
        }
        myDeviceBeen = DeviceDbUtil.getInstance().queryTimeAll();
        schoolMusicBeen = SchoolMusicDbUtil.getInstance().queryTimeAll();
        for (SchoolMusicBean schoolMusicBean : schoolMusicBeen) {
            facelight.add(schoolMusicBean.getSn());
        }
        audiomanage = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audiomanage.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);  //获取当前值
    }

    //侧边栏点击事件
    private void OnItemClick() {
        listview.setOnItemClickListener((adapterView, view1, i, l) -> {
            switch (i) {
                case 0://我的设备
                    startActivity(new Intent(getActivity(), WifiContactActivity.class));
                    break;
                case 1://搜索设备
                    startActivity(new Intent(getActivity(), SearchDeviceActivity.class));
//                        if (SharedPreferencesUtils.getinstance().getBooleanValue(ConstStringUtil.LOGINSTATE, false)) {
//                            startActivity(new Intent(MainActivity.this, MyWalletActivity.class));
//                        } else {
//                            startActivity(new Intent(MainActivity.this, LoginAndRegisetActivity.class));
//                        }
                    break;
                case 2:
                    // startActivity(new Intent(MainActivity.this, LoginAndRegisetActivity.class));
                case 3://我的消息
                    startActivity(new Intent(getActivity(), MyMessageActivity.class));
                    break;
                case 4://设置
                    startActivity(new Intent(getActivity(), SettingActivity.class));
                    break;
            }
        });
    }

    private void inteDate() {
        for (int position = 0; position < 5; position++) {
            localImages.add(getResId("banner" + position, R.mipmap.class));
        }
        conventbanner.setPages(() -> new LocalImageHolderView(), localImages)
                //设置指示器是否可见
                .setPointViewVisible(true)
                .startTurning(2000)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.drawable.yuanheise, R.drawable.yuan})
                //设置指示器的方向（左、中、右）
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                //设置点击监听事件
//                .setOnItemClickListener(this)
                //设置手动影响（设置了该项无法手动切换）
                .setManualPageable(true);
    }

    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void onClick(View view) {
        Dialog dialo = new Dialog(getActivity(), R.style.dialog_loading);
        Dialog dialog = new Dialog(getActivity(), R.style.dialog_loading);
        switch (view.getId()) {
            case R.id.alltextviewsn:
                ArrayList<String> arrayList = new ArrayList<>();
                List<MyDeviceBean> eviceBeen1 = DeviceDbUtil.getInstance().queryTimeAll();
                if (eviceBeen1 != null) {
                    for (MyDeviceBean deviceBean : eviceBeen1) {
                        arrayList.add(deviceBean.getSn());
                    }

                }
                Intent intent = new Intent();
                intent.putStringArrayListExtra("sn", arrayList);
                intent.setClass(getActivity(), ColorActivity.class);
                getActivity().startActivity(intent);
                break;

            /**
             * 开灯方法
             */
            case R.id.allroomopen:
                MainActivity.netSocket.sendPlayColor(0, 0, 0, 255);
                //如果正在播放音乐按开灯按钮   关闭音效与光效  开白光
                if (PlayLightSongListService.mediaPlayer != null && PlayLightSongListService.mediaPlayer.isPlaying()) {
                    GrandarUtils.stopMp3Data();
                }

                new Thread(() -> {
                    for (MyDeviceBean myDeviceBean1 : myDeviceBeen) {
                        String sn = myDeviceBean1.getSn();
                        Socket device = GlobalApplication.getInstance().getDevice(sn);
                        if (device != null) {
                            byte[] data = new byte[1];
                            data[0] = 50;
                            GrandarUtils.sendFrameOff(MyConstant.POWER_ON, data, sn);
                        }
                    }
                }).start();
                break;

            /**
             * 关灯方法
             */
            case R.id.allroomclose:
                if (allrooomopenboolen == true && allrooomopenboolen == false) {
                    allrooomopenboolen = false;
                    allrooomopenboolen = true;
                    allroomopen.setImageResource(R.mipmap.allroomfalse);
                    allroomclose.setImageResource(R.drawable.allroomlighttrue);
                }
                //线条灯关灯
                GlobalApplication.getInstance().setStopSendFrame(true);
                MainActivity.netSocket.sendPlayColor(0, 0, 0, 0);
                if (PlayLightSongListService.mediaPlayer != null && PlayLightSongListService.mediaPlayer.isPlaying()) {
                    GrandarUtils.stopMp3Data();
                }
                //R1U00100000037ff6e063448333324
                new Thread(() -> {
                    GrandarUtils.stopMp3Data();
                    for (MyDeviceBean myDeviceBean12 : myDeviceBeen) {
                        String sn = myDeviceBean12.getSn();
                        Socket device = GlobalApplication.getInstance().getDevice(sn);
                        KLog.e("POWER_OFF" + sn);
                        if (device != null) {
                            GrandarUtils.sendFrameOff(MyConstant.POWER_OFF, null, sn);
                            KLog.e("deploySn主开关" + sn);
                        }
                    }
                }).start();
                break;
            //亮度
            case R.id.allroombritness:
                final List<MyDeviceBean> myDeviceBeen = DeviceDbUtil.getInstance().queryTimeAll();
                allroombritnessboolean = !allroombritnessboolean;
                allroombritness.setImageResource(R.mipmap.sunlinghallroom);
                View inflate1 = LayoutInflater.from(getActivity()).inflate(R.layout.seekbarlayout, null);
                progressstexttt = inflate1.findViewById(R.id.progressstext);
                SeekBar secckBarlight = inflate1.findViewById(R.id.secckBarlight);
                secckBarlight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, final int i, boolean b) {
                        progressstexttt.setText("" + i);
                        KLog.e("seekBartt2" + i);
                        new Thread(() -> {
                            for (MyDeviceBean myDeviceBean13 : myDeviceBeen) {
                                String sn = myDeviceBean13.getSn();
                                Socket socket = GlobalApplication.getInstance().getDevice(sn);
                                if (socket != null && socket.isConnected()) {
                                    GrandarUtils.setWholeLight(i, sn);
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                dialog.setContentView(inflate1);
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = screenWidth * 4 / 5;
                dialogWindow.setAttributes(lp);
                dialogWindow.setGravity(Gravity.CENTER);
                dialog.show();
                if (dialog.isShowing()) {
                    allroombritness.setImageResource(R.mipmap.lightpressboolean);
                }
                dialog.setOnDismissListener(dialogInterface -> allroombritness.setImageResource(R.mipmap.sunlinghallroom));
                break;
            //音量的调节
            case R.id.song0:
            case R.id.song1:
            case R.id.song2:
            case R.id.sound:
                View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.seekbarlayoutsong, null);
                final SeekBar secckBarsong = inflate.findViewById(R.id.secckBarsong);
                secckBarsong.setMax(maxVolume);
                secckBarsong.setProgress(currentVolume);
                secckBarsong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                        currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);  //获取当前值
                        secckBarsong.setProgress(currentVolume);
                        KLog.e("seekBartt" + i);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                dialo.setContentView(inflate);
                Window dialogWindo = dialo.getWindow();
                WindowManager.LayoutParams l = dialogWindo.getAttributes();
                l.width = screenWidth * 4 / 5;
                dialogWindo.setAttributes(l);
                dialogWindo.setGravity(Gravity.CENTER);
                dialo.show();
                if (dialo.isShowing()) {
                    sound.setImageResource(R.mipmap.songpressboolean);
                }
                dialo.setOnDismissListener(dialogInterface -> sound.setImageResource(R.mipmap.songallroom));
                break;
            case openlight0:
                new Thread(() -> {
                    for (String sn : toplight) {
                        Socket device = GlobalApplication.getInstance().getDevice(sn);
                        if (device != null) {
                            byte[] data = new byte[1];
                            data[0] = 50;
                            GrandarUtils.sendFrameOff(MyConstant.POWER_ON, data, sn);
                        }

                    }
                }).start();

                break;
            case openlight1:
                new Thread(() -> {
                    for (String sn : facelight) {
                        Socket device = GlobalApplication.getInstance().getDevice(sn);
                        if (device != null) {
                            byte[] data = new byte[1];
                            data[0] = 50;
                            GrandarUtils.sendFrameOff(MyConstant.POWER_ON, data, sn);
                        }

                    }
                }).start();
                break;
            case openlight2:
                break;
            case R.id.closelight0:
                new Thread(() -> {
                    for (String sn : toplight) {
                        Socket device = GlobalApplication.getInstance().getDevice(sn);
                        if (device != null) {
                            GrandarUtils.sendFrameOff(MyConstant.POWER_OFF, null, sn);
                        }
                    }
                }).start();
                break;
            case R.id.closelight1:
                new Thread(() -> {
                    for (String sn : facelight) {
                        Socket device = GlobalApplication.getInstance().getDevice(sn);
                        if (device != null) {
                            GrandarUtils.sendFrameOff(MyConstant.POWER_OFF, null, sn);
                        }
                    }
                }).start();
                break;
            case R.id.closelight2:
                break;
            case R.id.light0:
                View viewww = LayoutInflater.from(getActivity()).inflate(R.layout.seekbarlayout, null);
                progressstexttt = viewww.findViewById(R.id.progressstext);
                SeekBar secckBarss = viewww.findViewById(R.id.secckBarlight);
                secckBarss.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, final int i, boolean b) {
                        progressstexttt.setText("" + i);
                        KLog.e("seekBartt3" + i);
                        new Thread(() -> {
                            for (String sn : toplight) {
                                Socket socket = GlobalApplication.getInstance().getDevice(sn);
                                if (socket != null) {
                                    GrandarUtils.setWholeLight(i, sn);
                                }

                            }

                        }).start();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                dialog.setContentView(viewww);
                Window dialogWind = dialog.getWindow();
                WindowManager.LayoutParams lqqq = dialogWind.getAttributes();
                lqqq.width = screenWidth * 4 / 5;
                dialogWind.setAttributes(lqqq);
                dialogWind.setGravity(Gravity.CENTER);
                dialog.show();
                break;
            case R.id.light1:
                View viewwww = LayoutInflater.from(getActivity()).inflate(R.layout.seekbarlayout, null);
                progressstexttt = viewwww.findViewById(R.id.progressstext);
                SeekBar secckBarssw = viewwww.findViewById(R.id.secckBarlight);
                secckBarssw.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, final int i, boolean b) {
                        progressstexttt.setText("" + i);
                        KLog.e("seekBartt1" + i);
                        new Thread(() -> {
                            for (String sn : facelight) {
                                Socket socket = GlobalApplication.getInstance().getDevice(sn);
                                if (socket != null) {
                                    GrandarUtils.setWholeLight(i, sn);
                                }

                            }

                        }).start();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                dialog.setContentView(viewwww);
                Window dialogWin = dialog.getWindow();
                WindowManager.LayoutParams lqqqqq = dialogWin.getAttributes();
                lqqqqq.width = screenWidth * 4 / 5;
                dialogWin.setAttributes(lqqqqq);
                dialogWin.setGravity(Gravity.CENTER);
                dialog.show();
                break;
            case R.id.light2:
                break;
            case lefttextview0:
                Intent inten = new Intent();
                inten.putStringArrayListExtra("sn", toplight);
                inten.setClass(getActivity(), ColorActivity.class);
                getActivity().startActivity(inten);
                break;
            case lefttextview1:
                Intent inte = new Intent();
                inte.putStringArrayListExtra("sn", facelight);
                inte.setClass(getActivity(), ColorActivity.class);
                getActivity().startActivity(inte);
                break;


        }
    }

    private class LocalImageHolderView implements Holder<Integer> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int position, Integer data) {
            imageView.setImageResource(data);
        }
    }

}

