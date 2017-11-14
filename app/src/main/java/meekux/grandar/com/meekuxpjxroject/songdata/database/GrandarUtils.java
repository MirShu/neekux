package meekux.grandar.com.meekuxpjxroject.songdata.database;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.socks.library.KLog;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;

import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.service.TcpClientService;
import meekux.grandar.com.meekuxpjxroject.utils.IConstant;
import meekux.grandar.com.meekuxpjxroject.utils.LogUtils;
import meekux.grandar.com.meekuxpjxroject.utils.SharedPreferencesUtils;

public class GrandarUtils {

    private static final String TAG = "GrandarUtils";
    private static TcpClientService.TcpClientServiceBinder sService;
    private static Activity sContext;
    private static HashMap<Context, ServiceBinder> sConnectionMap = new HashMap<Context, ServiceBinder>();


    public static class ServiceToken {
        ContextWrapper mWrappedContext;

        ServiceToken(ContextWrapper context) {
            mWrappedContext = context;
        }
    }

    public static ServiceToken bindToService(Activity context) {
        LogUtils.e("开始绑定服务器-----", "..");
        return bindToService(context, null);
    }

    public static ServiceToken bindToService(Activity context,
                                             ServiceConnection callback) {
        Activity realActivity = context.getParent();
        if (realActivity == null) {
            realActivity = context;
        }
        sContext = context;
        LogUtils.e(TAG, "bindToService: activity=" + context.toString()
                + " callback:" + callback);
        ContextWrapper cw = new ContextWrapper(realActivity);
        Intent intent = new Intent(cw, TcpClientService.class);
        cw.startService(intent);
        ServiceBinder sb = new ServiceBinder(callback);
        LogUtils.d(TAG, "服务开启---->" + sb);
        if (cw.bindService((new Intent()).setClass(cw, TcpClientService.class),
                sb, 0)) {
            LogUtils.d(TAG, "服务开启---->" + sb + "..." + cw);
            sConnectionMap.put(cw, sb);
            return new ServiceToken(cw);
        }
        LogUtils.e(TAG, "Failed to bind to service");
        return null;
    }

    public static void unbindFromService(ServiceToken token) {
        // mLastSdStatus = null;
        LogUtils.e(TAG, "unbindFromService");

        // sService.setOnBtStateChangedListner(null);

        if (token == null) {
            LogUtils.e(TAG, "Trying to unbind with null token");
            return;
        }
        ContextWrapper cw = token.mWrappedContext;
        ServiceBinder sb = sConnectionMap.remove(cw);
        if (sb == null) {
            LogUtils.e(TAG, "Trying to unbind for unknown Context");
            return;
        }
        cw.unbindService(sb);
        if (sConnectionMap.isEmpty()) {
            // presumably there is nobody interested in the service at this
            // point,
            // so don't hang on to the ServiceConnection
            sService = null;
        }

    }

    private static class ServiceBinder implements ServiceConnection {
        ServiceConnection mCallback;

