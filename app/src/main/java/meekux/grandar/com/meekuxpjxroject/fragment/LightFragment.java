package meekux.grandar.com.meekuxpjxroject.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socks.library.KLog;

import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import meekux.grandar.com.meekuxpjxroject.GlobalApplication;
import meekux.grandar.com.meekuxpjxroject.MainActivity;
import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.activity.AddSceneActivity;
import meekux.grandar.com.meekuxpjxroject.activity.SettingActivity;
import meekux.grandar.com.meekuxpjxroject.adapter.FragmentVpAdapter;
import meekux.grandar.com.meekuxpjxroject.adapter.LitFragmentVpAdapter;
import meekux.grandar.com.meekuxpjxroject.adapter.recycler.RecyclerAdapter;
import meekux.grandar.com.meekuxpjxroject.adapter.recycler.RecyclerViewHolder;
import meekux.grandar.com.meekuxpjxroject.service.PlayLightSongListService;
import meekux.grandar.com.meekuxpjxroject.songdata.database.GrandarUtils;
import meekux.grandar.com.meekuxpjxroject.songdata.database.MyConstant;
import meekux.grandar.com.meekuxpjxroject.utils.DataManager;
import meekux.grandar.com.meekuxpjxroject.utils.DividerGridItemDecoration;
import meekux.grandar.com.meekuxpjxroject.utils.OnRecyclerItemClickListener;
import meekux.grandar.com.meekuxpjxroject.utils.RecyItemTouchHelperCallback;
import meekux.grandar.com.meekuxpjxroject.utils.SharedPutils;
import meekux.grandar.com.meekuxpjxroject.view.DialogLight;
import meekux.grandar.com.meekuxpjxroject.view.MyDialog;

import static android.widget.Toast.LENGTH_LONG;

