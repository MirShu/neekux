package meekux.grandar.com.meekuxpjxroject.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SeekBar;

import com.socks.library.KLog;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.DeviceDbUtil;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import meekux.grandar.com.meekuxpjxroject.GlobalApplication;
import meekux.grandar.com.meekuxpjxroject.MainActivity;
import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.adapter.ChangeColorGrideViewAdapter;
import meekux.grandar.com.meekuxpjxroject.adapter.InViewClickListener;
import meekux.grandar.com.meekuxpjxroject.entity.ChangeColor;
import meekux.grandar.com.meekuxpjxroject.songdata.database.Constant;
import meekux.grandar.com.meekuxpjxroject.songdata.database.GrandarUtils;
import meekux.grandar.com.meekuxpjxroject.songdata.database.MyConstant;
import meekux.grandar.com.meekuxpjxroject.view.ColorPickerView;

/**
 * Created by liuqk on 2017/5/11.
 */
public class ColorActivityFragemnt extends Fragment implements SeekBar.OnSeekBarChangeListener {
    GridView gridView;
    String[] changecolorTitleString;
    ChangeColorGrideViewAdapter changeColorGrideViewAdapter;
    List<ChangeColor> changeColorList = new ArrayList<ChangeColor>();
    private boolean isFirstInit;
    private ColorPickerView colorpick;
    private int[] thems = new int[3];
    private MediaPlayer mediaPlayer01;
    public AudioManager audiomanage;
    public SeekBar soundBar;
    private int maxVolume, currentVolume;
    private SeekBar secckBarsong;
    private List<MyDeviceBean> myDeviceBeen;

    public ColorActivityFragemnt(List<ChangeColor> changeColorList) {
        this.changeColorList = changeColorList;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        inti();
    }