        ServiceBinder(ServiceConnection callback) {
            mCallback = callback;
        }

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            sService = (TcpClientService.TcpClientServiceBinder) service;
            LogUtils.e("服务状态-----", sService + "");
            // initAlbumArtCache();
            if (sService != null) {
                if (mCallback != null) {
                    mCallback.onServiceConnected(className, service);
                }
                getRemoteConnectState();
            }
            LogUtils.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            if (mCallback != null) {
                mCallback.onServiceDisconnected(className);
            }
            sService = null;
            LogUtils.d(TAG, "onServiceDisconnected");
        }

    }

    public static String getRemoteConnectState() {
        if (sService != null) {

            return sService.getRemoteConnectState();
        }
        return "Fail";
    }

    public static boolean sendNightLightToDevice() {
        if (!getRemoteConnectState().equals("Success")) {
            return false;
        }
        if (sService != null) {
            // MainActivity.nProgressLight = 100;
            sService.sendNightLightToDevice();
            return true;
        }
        return false;
    }

    public static boolean sendLightFilesListToDevice(final String filesListName,
                                                     final int beginIndex) {
        if (!getRemoteConnectState().equals("Success")) {
            return false;
        }

        if (sService != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        sService.setWholeLightness
                                (SharedPreferencesUtils.getinstance().getIntValue(IConstant.LIGHTPROGRESS, IConstant.DEFAULTLIGHT));
                        Thread.sleep(500);
                        sService.setVolumeness
                                (SharedPreferencesUtils.getinstance().getIntValue(IConstant.VOICEPROGRESS, IConstant.DEFAULTVOLUME));
                        Thread.sleep(500);
                        sService.setMusicStart();
                        Thread.sleep(500);
                        sService.sendFilesListToDevice(filesListName, beginIndex);


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            sService.setWholeLightness
                    (SharedPreferencesUtils.getinstance().getIntValue(IConstant.LIGHTPROGRESS, IConstant.DEFAULTLIGHT));
//            sService.setVolumeness
//                    (SharedPreferencesUtils.getinstance().getIntValue(IConstant.VOICEPROGRESS, IConstant.DEFAULTVOLUME));
//            sService.setMusicStart();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sService.sendFilesListToDevice(filesListName, beginIndex);
                }
            }, 700);
            return true;
        }
        return false;
    }


    public static boolean sendBabyLightFilesListToDevice(String filesListName, int beginIndex) {
        if (!getRemoteConnectState().equals("Success")) {
            return false;
        }

        if (sService != null) {

            sService.setBabyWholeLightness(100);
//                    (SharedPreferencesUtils.getinstance().getIntValue(IConstant.LIGHTPROGRESS, IConstant.DEFAULTLIGHT));
//            sService.setBabyVolumeness
//                    (SharedPreferencesUtils.getinstance().getIntValue(IConstant.VOICEPROGRESS, IConstant.DEFAULTVOLUME));
            sService.sendBabyFilesListToDevice(filesListName, beginIndex);

            return true;
        }
        return false;
    }

    public static boolean sendLightFilesListToDevice(String filesListName) {
        if (!getRemoteConnectState().equals("Success")) {
            return false;
        }

        if (sService != null) {
            // MainActivity.nProgressLight = 100;
//            sService.setWholeLightness(MainActivity.nProgressLight);
//            sService.setVolumeness(MainActivity.nProgressVolume);
            sService.sendFilesListToDevice(filesListName);
            return true;
        }
        return false;
    }

    //设置音量
    public static boolean setVolumeness(int vol) {
        if (!getRemoteConnectState().equals("Success")) {
            return false;
        }

        if (sService != null) {
            //sService.setVolumeness(vol);
            return true;
        }
        return false;
    }

    //暂停播放
    public static boolean stopPlaying() {
        if (!getRemoteConnectState().equals("Success")) {
            return false;
        }

        if (sService != null) {
            sService.stopMusic();
            return true;
        }
        return false;
    }

    //暂停播放
    public static boolean rePlaying() {
        if (!getRemoteConnectState().equals("Success")) {
            return false;
        }

        if (sService != null) {
            sService.rePlayMusic();
            return true;
        }
        return false;
    }

    public static boolean stopCurPlaying() {
        if (!getRemoteConnectState().equals("Success")) {
            return false;
        }
        boolean flag = false;
        if (sService != null) {
            sService.stopCurPlaying();
            flag = true;
        }
        return flag;
    }

    private static boolean mGetPlayingFlag = false;

    public static void setGetPlayingFlag(boolean b) {
        // TODO Auto-generated method stub
        mGetPlayingFlag = b;
    }

    public static boolean getGetPlayingFlag() {
        // TODO Auto-generated method stub
        return mGetPlayingFlag;
    }

    public static boolean pauseCurPlaying() {
        if (!getRemoteConnectState().equals("Success")) {
            return false;
        }
        boolean flag = false;
        if (sService != null) {
            sService.pauseCurPlaying();
            flag = true;
        }
        if (flag) {
            setPauseFlag(true);
        }
        return flag;
    }

    public static boolean playCurPause() {
        if (!getRemoteConnectState().equals("Success")) {
            return false;
        }
        boolean flag = false;
        if (sService != null) {
            sService.playCurPause();
            flag = true;
        }
        if (flag) {
            setPauseFlag(false);
        }
        return flag;
    }

    private static boolean mPauseFlag = false;

    public static void setPauseFlag(boolean b) {
        // TODO Auto-generated method stub
        mPauseFlag = b;
    }

    public static boolean getPauseFlag() {
        // TODO Auto-generated method stub
        return mPauseFlag;
    }

    public static Cursor query(Context context, Uri uri, String[] projection,
                               String selection, String[] selectionArgs, String sortOrder,
                               int limit) {
        try {
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return null;
            }
            if (limit > 0) {
                uri = uri.buildUpon().appendQueryParameter("limit", "" + limit)
                        .build();
            }
            return resolver.query(uri, projection, selection, selectionArgs,
                    sortOrder);
        } catch (UnsupportedOperationException ex) {
            return null;
        }

    }

    public static Cursor query(Context context, Uri uri, String[] projection,
                               String selection, String[] selectionArgs, String sortOrder) {
        return query(context, uri, projection, selection, selectionArgs,
                sortOrder, 0);
    }

    private static int curModeIdx = -2;
    private static Bitmap mCachedBit = null;
    private static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    private static final HashMap<Long, Drawable> sArtCache = new HashMap<Long, Drawable>(); // 光曲图标
    private static int sArtCacheId = -1;

    static {
        // for the cache,
        // 565 is faster to decode and display
        // and we don't want to dither here because the image will be scaled
        // down later
        sBitmapOptionsCache.inPreferredConfig = Bitmap.Config.RGB_565;
        sBitmapOptionsCache.inDither = false;

        // Modify for Contour issue
        sBitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        sBitmapOptions.inDither = false;
    }

    private static int playingModeIdx = -1; // 正在播放的模式索引

    public static int getPlayingModeIdx(Context context) {
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(Constant.ConValue.PLAYING_LIST_INFO_NAME,
                        Activity.MODE_PRIVATE);
        playingModeIdx = sharedPreferences.getInt("mode", 0);
        return playingModeIdx;
    }

    public static void savePlayingIdx(Context context, int playingIdx) {
        /* 没有播放情况下，返回值为0 */
        if (playingIdx == 0)
            return;

        SharedPreferences sharedPreferences = context
                .getSharedPreferences(Constant.ConValue.PLAYING_LIST_INFO_NAME,
                        Activity.MODE_PRIVATE);

        int curPlayType = sharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_PLAY_TYPE, 0);
        int count = sharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_COUNT, 0);

        if ((curPlayType == Constant.ConValue.PLAY_TYPE_PLAY_LIST)
                || (curPlayType == Constant.ConValue.PLAY_TYPE_SIMPLE_PLAY_LIST)) {
            if (playingIdx <= count) {
                int playingID = sharedPreferences.getInt(
                        Constant.ConValue.PLAYING_LIST_INFO_ID
                                + (playingIdx - 1), 0);
                if (playingID > 0) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(Constant.ConValue.PLAYING_LIST_INFO_FILE_ID,
                            playingID);
                    editor.commit();
                }
            }
        }

    }
    /*
     * SharedPreferences Constant.ConValue.PLAYING_LIST_INFO_NAME mode:当前正在播放的模式
	 * playType：当前属于 光曲、 简单列表、 光曲列表 里的哪种类型 fileListId：如果是光曲列表类型 ，表示当前光曲列表的id 表格
	 * "modei"中的id fileNameId：当前正在播放光曲的id，如果是 光曲、简单列表类型
	 * 则该id为"total_songlights"中的id 如果是 光曲列表，则该id是 modei_list_members的id COUNT
	 * 播放列表的光曲数目 ID_i 列表中第i个的光曲对应的id值
	 */

    /*
     * 保存当前正在播放的光曲列表信息、模式值、、 播放类型,列表id,光曲id,列表中播放第一个光曲的位置 点击简单列表 光曲列表播放时调用此函数
     */
    public static void saveCurPlayingListInfo(Context context, int modeIdx,
                                              int subModeIdx, int playType, int listId, int songLightID,
                                              int beginPos) {
        KLog.e("saveCurPlayingListInfo-1-1");
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(Constant.ConValue.PLAYING_LIST_INFO_NAME,
                        Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt(Constant.ConValue.PLAYING_LIST_INFO_MODE, modeIdx);
        editor.putInt(Constant.ConValue.PLAYING_LIST_INFO_PLAY_TYPE, playType);
        if (playType == Constant.ConValue.PLAY_TYPE_PLAY_LIST) {
            editor.putInt(Constant.ConValue.PLAYING_LIST_INFO_LIST_ID, listId);
            editor.putInt(Constant.ConValue.PLAYING_LIST_INFO_FILE_ID,
                    songLightID);

			/* 记录下列表播放的顺序 */
            Cursor listCursor = queryPlayList(context, modeIdx, listId);
            if ((listCursor != null) && (listCursor.getCount() > 0)) {
                int total = listCursor.getCount();
                editor.putInt(Constant.ConValue.PLAYING_LIST_INFO_COUNT, total);
                for (int i = 0; i < total; i++) {
                    int pos = 0;
                    if (i < (total - beginPos)) {
                        pos = beginPos + i;
                    } else {
                        pos = i - (total - beginPos);
                    }
                    listCursor.moveToPosition(pos);
                    editor.putInt(Constant.ConValue.PLAYING_LIST_INFO_ID + i,
                            listCursor.getInt(listCursor.getColumnIndexOrThrow(SongLightStore.Playlists.Members._ID)));
                }
            }
            if (listCursor != null) {
                listCursor.close();
            }
        } else if (playType == Constant.ConValue.PLAY_TYPE_SIMPLE_PLAY_LIST) {
            editor.putInt("fileNameId", songLightID);
            /* 记录下列表播放的顺序 */
            Cursor simpleListCursor = querySimplePlayList(context, modeIdx);
            if ((simpleListCursor != null) && (simpleListCursor.getCount() > 0)) {
                int total = simpleListCursor.getCount();
                editor.putInt(Constant.ConValue.PLAYING_LIST_INFO_COUNT, total);
                for (int i = 0; i < total; i++) {
                    simpleListCursor.moveToPosition(i);
                    int id = simpleListCursor
                            .getInt(simpleListCursor
                                    .getColumnIndexOrThrow(SongLightStore.TotalSongLights._ID));
                    if (id == songLightID) {
                        beginPos = i;
                        break;
                    }
                }

                for (int i = 0; i < total; i++) {
                    int pos = 0;
                    if (i < (total - beginPos)) {
                        pos = beginPos + i;
                    } else {
                        pos = i - (total - beginPos);
                    }
                    simpleListCursor.moveToPosition(pos);
                    editor.putInt(
                            Constant.ConValue.PLAYING_LIST_INFO_ID + i,
                            simpleListCursor.getInt(simpleListCursor
                                    .getColumnIndexOrThrow(SongLightStore.TotalSongLights._ID)));
                }
            }
            if (simpleListCursor != null) {
                simpleListCursor.close();
            }
        } else if (playType == Constant.ConValue.PLAY_TYPE_SINGLE_SONG_LIGHT) {
            editor.putInt(Constant.ConValue.PLAYING_LIST_INFO_SUBMODE,
                    subModeIdx);
            editor.putInt(Constant.ConValue.PLAYING_LIST_INFO_FILE_ID,
                    songLightID);
        }

        editor.commit();
    }

    public static void clearCurPlayingListInfo(Context context) {
        KLog.e("clearCurPlayingListInfo-2-1");
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.ConValue.PLAYING_LIST_INFO_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public static Cursor querySimplePlayList(Context context, int modeIdx) {
        Cursor cursor = null;
        int retIndex = 0;
        String[] mSimplePlaylistMemberCols = new String[]{SongLightStore.TotalSongLights._ID};
        ContentResolver resolver = context.getContentResolver();
        String mSortOrder = SongLightStore.TotalSongLights.SUBMODE + ","
                + SongLightStore.TotalSongLights.DEFAULT_SORT_ORDER;
        StringBuilder where = new StringBuilder();

        if (modeIdx == 9) {
            mSortOrder = SongLightStore.TotalSongLights.DEFAULT_SORT_ORDER;
            where.append("favor = 1" + " and "
                    + SongLightStore.TotalSongLights.TYPE + "="
                    + MyUtil.SONG_AND_LIGHT + " and "
                    + SongLightStore.TotalSongLights.FAVOR_SIMPLELIST + "= 1");
        } else {
            where.append("mode = 'mode" + modeIdx + "'" + " and "
                    + SongLightStore.TotalSongLights.TYPE + "="
                    + MyUtil.SONG_AND_LIGHT + " and "
                    + SongLightStore.TotalSongLights.SIMPLELIST + "= 1");
        }

        Uri uri = SongLightStore.TotalSongLights.getContentUri();

        cursor = resolver.query(uri, mSimplePlaylistMemberCols,
                where.toString(), null, mSortOrder);

        return cursor;
    }

    public static Cursor queryPlayList(Context context, int modeIdx, int listId) {
        String[] mPlaylistMemberCols = new String[]{
                SongLightStore.Playlists.Members._ID,
                SongLightStore.Playlists.Members.SONGLIGHT_ID};
        ContentResolver resolver = context.getContentResolver();
        String mSortOrder = SongLightStore.Playlists.Members.PLAY_ORDER;
        StringBuilder where = new StringBuilder();
        where.append("list_id=" + listId);
        where.append(" AND " + SongLightStore.Playlists.Members.NAME + " != ''");
        String tableName = "mode" + modeIdx + "_list_members";
        Uri uri = SongLightStore.Playlists.Members.getContentUri(tableName);

        Cursor cursor = resolver.query(uri, mPlaylistMemberCols,
                where.toString(), null, mSortOrder);
        return cursor;
    }

    /* 判断是否是正在播放当前模式的单个光曲 */
    public static Boolean isPlayingSingleSongLight(Context context, int mode) {
        Boolean flag = false;
        SharedPreferences mySharedPreferences = context
                .getSharedPreferences(Constant.ConValue.PLAYING_LIST_INFO_NAME,
                        Activity.MODE_PRIVATE);
        // SharedPreferences.Editor editor = mySharedPreferences.edit();
        int curMode = mySharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_MODE, 0);
        int curPlayType = mySharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_PLAY_TYPE, 0);
        if ((mode == curMode)
                && (curPlayType == Constant.ConValue.PLAY_TYPE_SINGLE_SONG_LIGHT)) {
            flag = true;
        }
        return flag;
    }

    /* 判断当前简单列表是否是正在播放的 */
    public static Boolean isPlayingSimpleList(Context context, int mode) {
        Boolean flag = false;
        SharedPreferences mySharedPreferences = context
                .getSharedPreferences(Constant.ConValue.PLAYING_LIST_INFO_NAME,
                        Activity.MODE_PRIVATE);
        // SharedPreferences.Editor editor = mySharedPreferences.edit();
        int curMode = mySharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_MODE, 0);
        int curPlayType = mySharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_PLAY_TYPE, 0);
        if ((mode == curMode)
                && (curPlayType == Constant.ConValue.PLAY_TYPE_SIMPLE_PLAY_LIST)) {
            flag = true;
        }
        return flag;
    }

    /* 判断当前列表是否是正在播放的 */
    public static Boolean isPlayingList(Context context, int mode,
                                        int playlistId) {
        Boolean flag = false;
        SharedPreferences mySharedPreferences = context
                .getSharedPreferences(Constant.ConValue.PLAYING_LIST_INFO_NAME,
                        Activity.MODE_PRIVATE);
        // SharedPreferences.Editor editor = mySharedPreferences.edit();
        int curMode = mySharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_MODE, 0);
        int curPlayType = mySharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_PLAY_TYPE, 0);
        int fileListId = mySharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_LIST_ID, 0);
        if ((mode == curMode)
                && (curPlayType == Constant.ConValue.PLAY_TYPE_PLAY_LIST)) {
            if (playlistId == fileListId)
                flag = true;
        }
        return flag;
    }

    /* 判断当前光曲是否是正在播放的 首先 模式 播放类型要一致 然后判断id */
    public static Boolean isPlayingSongLight(Context context, int mode,
                                             int playType, int playlistId, int songLightID) {
        Boolean flag = false;

        LogUtils.d(TAG, "isPlayingSongLight mode:" + mode + " playType:"
                + playType + " playlistId:" + playlistId + " songLightID:"
                + songLightID);

        SharedPreferences mySharedPreferences = context
                .getSharedPreferences(Constant.ConValue.PLAYING_LIST_INFO_NAME,
                        Activity.MODE_PRIVATE);
        // SharedPreferences.Editor editor = mySharedPreferences.edit();
        int curMode = mySharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_MODE, 0);
        int curPlayType = mySharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_PLAY_TYPE, 0);
        int fileListId = mySharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_LIST_ID, 0);
        int nameId = mySharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_FILE_ID, 0);

        LogUtils.d(TAG, "isPlayingSongLight curMode:" + curMode
                + " curPlayType:" + curPlayType + " fileListId:" + fileListId
                + " nameId:" + nameId);

        if ((mode == curMode) && (curPlayType == playType)) {
            if (curPlayType == Constant.ConValue.PLAY_TYPE_PLAY_LIST) {
                if (fileListId == playlistId) {
                    if (nameId == songLightID) {
                        flag = true;
                    }
                }
            } else if (nameId == songLightID) {
                flag = true;
            }
        }

        return flag;

    }

    /* 判断当前光曲是否是正在播放的 播放列表中的光曲 */
    public static boolean isPlayingTotalSongLithtsId(Context context,
                                                     int totalSongLightID) {
        Boolean flag = false;

        LogUtils.d(TAG, "isPlayingTotalSongLithtsId totalSongLightID:"
                + totalSongLightID);

        SharedPreferences mySharedPreferences = context
                .getSharedPreferences(Constant.ConValue.PLAYING_LIST_INFO_NAME,
                        Activity.MODE_PRIVATE);
        // SharedPreferences.Editor editor = mySharedPreferences.edit();
        int curMode = mySharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_MODE, 0);
        int curPlayType = mySharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_PLAY_TYPE, 0);
        int fileListId = mySharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_LIST_ID, 0);
        int nameId = mySharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_FILE_ID, 0);

        LogUtils.d(TAG, "isPlayingTotalSongLithtsId curMode:" + curMode
                + " curPlayType:" + curPlayType + " fileListId:" + fileListId
                + " nameId:" + nameId);

        if (curPlayType == Constant.ConValue.PLAY_TYPE_PLAY_LIST) {
            String[] mPlaylistMemberCols = new String[]{
                    SongLightStore.Playlists.Members._ID,
                    SongLightStore.Playlists.Members.SONGLIGHT_ID};
            ContentResolver resolver = context.getContentResolver();
            String mSortOrder = SongLightStore.Playlists.Members.PLAY_ORDER;
            StringBuilder where = new StringBuilder();
            where.append("list_id=" + fileListId);
            where.append(" AND "
                    + SongLightStore.Playlists.Members.SONGLIGHT_ID + " = "
                    + totalSongLightID);
            String tableName = "mode" + curMode + "_list_members";
            Uri uri = SongLightStore.Playlists.Members.getContentUri(tableName);

            Cursor cursor = resolver.query(uri, mPlaylistMemberCols,
                    where.toString(), null, mSortOrder);
            if ((cursor != null) && (cursor.getCount() > 0)) {
                flag = true;
            }
            if (cursor != null) {
                cursor.close();
            }

        } else if (curPlayType == Constant.ConValue.PLAY_TYPE_SIMPLE_PLAY_LIST) {
            String[] mTotalSongLightsCols = new String[]{SongLightStore.TotalSongLights._ID};
            ContentResolver resolver = context.getContentResolver();
            StringBuilder where = new StringBuilder();
            Uri uri = SongLightStore.TotalSongLights.getContentUri();
            if (curMode == 9)// 在播最爱模式里的
            {
                where.append("_id=" + totalSongLightID);
                where.append(" AND " + SongLightStore.TotalSongLights.FAVOR
                        + " = 1");
                where.append(" AND " + SongLightStore.TotalSongLights.TYPE
                        + "=" + MyUtil.SONG_AND_LIGHT);
                where.append(" AND "
                        + SongLightStore.TotalSongLights.FAVOR_SIMPLELIST
                        + " = 1");
            } else {
                where.append("_id=" + totalSongLightID);
                where.append(" and " + SongLightStore.TotalSongLights.TYPE
                        + "=" + MyUtil.SONG_AND_LIGHT);
                where.append(" AND "
                        + SongLightStore.TotalSongLights.SIMPLELIST + " = 1");
                where.append(" AND " + SongLightStore.TotalSongLights.MODE
                        + " = " + "'mode" + curMode + "'");
            }
            Cursor cursor = resolver.query(uri, mTotalSongLightsCols,
                    where.toString(), null, null);
            if ((cursor != null) && (cursor.getCount() > 0)) {
                flag = true;
            }
            if (cursor != null) {
                cursor.close();
            }
        } else if (nameId == totalSongLightID) {
            flag = true;
        }

        return flag;

    }

    public static void clearAlbumArtCache() {
        synchronized (sArtCache) {
            sArtCache.clear();
        }
    }

    // 后续再实现，显示光曲文件示意图标
    /*
     * public static Drawable getCachedArtwork(Context context, long artIndex,
	 * BitmapDrawable defaultArtwork) { Drawable d = null;
	 * synchronized(sArtCache) { d = sArtCache.get(artIndex); } if (d == null) {
	 * d = defaultArtwork; final Bitmap icon = defaultArtwork.getBitmap(); int w
	 * = icon.getWidth(); int h = icon.getHeight(); // Get art work directly
	 * from file //Bitmap b = MusicUtils.getArtworkQuick(context, artIndex, w,
	 * h); Bitmap b = MusicUtils.getArtwork(context, -1, artIndex, false); if (b
	 * != null) { b = Bitmap.createScaledBitmap(b, w, h, true); d = new
	 * FastBitmapDrawable(b); synchronized(sArtCache) { // the cache may have
	 * changed since we checked Drawable value = sArtCache.get(artIndex); if
	 * (value == null) { sArtCache.put(artIndex, d); } else { d = value; } } } }
	 * return d; }
	 */

    /* 模式编辑界面 当前处于编辑状态 */
    private static Boolean inEditMode = false;

    public static void setEditModeInEdit(Boolean editMode) {
        LogUtils.d(TAG, "setEditModeInEdit editMode:" + editMode);
        inEditMode = editMode;
    }

    public static Boolean getEditModeInEdit() {
        LogUtils.d(TAG, "getEditModeInEdit inEditMode:" + inEditMode);
        return inEditMode;
    }

    /* 模式编辑界面 当前处于列表编辑状态 */
    private static Boolean inListEditMode = false;

    public static void setEditModeInListEdit(Boolean editMode) {
        inListEditMode = editMode;
    }

    public static Boolean getEditModeInListEdit() {
        return inListEditMode;
    }

    private static ContentValues[] sContentValuesCache = null;

    /**
     * @param ids    The source array containing all the ids to be added to the
     *               playlist
     * @param offset Where in the 'ids' array we start reading
     * @param len    How many items to copy during this pass
     * @param base   The play order offset to use for this pass
     */
    private static byte[] makeInsertItems(Context context, List<Integer> ids,
                                          int offset, int len, int base, String listId, String strMode) {
        // adjust 'len' if would extend beyond the end of the source array
        if (offset + len > ids.size()) {
            len = ids.size() - offset;
        }
        byte[] idBuf = new byte[4 * len + 8];
        System.arraycopy(MyUtil.intToByte(Integer.valueOf(strMode)), 0, idBuf,
                0, 4);
        System.arraycopy(MyUtil.intToByte(Integer.valueOf(listId)), 0, idBuf,
                4, 4);
        // allocate the ContentValues array, or reallocate if it is the wrong
        // size
        if (sContentValuesCache == null || sContentValuesCache.length != len) {
            sContentValuesCache = new ContentValues[len];
        }
        // fill in the ContentValues array with the right values for this pass
        ContentResolver resolver = context.getContentResolver();
        Uri uri = SongLightStore.TotalSongLights.getContentUri();
        String[] songLightCols = new String[]{
                SongLightStore.Playlists.Members.NAME,
                SongLightStore.Playlists.Members.MODE,
                SongLightStore.Playlists.Members.SUBMODE,
                SongLightStore.Playlists.Members.SIZE,
                SongLightStore.Playlists.Members.DURATION,
                SongLightStore.Playlists.Members.CHECKCODE,
                SongLightStore.Playlists.Members.TYPE};
        Cursor cur = null;
        for (int i = 0; i < len; i++) {
            System.arraycopy(MyUtil.intToByte(ids.get(offset + i)), 0, idBuf,
                    8 + i * 4, 4);
            if (sContentValuesCache[i] == null) {
                sContentValuesCache[i] = new ContentValues();
            }
            String whereclause = SongLightStore.TotalSongLights._ID + " = '"
                    + ids.get(offset + i) + "'";
            cur = resolver.query(uri, songLightCols, whereclause, null, null);
            cur.moveToFirst();
            String name = cur
                    .getString(cur
                            .getColumnIndexOrThrow(SongLightStore.TotalSongLights.NAME));
            String mode = cur
                    .getString(cur
                            .getColumnIndexOrThrow(SongLightStore.TotalSongLights.MODE));
            String submode = cur
                    .getString(cur
                            .getColumnIndexOrThrow(SongLightStore.TotalSongLights.SUBMODE));
            int size = cur
                    .getInt(cur
                            .getColumnIndexOrThrow(SongLightStore.TotalSongLights.SIZE));
            int duration = cur
                    .getInt(cur
                            .getColumnIndexOrThrow(SongLightStore.TotalSongLights.DURATION));
            byte[] checkcode = cur
                    .getBlob(cur
                            .getColumnIndexOrThrow(SongLightStore.TotalSongLights.CHECKCODE));
            int type = cur
                    .getInt(cur
                            .getColumnIndexOrThrow(SongLightStore.TotalSongLights.TYPE));

            sContentValuesCache[i].put(
                    SongLightStore.Playlists.Members.LIST_ID,
                    Integer.valueOf(listId));
            sContentValuesCache[i].put(
                    SongLightStore.Playlists.Members.PLAY_ORDER, base + offset
                            + i);
            sContentValuesCache[i].put(
                    SongLightStore.Playlists.Members.SONGLIGHT_ID,
                    ids.get(offset + i));
            sContentValuesCache[i].put(SongLightStore.Playlists.Members.NAME,
                    name);
            sContentValuesCache[i].put(SongLightStore.Playlists.Members.MODE,
                    mode);
            sContentValuesCache[i].put(
                    SongLightStore.Playlists.Members.SUBMODE, submode);
            sContentValuesCache[i].put(SongLightStore.Playlists.Members.SIZE,
                    size);
            sContentValuesCache[i].put(
                    SongLightStore.Playlists.Members.DURATION, duration);
            sContentValuesCache[i].put(
                    SongLightStore.Playlists.Members.CHECKCODE, checkcode);
            sContentValuesCache[i].put(SongLightStore.Playlists.Members.TYPE,
                    type);
            if (MyUtil.isSongAndLight(type)) {
                sContentValuesCache[i].put(
                        SongLightStore.Playlists.Members.TIMES, 1);
            } else {
                sContentValuesCache[i].put(
                        SongLightStore.Playlists.Members.TIMES, 0);
            }
        }

        if (cur != null) {
            LogUtils.d(TAG, "makeInsertItems cur" + cur);
            cur.close();
        }
        return idBuf;
    }

    /* 加入到最爱的简单列表 */
    public static void addToFavorSimpleList(Context context, int songlight_id,
                                            boolean enable) {
        if (isPlayingSimpleList(context, 9)) {
            stopCurPlaying();
        }

        ContentResolver resolver = context.getContentResolver();

		/* 先查询是否存在 */
        String[] cols = new String[]{"count(*)"};
        Cursor cursor = resolver.query(ContentUris.withAppendedId(
                SongLightStore.TotalSongLights.getContentUri(), songlight_id),
                cols, null, null, null);
        cursor.moveToFirst();
        int num = cursor.getInt(0);
        if (cursor != null) {
            cursor.close();
        }
        if (num == 0) {
            return;
        }

		/* 更新 */
        ContentValues values = new ContentValues(1);
        values.put(SongLightStore.TotalSongLights.FAVOR_SIMPLELIST, enable ? 1
                : 0);
        resolver.update(ContentUris.withAppendedId(
                SongLightStore.TotalSongLights.getContentUri(), songlight_id),
                values, null, null);

		/* 发送远程命令 */
        byte[] buff = new byte[5];
        byte[] idBuf = MyUtil.intToByte(songlight_id);
        System.arraycopy(idBuf, 0, buff, 0, 4);
        buff[4] = enable ? (byte) 0x01 : (byte) 0x00;
        // sendRemoteControlFrame(MyConstant.CMD84, MyConstant.CMD_TYPE3, buff);
    }

    /* 加入到简单列表 */
    public static void addToSimpleList(Context context, int mode,
                                       int songlight_id, boolean enable) {
        if (isPlayingSimpleList(context, mode)) {
            stopCurPlaying();
        }

        ContentResolver resolver = context.getContentResolver();

		/* 先查询是否存在 */
        String[] cols = new String[]{"count(*)"};
        Cursor cursor = resolver.query(ContentUris.withAppendedId(
                SongLightStore.TotalSongLights.getContentUri(), songlight_id),
                cols, null, null, null);
        cursor.moveToFirst();
        int num = cursor.getInt(0);
        if (cursor != null) {
            cursor.close();
        }
        if (num == 0) {
            return;
        }

		/* 更新 */
        ContentValues values = new ContentValues(1);
        values.put(SongLightStore.TotalSongLights.SIMPLELIST, enable ? 1 : 0);
        resolver.update(ContentUris.withAppendedId(
                SongLightStore.TotalSongLights.getContentUri(), songlight_id),
                values, null, null);

		/* 发送远程命令 */
        byte[] buff = new byte[9];
        byte[] modeBuf = MyUtil.intToByte(mode);
        System.arraycopy(modeBuf, 0, buff, 0, 4);
        byte[] idBuf = MyUtil.intToByte(songlight_id);
        System.arraycopy(idBuf, 0, buff, 4, 4);
        buff[8] = enable ? (byte) 0x01 : (byte) 0x00;
        // sendRemoteControlFrame(MyConstant.CMD84, MyConstant.CMD_TYPE4, buff);
    }

    /* 加入到最爱 */
    public static void addToFavorMode(Context context, int songlight_id,
                                      boolean enable) {
        ContentResolver resolver = context.getContentResolver();

		/* 先查询是否存在 */
        String[] cols = new String[]{"count(*)"};
        Cursor cursor = resolver.query(ContentUris.withAppendedId(
                SongLightStore.TotalSongLights.getContentUri(), songlight_id),
                cols, null, null, null);
        cursor.moveToFirst();
        int num = cursor.getInt(0);
        if (cursor != null) {
            cursor.close();
        }
        if (num == 0) {
            return;
        }

		/* 更新 */
        ContentValues values = new ContentValues(1);
        values.put(SongLightStore.TotalSongLights.FAVOR, enable ? 1 : 0);
        resolver.update(ContentUris.withAppendedId(
                SongLightStore.TotalSongLights.getContentUri(), songlight_id),
                values, null, null);
    }

    public static void addToPlaylist(Context context, List<Integer> ids,
                                     String strMode, String listId) {
        if (ids == null) {
            // this shouldn't happen (the menuitems shouldn't be visible
            // unless the selected item represents something playable
            LogUtils.e(TAG, "ListSelection null");
        } else {
            int size = ids.size();
            ContentResolver resolver = context.getContentResolver();
            // need to determine the number of items currently in the playlist,
            // so the play_order field can be maintained.
            StringBuilder where = new StringBuilder();
            where.append("list_id=" + listId);
            String[] cols = new String[]{SongLightStore.Playlists.Members.PLAY_ORDER};
            Uri uri = SongLightStore.Playlists.Members.getContentUri("mode"
                    + strMode + "_list_members");
            Cursor cur = resolver.query(uri, cols, where.toString(), null,
                    SongLightStore.Playlists.Members.PLAY_ORDER);
            cur.moveToLast();
            int base = 0;
            if (cur.getCount() > 0) {
                base = cur.getInt(0) + 1;
            }
            cur.close();
            int numinserted = 0;
            for (int i = 0; i < size; i += 1000) {
                byte[] idBuf = makeInsertItems(context, ids, i, 1000, base,
                        listId, strMode);
                numinserted += resolver.bulkInsert(uri, sContentValuesCache);
                // sendRemoteControlFrame(MyConstant.CMD84,
                // MyConstant.CMD_TYPE7,
                // idBuf);
            }
            LogUtils.d(TAG, "addToPlaylist numinserted" + numinserted);
            // mLastPlaylistSelected = playlistid;
        }
    }

    public static void updateNowPlaying(Activity a) {
        // TODO Auto-generated method stub

    }

    public static void deleteAllFromPlaylist(Context context,
                                             String playlistName, String playListId) {

        LogUtils.d(TAG, ">> deleteAllFromPlaylist");
        String[] cols = new String[]{SongLightStore.Playlists.Members._ID};
        StringBuilder where = new StringBuilder();
        Uri uri = SongLightStore.Playlists.Members.getContentUri(playlistName);
        where.append("list_id=" + playListId);

        Cursor c = query(context, uri, cols, where.toString(), null, null);

        if (c != null) {

            try {
                context.getContentResolver()
                        .delete(uri, where.toString(), null);
            } catch (Exception e) {
                LogUtils.d(TAG, "deleteAllFromPlaylist in Exception");
                return;
            }

            c.close();
        }

        // We deleted a number of tracks, which could affect any number of
        // things
        // in the media content domain, so update everything.
        context.getContentResolver().notifyChange(uri, null);
        LogUtils.d(TAG, "<< deleteAllFromPlaylist");

    }

    public static void deleteAllFromNotifications(Context context) {
        LogUtils.d(TAG, ">> deleteAllFromPlaylist");
        String[] cols = new String[]{SongLightStore.Playlists.Members._ID};
        StringBuilder where = new StringBuilder();
        Uri uri = SongLightStore.Notifications.getContentUri();
        // where.append( "list_id=" +playListId);

        Cursor c = query(context, uri, cols, where.toString(), null, null);

        if (c != null) {
            c.close();
            try {
                context.getContentResolver()
                        .delete(uri, where.toString(), null);
            } catch (Exception e) {
                LogUtils.d(TAG, "deleteAllFromPlaylist in Exception");
                return;
            }
        }

        // We deleted a number of tracks, which could affect any number of
        // things
        // in the media content domain, so update everything.
        context.getContentResolver().notifyChange(uri, null);
        LogUtils.d(TAG, "<< deleteAllFromNotifications");

    }

    public static void deleteFromNotifications(Context context, int id) {
        LogUtils.d(TAG, ">> deleteAllFromPlaylist");
        String[] cols = new String[]{SongLightStore.Notifications._ID};
        StringBuilder where = new StringBuilder();
        Uri uri = SongLightStore.Notifications.getContentUri();
        where.append(SongLightStore.Notifications._ID + "=" + id);

        Cursor c = query(context, uri, cols, where.toString(), null, null);

        if (c != null) {
            c.close();
            try {
                context.getContentResolver()
                        .delete(uri, where.toString(), null);
            } catch (Exception e) {
                LogUtils.d(TAG, "deleteFromNotifications in Exception");
                return;
            }
        }

        // We deleted a number of tracks, which could affect any number of
        // things
        // in the media content domain, so update everything.
        context.getContentResolver().notifyChange(uri, null);
        LogUtils.d(TAG, "<< deleteFromNotifications");

    }

    /* 批量从最爱模式里移除 */
    public static void removeFromFavorMode(Context context, List<Integer> list) {
        LogUtils.d(TAG, ">> removeFromFavorMode");
        String[] cols = new String[]{SongLightStore.Playlists.Members._ID};
        StringBuilder where = new StringBuilder();
        Uri uri = SongLightStore.TotalSongLights.getContentUri();
        // where.append( "list_id=" +playListId);
        where.append(SongLightStore.Playlists.Members._ID + " IN (");
        for (int i = 0; i < list.size(); i++) {
            where.append(list.get(i));
            if (i < list.size() - 1) {
                where.append(",");
            }
        }
        where.append(")");

        Cursor c = query(context, uri, cols, where.toString(), null, null);

        if (c != null) {

            try {
                /* 更新 */
                ContentValues values = new ContentValues(1);
                values.put(SongLightStore.TotalSongLights.FAVOR, 0);
                context.getContentResolver().update(
                        SongLightStore.TotalSongLights.getContentUri(), values,
                        where.toString(), null);
            } catch (Exception e) {
                LogUtils.d(TAG, "deleteTracks in Exception");
            }

            c.close();
        }

        // We deleted a number of tracks, which could affect any number of
        // things
        // in the media content domain, so update everything.
        context.getContentResolver().notifyChange(uri, null);
        LogUtils.d(TAG, "<< deleteTracks");
    }

    /* 从光曲列表中删除多个光曲 */
    public static void deleteFromPlaylist(Context context, List<Long> list,
                                          String playlistName, String playListId) {
        LogUtils.d(TAG, ">> deleteTracks");
        String[] cols = new String[]{SongLightStore.Playlists.Members._ID};
        StringBuilder where = new StringBuilder();
        Uri uri = SongLightStore.Playlists.Members.getContentUri(playlistName);
        where.append("list_id=" + playListId);
        where.append(" AND " + SongLightStore.Playlists.Members._ID + " IN (");
        for (int i = 0; i < list.size(); i++) {
            where.append(list.get(i));
            if (i < list.size() - 1) {
                where.append(",");
            }
        }
        where.append(")");

        Cursor c = query(context, uri, cols, where.toString(), null, null);

        if (c != null) {

            try {
                context.getContentResolver()
                        .delete(uri, where.toString(), null);
            } catch (Exception e) {
                LogUtils.d(TAG, "deleteTracks in Exception");
                return;
            }

            c.close();
        }

        // We deleted a number of tracks, which could affect any number of
        // things
        // in the media content domain, so update everything.
        context.getContentResolver().notifyChange(uri, null);
        LogUtils.d(TAG, "<< deleteTracks");
    }

    /* 从光曲列表中删除单个光曲 */
    public static void deleteFromPlaylist(Context context, int id, String mode,
                                          String playListId) {
        LogUtils.d(TAG, ">> deleteTracks");
        String[] cols = new String[]{SongLightStore.Playlists.Members._ID};
        StringBuilder where = new StringBuilder();
        Uri uri = SongLightStore.Playlists.Members.getContentUri("mode" + mode
                + "_list_members");
        where.append("list_id=" + playListId);
        where.append(" AND " + SongLightStore.Playlists.Members._ID + " = "
                + id);

        Cursor c = query(context, uri, cols, where.toString(), null, null);

        if (c != null) {

            try {
                context.getContentResolver()
                        .delete(uri, where.toString(), null);
            } catch (Exception e) {
                LogUtils.d(TAG, "deleteTracks in Exception");
                return;
            }

            c.close();
        }

        byte[] buff = new byte[12];
        System.arraycopy(MyUtil.intToByte(Integer.valueOf(mode)), 0, buff, 0, 4);
        System.arraycopy(MyUtil.intToByte(Integer.valueOf(playListId)), 0,
                buff, 4, 4);
        System.arraycopy(MyUtil.intToByte(id), 0, buff, 8, 4);
        // sendRemoteControlFrame(MyConstant.CMD84, MyConstant.CMD_TYPE8, buff);

        LogUtils.d(TAG, "<< deleteTracks");
    }

    /* 从光曲整表中删除单个光曲 */
    public static void deleteFromTotalSonglightsTable(Context context, int id) {
        LogUtils.d(TAG, ">> deleteFromTotalSonglightsTable");

        if (isPlayingTotalSongLithtsId(context, id)) {
            stopCurPlaying();
        }

        String[] cols = new String[]{SongLightStore.TotalSongLights._ID,
                SongLightStore.TotalSongLights.CHECKCODE,
                SongLightStore.TotalSongLights.DOWNLOAD_ID};
        byte[] md5;
        long download_id = -1;

        StringBuilder where = new StringBuilder();
        Uri uri = SongLightStore.TotalSongLights.getContentUri();
        where.append(SongLightStore.TotalSongLights._ID + "=" + id);

        Cursor c = query(context, uri, cols, where.toString(), null, null);

        if (c != null) {

            try {
                c.moveToFirst();
                md5 = c.getBlob(c
                        .getColumnIndexOrThrow(SongLightStore.TotalSongLights.CHECKCODE));
                download_id = c
                        .getLong(c
                                .getColumnIndexOrThrow(SongLightStore.TotalSongLights.DOWNLOAD_ID));
                context.getContentResolver()
                        .delete(uri, where.toString(), null);
            } catch (Exception e) {
                c.close();
                LogUtils.d(TAG,
                        "deleteFromTotalSonglightsTable in Exception");
                return;
            }

            c.close();
        }

        context.getContentResolver().notifyChange(uri, null);

		/* 发送远程命令 */
        // byte[] idBuf = MyUtil.intToByte(id);
        // sendRemoteControlFrame(MyConstant.CMD84, MyConstant.CMD_TYPE2,
        // idBuf);
        LogUtils.d(TAG, "<< deleteFromTotalSonglightsTable finish");
    }

    /* 从光曲整表中批量删除光曲 */
    public static void deleteFromTotalSonglightsTable(Context context,
                                                      List<Integer> list) {
        LogUtils.d(TAG, ">> deleteFromTotalSonglightsTable");
        String[] cols = new String[]{SongLightStore.TotalSongLights._ID};
        StringBuilder where = new StringBuilder();
        Uri uri = SongLightStore.TotalSongLights.getContentUri();
        where.append(SongLightStore.Playlists.Members._ID + " IN (");
        for (int i = 0; i < list.size(); i++) {
            where.append(list.get(i));
            if (i < list.size() - 1) {
                where.append(",");
            }
        }
        where.append(")");

        Cursor c = query(context, uri, cols, where.toString(), null, null);

        if (c != null) {

            try {
                context.getContentResolver()
                        .delete(uri, where.toString(), null);
            } catch (Exception e) {
                LogUtils.d(TAG,
                        "deleteFromTotalSonglightsTable in Exception");
                return;
            }

            c.close();
        }

        // We deleted a number of tracks, which could affect any number of
        // things
        // in the media content domain, so update everything.
        context.getContentResolver().notifyChange(uri, null);
        LogUtils.d(TAG, "<< deleteFromTotalSonglightsTable finish");
    }

    /* 删除指定的光曲列表 */
    public static void deleteSongLightList(Context context, String modeName,
                                           String listId) {
        ContentResolver resolver = context.getContentResolver();
        String where = SongLightStore.Playlists._ID + " = ?";
        String[] whereargs = {listId};
        Uri base = SongLightStore.Playlists.getContentUri(modeName);
        try {
            resolver.delete(base, where, whereargs);
            // byte[] buff = new byte[8];
            // System.arraycopy(MyUtil.intToByte(Integer.valueOf(modeName)), 0,
            // buff, 0, 4);
            // System.arraycopy(MyUtil.intToByte(Integer.valueOf(listId)), 0,
            // buff, 4, 4);
            // sendRemoteControlFrame(MyConstant.CMD84, MyConstant.CMD_TYPE6,
            // buff);
        } catch (Exception e) {
            LogUtils.d(TAG, "deleteSongLightList in Exception");
            return;
        }
    }

    /* 系统初始化提示框 */
    private static boolean mExternalStorageState = false;

    public static boolean getExternalStorageState() {
        LogUtils.d(TAG, "getExternalStorageState :"
                + mExternalStorageState);
        return mExternalStorageState;
    }

    public static void setExternalStorageState(boolean flag, Context context) {
        LogUtils.i(TAG, "setExternalStorageState flag" + flag);
        mExternalStorageState = flag;
        if (mExternalStorageState) {
            if ((mInitProgressDialog != null)
                    && (mInitProgressDialog.isShowing())) {
                mInitProgressDialog.dismiss();
            }
            if (context != null) {
                Intent completeIntent = new Intent();
                completeIntent.setAction(Constant.ACTION_DATEBASE_INIT_OK);
                context.sendBroadcast(completeIntent);
            }
        }
    }

    public static ProgressDialog mInitProgressDialog = null;

    public static ProgressDialog createInitProgressDlg(Activity context) {
        LogUtils.d(TAG, "createInitProgressDlg :" + context);
        if (mInitProgressDialog == null) {
            LogUtils.d(TAG, "createInitProgressDlg2 :" + context);
            mInitProgressDialog = new ProgressDialog(context);
            mInitProgressDialog.setMessage(context
                    .getString(R.string.system_initializing));
            mInitProgressDialog.setCancelable(false);
            mInitProgressDialog.setIndeterminate(true);
            // mProgressDialog.setCancelMessage(mHandler.obtainMessage(PRESS_BACK));
        }
        return mInitProgressDialog;
    }

    public static void showInitProgress(Activity context) {
        if (mInitProgressDialog == null) {
            mInitProgressDialog = createInitProgressDlg(context);
        }
        mInitProgressDialog.show();
    }

    /*
     * Try to use String.format() as little as possible, because it creates a
     * new Formatter every time you call it, which is very inefficient. Reusing
     * an existing Formatter more than tripled the speed of makeTimeString().
     * This Formatter/StringBuilder are also used by makeAlbumSongsLabel()
     */
    private static StringBuilder sFormatBuilder = new StringBuilder();
    private static Formatter sFormatter = new Formatter(sFormatBuilder,
            Locale.getDefault());
    private static final Object[] sTimeArgs = new Object[5];

    public static String makeTimeString(Context context, long secs) {
        String durationformat = context
                .getString(secs < 3600 ? R.string.durationformatshort
                        : R.string.durationformatlong);

		/*
         * Provide multiple arguments so the format can be changed easily by
		 * modifying the xml.
		 */
        sFormatBuilder.setLength(0);

        final Object[] timeArgs = sTimeArgs;
        timeArgs[0] = secs / 3600;
        timeArgs[1] = secs / 60;
        timeArgs[2] = (secs / 60) % 60;
        timeArgs[3] = secs;
        timeArgs[4] = secs % 60;

        return sFormatter.format(Locale.getDefault(), durationformat, timeArgs)
                .toString();
    }

