package meekux.grandar.com.meekuxpjxroject.songdata.database;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.util.Log;


import com.socks.library.KLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;

import meekux.grandar.com.meekuxpjxroject.utils.LogUtils;

public class MyUtil {

    private static final String TAG = "MyUtil";

    /* byte[]转Int */
    public static int bytesToInt(byte[] bytes) {
        int addr = bytes[0] & 0xFF;
        addr |= ((bytes[1] << 8) & 0xFF00);
        addr |= ((bytes[2] << 16) & 0xFF0000);
        addr |= ((bytes[3] << 24) & 0xFF000000);
        return addr;
    }

    /* byte[]转Int */
    public static int bytesToInt(byte[] bytes, int index) {
        int addr = bytes[index] & 0xFF;
        addr |= ((bytes[index + 1] << 8) & 0xFF00);
        addr |= ((bytes[index + 2] << 16) & 0xFF0000);
        addr |= ((bytes[index + 3] << 24) & 0xFF000000);
        return addr;
    }

    /* Int转byte[] */
    public static byte[] intToByte(int i) {
        byte[] abyte0 = new byte[4];
        abyte0[0] = (byte) (0xff & i);
        abyte0[1] = (byte) ((0xff00 & i) >> 8);
        abyte0[2] = (byte) ((0xff0000 & i) >> 16);
        abyte0[3] = (byte) ((0xff000000 & i) >> 24);
        return abyte0;
    }

    /* long转byte[] */
    public static byte[] longToByte(long temp) {
        byte[] abyte0 = new byte[8];
        for (int i = 0; i < abyte0.length; i++) {

            abyte0[i] = (byte) (temp & 0xff);
            temp = temp >> 8;
        }

        return abyte0;
    }

