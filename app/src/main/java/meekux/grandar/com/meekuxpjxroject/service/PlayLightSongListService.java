package meekux.grandar.com.meekuxpjxroject.service;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.socks.library.KLog;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MusicListSong;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import meekux.grandar.com.meekuxpjxroject.GlobalApplication;
import meekux.grandar.com.meekuxpjxroject.entity.SearchBean;
import meekux.grandar.com.meekuxpjxroject.songdata.database.Constant;
import meekux.grandar.com.meekuxpjxroject.songdata.database.FileHelper;
import meekux.grandar.com.meekuxpjxroject.songdata.database.GrandarUtils;
import meekux.grandar.com.meekuxpjxroject.songdata.database.MyConstant;
import meekux.grandar.com.meekuxpjxroject.songdata.database.MyUtil;
import meekux.grandar.com.meekuxpjxroject.songdata.database.SongLightFrameException;
import meekux.grandar.com.meekuxpjxroject.songdata.database.SongLightStore;
import meekux.grandar.com.meekuxpjxroject.utils.LogUtils;
import meekux.grandar.com.meekuxpjxroject.utils.SharedPutils;
import meekuxpjxroject.AIDLPlayLightSongListService;


public class PlayLightSongListService extends Service implements MediaPlayer.OnCompletionListener {
    public static String EXPORTFILEPATHx = Environment.getExternalStorageDirectory() + "/NEW_MEEKUX/Candle/";
    private static final String TAG = "PlayLightSongListService";
    private PlayLightSongListThread mPlayLightSongListThread = null;
    int Mp3frameCount = 0;// 光曲文件的总帧数
    int Mp3sendCount = 0; // 当前发送的帧数统计
    /* 是否是循环模式 */
    private boolean inLoopMode = true;
    Cursor filesListCursor = null;
    boolean needPauseCurPlaying = false; // 需要暂停播放
    boolean isPausePlaying = false;
    // 有音乐的开始发送光效帧
    FileInputStream isLight = null;
    FileInputStream isLight2 = null;
    FileInputStream isLight3 = null;
    ArrayList<byte[]> datass = new ArrayList();
    ArrayList<String> string = new ArrayList<>();
    byte[] lightBuf = new byte[128];
    byte[] lightBuf1 = new byte[128];
    byte[] lightBuf2 = new byte[128];
    ArrayList<String> stringss = GlobalApplication.getInstance().getStringss();
    /* 播放音乐 发送光效文件相关 */
    /* 播放音频数据类 */
    private final Object mPlaySongLightLock = new Object(); // 播放光曲相关参数同步锁
    private final int MP3_HEAD_LEN = 292; //MP3文件头字节长度
    private byte[] Mp3Head = new byte[MP3_HEAD_LEN]; // 光曲文件头信息
    private final int FRAME_HEAD_LEN = 16; // 每个数据帧 帧头长度
    private final int LIGHT_LEN = 128; // 光效长度
    private final int MAX_FRAME_LEN = 2500;
    public static MediaPlayer mediaPlayer = null;
    private long mediaPlayerfile_size = 0;
    private long tempPlayerfile_size = 0;
    private static Handler playMp3Handler = new Handler();
    private static String songLightFile;
    private float setVolumeTimes = 0;
    private boolean needPlayMp3 = false;
    private File playMp3, playlight;
    private long sendLightFrameNum = 0;
    private Timer sendLightTimer = null;
    private Timer outSendLightTimer = null;

    private synchronized void setPauseCurPlaying(boolean flag) {
        needPauseCurPlaying = flag;
    }

    private synchronized boolean pauseCurPlaying() {
        return needPauseCurPlaying;
    }

    private synchronized void setIsPausePlaying(boolean flag) {
        isPausePlaying = flag;
    }

    private synchronized boolean isPausePlaying() {
        return isPausePlaying;
    }

    private synchronized void setFilesListCursor(Cursor cursor) {
        filesListCursor = cursor;
    }

    private synchronized Cursor getFilesListCursor() {
        return filesListCursor;
    }

    private boolean needStopThread = false;
    private boolean needStopCreateMediaFileThread = false;

    private synchronized int getList_Mp3frameCount() {
        return list_Mp3frameCount;
    }

    private synchronized void setList_Mp3frameCount(int value) {
        list_Mp3frameCount = value;
    }

    private synchronized int getList_Mp3sendCount() {
        return list_Mp3sendCount;
    }

    private synchronized void setList_Mp3sendCount(int value) {
        list_Mp3sendCount = value;
    }

    private synchronized int getList_BeginFromIndex() {
        return list_BeginFromIndex;
    }

    private synchronized void setList_BeginFromIndex(int value) {
        list_BeginFromIndex = value;
    }

    private synchronized boolean getLoopMode() {
        return inLoopMode;
    }

    private synchronized void setLoopMode(boolean flag) {
        inLoopMode = flag;
    }

