package meekux.grandar.com.meekuxpjxroject.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.adapter.LampListAdapte;
import meekux.grandar.com.meekuxpjxroject.entity.LampListHeaderBean;

/**
 * 顶灯页面
 */
public class TopListFragment extends Fragment {
    private static final String TAG = "LampListFragment";
    @Bind(R.id.homeGridView)
    RecyclerView mRecycleLamplist;
    @Bind(R.id.fragment_recycle_img_empty)
    ImageView img_empty;
    private List<LampListHeaderBean> mLampListBeen;
    private List<MyDeviceBean> myDeviceList;
    private LampListAdapte mLampListAdapter;
    private String curSn;
    private boolean isUpdate;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lamp_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

}
