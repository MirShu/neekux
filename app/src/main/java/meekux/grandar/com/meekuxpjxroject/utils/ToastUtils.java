package meekux.grandar.com.meekuxpjxroject.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Description:
 * Author:yangzhenxiang
 * Email:yzxandroid981@163.com
 * Date : 2016/5/11
 */
public class ToastUtils {
    private static Context mContext;
    private static Toast mToast;

    public static void init(Context context) {
        mContext = context;
    }

    public static void show(Context context, String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
        mToast = toast;
    }


    public static void show(String msg) {
        if (mContext != null) {
            if (mToast != null) {
                mToast.cancel();
            }
            Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
            toast.show();
            mToast = toast;
        }
    }

    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
