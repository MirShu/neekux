package meekux.grandar.com.meekuxpjxroject.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.DeviceDbUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.activity.FaceDetectActivity;
import meekux.grandar.com.meekuxpjxroject.adapter.SonsorListViewAdapter;
import meekux.grandar.com.meekuxpjxroject.entity.Sonsorroom;
import meekux.grandar.com.meekuxpjxroject.utils.GetByteArraylist;
import meekux.grandar.com.meekuxpjxroject.utils.LogUtils;
import meekux.grandar.com.meekuxpjxroject.utils.StringgetByte;
import meekux.grandar.com.meekuxpjxroject.view.VoiceBean;


/**
 * Created by @author:xuqunwang on 17/1/4.
 * desceription:
 */
public class SensorFragment extends Fragment {
    private View view;
    private ListView sonsorlistview;
    private ArrayList<Sonsorroom> sonsorroo;
    private SonsorListViewAdapter adapter;
    private ArrayList<String> sn;
    private Intent intent1;
    public SensorFragment(ArrayList<String> sn) {
        this.sn = sn;
    }

    String st = "";
    private Boolean close = false;
    private byte[] bytes = {(byte) 0xAA, (byte) 0x55, (byte) 'm', (byte) 'e', (byte) 0x01, (byte) 0x01};
    private StringBuffer mBuffer;
    private TextView tv;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        intent1 = new Intent();
        inits();
        view = inflater.inflate(R.layout.senser, null);
        sonsorlistview = view.findViewById(R.id.sonsorlistview);
        adapter = new SonsorListViewAdapter(getActivity(), sonsorroo);
        adapter.setSenImp(new SonsorListViewAdapter.SensorServiceImp() {
            //视觉的位置
            @Override
            public void lightClick(final int i, View view) {
                if (i == 0) {
                    startActivity(new Intent(getActivity(), FaceDetectActivity.class));
                }
            }

            //声音的位置
            @Override
            public void volClick(final int i) {
                if (i == 0) {
                    faceInitView();
                }
            }

        });
        sonsorlistview.setAdapter(adapter);
        tv = view.findViewById(R.id.tv_translate_text);
        SpeechUtility.createUtility(getActivity(), "appid=" + getString(R.string.app_id));
        return view;
    }

    private void faceInitView() {
        RecognizerDialog mDialog = new RecognizerDialog(getActivity(), null);
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        mBuffer = new StringBuffer();
        mDialog.setListener(mRecognizerDialogListener);
        mDialog.show();
    }


    RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            String result = results.getResultString();//语音听写的结果

            String resultString = processData(result);

            mBuffer.append(resultString);

            if (isLast) {
                //话已经说完了
                String finalResult = mBuffer.toString();
                System.out.println("解析结果:" + finalResult);
                tv.setText(finalResult);

            }

        }

        @Override
        public void onError(SpeechError error) {

        }
    };

    //解析json
    protected String processData(String result) {
        Gson gson = new Gson();
        VoiceBean voiceBean = gson.fromJson(result, VoiceBean.class);

        StringBuffer sb = new StringBuffer();

        ArrayList<VoiceBean.WsBean> ws = voiceBean.ws;
        for (VoiceBean.WsBean wsBean : ws) {
            String word = wsBean.cw.get(0).w;
            sb.append(word);
        }

        return sb.toString();
    }

    private void inits() {
        sonsorroo = new ArrayList<>();
        List<MyDeviceBean> myDeviceBeen = DeviceDbUtil.getInstance().queryTimeAll();
        ArrayList<String> arrayList = new ArrayList<>();
        for (MyDeviceBean DeviceBean : myDeviceBeen) {
            arrayList.add(DeviceBean.getSn());
        }
        Sonsorroom sonsorroom1 = new Sonsorroom("客厅", "(10人)", arrayList);
        Sonsorroom sonsorroom2 = new Sonsorroom("餐厅", "", arrayList);
        Sonsorroom sonsorroom3 = new Sonsorroom("主卧", "", arrayList);
        sonsorroo.add(sonsorroom1);
        sonsorroo.add(sonsorroom2);
        sonsorroo.add(sonsorroom3);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}