    boolean inDeleteListMode = false;
    private PlayLightSongListServiceBinder myBinder = new PlayLightSongListServiceBinder();
    private boolean hasInitLightSong = false;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        LogUtils.v(TAG, "onBind");
        return myBinder;
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopMp3Data();
        }
    };

    public void onCreate() {
        LogUtils.v(TAG, "onCreate");
        super.onCreate();
        registerReceiver(myReceiver, new IntentFilter("stopmusic"));
        // 启动播放光曲列表线程
        StartPlayLightSongListThread();
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY_COMPATIBILITY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    public class PlayLightSongListServiceBinder extends AIDLPlayLightSongListService.Stub {
        /*
         * 循环播放文件列表 , filesListName:保存文件列表id的文件 beginIndex:开始播放的歌曲在列表中的索引值
         */
        public void sendFilesListToDevice(String filesListName, int beginIndex) {
            GrandarUtils.setPauseFlag(false);
            SendFilesListToDeviceTask Task = new SendFilesListToDeviceTask(
                    filesListName, beginIndex);
            Task.execute();
        }

        /* 循环播放文件列表 */
        public void sendFilesListToDevice(String filesListName) {
            GrandarUtils.setPauseFlag(false);
            SendFilesListToDeviceTask Task = new SendFilesListToDeviceTask(
                    filesListName, 0);
            Task.execute();
        }

        /* 循环播放文件列表 闹钟播放提前设定的光曲或光曲列表 */
        public void sendFilesListToDevice(int mode, int playType, int id) {
            AlarmSendFilesFileToDeviceTask Task = new AlarmSendFilesFileToDeviceTask(
                    mode, playType, id);
            Task.execute();
        }

        /* 其他应用请求发送的光曲文件循环播放 */
        public void sendLoopFileToDevice(String fileName) {
            GrandarUtils.setPauseFlag(false);
            SendLoopFileInitFirst(fileName);
        }

        @Override
        public void stopMp3() throws RemoteException {
            stopMp3Data();
        }

        @Override
        public void createMp3Data(String filepath, boolean hasMusic, String name, int flag, int position) throws RemoteException {
            synchronized (mPlaySongLightLock) {
                try {
                    if (flag == 2) {
//                        GrandarUtils.stopMp3Data();
                        StartCreateMediaFile(filepath, name, flag, position);
                    } else {
                        //执行只有光效方法
//                        StartCreateLightFile(filepath, name, flag, position);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void pause_Music() throws RemoteException {
            pauseMp3Data();
        }

        public void resume_Mp3Data() throws RemoteException {
            resumeMp3Data();
        }


        @Override
        public boolean createMediaPlayer(String fileName) throws RemoteException {
            return false;
        }

        @Override
        public boolean startMediaPlaye(String fileName) throws RemoteException {
            return false;
        }

        @Override
        public boolean pause(String fileName) throws RemoteException {
            return false;
        }

        @Override
        public boolean stop(String fileName) throws RemoteException {
            return false;
        }

        public boolean getLoopMode() {
            return inLoopMode;
        }

        public int getPlayingSongLightId() {
            return -1;
        }


        public void pauseCurPlaying() {
            setPauseCurPlaying(true);
        }

        public void resumeCurPlaying() {
            setPauseCurPlaying(false);
        }

        public boolean isLightSongPlaying() {
            return isMp3Playing();
        }
    }


    class SendFilesListToDeviceTask extends AsyncTask<String, Integer, String> {
        final String mFilesListName;
        final int mBeginIndex;

        public SendFilesListToDeviceTask(String filesListName, int beginIndex) {
            mFilesListName = filesListName;
            mBeginIndex = beginIndex;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String result = "";
            // StopSendFileTimer();
            // GrandarUtils.setPauseFlag(false);
            SendFilesListInitFirst(mFilesListName, mBeginIndex);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("show")) {
            }
        }

    }

    class AlarmSendFilesFileToDeviceTask extends AsyncTask<String, Integer, String> {
        final int mMode;
        final int mPlayType;
        final int mId;

        public AlarmSendFilesFileToDeviceTask(int mode, int playType, int id) {
            mMode = mode;
            mPlayType = playType;
            mId = id;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String result = "";
            // StopSendFileTimer();
            /* 清除 PlayingListInfo 文件的内容，重置 相关状态 */
            GrandarUtils.clearCurPlayingListInfo(getApplicationContext());
            GrandarUtils.setGetPlayingFlag(false);
            GrandarUtils.setPauseFlag(false);
            SendFilesListInitFirst(mMode, mPlayType, mId);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("show")) {
            }
        }
    }

    // 1. 开启一个全局线程，该函数只调用一次，开启后不要断开
    private class PlayLightSongListThread extends Thread {
        public void run() {
            initSendThreadVar();
            while (true) {
                if (needStopThread) {
                    return;
                }
                // 有要播放的光曲列表
                if (getList_Mp3frameCount() > 0) {
                    if (getLoopMode()) {
                        if (getList_Mp3sendCount() == getList_Mp3frameCount()) {
                            setList_Mp3sendCount(0);
                        }
                    }
                    if (getList_Mp3sendCount() < getList_Mp3frameCount()) {
                        if (!hasInitLightSong()) {// 光曲文件是否初始化完成
                            // 初始化需要播放的光曲文件
                            try {
                                LogUtils.e(TAG, "getList_Mp3sendCount()---" + getList_Mp3sendCount());
                                playMusicSendLight(getList_Mp3sendCount());
                            } catch (Exception e) {
                                LogUtils.e(TAG, e.getMessage());
                            }
                        } else {
                            if (pauseCurPlaying() && isMp3Playing()
                                    && (!isPausePlaying())) { // 暂停播放当前光曲
                                pauseMp3Data();
                                setIsPausePlaying(true);
                            } else if (!pauseCurPlaying() && isPausePlaying()) {
                                resumeMp3Data();
                                setIsPausePlaying(false);
                            }
                        }
                    }
                }
            }
        }
    }

    public void StartPlayLightSongListThread() {
        if ((mPlayLightSongListThread == null)
                || (!mPlayLightSongListThread.isAlive())) {
            // 建立连接
            mPlayLightSongListThread = new PlayLightSongListThread();
            mPlayLightSongListThread.start();
        }
    }


    /* 根据灯体端请求的光曲序号得到光曲所在光曲列表的真实位置 */
    private int getDeviceReqPosToRealPos(int posIndex) {
        int total = getList_Mp3frameCount();
        int beginIndex = getList_BeginFromIndex();
        int pos = 0;

        if (posIndex < (total - beginIndex)) {
            pos = posIndex + beginIndex;
        } else {
            pos = posIndex - (total - beginIndex);
        }

        return pos;
    }

    String[] mPlaylistMemberCols = new String[]{
            SongLightStore.Playlists.Members._ID,
            SongLightStore.Playlists.Members.SONGLIGHT_ID,
            SongLightStore.Playlists.Members.PLAY_ORDER,
            SongLightStore.Playlists.Members.NAME,
            SongLightStore.Playlists.Members.MODE,
            SongLightStore.Playlists.Members.SUBMODE,
            SongLightStore.Playlists.Members.CHECKCODE,
            SongLightStore.Playlists.Members.TYPE,
            SongLightStore.Playlists.Members.TIMES};

    String[] mSongLightCols = new String[]{
            SongLightStore.TotalSongLights._ID,
            SongLightStore.TotalSongLights.NAME,
            SongLightStore.TotalSongLights.MODE,
            SongLightStore.TotalSongLights.SUBMODE,
            SongLightStore.TotalSongLights.CHECKCODE,
            SongLightStore.TotalSongLights.TYPE};

    public void querySongLightUseServersId(int serversId) {
        LogUtils.d(TAG, "querySongLightUseServersId serversId:"
                + serversId);
        Cursor ret = null;
        ContentResolver resolver = this.getContentResolver();
        Uri uri = SongLightStore.TotalSongLights.getContentUri();
        StringBuilder where = new StringBuilder();
        where.append(SongLightStore.TotalSongLights.SERVERS_ID + " = "
                + serversId);
        ret = resolver.query(uri, mSongLightCols, where.toString(), null, null);
        if (ret != null) {
            if (ret.getCount() > 0) {
                ret.moveToFirst();
                MatrixCursor addnewcursor = new MatrixCursor(
                        mPlaylistMemberCols);
                ArrayList<Object> addNew = new ArrayList<Object>(10);
                addNew.add(1);// _ID
                addNew.add(ret.getInt(ret
                        .getColumnIndexOrThrow(SongLightStore.TotalSongLights._ID))); // SONGLIGHT_ID
                addNew.add(0);// PLAY_ORDER
                addNew.add(ret.getString(ret
                        .getColumnIndexOrThrow(SongLightStore.TotalSongLights.NAME))); // NAME
                addNew.add(ret.getString(ret
                        .getColumnIndexOrThrow(SongLightStore.TotalSongLights.MODE))); // MODE
                addNew.add(ret.getString(ret
                        .getColumnIndexOrThrow(SongLightStore.TotalSongLights.SUBMODE))); // SUBMODE
                addNew.add(ret.getBlob(ret
                        .getColumnIndexOrThrow(SongLightStore.TotalSongLights.CHECKCODE))); // CHECKCODE
                addNew.add(ret.getInt(ret
                        .getColumnIndexOrThrow(SongLightStore.TotalSongLights.TYPE))); // TYPE
                if (ret.getInt(ret
                        .getColumnIndexOrThrow(SongLightStore.TotalSongLights.TYPE)) == 17) {
                    addNew.add(1);
                } else {
                    addNew.add(0);
                }
                addnewcursor.addRow(addNew);
                Cursor cursor = new MergeCursor(new Cursor[]{addnewcursor,
                        null});
                setFilesListCursor(cursor);
            }

            ret.close();
        }
    }

    public void querySingleSongLight(int songLightId) {
        Cursor ret = null;
        ContentResolver resolver = this.getContentResolver();
        Uri uri = SongLightStore.TotalSongLights.getContentUri();
        StringBuilder where = new StringBuilder();
        where.append(SongLightStore.TotalSongLights._ID + " = '" + songLightId
                + "'");
        LogUtils.e("筛选条件---", where.toString());
        ret = resolver.query(uri, mSongLightCols, where.toString(), null, null);
        if (ret.getCount() == 1) {
            ret.moveToFirst();
            MatrixCursor addnewcursor = new MatrixCursor(mPlaylistMemberCols);
            ArrayList<Object> addNew = new ArrayList<Object>(10);
            addNew.add(1);// _ID
            addNew.add(ret.getInt(ret
                    .getColumnIndexOrThrow(SongLightStore.TotalSongLights._ID))); // SONGLIGHT_ID
            addNew.add(0);// PLAY_ORDER
            addNew.add(ret.getString(ret
                    .getColumnIndexOrThrow(SongLightStore.TotalSongLights.NAME))); // NAME
            addNew.add(ret.getString(ret
                    .getColumnIndexOrThrow(SongLightStore.TotalSongLights.MODE))); // MODE
            addNew.add(ret.getString(ret
                    .getColumnIndexOrThrow(SongLightStore.TotalSongLights.SUBMODE))); // SUBMODE
            addNew.add(ret.getBlob(ret
                    .getColumnIndexOrThrow(SongLightStore.TotalSongLights.CHECKCODE))); // CHECKCODE
            addNew.add(ret.getInt(ret
                    .getColumnIndexOrThrow(SongLightStore.TotalSongLights.TYPE))); // TYPE
            for (int i = 0; i < addNew.size(); i++) {
                LogUtils.e("内容----", addNew.get(i).toString());
            }
            if (ret.getInt(ret
                    .getColumnIndexOrThrow(SongLightStore.TotalSongLights.TYPE)) == 17) {
                addNew.add(1);
            } else {
                addNew.add(0);
            }
            addnewcursor.addRow(addNew);
            Cursor cursor = new MergeCursor(new Cursor[]{addnewcursor, null});
            setFilesListCursor(cursor);
        }
        if (ret != null) {
            ret.close();
        }
    }

    public void queryPlayList(String modeName, int playListId) {
        ContentResolver resolver = this.getContentResolver();
        String mSortOrder = SongLightStore.Playlists.Members.PLAY_ORDER;
        StringBuilder where = new StringBuilder();
        where.append("list_id=" + playListId);
        where.append(" AND " + SongLightStore.Playlists.Members.NAME + " != ''");
        String tableName = modeName + "_list_members";
        Uri uri = SongLightStore.Playlists.Members.getContentUri(tableName);

        Cursor cursor = resolver.query(uri, mPlaylistMemberCols,
                where.toString(), null, mSortOrder);
        setFilesListCursor(cursor);
    }


    public int querySimplePlayList(String modeName, int startId) {
        Cursor ret = null;
        int retIndex = 0;
        ContentResolver resolver = this.getContentResolver();
        String mSortOrder = SongLightStore.TotalSongLights.SUBMODE + ","
                + SongLightStore.TotalSongLights.DEFAULT_SORT_ORDER;
        StringBuilder where = new StringBuilder();

        if (modeName.equals("mode9")) {
            mSortOrder = SongLightStore.TotalSongLights.DEFAULT_SORT_ORDER;
            where.append("favor = 1" + " and "
                    + SongLightStore.TotalSongLights.TYPE + "="
                    + MyUtil.SONG_AND_LIGHT + " and "
                    + SongLightStore.TotalSongLights.FAVOR_SIMPLELIST + "= 1");
        } else {
            where.append("mode = '" + modeName + "'" + " and "
                    + SongLightStore.TotalSongLights.TYPE + "="
                    + MyUtil.SONG_AND_LIGHT + " and "
                    + SongLightStore.TotalSongLights.SIMPLELIST + "= 1");
        }

        Uri uri = SongLightStore.TotalSongLights.getContentUri();

        ret = resolver.query(uri, mSongLightCols, where.toString(), null,
                mSortOrder);
        if (ret.getCount() > 0) {
            MatrixCursor addnewcursor = new MatrixCursor(mPlaylistMemberCols);
            for (int i = 0; i < ret.getCount(); i++) {
                ret.moveToPosition(i);
                ArrayList<Object> addNew = new ArrayList<Object>(10);
                int id = ret
                        .getInt(ret
                                .getColumnIndexOrThrow(SongLightStore.TotalSongLights._ID));
                if (id == startId) {
                    retIndex = i;
                }
                addNew.add(id);// _ID
                addNew.add(id); // SONGLIGHT_ID
                addNew.add(i);// PLAY_ORDER
                addNew.add(ret.getString(ret
                        .getColumnIndexOrThrow(SongLightStore.TotalSongLights.NAME))); // NAME
                addNew.add(ret.getString(ret
                        .getColumnIndexOrThrow(SongLightStore.TotalSongLights.MODE))); // MODE
                addNew.add(ret.getString(ret
                        .getColumnIndexOrThrow(SongLightStore.TotalSongLights.SUBMODE))); // SUBMODE
                addNew.add(ret.getBlob(ret
                        .getColumnIndexOrThrow(SongLightStore.TotalSongLights.CHECKCODE))); // CHECKCODE
                addNew.add(ret.getInt(ret
                        .getColumnIndexOrThrow(SongLightStore.TotalSongLights.TYPE))); // TYPE
                if (ret.getInt(ret
                        .getColumnIndexOrThrow(SongLightStore.TotalSongLights.TYPE)) == 17) {
                    addNew.add(1);
                } else {
                    addNew.add(0);
                }
                addnewcursor.addRow(addNew);
            }
            Cursor cursor = new MergeCursor(new Cursor[]{addnewcursor, null});
            setFilesListCursor(cursor);
        }
        if (ret != null) {
            ret.close();
        }
        return retIndex;
    }

    // 发送文件 循环播放
    public void SendLoopFileInitFirst(String fileName) {
        // inLoopMode = false;

        initSendThreadVar();
        setLoopMode(true);

        Cursor cursor = getFilesListCursor();
        if (cursor != null) {
            cursor.close();
            setFilesListCursor(null);
        }

        MatrixCursor addnewcursor = new MatrixCursor(mPlaylistMemberCols);
        ArrayList<Object> addNew = new ArrayList<Object>(1);
        addNew.add(1);// _ID
        addNew.add(1); // SONGLIGHT_ID
        addNew.add(0);// PLAY_ORDER
        addNew.add(fileName); // NAME
        addNew.add(null); // MODE
        addNew.add(null); // SUBMODE
        addNew.add(MyUtil.getSongLightMd5(fileName)); // CHECKCODE
        int type = MyUtil.getSongLightType(fileName);
        addNew.add(type); // TYPE
        if (MyUtil.isSongAndLight(type)) {
            addNew.add(1);
        } else {
            addNew.add(0);
        }
        addnewcursor.addRow(addNew);
        cursor = new MergeCursor(new Cursor[]{addnewcursor, null});

		/* 准备发送 初始化变量 统计需要发送的路径个数 */
        if ((cursor != null) && (cursor.getCount() > 0)) {
            LogUtils.i(TAG,
                    "SendFileInitFirst count=" + cursor.getCount());
            setFilesListCursor(cursor);
            SendFilesListInitSecond();
        }

    }

    /* 闹铃发送光曲 */
    public void SendFilesListInitFirst(int mode, int playType, int id) {

        initSendThreadVar();

        // inLoopMode = true;
        Cursor cursor = getFilesListCursor();
        if (cursor != null) {
            cursor.close();
            setFilesListCursor(null);
        }

        // playType 0:单个光曲 1：播放列表 2:简单列表
        if (playType == Constant.ConValue.PLAY_TYPE_SINGLE_SONG_LIGHT) {
            querySingleSongLight(id);

        } else if (playType == Constant.ConValue.PLAY_TYPE_PLAY_LIST) {
            queryPlayList("mode" + mode, id);
        }

        cursor = getFilesListCursor();
        /* 准备发送 初始化变量 统计需要发送的路径个数 */
        if ((cursor != null) && (cursor.getCount() > 0)) {
            /* 闹铃光曲是催眠光曲，则不循环播放 */
            cursor.moveToFirst();
            if (playType == Constant.ConValue.PLAY_TYPE_SINGLE_SONG_LIGHT) {
                if (cursor
                        .getString(
                                cursor.getColumnIndex(SongLightStore.TotalSongLights.MODE))
                        .equals("mode2")
                        && cursor
                        .getString(
                                cursor.getColumnIndex(SongLightStore.TotalSongLights.SUBMODE))
                        .equals("submode1")) {
                    setLoopMode(false);
                }
            }
            LogUtils.i(TAG,
                    "SendFilesListInitFirst count=" + cursor.getCount());
            SendFilesListInitSecond();
        }
    }

    /* 主应用播放器发送光曲 */
    public void SendFilesListInitFirst(String filesListname, int beginFromIndex) {
        SharedPreferences sharedPreferences = getSharedPreferences(
                filesListname, Activity.MODE_PRIVATE);

        int mode = sharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_MODE, 0);
        int submode = sharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_SUBMODE, 0);
        int playType = sharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_PLAY_TYPE, 0);
        int nameId = sharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_FILE_ID, 0);
        int fileListId = sharedPreferences.getInt(
                Constant.ConValue.PLAYING_LIST_INFO_LIST_ID, 0);
        // int simpleList = sharedPreferences.getInt("simpleList", 0);
        LogUtils.i(TAG, "SendFilesListInit filesListname="
                + filesListname + " mode=" + mode + " playType=" + playType);

        initSendThreadVar();

        // inLoopMode = true;
        Cursor listCursor = getFilesListCursor();
        if (listCursor != null) {
            listCursor.close();
            setFilesListCursor(null);
        }

        if (playType == Constant.ConValue.PLAY_TYPE_SIMPLE_PLAY_LIST) {
            /* 简单列表通过id值判断是简单列表的第几个 */
            beginFromIndex = querySimplePlayList("mode" + mode, beginFromIndex);
        } else if (playType == Constant.ConValue.PLAY_TYPE_SINGLE_SONG_LIGHT) {
            if ((mode == 2) && (submode == 1)) {
                // inLoopMode = false;
                setLoopMode(false);
                LogUtils.e(TAG, "SendFilesListInit 催眠模式不循环");
            }
            querySingleSongLight(nameId);
        } else if (playType == Constant.ConValue.PLAY_TYPE_PLAY_LIST) {
            queryPlayList("mode" + mode, fileListId);
        }

        listCursor = getFilesListCursor();

		/* 准备发送 初始化变量 统计需要发送的路径个数 */
        if ((listCursor != null) && (listCursor.getCount() > 0)) {
            if ((beginFromIndex >= 0)
                    && (beginFromIndex < listCursor.getCount())) {
                setList_BeginFromIndex(beginFromIndex);
            }
            SendFilesListInitSecond();
        }
    }

    int list_Mp3frameCount = 0;// 播放列表 光曲文件的总数
    int list_Mp3sendCount = 0; // 播放列表 当前发送的光曲数统计
    int list_BeginFromIndex = 0;// 播放列表 从该列表的第几个开始循环播放该列表（从0开始计数）

    /* 初始化发送线程使用到的变量 */
    private void initSendThreadVar() {
        LogUtils.i(TAG, "initSendThreadVar");
        setList_SendAllVar(0, 0, 0, true);
        setMp3_SendAllVar(0, 0, false, false);
        stopMp3Data(); // 插入耳机时 停止当前播放
    }

    private void SendFilesListInitSecond() {
        /* 统计需要发送的路径总个数 */
        Cursor listCursor = getFilesListCursor();
        setList_Mp3frameCount(listCursor.getCount());
        setHasInitLightSong(false);
        /* 发送播放列表开始标志 */
        // SendFrameCmd((byte) 0x01, (byte) 0x09, null);

        // zang -- 发送一次音量
        /*
         * if (LogUtils.DEBUG_ON_SAMSUNG) { byte[] data = new byte[1];
		 * data[0] = bSaveVol; SendFrameCmd((byte) 0x01, (byte) 0x03, data); }
		 */

        LogUtils.i(TAG, "SendMp3Thread getList_Mp3sendCount="
                + getList_Mp3sendCount() + "list_Mp3frameCount="
                + getList_Mp3frameCount());
    }

    private synchronized void setHasInitLightSong(boolean flag) {
        hasInitLightSong = flag;
    }

    private synchronized boolean hasInitLightSong() {
        return hasInitLightSong;
    }

    /* 设置发送光曲数据 用到的所有共享变量 */
    private synchronized void setMp3_SendAllVar(int frameCount, int sendCount,
                                                boolean needPause, boolean isPause) {
        Mp3frameCount = frameCount;
        Mp3sendCount = sendCount;
        needPauseCurPlaying = needPause;
        isPausePlaying = isPause;
    }


    /* 设置发送光曲列表 用到的所有共享变量 */
    private synchronized void setList_SendAllVar(int sendFrame, int frameCount,
                                                 int BeginFromIndex, boolean loopMode) {
        list_Mp3sendCount = sendFrame;
        list_Mp3frameCount = frameCount;
        list_BeginFromIndex = BeginFromIndex;
        inLoopMode = loopMode;
    }

    void completePlayMusicSendLight() {
        setHasInitLightSong(false);
        setList_Mp3sendCount(getList_Mp3sendCount() + 1);
    }

    public void playMusicSendLight(int serial_num)
            throws IllegalArgumentException, Exception {

        LogUtils.i(TAG, "playMusicSendLight serial_num is " + serial_num);
        Cursor listCursor = getFilesListCursor();
        if (listCursor == null) {
            LogUtils.e(TAG, "playMusicSendLight filesListCursor == null");
            return;
        }

        if ((serial_num >= listCursor.getCount()) || (serial_num < 0)) {
            LogUtils.e(TAG, "playMusicSendLight serial_num > filesListCursor.getCount() count="
                    + listCursor.getCount());
            return;
        }

        int pos = getDeviceReqPosToRealPos(serial_num);
        listCursor.moveToPosition(pos);
        // setSendingFileName(null);
        String fileNameString;
        if (listCursor.getString(listCursor
                .getColumnIndexOrThrow(SongLightStore.Playlists.Members.MODE)) != null) {
            //fileNameString = FileHelper.FILEPATH+"/mode6/submode1/婚礼那天的记忆.ils"
            fileNameString = FileHelper.FILEPATH
                    + "/"
                    + listCursor
                    .getString(listCursor.getColumnIndexOrThrow(SongLightStore.Playlists.Members.MODE))
                    + "/";
            if (listCursor.getString(listCursor.getColumnIndexOrThrow(SongLightStore.Playlists.Members.SUBMODE)) != null) {
                fileNameString = fileNameString + listCursor.getString(listCursor.getColumnIndexOrThrow(SongLightStore.Playlists.Members.SUBMODE))
                        + "/";
            }
            fileNameString = fileNameString + listCursor.getString(listCursor.getColumnIndexOrThrow(SongLightStore.Playlists.Members.NAME));
        } else {
            fileNameString = listCursor.getString(listCursor.getColumnIndexOrThrow(SongLightStore.Playlists.Members.NAME));
        }

        File file = new File(fileNameString);
        if (!file.exists()) {
            LogUtils.i(TAG, "playMusicSendLight file not exitst ");
            return;
        }
        stopMp3Data();

        // 发送正在播放的光曲序号
        GrandarUtils.savePlayingIdx(getApplicationContext(), serial_num + 1);
        sendPlayingChangeBroadcast(serial_num + 1);

        setHasInitLightSong(true);
    }

    private void sendPlayingChangeBroadcast(int order) {
        Intent sendIntent = new Intent(Constant.ACTION_IGOO_INFO_PLAYING_CHANGE);
        sendIntent.putExtra("Order", order);
        sendBroadcast(sendIntent);
    }

    public void playMp3Data(final String name, final int flag, final File outLight) {
        startMediaPlayer(name, flag, outLight);
    }

    boolean isMp3Playing() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    return true;
                }
            } catch (IllegalStateException e) {
                return false;
            }
        }
        return false;
    }

    public void pauseMp3Data() {
        synchronized (mPlaySongLightLock) {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        }
    }

    public void resumeMp3Data() {
        synchronized (mPlaySongLightLock) {
            if (mediaPlayer != null) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(this);
                StartSendLight(sendLightFrameNum);
            } else {
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        // TODO Auto-generated method stub
        KLog.e(TAG, "onCompletion: 歌曲放完了");
        synchronized (mPlaySongLightLock) {
            if (sendLightTimer != null) {
                sendLightTimer.cancel();
                KLog.e("stopMp3Data:" + "歌曲放完了");
            }
//            resumeMp3Data();
//            new TcpClientService().stopMp3Data();
        }
//        completePlayMusicSendLight();
    }

    public void stopMp3Data() {
        synchronized (mPlaySongLightLock) {
            if (sendLightTimer != null) {
                sendLightTimer.cancel();
            }
            if (mediaPlayer != null) {
                try {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                } catch (IllegalStateException e) {
                }
            }
            needStopCreateMediaFileThread = true;
            songLightFile = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        needStopThread = true;
        Cursor cursor = getFilesListCursor();
        if (cursor != null) {
            cursor.close();
            setFilesListCursor(null);
        }
        if (sendLightTimer != null) {
            sendLightTimer.cancel();
        }
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (IllegalStateException e) {

            }
        }
    }

    /**
     * 发送音效加光效文件方法
     */
    public void StartCreateMediaFile(String path, String name, int flag, int position) throws Exception {
        FileInputStream isMp3 = null;
        FileOutputStream outMp3 = null, outLight = null;
        int Mp3frameCount = 0;// 光曲文件的总帧数
        int Mp3sendCount; // 当前发送的帧数统计
        try {
            //创建两个临时的文件  用来存储MP3和单光效
            playMp3 = FileHelper.newFile("/temp", "playingMedia.dat");
            playlight = FileHelper.newFile("/temp", "playingMedia" + position + ".dat");
            outMp3 = new FileOutputStream(playMp3);
            outLight = new FileOutputStream(playlight);
            //输入流读取原来的数据
            isMp3 = new FileInputStream(path);
            // 读取292字节的头文件
            isMp3.read(Mp3Head);
            //  四个字节合成一个int类型的偏移值
            int offset = 0;
            offset += (Mp3Head[276] & 0x00FF);
            offset += (Mp3Head[277] & 0x00FF) << 8;
            offset += (Mp3Head[278] & 0x00FF) << 16;
            offset += (Mp3Head[279] & 0x00FF) << 24;
            //  输入流偏移对应的偏移值
            isMp3.skip(offset);
            //四个字节合成总帧数
            Mp3frameCount += (Mp3Head[280] & 0x00FF);
            Mp3frameCount += (Mp3Head[281] & 0x00FF) << 8;
            Mp3frameCount += (Mp3Head[282] & 0x00FF) << 16;
            Mp3frameCount += (Mp3Head[283] & 0x00FF) << 24;
            for (Mp3sendCount = 0; Mp3sendCount < Mp3frameCount; Mp3sendCount++) {
                try {
                    // MP3每一帧的帧头长度  16
                    byte[] bframeHead = new byte[FRAME_HEAD_LEN];
                    //  输入流读取对应的长度到  16个字节的帧头
                    isMp3.read(bframeHead);
                    //16个字节的的第一个字节表示有没有音乐
                    byte byType = bframeHead[0];
                    //等于17  表示有音乐  进入有音乐的方法
                    if (byType == MyUtil.SONG_AND_LIGHT) {
                        // 创建一个 4个字节的数组
                        byte lenBuf[] = new byte[4];
                        //把bframeHead的第8个字节开始复制到lenBuf  复制4个字节
                        System.arraycopy(bframeHead, 8, lenBuf, 0, 4);
                        // 把四个字节转换为一个int值
                        int lightOffset = MyUtil.bytesToInt(lenBuf);
                        if (lightOffset > MAX_FRAME_LEN || lightOffset < 0) {
                            throw new SongLightFrameException("error lightOffset=" + lightOffset + " frame index=" + Mp3sendCount);
                        }
                        // 创建一个int类型的长度的bte数组  并且把数组读取到MP3中
                        byte[] buffMp3Data = new byte[lightOffset];
                        isMp3.read(buffMp3Data);
                        outMp3.write(buffMp3Data);
                    }
                    //创建一个128位的光效文件  并且把数据读入到光效文件中
                    byte[] buffLightData = new byte[LIGHT_LEN];
                    isMp3.read(buffLightData);
                    outLight.write(buffLightData);
                    outLight.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                //  播放MP3  还有音乐
                mediaPlayerfile_size = playMp3.length();
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(new FileInputStream(playMp3.getAbsolutePath()).getFD());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(this);
                playMp3Data(name, flag, playlight);
                outMp3.close();
                outLight.close();
                isMp3.close();
            } catch (IOException e) {

            }
        }
    }

    //没有音乐的开始发送光效帧
    private void StartSendLight(long sendFrame) {
        try {
            final int offset = MyUtil.getLightSongOffset(songLightFile);
            // 获取总帧数
            final int totalFrame = MyUtil.getLightSongFrame(songLightFile);
            sendLightFrameNum = sendFrame;
            final File lightFile = new File(songLightFile);
            sendLightTimer = new Timer();
            sendLightTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (sendLightFrameNum >= totalFrame) {
                        sendLightTimer.cancel();
                        return;
                    }
                    FileInputStream isLight = null;
                    try {
                        isLight = new FileInputStream(lightFile);
                        byte[] lightBuf = new byte[128];
                        isLight.skip(MP3_HEAD_LEN + offset + sendLightFrameNum * (FRAME_HEAD_LEN + LIGHT_LEN) + FRAME_HEAD_LEN);
                        isLight.read(lightBuf);
                        GrandarUtils.sendLightDataTime(lightBuf, 0, ""); // 发送光效帧
                        sendLightFrameNum = sendLightFrameNum + 1;
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally {
                        try {
                            isLight.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                        }
                    }
                }

            }, 0, 26);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private List<SearchBean> list;

    private void musicStartSendLight(long sendFrame, final String name, File outLight) {
        String deviceSn = SharedPutils.getString(getApplication(), "deviceSn", "");
//        List<String> Sn = SharedPutils.getList(getBaseContext(), "snList");
//        for (SearchBean listSn : list) {
//
//        }
        KLog.e("snList:" + deviceSn);
        try {
            final ArrayList<MusicListSong> data = GlobalApplication.getInstance().getData();
            if (data != null) {
                final int[] sendLightFrameNumx = {0};
                final int MP3_HEAD_LEN = 292;
                final int FRAME_HEAD_LEN = 16; // 每个数据帧 帧头长度
                final int LIGHT_LEN = 128;
                final File playlight = outLight;
                long frameNum = playlight.length() / 128;
                final long frameSec = mediaPlayer.getDuration() / frameNum;
                final long time = mediaPlayer.getDuration() / frameNum;

//                String s = EXPORTFILEPATHx + data.get(0).getName();
//                String s1 = EXPORTFILEPATHx + data.get(1).getName();

//                final int lightSongOffset1 = MyUtil.getLightSongOffset(s);
//                final int lightSongOffset2 = MyUtil.getLightSongOffset(s1);
//
//                int lightSongFrame1 = MyUtil.getLightSongFrame(s);
//                int lightSongFrame2 = MyUtil.getLightSongFrame(s1);

                isLight2 = new FileInputStream("/storage/emulated/0/IGOO/Grandar/temp/playingMedia1.dat");
                isLight3 = new FileInputStream("/storage/emulated/0/IGOO/Grandar/temp/playingMedia2.dat");
                isLight = new FileInputStream(playlight);

                final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                sendLightFrameNum = sendFrame;
                sendLightTimer = new Timer();
                sendLightTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        boolean isBtSpeaker = audioManager.isBluetoothA2dpOn();
                        int curPosition;
                        synchronized (mPlaySongLightLock) {
                            if (mediaPlayer != null) {
                                curPosition = mediaPlayer.getCurrentPosition();
                            } else {
                                return;
                            }
                        }
                        long curFrame = isBtSpeaker ? ((curPosition > MyConstant.BT_SPEAKER_DELAY_TIME) ?
                                (((curPosition - MyConstant.BT_SPEAKER_DELAY_TIME) / frameSec) + 1) : 0) : ((curPosition / frameSec) + 1);
                        if (sendLightFrameNum < curFrame) {
                            if ((curFrame * 128) > playlight.length()) {
                                return;
                            }
                            try {
//                                isLight.skip((curFrame - 1) * 128);
                                isLight.read(lightBuf);
                                datass.add(lightBuf);

//                                isLight2.skip(MP3_HEAD_LEN + lightSongOffset1 + sendLightFrameNumx[0] * (FRAME_HEAD_LEN + LIGHT_LEN) + FRAME_HEAD_LEN);
//                                isLight2.read(lightBuf1);
//                                datass.add(lightBuf1);

//                                isLight3.skip(MP3_HEAD_LEN + lightSongOffset2 + sendLightFrameNumx[0] * (FRAME_HEAD_LEN + LIGHT_LEN) + FRAME_HEAD_LEN);
//                                isLight3.read(lightBuf2);
//                                datass.add(lightBuf2);

                                sendLightFrameNumx[0] = sendLightFrameNumx[0] + 1;
                                try {
//                                    for (int i = 0; i < 1; i++) {
//                                        GrandarUtils.sendLightDataTime(datass.get(i), 0, stringss.get(i));
//                                    }
                                    GrandarUtils.sendLightDataTime(datass.get(0), 0, deviceSn);
                                    KLog.e("当前ip  开始发送sn：" + deviceSn);
                                } catch (Exception e) {
                                }
                                datass.clear();
                                sendLightFrameNum = curFrame;
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } finally {
                            }
                        }
                    }
                }, 0, time);
            }
        } catch (Exception e) {
        }
    }

    private void startMediaPlayer(String name, int flag, File outLight) {
        musicStartSendLight(0, name, outLight);
    }
}
