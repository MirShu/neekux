package meekux.grandar.com.meekuxpjxroject.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.socks.library.KLog;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.TimeBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.TimeDbUtil;

import java.util.List;

import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.utils.ConstStringUtil;
import meekux.grandar.com.meekuxpjxroject.utils.RadioGroup;
import meekux.grandar.com.meekuxpjxroject.utils.SharedPreferencesUtils;

import static meekux.grandar.com.meekuxpjxroject.R.string.setting;


public class MyDialog {

    private Dialog avatarChooseDialog;
    private Dialog avatarPhoneDialog;
    private Dialog tipsDialog;
    private Dialog shareDialog;
    private Dialog mainShadeDialog;
    private Dialog renameDialog;
    private Dialog resetPlayDialog;
    private Dialog voiceAndBrightnessDialog;
    private Dialog vbCommonDialog;
    private Dialog setVolumeDialog;
    private Dialog selectorDayDialog;
    private CheckBox box7, box1, box2, box3, box4, box5, box6;
    private Dialog commonRenameDialog;
    private Dialog loadDialog;
    private String sunday;
    private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;
    private String saturday;
    private Dialog delFileDialog;

    private int screenWidth;
    private String selectDay;
    private Dialog downLoadDialog;
    private Dialog downLoadialogPushMessage;
    private Dialog downLoadMessageDialog;
    private RadioButton m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12;
    private String s1;
    private String numberM;

    public MyDialog(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
    }

    //通用取消确定提示对话框 传入提示内容
    public void tipsDialog(Context context, int tipsId, final OnDialogClickImp imp) {
        if (tipsDialog != null && tipsDialog.isShowing()) {
            return;
        }
        tipsDialog = new Dialog(context, R.style.dialog_loading_nobackground);
        View view = View.inflate(context, R.layout.layout_dialog_commonnew, null);
        TextView text_confirm = view.findViewById(R.id.common_dialog_text_confirm);
        TextView text_cancel = view.findViewById(R.id.common_dialog_text_cancel);
        EditText text_content = view.findViewById(R.id.common_dialog_text_content);
        m1 = view.findViewById(R.id.rb_1);
        m2 = view.findViewById(R.id.rb_2);
        m3 = view.findViewById(R.id.rb_3);
        m4 = view.findViewById(R.id.rb_4);
        m5 = view.findViewById(R.id.rb_5);
        m6 = view.findViewById(R.id.rb_6);
        m7 = view.findViewById(R.id.rb_7);
        m8 = view.findViewById(R.id.rb_8);
        m9 = view.findViewById(R.id.rb_9);
        m10 = view.findViewById(R.id.rb_10);
        m11 = view.findViewById(R.id.rb_11);
        m12 = view.findViewById(R.id.rb_12);


        m1.isClickable();
        RadioGroup group = view.findViewById(R.id.radioGroup);
        text_content.setText(context.getResources().getString(tipsId));

        text_cancel.setOnClickListener(v -> tipsDialog.dismiss());
        group.setOnCheckedChangeListener((group1, checkedId) -> {
            if (m1.getId() == checkedId) {
                numberM = m1.getText().toString();
            } else if (m2.getId() == checkedId) {
                numberM = m2.getText().toString();
            } else if (m3.getId() == checkedId) {
                numberM = m3.getText().toString();
            } else if (m4.getId() == checkedId) {
                numberM = m4.getText().toString();
            } else if (m5.getId() == checkedId) {
                numberM = m5.getText().toString();
            } else if (m6.getId() == checkedId) {
                numberM = m6.getText().toString();
            } else if (m7.getId() == checkedId) {
                numberM = m7.getText().toString();
            } else if (m8.getId() == checkedId) {
                numberM = m8.getText().toString();
            } else if (m9.getId() == checkedId) {
                numberM = m9.getText().toString();
            } else if (m10.getId() == checkedId) {
                numberM = m10.getText().toString();
            } else if (m11.getId() == checkedId) {
                numberM = m11.getText().toString();
            } else if (m12.getId() == checkedId) {
                numberM = m12.getText().toString();
            }

        });


        text_confirm.setOnClickListener(v -> {
            tipsDialog.dismiss();
            if (imp != null) imp.sendValue(numberM);

        });
        tipsDialog.setContentView(view);
        Window window = tipsDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = screenWidth * 5 / 6;
        lp.height = screenWidth * 6 / 7 * 650 / 624;
        window.setAttributes(lp);
        tipsDialog.setCanceledOnTouchOutside(true);
        tipsDialog.setCancelable(true);
        tipsDialog.show();
    }

