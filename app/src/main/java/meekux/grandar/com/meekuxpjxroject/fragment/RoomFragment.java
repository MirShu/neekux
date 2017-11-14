package meekux.grandar.com.meekuxpjxroject.fragment;


import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.adapter.LampListAdapter;
import meekux.grandar.com.meekuxpjxroject.entity.LampListHeaderBean;

/**
 * A simple {@link Fragment} subclass.
 * 客厅页面
 */
public class RoomFragment extends Fragment {
    @Bind(R.id.recycle_lamplist)
    RecyclerView mRecycleLamplist;
    @Bind(R.id.fragment_recycle_img_empty)
    ImageView img_empty;
    @Bind(R.id.nodevicetitle)
    TextView text_empty;
    private List<LampListHeaderBean> mLampListBeen;
    private List<MyDeviceBean> myDeviceList;
    private LampListAdapter mLampListAdapter;
    private String curSn;
    private IntentFilter mFilter;
    private String sn;
    private String ip;

    public RoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
//    private void initView() {
//        mFilter = new IntentFilter();
//        mFilter.addAction(MyConstant.switchtomain);
//        mFilter.addAction(MyConstant.ACTION_RC_SOCKET_CLOSE);
//        mFilter.setPriority(100);
//        getActivity().registerReceiver(myReceiver, mFilter);
//        curSn = SharedPreferencesUtils.getinstance().getStringValue(IConstant.LAMPSN);
//        mLampListBeen = new ArrayList<>();
//        myDeviceList = new ArrayList<>();
//        mLampListAdapter = new LampListAdapter(mLampListBeen, curSn);
//
//        myDeviceList = DeviceDbUtil.getInstance().queryTimeAll();
//        int size = myDeviceList.size();
//        KLog.e(size);
//        if (size != 0) {
//            for (int i = 0; i < size; i++) {
//                LampListHeaderBean bean = new LampListHeaderBean(myDeviceList.get(i));
//                mLampListBeen.add(bean);
//            }
//            mRecycleLamplist.setVisibility(View.VISIBLE);
//            img_empty.setVisibility(View.GONE);
//            text_empty.setVisibility(View.GONE);
//        }
//
//
//        mRecycleLamplist.setAdapter(mLampListAdapter);
//        mRecycleLamplist.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//        mLampListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
//            @Override
//            public boolean onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//                Intent mintent = new Intent();
//                switch (view.getId()) {
//                    case R.id.item_content:
//                        mintent.putExtra("name", mLampListBeen.get(position).t.getName());
//                        mintent.putExtra("date", mLampListBeen.get(position).t.getDate());
//                        mintent.putExtra("sn", mLampListBeen.get(position).t.getSn());
//                        mintent.putExtra("ip", mLampListBeen.get(position).t.getIp());
//                        mintent.putExtra("id", mLampListBeen.get(position).t.getId());
//                        mintent.putExtra("position", position);
//                        mintent.setClass(getActivity(), LampDetailActivity.class);
//                        startActivityForResult(mintent, 0);
//                        break;
//                    case R.id.lamplist_text_contact:
//                        LampListHeaderBean bean = mLampListBeen.get(position);
//                        sn = bean.t.getSn();
//                        ip = bean.t.getIp();
//                        if (TextUtils.isEmpty(ip)) break;
//                        startTcpClientService(null, ip, sn, 1);
//                        break;
//                }
//                return true;
//            }
//        });
//
//    }

}
