package meekux.grandar.com.meekuxpjxroject.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.DeviceDbUtil;

import butterknife.Bind;
import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.view.CommonItemView;

/**
 * Created by baixiaoming on 2017/3/17 16:28
 * Function 灯信息
 */

public class LampDetailActivity extends BaseActivity {
    @Bind(R.id.lamp_detail_view_rename)
    CommonItemView view_rename;
    @Bind(R.id.lamp_detail_text_name)
    TextView text_name;
    @Bind(R.id.lamp_detail_text_date)
    TextView text_date;
    @Bind(R.id.lamp_detail_text_sn)
    TextView text_sn;
    @Bind(R.id.lamp_detail_btn_delete)
     Button bt_delete;
    private String name;
    private String sn;
    private String date;
    private String ip;
    private long id;
    private int position;

    @Override
    protected int getResouseID() {
        return R.layout.layout_activity_lamp_detail;
    }

    @Override
    protected void init() {
        view_rename.setSubTitleColor(R.color.whiteSubtextColor);
        position=getIntent().getIntExtra("position",-1);
        name = getIntent().getStringExtra("name");
        sn = getIntent().getStringExtra("sn");
        ip = getIntent().getStringExtra("ip");
        id = getIntent().getLongExtra("id", 0);
        date = getIntent().getStringExtra("date");
        if (TextUtils.isEmpty(name)) name = "落地灯";
        text_name.setText(name);
        text_sn.setText("型号：" + sn);
        text_date.setText("绑定日期：" + date);
        view_rename.setSubTitle(name);

    }

    @Override
    protected void click() {
        bt_delete.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("id", id);
            intent.putExtra("position",position );
            intent.putExtra("flag",0);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    public void rename(View view) {
//        Intent intent = new Intent(this, UpdateLampActivity.class);
//        intent.putExtra("name", name);
//        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        String res = data.getStringExtra("result");
        view_rename.setSubTitle(res);
        text_name.setText(res);
        Intent intent=new Intent();
        intent.putExtra("position",position);
        intent.putExtra("name",res);
        intent.putExtra("flag",1);
        setResult(RESULT_OK,intent);
        MyDeviceBean bean=new MyDeviceBean();
        bean.setSid(id);
        bean.setName(res);
        bean.setDate(date);
        bean.setIp(ip);
        bean.setSn(sn);
        DeviceDbUtil.getInstance().updateTime(bean);

    }
}