public class LightFragment extends Fragment {
    private View viewContent;
    private ViewPager vp;
    private TabLayout tab;
    private List<String> tablist = new ArrayList<>();
    private List<View> views;
    private FragmentVpAdapter fragmentVpAdapter;
    private LayoutInflater inflater;
    private View inflaterView;
    private RecyclerView mRecyclerView;
    private List<String> mStringList;
    private RecyAdapter mRecyAdapter;
    private RecyclerView litEdRecy;
    private List<String> litEdRecyList;
    private RecyclerAdapter litEdRecyAdapter;
    private TextView tvLitNme;
    private TextView tvSpec;
    private CheckBox checkBox;
    private MyDialog myDialog;
    private String nameLight;
    private GridLayoutManager layoutManager;
    private boolean isFirstClick;
    private DialogLight dialogLight;
    private ImageView ivLiState;
    private Object object;
    private String objectM;
    private TextView tvLightName;
    private ImageView addScene;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.viewContent = inflater.inflate(R.layout.fragment_lightcontr, container, false);
        this.initTabStr();
        this.initViews();
        this.tab = viewContent.findViewById(R.id.adddevice_view_tabLayout);
        this.vp = viewContent.findViewById(R.id.adddevice_view_vp);
        this.addScene = viewContent.findViewById(R.id.tv_add);
        this.tab.setTabMode(TabLayout.MODE_SCROLLABLE);
        this.fragmentVpAdapter = new FragmentVpAdapter(views, tablist);
        this.vp.setAdapter(fragmentVpAdapter);
        this.tab.setupWithViewPager(vp);
        this.tab.setTabsFromPagerAdapter(fragmentVpAdapter);
        this.addScene.setOnClickListener(view -> addSceneVoid());
        return viewContent;
    }

    private void addSceneVoid() {
        startActivity(new Intent(getActivity(), AddSceneActivity.class));
    }

    private void initViews() {
        View view1 = adddView();
        View view2 = adddView();
        View view3 = adddView();
        View view4 = adddView();
        View view5 = adddView();
        View view6 = adddView();
        views = new ArrayList<>();
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
        views.add(view5);
        views.add(view6);
    }

    private View adddView() {
        inflater = LayoutInflater.from(getActivity().getBaseContext());
        inflaterView = inflater.inflate(R.layout.fragment_lamp_list, null);
        mRecyclerView = inflaterView.findViewById(R.id.recyclerView);
        litEdRecy = inflaterView.findViewById(R.id.reView_light_editor);
        this.bindRecycleView();
        this.initRecy();
        return inflaterView;
    }

    private void initTabStr() {
        tablist.clear();
        tablist.add("客厅");
        tablist.add("厨房");
        tablist.add("厕所");
        tablist.add("主卧");
        tablist.add("次卧");
        tablist.add("餐厅");
    }

    private void initRecy() {
        if (mStringList == null) {
            mStringList = new ArrayList<>();
        }
        mStringList.addAll(DataManager.getData(25 - mStringList.size()));
        mRecyAdapter = new RecyAdapter(R.layout.item_gridview, mStringList, true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        RecyItemTouchHelperCallback itemTouchHelperCallback = new RecyItemTouchHelperCallback(mRecyAdapter, false, true);
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder) {
                RecyAdapter.ViewHolder viewHolder1 = (RecyAdapter.ViewHolder) viewHolder;
                tvLightName = viewHolder1.mTextView;
                CheckBox checkBox = viewHolder1.checkBox;
                checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    if (isChecked) {
                        if (nameLight == null) {
                            Toast.makeText(getActivity(), "先选灯再进行部署", LENGTH_LONG).show();
                            checkBox.setBackgroundColor(Color.argb(0, 0, 0, 0));
                        } else {
                            if (isFirstClick) {
                                dialogLight = new DialogLight(getActivity());
                                dialogLight.setTitle("温馨提示");
                                dialogLight.setMessage(getString(R.string.tv_light_dialog_message));
                                dialogLight.setYesOnclickListener("确定", () -> dialogLight.dismiss());
                                dialogLight.show();
                            } else {
                                try {
                                    isFirstClick = true;
                                    tvLightName.setText(nameLight);
                                    checkBox.setButtonDrawable(R.drawable.selector_light_c);
                                    String deploySn = SharedPutils.getString(getActivity(), "deviceSn", "");
                                    String substring = deploySn.substring(0, 4);
                                    SharedPutils sharedPutils = new SharedPutils();
                                    sharedPutils.putString(getActivity(), "deploySn", substring);
                                    KLog.e("deploySn发送Sn:" + substring);
                                } catch (Exception e) {

                                }

                            }
                        }
                    } else {
                        if (nameLight == null) {
                            Toast.makeText(getActivity(), "先选灯再进行部署", Toast.LENGTH_SHORT).show();
                        } else {
                            canselLight();
                        }
                    }
                });
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder viewHolder) {
                if (viewHolder.getLayoutPosition() != 0) {
                    itemTouchHelper.startDrag(viewHolder);
                }
            }
        });
        mRecyclerView.setAdapter(mRecyAdapter);
    }


    /**
     * 取消布灯
     */
    private void canselLight() {
        SharedPutils sharedPutils = new SharedPutils();
        sharedPutils.putString(getActivity(), "deploySn", null);
        ivLiState.setVisibility(View.GONE);
        tvLightName.setText("");
        nameLight = null;
        try {
            String deploySn = SharedPutils.getString(getActivity(), "deploySn", "");
            GlobalApplication.getInstance().setStopSendFrame(true);
            MainActivity.netSocket.sendPlayColor(0, 0, 0, 0);
            if (PlayLightSongListService.mediaPlayer != null && PlayLightSongListService.mediaPlayer.isPlaying()) {
                GrandarUtils.stopMp3Data();
            }
            new Thread(() -> {
                GrandarUtils.stopMp3Data();
                Socket device = GlobalApplication.getInstance().getDevice(deploySn);
                if (device != null) {
                    byte[] data = new byte[1];
                    data[0] = 50;
                    GrandarUtils.sendFrameOff(MyConstant.POWER_OFF, data, deploySn);
                }
            }).start();
        } catch (Exception e) {
        }
    }


    private void bindRecycleView() {
        this.getLihtData();
        this.litEdRecyAdapter = new RecyclerAdapter<String>(getActivity(), litEdRecyList,
                R.layout.itme_ed_light) {
            @Override
            public void convert(RecyclerViewHolder helper, String item, int position) {
                tvLitNme = helper.getView(R.id.tv_li_name);
                tvSpec = helper.getView(R.id.tv_spec);
                checkBox = helper.getView(R.id.checkBox);
                ivLiState = helper.getView(R.id.iv_li_state);
                if (position == 0) {
                    tvSpec.setText("1m");
                    tvLitNme.setText("灯带1");
                    checkBox.setButtonDrawable(R.drawable.selector_itme_lightx);
                } else if (position == 1) {
                    tvSpec.setVisibility(View.GONE);
                    tvLitNme.setText("灯泡2");
                } else if (position == 2) {
                    tvSpec.setVisibility(View.GONE);
                    tvLitNme.setText("灯泡3");
                } else if (position == 3) {
                    tvSpec.setVisibility(View.GONE);
                    tvLitNme.setText("灯泡4");
                } else if (position == 4) {
                    checkBox.setButtonDrawable(R.drawable.selector_itme_lightx);
                    tvLitNme.setText("灯带5");
                    tvSpec.setText("1m");
                } else if (position == 5) {
                    tvSpec.setVisibility(View.GONE);
                    tvLitNme.setText("灯泡6");
                } else if (position == 6) {
                    checkBox.setButtonDrawable(R.drawable.selector_itme_lightx);
                    tvLitNme.setText("灯带7");
                    tvSpec.setText("1m");
                } else if (position == 7) {
                    checkBox.setButtonDrawable(R.drawable.selector_itme_lightx);
                    tvLitNme.setText("灯带8");
                    tvSpec.setText("1m");
                } else if (position == 8) {
                    tvSpec.setVisibility(View.GONE);
                    tvLitNme.setText("灯泡9");
                } else if (position == 9) {
                    tvSpec.setVisibility(View.GONE);
                    tvLitNme.setText("灯泡10");
                }
                tvLitNme.setOnClickListener(view1 -> {
                    myDialog = new MyDialog(getActivity());
                    if (position == 0 || position == 4 || position == 6 || position == 7) {
                        myDialog.tipsDialog(getActivity(), R.string.tv_light_num, obj -> {
                            object = obj;
                            objectM = object.toString();
                            String fixLamp = objectM.substring(0, 1);
                            int intFixLamp = Integer.parseInt(fixLamp) * 10;
                            String deploySn = SharedPutils.getString(getActivity(), "deviceSn", "");
                            new Thread(() -> {
                                byte[] SendBuff1 = {(byte) 0xAA, (byte) 0x55, 0x01, 0x11, (1 & 0xFF), (1 >> 8 & 0xFF), (byte) intFixLamp};
                                GrandarUtils.sendFrameOff(SendBuff1, null, deploySn);
                                KLog.e("intFixLamp" + intFixLamp);
                            }).start();
                            tvSpec.setText("");
                        });
                    } else if (position == 1 || position == 2 || position == 3 || position == 5 || position == 8 || position == 9) {
                        myDialog.commonRenameDialog(getActivity(), obj -> {
                            tvLitNme.setText("" + obj);
                        });
                    }
                });

                checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    int inPosition = position + 1;
                    if (isChecked) {
                        if (position == 0 || position == 4 || position == 6 || position == 7) {
                            isFirstClick = false;
                            nameLight = "灯带" + inPosition;
                        } else {
                            isFirstClick = false;
                            nameLight = "灯泡" + inPosition;
                        }
                    } else {
                        nameLight = null;
                    }
                });
            }
        };
        this.litEdRecy.setHasFixedSize(true);
        this.layoutManager = new GridLayoutManager(getActivity(), 5);
        this.litEdRecy.setLayoutManager(layoutManager);
        this.litEdRecy.setAdapter(this.litEdRecyAdapter);

    }

    private void getLihtData() {
        litEdRecyList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            litEdRecyList.add("" + i);
        }
    }
}
