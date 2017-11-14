package meekux.grandar.com.meekuxpjxroject.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

import meekux.grandar.com.meekuxpjxroject.R;

/**
 * Author:SeanLim
 * Created by Time on: 2017/8/29
 * 类名：ImageLoaderEx
 */
public class ImageLoaderEx {
    private static final String CACHE_DIR = "quant-power/cache";
    private static ImageLoader imageLoader = ImageLoader.getInstance();
    private static DisplayImageOptions options = null;


    private DisplayImageOptions optionssrc;

    /**
     * @param context
     * @return
     */
    public static File getCacheDir(Context context) {
        File cacheDir = StorageUtils.getCacheDirectory(context);
        return cacheDir;
    }

    /**
     * @param context
     * @return
     */
    public static File getOwnCacheDir(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, CACHE_DIR);
        return cacheDir;
    }

    /**
     * 初始化默认加载图片
     *
     * @return
     */
    public static DisplayImageOptions getDisplayImageOptions() {
        if (options != null) {
            return options;
        }
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnFail(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showStubImage(R.mipmap.ic_launcher)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        return options;
    }

    /**
     * @return
     */
    public static DisplayImageOptions getDefaultDisplayImageOptions() {
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        return displayImageOptions;
    }

    /**
     * 从缓存中获取Bitmap对象
     *
     * @param filePath 网络图片路径
     * @return
     */
    public static Bitmap getBitmapByCache(String filePath) {
        Bitmap bitmap = imageLoader.getMemoryCache().get(filePath);
        if (bitmap == null) {
            File file = imageLoader.getDiskCache().get(filePath);
            if (file != null && file.exists()) {
                bitmap = BitmapFactory.decodeFile(file.getPath());
            }
        }
        return bitmap;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

}
