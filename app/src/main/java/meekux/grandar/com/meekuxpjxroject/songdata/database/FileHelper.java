package meekux.grandar.com.meekuxpjxroject.songdata.database;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import meekux.grandar.com.meekuxpjxroject.utils.LogUtils;

public class FileHelper {
    Context context;
    // private static String FILEPATH = "/data/local/tmp";
    public static final String TAG = "FileHelper";
    public static String IGOOPATH = Environment.getExternalStorageDirectory().getPath() + "/IGOO";
    // public static String FILEPATH = Environment.getExternalStorageDirectory().getPath() + "/IGOO/BabyLauncher";
    public static String FILEPATH = Environment.getExternalStorageDirectory().getPath() + "/IGOO/Grandar";
    public static String GRANDARPATH = Environment.getExternalStorageDirectory().getPath() + "/IGOO/SongLightStore";
    public static String MUSIC_PATH = Environment.getExternalStorageDirectory().getPath() + "/IGOO/Music_path";
    public static String COLOUR_PATH = Environment.getExternalStorageDirectory().getPath() + "/IGOO/Colour_path";
    //public static String NETCTRLPATH = Environment.getExternalStorageDirectory().getPath() + "/IGOO/NetCtrl";
    public static String DATABASE_NAME = FILEPATH + "/sdcard_igoo.db";
    public static String EXPORTFILEPATH = FILEPATH + "/data.bin";

    // private static String FILEPATH = "/tmp";

    public static File newFile(String filepath, String fileName) {
        File file = null;
        try {
            LogUtils.e(TAG, "newFile filepath=" + filepath
                    + " fileName=" + fileName);

            File fileDir = new File(FILEPATH + filepath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            file = new File(FILEPATH + filepath, fileName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LogUtils.e(TAG, "newFile error");
            e.printStackTrace();
        }
        return file;
    }

    public static void writeFile(File file, byte[] data, int offset, int count)
            throws IOException {

        FileOutputStream fos = new FileOutputStream(file, true);
        fos.write(data, offset, count);
        fos.flush();
        fos.close();
    }

    public static byte[] readFile(String fileName) throws IOException {
        File file = new File(FILEPATH, fileName);
        file.createNewFile();
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        int leng = bis.available();
        Log.v(TAG, "filesize = " + leng);
        byte[] b = new byte[leng];
        bis.read(b, 0, leng);
        // Input in = new Input(fis);
        // byte[] ret = in.readAll();
        // in.close();
        bis.close();
        return b;

    }

    public static boolean deleteDiskFile(String filePath, String fileName) {
        // TODO Auto-generated method stub
        File file = null;
        file = new File(filePath, fileName);
        return file.delete();
    }

    public static void copyAssetsDataToSD(Context context, String path, String strOutFileName) {
        try {
            File fileDir = new File(path);

            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            File file = new File(path, strOutFileName);
            if (file.exists()) {
                return;// 文件已存在则返回
            }

            InputStream myInput;
            OutputStream myOutput = new FileOutputStream(
                    path + "/" + strOutFileName);
            myInput = context.getAssets().open(strOutFileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }

            myOutput.flush();
            myInput.close();
            myOutput.close();
        } catch (IOException e) {
            // TODO: handle exception
            LogUtils.e(TAG, "newFile error:" + e);
        }
    }
}