//    public static Drawable getModeIcon(Context context, int idx) {
//        Drawable drawable = null;
//        switch ((int) idx) {
//            case 1:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_dairty);
//                break;
//            case 2:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_sleep);
//                break;
//            case 3:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_read);
//                break;
//            case 4:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_education);
//                break;
//            case 5:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_party);
//                break;
//            case 6:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_love);
//                break;
//            case 7:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_music);
//                break;
//            case 8:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_kids);
//                break;
//            case 9:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_farivoty);
//                break;
//        }
//        return drawable;
//    }
//
//    public static Drawable getListIcon(Context context, int idx) {
//        Drawable drawable = null;
//        switch ((int) idx) {
//            case 1:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_dairty);
//                break;
//            case 2:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_sleep);
//                break;
//            case 3:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_read);
//                break;
//            case 4:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_education);
//                break;
//            case 5:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_party);
//                break;
//            case 6:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_love);
//                break;
//            case 7:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_music);
//                break;
//            case 8:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_kids);
//                break;
//            case 9:
//                drawable = context.getResources()
//                        .getDrawable(R.mipmap.icon_main_farivoty);
//                break;
//        }
//        return drawable;
//    }
//
//    public static Bitmap getZipWallpaper(Context context, int imageResId,
//                                         int reqWidth, int reqHeight) {

    // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
