package com.wzgiceman.rxretrofitlibrary.retrofit_rx.subscribers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.wzgiceman.rxretrofitlibrary.R;


/**
 * Created by gpc on 2017/3/20.
 */

public class MyDialog extends Dialog {


    private Context context = null;
    private static MyDialog customProgressDialog = null;
    //定义动画


    public MyDialog(Context context) {
        super(context);
        this.context = context;
    }

    public MyDialog(Context context, int theme) {
        super(context, theme);
    }

    public static MyDialog createDialog(Context context) {
        customProgressDialog = new MyDialog(context, R.style.CustomProgressDialog);
        customProgressDialog.setContentView(R.layout.dialoglayout);
        return customProgressDialog;
    }

    public void onWindowFocusChanged(boolean hasFocus) {

        if (customProgressDialog == null) {
            return;
        }
        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
        Animation jumpAnimation = AnimationUtils.loadAnimation(
                customProgressDialog.getContext(), R.anim.loadinganim);
        imageView.startAnimation(jumpAnimation);
        // 使用ImageView显示动画
//        if (jumpAnimation != null && imageView != null && jumpAnimation.hasStarted()) {
//            imageView.clearAnimation();
//
//        }
    }

    /**
     * [Summary]
     * setTitile 标题
     *
     * @param strTitle
     * @return
     */
    public MyDialog setTitile(String strTitle) {
        return customProgressDialog;
    }

}
