package meekux.grandar.com.meekuxpjxroject.utils;

import android.util.Log;

/**
 * Created by liuqk on 2017/5/28.
 *
 */

public class StringgetByte {
    public static byte[] getByte(String string) {
        String str1 = string.substring(1, string.length() - 1);
        String str2 = str1.replace(" ", "");
        String[] substr = str2.split(",");
        int len = substr.length;
        byte[] b = new byte[len];
        for (int i = 0; i <= len - 1; i++) {
            String s = substr[i];
            int intstr = Integer.parseInt(s);
            b[i] = (byte) intstr;
        }
        Log.d("nihao", "nhao" + b);
        return b;
    }
}