    public void setDownLoadDialogPushMessage(Context context) {
        if (downLoadialogPushMessage != null && downLoadialogPushMessage.isShowing()) {
            return;
        }
        downLoadialogPushMessage = new Dialog(context, R.style.dialog_loading);


    }

//    public void downLoadDialog(Context context) {
//        if (downLoadDialog != null && downLoadDialog.isShowing()) {
//            return;
//        }
//        downLoadDialog = new Dialog(context, R.style.dialog_loading);
//        View view = View.inflate(context, R.layout.layout_dialog_download, null);
//        downLoadDialog.setContentView(view);
//        Window window = downLoadDialog.getWindow();
//        window.setGravity(Gravity.CENTER);
//        downLoadDialog.setCanceledOnTouchOutside(false);
//        downLoadDialog.setCancelable(false);
//        downLoadDialog.show();
//    }

    public void dismissDownLoad() {
        if (downLoadDialog != null && downLoadDialog.isShowing()) {
            downLoadDialog.dismiss();
        }
    }

//    public void downLoadMessageDialog(Context context) {
//        if (downLoadMessageDialog != null && downLoadMessageDialog.isShowing()) {
//            return;
//        }
//        downLoadMessageDialog = new Dialog(context, R.style.dialog_loading);
//        View view = View.inflate(context, R.layout.layout_dialog_message_download, null);
//        downLoadMessageDialog.setContentView(view);
//        Window window = downLoadMessageDialog.getWindow();
//        window.setGravity(Gravity.CENTER);
//        downLoadMessageDialog.setCanceledOnTouchOutside(false);
//        downLoadMessageDialog.setCancelable(false);
//        downLoadMessageDialog.show();
//    }

    public void dismissMessageDownLoad() {
        if (downLoadMessageDialog != null && downLoadMessageDialog.isShowing()) {
            downLoadMessageDialog.dismiss();
        }
    }

    public void versionDialog(Context context, String info, int confirm, final OnDialogClickImp imp) {
        if (tipsDialog != null && tipsDialog.isShowing()) {
            return;
        }
        tipsDialog = new Dialog(context, R.style.dialog_loading_nobackground);
        View view = View.inflate(context, R.layout.layout_dialog_commonnew, null);
        LinearLayout linratlayoutmydialog = view.findViewById(R.id.linratlayoutmydialog);
        TextView text_confirm = view.findViewById(R.id.common_dialog_text_confirm);
        TextView text_cancel = view.findViewById(R.id.common_dialog_text_cancel);
        TextView text_content = view.findViewById(R.id.common_dialog_text_content);
        text_content.setText(info);
        text_confirm.setText(context.getResources().getString(confirm));
        text_cancel.setOnClickListener(v -> tipsDialog.dismiss());
        text_confirm.setOnClickListener(v -> {
            if (imp != null) imp.sendValue(null);
            tipsDialog.dismiss();
        });
        tipsDialog.setContentView(view);
        Window window = tipsDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        int length = info.length() / 19;
        int height = text_content.getHeight();
        Log.e("lengthsith", length + "");
        lp.width = screenWidth * 5 / 6;
        if (length >= 2) {
            LinearLayout.LayoutParams linear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams textview = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            Log.e("lengthsith", length + "");
            linear.setMargins(0, 25, 0, 0);
            linear.height = screenWidth * 306 / 624 / 4;
            linratlayoutmydialog.setLayoutParams(linear);
            textview.setMargins(10, 0, 10, 0);
            text_content.setLayoutParams(textview);
            lp.height = screenWidth * 7 / 8 * 306 / 624;
        } else {
            lp.height = screenWidth * 6 / 7 * 306 / 624;
        }
        window.setAttributes(lp);
        tipsDialog.setCanceledOnTouchOutside(true);
        tipsDialog.setCancelable(true);
        tipsDialog.show();
    }

