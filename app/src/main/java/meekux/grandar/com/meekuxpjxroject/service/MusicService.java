package meekux.grandar.com.meekuxpjxroject.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import meekux.grandar.com.meekuxpjxroject.songdata.database.Constant;
import meekux.grandar.com.meekuxpjxroject.songdata.database.FileHelper;
import meekux.grandar.com.meekuxpjxroject.songdata.database.GrandarLogUtils;
import meekux.grandar.com.meekuxpjxroject.songdata.database.MyUtil;
import meekux.grandar.com.meekuxpjxroject.songdata.database.SongLightFrameException;

public class MusicService extends Service implements
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {
    private static final String TAG = "MusicService";
    private MediaPlayer mediaPlayer = null; // MediaPlayer
    private Uri uri;
    private String PATH = "/sdcard/music/sengling.mp3";
    private long mediaPlayerfile_size = 0;
    private long tempPlayerfile_size = 0;
    private boolean needPlayMp3 = false;
    private static Handler playMp3Handler = new Handler();
    private MusicServiceBinder myBinder = new MusicServiceBinder();

    public class MusicServiceBinder extends Binder {
        void createMp3Data(String filepath) {
            playMp3Handler.removeCallbacks(playMp3Runnable);
            songLightFile = filepath;
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayerfile_size = 0;
                tempPlayerfile_size = 0;
            }
            GrandarLogUtils.d(TAG, "createMp3Data");
            StartCreateMediaFile();
            needPlayMp3 = true;
        }

        void playMp3Data() {
            playMp3Handler.postDelayed(playMp3Runnable, 1250);
        }

        void stopMp3Data() {
            if (mediaPlayer != null) {
                try {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                } catch (IllegalStateException e) {
                    mediaPlayer = null;
                }
            }
        }

        void pauseMp3Data() {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        }

        void resumeMp3Data() {
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        }

        boolean isMp3Playing() {
            GrandarLogUtils.d(TAG, "isMp3Playing1 mediaPlayer:" + mediaPlayer);
            if (mediaPlayer != null) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        GrandarLogUtils.d(TAG, "isMp3Playing2 mediaPlayer:" + mediaPlayer);
                        return true;
                    }
                } catch (IllegalStateException e) {
                    GrandarLogUtils.d(TAG, "isMp3Playing IllegalStateException e:" + e);
                    return false;
                }
            }
            return false;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        GrandarLogUtils.v(TAG, "onBind");
        return myBinder;
    }

    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        System.out.println("Service onCreate");

    }

	/*
     * public IBinder onBind(Intent arg0) { return null; }
	 */

    private File downloadingMediaFile;
    String songLightFile;

    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        GrandarLogUtils.v(TAG, "onStart");
    }

    public void onDestroy() {
        GrandarLogUtils.v(TAG, "onDestroy");
        super.onDestroy();
        if (mediaPlayer != null)
            mediaPlayer.stop(); // 鍋滄service瀵硅薄
    }

    private int counter = 0;

    private void startMediaPlayer() {
        try {
            /*
             * File bufferedFile = FileHelper.newFile("/temp", "playingMedia" +
			 * (counter++) + ".dat"); moveFile(downloadingMediaFile,
			 * bufferedFile); Log.e(getClass().getName(), "Buffered File path: "
			 * + bufferedFile.getAbsolutePath()); Log.e(getClass().getName(),
			 * "Buffered File length: " + bufferedFile.length() + "");
			 */
            mediaPlayerfile_size = playMp3.length();
            mediaPlayer = createMediaPlayer(playMp3);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            GrandarLogUtils.d(TAG, "startMediaPlayer getDuration" + mediaPlayer.getDuration()
                    + " length:" + mediaPlayerfile_size);
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e(getClass().getName(), "Error initializing the MediaPlayer.",
                    e);
        }
    }

    public void moveFile(File oldLocation, File newLocation) throws IOException {

        if (oldLocation.exists()) {
            BufferedInputStream reader = new BufferedInputStream(
                    new FileInputStream(oldLocation));
            BufferedOutputStream writer = new BufferedOutputStream(
                    new FileOutputStream(newLocation, false));
            try {
                byte[] buff = new byte[8192];
                int numChars;
                while ((numChars = reader.read(buff, 0, buff.length)) != -1) {
                    writer.write(buff, 0, numChars);
                }
            } catch (IOException ex) {
                throw new IOException("IOException when transferring "
                        + oldLocation.getPath() + " to "
                        + newLocation.getPath());
            } finally {
                try {
                    if (reader != null) {
                        writer.close();
                        reader.close();
                    }
                } catch (IOException ex) {
                    Log.e(getClass().getName(),
                            "Error closing files when transferring "
                                    + oldLocation.getPath() + " to "
                                    + newLocation.getPath());
                }
            }
        } else {
            throw new IOException(
                    "Old location does not exist when transferring "
                            + oldLocation.getPath() + " to "
                            + newLocation.getPath());
        }
    }

    private MediaPlayer createMediaPlayer(File mediaFile) throws IOException {
        GrandarLogUtils.d(TAG, "createMediaPlayer1");
        MediaPlayer mPlayer = new MediaPlayer();
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(getClass().getName(), "Error in MediaPlayer: (" + what
                        + ") with extra (" + extra + ")");
                return false;
            }
        });
        mPlayer.setOnBufferingUpdateListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnPreparedListener(this);
        FileInputStream fis = new FileInputStream(mediaFile);

        mPlayer.setDataSource(fis.getFD());// 姝ゆ柟娉曡繑鍥炰笌娴佺浉鍏宠仈鐨勬枃浠惰鏄庣銆�
        // mPlayer.prepare();
        fis.close();
        //mPlayer.setDataSource(this,
        //		Uri.parse(FileHelper.FILEPATH + "/temp/playingMedia.dat"));
        GrandarLogUtils.d(TAG, "createMediaPlayer2 mediaFile" + mediaFile.length());
        return mPlayer;
    }

    private void transferBufferToMediaPlayer() {
        try {
            int curPosition = mediaPlayer.getCurrentPosition();
            boolean wasPlaying = mediaPlayer.isPlaying();
            boolean atEndOfFile = mediaPlayer.getDuration()
                    - mediaPlayer.getCurrentPosition() <= 1000;
            /*
             * File oldBufferedFile = new File(FileHelper.FILEPATH + "/temp",
			 * "playingMedia" + counter + ".dat"); File bufferedFile = new
			 * File(FileHelper.FILEPATH + "/temp", "playingMedia" + (counter++)
			 * + ".dat");
			 * 
			 * bufferedFile.deleteOnExit(); moveFile(downloadingMediaFile,
			 * bufferedFile);
			 */
            MediaPlayer tempMediaPlayer;
            tempPlayerfile_size = playMp3.length();
            tempMediaPlayer = createMediaPlayer(playMp3);
            tempMediaPlayer.setOnSeekCompleteListener(this);
            tempMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            tempMediaPlayer.prepare();
            GrandarLogUtils
                    .i(TAG,
                            "transferBufferToMediaPlayer1 curPosition:"
                                    + curPosition + " getDuration:"
                                    + mediaPlayer.getDuration() + "temp getDuration():" + tempMediaPlayer.getDuration()
                                    + " tempPlayerfile_size:" + tempPlayerfile_size);
            if (mediaPlayerfile_size != tempPlayerfile_size) {
                tempMediaPlayer.seekTo(curPosition);
            } else {
                tempMediaPlayer.release();
            }

            // oldBufferedFile.delete();

        } catch (Exception e) {
            Log.e(getClass().getName(),
                    "Error updating to newly loaded content.", e);
        }
    }

    private final int MP3_HEAD_LEN = 292;//光曲头文件信息
    byte[] Mp3Head = new byte[MP3_HEAD_LEN]; // 光曲文件头信息
    byte[] Mp3Expand;
    private int mp3_expand_len = 0;
    final int FRAME_HEAD_LEN = 16; // 每个数据帧 帧头长度
    final int LIGHT_LEN = 128; // 光效长度
    final int MAX_FRAME_LEN = 2500;
    File playMp3;
    createMediaFile createMediaFileThread;

    public void StartCreateMediaFile() {
        GrandarLogUtils.i(TAG, "StartCreateMediaFile createMediaFileThread="
                + createMediaFileThread);
        if (createMediaFileThread != null) {
            GrandarLogUtils.i(TAG,
                    "StartCreateMediaFile createMediaFileThread != null");
            createMediaFileThread.interrupt();
            createMediaFileThread = null;
        }
        if (createMediaFileThread == null) {
            createMediaFileThread = new createMediaFile();
            createMediaFileThread.start();
        }
    }

    private class createMediaFile extends Thread {
        public void run() {
            FileInputStream isMp3 = null;
            FileOutputStream outMp3 = null;
            int Mp3frameCount = 0;//光曲文件的总帧数
            int Mp3sendCount = 0; //当前发送的帧数统计
            Time time = new Time();
            time.setToNow();
            int nMinuteOld = time.minute;
            int nSecondOld = time.second;
            try {
                playMp3 = FileHelper.newFile("/temp", "playingMedia.dat");
                GrandarLogUtils.i(TAG,
                        "createMediaFile path:" + playMp3.getAbsolutePath());
                outMp3 = new FileOutputStream(playMp3);
                isMp3 = new FileInputStream(songLightFile);
                isMp3.read(Mp3Head);
                if ((Mp3Head[0] != 0x45) && (Mp3Head[1] != 0x44)
                        && (Mp3Head[2] != 0x45) && (Mp3Head[3] != 0x4c)) {
                    GrandarLogUtils.i(TAG, "createMediaFile 文件标识错，不是有效的光曲文件 ");
                    isMp3.close();
                    throw new SongLightFrameException("createMediaFile Mp3Head error");
                    //return;
                }
                int offset = 0;
                offset += (Mp3Head[276] & 0x00FF);
                offset += (Mp3Head[277] & 0x00FF) << 8;
                offset += (Mp3Head[278] & 0x00FF) << 16;
                offset += (Mp3Head[279] & 0x00FF) << 24;
                // mp3_expand_len = offset;
                isMp3.skip(offset);
                Mp3frameCount += (Mp3Head[280] & 0x00FF);
                Mp3frameCount += (Mp3Head[281] & 0x00FF) << 8;
                Mp3frameCount += (Mp3Head[282] & 0x00FF) << 16;
                Mp3frameCount += (Mp3Head[283] & 0x00FF) << 24;
                String str = String.format("Mp3总帧数: %d", Mp3frameCount);
                GrandarLogUtils.i(TAG, "createMediaFile " + str
                        + " Mp3sendCount=" + Mp3sendCount);

                for (Mp3sendCount = 0; Mp3sendCount < Mp3frameCount; Mp3sendCount++) {
                    try {
                        // 读光曲数据帧长度
                        byte[] bframeHead = new byte[FRAME_HEAD_LEN];
                        int nReadFrameHead = 0, nReadLight = 0;
                        nReadFrameHead = isMp3.read(bframeHead);

                        byte byType = bframeHead[0];
                        if (byType == MyUtil.SONG_AND_LIGHT) {
                            byte lenBuf[] = new byte[4];
                            System.arraycopy(bframeHead, 8, lenBuf, 0, 4);
                            int lightOffset = MyUtil.bytesToInt(lenBuf);

                            GrandarLogUtils.i(TAG, "createMediaFile lightOffset=" + lightOffset);
                            if (lightOffset > MAX_FRAME_LEN || lightOffset < 0) {
                                throw new SongLightFrameException("error lightOffset=" + lightOffset + " frame index=" + Mp3sendCount);
                            }
                            // 光效帧 mp3数据
                            byte[] buffMp3Data = new byte[lightOffset];
                            isMp3.read(buffMp3Data);
                            outMp3.write(buffMp3Data);
                        }
                        isMp3.skip(LIGHT_LEN);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                time.setToNow();
                int nMinute = time.minute;
                int nSecond = time.second;
                int nEqual = (nMinute - nMinuteOld) * 60 + nSecond - nSecondOld;
                if (nEqual == 0) {
                    nEqual = 1;
                }
                String debStr = String.format("总秒数：%d, 平均每秒写入的帧数：%d", nEqual,
                        (Mp3sendCount / nEqual));
                GrandarLogUtils.d(TAG, "createMediaFile " + debStr);

            } catch (IOException e) {
                GrandarLogUtils.i(TAG, "createMediaFile e" + e);
            } catch (SongLightFrameException e) {
                GrandarLogUtils.i(TAG, "createMediaFile SongLightFrameException e" + e);
                Intent startIntent = new Intent();
                startIntent.setAction(Constant.SONG_LIGHT_EXCEPTION);
                sendBroadcast(startIntent);
            } catch (Exception e) {
                GrandarLogUtils.i(TAG, "createMediaFile Exception e" + e);
            } finally {
                try {
                    outMp3.close();
                    isMp3.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    GrandarLogUtils.i(TAG, "createMediaFile finally IOException e:" + e);
                }
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        // TODO Auto-generated method stub
        GrandarLogUtils.d(TAG, "onPrepared player:" + player);
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        // TODO Auto-generated method stub
        GrandarLogUtils.d(TAG, "onCompletion player:" + player);
        transferBufferToMediaPlayer();
    }

    @Override
    public void onSeekComplete(MediaPlayer player) {
        // TODO Auto-generated method stub
        GrandarLogUtils.d(TAG, "onSeekComplete player.getDuration():" + player.getCurrentPosition()
                + " player.getDuration():" + player.getDuration());
        player.setOnSeekCompleteListener(null);
        player.start();
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = player;
        mediaPlayerfile_size = tempPlayerfile_size;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer player, int arg1) {
        // TODO Auto-generated method stub
        GrandarLogUtils.d(TAG, "onBufferingUpdate player:" + player);
    }

    Runnable playMp3Runnable = new Runnable() {

        @Override
        public void run() {
            if ((mediaPlayer == null) && (needPlayMp3 == true)) {
                needPlayMp3 = false;
                GrandarLogUtils.d(TAG, "playMp3Runnable mediaPlayer == null");
                // downloadingMediaFile = new File(songLightFile);
                try {
                    startMediaPlayer();
                } catch (Exception e) {
                    Log.e(getClass().getName(),
                            "Error copying buffered conent.", e);
                }

            }

        }
    };

}
