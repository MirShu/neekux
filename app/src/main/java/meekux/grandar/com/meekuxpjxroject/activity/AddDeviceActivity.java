package meekux.grandar.com.meekuxpjxroject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.adapter.AddDeviceVpAdapter;
import meekux.grandar.com.meekuxpjxroject.view.MyDialog;
import meekux.grandar.com.meekuxpjxroject.view.TopView;


/**
 * Created by baixiaoming on 2017/3/14 18:53
 * Function 我的设备
 */

public class AddDeviceActivity extends FragmentActivity {

    TopView mRightView;
    private TabLayout tableLayout;
    private ViewPager vp;
    private List<Fragment> list;
    private AddDeviceVpAdapter adapter;
    private String[] strings;
    protected MyDialog myDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.layout_activity_adddevice);
        init();
        click();
    }

    protected void init() {
//        myDialog = new MyDialog(this);
//        mRightView=(TopView) findViewById(R.id.right_view);
//        mRightView.setImageVis(R.mipmap.icon_menu);
//        strings = getResources().getStringArray(R.array.text_tablayout);
//        list = new ArrayList<>();
//        vp = (ViewPager) findViewById(R.id.adddevice_view_vp);
//        tableLayout = (TabLayout) findViewById(R.id.adddevice_view_tabLayout);
//        // 客厅
//        RoomFragment roomFragment = new RoomFragment();
//        // 餐厅
//        RestaurantFragment restaurantFragment = new RestaurantFragment();
//        // 主卧
//        BedRoomFragment bedRoomFragment = new BedRoomFragment();
//        // 次卧
//        SecondFragment secondFragment = new SecondFragment();
//        // 厨房
//        KitchenFragment kitchenFragment = new KitchenFragment();
//        // 卫生间
//        BathroomFragment bathroomFragment = new BathroomFragment();
//        list.add(roomFragment);
//        list.add(restaurantFragment);
//        list.add(bedRoomFragment);
//        list.add(secondFragment);
//        list.add(kitchenFragment);
//        list.add(bathroomFragment);
//        adapter = new AddDeviceVpAdapter(getSupportFragmentManager(), list, strings);
//        vp.setAdapter(adapter);
//        tableLayout.setTabMode(TabLayout.MODE_FIXED);
//        tableLayout.setupWithViewPager(vp);

    }


    protected void click() {
        mRightView.setOnTopviewListener(() -> {
            final View topview = mRightView.findViewById(R.id.topview_view_operate);
            myDialog.setPopWindow(AddDeviceActivity.this, topview, new MyDialog.popWiondowCallback() {
                @Override
                public void addDevice() {
                    startActivity(new Intent(AddDeviceActivity.this, WifiContactActivity.class));
                }

                @Override
                public void searchDevice() {
                    startActivity(new Intent(AddDeviceActivity.this, SearchDeviceActivity.class));
                }
            });

        });
    }


}
