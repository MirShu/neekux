package meekux.grandar.com.meekuxpjxroject.activity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.espressif.iot.esptouch.demo_activity.EspWifiAdminSimple;

import butterknife.Bind;
import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.utils.ConstStringUtil;
import meekux.grandar.com.meekuxpjxroject.utils.SharedPreferencesUtils;
import meekux.grandar.com.meekuxpjxroject.utils.ToastUtils;
import meekux.grandar.com.meekuxpjxroject.view.EsptouchAsyncTask3;

/**
 * Created by baixiaoming on 2017/3/17 11:05
 * Function wifi连接页面
 */

public class WifiContactActivity extends BaseActivity {
    private static final String TAG = "WifiContactActivity";
    @Bind(R.id.remenberPasswordlayout)
    RelativeLayout remenberPasswordlayout;
    @Bind(R.id.wifi_contact_text_curwifi)
    TextView text_curwifi;
    @Bind(R.id.wifi_contact_img_isvis)
    ImageView img_isvis;
    @Bind(R.id.remberIcon)
    ImageView remberIcon;
    @Bind(R.id.wifi_contact_et_pwd)
    EditText et_pwd;
    @Bind(R.id.swichwifi)
    TextView swichwifi;
    private WifiManager wifi;
    private WifiInfo info;
    private NetworkInfo networkInfo;
    //wifi是否连接
    private ConnectivityManager connManager;
    private boolean isVis;
    //WIFI连接属性
    private EspWifiAdminSimple mWifiAdmin;
    private EsptouchAsyncTask3 esptouchAsyncTask3;
    private Boolean falg = false;

    @Override
    protected int getResouseID() {
        return R.layout.layout_activity_wificontact;
    }

    @Override
    protected void init() {
        wifi = (WifiManager) getSystemService(getApplication().WIFI_SERVICE);
        connManager = (ConnectivityManager) getSystemService(getApplication().CONNECTIVITY_SERVICE);
    }

    @Override
    protected void click() {
        remenberPasswordlayout.setOnClickListener(view -> {
            if (falg) {
                falg = false;
                SharedPreferencesUtils.getinstance().setStringValue(ConstStringUtil.WIFIPASSWORD, "");
                remberIcon.setImageResource(R.mipmap.drawable_icon_choose_false);
            } else {
                if (!"".equals(et_pwd.getText().toString())) {
                    SharedPreferencesUtils.getinstance().setStringValue(ConstStringUtil.WIFIPASSWORD, et_pwd.getText().toString());
                }
                falg = true;
                remberIcon.setImageResource(R.mipmap.drawable_icon_sex_true);
            }
        });
        swichwifi.setOnClickListener(view -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
        img_isvis.setOnClickListener(v -> {
            if (TextUtils.isEmpty(et_pwd.getText().toString())) return;
            if (!isVis) {
                img_isvis.setImageResource(R.mipmap.icon_eye_press);
                et_pwd.setInputType(0x90);
            } else {
                img_isvis.setImageResource(R.mipmap.icon_eye);
                et_pwd.setInputType(0x81);
            }
            isVis = !isVis;

        });

    }

    public void bind(View view) {
        String pwd = et_pwd.getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.show(getResources().getString(R.string.tips_wifi_empty));
            return;
        }
        Intent intent = new Intent(this, BindSuccessActivity.class);
        intent.putExtra("pwd", pwd);
        startActivity(intent);
    }


    private String intToIp(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 24) & 0xFF);
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            info = wifi.getConnectionInfo();
            String ssid = info.getSSID();

            if (!TextUtils.isEmpty(ssid)) {
                text_curwifi.setText(ssid.subSequence(1, ssid.length() - 1));
            }

        } else {
            text_curwifi.setText(getResources().getString(R.string.tips_contact));
        }

    }
}