    //选择天数弹出框
//    public void selectorDayDialog(final Context context, String edittime, int position, boolean ischeck1, boolean ischeck2, boolean ischeck3, boolean ischeck4, boolean ischeck5, boolean ischeck6, boolean ischeck7, final OnDialogClickDay imp) {
//
//        if (selectorDayDialog != null && selectorDayDialog.isShowing()) {
//            return;
//        }
//        selectorDayDialog = new Dialog(context, R.style.dialog_loading_nobackground);
//        View view = View.inflate(context, R.layout.layout_dialog_day, null);
//        TextView text_confirm = (TextView) view.findViewById(R.id.day_dialog_text_confirm);
//        final TextView text_cancel = (TextView) view.findViewById(R.id.day_dialog_text_cancel);
//        RelativeLayout rl_box = (RelativeLayout) view.findViewById(R.id.rl_box);
//        RelativeLayout rl_box1 = (RelativeLayout) view.findViewById(R.id.rl_box1);
//        RelativeLayout rl_box2 = (RelativeLayout) view.findViewById(R.id.rl_box2);
//        RelativeLayout rl_box3 = (RelativeLayout) view.findViewById(R.id.rl_box3);
//        RelativeLayout rl_box4 = (RelativeLayout) view.findViewById(R.id.rl_box4);
//        RelativeLayout rl_box5 = (RelativeLayout) view.findViewById(R.id.rl_box5);
//        RelativeLayout rl_box6 = (RelativeLayout) view.findViewById(R.id.rl_box6);
//
//        box7 = (CheckBox) view.findViewById(R.id.box7);
//        box1 = (CheckBox) view.findViewById(R.id.box1);
//        box2 = (CheckBox) view.findViewById(R.id.box2);
//        box3 = (CheckBox) view.findViewById(R.id.box3);
//        box4 = (CheckBox) view.findViewById(R.id.box4);
//        box5 = (CheckBox) view.findViewById(R.id.box5);
//        box6 = (CheckBox) view.findViewById(R.id.box6);
//
//        if ("-1".equals(edittime)) {
//            List<TimeBean> bean = TimeDbUtil.getInstance().queryTimeAll();
//            TimeBean timeBean = bean.get(position);
//            if (timeBean.getMonday()) {
//                box1.setChecked(true);
//            } else {
//                box1.setChecked(false);
//            }
//            if (timeBean.getTuesday()) {
//                box2.setChecked(true);
//            } else {
//                box2.setChecked(false);
//            }
//            if (timeBean.getWednesday()) {
//                box3.setChecked(true);
//            } else {
//                box3.setChecked(false);
//            }
//            if (timeBean.getThursday()) {
//                box4.setChecked(true);
//            } else {
//                box4.setChecked(false);
//            }
//            if (timeBean.getFriday()) {
//                box5.setChecked(true);
//            } else {
//                box5.setChecked(false);
//            }
//            if (timeBean.getSaturday()) {
//                box6.setChecked(true);
//            } else {
//                box6.setChecked(false);
//            }
//            if (timeBean.getSunday()) {
//                box7.setChecked(true);
//            } else {
//                box7.setChecked(false);
//            }
//        } else {
//            if (ischeck1) {
//                box1.setChecked(true);
//            } else {
//                box1.setChecked(false);
//            }
//            if (ischeck2) {
//                box2.setChecked(true);
//            } else {
//                box2.setChecked(false);
//            }
//            if (ischeck3) {
//                box3.setChecked(true);
//            } else {
//                box3.setChecked(false);
//            }
//            if (ischeck4) {
//                box4.setChecked(true);
//            } else {
//                box4.setChecked(false);
//            }
//            if (ischeck5) {
//                box5.setChecked(true);
//            } else {
//                box5.setChecked(false);
//            }
//            if (ischeck6) {
//                box6.setChecked(true);
//            } else {
//                box6.setChecked(false);
//            }
//
//            if (ischeck7) {
//                box7.setChecked(true);
//            } else {
//                box7.setChecked(false);
//            }
//
//        }


//        rl_box.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (box7.isChecked()) {
//                    box7.setChecked(false);
//                } else {
//                    box7.setChecked(true);
//                }
//
//            }
//        });
//        rl_box1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (box1.isChecked()) {
//                    box1.setChecked(false);
//                } else {
//                    box1.setChecked(true);
//                }
//
//            }
//        });
//        rl_box2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (box2.isChecked()) {
//                    box2.setChecked(false);
//                } else {
//                    box2.setChecked(true);
//                }
//
//            }
//        });
//        rl_box3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (box3.isChecked()) {
//                    box3.setChecked(false);
//                } else {
//                    box3.setChecked(true);
//                }
//
//            }
//        });
//        rl_box4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (box4.isChecked()) {
//                    box4.setChecked(false);
//                } else {
//                    box4.setChecked(true);
//                }
//
//            }
//        });
//        rl_box5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (box5.isChecked()) {
//                    box5.setChecked(false);
//                } else {
//                    box5.setChecked(true);
//                }
//
//            }
//        });
//        rl_box6.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (box6.isChecked()) {
//                    box6.setChecked(false);
//                } else {
//                    box6.setChecked(true);
//                }
//
//            }
//        });
//
//        text_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectorDayDialog.dismiss();
//            }
//        });
//
//        text_confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (box1.isChecked()) {
//                    monday = context.getResources().getString(R.string.monday);
//                } else {
//                    monday = "";
//                }
//                if (box2.isChecked()) {
//                    tuesday = context.getResources().getString(R.string.tuesday);
//                } else {
//                    tuesday = "";
//                }
//                if (box3.isChecked()) {
//                    wednesday = context.getResources().getString(R.string.wednesday);
//                } else {
//                    wednesday = "";
//                }
//                if (box4.isChecked()) {
//                    thursday = context.getResources().getString(R.string.thursday);
//                } else {
//                    thursday = "";
//                }
//                if (box5.isChecked()) {
//                    friday = context.getResources().getString(R.string.friday);
//                } else {
//                    friday = "";
//                }
//                if (box6.isChecked()) {
//                    saturday = context.getResources().getString(R.string.saturday);
//                } else {
//                    saturday = "";
//                }
//                if (box7.isChecked()) {
//                    sunday = context.getResources().getString(R.string.sunday);
//                } else {
//                    sunday = "";
//                }
//
//                if (box7.isChecked() && box1.isChecked() && box2.isChecked() && box3.isChecked() && box4.isChecked() && box5.isChecked() && box6.isChecked()) {
//                    selectDay = context.getResources().getString(R.string.everyday);
//                } else if (!box7.isChecked() && !box1.isChecked() && !box2.isChecked() && !box3.isChecked() && !box4.isChecked() && !box5.isChecked() && !box6.isChecked()) {
//                    selectDay = context.getResources().getString(R.string.only_one);
//                } else {
//                    selectDay = getReplaceAllBuilderString(sunday, monday, tuesday, wednesday, thursday, friday, saturday);
//
//                }
//                if (imp != null)
//                    imp.getDays(selectDay, box1.isChecked(), box2.isChecked(), box3.isChecked(), box4.isChecked(), box5.isChecked(), box6.isChecked(), box7.isChecked());
//                selectorDayDialog.dismiss();
//            }
//        });
//
//
//        selectorDayDialog.setContentView(view);
//        Window window = selectorDayDialog.getWindow();
//        window.setGravity(Gravity.CENTER);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = screenWidth * 4 / 5;
//        window.setAttributes(lp);
//        selectorDayDialog.setCanceledOnTouchOutside(false);
//        selectorDayDialog.setCancelable(false);
//        selectorDayDialog.show();
//
//    }

