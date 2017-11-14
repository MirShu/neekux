package meekux.grandar.com.meekuxpjxroject.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by baixiaoming on 2017/3/14 19:12
 * Function tablayout适配器
 */

public class AddDeviceVpAdapter extends FragmentPagerAdapter {
    private List<Fragment> list;
    private String[] strings;

    public AddDeviceVpAdapter(FragmentManager fm, List<Fragment> list, String[] strings) {
        super(fm);
        this.list=list;
        this.strings = strings;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }


    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        return view == object;
//    }

//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        Fragment view = list.get(position);
//        container.addView(view);
//        return view;
//    }

    //    @Override
//    public Fragment getItem(int position) {
//        return list.get(position);
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView((View) object);
//    }
//
    @Override
    public CharSequence getPageTitle(int position) {
        return strings[position];
    }
}
