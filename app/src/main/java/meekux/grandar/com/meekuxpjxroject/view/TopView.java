package meekux.grandar.com.meekuxpjxroject.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import meekux.grandar.com.meekuxpjxroject.R;


/**
 * Created by baixiaoming on 2017/3/10 11:23
 * Function 头部布局
 */

public class TopView extends RelativeLayout {
    private LinearLayout img_back;
    private TextView text_title;
    private Context mContext;
    private TextView text_operate;
    private LinearLayout view_operate;
    private ImageView img_operate;
    private TextView text_count;
    private Button btn_edit;

    public TopView(Context context) {
        super(context);
        initView(context);
    }

    public TopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initWidge(attrs);
    }

    private void initWidge(AttributeSet attrs) {
        int title_id = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res-auto", "title", 0);
        int operate_id = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res-auto", "subhead", 0);
//        int img_back_id = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res-auto", "imgleft", 0);
        if (title_id != 0)
            text_title.setText(getResources().getString(title_id));
        if (operate_id != 0) {
            text_operate.setText(getResources().getString(operate_id));
            text_operate.setVisibility(VISIBLE);
        }
    }

    private void initView(Context context) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_view_topview, this);
        img_back = (LinearLayout) view.findViewById(R.id.topview_view_back);
        text_title = (TextView) view.findViewById(R.id.topview_text_title);
        text_count = (TextView) view.findViewById(R.id.topview_text_choosenum);
        text_operate = (TextView) view.findViewById(R.id.topview_text_operate);
        btn_edit = (Button) view.findViewById(R.id.topview_btn_edit);
        view_operate = (LinearLayout) view.findViewById(R.id.topview_view_operate);
        img_operate = (ImageView) view.findViewById(R.id.topview_img_oprate);
        initListener();
    }

    private void initListener() {
        img_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) mContext).finish();
            }
        });
        text_operate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.clickOperate();
            }
        });
        view_operate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.clickOperate();
            }
        });
        btn_edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) mListener.editClick();
            }
        });

    }

    public void setTitle(int title_id) {
        text_title.setText(getResources().getString(title_id));
    }

    public void setTitleString(String title) {
        text_title.setText(title);
    }

    //标题栏右边的文字
    public void setRightTitle(String rightTitle) {
        text_operate.setText(rightTitle);
        text_operate.setVisibility(VISIBLE);
    }

    //编辑按钮
    public void setEditTitle(String editTitle) {
        btn_edit.setText(editTitle);
        btn_edit.setVisibility(VISIBLE);
    }

    public String getEditTitle() {
        String edit_text=btn_edit.getText().toString();
        return edit_text;
    }

    //我的设备页面右上方+事件
    public void setImageVis(int imgId) {
        view_operate.setVisibility(VISIBLE);
        img_operate.setImageResource(imgId);
    }

    //添加光曲页面专用
    public void setTopNum(String content) {
        text_count.setVisibility(VISIBLE);
        text_count.setText(content);
    }

    public void setImageVisGone() {
        view_operate.setVisibility(GONE);
    }

    public interface OnTopviewClickListener {
        //头布局右边操作点击事件
        void clickOperate();
    }

    public interface OnTopEditClickListener {
        void editClick();
    }

    private OnTopviewClickListener listener;

    private OnTopEditClickListener mListener;

    public void setOnTopviewListener(OnTopviewClickListener listener) {
        this.listener = listener;
    }

    public void setOnTopEditListener(OnTopEditClickListener listener) {
        this.mListener = listener;
    }

}