    //编辑定时界面，音量和亮度弹出框
//    public void voiceAndBrightnessDialog(final Context context, String brightness_voice, final onDialogVoiceAndBrightness imp) {
//        if (voiceAndBrightnessDialog != null && voiceAndBrightnessDialog.isShowing()) {
//            return;
//        }
//        voiceAndBrightnessDialog = new Dialog(context, R.style.dialog_loading_nobackground);
//        View view = View.inflate(context, R.layout.layout_dialog_voice_brightness, null);
//        TextView text_confirm = (TextView) view.findViewById(R.id.voice_dialog_text_confirm);
//        TextView text_cancel = (TextView) view.findViewById(R.id.voice_dialog_text_cancel);
//        SeekBar sk_brightness = (SeekBar) view.findViewById(R.id.seekbar_brightness);
//        SeekBar sk_voice = (SeekBar) view.findViewById(R.id.seekbar_voice);
//        final TextView tv_voice = (TextView) view.findViewById(R.id.voice_text);
//        final TextView tv_brightness = (TextView) view.findViewById(R.id.brightness_text);
//        int brightness = SharedPreferencesUtils.getinstance().getIntValue(ConstStringUtil.BRIGHTNESS_PROCESS, 0);
//        int voice = SharedPreferencesUtils.getinstance().getIntValue(ConstStringUtil.VOICE_PROCESS, 0);
//        if (context.getResources().getString(R.string.manually).equals(brightness_voice)) {
//            tv_brightness.setText(0 + "%");
//            sk_brightness.setProgress(0);
//            tv_voice.setText(0 + "%");
//            sk_voice.setProgress(0);
//        } else {
//            tv_brightness.setText(brightness + "%");
//            sk_brightness.setProgress(brightness);
//            tv_voice.setText(voice + "%");
//            sk_voice.setProgress(voice);
//
//        }
//
//        sk_voice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                tv_voice.setText(seekBar.getProgress() + "%");
//                SharedPreferencesUtils.getinstance().setIntValue(ConstStringUtil.VOICE_PROCESS, progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//        sk_brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                tv_brightness.setText(seekBar.getProgress() + "%");
//                SharedPreferencesUtils.getinstance().setIntValue(ConstStringUtil.BRIGHTNESS_PROCESS, progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
//        text_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                voiceAndBrightnessDialog.dismiss();
//            }
//        });
//        text_confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (imp != null)
//                    imp.getInfo(tv_brightness.getText().toString(), tv_voice.getText().toString());
//                voiceAndBrightnessDialog.dismiss();
//            }
//        });
//        voiceAndBrightnessDialog.setContentView(view);
//        Window window = voiceAndBrightnessDialog.getWindow();
//        window.setGravity(Gravity.CENTER);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = screenWidth * 3 / 4;
//        window.setAttributes(lp);
//        voiceAndBrightnessDialog.setCanceledOnTouchOutside(true);
//        voiceAndBrightnessDialog.setCancelable(true);
//        voiceAndBrightnessDialog.show();
//
//
//    }
//
//
//    //控制器头像选择
//    public void avatarChooseDialog(final Context context, final OnDialogClickImp listener) {
//        if (avatarChooseDialog != null && avatarChooseDialog.isShowing()) {
//            return;
//        }
//        final int[] curImgId = new int[1];
//        View view = View.inflate(context, R.layout.layout_view_dialog_avatarchoose, null);
//        avatarChooseDialog = new Dialog(context, R.style.dialog_loading_nobackground);
//        GridView gv = (GridView) view.findViewById(R.id.dialog_view_gv);
//        TextView text_confirm = (TextView) view.findViewById(R.id.dialog_view_text_comfirm);
//        final int[] ints = new int[]{R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
//                R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
//                R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
//                R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,};
//        int len = ints.length;
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                142 * len, 140);
//        gv.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
//        gv.setColumnWidth(130); // 设置列表项宽
//        gv.setHorizontalSpacing(12); // 设置列表项水平间距
//        gv.setStretchMode(GridView.NO_STRETCH);
//        gv.setNumColumns(ints.length); // 设列数量
//        DialogGridAdapter adapter = new DialogGridAdapter(context, ints);
//        gv.setAdapter(adapter);
//        adapter.setOnDialogImp(new DialogGridAdapter.OnDialogValueImp() {
//            @Override
//            public void value(int position) {
//                curImgId[0] = position;
//            }
//        });
//        text_confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listener != null) listener.sendValue(ints[curImgId[0]]);
//                avatarChooseDialog.dismiss();
//            }
//        });
//
//        avatarChooseDialog.setContentView(view);
//        Window window = avatarChooseDialog.getWindow();
//        window.setGravity(Gravity.BOTTOM);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = screenWidth;
//        window.setAttributes(lp);
//        avatarChooseDialog.setCanceledOnTouchOutside(true);
//        avatarChooseDialog.setCancelable(true);
//        avatarChooseDialog.show();
//    }
//
//    //手机头像选择
//    public void avatarPhoneDialog(final Context context, final OnDialogPhotographImp listener) {
//        if (avatarPhoneDialog != null && avatarPhoneDialog.isShowing()) {
//            return;
//        }
//        View view = View.inflate(context, R.layout.layout_view_dialog_avatarphone, null);
//        avatarPhoneDialog = new Dialog(context, R.style.dialog_loading_nobackground);
//        TextView text_confirm = view.findViewById(R.id.dialog_view_avatar_text_cancel);
//        TextView text_album = view.findViewById(R.id.dialog_view_avatar_text_album);
//        TextView text_camera = view.findViewById(R.id.dialog_view_avatar_text_camera);
//        text_confirm.setOnClickListener(v -> avatarPhoneDialog.dismiss());
//        text_album.setOnClickListener(v -> {
//            if (listener != null) listener.album();
//            avatarPhoneDialog.dismiss();
//        });
//        text_camera.setOnClickListener(v -> {
//            if (listener != null) listener.camera();
//            avatarPhoneDialog.dismiss();
//        });
//        avatarPhoneDialog.setContentView(view);
//        Window window = avatarPhoneDialog.getWindow();
//        window.setGravity(Gravity.BOTTOM);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = screenWidth;
//        window.setAttributes(lp);
//        avatarPhoneDialog.setCanceledOnTouchOutside(true);
//        avatarPhoneDialog.setCancelable(true);
//        avatarPhoneDialog.show();
//    }
//
//    //分享对话框
//    public void shareDialog(Context context, final OnDialogClickImp imp) {
//        if (shareDialog != null && shareDialog.isShowing()) {
//            return;
//        }
//        shareDialog = new Dialog(context, R.style.dialog_loading_nobackground);
//        View view = View.inflate(context, R.layout.layout_view_dialog_share, null);
//
//        LinearLayout view_wechat = view.findViewById(R.id.dialog_share_view_wechat);
//        LinearLayout view_wechat_friend = view.findViewById(R.id.dialog_share_view_wechat_friend);
//        LinearLayout view_qq = view.findViewById(R.id.dialog_share_view_qq);
//        LinearLayout view_qqzone = view.findViewById(R.id.dialog_share_view_qqzone);
//        TextView text_cancel = view.findViewById(R.id.dialog_share_view_cancel);
//        text_cancel.setOnClickListener(v -> shareDialog.dismiss());
//        view_wechat.setOnClickListener(v -> {
//            if (imp != null) imp.sendValue(1);
//            shareDialog.dismiss();
//        });
//        view_wechat_friend.setOnClickListener(v -> {
//            if (imp != null) imp.sendValue(2);
//            shareDialog.dismiss();
//        });
//        view_qq.setOnClickListener(v -> {
//            if (imp != null) imp.sendValue(3);
//            shareDialog.dismiss();
//        });
//        view_qqzone.setOnClickListener(v -> {
//            if (imp != null) imp.sendValue(4);
//            shareDialog.dismiss();
//        });
//        shareDialog.setContentView(view);
//        Window window = shareDialog.getWindow();
//        window.setGravity(Gravity.BOTTOM);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = screenWidth;
//        window.setAttributes(lp);
//        shareDialog.setCanceledOnTouchOutside(true);
//        shareDialog.setCancelable(true);
//        shareDialog.show();
//    }
//
//    //我的设备界面的弹窗
    public void setPopWindow(Context context, View v, final popWiondowCallback callback) {
        View contentview = LayoutInflater.from(context).inflate(R.layout.popwindow, null);
        TextView tv_add = (TextView) contentview.findViewById(R.id.tv_add_device);
        TextView tv_serach = (TextView) contentview.findViewById(R.id.tv_serach_device);
        //标题栏右边的按钮
        //ImageView bgview = (ImageView) contentview.findViewById(R.id.bg_view);
        //获取尖角图片的宽度
//        BitmapFactory.Options opts = new BitmapFactory.Options();
//        opts.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_bg_pop, opts);
//        opts.inSampleSize = 1;
//        opts.inJustDecodeBounds = false;
//        int width = opts.outWidth;
        //设置尖角图片在标题栏的正下方
//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bgview.getLayoutParams();
//        lp.setMargins(0, 0, v.getWidth() / 2 - width / 2 - 5
//                , 0);
//        bgview.setLayoutParams(lp);
        final PopupWindow pop = new PopupWindow(contentview, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new ColorDrawable(0x00000000));
        pop.showAsDropDown(v);

        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.addDevice();
                }
                pop.dismiss();
            }
        });
        tv_serach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.searchDevice();
                }
                pop.dismiss();

            }
        });
    }

    //
