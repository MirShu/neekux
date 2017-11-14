package meekux.grandar.com.meekuxpjxroject.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import meekux.grandar.com.meekuxpjxroject.R;


/**
 * Created by baixiaoming on 2017/3/10 9:29
 * Function 常用的item支持各种格式，   左图标 文字         文字  右图标   这种格式
 */

public class CommonItemView extends RelativeLayout {
    private ImageView img_left;
    private ImageView img_right;
    private TextView text_title;
    private TextView text_subtitle;


    public CommonItemView(Context context) {
        super(context);
        initView(context);
    }

    public CommonItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initWidge(attrs);
    }

    private void initWidge(AttributeSet attrs) {
        int title = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res-auto", "title", 0);
        int subhead = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res-auto", "subhead", 0);
        int img_left_id = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res-auto", "imgleft", 0);
        int img_right_id = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res-auto", "imgright", 0);
        boolean isleftimg = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res-auto", "isleftimg", false);
        text_title.setText(getResources().getString(title));
        if(subhead !=0){
            text_subtitle.setText(getResources().getString(subhead));
        }
        img_left.setImageResource(img_left_id);
        //img_right.setImageResource(img_right_id);
        if (isleftimg) {
            img_left.setVisibility(GONE);
        } else {
            text_subtitle.setTextColor(getResources().getColor(R.color.greencolor));
        }

    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_view_common_item, this);
        img_left = (ImageView) view.findViewById(R.id.common_view_item_leftimg);
        img_right = (ImageView) view.findViewById(R.id.common_view_item_rightimg);
        text_title = (TextView) view.findViewById(R.id.common_view_item_title);
        text_subtitle = (TextView) view.findViewById(R.id.common_view_item_subtitle);
    }

    //换标题
    public void setTitle(int strId) {
        text_title.setText(getResources().getString(strId));
    }
    //换副标题
    public void setText(String str) {
        text_title.setText(str);
    }
    //换副标题
    public void setSubTitle(String str) {
        text_subtitle.setText(str);
    }
    //换副标题颜色
    public void setSubTitleColor(int colorId) {
        text_subtitle.setTextColor(getResources().getColor(colorId));
    }
}