    /**
     * 将16进制数组转为字符串显示
     */
    public static String HexToString(byte[] buff) {
        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < buff.length; i++) {
            msg.append(String.format("%02x", buff[i]));
        }
        return msg.toString();
    }

    /**
     * 将unicode 字符串
     *
     * @param str 待转字符串
     * @return 普通字符串
     */
    public static String revert(String str) {
        str = (str == null ? "" : str);
        if (str.indexOf("\\u") == -1)// 如果不是unicode码则原样返回
            return str;

        StringBuffer sb = new StringBuffer(1000);

        for (int i = 0; i < str.length() - 6; ) {
            String strTemp = str.substring(i, i + 6);
            String value = strTemp.substring(2);
            int c = 0;
            for (int j = 0; j < value.length(); j++) {
                char tempChar = value.charAt(j);
                int t = 0;
                switch (tempChar) {
                    case 'a':
                        t = 10;
                        break;
                    case 'b':
                        t = 11;
                        break;
                    case 'c':
                        t = 12;
                        break;
                    case 'd':
                        t = 13;
                        break;
                    case 'e':
                        t = 14;
                        break;
                    case 'f':
                        t = 15;
                        break;
                    default:
                        t = tempChar - 48;
                        break;
                }

                c += t * ((int) Math.pow(16, (value.length() - j - 1)));
            }
            sb.append((char) c);
            i = i + 6;
        }
        return sb.toString();
    }

    public static int SONG_AND_LIGHT = 17;
    public static int ONLY_LIGHT = 1;

    public static int getSongLightType(String fileName) {
        String strFileName = fileName;// String.format("%s/Grandar/LightMp3/%d.lovled",
        // Environment.getExternalStorageDirectory().getAbsolutePath(),
        KLog.e("strFileName========>" + strFileName);
        // nNum);
        int type = -1;
        File file = new File(fileName);
        if (!file.exists()) {
            Log.i(TAG, "getSongLightType file not exitst ");
            return -1;
        }
        FileInputStream finMp3 = null;
        try {
            finMp3 = new FileInputStream(strFileName);
            int nLen = finMp3.available();
            byte[] bHead = new byte[292];
            int nSize = finMp3.read(bHead); // 获取文件头 (292字节)
            if (bHead[275] == 0x11)
                type = SONG_AND_LIGHT;
            else if (bHead[275] == 0x01)
                type = ONLY_LIGHT;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            finMp3.close();
        } catch (IOException ignored) {
        }
        return type;
    }

    public static int getLightSongFrame(String fileName) {
        String strFileName = fileName;// String.format("%s/Grandar/LightMp3/%d.lovled",
        int Mp3frameCount = 0;
        File file = new File(fileName);
        if (!file.exists()) {
            Log.i(TAG, "getSongLightType file not exitst ");
            return -1;
        }
        FileInputStream finMp3 = null;
        try {
            finMp3 = new FileInputStream(strFileName);
            int nLen = finMp3.available();
            byte[] bHead = new byte[292];
            int nSize = finMp3.read(bHead); // 获取文件头 (292字节)
            int offset = 0;

            offset += (bHead[276] & 0x00FF);
            offset += (bHead[277] & 0x00FF) << 8;
            offset += (bHead[278] & 0x00FF) << 16;
            offset += (bHead[279] & 0x00FF) << 24;

            // mp3_expand_len = offset;
            finMp3.skip(offset);

            Mp3frameCount += (bHead[280] & 0x00FF);
            Mp3frameCount += (bHead[281] & 0x00FF) << 8;
            Mp3frameCount += (bHead[282] & 0x00FF) << 16;
            Mp3frameCount += (bHead[283] & 0x00FF) << 24;

            String str = String.format("Mp3总帧数: %d", Mp3frameCount);
            KLog.e(TAG, "getLightSongFrame " + str);
        } catch (IOException e) {
            e.printStackTrace();
            KLog.e(TAG, "printStackTrace " + e.getMessage());
        }
        try {
            finMp3.close();
        } catch (IOException ignored) {
            KLog.e(TAG, "ignored " + ignored.getMessage());
        }
        return Mp3frameCount;
    }

    public static int getLightSongOffset(String fileName) {
        String strFileName = fileName;// String.format("%s/Grandar/LightMp3/%d.lovled",
        int offset = 0;
        File file = new File(fileName);
        if (!file.exists()) {
            Log.i(TAG, "getSongLightType file not exitst ");
            return -1;
        }
        FileInputStream finMp3 = null;
        try {
            finMp3 = new FileInputStream(strFileName);
            int nLen = finMp3.available();
            byte[] bHead = new byte[292];
            int nSize = finMp3.read(bHead); // 获取文件头 (292字节)

            offset += (bHead[276] & 0x00FF);
            offset += (bHead[277] & 0x00FF) << 8;
            offset += (bHead[278] & 0x00FF) << 16;
            offset += (bHead[279] & 0x00FF) << 24;

            String str = String.format("offset: %d", offset);
            LogUtils.e(TAG, "getLightSongOffset " + str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            finMp3.close();
        } catch (IOException ignored) {
        }
        return offset;
    }

    public static Boolean isSongAndLight(int type) {
        if (type == SONG_AND_LIGHT)
            return true;

        return false;
    }

    public static byte[] getSongLightMd5(String fileName) {
        byte[] md5 = new byte[16];

        File file = new File(fileName);
        if (!file.exists()) {
            Log.i(TAG, "getSongLightMd5 file not exitst ");
            return md5;
        }

        FileInputStream finMp3 = null;
        try {
            finMp3 = new FileInputStream(fileName);
            int nLen = finMp3.available();
            byte[] bHead = new byte[292];
            int nSize = finMp3.read(bHead); // 获取文件头 (292字节)
            System.arraycopy(bHead, 259, md5, 0, 16);
            String msg = null;
            for (int i = 0; i < 16; i++) {
                msg = msg + String.format(" %2x", md5[i]);
            }
            LogUtils.e(TAG, "getSongLightMd5 msg=" + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            finMp3.close();
        } catch (IOException ignored) {
        }
        return md5;
    }

    public static int getSongLightTime(String fileName) {
        byte[] time = new byte[4];
        int time_int = 0;

        File file = new File(fileName);
        if (!file.exists()) {
            Log.i(TAG, "getSongLightMd5 file not exitst ");
            return time_int;
        }

        FileInputStream finMp3 = null;
        try {
            finMp3 = new FileInputStream(fileName);
            int nLen = finMp3.available();
            byte[] bHead = new byte[292];
            int nSize = finMp3.read(bHead); // 获取文件头 (292字节)
            System.arraycopy(bHead, 255, time, 0, 4);
            String msg = null;
            for (int i = 0; i < 4; i++) {
                msg = msg + String.format(" %2x", time[i]);
            }
            time_int = bytesToInt(time);
            LogUtils.e(TAG, "getSongLightTime fileName=" + fileName
                    + " msg=" + msg + " time_int=" + time_int);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            finMp3.close();
        } catch (IOException ignored) {
        }
        return time_int;
    }

    public static long getCRC32(File f) {
        try {
            FileInputStream in = new FileInputStream(f);
            CRC32 crc = new CRC32();
            byte[] bytes = new byte[8192];
            int byteCount;
            crc.reset();
            while ((byteCount = in.read(bytes)) > 0) {
                crc.update(bytes, 0, byteCount);
            }
            in.close();
            long sum = crc.getValue();
            return sum;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getSongLightDescr(String fileName) {
        byte[] desc = null;
        String desc_str = null;
        File file = new File(fileName);
        if (!file.exists()) {
            Log.i(TAG, "getSongLightDescr file not exitst ");
            return desc_str;
        }

        FileInputStream finMp3 = null;
        try {
            finMp3 = new FileInputStream(fileName);
            int nLen = finMp3.available();
            byte[] bHead = new byte[292];
            int nSize = finMp3.read(bHead); // 获取文件头 (292字节)
            // 读取版本号标识，正确再进行读取
            byte[] verFlag = new byte[2];
            byte[] flag = {(byte) 0x01, (byte) 0xFE};
            System.arraycopy(bHead, 233, verFlag, 0, 2);
            if (Arrays.equals(verFlag, flag) == false) {
                return desc_str;
            }

            // 得到描述地址 描述长度
            byte[] descrOffset = new byte[4];
            byte[] descrSize = new byte[4];
            System.arraycopy(bHead, 239, descrOffset, 0, 4);
            System.arraycopy(bHead, 243, descrSize, 0, 4);
            int descrOffset_int = bytesToInt(descrOffset);
            int descrSize_int = bytesToInt(descrSize);

            // copy 描述数据
            desc = new byte[descrSize_int];
            int skip_size = (int) finMp3.skip(descrOffset_int - 292);
            int read_size = finMp3.read(desc);

            String msg = null;
            for (int i = 0; i < descrSize_int; i++) {
                msg = msg + String.format(" %2x", desc[i]);
                if (i > 10) {
                    break;
                }
            }
            desc_str = new String(desc, "UTF-8");
            LogUtils.e(TAG, "getSongLightDescr msg=" + msg
                    + " desc_str:" + desc_str + " desc:" + desc.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            finMp3.close();
        } catch (IOException ignored) {
        }
        return desc_str;
    }

    public static byte[] getSongLightIcon(String fileName) {
        byte[] icon = null;

        File file = new File(fileName);
        if (!file.exists()) {
            Log.i(TAG, "getSongLightIcon file not exitst ");
            return null;
        }

        FileInputStream finMp3 = null;
        try {
            finMp3 = new FileInputStream(fileName);
            int nLen = finMp3.available();
            byte[] bHead = new byte[292];
            int nSize = finMp3.read(bHead); // 获取文件头 (292字节)
            // 读取版本号标识，正确再进行读取
            byte[] verFlag = new byte[2];
            byte[] flag = {(byte) 0x01, (byte) 0xFE};
            System.arraycopy(bHead, 233, verFlag, 0, 2);
            if (Arrays.equals(verFlag, flag) == false) {
                return null;
            }

            // 得到图标地址 图标长度
            byte[] iconOffset = new byte[4];
            byte[] iconSize = new byte[4];
            System.arraycopy(bHead, 247, iconOffset, 0, 4);
            System.arraycopy(bHead, 251, iconSize, 0, 4);
            int iconOffset_int = bytesToInt(iconOffset);
            int iconSize_int = bytesToInt(iconSize);

            // copy png 图片数据
            icon = new byte[iconSize_int];
            int skip_size = (int) finMp3.skip(iconOffset_int - 292);
            int read_size = finMp3.read(icon);

            String msg = null;
            for (int i = 0; i < iconSize_int; i++) {
                msg = msg + String.format(" %2x", icon[i]);
                if (i > 10) {
                    break;
                }
            }
            LogUtils.e(TAG, "getSongLightIcon msg=" + msg
                    + " read_size=" + read_size + " skip_size=" + skip_size);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            finMp3.close();
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
        return icon;
    }

    public static int getSongLightVerno(String fileName) {
        int verno_int = -1;
        File file = new File(fileName);
        if (!file.exists()) {
            Log.i(TAG, "getSongLightVerno file not exitst ");
            return -1;
        }

        FileInputStream finMp3 = null;
        try {
            finMp3 = new FileInputStream(fileName);
            int nLen = finMp3.available();
            byte[] bHead = new byte[292];
            int nSize = finMp3.read(bHead); // 获取文件头 (292字节)
            // 读取版本号标识，正确再进行读取
            byte[] verFlag = new byte[2];
            byte[] flag = {(byte) 0x01, (byte) 0xFE};
            byte[] verno = new byte[4];
            System.arraycopy(bHead, 233, verFlag, 0, 2);
            System.arraycopy(bHead, 235, verno, 0, 4);
            if (Arrays.equals(verFlag, flag) == false) {
                verno_int = 0;
            } else {
                verno_int = bytesToInt(verno);
            }
            LogUtils.e(TAG, "getSongLightVerno verno_int=" + verno_int);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            finMp3.close();
        } catch (IOException ignored) {
        }
        return verno_int;
    }

    public static String getTopActivity(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            // Log.d(TAG, tasksInfo.get(0).topActivity.getPackageName());
            // 应用程序位于堆栈的顶层
            // Log.d(TAG,
            // "isTopActivity: "+tasksInfo.get(0).topActivity.getClassName());
            return tasksInfo.get(0).topActivity.getClassName();
        }
        return null;
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[8192];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static boolean isAppDelete;

    public static void setIsAppDelete(boolean isDelete) {
        isAppDelete = isDelete;
    }

    public static boolean getIsAppDelete() {
        return isAppDelete;
    }
}