    private String ipp;
    private ArrayList<MyDeviceBean> yDeviceBeen1;
    private MyDeviceBean DeviceBean;
    private String sn;
    private int ipLIght;
    private View inflate;
    private ArrayList<String> arrayList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.activity_color, container, false);
        myDeviceBeen = DeviceDbUtil.getInstance().queryTimeAll();
        return inflate;
    }

    private void inti() {
        audiomanage = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audiomanage.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
        Intent intent = getActivity().getIntent();
        arrayList = intent.getStringArrayListExtra("sn");
        if (this.sn != null) {
            ipp = this.sn;
        }
        yDeviceBeen1 = (ArrayList<MyDeviceBean>) DeviceDbUtil.getInstance().queryTimeAll();
        colorpick = inflate.findViewById(R.id.colorpick);
        gridView = inflate.findViewById(R.id.changecolorgv);
        colorpick.setColorListener(new ColorPickerView.ColorListener() {
            @Override
            public void onColorSelected(int color) {
                KLog.e(color + "------------setColorListener");
                MainActivity.netSocket.sendPlayColor(colorpick.getColorRGB()[0], colorpick.getColorRGB()[1], colorpick.getColorRGB()[2], 100);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (arrayList != null) {
                            for (String s : arrayList) {
                                Socket device = GlobalApplication.getInstance().getDevice(s);
                                if (device != null) {
                                    KLog.e("onColorSelected");
                                    int[] colors = colorpick.getColorRGB();
                                    int white = 100 * 4096 / 255;
                                    if (s.contains("12123")) {
                                        //包含线条灯sn的内容进行执行
                                    } else {
                                        GrandarUtils.sendLightCtr(colors[0] * 4096 / 255, colors[1] * 4096 / 255, colors[2] * 4096 / 255, 0, s);
                                    }
                                }
                            }
                        }
                    }
                }).start();
            }
        });
        SeekBar secckBar = inflate.findViewById(R.id.secckBar);
        secckBarsong = inflate.findViewById(R.id.secckBarsong);
        secckBarsong.setMax(maxVolume);
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
        int brightness = Constant.brightness;
        secckBar.setProgress(brightness);
        secckBar.setOnSeekBarChangeListener(this);
        changecolorTitleString = getResources().getStringArray(R.array.changecolorarray);
        changeColorGrideViewAdapter = new ChangeColorGrideViewAdapter(getActivity(), R.layout.changgv_item);
        gridView.setAdapter(changeColorGrideViewAdapter);
        changeColorGrideViewAdapter.addAll(changeColorList);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (MyDeviceBean myDeviceBean : myDeviceBeen) {
                                    String sn = myDeviceBean.getSn();
                                    Socket device = GlobalApplication.getInstance().getDevice(sn);
                                    if (device != null) {
                                        GrandarUtils.sendLightCtr(242 * 4096 / 255, 126 * 4096 / 255, 97 * 4096 / 255, 0, sn);
                                    }
                                }
                            }
                        }).start();
                        break;
                    case 1:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (MyDeviceBean myDeviceBean : myDeviceBeen) {
                                    String sn = myDeviceBean.getSn();
                                    Socket device = GlobalApplication.getInstance().getDevice(sn);
                                    if (device != null) {
                                        GrandarUtils.sendLightCtr(115 * 4096 / 255, 122 * 4096 / 255, 254 * 4096 / 255, 0, sn);
                                    }
                                }
                            }
                        }).start();
                        break;
                    case 2:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (MyDeviceBean myDeviceBean : myDeviceBeen) {
                                    String sn = myDeviceBean.getSn();
                                    Socket device = GlobalApplication.getInstance().getDevice(sn);
                                    if (device != null) {
                                        GrandarUtils.sendLightCtr(66 * 4096 / 255, 189 * 4096 / 255, 95 * 4096 / 255, 0, sn);
                                    }
                                }
                            }
                        }).start();
                        break;
                    case 3:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                for (MyDeviceBean myDeviceBean : myDeviceBeen) {
                                    String sn = myDeviceBean.getSn();
                                    Socket device = GlobalApplication.getInstance().getDevice(sn);
                                    if (device != null) {
                                        GrandarUtils.sendLightCtr(71 * 4096 / 255, 206 * 4096 / 255, 255 * 4096 / 255, 0, sn);
                                    }
                                }

                            }
                        }).start();
                        break;
                    case 4:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (MyDeviceBean myDeviceBean : myDeviceBeen) {
                                    String sn = myDeviceBean.getSn();
                                    Socket device = GlobalApplication.getInstance().getDevice(sn);
                                    if (device != null) {
                                        GrandarUtils.sendLightCtr(255 * 4096 / 255, 226 * 4096 / 255, 100 * 4096 / 255, 0, sn);
                                    }
                                }

                            }
                        }).start();
                        break;
                    case 5:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (MyDeviceBean myDeviceBean : myDeviceBeen) {
                                    String sn = myDeviceBean.getSn();
                                    Socket device = GlobalApplication.getInstance().getDevice(sn);
                                    if (device != null) {
                                        GrandarUtils.sendLightCtr(255 * 4096 / 255, 71 * 4096 / 255, 81 * 4096 / 255, 0, sn);
                                    }
                                }

                            }
                        }).start();
                        break;
                }
            }
        });
        changeColorGrideViewAdapter.setOnInViewClickListener(new InViewClickListener<ChangeColor>() {
            @Override
            public void OnClickListener(View parentV, View v, Integer position, ChangeColor values) {
            }

            @Override
            public void OnClickLongListener(View parentV, View v, Integer position, ChangeColor values) {
            }
        });

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        Constant.brightness = i;
        Threadl threadl = new Threadl(new Object(), i);
        Thread thread = new Thread(threadl);
        thread.start();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public class Threadl implements Runnable {
        Object lock;
        int i;

        public Threadl(Object lock, int i) {
            this.lock = lock;
            this.i = i;
        }

        public void run() {
            synchronized (lock) {
                if (arrayList != null) {
                    for (String s : arrayList) {
                        Socket device = GlobalApplication.getInstance().getDevice(s);
                        if (device != null) {
                            GrandarUtils.setWholeLight(i, s);
                        }
                    }
                }
            }
        }
    }
}
