package meekux.grandar.com.meekuxpjxroject.activity;

import android.app.Activity;
import android.os.Bundle;

import butterknife.ButterKnife;
import meekux.grandar.com.meekuxpjxroject.view.MyDialog;


/**
 * 基类activty
 * author：xuqunwang
 */
public abstract class BaseActivity extends Activity {
    protected MyDialog myDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResouseID());
        ButterKnife.bind(this);
        myDialog = new MyDialog(this);
        init();
        click();
    }

    /**
     * 设置布局ID
     */
    protected abstract int getResouseID();

    /**
     * 初始化控件
     */
    protected abstract void init();

    /**
     * 点击事件
     */
    protected abstract void click();


}
