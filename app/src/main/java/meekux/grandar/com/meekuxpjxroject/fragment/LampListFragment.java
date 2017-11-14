package meekux.grandar.com.meekuxpjxroject.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.socks.library.KLog;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.DeviceDbUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import meekux.grandar.com.meekuxpjxroject.GlobalApplication;
import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.activity.LampDetailActivity;
import meekux.grandar.com.meekuxpjxroject.adapter.LampListAdapter;
import meekux.grandar.com.meekuxpjxroject.entity.LampListHeaderBean;
import meekux.grandar.com.meekuxpjxroject.utils.IConstant;
import meekux.grandar.com.meekuxpjxroject.utils.SharedPreferencesUtils;

/**
 * 落地灯页面
 */
public class LampListFragment extends Fragment {
    private static final String TAG = "LampListFragment";
    @Bind(R.id.homeGridView)
    GridView mRecycleLamplist;
    @Bind(R.id.fragment_recycle_img_empty)
    ImageView img_empty;
    private List<LampListHeaderBean> mLampListBeen;
    private List<MyDeviceBean> myDeviceList;
    private LampListAdapter mLampListAdapter;
    private String curSn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lamp_list, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }
    private void initView(){
        curSn = SharedPreferencesUtils.getinstance().getStringValue(IConstant.LAMPSN);
        mLampListBeen = new ArrayList<>();
        myDeviceList = new ArrayList<>();
        mLampListAdapter = new LampListAdapter(mLampListBeen, curSn);

        myDeviceList = DeviceDbUtil.getInstance().queryTimeAll();
        int size = myDeviceList.size();
        KLog.e(size);
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                LampListHeaderBean bean = new LampListHeaderBean(myDeviceList.get(i));
                mLampListBeen.add(bean);
            }
            mRecycleLamplist.setVisibility(View.VISIBLE);
            img_empty.setVisibility(View.GONE);
        }


//        mRecycleLamplist.setAdapter(mLampListAdapter);
//        mRecycleLamplist.setLayoutManager(new LinearLayoutManager(getActivity()));

        mLampListAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            Intent mintent = new Intent();
            switch (view.getId()) {
                case R.id.item_content:
                    mintent.putExtra("name", mLampListBeen.get(position).t.getName());
                    mintent.putExtra("date", mLampListBeen.get(position).t.getDate());
                    mintent.putExtra("sn", mLampListBeen.get(position).t.getSn());
                    mintent.putExtra("ip", mLampListBeen.get(position).t.getIp());
                    mintent.putExtra("id", mLampListBeen.get(position).t.getSid());
                    mintent.putExtra("position", position);
                    mintent.setClass(getActivity(), LampDetailActivity.class);
                    startActivityForResult(mintent, 0);
                    break;
                case R.id.lamplist_text_contact:
                    LampListHeaderBean bean = mLampListBeen.get(position);
                    String sn = bean.t.getSn();
                    String ip = bean.t.getIp();
                    SharedPreferencesUtils.getinstance().setStringValue(IConstant.LAMPSN, sn);
                    SharedPreferencesUtils.getinstance().setStringValue(IConstant.LAMPIP, ip);
                    GlobalApplication.getInstance().exit();
//                        startTcpClientService(null, ip, sn);
                    break;
            }
            return true;
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;
        switch (requestCode) {
            case 0:
                int flag = data.getIntExtra("flag", -1);
                int position = data.getIntExtra("position", -1);
                //表示更新名字
                if (flag == 1) {
                    String name = data.getStringExtra("name");
                    mLampListBeen.get(position).t.setName(name);
                    mLampListAdapter.notifyDataSetChanged();
                }
                //表示删除
                if (flag == 0) {
                    long id = data.getLongExtra("id", 0);

                    KLog.e(id + "..." + position);
                    DeviceDbUtil.getInstance().deleteTime(id);
                    mLampListBeen.remove(position);
                    mLampListAdapter.notifyDataSetChanged();
                }

                break;
        }


    }

}