//    //主屏第一屏
//    public void mainShadeDialog(Context context, final OnDialogClickImp imp) {
//        if (mainShadeDialog != null && mainShadeDialog.isShowing()) {
//            return;
//        }
//        View view = View.inflate(context, R.layout.layout_view_dialog_mainshade, null);
//        mainShadeDialog = new Dialog(context, R.style.dialog_loading_nobackground);
//        ImageView img_bg = (ImageView) view.findViewById(R.id.main_dialog_img_bg);
//        ImageView img_in = (ImageView) view.findViewById(R.id.main_dialog_img_in);
//        Animation animation = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1f,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//                0.5f);
//        animation.setDuration(2500);
//        img_in.setAnimation(animation);
//        img_in.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (imp != null) imp.sendValue(null);
//                mainShadeDialog.dismiss();
//            }
//        });
//
//        mainShadeDialog.setContentView(view);
//        Window window = mainShadeDialog.getWindow();
//        window.setGravity(Gravity.BOTTOM);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        //1169.720
//        //Glide.with(context).load(R.drawable.main_dialog_bg).into(img_bg);
//        lp.width = screenWidth;
//
//        lp.height = context.getResources().getDisplayMetrics().heightPixels;
//        window.setAttributes(lp);
//        mainShadeDialog.setCanceledOnTouchOutside(false);
//        mainShadeDialog.setCancelable(false);
//        mainShadeDialog.show();
//    }
//
//    //重命名
//    public void renameDialog(final Context context, final OnDialogPhotographImp listener) {
//        if (renameDialog != null && renameDialog.isShowing()) {
//            return;
//        }
//        View view = View.inflate(context, R.layout.layout_view_dialog_rename, null);
//        renameDialog = new Dialog(context, R.style.dialog_loading_nobackground);
//        TextView text_delete = (TextView) view.findViewById(R.id.rename_dialog_text_delete);
//        TextView text_rename = (TextView) view.findViewById(R.id.rename_dialog_text_rename);
//        text_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listener != null) listener.album();
//                renameDialog.dismiss();
//            }
//        });
//        text_rename.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listener != null) listener.camera();
//                renameDialog.dismiss();
//            }
//        });
//
//        renameDialog.setContentView(view);
//        Window window = renameDialog.getWindow();
//        window.setGravity(Gravity.BOTTOM);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = screenWidth;
//        window.setAttributes(lp);
//        renameDialog.setCanceledOnTouchOutside(true);
//        renameDialog.setCancelable(true);
//        renameDialog.show();
//    }
//
//    //更改播放时间，次数
//    public void resetPlayDialog(final Context context, final OnResetPlayImp listener) {
//        if (resetPlayDialog != null && resetPlayDialog.isShowing()) {
//            return;
//        }
//        View view = View.inflate(context, R.layout.layout_view_dialog_resetplay, null);
//        resetPlayDialog = new Dialog(context, R.style.dialog_loading_nobackground);
//        TextView text_delete = (TextView) view.findViewById(R.id.rename_dialog_text_delete);
//        TextView text_time = (TextView) view.findViewById(R.id.rename_dialog_text_resettime);
//        TextView text_count = (TextView) view.findViewById(R.id.rename_dialog_text_resetcount);
//        text_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listener != null) listener.delete();
//                resetPlayDialog.dismiss();
//            }
//        });
//        text_time.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listener != null) listener.playTime();
//                resetPlayDialog.dismiss();
//            }
//        });
//        text_count.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listener != null) listener.playCount();
//                resetPlayDialog.dismiss();
//            }
//        });
//
//        resetPlayDialog.setContentView(view);
//        Window window = resetPlayDialog.getWindow();
//        window.setGravity(Gravity.BOTTOM);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = screenWidth;
//        window.setAttributes(lp);
//        resetPlayDialog.setCanceledOnTouchOutside(true);
//        resetPlayDialog.setCancelable(true);
//        resetPlayDialog.show();
//    }
//
//    //通用改名字对话框
    public void commonRenameDialog(Context context, final OnDialogClickImp imp) {
        if (commonRenameDialog != null && commonRenameDialog.isShowing()) {
            return;
        }
        commonRenameDialog = new Dialog(context, R.style.dialog_loading_nobackground);
        View view = View.inflate(context, R.layout.layout_dialog_common_rename, null);
        TextView text_confirm = view.findViewById(R.id.rename_dialog_text_confirm);
        TextView text_cancel = view.findViewById(R.id.rename_dialog_text_cancel);
        final EditText text_content = view.findViewById(R.id.rename_dialog_et_content);
        text_cancel.setOnClickListener(v -> commonRenameDialog.dismiss());
        text_confirm.setOnClickListener(v -> {
            String content = text_content.getText().toString();
            if (TextUtils.isEmpty(content)) return;
            if (imp != null) imp.sendValue(content);
            commonRenameDialog.dismiss();
        });
        commonRenameDialog.setContentView(view);
        Window window = commonRenameDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = screenWidth * 3 / 4;
        window.setAttributes(lp);
        commonRenameDialog.setCanceledOnTouchOutside(true);
        commonRenameDialog.setCancelable(true);
        commonRenameDialog.show();
    }

    //
