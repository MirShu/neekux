package meekux.grandar.com.meekuxpjxroject.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.FolderBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MusicListSong;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.DeviceDbUtil;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.FolderUtil;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.MusicDbUtil;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import meekux.grandar.com.meekuxpjxroject.GlobalApplication;
import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.adapter.recycler.RecyclerAdapter;
import meekux.grandar.com.meekuxpjxroject.adapter.recycler.RecyclerViewHolder;
import meekux.grandar.com.meekuxpjxroject.service.PlayLightSongListService;
import meekux.grandar.com.meekuxpjxroject.songdata.database.GrandarUtils;


/**
 * Created by @author:xuqunwang on 17/1/4.
 * desceription:手动控制
 */
public class AutoContolFragment extends Fragment {
    private View view;
    private RecyclerAdapter recyclerAdapter;
    private List<String> recyclerList;
    private RecyclerView recyclerView;
    private int recSta;
    private List<MusicListSong> path_list;
    private List<MyDeviceBean> myDeviceBeen;
    private List<FolderBean> list;
    private int isPlayingState;
    private CheckBox checkBoxState;
    private TextView tvState;
    private boolean isPasue = false; //是否暂停

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.autocontrol, null);
        this.recyclerView = view.findViewById(R.id.recyclerView);
        myDeviceBeen = DeviceDbUtil.getInstance().queryTimeAll();
        list = FolderUtil.getInstance().queryTimeAll();
        this.bindRecycleView();
        return view;
    }

    private void bindRecycleView() {
        this.getData();
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
                    helper.setText(R.id.main_music_item_title, "深蓝海洋");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.music_item_icon_01);
                    helper.setImageResource(R.id.music_item_icon, R.mipmap.music_icon);
                    helper.setText(R.id.main_music_item_des, "黑暗之路");
                } else if (position == 1) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 2) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "霞之美，渔之欢");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.purple);
                    helper.setImageResource(R.id.music_item_icon, R.mipmap.music_icon);
                    helper.setText(R.id.main_music_item_des, "渔舟唱完");
                } else if (position == 2) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 3) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "人生回忆");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.music_item_icon_05);
                    helper.setImageResource(R.id.music_item_icon, R.mipmap.music_icon);
                    helper.setText(R.id.main_music_item_des, "回忆");
                } else if (position == 3) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 4) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "天堂晨曦（标准唤醒）");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.red);
                    helper.setImageResource(R.id.music_item_icon, R.mipmap.music_icon);
                    helper.setText(R.id.main_music_item_des, "最近的天堂");
                } else if (position == 4) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 5) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "因你而在");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.music_item_icon_04);
                    helper.setImageResource(R.id.music_item_icon, R.mipmap.music_icon);
                    helper.setText(R.id.main_music_item_des, "柔情的玫瑰酒");
                } else if (position == 5) {
                    tvState.setVisibility(View.GONE);
                    if (isPlayingState == 6) {
                        checkBoxState.setVisibility(View.VISIBLE);
                    } else {
                        checkBoxState.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.main_music_item_title, "情色夜晚");
                    helper.setImageResource(R.id.main_music_item_img, R.mipmap.music_item_icon_02);
                    helper.setImageResource(R.id.music_item_icon, R.mipmap.music_icon);
                    helper.setText(R.id.main_music_item_des, "暖光");
                } else if (position == 6) {
                    tvState.setVisibility(View.VISIBLE);
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
                recSta = 5;
                isPlayingState = 5;
                path_list = MusicDbUtil.getInstance().queryTimeBy(1);
                musicPlay(path_list, true, list.get(0).getPath());
                recyclerAdapter.notifyDataSetChanged();
            } else if (position == 5) {
                GrandarUtils.stopMp3Data();
                recSta = 6;
                isPlayingState = 6;
                path_list = MusicDbUtil.getInstance().queryTimeBy(1);
                musicPlay(path_list, true, list.get(0).getPath());
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


    private void getData() {
        recyclerList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            recyclerList.add("" + i);
        }
    }


    private void musicPlay(final List<MusicListSong> data, boolean ismusic, final String path) {
        GlobalApplication.getInstance().setData((ArrayList<MusicListSong>) data);
        if (recSta == 1) {
            String path_name = path + data.get(6).getName();
            test(path_name, true, "", 0, 2);
        } else if (recSta == 2) {
            String path_name = path + data.get(8).getName();
            test(path_name, true, "", 0, 2);
        } else if (recSta == 3) {
            String path_name = path + data.get(9).getName();
            test(path_name, true, "", 0, 2);
        } else if (recSta == 4) {
            String path_name = path + data.get(10).getName();
            test(path_name, true, "", 0, 2);
        } else if (recSta == 5) {
            String path_name = path + data.get(16).getName();
            test(path_name, true, "", 0, 2);
        } else if (recSta == 6) {
            String path_name = path + data.get(17).getName();
            test(path_name, true, "", 0, 2);
        }
    }

    private void test(String path, boolean ismusics, String name, int position, int flag) {
        if (ismusics) {
            GrandarUtils.playMusic(path, name, flag, position);
            Intent intent = new Intent();
            intent.setAction("meekux.grandar.com.meekuxpjxroject");
            intent.putExtra("playingState", 1);
            getActivity().sendBroadcast(intent);
            Toast.makeText(getActivity(), "发送广播成功", Toast.LENGTH_SHORT).show();
        } else {
            //没有音乐的方法
//            StartSendLight(path, name);
//            GlobalApplication.getInstance().setPlayCount(GlobalApplication.getInstance().getPlayCount() + 1);
        }
    }
}
