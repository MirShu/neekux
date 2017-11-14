package meekux.grandar.com.meekuxpjxroject.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socks.library.KLog;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.ColourBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.ColourListBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.FolderBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MarketMusicBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MusicListSong;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.DeviceDbUtil;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.FolderUtil;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.MarketMusicDbUtil;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.MusicDbUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import meekux.grandar.com.meekuxpjxroject.GlobalApplication;
import meekux.grandar.com.meekuxpjxroject.MainActivity;
import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.adapter.MusicsingleAdapter;
import meekux.grandar.com.meekuxpjxroject.adapter.recycler.RecyclerAdapter;
import meekux.grandar.com.meekuxpjxroject.adapter.recycler.RecyclerViewHolder;
import meekux.grandar.com.meekuxpjxroject.service.PlayLightSongListService;
import meekux.grandar.com.meekuxpjxroject.songdata.database.GrandarUtils;
import meekux.grandar.com.meekuxpjxroject.songdata.database.MyUtil;
import meekux.grandar.com.meekuxpjxroject.utils.SProject;
import meekux.grandar.com.meekuxpjxroject.utils.SharedPutils;


/**
 * Created by @author:xuqunwang on 17/1/4.
 * desceription:
 */
public class ManulControlFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RelativeLayout selectlayout;
    private static List<String> listplay = new ArrayList<>();
    private static List<String> playlistName = new ArrayList<>();
    private static SProject sProjects;
    static int selectpostion = -1;
    public static MediaPlayer mediaPlayer;
    private List<FolderBean> list;
    private MusicsingleAdapter adaptermusic;
    private List<MusicListSong> path_list;
    private static final String ACTION = "com.grandar.voicecommend";
    private RecyclerAdapter recyclerAdapter;
    private List<String> recyclerList;
    private RecyclerView recyclerView;
    private RecyclerAdapter lineReclerAdapter;
    private List<String> lineReclerList;
    private RecyclerView lineReclView;
    private RecyclerAdapter qbReclerAdapter;
    private List<String> qbReclerList;
    private RecyclerView qbRecyclView;
    private CheckBox checkBoxState;
    private int recSta;
    private boolean isPasue = false; //是否暂停
    private List<MyDeviceBean> myDeviceBeen;
    private int isPlayingState;
    private TextView tvState;
    private String deploySn;
    private RelativeLayout mianbanlayout;//面板灯
    private RelativeLayout xiantiaolayout;//线条灯
    private TextView mianbantitle;
    private TextView xiantiaotitle;
    private TextView title;
    private PopupWindow popupWindow;
    private TextView classtitle;
    private ImageView ivArrow;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.manulcontrol, null);
        this.recyclerView = view.findViewById(R.id.recyclerView);
        this.lineReclView = view.findViewById(R.id.recyclViewLine);
        this.qbRecyclView = view.findViewById(R.id.recyclViewQb);
        this.mediaPlayer = new MediaPlayer();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        this.title = view.findViewById(R.id.title);
        this.selectlayout = view.findViewById(R.id.selectlayout);
        this.ivArrow = view.findViewById(R.id.iv_arrow);
        this.selectlayout.setOnClickListener(this);
        this.title.setText("面板灯");
        this.myDeviceBeen = DeviceDbUtil.getInstance().queryTimeAll();
        this.deploySn = SharedPutils.getString(getActivity(), "deploySn", "");
        this.initMusic(view);
        this.bindRecycleView();
        this.bindQbRecycleView();
        return view;
    }

    private void bindRecycleView() {
        this.getData();
        this.getLineData();
        this.lineReclerAdapter = new RecyclerAdapter<String>(getActivity(), lineReclerList, R.layout.layout_activity_item_music) {
            @Override
            public void convert(RecyclerViewHolder helper, String item, int position) {
                checkBoxState = helper.getView(R.id.checkBox_state);
                tvState = helper.getView(R.id.tv_state);
                if (position == 0) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 1) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "青春修炼手册");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.music_item_icon_01);
                    helper.setImageResource(R.id.music_item_icon, R.mipmap.music_icon);
                    helper.setText(R.id.main_music_item_des, "TFboys");
                } else if (position == 1) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 2) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "发现新世界");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.purple);
                    helper.setImageResource(R.id.music_item_icon, R.mipmap.music_icon);
                    helper.setText(R.id.main_music_item_des, "新世界");
                } else if (position == 2) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 3) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "璀璨霓虹");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.music_item_icon_05);
                    helper.setImageResource(R.id.music_item_icon, R.mipmap.music_icon);
                    helper.setText(R.id.main_music_item_des, "美好生活");
                } else if (position == 3) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 4) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "混纯爱恋");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.red);
                    helper.setImageResource(R.id.music_item_icon, R.mipmap.music_icon);
                    helper.setText(R.id.main_music_item_des, "恋爱是纯洁的奴隶");
                }

                checkBoxState.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    try {
                        if (isChecked) {
                            if (PlayLightSongListService.mediaPlayer.isPlaying()) {
                                GrandarUtils.pauseMusics();
                                isPasue = true;
                            }
                        } else {
                            if (isPasue) {
                                GrandarUtils.resumeMp3Data();
                                isPasue = false;
                            }
                        }
                    } catch (Exception e) {

                    }
                });
            }
        };

        this.lineReclView.setHasFixedSize(true);
        this.lineReclView.setLayoutManager(new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false));
        this.lineReclView.setAdapter(this.lineReclerAdapter);
        this.lineReclerAdapter.setOnItemClickListener((parent, position) -> {
            if (position == 0) {
                GrandarUtils.stopMp3Data();
                recSta = 2;
                isPlayingState = 1;
                path_list = MusicDbUtil.getInstance().queryTimeBy(position + 1);
                musicPlay(path_list, true, list.get(position).getPath());
                lineReclerAdapter.notifyDataSetChanged();
            } else if (position == 1) {
                GrandarUtils.stopMp3Data();
                recSta = 3;
                isPlayingState = 2;
                path_list = MusicDbUtil.getInstance().queryTimeBy(1);
                musicPlay(path_list, true, list.get(0).getPath());
                lineReclerAdapter.notifyDataSetChanged();
            } else if (position == 2) {
                GrandarUtils.stopMp3Data();
                recSta = 1;
                isPlayingState = 3;
                path_list = MusicDbUtil.getInstance().queryTimeBy(1);
                musicPlay(path_list, true, list.get(0).getPath());
                lineReclerAdapter.notifyDataSetChanged();
            } else if (position == 3) {
                GrandarUtils.stopMp3Data();
                recSta = 4;
                isPlayingState = 4;
                path_list = MusicDbUtil.getInstance().queryTimeBy(1);
                musicPlay(path_list, true, list.get(0).getPath());
                lineReclerAdapter.notifyDataSetChanged();
            }
        });

        this.recyclerAdapter = new RecyclerAdapter<String>(getActivity(), recyclerList,
                R.layout.layout_activity_item_music) {
            @Override
            public void convert(RecyclerViewHolder helper, String item, int position) {
                checkBoxState = helper.getView(R.id.checkBox_state);
                tvState = helper.getView(R.id.tv_state);
                if (position == 0) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 1) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "青春修炼手册");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.music_item_icon_01);
                    helper.setImageResource(R.id.music_item_icon, R.mipmap.music_icon);
                    helper.setText(R.id.main_music_item_des, "TFboys");
                } else if (position == 1) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 2) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "发现新世界");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.purple);
                    helper.setImageResource(R.id.music_item_icon, R.mipmap.music_icon);
                    helper.setText(R.id.main_music_item_des, "新世界");
                } else if (position == 2) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 3) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "璀璨霓虹");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.music_item_icon_05);
                    helper.setImageResource(R.id.music_item_icon, R.mipmap.music_icon);
                    helper.setText(R.id.main_music_item_des, "美好生活");
                } else if (position == 3) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 4) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "混纯爱恋");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.red);
                    helper.setImageResource(R.id.music_item_icon, R.mipmap.music_icon);
                    helper.setText(R.id.main_music_item_des, "恋爱是纯洁的奴隶");
                } else if (position == 4) {
                    tvState.setVisibility(View.VISIBLE);
                    if (isPlayingState == 5) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "生日派对");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.music_item_icon_04);
                } else if (position == 5) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 6) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "优雅舞会");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.music_item_icon_02);
                } else if (position == 6) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 7) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "蓝天白云");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.music_item_icon_01);
                } else if (position == 7) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 8) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "夕阳下");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.green_icon);
                } else if (position == 8) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 9) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "冬暖");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.green);
                }
                checkBoxState.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    try {
                        if (isChecked) {
                            if (PlayLightSongListService.mediaPlayer.isPlaying()) {
                                GrandarUtils.pauseMusics();
                                isPasue = true;
                            }
                        } else {
                            if (isPasue) {
                                GrandarUtils.resumeMp3Data();
                                isPasue = false;
                            }
                        }
                    } catch (Exception e) {

                    }
                });
            }
        };

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1,
                LinearLayoutManager.VERTICAL, false));
        this.recyclerView.setAdapter(this.recyclerAdapter);
        this.recyclerAdapter.setOnItemClickListener((parent, position) -> {
            if (position == 0) {
                GrandarUtils.stopMp3Data();
                recSta = 2;
                isPlayingState = 1;
                path_list = MusicDbUtil.getInstance().queryTimeBy(position + 1);
                musicPlay(path_list, true, list.get(position).getPath());
                recyclerAdapter.notifyDataSetChanged();
            } else if (position == 1) {
                GrandarUtils.stopMp3Data();
                recSta = 3;
                isPlayingState = 2;
                path_list = MusicDbUtil.getInstance().queryTimeBy(1);
                musicPlay(path_list, true, list.get(0).getPath());
                recyclerAdapter.notifyDataSetChanged();
            } else if (position == 2) {
                GrandarUtils.stopMp3Data();
                recSta = 1;
                isPlayingState = 3;
                path_list = MusicDbUtil.getInstance().queryTimeBy(1);
                musicPlay(path_list, true, list.get(0).getPath());
                recyclerAdapter.notifyDataSetChanged();
            } else if (position == 3) {
                GrandarUtils.stopMp3Data();
                recSta = 4;
                isPlayingState = 4;
                path_list = MusicDbUtil.getInstance().queryTimeBy(1);
                musicPlay(path_list, true, list.get(0).getPath());
                recyclerAdapter.notifyDataSetChanged();
            } else if (position == 4) {
                GrandarUtils.stopMp3Data();
                isPlayingState = 5;
                new Thread(() -> {
                    for (MyDeviceBean myDeviceBean : myDeviceBeen) {
                        String sn1 = myDeviceBean.getSn();
                        Socket device = GlobalApplication.getInstance().getDevice(sn1);
                        if (device != null) {
                            GrandarUtils.sendLightCtr(115 * 4096 / 255, 122 * 4096 / 255, 254 * 4096 / 255, 0, sn1);
                        }
                    }
                }).start();
                recyclerAdapter.notifyDataSetChanged();
            } else if (position == 5) {
                GrandarUtils.stopMp3Data();
                isPlayingState = 6;
                new Thread(() -> {
                    for (MyDeviceBean myDeviceBean : myDeviceBeen) {
                        String sn12 = myDeviceBean.getSn();
                        Socket device = GlobalApplication.getInstance().getDevice(sn12);
                        if (device != null) {
                            GrandarUtils.sendLightCtr(242 * 4096 / 255, 126 * 4096 / 255, 97 * 4096 / 255, 0, sn12);
                        }
                    }
                }).start();
                recyclerAdapter.notifyDataSetChanged();
            } else if (position == 6) {
                GrandarUtils.stopMp3Data();
                isPlayingState = 7;
                new Thread(() -> {
                    for (MyDeviceBean myDeviceBean : myDeviceBeen) {
                        String sn12 = myDeviceBean.getSn();
                        Socket device = GlobalApplication.getInstance().getDevice(sn12);
                        if (device != null) {
                            GrandarUtils.sendLightCtr(66 * 4096 / 255, 189 * 4096 / 255, 95 * 4096 / 255, 0, sn12);
                        }
                    }
                }).start();
                recyclerAdapter.notifyDataSetChanged();
            } else if (position == 7) {
                GrandarUtils.stopMp3Data();
                isPlayingState = 8;
                new Thread(() -> {
                    for (MyDeviceBean myDeviceBean : myDeviceBeen) {
                        String sn12 = myDeviceBean.getSn();
                        Socket device = GlobalApplication.getInstance().getDevice(sn12);
                        if (device != null) {
                            GrandarUtils.sendLightCtr(71 * 4096 / 255, 206 * 4096 / 255, 255 * 4096 / 255, 0, sn12);
                        }
                    }

                }).start();
                recyclerAdapter.notifyDataSetChanged();
            } else if (position == 8) {
                GrandarUtils.stopMp3Data();
                isPlayingState = 9;
                new Thread(() -> {
                    for (MyDeviceBean myDeviceBean : myDeviceBeen) {
                        String sn12 = myDeviceBean.getSn();
                        Socket device = GlobalApplication.getInstance().getDevice(sn12);
                        if (device != null) {
                            GrandarUtils.sendLightCtr(255 * 4096 / 255, 226 * 4096 / 255, 100 * 4096 / 255, 0, sn12);
                        }
                    }

                }).start();
                recyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initMusic(View view) {
        list = FolderUtil.getInstance().queryTimeAll();
        adaptermusic = new MusicsingleAdapter(getActivity(), list);
        adaptermusic.setOnMusicClick((position, state) -> {
            switch (state) {
                //播放光效
                case 0:
                    GrandarUtils.stopMp3Data();
                    recSta = 1;
                    path_list = MusicDbUtil.getInstance().queryTimeBy(position + 1);
                    musicPlay(path_list, true, list.get(position).getPath());
                    break;
                //暂停播放
                case 1:
                    if (state == 2) {
                        GrandarUtils.stopMp3Data();
                    } else {
                        GrandarUtils.pauseMusics();
                    }
                    break;
                //继续播放
                case 2:
                    GrandarUtils.resumeMp3Data();
                    break;
            }
        });
    }

    private void musicPlay(final List<MusicListSong> data, boolean ismusic, final String path) {
        GlobalApplication.getInstance().setData((ArrayList<MusicListSong>) data);
        if (recSta == 1) {
            String path_name = path + data.get(6).getName();
            test(path_name, true, "", 0, 2);
        } else if (recSta == 2) {
            String path_name = path + data.get(5).getName();
            test(path_name, true, "", 0, 2);
        } else if (recSta == 3) {
            String path_name = path + data.get(7).getName();
            test(path_name, true, "", 0, 2);
        } else if (recSta == 4) {
            String path_name = path + data.get(11).getName();
            test(path_name, true, "", 0, 2);
        }
    }

    private void test(String path, boolean ismusics, String name, int position, int flag) {
        if (ismusics) {
            deploySn = SharedPutils.getString(getActivity(), "deploySn", "");
            if (deploySn.isEmpty()) {
                Toast.makeText(getActivity(), "暂未布灯", Toast.LENGTH_SHORT).show();
                KLog.e("deploySn接收到的Sn号:" + deploySn);
            } else {
                GrandarUtils.playMusic(path, name, flag, position);
            }
        } else {
            //没有音乐的方法
            StartSendLight(path, name);
            GlobalApplication.getInstance().setPlayCount(GlobalApplication.getInstance().getPlayCount() + 1);
        }

    }


    private void StartSendLight(final String path, final String name) {
        final int[] sendLightFrameNum = {0};
        final int MP3_HEAD_LEN = 292;
        final int FRAME_HEAD_LEN = 16; // 每个数据帧 帧头长度
        final int LIGHT_LEN = 128; // 光效长度
        final int offset = MyUtil.getLightSongOffset(path);
        // 获取总帧数
        final int totalFrame = MyUtil.getLightSongFrame(path);
        final File lightFile = new File(path);
        final Timer sendLightTimer = new Timer();
        sendLightTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (GlobalApplication.getInstance().isStopSendFrame()) {
                    sendLightTimer.cancel();
                }
                KLog.e(sendLightFrameNum[0] + "----" + totalFrame);
                if (sendLightFrameNum[0] >= totalFrame) {
                    sendLightTimer.cancel();
                    return;
                }
                FileInputStream isLight = null;
                try {
                    isLight = new FileInputStream(lightFile);
                    final byte[] lightBuf = new byte[128];
                    isLight.skip(MP3_HEAD_LEN + offset + sendLightFrameNum[0] * (FRAME_HEAD_LEN + LIGHT_LEN) + FRAME_HEAD_LEN);
                    isLight.read(lightBuf);
                    sendLightFrameNum[0] = sendLightFrameNum[0] + 1;
                    GrandarUtils.sendLightDataTime(lightBuf, 0, name); // 发送光效帧

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    try {
                        isLight.close();
                    } catch (IOException e) {
                    }
                }

            }
        }, 0, 26);
    }

    private void bindQbRecycleView() {
        this.getQbData();
        this.qbReclerAdapter = new RecyclerAdapter<String>(getActivity(), qbReclerList, R.layout.layout_activity_item_music) {
            @Override
            public void convert(RecyclerViewHolder helper, String item, int position) {
                checkBoxState = helper.getView(R.id.checkBox_state);
                tvState = helper.getView(R.id.tv_state);
                if (position == 0) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 1) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "青春修炼手册");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.music_item_icon_01);
                    helper.setImageResource(R.id.music_item_icon, R.mipmap.music_icon);
                    helper.setText(R.id.main_music_item_des, "TFboys");
                } else if (position == 1) {
                    tvState.setVisibility(View.VISIBLE);
                    if (isPlayingState == 5) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "生日派对");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.music_item_icon_04);
                } else if (position == 2) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 6) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "优雅舞会");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.music_item_icon_02);
                } else if (position == 3) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 7) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "蓝天白云");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.music_item_icon_01);
                }
                checkBoxState.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    try {
                        if (isChecked) {
                            if (PlayLightSongListService.mediaPlayer.isPlaying()) {
                                GrandarUtils.pauseMusics();
                                isPasue = true;
                            }
                        } else {
                            if (isPasue) {
                                GrandarUtils.resumeMp3Data();
                                isPasue = false;
                            }
                        }
                    } catch (Exception e) {

                    }
                });
            }
        };

        this.qbRecyclView.setHasFixedSize(true);
        this.qbRecyclView.setLayoutManager(new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false));
        this.qbRecyclView.setAdapter(this.qbReclerAdapter);
        this.qbReclerAdapter.setOnItemClickListener((parent, position) -> {
            if (position == 0) {
                GrandarUtils.stopMp3Data();
                recSta = 2;
                isPlayingState = 1;
                path_list = MusicDbUtil.getInstance().queryTimeBy(position + 1);
                musicPlay(path_list, true, list.get(position).getPath());
                qbReclerAdapter.notifyDataSetChanged();
            } else if (position == 1) {
                GrandarUtils.stopMp3Data();
                isPlayingState = 5;
                new Thread(() -> {
                    for (MyDeviceBean myDeviceBean : myDeviceBeen) {
                        String sn1 = myDeviceBean.getSn();
                        Socket device = GlobalApplication.getInstance().getDevice(sn1);
                        if (device != null) {
                            GrandarUtils.sendLightCtr(242 * 4096 / 255, 126 * 4096 / 255, 97 * 4096 / 255, 0, sn1);
                        }
                    }
                }).start();
                recyclerAdapter.notifyDataSetChanged();
            } else if (position == 2) {
                GrandarUtils.stopMp3Data();
                isPlayingState = 6;
                new Thread(() -> {
                    for (MyDeviceBean myDeviceBean : myDeviceBeen) {
                        String sn1 = myDeviceBean.getSn();
                        Socket device = GlobalApplication.getInstance().getDevice(sn1);
                        if (device != null) {
                            GrandarUtils.sendLightCtr(115 * 4096 / 255, 122 * 4096 / 255, 254 * 4096 / 255, 0, sn1);
                        }
                    }
                }).start();
                recyclerAdapter.notifyDataSetChanged();
            } else if (position == 3) {
                GrandarUtils.stopMp3Data();
                isPlayingState = 7;
                new Thread(() -> {
                    for (MyDeviceBean myDeviceBean : myDeviceBeen) {
                        String sn1 = myDeviceBean.getSn();
                        Socket device = GlobalApplication.getInstance().getDevice(sn1);
                        if (device != null) {
                            GrandarUtils.sendLightCtr(260 * 4096 / 255, 122 * 4096 / 255, 254 * 4096 / 255, 0, sn1);
                        }
                    }
                }).start();
                recyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getData() {
        recyclerList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            recyclerList.add("" + i);
        }
    }

    private void getLineData() {
        lineReclerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            lineReclerList.add("" + i);
        }
    }

    private void getQbData() {
        qbReclerList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            qbReclerList.add("" + i);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void createData(SProject sprojects) {
        sProjects = sprojects;
        int i = 0;
        int j = 0;
        int k = 0;
        listplay.clear();         //需要先清空，避免重复添加
        playlistName.clear();
        SProject sproject = sprojects;
        for (i = 0; i < sproject.listPD.size(); i++) {
            playlistName.add(sproject.listPD.get(i).name);
        }

        for (i = 0; i < playlistName.size(); i++) {
            k = sproject.listPD.get(i).posStart + sproject.listPD.get(i).num;

            for (j = sproject.listPD.get(i).posStart; j < k; j++) {

                listplay.add(sproject.listAct.get(j).name);

            }
        }

    }

    void playEffect(int selPos, int state)    //播放当前选中的场景或者列表
    {
        int i = 0;
        int j = 0;

        if (sProjects == null)
            return;
        SProject sproject = sProjects;
        int list = -1;
        int act = -1;
        int mode = 0;
        int count = 0;
        for (i = 0; i < sproject.listPD.size(); i++) {
            if (selPos == count) {
                list = i;
                mode = 1;
                break;
            }
            count++;
            SProject.SProject_PD_LIST pdList = sproject.listPD.get(i);
            for (j = 0; j < pdList.num; j++) {
                if (selPos == count) {
                    list = i;
                    act = j;
                    mode = 2;
                    break;
                }
                count++;
            }

            if (list != -1 || act != -1)
                break;
        }
        MainActivity.netSocket.sendPlayPD(mode, list, act, state);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selectlayout:
                showPopupWindow(selectlayout);   //弹出PopupWindow 后期有更多数据列表可打开
                backgroundAlpha(0.7f);
                ivArrow.setImageResource(R.mipmap.icon_bg_pop_up);
                break;
        }
    }

    private void showPopupWindow(View view) {
        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.pop_window, null);
        RelativeLayout mianbanlayout = contentView.findViewById(R.id.mianbandenglayout);//商场
        RelativeLayout xiantiaolayout = contentView.findViewById(R.id.xiantiaolayout);//客厅
        RelativeLayout classromelayout = contentView.findViewById(R.id.classromelayout);//教室
        mianbantitle = contentView.findViewById(R.id.mianbtitle);
        xiantiaotitle = contentView.findViewById(R.id.xiantiaotitle);
        classtitle = contentView.findViewById(R.id.classrometitle);
        if (popupWindow == null) {
            popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        }
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor((v, event) -> {
            return false;
            // 这里如果返回true的话，touch事件将被拦截
            // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
        });
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bugColor.parseColor("#000000")
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.wheel));
        // 设置好参数之后再show
        popupWindow.showAsDropDown(view);
        popupWindow.setOnDismissListener(() -> backgroundAlpha(1f));
        xiantiaolayout.setOnClickListener(view1 -> {
            recyclerView.setVisibility(View.VISIBLE);
            lineReclView.setVisibility(View.GONE);
            qbRecyclView.setVisibility(View.GONE);
            title.setText("面板灯");
            popupWindow.dismiss();
            ivArrow.setImageResource(R.mipmap.icon_bg_pop_dow);
        });
        mianbanlayout.setOnClickListener(view12 -> {
            recyclerView.setVisibility(View.GONE);
            lineReclView.setVisibility(View.VISIBLE);
            qbRecyclView.setVisibility(View.GONE);
            title.setText("线条灯");
            popupWindow.dismiss();
            ivArrow.setImageResource(R.mipmap.icon_bg_pop_dow);
        });

        classromelayout.setOnClickListener(view13 -> {
            recyclerView.setVisibility(View.GONE);
            lineReclView.setVisibility(View.GONE);
            qbRecyclView.setVisibility(View.VISIBLE);
            title.setText("球泡灯");
            popupWindow.dismiss();
            ivArrow.setImageResource(R.mipmap.icon_bg_pop_dow);
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    public void updateData(SProject sprojects) {
        SProject sproject = sprojects;

        if (sproject == null)
            return;

        SProject.SProject_PD_PLAY play = sproject.pdPlay;

        int list = play.list;
        int act = play.act;

        if (list >= sproject.listPD.size())
            return;
        if (sproject.modePD == 0)    //STOP
        {
        } else {
        }
        if (play.frameAll - play.frameNow <= 8) {
            if (selectpostion == -1) {
                playEffect(selectpostion + 2, 0);
            } else {
                playEffect(selectpostion + 1, 0);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
