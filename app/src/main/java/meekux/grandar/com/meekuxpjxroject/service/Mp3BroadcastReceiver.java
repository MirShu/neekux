package meekux.grandar.com.meekuxpjxroject.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.socks.library.KLog;

/**
 * Author:SeanLim
 * Created by Time on: 2017/9/12
 * 类名：Mp3BroadcastReceiver
 */
public class Mp3BroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int playingState = intent.getExtras().getInt("playingState");
        KLog.e("Mp3BroadcastReceiver:" + playingState);
        //Receiver1接收到广播后中断广播
    }
}
