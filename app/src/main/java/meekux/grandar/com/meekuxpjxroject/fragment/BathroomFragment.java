package meekux.grandar.com.meekuxpjxroject.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.adapter.recycler.RecyclerAdapter;
import meekux.grandar.com.meekuxpjxroject.adapter.recycler.RecyclerViewHolder;
import meekux.grandar.com.meekuxpjxroject.utils.DataManager;
import meekux.grandar.com.meekuxpjxroject.utils.DividerGridItemDecoration;
import meekux.grandar.com.meekuxpjxroject.utils.OnRecyclerItemClickListener;
import meekux.grandar.com.meekuxpjxroject.utils.RecyItemTouchHelperCallback;
import meekux.grandar.com.meekuxpjxroject.view.DialogLight;
import meekux.grandar.com.meekuxpjxroject.view.MyDialog;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Author:SeanLim
 * Created by Time on: 2017/9/12
 * 类名：BathroomFragment   客厅fragment
 */
public class BathroomFragment extends Fragment {
    private View view;
    private RecyclerView mRecyclerView;
    private List<String> mStringList;
    private RecyAdapter mRecyAdapter;
    private RecyclerView litEdRecy;
    private List<String> litEdRecyList;
    private RecyclerAdapter litEdRecyAdapter;
    private TextView tvLitNme;
    private TextView tv_spec;
    private CheckBox checkBox;
    private MyDialog myDialog;
    private String nameLight;
    private String tvString;
    private GridLayoutManager layoutManager;
    private CheckBox checkBoxTop;
    private RecyItemTouchHelperCallback itemTouchHelperCallback;
    private ItemTouchHelper itemTouchHelper;
    private boolean isFirstClick;
    private DialogLight dialogLight;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_bathroom, null);
        this.mRecyclerView = view.findViewById(R.id.recy);
        this.litEdRecy = view.findViewById(R.id.reView_light_editor);
        this.bindRecycleView();
        this.initRecy();
        return view;
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
        itemTouchHelperCallback = new RecyItemTouchHelperCallback(mRecyAdapter, false, true);
        itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder) {
                RecyAdapter.ViewHolder viewHolder1 = (RecyAdapter.ViewHolder) viewHolder;
                TextView tvLightName = viewHolder1.mTextView;
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
                                isFirstClick = true;
                                tvLightName.setText(nameLight);
                                checkBox.setButtonDrawable(R.drawable.selector_light_c);

                            }
                        }
                    } else {
                        if (nameLight == null) {
                            Toast.makeText(getActivity(), "先选灯再进行部署", LENGTH_LONG).show();
                        } else {
                            tvLightName.setText("");
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


    private void bindRecycleView() {
        this.getLihtData();
        this.litEdRecyAdapter = new RecyclerAdapter<String>(getActivity(), litEdRecyList,
                R.layout.itme_ed_light) {
            @Override
            public void convert(RecyclerViewHolder helper, String item, int position) {
                tvLitNme = helper.getView(R.id.tv_li_name);
                tv_spec = helper.getView(R.id.tv_spec);
                checkBox = helper.getView(R.id.checkBox);
                if (position == 0) {
                    tv_spec.setText("12m");
                    tvLitNme.setText("灯带1");
                    checkBox.setButtonDrawable(R.drawable.selector_itme_lightx);
                } else if (position == 1) {
                    tv_spec.setVisibility(View.GONE);
                    tvLitNme.setText("灯泡2");
                } else if (position == 2) {
                    tv_spec.setVisibility(View.GONE);
                    tvLitNme.setText("灯泡3");
                } else if (position == 3) {
                    tv_spec.setVisibility(View.GONE);
                    tvLitNme.setText("灯泡4");
                } else if (position == 4) {
                    checkBox.setButtonDrawable(R.drawable.selector_itme_lightx);
                    tvLitNme.setText("灯带5");
                    tv_spec.setText("7m");
                } else if (position == 5) {
                    tv_spec.setVisibility(View.GONE);
                    tvLitNme.setText("灯泡6");
                } else if (position == 6) {
                    checkBox.setButtonDrawable(R.drawable.selector_itme_lightx);
                    tvLitNme.setText("灯带7");
                    tv_spec.setText("8m");
                } else if (position == 7) {
                    checkBox.setButtonDrawable(R.drawable.selector_itme_lightx);
                    tvLitNme.setText("灯带8");
                    tv_spec.setText("4m");
                } else if (position == 8) {
                    tv_spec.setVisibility(View.GONE);
                    tvLitNme.setText("灯泡9");
                } else if (position == 9) {
                    tv_spec.setVisibility(View.GONE);
                    tvLitNme.setText("灯泡10");
                }

                tvLitNme.setOnClickListener(view1 -> {
                    myDialog = new MyDialog(getActivity());
                    if (position == 0 || position == 4 || position == 6 || position == 7) {
                        myDialog.tipsDialog(getActivity(), R.string.tv_light_num, obj -> {

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
        }
        return super.onOptionsItemSelected(item);
    }

}