//    //音量和亮度通用弹出框
//    public void vbCommonDialog(final Context context, int imgId, int curPro, final OnSeekbarChangeImp imp) {
//        if (vbCommonDialog != null && vbCommonDialog.isShowing()) {
//            return;
//        }
//        vbCommonDialog = new Dialog(context, R.style.dialog_loading_nobackground);
//        View view = View.inflate(context, R.layout.layout_dialog_vandb_common, null);
//
//        SeekBar sk_brightness = (SeekBar) view.findViewById(R.id.dialog_vb_seekbar);
//        final TextView tv_voice = (TextView) view.findViewById(R.id.dialog_vb_text);
//        final ImageView img = (ImageView) view.findViewById(R.id.dialog_vb_img);
//        img.setImageResource(imgId);
//        tv_voice.setText(curPro + "%");
//        sk_brightness.setProgress(curPro);
//        sk_brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                tv_voice.setText(seekBar.getProgress() + "%");
//                if (imp != null) imp.onChange(progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                if (imp != null) imp.onStop(seekBar.getProgress());
//            }
//        });
//
//        vbCommonDialog.setContentView(view);
//        Window window = vbCommonDialog.getWindow();
//        window.setGravity(Gravity.CENTER);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = screenWidth * 3 / 4;
//        window.setAttributes(lp);
//        vbCommonDialog.setCanceledOnTouchOutside(true);
//        vbCommonDialog.setCancelable(true);
//        vbCommonDialog.show();
//
//    }
//
//    GrandarSeekBar seekBar;
//
//    public View getGrandarSeekBar() {
//        return seekBar;
//    }
//
//    //音量和亮度通用弹出框
//    public void setVolumeDialog(final Context context, final String title, int leftImg,
//                                int rightImg, final int curPro, final OnSeekbarChangeImp imp) {
//        if (setVolumeDialog != null && setVolumeDialog.isShowing()) {
//            return;
//        }
//        setVolumeDialog = new Dialog(context, R.style.dialog_loading_nobackground);
//        View view = View.inflate(context, R.layout.seekbar_dialog, null);
//        seekBar = (GrandarSeekBar) view.findViewById(R.id.seekdialog_seekbar);
//        ImageView img_left = (ImageView) view.findViewById(R.id.seekdialog_small_icon);
//        ImageView img_right = (ImageView) view.findViewById(R.id.seekdialog_large_icon);
//        final TextView text_title = (TextView) view.findViewById(R.id.seekdialog_title);
//        img_left.setImageResource(leftImg);
//        img_right.setImageResource(rightImg);
//        text_title.setText(context.getString(setting) + title + curPro + "%");
//        seekBar.setProgress(curPro);
//        // TODO: 2017/6/12 增加seekbar可点击区域
//        img_left.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                seekBar.setProgress(0);
//                text_title.setText(context.getString(setting) + title + seekBar.getProgress() + "%");
//                if (imp != null) imp.onStop(seekBar.getProgress());
//            }
//        });
//        img_right.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                seekBar.setProgress(100);
//                text_title.setText(context.getString(setting) + title + seekBar.getProgress() + "%");
//                if (imp != null) imp.onStop(seekBar.getProgress());
//            }
//        });
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                text_title.setText(context.getString(setting) + title + seekBar.getProgress() + "%");
//                if (imp != null) imp.onChange(progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                if (imp != null) imp.onStar();
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                if (imp != null) imp.onStop(seekBar.getProgress());
//            }
//        });
//
//        setVolumeDialog.setContentView(view);
//        Window window = setVolumeDialog.getWindow();
//        window.setGravity(Gravity.CENTER);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = screenWidth * 4 / 5;
//        window.setAttributes(lp);
//        setVolumeDialog.setCanceledOnTouchOutside(true);
//        setVolumeDialog.setCancelable(true);
//        setVolumeDialog.show();
//
//    }
//
//    //关闭音量对话框
//    public void dismissVoiceDialog() {
//        if (setVolumeDialog != null && setVolumeDialog.isShowing()) {
//            setVolumeDialog.dismiss();
//        }
//    }
//
//    //关闭搜索对话框
//    public void dismissLoadDialog() {
//        if (loadDialog != null && loadDialog.isShowing()) {
//            loadDialog.dismiss();
//        }
//    }
//
//
//    public void loadDialog(final Context context, final OnDialogClickImp listener) {
//        if (loadDialog != null && loadDialog.isShowing()) {
//            return;
//        }
//        loadDialog = new Dialog(context, R.style.dialog_loading_nobackground);
//
//        View view = View.inflate(context, R.layout.layout_dialog_device_search, null);
//
//        loadDialog.setContentView(view);
//        Window window = loadDialog.getWindow();
//        window.setGravity(Gravity.CENTER);
//        loadDialog.setCanceledOnTouchOutside(true);
//        loadDialog.setCancelable(true);
//        loadDialog.show();
//    }
//
//    public void delFileDialog(final Context context, final OnDialogClickImp listener) {
//        if (delFileDialog != null && delFileDialog.isShowing()) {
//            return;
//        }
//        delFileDialog = new Dialog(context, R.style.dialog_loading_nobackground);
//
//        View view = View.inflate(context, R.layout.layout_view_dialog_delfile, null);
//        Button btn_del = (Button) view.findViewById(R.id.delfile_report_del);
//        Button btn_cancel = (Button) view.findViewById(R.id.delfile_report_cancel);
//        btn_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                delFileDialog.dismiss();
//            }
//        });
//        btn_del.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listener != null) listener.sendValue(null);
//                delFileDialog.dismiss();
//            }
//        });
//        delFileDialog.setContentView(view);
//
//        Window window = delFileDialog.getWindow();
//        window.setGravity(Gravity.BOTTOM);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = screenWidth;
//        window.setAttributes(lp);
//        delFileDialog.setCanceledOnTouchOutside(true);
//        delFileDialog.setCancelable(true);
//        delFileDialog.show();
//
//    }
//
//
    public interface OnDialogClickImp {
        void sendValue(Object obj);

    }

    //
//    public interface OnDialogClick {
//        void cancle(Object obj);
//
//    }
//
//    public interface onDialogVoiceAndBrightness {
//        void getInfo(String v1, String v2);
//    }
//
//    public interface OnDialogClickDay {
//        void getDays(String totalday, boolean b1, boolean b2, boolean b3, boolean b4, boolean b5, boolean b6, boolean b7);
//
//    }
//
//    public interface OnDialogPhotographImp {
//        void camera();
//
//        void album();
//    }
//
//    public interface OnResetPlayImp {
//        void playTime();
//
//        void playCount();
//
//        void delete();
//    }
//
//    public interface OnSeekbarChangeImp {
//        void onStop(int progress);
//
//        void onChange(int progress);
//
//        void onStar();
//    }
//
    public interface popWiondowCallback {
        void addDevice();

        void searchDevice();
    }
//
//    //去掉拼接字符串中的null;
//    private static String getReplaceAllBuilderString(String... strs) {
//        StringBuilder builder = new StringBuilder();
//
//        for (int i = 0; i < strs.length; i++) {
//            String string = strs[i];
//            builder.append(string);
//        }
//        String st = builder.toString().replaceAll("null", "");
//
//        return st;
//    }

}
