package meekux.grandar.com.meekuxpjxroject.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import meekux.grandar.com.meekuxpjxroject.R;

/**
 * Author:SeanLim
 * Created by Time on: 2017/9/18
 * 类名：HouseroomFragment
 */
public class HouseroomFragment extends Fragment {
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bed_room, null);
        return view;
    }

}
