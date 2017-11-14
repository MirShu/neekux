package meekux.grandar.com.meekuxpjxroject.utils;

import android.content.Context;

/**
 * Author:SeanLim
 * Created by Time on: 2017/9/8
 * 类名：DataTools
 */
public class DataTools {
    public static int dip2px(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
