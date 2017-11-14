package meekux.grandar.com.meekuxpjxroject.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.entity.ChangeColor;
import meekux.grandar.com.meekuxpjxroject.fragment.ColorActivityFragemnt;
import meekux.grandar.com.meekuxpjxroject.fragment.WhiteColor;


public class ColorActivity extends BaseActivity implements View.OnClickListener {
    private FrameLayout framelayout;
    private FragmentManager fragmentManager;
    private ColorActivityFragemnt colorActivityFragemnt;
    private WhiteColor whiteColor;
    private List<ChangeColor> changeColorList;
    private Button selectorcolor;
    private Button whitecolor;
    private LinearLayout toplinearlayout;
    private TextView tv_head_back;
    @Override
    protected int getResouseID() {
        return R.layout.activitycolor;
    }
    @Override
    protected void init(){
        ArrayList<String> sn = getIntent().getStringArrayListExtra("sn");
        KLog.e("colorActivity--->oncreate");
        changeColorList = new ArrayList<ChangeColor>();
        initDate();
        tv_head_back = findViewById(R.id.tv_head_back);
        selectorcolor = findViewById(R.id.selectorcolor);
        whitecolor = findViewById(R.id.whitecolor);
        toplinearlayout = findViewById(R.id.toplinearlayout);
        framelayout = findViewById(R.id.framelayout);
        colorActivityFragemnt = new ColorActivityFragemnt(changeColorList);
        whiteColor = new WhiteColor();
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.framelayout, colorActivityFragemnt).commit();
        selectorcolor.setOnClickListener(this);
        whitecolor.setOnClickListener(this);
        toplinearlayout.setOnClickListener(this);
        tv_head_back.setOnClickListener(this);
    }

    @Override
    protected void click() {

    }

    //    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
////            case R.id.selectcolor:
////                final Drawable drawablehome = getResources().getDrawable(R.drawable.shape_setting_pressnow);
////                final Drawable drawableho = getResources().getDrawable(R.drawable.shape_setting_press);
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
////                    selectcolor.setBackground(drawablehome);
////                    whitecolor.setBackground(drawableho);
////                }
////                  selectcolor.setBackgroundColor(Color.parseColor("#f314a3"));
////                whitecolor.setBackgroundColor(Color.parseColor("#ffffff"));
////            fragmentManager.beginTransaction().replace(R.id.framelayout, colorActivityFragemnt).commit();
////                break;
////            case R.id.whitecolor:
////                final Drawable drawa = getResources().getDrawable(R.drawable.shape_setting_pressnow);
////                final Drawable dra = getResources().getDrawable(R.drawable.shape_setting_press);
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
////                    selectcolor.setBackground(dra);
////                    whitecolor.setBackground(drawa);
////                }
//                selectcolor.setBackgroundColor(Color.parseColor("#ffffff"));
//                whitecolor.setBackgroundColor(Color.parseColor("#f314a3"));
//                fragmentManager.beginTransaction().replace(R.id.framelayout, whiteColor).commit();
//                break;
//        }
//    }
    private void initDate(){
        ChangeColor changeColor = new ChangeColor();
        changeColor.setIconid(R.mipmap.food);
        changeColor.setTitle("饮食");
        changeColorList.add(changeColor);
        ChangeColor changeColor1 = new ChangeColor();
        changeColor1.setIconid((R.mipmap.sleep));
        changeColor1.setTitle("休息");
        changeColorList.add(changeColor1);
        ChangeColor changeColor2 = new ChangeColor();
        changeColor2.setIconid(R.mipmap.reed);
        changeColor2.setTitle("阅读");
        changeColorList.add(changeColor2);
        ChangeColor changeColo3 = new ChangeColor();
        changeColo3.setIconid(R.mipmap.relax);
        changeColo3.setTitle("放松");
        changeColorList.add(changeColo3);
        ChangeColor changeColor4 = new ChangeColor();
        changeColor4.setIconid(R.mipmap.coolandwarm);
        changeColor4.setTitle("冷-暖");
        changeColorList.add(changeColor4);
        ChangeColor changeColor5 = new ChangeColor();
        changeColor5.setIconid(R.mipmap.walk);
        changeColor5.setTitle("运动");
        changeColorList.add(changeColor5);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selectorcolor:
                selectorcolor.setTextColor(Color.rgb(0, 0, 0));
                whitecolor.setTextColor(Color.rgb(255, 255, 255));
                selectorcolor.setBackgroundResource(R.drawable.shapebuttonleft);
                whitecolor.setBackgroundResource(R.drawable.shapebuttonrighttrue);
                fragmentManager.beginTransaction().replace(R.id.framelayout, colorActivityFragemnt).commit();
                 break;
            case R.id.whitecolor:
                selectorcolor.setTextColor(Color.rgb(255, 255, 255));
                whitecolor.setTextColor(Color.rgb(0, 0, 0));
                selectorcolor.setBackgroundResource(R.drawable.shapebuttonlefttrue);
                whitecolor.setBackgroundResource(R.drawable.shapebuttonright);
                fragmentManager.beginTransaction().replace(R.id.framelayout, whiteColor).commit();
                break;
            case R.id.toplinearlayout:
                startActivity(new Intent(ColorActivity.this, SettingActivity.class));
                break;
            case R.id.tv_head_back:
                finish();
                break;
        }
    }
}
