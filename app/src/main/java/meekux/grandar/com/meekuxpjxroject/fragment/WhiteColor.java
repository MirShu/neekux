package meekux.grandar.com.meekuxpjxroject.fragment;

import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import meekux.grandar.com.meekuxpjxroject.GlobalApplication;
import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.songdata.database.GrandarUtils;

/**
 * Created by liuqk on 2017/5/11.
 */

public class WhiteColor extends Fragment{
    private View view;
    final int CONVERSION_VALUE = 4095/255;
    private SeekBar colortemperture;
    private int nProgressRed3;
    private int nProgressGreen3;
    private int nProgressBlue3;
    private int nProgressWhite3;
    private int nProgressColorHot3;
    private ArrayList<String> arrayList;
    private SeekBar lightseekbar;
    private SeekBar secckBarWhitecolorLight;
    private SeekBar secckBarWhitecolorLight1;
    private SeekBar secckBarsongwhitefragement;
    private ArrayList<String> snarraylist;

    private HashMap<String, ArrayList<String>> snmap;
    private MediaPlayer mediaPlayer01;
    public AudioManager audiomanage;
    public SeekBar soundBar;
    private int maxVolume, currentVolume;
    public class Threa implements Runnable {
        Object lock;
        int i;
        public Threa(Object lock,int i) {
            this.lock = lock;
            this.i = i;
        }
        public void run() {
            synchronized(lock){
                if (arrayList != null) {
                    for (String s : arrayList) {
                        Socket device = GlobalApplication.getInstance().getDevice(s);
                        if (device != null) {
                            GrandarUtils.setWholeLight(i, s);
                        }
                    }
                }
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        audiomanage = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audiomanage.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
        view = inflater.inflate(R.layout.whitecolor, null);
        secckBarWhitecolorLight = (SeekBar) view.findViewById(R.id.secckBarWhitecolorLight);
        secckBarsongwhitefragement = (SeekBar) view.findViewById(R.id.secckBarsongwhitefragement);
        secckBarsongwhitefragement.setMax(maxVolume);
        secckBarsongwhitefragement.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);  //获取当前值
                secckBarsongwhitefragement.setProgress(currentVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        lightseekbar = (SeekBar) view.findViewById(R.id.lightseekbar);


        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
      init();
    }
  private void init() {

        arrayList = getActivity().getIntent().getStringArrayListExtra("sn");
        secckBarWhitecolorLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
          @Override
          public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
              Threa threa = new Threa(new Object(), i);
              Thread thread = new Thread(threa);
              thread.start();

          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {

          }

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {

          }
      });
      lightseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                nProgressRed3 = 0;
                nProgressGreen3 = 0;
                nProgressBlue3 = 0;
                nProgressWhite3 = 0;
                nProgressColorHot3 = i;
                int colorHot = 2500 + (i * 500);
                Log.i("123", "colorHot" + colorHot);
                switch (colorHot){
                    case 2500:
                        nProgressRed3 = 255 * CONVERSION_VALUE;
                        nProgressGreen3 = 152 * CONVERSION_VALUE;
                        nProgressBlue3 = 0;
                        nProgressWhite3 = 33 * CONVERSION_VALUE;
                        break;
                    case 3000:
                        nProgressRed3 = 255 * CONVERSION_VALUE;
                        nProgressGreen3 = 167 * CONVERSION_VALUE;
                        nProgressBlue3 = 0;
                        nProgressWhite3 = 59 * CONVERSION_VALUE;
                        break;
                    case 3500:
                        nProgressRed3 = 255 * CONVERSION_VALUE;
                        nProgressGreen3 = 180 * CONVERSION_VALUE;
                        nProgressBlue3 = 0;
                        nProgressWhite3 = 180 * CONVERSION_VALUE;
                        break;
                    case 4000:
                        nProgressRed3 = 255 * CONVERSION_VALUE;
                        nProgressGreen3 = 194 * CONVERSION_VALUE;
                        nProgressBlue3 = 0;
                        nProgressWhite3 = 187 * CONVERSION_VALUE;
                        break;
                    case 4500:
                        nProgressRed3 = 255 * CONVERSION_VALUE;
                        nProgressGreen3 = 207 * CONVERSION_VALUE;
                        nProgressBlue3 = 20 * CONVERSION_VALUE;
                        nProgressWhite3 = 255 * CONVERSION_VALUE;
                        break;
                    case 5000:
                        nProgressRed3 = 255 * CONVERSION_VALUE;
                        nProgressGreen3 = 220 * CONVERSION_VALUE;
                        nProgressBlue3 = 66 * CONVERSION_VALUE;
                        nProgressWhite3 = 255 * CONVERSION_VALUE;
                        break;
                    case 5500:
                        nProgressRed3 = 255 * CONVERSION_VALUE;
                        nProgressGreen3 = 228 * CONVERSION_VALUE;
                        nProgressBlue3 = 110 * CONVERSION_VALUE;
                        nProgressWhite3 = 255 * CONVERSION_VALUE;
                        break;
                    case 6000:
                        nProgressRed3 = 255 * CONVERSION_VALUE;
                        nProgressGreen3 = 233 * CONVERSION_VALUE;
                        nProgressBlue3 = 153 * CONVERSION_VALUE;
                        nProgressWhite3 = 255 * CONVERSION_VALUE;
                        break;
                    case 6500:
                        nProgressRed3 = 255 * CONVERSION_VALUE;
                        nProgressGreen3 = 235 * CONVERSION_VALUE;
                        nProgressBlue3 = 192 * CONVERSION_VALUE;
                        nProgressWhite3 = 255 * CONVERSION_VALUE;
                        break;
                    case 7000:
                        nProgressRed3 = 255 * CONVERSION_VALUE;
                        nProgressGreen3 = 241 * CONVERSION_VALUE;
                        nProgressBlue3 = 229 * CONVERSION_VALUE;
                        nProgressWhite3 = 255 * CONVERSION_VALUE;
                        break;
                    default:
                        break;
                }
//                nProgressRed3= nProgressRed3 * 255 / 4095;
//                nProgressGreen3 = nProgressGreen3 * 255 / 4095;
//                nProgressBlue3 = nProgressBlue3 * 255 / 4095;
//                nProgressWhite3 = nProgressWhite3 * 255 / 4095;
                Threadl threadl = new Threadl(new Object(), i,nProgressRed3,nProgressGreen3,nProgressBlue3,nProgressWhite3);
                Thread thread = new Thread(threadl);
                thread.start();

            }



            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

            }
    public class Threadl implements Runnable {
        Object lock;
        int i;
        private int nProgressRed3;
        private int nProgressGreen3;
        private int nProgressBlue3;
        private int nProgressWhite3;
        public Threadl(Object lock,int i,int nProgressRed3,int nProgressGreen3 ,int nProgressBlue3,int nProgressWhite3 ) {
            this.lock = lock;
            this.i = i;
            this.nProgressRed3 = nProgressRed3;
            this.nProgressGreen3 = nProgressGreen3;
            this.nProgressBlue3 = nProgressBlue3;
            this.nProgressWhite3 = nProgressWhite3;
        }
        public void run() {
            synchronized(lock){
                if (arrayList != null) {
                    for (String s : arrayList) {
                        Socket device = GlobalApplication.getInstance().getDevice(s);
                        if (device != null) {
                            if (s.contains("")){
                                //对应线条灯的sn
                            }else {
                                GrandarUtils.sendLightCtr(nProgressRed3, nProgressGreen3, nProgressBlue3, nProgressWhite3, s);
                            }

                        }
                    }
                }

            }
        }
    }

}
