package meekux.grandar.com.meekuxpjxroject.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import meekux.grandar.com.meekuxpjxroject.R;


/**
 * Created by @author:liuqiankun on 17/1/4.
 * desceription:
 */
public class ControlFragment extends Fragment implements View.OnClickListener {
    private Button singlecontrol;//单控
    private Button autom;//自动
    private Button manul;//手动
    View view;
    private SingleControlFragment singleControlFragment;
    private AutoContolFragment autoContolFragment;
    private ManulControlFragment manulControlFragment;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_control_layout, null);
        initViews();
//      selectItem();
        if (manulControlFragment == null) {
            manulControlFragment = new ManulControlFragment();
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.layoutcontent, manulControlFragment)
                .commit();
        return view;
    }

    private void initViews() {
        singlecontrol = view.findViewById(R.id.singlecontrol);
        autom = view.findViewById(R.id.autom);
        manul = view.findViewById(R.id.manul);
        singlecontrol.setOnClickListener(this);
        autom.setOnClickListener(this);
        manul.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (view.getId()) {
            case R.id.singlecontrol:
                if (singleControlFragment == null) {
                    singleControlFragment = new SingleControlFragment();
                    if (!singleControlFragment.isAdded()) {
                        transaction.add(R.id.layoutcontent, singleControlFragment, "singleControlFragment");
                    }
                } else if (singleControlFragment.isHidden()) {
                    transaction.show(singleControlFragment);
                }
                singlecontrol.setClickable(false);
                autom.setClickable(true);
                manul.setClickable(true);
                singlecontrol.setBackgroundColor(Color.parseColor("#787878"));
                autom.setTextColor(Color.rgb(114, 114, 114));
                autom.setBackgroundColor(Color.parseColor("#00000000"));
                autom.setTextColor(Color.parseColor("#333333"));
                manul.setBackgroundColor(Color.parseColor("#00000000"));
                manul.setTextColor(Color.parseColor("#333333"));
                show(transaction);
                break;
            case R.id.autom:
                if (autoContolFragment == null) {
                    autoContolFragment = new AutoContolFragment();
                    if (!autoContolFragment.isAdded()) {
                        transaction.add(R.id.layoutcontent, autoContolFragment, "autoContolFragment");
                    }
                } else if (autoContolFragment.isHidden()) {
                    transaction.show(autoContolFragment);
                }
                singlecontrol.setClickable(true);
                autom.setClickable(false);
                manul.setClickable(true);
                singlecontrol.setBackgroundColor(Color.parseColor("#00000000"));
                singlecontrol.setTextColor(Color.parseColor("#333333"));

                autom.setBackgroundColor(Color.parseColor("#787878"));
//                autom.setTextColor(Color.rgb(114,114,114));

                manul.setBackgroundColor(Color.parseColor("#00000000"));
                manul.setTextColor(Color.parseColor("#333333"));
                show(transaction);
                break;
            case R.id.manul:
                if (manulControlFragment == null) {
                    manulControlFragment = new ManulControlFragment();
                    if (!manulControlFragment.isAdded()) {
                        transaction.add(R.id.layoutcontent, manulControlFragment, "manulControlFragment");
                    }
                } else if (manulControlFragment.isHidden()) {
                    transaction.show(manulControlFragment);
                }
                singlecontrol.setClickable(true);
                autom.setClickable(true);
                manul.setClickable(false);
                singlecontrol.setBackgroundColor(Color.parseColor("#00000000"));
                singlecontrol.setTextColor(Color.parseColor("#333333"));

                autom.setBackgroundColor(Color.parseColor("#00000000"));
                autom.setTextColor(Color.parseColor("#333333"));

                manul.setBackgroundColor(Color.parseColor("#787878"));
//                autom.setTextColor(Color.rgb(114,114,114));
                show(transaction);
                break;

        }
        transaction.commit();
    }

    private void show(FragmentTransaction transaction) {
        if (singleControlFragment != null && singleControlFragment.isVisible()) {
            transaction.hide(singleControlFragment);
        }
        if (autoContolFragment != null && autoContolFragment.isVisible()) {
            transaction.hide(autoContolFragment);
        }
        if (manulControlFragment != null && manulControlFragment.isVisible()) {
            transaction.hide(manulControlFragment);
        }
    }

    private void selectItem() {
        if (singleControlFragment == null) {
            singleControlFragment = new SingleControlFragment();
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.layoutcontent, singleControlFragment)
                .commit();

    }
}
