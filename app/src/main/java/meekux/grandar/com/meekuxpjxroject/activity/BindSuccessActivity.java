package meekux.grandar.com.meekuxpjxroject.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.espressif.iot.esptouch.demo_activity.EspWifiAdminSimple;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.DeviceDbUtil;

import java.util.List;

import butterknife.Bind;
import meekux.grandar.com.meekuxpjxroject.GlobalApplication;
import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.songdata.database.GrandarUtils;
import meekux.grandar.com.meekuxpjxroject.songdata.database.MyConstant;
import meekux.grandar.com.meekuxpjxroject.utils.LogUtils;
import meekux.grandar.com.meekuxpjxroject.utils.ToastUtils;
import meekux.grandar.com.meekuxpjxroject.view.EsptouchAsyncTask3;
import meekux.grandar.com.meekuxpjxroject.view.RippleBackground;
import meekux.grandar.com.meekuxpjxroject.view.TopView;

/**
 * Created by baixiaoming on 2017/3/17 15:15
 * Function 绑定成功页面
 */

public class BindSuccessActivity extends BaseActivity implements ServiceConnection {
    private static final String TAG = "BindSuccessActivity";
    //正在连接
    @Bind(R.id.contact_view_connecting)
    RelativeLayout view_connecting;
    //连接成功
    @Bind(R.id.contact_view_success)
    LinearLayout view_success;
    @Bind(R.id.connect_success_topview)
    TopView topview;
    private InitBroadcast broadcast;
    private IntentFilter mFilter;
    //返回键重写
    private LinearLayout view_back;
    //WIFI连接属性
    private EspWifiAdminSimple mWifiAdmin;
    private EsptouchAsyncTask3 esptouchAsyncTask3;
    private GrandarUtils.ServiceToken mToken;
    RippleBackground rippleBackground;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    LogUtils.e(TAG, "失败---mUiHandler msg_config_error");
                    // ToastUtils.show("绑定失败，请重试");
                    LogUtils.e("执行1结束---------", "中断线程");
                    esptouchAsyncTask3.setIsCancel(true);
                    esptouchAsyncTask3.cancel(true);
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case 1:
                    ToastUtils.show("连接成功，开始同步，请稍后...");
                    //开始同步
                    List<MyDeviceBean> myDeviceBean = DeviceDbUtil.getInstance().queryTimeAll();
                    if (myDeviceBean != null) {
                        startTcpClientService(myDeviceBean);
                    }
                    break;
                case 2:
                    ToastUtils.show("连接成功");
                    LogUtils.e("执行2结束---------", "同步成功");
                    view_connecting.setVisibility(View.GONE);
//                    view_success.setVisibility(View.VISIBLE);
                    finish();
                    esptouchAsyncTask3.setIsCancel(true);
                    esptouchAsyncTask3.cancel(true);
                    break;
            }
        }
    };
    private Boolean isSsidHidden;

    @Override
    protected int getResouseID()

    {
        return R.layout.layout_activity_success;
    }

    @Override
    protected void init() {
//        if (mToken==null)
//        mToken = GrandarUtils.bindToService(this, this);
        // 注册接收广播
        rippleBackground = findViewById(R.id.ripple);
        rippleBackground.startRippleAnimation();
//        ImageView imageView=(ImageView)findViewById(R.id.centerImage);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        mWifiAdmin = new EspWifiAdminSimple(this);
        broadcast = new InitBroadcast();
        mFilter = new IntentFilter();
        mFilter.addAction(MyConstant.switchtomain);
        mFilter.addAction(MyConstant.ACTION_RC_SOCKET_CLOSE);
        registerReceiver(broadcast, mFilter);
        view_back = topview.findViewById(R.id.topview_view_back);

        esptouchAsyncTask3 = new EsptouchAsyncTask3(this);

        //开始连接
        String apSsid = mWifiAdmin.getWifiConnectedSsid();
        String apPassword = getIntent().getStringExtra("pwd");
        String apBssid = mWifiAdmin.getWifiConnectedBssid();
        Boolean isSsidHidden = false;
        String isSsidHiddenStr = "NO";
        String taskResultCountStr = "-1";
        if (isSsidHidden) {
            isSsidHiddenStr = "YES";
        }
        LogUtils.e("wificontact----", "开始发送密码 apSsid----》 = " + apSsid + ", "
                + " apPassword-----》 = " + apPassword + " apBssid-----》 = " + apBssid
        );
        if (TextUtils.isEmpty(apBssid) || TextUtils.isEmpty(apBssid)) return;

        esptouchAsyncTask3.execute(apSsid, apBssid, apPassword,
                isSsidHiddenStr, taskResultCountStr);
    }

    @Override
    protected void click() {
        esptouchAsyncTask3.setEsptouchListener(what -> mHandler.sendEmptyMessage(what));
        view_back.setOnClickListener(v -> {
            esptouchAsyncTask3.setIsCancel(true);
            esptouchAsyncTask3.cancel(true);
            finish();
        });

    }

    public void complete(View view) {
        GlobalApplication.getInstance().exit();

    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(broadcast);
        rippleBackground.stopRippleAnimation();
        //GrandarUtils.unbindFromService(mToken);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        esptouchAsyncTask3.setIsCancel(true);
        esptouchAsyncTask3.cancel(true);
        super.onBackPressed();
    }

    private void startTcpClientService(List<MyDeviceBean> data) {
        GrandarUtils.startTcpClient(data, true);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        LogUtils.e("连接完成", "连接完成");
    }

    // 广播接收器
    private class InitBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyConstant.switchtomain)) {
                LogUtils.e(TAG, "连接成功");
                mHandler.sendEmptyMessage(2);
            } else if (intent.getAction().equals(MyConstant.ACTION_RC_SOCKET_CLOSE)) {
                // 连接失败
                LogUtils.e(TAG, "连接失败");
                mHandler.sendEmptyMessage(0);
            }
        }
    }
}
