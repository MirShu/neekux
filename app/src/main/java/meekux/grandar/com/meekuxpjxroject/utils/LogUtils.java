package meekux.grandar.com.meekuxpjxroject.utils;

import android.util.Log;

/**
 * Description : 日子工具类
 * Author : lauren
 * Email  : lauren.liuling@gmail.com
 * Blog   : http://www.liuling123.com
 * Date   : 15/12/14
 */
public class LogUtils {

    public static final boolean GRANDAR_LOG_ENABLED = true;
    public static final boolean DEBUG_ON_PC = false;
    public static final boolean DEBUG_ON_REMOTECONTROL = true;
    public static final boolean DEBUG_ON_REMOTECONTROL_TEST = false;//长时间发数据包测试
    public static final boolean DEBUG_NIGHT_LIGHT = true;
    public static final boolean DEBUG_ON_UDP = true;

    public static  boolean DEBUG = true;

    public static void setDebug(boolean bool){
          DEBUG=bool;
    }

    public static void v(String tag, String message) {
        if(DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if(DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if(DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if(DEBUG) {
            Log.w(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if(DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void e(String tag, String message, Exception e) {
        if(DEBUG) {
            Log.e(tag, message, e);
        }
    }
}