//        Bitmap bmp, bmp2 = null;
//        try {
//            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeResource(context.getResources(), imageResId,
//                    options);
//            // 调用上面定义的方法计算inSampleSize值
//            options.inSampleSize = calculateInSampleSize(options, reqWidth,
//                    reqHeight);
//            // 使用获取到的inSampleSize值再次解析图片
//            options.inJustDecodeBounds = false;
//            bmp = BitmapFactory.decodeResource(context.getResources(),
//                    imageResId, options);
//            bmp2 = Bitmap.createScaledBitmap(bmp, (int) reqWidth,
//                    (int) reqHeight, true);
//            bmp.recycle();
//
//            return bmp2;
//        } finally {
//            System.gc();
//        }
//    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /* 设置新光曲标志 */
    public static void setNewLightSongFlag(Context context, int songlight_id,
                                           boolean enable) {
        ContentResolver resolver = context.getContentResolver();

		/* 先查询是否存在 */
        String[] cols = new String[]{SongLightStore.TotalSongLights.NEW_FLAG};
        Cursor cursor = resolver.query(ContentUris.withAppendedId(
                SongLightStore.TotalSongLights.getContentUri(), songlight_id),
                cols, null, null, null);
        cursor.moveToFirst();
        int num = cursor.getCount();
        boolean oldFlag = false;
        if (num > 0) {
            oldFlag = (cursor.getInt(cursor.getColumnIndexOrThrow(SongLightStore.TotalSongLights.NEW_FLAG)) == 1);
        }
        if (cursor != null) {
            cursor.close();
        }
        if (num == 0) {
            return;
        }
        if (oldFlag == enable) {
            return;
        }

		/* 更新 */
        ContentValues values = new ContentValues(1);
        values.put(SongLightStore.TotalSongLights.NEW_FLAG, enable ? 1 : 0);
        resolver.update(ContentUris.withAppendedId(
                SongLightStore.TotalSongLights.getContentUri(), songlight_id),
                values, null, null);

        // byte[] buff = new byte[5];
        // System.arraycopy(MyUtil.intToByte(Integer.valueOf(songlight_id)), 0,
        // buff, 0, 4);
        // buff[4] = enable ? (byte) 0x01 : (byte) 0x00;
        // sendRemoteControlFrame(MyConstant.CMD84, MyConstant.CMD_TYPE14,
        // buff);

    }

    public static void saveLightBaseInfo(Context context, int light) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(
                Constant.ConValue.BASE_INFO_FILENAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt(Constant.ConValue.BASE_INFO_LIGHT, light);
        editor.commit();
    }

    public static void saveVolumeBaseInfo(Context context, int volume) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(
                Constant.ConValue.BASE_INFO_FILENAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt(Constant.ConValue.BASE_INFO_VOLUME, volume);
        editor.commit();
    }

    public static void saveIgooBaseInfo(Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(
                Constant.ConValue.BASE_INFO_FILENAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        // 系统版本号
        String sysVersion = Build.DISPLAY;
        editor.putString(Constant.ConValue.BASE_INFO_SYS_VER, sysVersion);
        // 主应用版本号
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    Constant.ConValue.MAIN_PACKAGE_NAME, 0).versionName;
        } catch (NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        editor.putString(Constant.ConValue.BASE_INFO_APP_VER, verName);
        editor.commit();
    }

    private static Boolean isAlarmShow = false;

    public static Boolean isAlarmShow() {
        return isAlarmShow;
    }

    public static void setAlarmShow(Boolean flag) {
        isAlarmShow = flag;
    }


    /* 设置灯光整体亮度 */
    public static boolean setWholeLightness(int num) {

        if (!getRemoteConnectState().equals("Success")) {
            return false;
        }

        if (sService != null) {
            sService.setWholeLightness(num);
            return true;
        }
        return false;
    }

    public static boolean setWholeLight(int num, String sn) {

        if (!getRemoteConnectState().equals("Success")) {
            return false;
        }

        if (sService != null) {
            sService.setWholeLight(num, sn);
            return true;
        }
        return false;
    }


    // 请求闹钟列表
    public static boolean requestAlarmClock() {
        if (!getRemoteConnectState().equals("Success")) {
            return false;
        }

        if (sService != null) {
            //  sService.requestAlarmClock();
            return true;
        }
        return false;
    }

    public static boolean setWholeLightnesssssss(int num, String sn) {

        if (!getRemoteConnectState().equals("Success")) {
            return false;
        }

        if (sService != null) {
            sService.setWholeLight(num, sn);
            return true;
        }
        return false;
    }

    //设置闹钟列表
