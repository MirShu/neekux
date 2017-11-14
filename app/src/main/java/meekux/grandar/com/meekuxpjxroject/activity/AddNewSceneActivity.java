package meekux.grandar.com.meekuxpjxroject.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.adapter.recycler.RecyclerAdapter;
import meekux.grandar.com.meekuxpjxroject.adapter.recycler.RecyclerViewHolder;

/**
 * Author:SeanLim
 * Created by Time on: 2017/9/25
 * 类名：AddNewSceneActivity
 */
public class AddNewSceneActivity extends BaseActivity {
    private RecyclerAdapter recyclerAdapter;
    private List<String> recyclerList;
    private RecyclerView recyclerView;
    private ImageView ivBack;

    @Override
    protected int getResouseID() {
        return R.layout.activity_add_new_scene;

    }

    @Override
    protected void init() {
        this.recyclerView = findViewById(R.id.recyclerView);
        this.ivBack = findViewById(R.id.iv_back);
        this.ivBack.setOnClickListener(view -> finish());
        this.bindRecycleView();
    }

    private void bindRecycleView() {
        this.getData();
        this.recyclerAdapter = new RecyclerAdapter<String>(this, recyclerList,
                R.layout.item_add_new_scen) {
            @Override
            public void convert(RecyclerViewHolder helper, String item, int position) {
                TextView tvName = helper.getView(R.id.tv_name);
                if (position == 0) {
                    tvName.setText("健身房");
                } else if (position == 1) {
                    tvName.setText("阳台");
                } else if (position == 2) {
                    tvName.setText("书房");
                } else if (position == 3) {
                    tvName.setText("次卧");
                } else if (position == 4) {
                    tvName.setText("阳台");
                } else if (position == 6) {
                    tvName.setText("卫生间");
                }
            }
        };
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false));
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
