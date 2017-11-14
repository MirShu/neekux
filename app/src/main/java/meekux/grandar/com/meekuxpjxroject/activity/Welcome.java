package meekux.grandar.com.meekuxpjxroject.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.DeviceDbUtil;

import java.util.List;

import meekux.grandar.com.meekuxpjxroject.MainActivity;
import meekux.grandar.com.meekuxpjxroject.R;

public class Welcome extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Welcome.this, MainActivity.class));
                Welcome.this.finish();
            }
        }, 2000);
    }

    private void init() {
//     .console();
        List<MyDeviceBean> myDeviceBeen = DeviceDbUtil.getInstance().queryTimeAll();
        Log.e("string", "fdjakl;sj");
//      SnControl instance = SnControl.getInstance();
//        instance.deleteAll();
    }

}
