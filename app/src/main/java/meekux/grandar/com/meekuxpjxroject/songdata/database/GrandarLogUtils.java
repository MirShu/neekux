package meekux.grandar.com.meekuxpjxroject.songdata.database;

import android.util.Log;

public class GrandarLogUtils {
	public static final boolean GRANDAR_LOG_ENABLED = true;
    public static final boolean DEBUG_ON_PC = true;
    public static final boolean DEBUG_ON_REMOTECONTROL = true;
    public static final boolean DEBUG_ON_REMOTECONTROL_TEST = false;//长时间发数据包测试
    public static final boolean DEBUG_ON_EARPHONE = true;
    public static final boolean DEBUG_ON_SAMSUNG = false;
    public static final boolean DEBUG_ON_STATUSICONBAR = false;
    //private static final boolean GRANDAR_LOG_ENABLED = false;
    public static final boolean DEBUG_CONNECTAGAIN = true;
    public static final boolean DEBUG_FACTORY = false;
    public static final boolean DEBUG_NIGHT_LIGHT = true;
    public static final boolean DEBUG_ON_UDP = true;
      
    public static final int v(String tag, String msg) {
        int result = 0;
        if (true == GRANDAR_LOG_ENABLED ) {
            result = Log.v(tag,msg);
        }
        return result;
    }

    public static final int v(String tag, String msg, Throwable tr) {
        int result = 0;
        if (true == GRANDAR_LOG_ENABLED ) {
            result = Log.v(tag,msg,tr);
        }
        return result;
    }
    
    public static final int d(String tag, String msg) {
        int result = 0;
        if (true == GRANDAR_LOG_ENABLED ) {
            result = Log.d(tag,msg);
        }
        return result;
    }

    public static final int d(String tag, String msg, Throwable tr) {
        int result = 0;
        if (true == GRANDAR_LOG_ENABLED ) {
            result = Log.d(tag,msg,tr);
        }
        return result;
    }
    public static final int i(String tag, String msg) {
        int result = 0;
        if (true == GRANDAR_LOG_ENABLED ) {
            result = Log.i(tag,msg);
        }
        return result;
    }
    
    public static final int i(String tag, String msg, Throwable tr) {
        int result = 0;
        if (true == GRANDAR_LOG_ENABLED ) {
            result = Log.i(tag,msg,tr);
        }
        return result;
    }
    
    public static final int w(String tag, String msg) {
        int result = 0;
        if (true == GRANDAR_LOG_ENABLED ) {
            result = Log.w(tag,msg);
        }
        return result;
    }
    
    public static final int w(String tag, String msg, Throwable tr) {
        int result = 0;
        if (true == GRANDAR_LOG_ENABLED ) {
            result = Log.w(tag,msg,tr);
        }
        return result;
    }
    
    public static final int e(String tag, String msg) {
        int result = 0;
        if (true == GRANDAR_LOG_ENABLED ) {
            result = Log.e(tag,msg);
        }
        return result;
    }

    public static final int e(String tag, String msg, Throwable tr) {
        int result = 0;
        if (true == GRANDAR_LOG_ENABLED ) {
            result = Log.e(tag,msg,tr);
        }
        return result;
    }
      
}
