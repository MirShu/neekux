package meekux.grandar.com.meekuxpjxroject.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.socks.library.KLog;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MarketMusicBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.SchoolMusicBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.DeviceDbUtil;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.MarketMusicDbUtil;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.SchoolMusicDbUtil;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import meekux.grandar.com.meekuxpjxroject.GlobalApplication;
import meekux.grandar.com.meekuxpjxroject.MainActivity;
import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.adapter.FragmentVpAdapter;
import meekux.grandar.com.meekuxpjxroject.adapter.LampListAdapte;
import meekux.grandar.com.meekuxpjxroject.service.PlayLightSongListService;
import meekux.grandar.com.meekuxpjxroject.songdata.database.GrandarUtils;
import meekux.grandar.com.meekuxpjxroject.songdata.database.MyConstant;
import meekux.grandar.com.meekuxpjxroject.utils.SharedPutils;


/**
 * Created by @author:xuqunwang on 17/1/4.
 * desceription:
 */
public class SingleControlFragment extends Fragment {
    private View view;
    private ViewPager vp;
    private TabLayout tab;
    private List<String> tablist = new ArrayList<>();
    private List<View> views = new ArrayList<>();
    private FragmentVpAdapter fragmentVpAdapter;
    private List<MarketMusicBean> marketMusicBeen;
    private List<MyDeviceBean> myDeviceBeen1;
    private ArrayList<String> alllight;
    private ArrayList<String> toplight;
    private ArrayList<String> facelight;
    private ArrayList<String> top4light;
    private ArrayList<String> top5light;
    private ArrayList<String> top6light;
    private List<SchoolMusicBean> schoolMusicBeen;
    private TextView tvRoomName;
    private ImageView allroomopen;
    private ImageView allroomclose;
    private ImageView allroombritness;
    private List<MyDeviceBean> myDeviceBeen;
    private ImageView sound;
    private TextView progressstexttt;
    private Dialog dialo;
    private Dialog dialog;
    private int screenWidth;
    public AudioManager audiomanage;
    private int maxVolume, currentVolume;
    private String deploySn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initdatas();
        initTabStr();
        initViews();
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        view = inflater.inflate(R.layout.singlecontrol, null);
        tab = view.findViewById(R.id.adddevice_view_tabLayout);
        vp = view.findViewById(R.id.adddevice_view_vp);
        tab.setTabMode(TabLayout.MODE_SCROLLABLE);
        for (int i = 0; i < 6; i++) {
            tab.addTab(tab.newTab().setText(tablist.get(i)));
            tvRoomName.setText(tablist.get(i));
        }
        fragmentVpAdapter = new FragmentVpAdapter(views, tablist);
        vp.setAdapter(fragmentVpAdapter);
        tab.setupWithViewPager(vp);
        tab.setTabsFromPagerAdapter(fragmentVpAdapter);
        myDeviceBeen = DeviceDbUtil.getInstance().queryTimeAll();
        return view;
    }

    private void initdatas() {
        alllight = new ArrayList<>();
        toplight = new ArrayList<>();
        facelight = new ArrayList<>();
        top4light = new ArrayList<>();
        top5light = new ArrayList<>();
        top6light = new ArrayList<>();

        marketMusicBeen = MarketMusicDbUtil.getInstance().queryTimeAll();
        for (MarketMusicBean marketMusicBean : marketMusicBeen) {
            toplight.add(marketMusicBean.getSn());
        }
        myDeviceBeen1 = DeviceDbUtil.getInstance().queryTimeAll();
        if (myDeviceBeen1 != null) {
            for (MyDeviceBean deviceBean : myDeviceBeen1) {
                alllight.add(deviceBean.getSn());
            }
        }
        schoolMusicBeen = SchoolMusicDbUtil.getInstance().queryTimeAll();
        for (SchoolMusicBean schoolMusicBean : schoolMusicBeen) {
            facelight.add(schoolMusicBean.getSn());
        }
    }

    private void initViews() {
        views.clear();
        View view = adddView(toplight, alllight);
        View vie = adddView(facelight, alllight);
        View vi1 = adddView(toplight, alllight);
        View vi4 = adddView(top4light, alllight);
        View vi5 = adddView(top5light, alllight);
        View vi6 = adddView(top6light, alllight);

        views.add(view);
        views.add(vie);
        views.add(vi1);
        views.add(vi4);
        views.add(vi5);
        views.add(vi6);
    }

    private View adddView(ArrayList<String> lampDeviceList, ArrayList<String> myDeviceBeen1) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater3 = LayoutInflater.from(getActivity().getBaseContext());
        view = inflater3.inflate(R.layout.fragment_second, null);
        tvRoomName = view.findViewById(R.id.tv_room_name);
        initView();
        return view;
    }

    private void initView() {
        audiomanage = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audiomanage.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);  //获取当前值
        allroomopen = view.findViewById(R.id.allroomopen);
        allroomclose = view.findViewById(R.id.allroomclose);
        allroombritness = view.findViewById(R.id.allroombritness);
        sound = view.findViewById(R.id.sound);
        progressstexttt = view.findViewById(R.id.progressstext);
        dialo = new Dialog(getActivity(), R.style.dialog_loading);
        dialog = new Dialog(getActivity(), R.style.dialog_loading);
        allroomopen.setOnClickListener(view1 -> openLight());
        allroomclose.setOnClickListener(view12 -> closeLight());
        allroombritness.setOnClickListener(view13 -> britnessLight());
        sound.setOnClickListener(view14 -> soundLight());
    }

    private void initTabStr() {
        tablist.clear();
        tablist.add("餐厅");
        tablist.add("客厅");
        tablist.add("卧室");
        tablist.add("客厅");
        tablist.add("次卧");
        tablist.add("主卧");
    }

    /**
     * 开灯
     */
    private void openLight() {
        try {
            deploySn = SharedPutils.getString(getActivity(), "deploySn", "");
            KLog.e("deploySn开灯Sn:" + deploySn);
            if (deploySn.isEmpty()) {
                Toast.makeText(getActivity(), "餐厅暂未布灯", Toast.LENGTH_SHORT).show();
            } else {
                MainActivity.netSocket.sendPlayColor(0, 0, 0, 255);
                if (PlayLightSongListService.mediaPlayer != null && PlayLightSongListService.mediaPlayer.isPlaying()) {
                    GrandarUtils.stopMp3Data();
                }
                new Thread(() -> {
                    Socket device = GlobalApplication.getInstance().getDevice(deploySn);
                    if (device != null) {
                        byte[] data = new byte[1];
                        data[0] = 50;
                        GrandarUtils.sendFrameOff(MyConstant.POWER_ON, data, deploySn);
                    }
                }).start();
            }
        } catch (Exception e) {

        }

    }

    /**
     * 关灯
     */
    private void closeLight() {
        //线条灯关灯
        try {
            KLog.e("deploySn关灯Sn:" + deploySn);
            GlobalApplication.getInstance().setStopSendFrame(true);
            MainActivity.netSocket.sendPlayColor(0, 0, 0, 0);
            if (PlayLightSongListService.mediaPlayer != null && PlayLightSongListService.mediaPlayer.isPlaying()) {
                GrandarUtils.stopMp3Data();
            }
            if (deploySn.isEmpty()) {
                Toast.makeText(getActivity(), "餐厅暂未布灯", Toast.LENGTH_SHORT).show();
                KLog.e("deploySn接收到的Sn号:" + deploySn);
            } else {
                new Thread(() -> {
                    GrandarUtils.stopMp3Data();
                    KLog.e("deviceSn关灯" + deploySn);
                    Socket device = GlobalApplication.getInstance().getDevice(deploySn);
                    if (device != null) {
                        byte[] data = new byte[1];
                        data[0] = 50;
                        GrandarUtils.sendFrameOff(MyConstant.POWER_OFF, data, deploySn);
                    }
                }).start();
            }
        } catch (Exception e) {

        }
    }

    /**
     * 声音调整
     */
    private void soundLight() {
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
    }

    /**
     * 调节亮度
     */
    private void britnessLight() {
        allroombritness.setImageResource(R.mipmap.sunlinghallroom);
        View inflate1 = LayoutInflater.from(getActivity()).inflate(R.layout.seekbarlayout, null);
        progressstexttt = inflate1.findViewById(R.id.progressstext);
        SeekBar secckBarlight = inflate1.findViewById(R.id.secckBarlight);
        secckBarlight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int i, boolean b) {
                progressstexttt.setText("" + i);
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
    }
}
