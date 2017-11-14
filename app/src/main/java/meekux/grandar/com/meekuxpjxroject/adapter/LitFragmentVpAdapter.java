package meekux.grandar.com.meekuxpjxroject.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Author:SeanLim
 * Created by Time on: 2017/9/22
 * 类名：LitFragmentVpAdapter
 */
public class LitFragmentVpAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mtList;
    private List<String> mTitleList;

    public LitFragmentVpAdapter(FragmentManager fm, List<Fragment> fragmentList , List<String> list) {
        super(fm);
        mtList = fragmentList;
        mTitleList=list;
    }

    @Override
    public Fragment getItem(int position) {
        return mtList.get(position);
    }

    @Override
    public int getCount() {
        return mtList.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }
}
