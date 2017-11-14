package meekux.grandar.com.meekuxpjxroject.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;


import java.io.IOException;
import java.util.HashMap;

import meekux.grandar.com.meekuxpjxroject.MainActivity;
import meekux.grandar.com.meekuxpjxroject.R;

/**
 * Created by xiehj on 15-4-23.
 */
public class Utility         //实用类，提供音频播放，振动等常用简单功能控制
{
    public interface OnPlayAudioListener      //此接口用来传递播放音频结束回调
    {
        public void onPlayAudioFinish(int code);
    }

    OnPlayAudioListener playAudioListen = null;

    void setOnPlayAudioListener(OnPlayAudioListener listener) {
        playAudioListen = listener;
    }


    public static Vibrator vibrator = null;

    SoundPool soundPool;     // 创建一个 SoundPool 对象

    HashMap<Integer, Integer> musicId = new HashMap<Integer, Integer>(); // 定义一个 HashMap 用于存放音频流的 ID


    static MediaPlayer mPlayer = null;

    int playSeekTo = 0;   //需要播放跳转到的位置
    static int playStatus = 0;   //当前播放状态  0:空闲  1:开始载入音乐  2:载入成功  3:正在播放  4:播放结束  5:错误

    public Utility(MainActivity activity) {
        vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);

        // 初始化 soundPool, 设置可容纳 12 个音频流，音频流的质量为 5 ，
        soundPool = new SoundPool(3, 0, 5);

        // 通过 load 方法加载指定音频流，并将返回的音频 ID 放入 musicId 中

        musicId.put(1, soundPool.load(activity, R.raw.click, 1));
    }


    public void vibrate() {
        if (vibrator == null)
            return;

        long[] pattern = {60, 200};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern, 1);
    }

    public void sound(int id)  //id=1,2,3
    {
        soundPool.play(musicId.get(id), 1, 1, 0, 0, 1);
    }


    public boolean audioLoad(Context context, String music, int mscStart) {
//        if (playStatus > 0)
//            audioStop();

        if (mPlayer == null)
            mPlayer = new MediaPlayer();

        if (mPlayer == null)
            return false;

        //if(mscStart==0)
        playSeekTo = mscStart;
        // else
        //    playSeekTo = mscStart+20;   //中间开始同步的，超前30ms进行修正

        // 设置必要的监听器
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mPlayer.setLooping(false); //取消循环播放

                //mPlayer.start();         //这时能确保player处于Prepared状态，触发start是最合适的

                playStatus = 2;

                mPlayer.seekTo(playSeekTo);   //跳转到指定的播放位置

                Log.i("utility", "on prepare play music");
            }
        });

        mPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                Log.i("utility", "on seek complete");
            }
        });

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 正常播放结束
                playStatus = 4;

                if (playAudioListen != null)
                    playAudioListen.onPlayAudioFinish(0);

            }
        });
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // 操作错误或其他原因导致的错误会在这里被通知

                playStatus = -1;

                return true;
            }
        });


        // 设置资源路径，成功执行的话player将处于Initialized状态
        try {
            //AssetFileDescriptor fileDescriptor = context.getAssets().openFd(music);
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() ;

            mPlayer.setDataSource(path + music);
            mPlayer.prepareAsync();

            playStatus = 1;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // 如果在非Idle状态下调用setDataSource就会导致该异常
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return true;
    }

    public static void audioStop() {
        if (mPlayer == null || playStatus == 0)
            return;

        Log.i("utility", "audioStop");

        try {
            mPlayer.stop(); // 这是必要的，如果你设置了循环播放，否则程序退出了音乐仍在后台继续播...
            mPlayer.reset();
            //mPlayer.release();
            //mPlayer = null;
        } catch (IllegalStateException e) {
            e.printStackTrace();

            return;
        }

        playStatus = 0;
    }


    public static void audioStart() {
        if (mPlayer == null)
            return;

        mPlayer.start();

        playStatus = 3;

        Log.i("utility", "start play music");
    }

    public int audioGetPos() {
        if (mPlayer == null || playStatus == 0)
            return -1;

        return mPlayer.getCurrentPosition();
    }

    //关闭声音
    public static void CloseVolume() {
        if (mPlayer != null)
            mPlayer.setVolume(0, 0);
        //mPlayer.pause();

    }

    public static void OpenVolume() {
        if (mPlayer != null) {
            mPlayer.setVolume(0.5f, 0.5f);
            mPlayer.start();
        }
    }
}
