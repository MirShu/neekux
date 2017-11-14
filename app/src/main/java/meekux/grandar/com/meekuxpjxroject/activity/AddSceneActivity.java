package meekux.grandar.com.meekuxpjxroject.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.adapter.recycler.RecyclerAdapter;
import meekux.grandar.com.meekuxpjxroject.adapter.recycler.RecyclerViewHolder;

/**
 * Author:SeanLim
 * Created by Time on: 2017/9/22
 * 类名：AddSceneActivity
 */
public class AddSceneActivity extends BaseActivity {
    private RecyclerAdapter recyclerAdapter;
    private List<String> recyclerList;
    private RecyclerView recyclerView;
    private TextView tvNewAdd;
    private ImageView ivBack;

    @Override
    protected int getResouseID() {
        return R.layout.activity_add_scene;
    }

    @Override
    protected void init() {
        this.recyclerView = findViewById(R.id.recyclerView);
        this.tvNewAdd = findViewById(R.id.tv_new_add);
        this.ivBack = findViewById(R.id.iv_back);
        this.ivBack.setOnClickListener(view -> finish());
        this.tvNewAdd.setOnClickListener(view -> startActivity(new Intent(AddSceneActivity.this, AddNewSceneActivity.class)));
        this.bindRecycleView();
    }

    private void bindRecycleView() {
        this.getData();
        this.recyclerAdapter = new RecyclerAdapter<String>(this, recyclerList,
                R.layout.item_scene) {
            @Override
            public void convert(RecyclerViewHolder helper, String item, int position) {
                TextView tvName = helper.getView(R.id.tv_name);
                CheckBox sceneCheck = helper.getView(R.id.scene_check);
                if (position == 0) {
                    tvName.setText("客厅");
                } else if (position == 1) {
                    tvName.setText("厨房");
                } else if (position == 2) {
                    tvName.setText("厕所");
                } else if (position == 3) {
                    tvName.setText("主卧");
                } else if (position == 4) {
                    tvName.setText("次卧");
                } else if (position == 6) {
                    tvName.setText("餐厅");
                }
                tvName.setOnClickListener(view -> myDialog.commonRenameDialog(AddSceneActivity.this, obj -> {
                    tvName.setText("" + obj);
                }));
                sceneCheck.setOnCheckedChangeListener((compoundButton, isCheck) -> {
                    if (isCheck) {

                    } else {

                    }
                });
            }
        };
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new

                GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false));
        this.recyclerView.setAdapter(this.recyclerAdapter);

    }

    private void getData() {
        recyclerList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            recyclerList.add("" + i);
        }
    }

    @Override
    protected void click() {

    }
}