//    public static boolean setAlarmClock(List<TimeBean> timeSetBeen) {
//        if (!getRemoteConnectState().equals("Success")) {
//            return false;
//        }
//
//        if (sService != null) {
//            sService.setAlarmClock(timeSetBeen);
//            return true;
//        }
//        return false;
//    }


    public static boolean startTcpClient(List<MyDeviceBean> data,
                                         boolean needConnect) {
        LogUtils.e(TAG, "开始进入" + sService);
        if (sService != null) {
            sService.bStartTcpClient(data, needConnect);
            return true;
        }
        return false;
    }

    public static boolean startTcpClients(String sn, String ip,
                                          boolean needConnect) {
        LogUtils.e(TAG, "开始进入" + sService);
        if (sService != null) {
            sService.bStartTcpClients(sn, ip, needConnect);
            return true;
        }
        return false;
    }

    public static void startGetCurPlaying() {

    }

    public static void stopGetCurPlaying() {

    }

    public static boolean sendLightDataTime(byte[] data, int time, String name) {
        if (!getRemoteConnectState().equals("Success")) {
            return false;
        }
        if (sService != null) {
            try {
                sService.sendMusic(data, time, name);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    // 原始编号 1起居 2睡眠 3就餐 4教育 5聚会 6爱情 7音乐 8育儿 9最爱 该编号
    // 后续改变版本在这个地方
    public static int getCurrentVersion() {
        return 6;
    }

    public static TcpClientService.TcpClientServiceBinder getService() {
        return sService;
    }

    //--------------落地灯wifi版命令开始配置------------

    public static boolean sendFrame(byte bCmd1, byte bCmd2, byte[] data) {
        // TODO Auto-generated method stub
        String result = getRemoteConnectState();
        KLog.e(result + sService);
        if (!result.equals("Success")) {
            return false;
        }
        if (sService != null) {
            try {
                sService.sendFrame(bCmd1, bCmd2, data);
            } catch (RemoteException e) {
                KLog.e(e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public static boolean sendFrameOff(byte[] cmd, byte[] data, String sn) {
        // TODO Auto-generated method stub
        String result = getRemoteConnectState();
        if (!result.equals("Success")) {
            return false;
        }
        if (sService != null) {
            try {
                sService.sendOffCmd(cmd, data, sn);
            } catch (RemoteException e) {
                KLog.e(e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public static boolean sendLightFilesListToDeviceDelay(final String filesListName) {
//        Display(filesListName);
//        // btService.SendMp3Init(fileName);
//        if (!getBtConnectState().equals("Success")) {
//            // showProgress();
//            if (getBtConnectState().equals("Fail")) {
//                startBtService();
//            }
//            return false;
//        }
//
//        if (sService != null) {
//            // MainActivity.nProgressLight = 100;
//            sService.setWholeLightness(MainActivity.nProgressLight);
//            sService.setVolumeness(MainActivity.nProgressVolume);
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    sService.sendFilesListToDevice(filesListName);
//                }
//            }, 700);
//            return true;
//        }
        return false;
    }

    public static boolean sendLightFilesListToDeviceDelay(final String filesListName,
                                                          final int beginIndex) {
//        Display(filesListName);
//        // btService.SendMp3Init(fileName);
//        if (!getBtConnectState().equals("Success")) {
//            // showProgress();
//            if (getBtConnectState().equals("Fail")) {
//                startBtService();
//            }
//            return false;
//        }
//
//        if (sService != null) {
//            // MainActivity.nProgressLight = 100;
//            sService.setWholeLightness(MainActivity.nProgressLight);
//            sService.setVolumeness(MainActivity.nProgressVolume);
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    sService.sendFilesListToDevice(filesListName, beginIndex);
//                }
//            }, 700);
//            return true;
//        }
        return false;
    }

    // 发送颜色调节指令
    public static boolean sendLightCtrl(int nProgressRed, int nProgressGreen,
                                        int nProgressBlue, int nProgressWhite) {
        if (sService != null) {
            try {
                //该方法在主项目的AIDL通信接口中实现也就是TcpClientService中
                sService.sendLightCtrl(nProgressRed, nProgressGreen,
                        nProgressBlue, nProgressWhite);
                return true;
            } catch (RemoteException e) {

            }
        }
        return false;
    }

    public static boolean pauseBabyCurPlaying() {
        if (!getRemoteConnectState().equals("Success")) {
            return false;
        }
        boolean flag = false;
        if (sService != null) {
            sService.pauseBabyCurPlaying();
            flag = true;
        }
        if (flag) {
            setPauseFlag(true);
        }
        return flag;
    }

    public static boolean playBabyCurPause() {
        KLog.e("进入重新播放方法===========playBabyCurPause");
        if (!getRemoteConnectState().equals("Success")) {
            return false;
        }
        boolean flag = false;
        if (sService != null) {
            sService.playBabyCurPause();
            flag = true;
        }
        if (flag) {
            setPauseFlag(false);
        }
        return flag;
    }

    // 发送颜色调节指令
    public static boolean sendLightCtr(int nProgressRed, int nProgressGreen,
                                       int nProgressBlue, int nProgressWhite, String sn) {
        if (sService != null) {
            try {
                //该方法在主项目的AIDL通信接口中实现也就是TcpClientService中
                KLog.a("发送颜色调节指令");
                sService.sendLightC(nProgressRed, nProgressGreen,
                        nProgressBlue, nProgressWhite, sn);
                return true;
            } catch (RemoteException e) {

            }
        }
        return false;
    }

    public static boolean sendLightCt(int num, int nProgressRed, int nProgressGreen,
                                      int nProgressBlue, int nProgressWhite, String sn) {
        if (sService != null) {
            try {
                //该方法在主项目的AIDL通信接口中实现也就是TcpClientService中
                KLog.a("发送颜色调节指令");
                sService.sendLight(num, nProgressRed, nProgressGreen,
                        nProgressBlue, nProgressWhite, sn);
                return true;
            } catch (RemoteException e) {
            }
        }
        return false;
    }

    // 播放光曲
    public static void playMusic(String path, String name, int flag, int position) {
        sService.playMusicSendLight(path, name, flag, position);
    }

    // 暂停光曲
    public static void pauseMusics() {
        sService.pauseMusic();
    }

    // 重新播放光曲
    public static void resumeMp3Data() {
        sService.resumeMp3Data();
    }

    // 停止光曲
    public static void stopMp3Data() {
        sService.stopMp3();
    }

    public static void musicStartSendLight() {

    }
}
