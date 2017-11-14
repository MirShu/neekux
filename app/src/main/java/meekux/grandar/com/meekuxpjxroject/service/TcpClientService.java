package meekux.grandar.com.meekuxpjxroject.service;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.text.format.Time;
import android.util.Log;

import com.socks.library.KLog;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MarketMusicBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.TimeBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.MarketMusicDbUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import meekux.grandar.com.meekuxpjxroject.GlobalApplication;
import meekux.grandar.com.meekuxpjxroject.MainActivity;
import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.songdata.database.Constant;
import meekux.grandar.com.meekuxpjxroject.songdata.database.FileHelper;
import meekux.grandar.com.meekuxpjxroject.songdata.database.GrandarLogUtils;
import meekux.grandar.com.meekuxpjxroject.songdata.database.GrandarUtils;
import meekux.grandar.com.meekuxpjxroject.songdata.database.MyConstant;
import meekux.grandar.com.meekuxpjxroject.songdata.database.MyUtil;
import meekux.grandar.com.meekuxpjxroject.songdata.database.SongLightStore;
import meekux.grandar.com.meekuxpjxroject.utils.DateUtil;
import meekux.grandar.com.meekuxpjxroject.utils.LogUtils;
import meekuxpjxroject.AIDLWifiInterface;

public class TcpClientService extends Service {
    private static final String TAG = "TcpClientService";
    private Boolean bRun = true;  //终止接收循环
    private Socket mClientSocket = null;
    private final Object mClientSocketLock = new Object();
    private final Object mPlaySongLightLock = new Object(); // 播放光曲相关参数同步锁
    Timer setVolumeTimer = null;
    private Timer sendLightTimer = null;
    private MediaPlayer mediaPlayer = null;
    private boolean needStopCreateMediaFileThread = false;
    String songLightFile;
    private static Handler playMp3Handler = new Handler();
    private boolean needPlayMp3 = false;
    private long mediaPlayerfile_size = 0;
    ClientProcess mThreadClient = null;
    Thread mUdpThreadClient = null;
    private int mRemoteConnectState = 0; // 0 Fail ,1 Success, 2 Cancel 3
    private int filePathCount = 0;
    private DatagramSocket mUdpSocket = null;
    private Boolean disConnectByUser = false;
    private Timer mSendTcpPacketTimer = null; //定时发送tcp包的timer
    private ReceiverBroadcast broadcast = null;
    private IntentFilter mFilter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        // 应用退出关闭服务注册接收广播
        broadcast = new ReceiverBroadcast();
        mFilter = new IntentFilter();
        mFilter.addAction(MyConstant.mainExit);
        registerReceiver(broadcast, mFilter);
        KLog.e("----服务启动---------");
        //判断连接是否正常
        mSendTcpPacketTimer = new Timer();
        mSendTcpPacketTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                byte[] sendbuff = new byte[1];
                sendbuff[0] = 0x00;
                sendtoServerTask(sendbuff);
            }
        }, 15 * 1000, 15 * 1000);
        bindPlayLightSongListService();
//         StartSendThread();
    }

    // 启动客户端
    class ClientProcess extends Thread {
        String srvIp = null;
        Socket sockets;
        String sN;
        int retryTimes = 0;
        int reConnectTimes = 5; //断连后重连次数为5
        boolean needConnect = false;
        /* 从读取到的buff中提取正确的数据帧 处理一次发来很多数据帧的情况 或者一个数据帧分两次发来的情况 */
        final int RECEIVE_BUF_MAX_LEN = 2048; // 处理接收数据的buffer大小
        private byte[] mReadDataBuf = new byte[RECEIVE_BUF_MAX_LEN]; // 处理接收数据的buffer
        private int mFreeBufAddress = 0; // mReadDataBuf 的空闲buf的开始地址

        public ClientProcess(String ip) {
            srvIp = ip;
            retryTimes = 0;
        }

        public ClientProcess(Socket socket, String ip, int times, boolean Connect, String sn) {
            srvIp = ip;
            retryTimes = times;
            needConnect = Connect;
            sockets = socket;
            sN = sn;
        }

        @Override
        public void run() {
            try {
                LogUtils.e(TAG, "客户端开始连接..." + srvIp + " mFreeBufAddress:"
                        + mFreeBufAddress);
                if (needConnect) {
                    SocketAddress remoteAddr = new InetSocketAddress(srvIp,
                            MyConstant.TCPSERVERPORT);
//                    if (!sockets.getTcpNoDelay()) {
//                        sockets.setTcpNoDelay(true);
//                    }
//                    try {
//                        sockets.connect(remoteAddr, 5000);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    sockets.setTcpNoDelay(true);
                    sockets.setSendBufferSize(4096);
                    sockets.setReceiveBufferSize(4096);
                    // 作用：每隔一段时间检查服务器是否处于活动状态，如果服务器端长时间没响应，自动关闭客户端socket
                    // 防止服务器端无效时，客户端长时间处于连接状态
                    sockets.setKeepAlive(true);
                    sockets.connect(remoteAddr, 5000);
                }
                disConnectByUser = false;
                sendBroadcast(MyConstant.switchtomain, "");
                mRemoteConnectState = 1;
                KLog.e("客户端连接成功!");
                KLog.e("socket_save---->" + sockets + "--" + sN);
                GlobalApplication.getInstance().addDevice(sN, sockets);
                String sn_id = sN.substring(0, 4);
                GlobalApplication.getInstance().addDevice(sn_id, sockets);
                KLog.e("型号======================》" + sn_id);
                Socket socket = GlobalApplication.getInstance().getDevice(sN);
                KLog.a(socket);
                StartSendThread();
                clientThreadProcess(socket);
                // 启动发送线程
            } catch (IOException e) {
                String sn = sN;
                KLog.e("客户端连接失败---------------sn:" + sn);
                GlobalApplication.getInstance().removeDevice(sn);
                completeCloseSocket();
                sendSocketClosetBC();
            }
        }

        // 客户端接收线程
        private void clientThreadProcess(Socket socket) {
            try {
                // 绑定播放光曲列表服务
                bindPlayLightSongListService();
                KLog.e("---接收线程启动----");
                int temp = 0;
                InputStream inputStream = null;
                synchronized (mClientSocketLock) {
                    inputStream = socket.getInputStream();
                }
                byte[] buffer = new byte[MyConstant.TCP_BUFFSIZE];
                KLog.e("---接收inputStream----" + inputStream + "-----" +
                        "buffer---" + (temp = inputStream.read(buffer)));
                socket.setSoTimeout(0);
                while ((inputStream != null) && bRun && (temp = inputStream.read(buffer)) != -1) {
                    if (true) {
                        StringBuilder msg = new StringBuilder();
                        for (int i = 0; i < temp; i++) {
                            msg.append(String.format(" %2x", buffer[i]));
                        }
                        KLog.e("client----->buffer[]:" + msg.toString());
                    }
                    processReadData(buffer, temp);
                }
            } catch (IOException e) {
                KLog.e(e.getMessage());
            }
        }

        private void processReadData(byte[] buffer, int len) {
            // buf 已满，将buf最前面的抛掉 保证能保存后面收到的
            if (mFreeBufAddress + len > RECEIVE_BUF_MAX_LEN) {
                GrandarLogUtils.i(TAG, "processReadData buf 已满  mFreeBufAddress="
                        + mFreeBufAddress + " len=" + len);
                byte[] tempBuf = new byte[RECEIVE_BUF_MAX_LEN - len];
                System.arraycopy(mReadDataBuf, mFreeBufAddress, tempBuf, 0,
                        RECEIVE_BUF_MAX_LEN - len);
                System.arraycopy(tempBuf, 0, mReadDataBuf, 0, tempBuf.length);
                mFreeBufAddress = tempBuf.length;
            }
            System.arraycopy(buffer, 0, mReadDataBuf, mFreeBufAddress, len);
            mFreeBufAddress = mFreeBufAddress + len;

            for (int i = 0; i < mFreeBufAddress; i++) {
                byte bCheck = 0;
                int nStep = 0;
                short dataLength = 0;
            /* 判断帧头 */
                if ((mFreeBufAddress - i) >= 4 && mReadDataBuf[i] == (byte) 0xaa
                        && mReadDataBuf[i + 1] == (byte) 0x55
                        && mReadDataBuf[i + 2] == (byte) 'I'
                        && mReadDataBuf[i + 3] == (byte) 'G') {
                    for (int j = i; j < i + FRAME_HEAD_LENGTH; j++) {
                        bCheck += mReadDataBuf[j];
                    }
                    i = i + FRAME_HEAD_LENGTH;
                    nStep = 1;
                    GrandarLogUtils.i(TAG, "processReadData 帧头 i=" + i);
                }

			/* 读取数据长度，对比加和校验 */
                if (nStep == 1) {
                    if ((mFreeBufAddress - i) < 2) {
                        nStep = 0;
                        i--;
                    } else {
                        short low_len = (short) (mReadDataBuf[i] >= 0 ? mReadDataBuf[i]
                                : (mReadDataBuf[i] + 256));
                        dataLength = low_len;
                        dataLength += (mReadDataBuf[i + 1] << 8) & 0xFF00;

                        GrandarLogUtils.i(TAG,
                                "processReadData nStep==1 dataLength=" + dataLength
                                        + " mFreeBufAddress=" + mFreeBufAddress);
                        if (dataLength < 0) {
                            nStep = 0;
                            i--;
                        } else if (mFreeBufAddress >= (i + DATA_LENGTH + dataLength)) {
                            for (int j = i; j < (i + DATA_LENGTH + dataLength - 1); j++) {
                                bCheck += mReadDataBuf[j];
                            }
                            GrandarLogUtils.i(TAG,
                                    "processReadData nStep==1 bCheck=" + bCheck);
                            if (bCheck == mReadDataBuf[i + DATA_LENGTH + dataLength
                                    - 1]) {
                                nStep = 2;
                                i = i + DATA_LENGTH;
                            } else {
                                // 校验和不对 抛掉帧头
                                GrandarLogUtils
                                        .i(TAG, "processReadData 校验和不正确 i=" + i
                                                + " mFreeBufAddress="
                                                + mFreeBufAddress);
                                if (mFreeBufAddress <= i) {
                                    mFreeBufAddress = 0;
                                } else {
                                    byte[] tempBuf = new byte[mFreeBufAddress - i];
                                    System.arraycopy(mReadDataBuf, i, tempBuf, 0,
                                            mFreeBufAddress - i);
                                    System.arraycopy(tempBuf, 0, mReadDataBuf, 0,
                                            tempBuf.length);
                                    mFreeBufAddress = tempBuf.length;
                                }
                                nStep = 0;
                                i = -1;
                            }
                        } else {
                            nStep = 0;
                            i--;
                        }
                    }
                    GrandarLogUtils.i(TAG, "processReadData 数据长度 dataLength="
                            + dataLength + " nStep=" + nStep + " i=" + i);
                }

			/* 根据命令字做相应操作 */
                if (nStep == 2) {
                    nStep = 0;
                    bCheck = 0;

                    String msg;
                    Time time = new Time();
                    time.setToNow();

                    if (mReadDataBuf[i] == (byte) 0xA0) {
                        if (mReadDataBuf[i + 1] == (byte) 0x01) {
                            // 灯体通知播放器，请求发送光曲路径
                            setKeepOnSendFilesList(true);
                            i = i + dataLength;
                        } else if (mReadDataBuf[i + 1] == (byte) 0x02) {
                            // 灯体通知播放器，请求发送列表中第N首光曲数据
                            short serial_num = (short) (mReadDataBuf[i + CMD_LENGTH
                                    + KEEP_WORD_LENGTH] >= 0 ? mReadDataBuf[i
                                    + CMD_LENGTH + KEEP_WORD_LENGTH]
                                    : (mReadDataBuf[i + CMD_LENGTH
                                    + KEEP_WORD_LENGTH] + 256));
                            GrandarLogUtils.i(TAG,
                                    "processReadData 发送列表中第N首 serial_num1="
                                            + serial_num);
                            serial_num += (mReadDataBuf[i + CMD_LENGTH
                                    + KEEP_WORD_LENGTH + 1] << 8) & 0xFF00;
                            GrandarLogUtils.i(TAG,
                                    "processReadData 发送列表中第N首 serial_num2="
                                            + serial_num);

                            // 如正在发送光曲列表 则不处理
                            if (!getNeedSendFilesList()) {
                                if (serial_num == 0) // 灯体端进入流媒体模式
                                {
                                    setInStreamMediaMode(true);
                                    sendInStreamMediaModeBroadcast();
                                    serial_num = 1; // 如是用流媒体发送文件 则直接开始发送文件
                                }
                                SendMp3Init(serial_num);
                            }
                            i = i + dataLength;
                        } else if (mReadDataBuf[i + 1] == (byte) 0x04) {
                            // 灯体端停止播放，并将当前所播放光曲的序号和位置发送给播放器

                            i = i + dataLength;

                        } else if (mReadDataBuf[i + 1] == (byte) 0x05) {
                            // 灯体通知播放器，灯体端音乐播放卡

                            i = i + dataLength;

                        } else if (mReadDataBuf[i + 1] == (byte) 0x06) {
                            // 灯体基础信息
                            byte[] info = new byte[dataLength - CMD_LENGTH
                                    - KEEP_WORD_LENGTH - 1];
                            System.arraycopy(mReadDataBuf, i + CMD_LENGTH
                                    + KEEP_WORD_LENGTH, info, 0, dataLength
                                    - CMD_LENGTH - KEEP_WORD_LENGTH - 1);
                            sendIgooBaseInfoBroadcast(info);
                            i = i + dataLength;

                        } else if (mReadDataBuf[i + 1] == (byte) 0x07) {
                            // 灯体版本号信息
                            byte[] version = new byte[dataLength - CMD_LENGTH
                                    - KEEP_WORD_LENGTH - 1];
                            System.arraycopy(mReadDataBuf, i + CMD_LENGTH
                                    + KEEP_WORD_LENGTH, version, 0, dataLength
                                    - CMD_LENGTH - KEEP_WORD_LENGTH - 1);
                            sendIgooSoftwareVersionBroadcast(version);
                            i = i + dataLength;

                        } else if (mReadDataBuf[i + 1] == (byte) 0x0A) {
                            // （当前播放歌曲的序号）
                            short order = (short) (mReadDataBuf[i + CMD_LENGTH
                                    + KEEP_WORD_LENGTH] >= 0 ? mReadDataBuf[i
                                    + CMD_LENGTH + KEEP_WORD_LENGTH]
                                    : (mReadDataBuf[i + CMD_LENGTH
                                    + KEEP_WORD_LENGTH] + 256));
                            order += (mReadDataBuf[i + CMD_LENGTH
                                    + KEEP_WORD_LENGTH + 1] << 8) & 0xFF00;
                            KLog.e("processReadData 告知当前播放歌曲的序号 serial_num2="
                                    + order);
                            GrandarUtils.savePlayingIdx(getApplicationContext(),
                                    order);
                            byte[] sendBuf = MyUtil.intToByte(order);
//                            RemoteControlUtils
//                                    .sendRemoteControlFrame(MyConstant.CMDA1,
//                                            MyConstant.CMD_TYPE1, sendBuf);
                            sendPlayingChangeBroadcast(order);
                            i = i + dataLength;
                        } else if (mReadDataBuf[i + 1] == (byte) 0x0F) {
                            // 灯体端光曲数据缓冲区满 请求延迟发送后续光曲数据
                            setDelaySendSongLightFlag(true);
                            i = i + dataLength;
                        } else if (mReadDataBuf[i + 1] == (byte) 0x0D) {
                            // 升级灯体端程序请求应答
                            if (mReadDataBuf[i + 1 + KEEP_WORD_LENGTH + 1] == (byte) 0x01) {
                                setInUpdateIgooSoftware(true);
                                sendUpdateIgooSoftwareRspBroadcast();
                            }
                            i = i + dataLength;
                        } else if (mReadDataBuf[i + 1] == (byte) 0x0E) {
                            // 灯体端接收升级程序应答
                            byte[] state = new byte[dataLength - CMD_LENGTH
                                    - KEEP_WORD_LENGTH - 1];
                            System.arraycopy(mReadDataBuf, i + CMD_LENGTH
                                    + KEEP_WORD_LENGTH, state, 0, dataLength
                                    - CMD_LENGTH - KEEP_WORD_LENGTH - 1);
                            sendUpdateIgooSoftwareStateBroadcast(state);
                            i = i + dataLength;
                        } else if (mReadDataBuf[i + 1] == (byte) 0x10) {
                            // 得到灯体端摇一摇功能当前状态
                            byte[] state = new byte[dataLength - CMD_LENGTH
                                    - KEEP_WORD_LENGTH - 1];
                            System.arraycopy(mReadDataBuf, i + CMD_LENGTH
                                    + KEEP_WORD_LENGTH, state, 0, dataLength
                                    - CMD_LENGTH - KEEP_WORD_LENGTH - 1);
                            sendIgooShakeStateBroadcast(state);
                            i = i + dataLength;
                        }

                        // 抛掉处理完的数据
                        GrandarLogUtils.i(TAG, "processReadData 抛掉处理完的数据 i=" + i
                                + " mFreeBufAddress=" + mFreeBufAddress);
                        if (mFreeBufAddress <= i) {
                            mFreeBufAddress = 0;
                        } else {
                            byte[] tempBuf = new byte[mFreeBufAddress - i];
                            System.arraycopy(mReadDataBuf, i, tempBuf, 0,
                                    mFreeBufAddress - i);
                            System.arraycopy(tempBuf, 0, mReadDataBuf, 0,
                                    tempBuf.length);
                            mFreeBufAddress = tempBuf.length;
                        }
                        nStep = 0;
                        i = -1;
                        GrandarLogUtils.i(TAG, "processReadData nStep==2 i=" + i);
                    } else {
                        i--;
                    }
                }
            }
            acquireDelayWakeLock();
            releaseReceiveWakeLock();
        }

        BufferedOutputStream mfbos;

        public void processReadDataSnd(byte[] buffer) throws IOException {
            analyzeServerCmdData(buffer);
        }

    }

    //广播区
    private void sendUpdateIgooSoftwareStateBroadcast(byte[] state) {
        Intent sendIntent = new Intent(
                Constant.ACTION_UPDATE_IGOO_SOFTWARE_STATE);
        sendIntent.putExtra("state", state);
        sendIntent.putExtra("apkname", getSendingFileName());
        sendBroadcast(sendIntent);
    }

    private void sendIgooBaseInfoBroadcast(byte[] info) {
        Intent sendIntent = new Intent(Constant.ACTION_IGOO_BASE_INFO_RSP);
        sendIntent.putExtra("info", info);
        sendBroadcast(sendIntent);
    }

    private void sendUpdateIgooSoftwareRspBroadcast() {
        Intent sendIntent = new Intent(Constant.ACTION_UPDATE_IGOO_SOFTWARE_RSP);
        sendBroadcast(sendIntent);
    }

    private void sendIgooShakeStateBroadcast(byte[] state) {
        Intent sendIntent = new Intent(Constant.ACTION_IGOO_SHAKE_STATE);
        sendIntent.putExtra("state", state);
        sendBroadcast(sendIntent);
    }

    private void sendPlayingChangeBroadcast(int order) {
        Intent sendIntent = new Intent(Constant.ACTION_IGOO_INFO_PLAYING_CHANGE);
        sendIntent.putExtra("Order", order);
        sendBroadcast(sendIntent);
    }

    private void sendInStreamMediaModeBroadcast() {
        Intent sendIntent = new Intent(Constant.ACTION_IGOO_INFO_PLAYING_CHANGE);
        sendBroadcast(sendIntent);
    }

    private void sendIgooSoftwareVersionBroadcast(byte[] version) {
        Intent sendIntent = new Intent(
                Constant.ACTION_IGOO_SOFTWARE_VERSION_RSP);
        sendIntent.putExtra("version", version);
        sendBroadcast(sendIntent);
    }

    //休眠区
    private void acquireDelayWakeLock() {
        if (mDelayWakeLock == null) {
            PowerManager pm = (PowerManager) this
                    .getSystemService(Context.POWER_SERVICE);
            mDelayWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "BtService");
            mDelayWakeLock.setReferenceCounted(false);
        }
        mDelayWakeLock.acquire(14000);
    }

    private void releaseReceiveWakeLock() {
        if (null != mReceiveWakeLock && mReceiveWakeLock.isHeld()) {
            // GrandarLogUtils.e(TAG, "release receive wake lock");
            mReceiveWakeLock.release();
            mReceiveWakeLock = null;
        }
    }

    // 客户端向服务器发送数据
    public synchronized void sendtoServer(byte[] buff) {
        OutputStream out = null;
        try {
            if (mClientSocket != null) {
                out = mClientSocket.getOutputStream();
                out.write(buff);
                out.flush();
                if (LogUtils.DEBUG_ON_REMOTECONTROL) {
                    StringBuilder msg = new StringBuilder();
                    for (int i = 0; i < buff.length; i++) {
                        msg.append(String.format(" %2x", buff[i]));
                    }
                    LogUtils.e(TAG, "--->收到udp内容send:" + msg.toString());
                }
            }
        } catch (IOException e) {
            LogUtils.e(TAG, "sendtoServer IOException e:" + e);
        }
    }

    // /////////////////////////////////////////////////与其它Activity交互//////////////////////////////////////////////////////////////
    private void sendBroadcast(String action, String info) {
        Intent intent = new Intent();
        intent.putExtra("msg", info);
        intent.setAction(action);
        sendBroadcast(intent);
    }

    private void sendBroadcast(String action, byte info) {
        LogUtils.d(TAG, "sendBroadcast info:" + info);
        Intent intent = new Intent();
        intent.putExtra(MyConstant.ACTION_RC_SERVER_CMD_PARAM, info);
        intent.setAction(action);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bRun = false;
        //死循环终止
        if (mSendTcpPacketTimer != null) {
            mSendTcpPacketTimer.cancel();
            mSendTcpPacketTimer = null;
        }
        completeCloseSocket();
        if (mUdpSocket != null) {
            mUdpSocket.close();
        }
        KLog.e("服务销毁", "服务销毁");
        unBindPlayLightSongListService();
        stopPlayLightSongListService();
    }

    public class TcpClientServiceBinder extends AIDLWifiInterface.Stub {

        public void bSendtoServer(byte[] buff) {

            sendtoServerTask(buff);
        }

        public void bSendUdptoServer(byte[] buff) {
            sendUdptoServer(buff);
        }

        public String getRemoteConnectState() {
            if (mRemoteConnectState == 0) {
                // sendBtStateChangeBroadcast(Constant.FAIL_ACTION);
                return "Fail";
            } else if (mRemoteConnectState == 1) {
                // sendBtStateChangeBroadcast(Constant.SUCCESS_ACTION);
                return "Success";
            } else if (mRemoteConnectState == 2) {
                // sendBtStateChangeBroadcast(Constant.CANCEL_ACTION);
                return "Cancel";
            } else if (mRemoteConnectState == 3) {
                // sendBtStateChangeBroadcast(Constant.CONNECTING_ACTION);
                return "Connecting";
            }

            return "Fail";
        }

        public void bStartTcpClient(List<MyDeviceBean> data,
                                    boolean needConnect) {
            startTcpClient(data, needConnect);
        }

        public void bStartTcpClients(String sn, String ip,
                                     boolean needConnect) {
//            startTcpClients(sn,ip, needConnect);
        }

        /* 设置灯光整体亮度 */
        public void setWholeLightness(int num) {
            MainActivity.nProgressLight = num;
            GrandarUtils.saveLightBaseInfo(getApplicationContext(), num);

//            if (mMusicService.isMp3Playing()
//                    && (!GrandarUtils.getPauseFlag())) {
//                LogUtils.e(TAG, "setWholeLightness return");
//                return;
//            }
            byte[] data = new byte[1];
            data[0] = (byte) num;
            SendFrameCmd((byte) 0x01, (byte) 0x04, data);

        }

        //请求闹钟列表
        public void
        setAlarmClock(List<TimeBean> timeSetBeen) {
            int week = 0;
            int turnon;
            int brightness;
            int voice;
            int hours;
            int minutes;
            int sleep;
            int isturn;
            byte[] md5code;
            List<Integer> list = new ArrayList<>();

            //列表个数
            byte[] data = new byte[27];
            //start or end
            int len = data.length;
            byte[] bytes = new byte[len * timeSetBeen.size() + 1];
            bytes[0] = (byte) timeSetBeen.size();
            for (int i = 0; i < timeSetBeen.size(); i++) {
                int size = timeSetBeen.get(i).getRepeat().length();
                if (timeSetBeen.get(i).getRepeat().equals(getResources().getString(R.string.everyday))) {
                    week = 127;
                } else if (timeSetBeen.get(i).getRepeat().equals(getResources().getString(R.string.only_one))) {
                    week = 0;
                } else {
                    for (int j = 0; j < size; j++) {
                        char content = timeSetBeen.get(i).getRepeat().charAt(j);
                        String str = "周" + String.valueOf(content);
                        if (str.equals(getResources().getString(R.string.sunday))) {
                            list.add(64);
                        } else if (str.equals(getResources().getString(R.string.monday))) {
                            list.add(1);
                        } else if (str.equals(getResources().getString(R.string.tuesday))) {
                            list.add(2);
                        } else if (str.equals(getResources().getString(R.string.wednesday))) {
                            list.add(4);
                        } else if (str.equals(getResources().getString(R.string.thursday))) {
                            list.add(8);
                        } else if (str.equals(getResources().getString(R.string.friday))) {
                            list.add(16);
                        } else if (str.equals(getResources().getString(R.string.saturday))) {
                            list.add(32);
                        }

                    }
                    for (int k = 0; k < list.size(); k++) {
                        week += list.get(k);
                    }
                    KLog.e(list.toString());
                    KLog.e(week);


                }
                KLog.e(timeSetBeen.get(i).getRepeat());
                if (timeSetBeen.get(i).getTurnon()) {
                    turnon = 0x01;
                } else {
                    turnon = 0x02;
                }
                if (timeSetBeen.get(i).getBrightness() == null) {
                    brightness = 0x00;
                } else {
                    String str = timeSetBeen.get(i).getBrightness().replace("%", "");
                    brightness = Integer.parseInt(str);
                }
                if (timeSetBeen.get(i).getVoice() == null) {
                    voice = 0x00;
                } else {
                    String str = timeSetBeen.get(i).getVoice().replace("%", "");
                    voice = Integer.parseInt(str);
                }
                KLog.e(brightness + voice);
                hours = timeSetBeen.get(i).getHours();
                minutes = timeSetBeen.get(i).getMinutes();
                //小睡功能保留
//                if (timeSetBeen.get(i).getSleep()) {
//                    sleep = true;
//                } else {
//                    sleep = false;
//                }

                if (timeSetBeen.get(i).getData() == null) {
                    md5code = new byte[]{0, 3, 0, 7, 1, 0, 5, 1, 0, 2, 6, 2, 5, 8, 6, 2};
                } else {
                    md5code = timeSetBeen.get(i).getData();

                }

                if (timeSetBeen.get(i).getIsturn()) {
                    isturn = 0x01;
                } else {
                    isturn = 0x02;
                }

                if (i == 0) {
                    data[0] = 0x00;
                } else if (i == timeSetBeen.size() - 1) {
                    data[0] = 0x02;
                } else {
                    data[0] = 0x01;
                }

                //列表id
                data[1] = (byte) (i + 1);
                //星期
                data[2] = (byte) week;
                //时
                data[3] = (byte) hours;
                //分
                data[4] = (byte) minutes;
                //关灯 or 光曲
                data[5] = (byte) turnon;
                //亮度
                data[6] = (byte) brightness;
                //音量
                data[7] = (byte) voice;
                //光曲MD5效验码


                data[8] = md5code[0];
                data[9] = md5code[1];
                data[10] = md5code[2];
                data[11] = md5code[3];
                data[12] = md5code[4];
                data[13] = md5code[5];
                data[14] = md5code[6];
                data[15] = md5code[7];
                data[16] = md5code[8];
                data[17] = md5code[9];
                data[18] = md5code[10];
                data[19] = md5code[11];
                data[20] = md5code[12];
                data[21] = md5code[13];
                data[22] = md5code[14];
                data[23] = md5code[15];

                //光效标识
                data[24] = 0x11;
                //小睡
                data[25] = 0x00;
                //开关
                data[26] = (byte) isturn;
                System.arraycopy(data, 0, bytes, len * i + 1, len);
                list.clear();
                week = 0;
            }


            SendFrameCmd((byte) 0x01, (byte) 0x21, bytes);
            KLog.e("21发送成功" + Arrays.toString(bytes));

        }

        //请求闹钟列表
        public void requestAlarmClock() {
            int week = 0;
            if (DateUtil.getWeek().equals(getResources().getString(R.string.monday))) {
                week = 1;
            } else if (DateUtil.getWeek().equals(getResources().getString(R.string.tuesday))) {
                week = 2;
            } else if (DateUtil.getWeek().equals(getResources().getString(R.string.wednesday))) {
                week = 3;
            } else if (DateUtil.getWeek().equals(getResources().getString(R.string.thursday))) {
                week = 4;
            } else if (DateUtil.getWeek().equals(getResources().getString(R.string.friday))) {
                week = 5;
            } else if (DateUtil.getWeek().equals(getResources().getString(R.string.saturday))) {
                week = 6;
            } else if (DateUtil.getWeek().equals(getResources().getString(R.string.sunday))) {
                week = 7;
            }
            byte[] data = new byte[4];
            data[0] = (byte) week;
            data[1] = (byte) DateUtil.getHours();
            data[2] = (byte) DateUtil.getMinutes();
            data[3] = (byte) DateUtil.getSecond();
            SendFrameCmd((byte) 0x01, (byte) 0x20, data);
            KLog.e("20指令发送成功" + Arrays.toString(data));

        }

        public void setMusicStart() {
            SendFrameCmd((byte) 0x01, (byte) 0x13, null);
        }

        public void setSingleLight() {
            SendFrameCmd((byte) 0x01, (byte) 0x05, null);
        }

        /* 设置音量 */
        public void setVolumeness(int num) {
            if (num >= Constant.ConValue.MIN_IGOO_VOLUME
                    && num <= Constant.ConValue.MAX_IGOO_VOLUME) {
                MainActivity.nProgressVolume = num;
                GrandarUtils.saveVolumeBaseInfo(getApplicationContext(), num);
                byte[] data = new byte[1];
                data[0] = (byte) num;
                // 插入耳机调节音量的处理
//                if (mIsHeadsetOn && isMp3Playing()) {
//                    int phoneVol = (int) data[0] / 10;
//                    setMusicStreamVolume(phoneVol);
//                    data[0] = 0x00;
//                }
                SendFrameCmd((byte) 0x01, (byte) 0x03, data);
            }
        }

        /* 暂停播放 */
        public void stopMusic() {
            SendFrameCmd((byte) 0x01, (byte) 0x16, null);
        }

        /* 继续播放 */
        public void rePlayMusic() {
            SendFrameCmd((byte) 0x01, (byte) 0x17, null);
        }

        /* 循环播放文件列表 */
        public void sendFilesListToDevice(String filesListName) {
            GrandarUtils.setPauseFlag(false);
            SendFilesListToDeviceTask Task = new SendFilesListToDeviceTask(
                    filesListName, 0);
            Task.execute();
        }

        public void sendFilesListToDevice(String filesListName, int beginIndex) {
            GrandarUtils.setPauseFlag(false);
            SendFilesListToDeviceTask Task = new SendFilesListToDeviceTask(
                    filesListName, beginIndex);
            Task.execute();
        }

        public void stopCurPlaying() {
            if (mMusicService != null) {
                mMusicService.stopMp3Data();
            }
        }

        public void pauseCurPlaying() {
            if (mMusicService != null) {
                mMusicService.pauseMp3Data();
            }
        }

        public void playCurPause() {
            if (mMusicService != null) {
                mMusicService.resumeMp3Data();
            }
        }


        public void sendLightDataTime(byte[] data, int time) {
            byte[] totalData = new byte[1 + 128 + 4];
            totalData[0] = (byte) MainActivity.nProgressLight;
            System.arraycopy(data, 0, totalData, 1, 128);
            // 光效渐变时长
            byte[] timebuf = new byte[4];
            timebuf = MyUtil.intToByte(time >= 0 ? time : 0); // 默认500毫秒
            System.arraycopy(timebuf, 0, totalData, 128 + 1, 4);
            short len = (short) (totalData != null ? totalData.length : 0);
            byte[] buff = new byte[2 + (totalData != null ? totalData.length : 0)
                    + MyConstant.LIGHT_FRAME.length];
            System.arraycopy(MyConstant.LIGHT_FRAME, 0, buff, 0, MyConstant.LIGHT_FRAME.length);
            buff[MyConstant.LIGHT_FRAME.length] = (byte) (len & 0x00FF);
            buff[MyConstant.LIGHT_FRAME.length + 1] = (byte) ((len >> 8) & 0x00FF);
            if (totalData != null && totalData.length > 0) {
                System.arraycopy(totalData, 0, buff, MyConstant.LIGHT_FRAME.length + 2, totalData.length);
            }
            KLog.e("------>" + Arrays.toString(buff));
            StringBuffer msg = new StringBuffer();
            for (int i = 0; i < buff.length; i++) {
                msg.append(String.format(" %2x", buff[i]));
            }
            KLog.e("--->sendUdptoServer:" + msg.toString());
            if (GrandarLogUtils.DEBUG_ON_UDP) {
                KLog.e("sendtoUdpServerTask");
                sendtoUdpServerTask(buff);
            } else {
                KLog.e("sendtoServerTask");
                sendtoServerTask(buff);
            }
        }

        public void sendNightLightToDevice() {
            byte[] data = new byte[1];
            data[0] = (byte) MainActivity.nProgressLight;
            SendFrameCmd((byte) 0x01, (byte) 0x03, data);
        }

        @Override
        public String getString() throws RemoteException {
            return null;
        }


        //该方法是捕获远程服务器端异常
        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            try {
                super.onTransact(code, data, reply, flags);
            } catch (RuntimeException e) {
                Log.e("远程实现出现异常----", "Unexpected remote exception", e);
                throw e;
            }
            //返回true
            return true;
        }

        @Override
        public void sendLightCtrl(int nProgressRed, int nProgressGreen, int nProgressBlue, int nProgressWhite) throws RemoteException {
            byte[] data = new byte[8];
            data[0] = (byte) (0xff & nProgressRed);
            data[1] = (byte) ((0xff00 & nProgressRed) >> 8);
            data[2] = (byte) (0xff & nProgressGreen);
            data[3] = (byte) ((0xff00 & nProgressGreen) >> 8);
            data[4] = (byte) (0xff & nProgressBlue);
            data[5] = (byte) ((0xff00 & nProgressBlue) >> 8);
            data[6] = (byte) (0xff & nProgressWhite);
            data[7] = (byte) ((0xff00 & nProgressWhite) >> 8);

            byte[] totalData = new byte[128 + 4];

            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 8; j++) {
                    totalData[i * 8 + j] = data[j];
                }
            }
            // 光效渐变时长
            byte[] time = new byte[4];
            time = MyUtil.intToByte(500); // 默认500毫秒
            System.arraycopy(time, 0, totalData, 128, 4);
            SendFrameCmd((byte) 0x01, (byte) 0x05, totalData);

        }

        @Override
        public void sendFrame(byte bCmd1, byte bCmd2, byte[] data) throws RemoteException {
            if ((bCmd1 == (byte) 0x01)
                    && (bCmd2 == (byte) 0x05)
                    && (data.length == 128)) {
                //发送单光效
                sendLightDataTime(data, 500);
            } else
                SendFrameCmd(bCmd1, bCmd2, data);
        }

        @Override
        public void sendFileToDevice(String fileName) throws RemoteException {
            GrandarUtils.setPauseFlag(false);
            // SendFileInitFirst(fileName);
        }

        @Override
        public void sendLightCtrlTime(int nProgressRed, int nProgressGreen, int nProgressBlue, int nProgressWhite, int time) throws RemoteException {
            byte[] data = new byte[8];
            data[0] = (byte) (0xff & nProgressRed);
            data[1] = (byte) ((0xff00 & nProgressRed) >> 8);
            data[2] = (byte) (0xff & nProgressGreen);
            data[3] = (byte) ((0xff00 & nProgressGreen) >> 8);
            data[4] = (byte) (0xff & nProgressBlue);
            data[5] = (byte) ((0xff00 & nProgressBlue) >> 8);
            data[6] = (byte) (0xff & nProgressWhite);
            data[7] = (byte) ((0xff00 & nProgressWhite) >> 8);
            byte[] totalData = new byte[128 + 4];
            String msg = null;
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 8; j++) {
                    totalData[i * 8 + j] = data[j];
                    // msg =msg+ String.format(" %2x",totalData[i*8+j]);
                }
            }
            // 光效渐变时长
            byte[] timebuf = new byte[4];
            timebuf = MyUtil.intToByte(time >= 0 ? time : 0); // 默认500毫秒
            System.arraycopy(timebuf, 0, totalData, 128, 4);
            // LogUtils.d(TAG, "sendLightCtrl msg="+msg);
            SendFrameCmd((byte) 0x01, (byte) 0x05, totalData);
        }

        @Override
        public void sendLoopFileToDevice(String fileName) throws RemoteException {
        }

        public void sendOffCmd(byte[] cmd, byte[] data, String sn) throws RemoteException {
            short len = (short) (data != null ? data.length : 0);
            byte[] buff = new byte[2 + (data != null ? data.length : 0)
                    + cmd.length];
            System.arraycopy(cmd, 0, buff, 0, cmd.length);
            buff[cmd.length] = (byte) (len & 0x00FF);
            buff[cmd.length + 1] = (byte) ((len >> 8) & 0x00FF);
            if (data != null && data.length > 0) {
                System.arraycopy(data, 0, buff, cmd.length + 2, data.length);
            }
            Socket socket = GlobalApplication.getInstance().getDevice(sn.substring(0, 4));
            KLog.e(socket);
            KLog.e(Arrays.toString(buff));
            if (socket != null && socket.getInetAddress() != null) {
                try {
                    // DatagramSocket datagramSocket = new DatagramSocket(MyConstant.UDPSERVERPORT);
                    InetAddress server = socket.getInetAddress();
                    // 构造发送的数据包对象
                    DatagramPacket sendDp = new DatagramPacket(buff, buff.length,
                            server, MyConstant.UDPSERVERPORT);
                    // 发送数据
                    if (mUdpSocket != null) {
                        mUdpSocket.send(sendDp);
                    }

                    KLog.e("-------->successful");
                } catch (SocketException e) {
                    KLog.e("send--SocketException-" + e.getMessage());
                } catch (IOException e) {
                    KLog.e("send--SocketException-" + e.getMessage());
                }
            }
        }

        /* 设置baby灯光整体亮度 */
        public void setBabyWholeLightness(int num) {
//            SharedPreferencesUtils.getinstance().setIntValue(IConstant.LIGHTPROGRESS, num);
//            if (mMusicService.isMp3Playing()
//                    && (!GrandarUtils.getPauseFlag())) {
//                GrandarLogUtils.e(TAG, "setWholeLightness return");
//                return;
//            }
            byte[] data = new byte[1];
            data[0] = (byte) num;

            sendRemoteControlFrame(MyConstant.CHANGE_BIGHTNESS, data);
        }

        /* 设置baby音量 */
        public void setBabyVolumeness(int num) {

        }

        public void sendBabyFilesListToDevice(String filesListName, int beginIndex) {
            setBabyWholeLightness(100);
//                    (SharedPreferencesUtils.getinstance().getIntValue(IConstant.LIGHTPROGRESS, IConstant.DEFAULTLIGHT));
//            setBabyVolumeness
//                    (SharedPreferencesUtils.getinstance().getIntValue(IConstant.VOICEPROGRESS, IConstant.DEFAULTVOLUME));

            // 发送列表
            sendRemoteControlFrame(MyConstant.REQ_SEND_LIGHT_FRAME, null);
            if (mPlayLightSongListService != null) {
                mPlayLightSongListService.sendFilesListToDevice(filesListName, beginIndex);
                KLog.e("sendFilesListToDevice");
            } else {
                KLog.e("sendFilesListToDevice mPlayLightSongListService==null");
            }
        }

        public void pauseBabyCurPlaying() {
            if (mPlayLightSongListService != null) {
                mPlayLightSongListService.pauseCurPlaying();
            }
        }

        public void playBabyCurPause() {
            if (mPlayLightSongListService != null) {
                mPlayLightSongListService.resumeCurPlaying();
                KLog.e("playBabyCurPause");
            }
        }

        @Override
        public void sendLightC(int nProgressRed, int nProgressGreen, int nProgressBlue, int nProgressWhite, String sn) throws RemoteException {
            byte[] data = new byte[8];
            data[0] = (byte) (0xff & nProgressRed);
            data[1] = (byte) ((0xff00 & nProgressRed) >> 8);
            data[2] = (byte) (0xff & nProgressGreen);
            data[3] = (byte) ((0xff00 & nProgressGreen) >> 8);
            data[4] = (byte) (0xff & nProgressBlue);
            data[5] = (byte) ((0xff00 & nProgressBlue) >> 8);
            data[6] = (byte) (0xff & nProgressWhite);
            data[7] = (byte) ((0xff00 & nProgressWhite) >> 8);

            byte[] totalData = new byte[128];
            byte[] totalDatas = new byte[128 + 1 + 4];
//            totalDatas[0] = (byte) 20;
            totalDatas[0] = (byte) Constant.brightness;
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 8; j++) {
                    totalData[i * 8 + j] = data[j];
                }
            }
            // 光效渐变时长
            byte[] time = new byte[4];
            time = MyUtil.intToByte(500); //
            System.arraycopy(time, 0, totalDatas, 129, 4);
            System.arraycopy(totalData, 0, totalDatas, 1, 128);


            KLog.e("sendLightC");
            sendOffCmd(MyConstant.LIGHT_FRAME, totalDatas, sn);
        }

        @Override
        public void setWholeLight(int num, String sn) {

            byte[] data = new byte[1];
            data[0] = (byte) num;
            try {
                KLog.e("setWholeLight");
                sendOffCmd(MyConstant.CHANGE_BIGHTNESS, data, sn);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void sendLight(int num, int nProgressRed, int nProgressGreen, int nProgressBlue, int nProgressWhite, String sn) throws RemoteException {
            byte[] data = new byte[8];
            data[0] = (byte) (0xff & nProgressRed);
            data[1] = (byte) ((0xff00 & nProgressRed) >> 8);
            data[2] = (byte) (0xff & nProgressGreen);
            data[3] = (byte) ((0xff00 & nProgressGreen) >> 8);
            data[4] = (byte) (0xff & nProgressBlue);
            data[5] = (byte) ((0xff00 & nProgressBlue) >> 8);
            data[6] = (byte) (0xff & nProgressWhite);
            data[7] = (byte) ((0xff00 & nProgressWhite) >> 8);

            byte[] totalData = new byte[128];
            byte[] totalDatas = new byte[128 + 1 + 4];
//            totalDatas[0] = (byte) 20;
            totalDatas[0] = (byte) num;
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 8; j++) {
                    totalData[i * 8 + j] = data[j];
                }
            }
            // 光效渐变时长
            byte[] time = new byte[4];
            time = MyUtil.intToByte(500); //
            System.arraycopy(time, 0, totalDatas, 129, 4);
            System.arraycopy(totalData, 0, totalDatas, 1, 128);
            KLog.e("sendLightC");
            sendOffCmd(MyConstant.LIGHT_FRAME, totalDatas, sn);

        }

        @Override
        public boolean creatMediaPlayer(String path) throws RemoteException {
            return false;
        }

        @Override
        public boolean start(String path) throws RemoteException {
            return false;
        }

        @Override
        public boolean pause(String path) throws RemoteException {
            return false;
        }

        @Override
        public boolean stop(String path) throws RemoteException {
            return false;
        }

        @Override
        public void sendMusic(byte[] data, int time, String name) throws RemoteException {
            byte[] totalData = new byte[1 + 128 + 4];
            totalData[0] = (byte) MainActivity.nProgressLight;
            System.arraycopy(data, 0, totalData, 1, 128);
            // 光效渐变时长
            byte[] timebuf = new byte[4];
            timebuf = MyUtil.intToByte(time >= 0 ? time : 0); // 默认500毫秒
            System.arraycopy(timebuf, 0, totalData, 128 + 1, 4);
            short len = (short) (totalData != null ? totalData.length : 0);
            byte[] buff = new byte[2 + (totalData != null ? totalData.length : 0)
                    + MyConstant.LIGHT_FRAME.length];
            System.arraycopy(MyConstant.LIGHT_FRAME, 0, buff, 0, MyConstant.LIGHT_FRAME.length);
            buff[MyConstant.LIGHT_FRAME.length] = (byte) (len & 0x00FF);
            buff[MyConstant.LIGHT_FRAME.length + 1] = (byte) ((len >> 8) & 0x00FF);
            if (totalData != null && totalData.length > 0) {
                System.arraycopy(totalData, 0, buff, MyConstant.LIGHT_FRAME.length + 2, totalData.length);
            }
            StringBuffer msg = new StringBuffer();
            for (int i = 0; i < buff.length; i++) {
                msg.append(String.format(" %2x", buff[i]));
            }
            KLog.e("--->sendUdptoServer:" + msg.toString());
            if (GrandarLogUtils.DEBUG_ON_UDP) {
                KLog.e("sendtoUdpServerTask");
                sendtoUdpServerTasks(buff, name);
            } else {
                KLog.e("sendtoServerTask");
                sendtoServerTask(buff);
            }
        }


        /**
         * 首次携带参数构造方法
         */
        public void playMusicSendLight(String fileNameString, String name, int flag, int position) {
            File file = new File(fileNameString);
            if (!file.exists()) {
                return;
            }
            try {
                createMp3Data(fileNameString, name, flag, position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 暂停音乐
         */
        public void pauseMusic() {
            if (mPlayLightSongListService != null) {
                try {
                    mPlayLightSongListService.pause_Music();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 继续播放音乐
         */
        public void resumeMp3Data() {
            if (mPlayLightSongListService != null) {
                try {
                    mPlayLightSongListService.resume_Mp3Data();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 停止音乐
         */
        public void stopMp3() {
            if (mPlayLightSongListService != null) {
                try {
                    mPlayLightSongListService.stopMp3();
                    isPauseThread = true;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                return;
            }
        }

    }

    public void sendtoServerTask(byte[] buff) {
        SendtoServerTask Task = new SendtoServerTask(buff);
        Task.execute();
    }

    public void sendtoUdpServerTask(byte[] buff) {

        if (LogUtils.DEBUG_ON_UDP) {
            SendUdptoServerTask Task = new SendUdptoServerTask(buff);
            Task.execute();
        }
    }

    public void sendtoUdpServerTasks(byte[] buff, String name) {

        if (LogUtils.DEBUG_ON_UDP) {
            SendUdptoServerTasks Task = new SendUdptoServerTasks(buff, name);
            Task.execute();
        }
    }

    //测试网络是否正常
    class SendtoServerTask extends AsyncTask<String, Integer, String> {
        final byte[] buffer;

        public SendtoServerTask(byte[] buff) {
            buffer = buff;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String result = "";
            sendtoServer(buffer);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("show")) {
            }
        }

    }


    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d(TAG, "onBind");
        return new TcpClientServiceBinder();
    }

    public void analyzeServerCmdData(byte[] buff) {
        ServerCmdDataTask Task = new ServerCmdDataTask(buff);
        Task.execute();
    }

    class ServerCmdDataTask extends AsyncTask<String, Integer, String> {
        final byte[] buffer;

        public ServerCmdDataTask(byte[] buff) {
            buffer = buff;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String result = "";
            analyzeFrame(buffer);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("show")) {
            }
        }

    }

    //灯体返回数据处理
    private void analyzeFrame(byte[] buff) {
        LogUtils.e(TAG, "开始分析------》analyzeFrame buff[0]:" + buff[0] + "..." + buff[1]);
        //灯体向控制器发回的命令
        if (buff[0] == (byte) 0x0A) {
            switch (buff[1]) {
                case (byte) 0x01: //开灯
                    if (mMusicService != null) {
                        mMusicService.stopMp3Data();
                    }
                    //发广播消息
                    break;
                case (byte) 0x02://关灯
                    if (mMusicService != null) {
                        mMusicService.stopMp3Data();
                    }
                    //发广播消息
                    break;
                case (byte) 0x03://夜灯
                    if (mMusicService != null) {
                        mMusicService.stopMp3Data();
                    }
                    //发广播消息
                    break;
                case (byte) 0x04://当前亮度
                    //设置亮度值
                    Log.e(TAG, "当前亮度---->analyzeFrame: " + buff[4]);
                    MainActivity.nProgressLight = buff[4];
                    //发广播消息
                    break;
                default:
                    break;
            }
            sendBroadcast(MyConstant.ACTION_RC_SERVER_CMD, buff[1]);
        }
    }

    // 广播接收器
    //应用退出断开服务
    private class ReceiverBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyConstant.mainExit)) {
                disConnectByUser = true;
                completeCloseSocket();
                LogUtils.e(TAG, "mExitTimer 时间到强制与服务器断开");

            }
        }
    }

    // 启动udp接收线程
    Runnable mUdpClientReceiveRun = () -> {
        //接收UDP返回数据
        byte[] arrayOfByte1 = new byte[MyConstant.UDP_BUFFSIZE];
        DatagramPacket localDatagramPacket = new DatagramPacket(
                arrayOfByte1, arrayOfByte1.length);
        while (true) {
            try {
                if (mUdpSocket != null) {
                    mUdpSocket.setReceiveBufferSize(MyConstant.UDP_BUFFSIZE);
                    mUdpSocket.receive(localDatagramPacket);
                }
                if (localDatagramPacket.getLength() <= 0)
                    break;
                int len = localDatagramPacket.getLength();
                byte[] dataBuf = new byte[len];
                System.arraycopy(localDatagramPacket.getData(), 0, dataBuf,
                        0, len);
                if (LogUtils.DEBUG_ON_REMOTECONTROL) {
                    StringBuilder msg = new StringBuilder();
                    for (int i = 0; i < dataBuf.length; i++) {
                        msg.append(String.format(" %2x", dataBuf[i]));
                    }
                    LogUtils.e(TAG, "mUdpClientReceiveRun buffer[]:"
                            + msg.toString());
                }
                // 对收到的数据进行分析 是否是一个完整的数据包
                // 对完整数据帧进行解析
                KLog.e("开始接收监听");
                analyzeServerCmdData(dataBuf);
            } catch (SocketException e) {
                // TODO Auto-generated catch block
                LogUtils.e(TAG, "mUdpClientReceiveRun SocketException e:" + e);
                return;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                LogUtils.e(TAG, "mUdpClientReceiveRun IOException e:" + e);
                return;
            }
        }
    };

    public synchronized void sendUdptoServers(byte[] buff, Socket socket) {
        KLog.e("发送数据-----sendUdptoServer");
        List<MarketMusicBean> myDeviceBean = MarketMusicDbUtil.getInstance().queryTimeAll();
        KLog.e("需要发送的数据==============》" + myDeviceBean.size());
        try {
            if (mUdpSocket != null && socket != null && socket.
                    getInetAddress() != null) {
                KLog.e("发送数据----" + buff.length + " srvIp:" + socket.getInetAddress().getHostAddress()
                        + " port:" + MyConstant.UDPSERVERPORT);
                // 将服务器IP转换为InetAddress对象
                InetAddress server = socket.getInetAddress();
                // 构造发送的数据包对象
                DatagramPacket sendDp = new DatagramPacket(buff, buff.length,
                        server, MyConstant.UDPSERVERPORT);
                // 发送数据
                mUdpSocket.send(sendDp);
                if (LogUtils.DEBUG_ON_REMOTECONTROL) {
                    StringBuilder msg = new StringBuilder();
                    for (int j = 0; j < buff.length; j++) {
                        msg.append(String.format(" %2x", buff[j]));
                    }
                    KLog.e("--->sendUdptoServer:" + msg.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            KLog.e("--->IOException:" + e.getMessage());
        }

    }

    // 客户端向服务器发送Udp数据 发送最后一步
    public synchronized void sendUdptoServer(byte[] buff) {
        KLog.e("发送数据-----sendUdptoServer");
//        List<MyDeviceBean> myDeviceBean = DeviceDbUtil.getInstance().queryTimeAll();
        List<MarketMusicBean> myDeviceBean = MarketMusicDbUtil.getInstance().queryTimeAll();
        KLog.e("需要发送的数据==============》" + myDeviceBean.size());
        for (int i = 0; i < myDeviceBean.size(); i++) {
            String sn = myDeviceBean.get(i).getSn();
            String sn_id = myDeviceBean.get(i).getName();
            Socket socket = GlobalApplication.getInstance().getDevice(sn_id);
//            Socket socket = GlobalApplication.getInstance().getDevice(sn);
            try {
                if (mUdpSocket != null && socket != null && socket.
                        getInetAddress() != null) {
                    KLog.e("发送数据----" + buff.length
                            + " srvIp:" + socket.getInetAddress().getHostAddress()
                            + " port:" + MyConstant.UDPSERVERPORT);
                    // 将服务器IP转换为InetAddress对象
                    InetAddress server = socket.getInetAddress();
                    // 构造发送的数据包对象
                    DatagramPacket sendDp = new DatagramPacket(buff, buff.length,
                            server, MyConstant.UDPSERVERPORT);
                    // 发送数据
                    mUdpSocket.send(sendDp);
                    if (LogUtils.DEBUG_ON_REMOTECONTROL) {
                        StringBuilder msg = new StringBuilder();
                        for (int j = 0; j < buff.length; j++) {
                            msg.append(String.format(" %2x", buff[j]));
                        }
                        KLog.e("--->sendUdptoServer:" + msg.toString());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                KLog.e("--->IOException:" + e.getMessage());
            }
        }

    }

    class SendUdptoServerTasks extends AsyncTask<String, Integer, String> {
        final byte[] buffer;
        final String names;

        public SendUdptoServerTasks(byte[] buff, String name) {
            buffer = buff;
            names = name;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String result = "";
            KLog.e("doInBackground");
            Socket socket = GlobalApplication.getInstance().getDevice(names);
            KLog.e("Socket==================>" + socket);
            sendUdptoServers(buffer, socket);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
        }

    }

    class SendUdptoServerTask extends AsyncTask<String, Integer, String> {
        final byte[] buffer;

        public SendUdptoServerTask(byte[] buff) {
            buffer = buff;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String result = "";
            KLog.e("doInBackground");
            List<MarketMusicBean> myDeviceBean = MarketMusicDbUtil.getInstance().queryTimeAll();
//            Socket socket = GlobalApplication.getInstance().getDevice(sn_id);
//            sendUdptoServers(buffer,socket);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
        }

    }

    private void sendSocketClosetBC() {
        Intent sendIntent = new Intent(MyConstant.ACTION_RC_SOCKET_CLOSE);
        sendBroadcast(sendIntent);
    }

    private void completeCloseSocket() {
        synchronized (mClientSocketLock) {
            if (mClientSocket != null) {
                try {
                    if (!mClientSocket.isInputShutdown()) {
                        mClientSocket.shutdownInput();
                    }
                    if (!mClientSocket.isOutputShutdown()) {
                        mClientSocket.shutdownOutput();
                    }
                    mClientSocket.close();
                    mClientSocket = null;
                } catch (IOException e) {
                    LogUtils.e(TAG, "completeCloseSocket IOException e:"
                            + e);
                }
            }
        }
    }


    public void startTcpClient(List<MyDeviceBean> data, boolean needConnect) {
        KLog.e(TAG, "------服务器开始连接-------" + mThreadClient);
        mUdpThreadClient = new Thread(mUdpClientReceiveRun);

        for (int i = 0; i < data.size(); i++) {
            String ip = data.get(i).getIp();
            String sn = data.get(i).getSn();
            KLog.e(ip + "==" + i);
            if (ip != null) {
                // if ((mThreadClient == null) || (!mThreadClient.isAlive())) {
                // 建立连接
                Socket socket1 = new Socket();
                KLog.e("Socket================》" + socket1);
                ClientProcess mThreadClient = new ClientProcess(socket1, ip, 1, needConnect, sn);
                mThreadClient.start();
                // }
                KLog.e("------------------------------------------");
                if (mUdpThreadClient == null || (!mUdpThreadClient.isAlive())) {
                    // 建立连接
                    try {
                        mUdpSocket = new DatagramSocket(MyConstant.UDPSERVERPORT);
                    } catch (SocketException e) {
                        LogUtils.e(TAG, "onStart e:" + e);
                    }
                    mUdpThreadClient.start();
                }

            }
        }
    }

//    public void startTcpClients(String sn,String ip, boolean needConnect) {
//        KLog.e(TAG, "------服务器开始连接-------" + mThreadClient);
//            if (ip != null) {
//                // 建立连接
//                Socket socket1 = new Socket();
//                KLog.e("Socket================》" + socket1);
//                ClientProcess mThreadClient = new ClientProcess(socket1, ip, 1, needConnect, sn);
//                mThreadClient.start();
//
//                if (LogUtils.DEBUG_ON_UDP) {
//                    if (mUdpThreadClient == null || (!mUdpThreadClient.isAlive())) {
//                        // 建立连接
//                        try {
//                            mUdpSocket = new DatagramSocket(MyConstant.UDPSERVERPORT);
//                        } catch (SocketException e) {
//                            LogUtils.e(TAG, "onStart e:" + e);
//                        }
//                        mUdpThreadClient = new Thread(mUdpClientReceiveRun);
//                        mUdpThreadClient.start();
//                    }
//                }
//            }
//    }

    /* 绑定播放光曲列表服务 */
    private void bindPlayLightSongListService() {
        KLog.e(TAG, "开始绑定---------");
        boolean isbind = this.getApplicationContext().bindService(
                new Intent(this, PlayLightSongListService.class),
                mPlayLightSongListServiceCon, Service.BIND_AUTO_CREATE);
        KLog.e(TAG, "开始绑定--->>" + isbind);
    }

    private void unBindPlayLightSongListService() {
        KLog.e("解除服务--------unBindPlayLightSongListService");
        this.getApplicationContext().unbindService(mPlayLightSongListServiceCon);
    }

    private void startPlayLightSongListService() {
        KLog.e("startPlayLightSongListService");
        this.startService(new Intent(this, PlayLightSongListService.class));
    }

    private void stopPlayLightSongListService() {
        KLog.e("stopPlayLightSongListService");
        this.stopService(new Intent(this, PlayLightSongListService.class));
    }

    private PlayLightSongListService.PlayLightSongListServiceBinder mPlayLightSongListService;


    ServiceConnection mPlayLightSongListServiceCon = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayLightSongListService = (PlayLightSongListService.PlayLightSongListServiceBinder) service;
            if (mPlayLightSongListService != null) {
                startPlayLightSongListService();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            mPlayLightSongListService = null;
        }
    };

    //发送指令
    private synchronized void SendFrameCmd(byte bCmd1, byte bCmd2, byte[] data) {
        SendFrameTask Task = new SendFrameTask(bCmd1, bCmd2, data);
        Task.execute();
        /* 关灯 开灯 定格命令，清除 PlayingListInfo 文件的内容，重置 相关状态 */
        if ((bCmd1 == (byte) 0x01)
                && (bCmd2 == (byte) 0x01 || bCmd2 == (byte) 0x02 || bCmd2 == (byte) 0x0E)) {
            GrandarUtils.clearCurPlayingListInfo(getApplicationContext());
            GrandarUtils.setGetPlayingFlag(false);
            GrandarUtils.setPauseFlag(false);
        } else if (bCmd1 == (byte) 0x02 && bCmd2 == (byte) 0x05)/* 进入流播放模式，清除 PlayingListInfo 文件的内容，重置 相关状态 */ {
            GrandarUtils.clearCurPlayingListInfo(getApplicationContext());
            GrandarUtils.setGetPlayingFlag(false);
            GrandarUtils.setPauseFlag(false);
        }
    }

    final int FRAME_HEAD_LENGTH = 4;
    final int DATA_LENGTH = 2;
    final int CMD_LENGTH = 2;
    final int KEEP_WORD_LENGTH = 4;
    final int CONST_INFO_LENGTH = FRAME_HEAD_LENGTH + DATA_LENGTH + CMD_LENGTH
            + KEEP_WORD_LENGTH;
    final int SEND_TRIGGER_LEN = 640;

    //发送指令
    class SendFrameTask extends AsyncTask<String, Integer, String> {
        final byte mCmd1;
        final byte mCmd2;
        final byte[] mData;

        public SendFrameTask(byte bCmd1, byte bCmd2, byte[] data) {
            mCmd1 = bCmd1;
            mCmd2 = bCmd2;
            mData = data;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String result = "";

            byte bCheck = 0;
            int dataLength = 0, bCount = 0;

            if (mData != null) {
                dataLength = mData.length;
            }
            int nSendsize = CONST_INFO_LENGTH + dataLength + 1;
            int nSendRemain = nSendsize % SEND_TRIGGER_LEN; // 发送的字节数要是128的整数倍
            if (nSendRemain != 0)
                nSendsize += (SEND_TRIGGER_LEN - nSendRemain);

            byte[] bInitFrame = new byte[nSendsize];
            // KLog.e(dataLength + Arrays.toString(bInitFrame));
            Arrays.fill(bInitFrame, (byte) 0x00);
            // 1. 帧头： 固定为4个字节，AA 55 I G
            bInitFrame[0] = (byte) 0xAA;
            bInitFrame[1] = 0x55;
            bInitFrame[2] = 'I';
            bInitFrame[3] = 'G';

            // 数据长度 固定2个字节 低位在前，高位在后
            int len = dataLength + CMD_LENGTH + KEEP_WORD_LENGTH + 1;
            bInitFrame[4] = (byte) (len & 0x00FF);
            bInitFrame[5] = (byte) (len >> 8 & 0x00FF);

            bInitFrame[6] = mCmd1;
            bInitFrame[7] = mCmd2;

            // 保留字
            bInitFrame[8] = 0x00;
            bInitFrame[9] = 0x00;
            bInitFrame[10] = 0x00;
            bInitFrame[11] = 0x00;

            // 加和校验
            for (bCount = 0; bCount <= (CONST_INFO_LENGTH - 1); bCount++)
                bCheck += bInitFrame[bCount];
            for (bCount = 0; bCount < dataLength; bCount++) {
                bCheck += mData[bCount];
                // 数据复制
                bInitFrame[bCount + CONST_INFO_LENGTH] = mData[bCount];
            }
            bInitFrame[dataLength + CONST_INFO_LENGTH] = bCheck;

            // KLog.e(bInitFrame.length + Arrays.toString(bInitFrame));
            //把byte数组光数据用TCP方式发送
//            if (LogUtils.DEBUG_ON_UDP) {
//                sendtoUdpServerTask(bInitFrame);
//            } else {
//                sendtoServerTask(bInitFrame);
//            }
            //GrandarUtils.sendLightDataTime(bInitFrame, 0);
//            setCmdFrameBuf(bInitFrame);
//
            sendUdptoServer(bInitFrame);
            sendtoUdpServerTask(bInitFrame);
            try {
                DatagramSocket socket = new DatagramSocket();
                Socket socket1 = GlobalApplication.getInstance().getDevice("");
                InetAddress server = socket1.getInetAddress();
                // 构造发送的数据包对象
                DatagramPacket sendDp = new DatagramPacket(bInitFrame, bInitFrame.length,
                        server, MyConstant.UDPSERVERPORT);
                socket.send(sendDp);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            // 发送数据
//            try {
//                socket.send(sendDp);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
        }

    }

    private synchronized void setFilesListCursor(Cursor cursor) {
        filesListCursor = cursor;
    }

    Cursor filesListCursor = null;

    private synchronized Cursor getFilesListCursor() {
        return filesListCursor;
    }


    private synchronized boolean getLoopMode() {
        return inLoopMode;
    }

    /* 是否是循环模式 */
    private boolean inLoopMode = true;

    private synchronized void setLoopMode(boolean flag) {
        inLoopMode = flag;
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

            SendFilesListInitFirst(mFilesListName, mBeginIndex);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("show")) {
            }
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
        KLog.e("SendFilesListInit filesListname="
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
            LogUtils.e(TAG, "SendFilesListInit 催眠模式不循环");
            querySingleSongLight(nameId);
        } else if (playType == Constant.ConValue.PLAY_TYPE_PLAY_LIST) {
            KLog.e(mode + "---" + fileListId);
            queryPlayList("mode" + mode, fileListId);
            KLog.e("开始列表播放");
        }

        listCursor = getFilesListCursor();
        KLog.e("开始列表播放" + listCursor + "--" + listCursor.getCount());
        /* 准备发送 初始化变量 统计需要发送的路径个数 */
        if ((listCursor != null) && (listCursor.getCount() > 0)) {
            if ((beginFromIndex >= 0)
                    && (beginFromIndex < listCursor.getCount())) {
                setList_BeginFromIndex(beginFromIndex);
            }
            SendFilesListInitSecond();

        }
    }

    //参数
    int list_Mp3frameCount = 0;// 播放列表 光曲文件的总帧数
    int list_Mp3sendCount = 0; // 播放列表 当前发送的帧数统计
    int list_BeginFromIndex = 0;// 播放列表 从该列表的第几个开始循环播放该列表（从0开始计数）

    boolean needSendFilesList = false; // 是否需要发送光曲列表
    boolean keepOnSendFilesList = false; // 是否可以继续发送光曲列表
    boolean inStreamMediaMode = false; // 处于流模式 发送数据
    boolean streamMediaModeReq = false; // 请求切换到流媒体模式
    boolean streamMediaDataSendOver = false; // 流模式下 需要发送的数据已发送完毕
    /* 请求升级灯体程序 */
    boolean inUpdateIgooSoftware = false; // 处于流模式 发送数据
    // 存放流模式下的数据
    private final Object mStreamDataBufLock = new Object();
    private final int MAX_STREAM_DATA_NUM = 10;
    private ArrayList<byte[]> mStreamDataBuf = new ArrayList<byte[]>();

    private synchronized boolean getInUpdateIgooSoftware() {
        return inUpdateIgooSoftware;
    }

    private synchronized void setInUpdateIgooSoftware(boolean flag) {
        inUpdateIgooSoftware = flag;
    }

    private synchronized int getList_BeginFromIndex() {
        return list_BeginFromIndex;
    }

    private synchronized void setList_BeginFromIndex(int value) {
        list_BeginFromIndex = value;
    }

    private ArrayList<byte[]> getStreamDataBuf() {
        synchronized (mStreamDataBufLock) {
            return mStreamDataBuf;
        }
    }

    private void initStreamDataBuf() {
        synchronized (mStreamDataBufLock) {
            mStreamDataBuf.clear();
        }
    }

    /* 初始化发送线程使用到的变量 */
    private void initSendThreadVar() {
        setList_SendAllVar(0, 0, 0, true, false, true, false, false, false);
        setMp3_SendAllVar(0, 0, true, null);
        setDelaySendSongLightFlag(false);
        setInUpdateIgooSoftware(false);
        initStreamDataBuf();// 初始化流媒体数据buffer
        stopMp3Data(); // 插入耳机时 停止当前播放
    }

    public void stopMp3Data() {
        if (GrandarLogUtils.DEBUG_ON_EARPHONE) {
            if (mMusicService != null) {
                mMusicService.stopMp3Data();
                isPauseThread = true;
            }
        }
    }

    private final int MAX_DELAY_SEND_SL_TIME = 10;// 延迟10秒发送
    private long begin_delay_time = 0;
    private boolean mDelaySendSongLight = false;

    private synchronized void setDelaySendSongLightFlag(boolean flag) {
        mDelaySendSongLight = flag;
        if (mDelaySendSongLight == true) {
            acquireDelaySendSLWakeLock();
            begin_delay_time = System.currentTimeMillis() / 1000;
        } else {
            begin_delay_time = 0;
        }
    }

    PowerManager.WakeLock mDelaySendSLWakeLock = null;

    private void acquireDelaySendSLWakeLock() {
        acquireKeepWakeLock();

        PowerManager pm = (PowerManager) this
                .getSystemService(Context.POWER_SERVICE);
        if (mDelaySendSLWakeLock != null && mDelaySendSLWakeLock.isHeld()) {
            mDelaySendSLWakeLock.release();
            // GrandarLogUtils.e(TAG,
            // "acquire1 mDelaySendSLWakeLock wake lock");
        }
        mDelaySendSLWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "BtService");
        mDelaySendSLWakeLock.acquire(14000);
        releaseKeepWakeLock();
        // GrandarLogUtils.e(TAG, "acquire2 send wake lock");
    }

    PowerManager.WakeLock mSendWakeLock = null;
    PowerManager.WakeLock mReceiveWakeLock = null;
    PowerManager.WakeLock mDelayWakeLock = null;
    PowerManager.WakeLock mKeepWakeLock = null;

    private void releaseKeepWakeLock() {
        if (null != mKeepWakeLock && mKeepWakeLock.isHeld()) {
            // GrandarLogUtils.e(TAG, "release keep wake lock");
            mKeepWakeLock.release();
            mKeepWakeLock = null;
        }
    }

    private void acquireKeepWakeLock() {
        if (mKeepWakeLock == null) {
            PowerManager pm = (PowerManager) this
                    .getSystemService(Context.POWER_SERVICE);

            mKeepWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "BtService");

			/*
             * mReceiveWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
			 * PowerManager.ACQUIRE_CAUSES_WAKEUP, "BtService");
			 */
            if (mKeepWakeLock != null) {
                mKeepWakeLock.acquire();
                // GrandarLogUtils.e(TAG, "acquire2 keep wake lock");
            }
        }

    }


    private synchronized boolean getDelaySendSongLightFlag() {
        if (mDelaySendSongLight) {
            long current_time = System.currentTimeMillis() / 1000;
            if ((current_time - begin_delay_time) > MAX_DELAY_SEND_SL_TIME) {
                // 延迟时间到 可以开始发送光曲数据
                setDelaySendSongLightFlag(false);
            }

        }
        return mDelaySendSongLight;
    }

    private synchronized boolean getNeedSendFilesList() {
        return needSendFilesList;
    }

    private synchronized void setNeedSendFilesList(boolean flag) {
        needSendFilesList = flag;
    }

    private void SendFilesListInitSecond() {
        setNeedSendFilesList(true);

		/* 统计需要发送的路径总个数 */
        Cursor listCursor = getFilesListCursor();
        setList_Mp3frameCount(listCursor.getCount());

        KLog.e("SendMp3Thread getList_Mp3sendCount="
                + getList_Mp3sendCount() + "list_Mp3frameCount="
                + getList_Mp3frameCount());

        // 模拟器模拟 真机注释掉
//        if (GrandarLogUtils.DEBUG_ON_PC) {
//            setKeepOnSendFilesList(true);
//        }

    }

    private synchronized boolean getKeepOnSendFilesList() {
        return keepOnSendFilesList;
    }

    private synchronized void setKeepOnSendFilesList(boolean flag) {
        keepOnSendFilesList = flag;
    }

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

    /* 设置发送光曲列表 用到的所有共享变量 */
    private synchronized void setList_SendAllVar(int sendFrame, int frameCount,
                                                 int BeginFromIndex, boolean needSendList, boolean keepOnSendList,
                                                 boolean loopMode, boolean streamMode, boolean streamModeReq,
                                                 boolean deleteList) {
        list_Mp3sendCount = sendFrame;
        list_Mp3frameCount = frameCount;
        list_BeginFromIndex = BeginFromIndex;
        needSendFilesList = needSendList;
        keepOnSendFilesList = keepOnSendList;
        inLoopMode = loopMode;
        inStreamMediaMode = streamMode;
        streamMediaModeReq = streamModeReq;
        inDeleteListMode = deleteList;
    }

    int Mp3frameCount = 0;// 光曲文件的总帧数
    int Mp3sendCount = 0; // 当前发送的帧数统计
    int mStreamSendCount = 0; // 流模式当前发送了多少包
    public boolean canSendNextData = true; // 前一包数据发送完成，可以发送下一包数据

    /* 设置发送光曲数据 用到的所有共享变量 */
    private synchronized void setMp3_SendAllVar(int frameCount, int sendCount,
                                                boolean canSendNext, String fileName) {
        Mp3frameCount = frameCount;
        Mp3sendCount = sendCount;
        canSendNextData = canSendNext;
        sendingFileName = fileName;
    }

    String sendingFileName = null; // 正在发送的光曲名称

    private synchronized boolean getInDeleteListMode() {
        return inDeleteListMode;
    }

    private synchronized void setInDeleteListMode(boolean flag) {
        inDeleteListMode = flag;
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
    boolean inDeleteListMode = false;

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

    public void querySingleSongLight(int songLightId) {
        Cursor ret = null;
        ContentResolver resolver = this.getContentResolver();
        Uri uri = SongLightStore.TotalSongLights.getContentUri();
        StringBuilder where = new StringBuilder();
        where.append(SongLightStore.TotalSongLights._ID + " = '" + songLightId
                + "'");
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
            if (ret.getInt(ret
                    .getColumnIndexOrThrow(SongLightStore.TotalSongLights.TYPE)) == 17) {
                addNew.add(1);
            } else {
                addNew.add(0);
            }
            addnewcursor.addRow(addNew);
            Cursor cursor = new MergeCursor(new Cursor[]{addnewcursor, null});
            setFilesListCursor(cursor);
            SendFilesListMp3(SendFlieListType.sendSingle);
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
        KLog.e("个数---" + cursor.getCount());
        setFilesListCursor(cursor);

    }

    final int MD5_LENGTH = 16;
    final int LIST_START_END = 1;
    final int LIST_LOOP = 1;
    final int SONGLIGHT_TYPE = 1;
    final int SONGLIGHT_TIMES = 2;
    final int LIST_ORDER = 2;
    final int CONST_SONGLIGHT_INFO_LENGTH = MD5_LENGTH + LIST_START_END
            + LIST_LOOP + SONGLIGHT_TYPE + SONGLIGHT_TIMES + LIST_ORDER;
    int pos = 1;
    boolean sendTag = true;

    public void SendFilesListMp3(byte cmdType) {
        try {
            KLog.e("进入---SendFilesListMp3");
            int pos = getCurFilesListPos();
            Cursor listCursor = getFilesListCursor();
            if (listCursor == null) {
                GrandarLogUtils.e(TAG, "SendFilesListMp3 listCursor == null");
                return;
            }
            listCursor.moveToPosition(pos);
            KLog.e("当前播放歌曲--" + listCursor.getString(listCursor.getColumnIndex("name")));
            setList_Mp3sendCount(getList_Mp3sendCount() + 1);

            KLog.e("SendFilesListMp3 getList_Mp3sendCount:"
                    + getList_Mp3sendCount());

            int nSendsize = CONST_INFO_LENGTH + CONST_SONGLIGHT_INFO_LENGTH + 1;
            int nSendRemain = nSendsize % SEND_TRIGGER_LEN; // 发送的字节数要是640的整数倍
            if (nSendRemain != 0)
                nSendsize += (SEND_TRIGGER_LEN - nSendRemain);
            byte[] fileInfoBuf = new byte[nSendsize];
            Arrays.fill(fileInfoBuf, (byte) 0x00);
            // 1. 帧头： 固定为4个字节，AA 55 I G
            fileInfoBuf[0] = (byte) 0xAA;
            fileInfoBuf[1] = 0x55;
            fileInfoBuf[2] = 'I';
            fileInfoBuf[3] = 'G';

            // 数据长度 固定2个字节 低位在前，高位在后
            int len = CMD_LENGTH + KEEP_WORD_LENGTH
                    + CONST_SONGLIGHT_INFO_LENGTH + 1;
            fileInfoBuf[4] = (byte) (len & 0x00FF);
            fileInfoBuf[5] = (byte) (len >> 8 & 0x00FF);

            // 命令字
            fileInfoBuf[6] = 0x02;
            fileInfoBuf[7] = cmdType;

            // 保留字
            fileInfoBuf[8] = 0x00;
            fileInfoBuf[9] = 0x00;
            fileInfoBuf[10] = 0x00;
            fileInfoBuf[11] = 0x00;

            if (cmdType != SendFlieListType.streamMediaMode) {
                // 光曲md5 校验码
                byte[] md5Code = listCursor
                        .getBlob(listCursor
                                .getColumnIndexOrThrow(SongLightStore.Playlists.Members.CHECKCODE));
                KLog.e(Arrays.toString(md5Code) + "..." + getList_Mp3sendCount());
                System.arraycopy(md5Code, 0, fileInfoBuf, CONST_INFO_LENGTH,
                        md5Code.length);
                if (sendTag) {
                    fileInfoBuf[CONST_INFO_LENGTH + MD5_LENGTH] = (byte) SendFlieListType.listStart;
                } else {
                    fileInfoBuf[CONST_INFO_LENGTH + MD5_LENGTH] = (byte) SendFlieListType.listEnd;
                }

                KLog.e("getList_Mp3sendCount()--" + getList_Mp3sendCount()
                        + "getList_Mp3frameCount()--" + getList_Mp3frameCount());
                // Start/end标识
                if (getList_Mp3sendCount() == 1) {
                    fileInfoBuf[CONST_INFO_LENGTH + MD5_LENGTH] = (byte) SendFlieListType.listStart;
                } else if (getList_Mp3sendCount() == getList_Mp3frameCount()) {
                    fileInfoBuf[CONST_INFO_LENGTH + MD5_LENGTH] = (byte) SendFlieListType.listEnd;
                } else {
                    fileInfoBuf[CONST_INFO_LENGTH + MD5_LENGTH] = (byte) SendFlieListType.listMid;
                }

                // 光曲列表循环标识
                fileInfoBuf[CONST_INFO_LENGTH + MD5_LENGTH + LIST_START_END] = (byte) (getLoopMode() ? 0x01
                        : 0x00);
                // 插入耳机
                if (mIsHeadsetOn) {
                    fileInfoBuf[CONST_INFO_LENGTH + MD5_LENGTH + LIST_START_END] = (byte) (fileInfoBuf[CONST_INFO_LENGTH
                            + MD5_LENGTH + LIST_START_END] | (byte) 0x10);
                }

                // 纯/MP3 光效标识
                KLog.e(listCursor
                        .getInt(listCursor
                                .getColumnIndexOrThrow(SongLightStore.Playlists.Members.TYPE)));
                fileInfoBuf[CONST_INFO_LENGTH + MD5_LENGTH + LIST_START_END
                        + LIST_LOOP] = (byte) listCursor
                        .getInt(listCursor
                                .getColumnIndexOrThrow(SongLightStore.Playlists.Members.TYPE));

                // 持续时间/重复次数
                short times = (short) listCursor
                        .getLong(listCursor
                                .getColumnIndexOrThrow(SongLightStore.Playlists.Members.TIMES));
                fileInfoBuf[CONST_INFO_LENGTH + MD5_LENGTH + LIST_START_END
                        + LIST_LOOP + SONGLIGHT_TYPE] = (byte) (times & 0x00FF);
                fileInfoBuf[CONST_INFO_LENGTH + MD5_LENGTH + LIST_START_END
                        + LIST_LOOP + SONGLIGHT_TYPE + 1] = (byte) (times >> 8 & 0x00FF);

                // 列表播放序号
                fileInfoBuf[CONST_INFO_LENGTH + MD5_LENGTH + LIST_START_END
                        + LIST_LOOP + SONGLIGHT_TYPE + SONGLIGHT_TIMES] = (byte) (getList_Mp3sendCount() & 0x00FF);
                fileInfoBuf[CONST_INFO_LENGTH + MD5_LENGTH + LIST_START_END
                        + LIST_LOOP + SONGLIGHT_TYPE + SONGLIGHT_TIMES + 1] = (byte) (getList_Mp3sendCount() >> 8 & 0x00FF);
            }
            // 加和校验位
            byte bCheck = 0x00;
            for (int i = 0; i < (CONST_INFO_LENGTH + CONST_SONGLIGHT_INFO_LENGTH); i++) {
                bCheck += fileInfoBuf[i];
                // GrandarLogUtils.d(TAG,
                // "SendFilesListMp3 fileInfoBuf[i]="+
                // String.format("%2x", fileInfoBuf[i])+" i= "+i);
            }
            fileInfoBuf[CONST_INFO_LENGTH + CONST_SONGLIGHT_INFO_LENGTH] = bCheck;
            GrandarLogUtils.e(TAG, "SendFilesListMp3 bCheck=" + bCheck);
            sendtoUdpServerTask(fileInfoBuf);
            //sendByteHandle(fileInfoBuf);
        } catch (Exception e) {
            GrandarLogUtils.e(TAG, "SendFilesListMp3 e:" + e);
            // return;
        }

        if (getList_Mp3sendCount() == getList_Mp3frameCount()) {
            setNeedSendFilesList(false);
            if (mIsHeadsetOn) {
                byte[] data = new byte[1];
                data[0] = 0x00;
                SendFrameCmd((byte) 0x01, (byte) 0x03, data);
            }
            if (GrandarLogUtils.DEBUG_ON_PC) {
                SendMp3Init(1);
//                mPlayLightSongListService.p
            }
        }

    }

    private int getCurFilesListPos() {
        // TODO Auto-generated method stub
        int total = getList_Mp3frameCount();
        int sendFrame = getList_Mp3sendCount();
        int beginIndex = getList_BeginFromIndex();
        int pos = 0;

        if (sendFrame < (total - beginIndex)) {
            pos = beginIndex + sendFrame;
        } else {
            pos = sendFrame - (total - beginIndex);
        }
        KLog.e("getCurFilesListPos total=" + total
                + " sendFrame=" + sendFrame + " beginIndex=" + beginIndex
                + " pos=" + pos);
        return pos;
    }

    private boolean mIsHeadsetOn = false;

    public static final class SendFlieListType {
        public static byte sendSingle = 0x01;
        public static byte sendMulti = 0x02;
        public static byte deleteSingle = 0x03;
        public static byte deleteMulti = 0x04;
        public static byte streamMediaMode = 0x05;

        public static byte listStart = 0x00;
        public static byte listMid = 0x01;
        public static byte listEnd = 0x02;
    }

    private SendMp3Thread sendMp3Thread = null;

    public void StartSendThread() {
        KLog.e("---------mp3线程启动---------" + sendMp3Thread);
        if (sendMp3Thread != null) {
            sendMp3Thread.interrupt();
            sendMp3Thread = null;
        }
        if (sendMp3Thread == null) {
            sendMp3Thread = new SendMp3Thread();
            sendMp3Thread.start();
        }
    }

    private synchronized void setCmdFrameBuf(byte[] buf) {
        if (mCmdFrameBufList.size() >= MAX_CMDFRAME_LIST_LEN) {
            mCmdFrameBufList.clear();
        }
        mCmdFrameBufList.add(buf);
    }

    final int MAX_CMDFRAME_LIST_LEN = 10;
    private ArrayList<byte[]> mCmdFrameBufList = new ArrayList<byte[]>();

    private synchronized byte[] getCmdFrameBuf() {
        if (mCmdFrameBufList != null && mCmdFrameBufList.size() > 0) {
            return mCmdFrameBufList.get(0);
        } else {
            return null;
        }
    }

    private synchronized boolean getNeedSendCmdFrame() {
        if (mCmdFrameBufList != null && mCmdFrameBufList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean canSend = true; //

    private synchronized void setCanSendFlag(boolean flag) {
        canSend = flag;
    }

    private synchronized boolean getCanSendFlag() {
        return canSend;
    }

    private void setCanSendNextData(boolean flag) {
        canSendNextData = flag;
    }

    private boolean getCanSendNextData() {
        return canSendNextData;
    }

    private synchronized void setNeedSendCmdFrame(boolean flag) {
        if (flag == false) {
            if (mCmdFrameBufList != null && mCmdFrameBufList.size() > 0) {
                mCmdFrameBufList.remove(0);
            }
        }
    }

    private synchronized int getMp3frameCount() {
        return Mp3frameCount;
    }

    private synchronized void setMp3frameCount(int value) {
        Mp3frameCount = value;
    }

    private synchronized int getMp3sendCount() {
        return Mp3sendCount;
    }

    private synchronized void setMp3sendCount(int value) {
        Mp3sendCount = value;
    }

    private synchronized boolean getStreamMediaModeReq() {
        return streamMediaModeReq;
    }

    private synchronized void setStreamMediaModeReq(boolean flag) {
        streamMediaModeReq = flag;
    }

    private synchronized boolean getInStreamMediaMode() {
        return inStreamMediaMode;
    }

    private synchronized void setInStreamMediaMode(boolean flag) {
        inStreamMediaMode = flag;
    }

    private void startMusicService() {
        this.startService(new Intent(this, MusicService.class));
    }

    private boolean isPauseThread = false;
    public OutputStream mmOutStream = null;

    private void stopMusicService() {
        this.stopService(new Intent(this, MusicService.class));
    }

    private MusicService.MusicServiceBinder mMusicService;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicService = (MusicService.MusicServiceBinder) service;
            Log.e(TAG, "mMusicServiceCon----" + mMusicService);
            if (mMusicService != null) {
                Log.e(TAG, "mMusicServiceCon onServiceConnected");
                startMusicService();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void playMp3Data() {
        if (GrandarLogUtils.DEBUG_ON_EARPHONE) {
            if (mIsHeadsetOn) {
                if (mMusicService != null) {
                    mMusicService.playMp3Data();
                }
            }
        }
    }

    public void createMp3Data(String songLightFile, String name, int flag, int position) throws RemoteException {
        if (GrandarLogUtils.DEBUG_ON_EARPHONE) {
            if (mPlayLightSongListService != null) {
                try {
                    mPlayLightSongListService.createMp3Data(songLightFile, true, name, flag, position);
                    KLog.e("createMp3Data" + songLightFile + name + flag + position);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // 1. 开启一个全局线程，该函数只调用一次，开启后不要断开 发送线程
    private class SendMp3Thread extends Thread {
        public void run() {
            final int myId = Process.myPid();
            if (Process.getThreadPriority(myId) != Process.THREAD_PRIORITY_AUDIO) {
                Process.setThreadPriority(myId, Process.THREAD_PRIORITY_AUDIO);
            }
            while (true) {
                if (isPauseThread) {
                    KLog.e("isPauseThread" + "总线程关闭");
                    return;
                } else {
                    if (getCanSendNextData()) {
                        if (getNeedSendCmdFrame()) {
                            byte[] cmdBuf = getCmdFrameBuf();
                            setNeedSendCmdFrame(false);
                            KLog.e("发送光效" + cmdBuf);
                            if (cmdBuf != null && cmdBuf.length >= 8) {
                                // 暂停命令特殊处理 先 sleep 30ms，在发送 保证 管道数据发送完毕
                                byte bCmd1 = cmdBuf[6];
                                byte bCmd2 = cmdBuf[7];
                                if ((bCmd1 == (byte) 0x01) && (bCmd2 == (byte) 0x16)) {
                                    try {
                                        if (!GrandarUtils.getPauseFlag()) {
                                            GrandarUtils.setPauseFlag(true);
                                        }
                                        Thread.sleep(30);
                                    } catch (InterruptedException e) {
                                    }
                                } else if ((bCmd1 == (byte) 0x01)
                                        && (bCmd2 == (byte) 0x17)) {
                                    if (GrandarUtils.getPauseFlag()) {
                                        GrandarUtils.setPauseFlag(false);
                                    }
                                } else if ((bCmd1 == (byte) 0x01)
                                        && ((bCmd2 == (byte) 0x01)
                                        || (bCmd2 == (byte) 0x02) || (bCmd2 == (byte) 0x0E))// 开灯/关灯/定格
                                        && (!getInDeleteListMode()))// 当前不处于删除灯体光曲模式时
                                // 停止循环发送数据
                                {
                                    initSendThreadVar();
                                }
                            }
                            if (cmdBuf != null && cmdBuf.length > 0) {
                                sendtoUdpServerTask(cmdBuf);
                            }
                        } else if (getInUpdateIgooSoftware())/* 升级灯体端程序模式 */ {
                            if ((getMp3frameCount() > 0)
                                    && (getMp3sendCount() < getMp3frameCount())) {
                                SendUpdateSoftwareData();
                            }

                        } else if ((getList_Mp3frameCount() > 0)
                                && (getList_Mp3sendCount() < getList_Mp3frameCount())) {
                            if (getStreamMediaModeReq())// 请求切换到流模式
                            {
                                setStreamMediaModeReq(false);
                                SendFilesListMp3(SendFlieListType.streamMediaMode);
                            } else if (getInDeleteListMode()) {
                                SendFilesListMp3(SendFlieListType.deleteSingle);
                            } else if (getList_Mp3sendCount() == 0) // 播放列表的第一个直接发
                            {
                                if (getList_Mp3frameCount() == 1) {
                                    SendFilesListMp3(SendFlieListType.sendSingle);
                                } else {
                                    SendFilesListMp3(SendFlieListType.sendMulti);
                                }
                            } else if (getKeepOnSendFilesList()) { // 其他的列表项
                                // 要得到灯体回应后发
                                SendFilesListMp3(SendFlieListType.sendMulti);
                            }

                        } else if ((getMp3frameCount() > 0)
                                && (getMp3sendCount() < getMp3frameCount())
                                && (GrandarUtils.getPauseFlag() == false)
                                && (getDelaySendSongLightFlag() == false)) {
                            SendMp3Time();
                            if (getMp3sendCount() == getMp3frameCount()) {
                                if (GrandarLogUtils.DEBUG_ON_PC) {
                                    playMp3Data();
                                }
                            }
                        } else if (getInStreamMediaMode())/* 当前处于流模式 */ {
                            processStreamDataBuf();
                        }
                    }
                }
            }

        }

    }

    private void acquireSendWakeLock() {
        if (mSendWakeLock == null) {
            PowerManager pm = (PowerManager) this
                    .getSystemService(Context.POWER_SERVICE);
            mSendWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "BtService");
            mSendWakeLock.setReferenceCounted(false);
        }
        mSendWakeLock.acquire(14000);

    }

    // 发送字节数据
    private void sendByteHandle(byte[] buffer) {
        if (GrandarLogUtils.DEBUG_ON_PC) {
            String str1 = null;
            StringBuilder msg = new StringBuilder(str1);
            for (int i = 6; i < buffer.length; i++) {
                msg.append(String.format(" %2x", buffer[i]));
            }
            KLog.e("mp3 buffer[]:" + msg.toString());
        }

        Thread checkThread = null;
        try {
            setCanSendNextData(false);
            // bSend = true;
            acquireSendWakeLock();

            if ((mmOutStream != null) && getCanSendFlag()) {
                final long lStartTime = System.currentTimeMillis();
                    /* 新建一个线程，检测是否阻塞在write */
                checkThread = new Thread() {
                    public void run() {
                        while (!getCanSendNextData()) {
                            long end = System.currentTimeMillis();
                            if ((end - lStartTime) > 1000 * 10) {
                                KLog.e("超时");
                                break;
                            }
                            try {
                                sleep(1000);
                            } catch (InterruptedException e) {

                                break;
                            }
                        }
                    }
                };
                checkThread.start();

                mmOutStream.write(buffer);
                mmOutStream.flush();
                long lEndTime = System.currentTimeMillis();
//                if (GrandarLogUtils.DEBUG_CONNECTAGAIN) {
//                    long lSendTime = lEndTime - lStartTime;
//                    if (lSendTime > 20 && lSendTime < 100) {
//                        nDelayTime++;
//                    }
//                }
            }

        } catch (IOException e) {
            GrandarLogUtils.i(TAG, "sendByteHandle1 error");
            e.printStackTrace();

        } finally {
            // releaseWakeLock();
            if (checkThread != null) {
                checkThread.interrupt();
            }
            setCanSendNextData(true);
        }
    }

    private synchronized void setSendingFileName(String name) {
        sendingFileName = name;
    }

    private synchronized String getSendingFileName() {
        return sendingFileName;
    }

    final int SONGLIGHT_PACKAGE_NUM = 4; // 光曲数据包序号 从0开始
    final int SEND_SONGLIGHT_DATA_LEN = 623; // 每包发送光曲数据的长度
    int nTestCount = 0; // 仅用于测试发送数据量的大小
    long mInitSendTime = 0, mSendCompleteTime = 0;

    /* 发送灯体程序 */
    private void SendUpdateSoftwareData() {
        setMp3sendCount(getMp3sendCount() + 1);
        BufferedInputStream inData = null;
        int mp3sendCount = getMp3sendCount();
        int mp3FrameCount = getMp3frameCount();
        String sendingFileName = getSendingFileName();
        if (mp3sendCount == 0) {
            GrandarLogUtils.i(TAG, "SendUpdateSoftwareData mp3sendCount==0");
            return;
        }
        if (mp3FrameCount == 0) {
            GrandarLogUtils.i(TAG, "SendUpdateSoftwareData mp3FrameCount==0");
            return;
        }
        if (sendingFileName == null) {
            GrandarLogUtils.i(TAG,
                    "SendUpdateSoftwareData sendingFileName==null");
            return;
        }

        try {

            File file = new File(sendingFileName);
            if (!file.exists()) {
                GrandarLogUtils.i(TAG,
                        "SendUpdateSoftwareData file not exitst ");
                return;
            }
            inData = new BufferedInputStream(new FileInputStream(
                    sendingFileName));
            // 当前包的包序号
            int packageNum = mp3sendCount - 1;

            // 当前包光曲数据长度
            int mp3DataLen = (int) ((packageNum < (mp3FrameCount - 1)) ? SEND_SONGLIGHT_DATA_LEN
                    : (file.length() - packageNum * SEND_SONGLIGHT_DATA_LEN));
            GrandarLogUtils.d(TAG, "SendUpdateSoftwareData mp3DataLen:"
                    + mp3DataLen + " packageNum:" + packageNum);

            int nSendsize = SEND_TRIGGER_LEN/*
                                             * CONST_INFO_LENGTH +
											 * SONGLIGHT_PACKAGE_NUM
											 * +mp3DataLen+ 1
											 */;
            // GrandarLogUtils.d(TAG, "SendMp3Time nSendsize=" + nSendsize);
            byte[] frameBuffer = new byte[nSendsize];
            Arrays.fill(frameBuffer, (byte) 0x00);
            nTestCount += nSendsize;
            // 1. 帧头： 固定为4个字节，AA 55 I G
            frameBuffer[0] = (byte) 0xAA;
            frameBuffer[1] = 0x55;
            frameBuffer[2] = 'I';
            frameBuffer[3] = 'G';

            // 数据长度 固定2个字节 低位在前，高位在后
            int len = CMD_LENGTH + KEEP_WORD_LENGTH + SONGLIGHT_PACKAGE_NUM
                    + mp3DataLen + 1;
            frameBuffer[4] = (byte) (len & 0x00FF);
            frameBuffer[5] = (byte) (len >> 8 & 0x00FF);

            // 命令字
            frameBuffer[6] = 0x04;
            frameBuffer[7] = (byte) 0x02;

            // 保留字
            frameBuffer[8] = 0x00;
            frameBuffer[9] = 0x00;
            frameBuffer[10] = 0x00;
            frameBuffer[11] = 0x00;

            // 当前包的包序号
            // int packageNum = getMp3sendCount()-1;
            frameBuffer[12] = (byte) (packageNum & 0x00FF); // 头8和9：
            frameBuffer[13] = (byte) ((packageNum >> 8) & 0x00FF);
            frameBuffer[14] = (byte) ((packageNum >> 16) & 0x00FF);
            frameBuffer[15] = (byte) ((packageNum >> 24) & 0x00FF);

            // 文件数据
            byte[] buffMp3Light = new byte[mp3DataLen];
            inData.skip(packageNum * SEND_SONGLIGHT_DATA_LEN);
            inData.read(buffMp3Light);
            // mReadFileOffset += buffMp3Light.length;
            // GrandarLogUtils.d(TAG, "SendMp3Time mReadFileOffset="
            // + mReadFileOffset);
            System.arraycopy(buffMp3Light, 0, frameBuffer, CONST_INFO_LENGTH
                    + SONGLIGHT_PACKAGE_NUM, buffMp3Light.length);

            // 加和校验位
            byte bCheck = 0x00;
            for (int i = 0; i < (CONST_INFO_LENGTH + SONGLIGHT_PACKAGE_NUM + mp3DataLen); i++) {
                bCheck += frameBuffer[i];
                // GrandarLogUtils.d(TAG, "SendMp3Time frameBuffer[i]="+
                // String.format("%2x", frameBuffer[i])+" i= "+i);
            }
            frameBuffer[CONST_INFO_LENGTH + SONGLIGHT_PACKAGE_NUM + mp3DataLen] = bCheck;
            // GrandarLogUtils.d(TAG, "SendMp3Time bCheck="+ bCheck);
            sendtoUdpServerTask(frameBuffer);
            //sendByteHandle(frameBuffer); // 发送数据

        } catch (IOException e) {
            GrandarLogUtils.e(TAG, "SendUpdateSoftwareData 发送帧数据 error:" + e);
        } catch (Exception e) {
            GrandarLogUtils.i(TAG, "SendUpdateSoftwareData Exception e:" + e);
        } finally {
            if (inData != null) {
                try {
                    // GrandarLogUtils.d(TAG, "SendMp3Time finMp3.close()");
                    inData.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    GrandarLogUtils.e(TAG,
                            "SendUpdateSoftwareData finMp3.close() error:" + e);
                }
            }
        }

        if (mp3sendCount == mp3FrameCount) {

            String str = String.format("发送完成,Mp3发送帧数：%d", Mp3sendCount);
            GrandarLogUtils.d(TAG, "SendUpdateSoftwareData " + str);
            Time time = new Time();
            time.setToNow();
            // nMinute = time.minute;
            // nSecond = time.second;
            mSendCompleteTime = System.currentTimeMillis() / 1000;
            long nEqual = mSendCompleteTime - mInitSendTime;
            nEqual = (nEqual == 0) ? 1 : nEqual;
            str = String.format("总秒数：%d, 平均每秒发送帧数：%d", nEqual,
                    (Mp3sendCount / nEqual));
            GrandarLogUtils.d(TAG, "SendUpdateSoftwareData " + str);
            SendFrameCmd((byte) 0x04, (byte) 0x03, null);
        }
    }

    private void SendMp3Time() {

        setMp3sendCount(getMp3sendCount() + 1);
        if (getInStreamMediaMode()) {
            mStreamSendCount = mStreamSendCount + 1;
        }
        /*
         * GrandarLogUtils.d(TAG, "SendMp3Time setMp3sendCount:" +
		 * getMp3sendCount());
		 */
        BufferedInputStream inMp3 = null;
        int mp3sendCount = getMp3sendCount();
        int mp3FrameCount = getMp3frameCount();
        String sendingFileName = getSendingFileName();
        if (mp3sendCount == 0) {
            GrandarLogUtils.i(TAG, "SendMp3Time mp3sendCount==0");
            return;
        }
        if (mp3FrameCount == 0) {
            GrandarLogUtils.i(TAG, "SendMp3Time mp3FrameCount==0");
            return;
        }
        if (sendingFileName == null) {
            GrandarLogUtils.i(TAG, "SendMp3Time sendingFileName==null");
            return;
        }

        try {

            // GrandarLogUtils
            // .i(TAG, "SendMp3Time lightOffset=" + lightOffset);
            File file = new File(sendingFileName);
            if (!file.exists()) {
                GrandarLogUtils.i(TAG, "SendMp3Time file not exitst ");
                return;
            }
            inMp3 = new BufferedInputStream(
                    new FileInputStream(sendingFileName));
            // 当前包的包序号
            int packageNum = mp3sendCount - 1;

            // 当前包光曲数据长度
            int mp3DataLen = (int) ((packageNum < (mp3FrameCount - 1)) ? SEND_SONGLIGHT_DATA_LEN
                    : (file.length() - packageNum * SEND_SONGLIGHT_DATA_LEN));
            /*
             * GrandarLogUtils.d(TAG, "SendMp3Time mp3DataLen:" + mp3DataLen +
			 * " packageNum:" + packageNum);
			 */

            int nSendsize = SEND_TRIGGER_LEN/*
                                             * CONST_INFO_LENGTH +
											 * SONGLIGHT_PACKAGE_NUM
											 * +mp3DataLen+ 1
											 */;
            // GrandarLogUtils.d(TAG, "SendMp3Time nSendsize=" + nSendsize);
            byte[] mFrameBuffer = new byte[nSendsize];
            Arrays.fill(mFrameBuffer, (byte) 0x00);
            nTestCount += nSendsize;
            // 1. 帧头： 固定为4个字节，AA 55 I G
            mFrameBuffer[0] = (byte) 0xAA;
            mFrameBuffer[1] = 0x55;
            mFrameBuffer[2] = 'I';
            mFrameBuffer[3] = 'G';

            // 数据长度 固定2个字节 低位在前，高位在后
            int len = CMD_LENGTH + KEEP_WORD_LENGTH + SONGLIGHT_PACKAGE_NUM
                    + mp3DataLen + 1;
            mFrameBuffer[4] = (byte) (len & 0x00FF);
            mFrameBuffer[5] = (byte) (len >> 8 & 0x00FF);

            // 命令字
            mFrameBuffer[6] = 0x03;
            mFrameBuffer[7] = (packageNum < (mp3FrameCount - 1)) ? (byte) 0x00
                    : (byte) 0x01;

            // 保留字
            mFrameBuffer[8] = 0x00;
            mFrameBuffer[9] = 0x00;
            mFrameBuffer[10] = 0x00;
            mFrameBuffer[11] = 0x00;

            // 当前包的包序号
            // int packageNum = getMp3sendCount()-1;
            mFrameBuffer[12] = (byte) (packageNum & 0x00FF); // 头8和9：
            mFrameBuffer[13] = (byte) ((packageNum >> 8) & 0x00FF);
            mFrameBuffer[14] = (byte) ((packageNum >> 16) & 0x00FF);
            mFrameBuffer[15] = (byte) ((packageNum >> 24) & 0x00FF);

            // 光曲文件数据
            // byte[] buffMp3Light = new byte[mp3DataLen];
            inMp3.skip(packageNum * SEND_SONGLIGHT_DATA_LEN);
            // inMp3.read(buffMp3Light);
            /*
             * System.arraycopy(buffMp3Light, 0, frameBuffer, CONST_INFO_LENGTH
			/*
			 * System.arraycopy(buffMp3Light, 0, frameBuffer, CONST_INFO_LENGTH
            /*
			 * System.arraycopy(buffMp3Light, 0, frameBuffer, CONST_INFO_LENGTH
			 * + SONGLIGHT_PACKAGE_NUM, buffMp3Light.length);
			 */
            inMp3.read(mFrameBuffer, CONST_INFO_LENGTH + SONGLIGHT_PACKAGE_NUM,
                    mp3DataLen);

            // 加和校验位
            byte bCheck = 0x00;
            for (int i = 0; i < (CONST_INFO_LENGTH + SONGLIGHT_PACKAGE_NUM + mp3DataLen); i++) {
                bCheck += mFrameBuffer[i];
                // GrandarLogUtils.d(TAG, "SendMp3Time frameBuffer[i]="+
                // String.format("%2x", frameBuffer[i])+" i= "+i);
            }
            mFrameBuffer[CONST_INFO_LENGTH + SONGLIGHT_PACKAGE_NUM + mp3DataLen] = bCheck;
            // GrandarLogUtils.d(TAG, "SendMp3Time bCheck="+ bCheck);
            sendtoUdpServerTask(mFrameBuffer);
            //sendByteHandle(mFrameBuffer); // 发送数据

        } catch (IOException e) {
            GrandarLogUtils.e(TAG, "SendMp3Time 发送帧数据 error:" + e);
        } catch (Exception e) {
            GrandarLogUtils.i(TAG, "SendMp3Time Exception e:" + e);
        } finally {
            if (inMp3 != null) {
                try {
                    // GrandarLogUtils.d(TAG, "SendMp3Time finMp3.close()");
                    inMp3.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    GrandarLogUtils.e(TAG, "SendMp3Time finMp3.close() error:"
                            + e);
                }
            }
        }

        if (mp3sendCount == mp3FrameCount) {

            String str = String.format("发送完成,Mp3发送帧数：%d", Mp3sendCount);
            GrandarLogUtils.d(TAG, "SendMp3Time " + str);
            if (!GrandarLogUtils.GRANDAR_LOG_ENABLED) {
                Log.d(TAG, "SendMp3Time " + str);
            }
            Time time = new Time();
            time.setToNow();
            // nMinute = time.minute;
            // nSecond = time.second;
            mSendCompleteTime = System.currentTimeMillis() / 1000;
            long nEqual = mSendCompleteTime - mInitSendTime;
            nEqual = (nEqual == 0) ? 1 : nEqual;
            str = String.format("总秒数：%d, 平均每秒发送帧数：%d", nEqual,
                    (Mp3sendCount / nEqual));
            GrandarLogUtils.d(TAG, "SendMp3Time " + str);

            SharedPreferences sharedPreferences = getSharedPreferences(
                    "SendDataSpeedLog", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(time.toString() + " speed", (Mp3sendCount * SEND_TRIGGER_LEN) / nEqual);
            editor.commit();
            if (!GrandarLogUtils.GRANDAR_LOG_ENABLED) {
                Log.d(TAG, "SendMp3Time " + str);
            }
        }
    }

    // 参数是 该MP3在播放列表的序号 从1开始
    public void SendMp3Init(int serial_num) {

        KLog.e("SendMp3Init serial_num is " + serial_num);

        Cursor listCursor = getFilesListCursor();
        if (listCursor == null) {
            GrandarLogUtils.e(TAG, "SendMp3Init filesListCursor == null");
            return;
        }

        if ((serial_num > listCursor.getCount()) || (serial_num <= 0)) {
            GrandarLogUtils.e(TAG,
                    "SendMp3Init serial_num > filesListCursor.getCount() count="
                            + listCursor.getCount());
            return;
        }

        int pos = getDeviceReqPosToRealPos(serial_num - 1);
        listCursor.moveToPosition(pos);
        // setSendingFileName(null);
        String fileNameString;
        if (listCursor.getString(listCursor
                .getColumnIndexOrThrow(SongLightStore.Playlists.Members.MODE)) != null) {
            fileNameString = FileHelper.FILEPATH
                    + "/"
                    + listCursor
                    .getString(listCursor
                            .getColumnIndexOrThrow(SongLightStore.Playlists.Members.MODE))
                    + "/";
            if (listCursor
                    .getString(listCursor
                            .getColumnIndexOrThrow(SongLightStore.Playlists.Members.SUBMODE)) != null) {
                fileNameString = fileNameString
                        + listCursor
                        .getString(listCursor
                                .getColumnIndexOrThrow(SongLightStore.Playlists.Members.SUBMODE))
                        + "/";
            }
            fileNameString = fileNameString
                    + listCursor
                    .getString(listCursor
                            .getColumnIndexOrThrow(SongLightStore.Playlists.Members.NAME));
        } else {
            fileNameString = listCursor
                    .getString(listCursor
                            .getColumnIndexOrThrow(SongLightStore.Playlists.Members.NAME));
        }

        GrandarLogUtils.e(TAG, "SendMp3Init strFileName =" + fileNameString);

        File file = new File(fileNameString);
        if (!file.exists()) {
            GrandarLogUtils.i(TAG, "SendMp3Init file not exitst ");
            return;
        }

        // 带音乐的光曲
        stopMp3Data();
//        if (MyUtil.isSongAndLight(MyUtil.getSongLightType(fileNameString))) {
//            createMp3Data(fileNameString,"",0);
//        }
        playMp3Data();

        // 初始化 发送光曲文件 所需的各种变量
        setMp3_SendAllVar(0, 0, true, fileNameString);

        String str = "";

        Time time = new Time();
        time.setToNow();
        // nMinuteOld = time.minute;
        // nSecondOld = time.second;
        mInitSendTime = System.currentTimeMillis() / 1000;

        int filelength = (int) file.length();
        int sendtimes = 0;
        if (filelength % SEND_SONGLIGHT_DATA_LEN == 0) {
            sendtimes = filelength / SEND_SONGLIGHT_DATA_LEN;
        } else {
            sendtimes = (filelength / SEND_SONGLIGHT_DATA_LEN) + 1;
        }
        setMp3frameCount(sendtimes);
        GrandarLogUtils.i(TAG, "SendMp3Init setMp3frameCount(sendtimes):"
                + getMp3frameCount());

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
        GrandarLogUtils.i(TAG, "getDeviceReqPosToRealPos total=" + total
                + " posIndex=" + posIndex + " beginIndex=" + beginIndex
                + " pos=" + pos);
        return pos;
    }

    private void processStreamDataBuf() {
        // if (mStreamDataBuf.size() > 0) {
        byte[] readBuf;
        byte[] sendBuf = new byte[SEND_SONGLIGHT_DATA_LEN];
        byte[] leftBuf;

        int len = 0;
        synchronized (mStreamDataBufLock) {
            while (mStreamDataBuf.size() > 0) {
                readBuf = mStreamDataBuf.get(0);
                /*
                 * GrandarLogUtils.d(TAG, "processStreamDataBuf 1 readBuf.length=" +
				 * readBuf.length + " mStreamDataBuf.size()=" +
				 * mStreamDataBuf.size());
				 */
                if ((len + readBuf.length) > SEND_SONGLIGHT_DATA_LEN) {
                    leftBuf = new byte[readBuf.length
                            - (SEND_SONGLIGHT_DATA_LEN - len)];
                    System.arraycopy(readBuf, 0, sendBuf, len,
                            SEND_SONGLIGHT_DATA_LEN - len);
                    System.arraycopy(readBuf, SEND_SONGLIGHT_DATA_LEN - len,
                            leftBuf, 0, readBuf.length
                                    - (SEND_SONGLIGHT_DATA_LEN - len));
                    mStreamDataBuf.remove(0);
                    mStreamDataBuf.add(0, leftBuf);
                    len = SEND_SONGLIGHT_DATA_LEN;
                    /*
                     * GrandarLogUtils.d(TAG,
					 * "processStreamDataBuf 2 leftBuf.length=" + leftBuf.length +
					 * " len=" + len);
					 */
                    break;
                } else if ((len + readBuf.length) == SEND_SONGLIGHT_DATA_LEN) {
                    System.arraycopy(readBuf, 0, sendBuf, len, readBuf.length);
                    mStreamDataBuf.remove(0);
                    len = SEND_SONGLIGHT_DATA_LEN;
                    /* GrandarLogUtils.d(TAG, "processStreamDataBuf 3 len=" + len); */
                    break;
                } else {
                    System.arraycopy(readBuf, 0, sendBuf, len, readBuf.length);
                    mStreamDataBuf.remove(0);
                    len = len + readBuf.length;
                    /* GrandarLogUtils.d(TAG, "processStreamDataBuf 4 len=" + len); */
                }
            }
        }
        /*
         * if ((len >= SEND_SONGLIGHT_DATA_LEN) || (streamMediaDataSendOver ==
		 * true))
		 */
        if (len > 0) {
            // 发送流媒体数据包
            /* GrandarLogUtils.d(TAG, "processStreamDataBuf 5 len=" + len); */
            mStreamSendCount = mStreamSendCount + 1;
            sendStreamMediaDataPackage(sendBuf, len);
        }
        // }
    }

    private void sendStreamMediaDataPackage(byte[] sendBuf, int dataLen) {
        // TODO Auto-generated method stub
        // byte bCheck = 0;
        int dataLength = 0, bCount = 0;

        if (sendBuf != null) {
            dataLength = dataLen;
/*			GrandarLogUtils.d(TAG, "sendStreamMediaDataPackage 1 dataLength="
                    + dataLength);*/
        }

        // int nSendsize = CONST_INFO_LENGTH + dataLength + 1;
        // 当前包的包序号
        int packageNum = 0;

        // 当前包光曲数据长度
        int mp3DataLen = dataLen;

        int nSendsize = SEND_TRIGGER_LEN;
        // GrandarLogUtils.d(TAG, "SendMp3Time nSendsize=" + nSendsize);
        byte[] frameBuffer = new byte[nSendsize];
        Arrays.fill(frameBuffer, (byte) 0x00);
        nTestCount += nSendsize;
        // 1. 帧头： 固定为4个字节，AA 55 I G
        frameBuffer[0] = (byte) 0xAA;
        frameBuffer[1] = 0x55;
        frameBuffer[2] = 'I';
        frameBuffer[3] = 'G';

        // 数据长度 固定2个字节 低位在前，高位在后
        int len = CMD_LENGTH + KEEP_WORD_LENGTH + SONGLIGHT_PACKAGE_NUM
                + mp3DataLen + 1;
        frameBuffer[4] = (byte) (len & 0x00FF);
        frameBuffer[5] = (byte) (len >> 8 & 0x00FF);

        // 命令字
        frameBuffer[6] = 0x03;
        frameBuffer[7] = (byte) 0x00;

        // 保留字
        frameBuffer[8] = 0x00;
        frameBuffer[9] = 0x00;
        frameBuffer[10] = 0x00;
        frameBuffer[11] = 0x00;

        // 当前包的包序号
        // int packageNum = getMp3sendCount()-1;
        frameBuffer[12] = (byte) (packageNum & 0x00FF); // 头8和9：
        frameBuffer[13] = (byte) ((packageNum >> 8) & 0x00FF);
        frameBuffer[14] = (byte) ((packageNum >> 16) & 0x00FF);
        frameBuffer[15] = (byte) ((packageNum >> 24) & 0x00FF);

        // 光曲文件数据
        // byte[] buffMp3Light = new byte[mp3DataLen];
        // inMp3.skip(packageNum * SEND_SONGLIGHT_DATA_LEN);
        // inMp3.read(buffMp3Light);
        // mReadFileOffset += buffMp3Light.length;
        // GrandarLogUtils.d(TAG, "SendMp3Time mReadFileOffset="
        // + mReadFileOffset);
        System.arraycopy(sendBuf, 0, frameBuffer, CONST_INFO_LENGTH
                + SONGLIGHT_PACKAGE_NUM, dataLen);

        // 加和校验位
        byte bCheck = 0x00;
        for (int i = 0; i < (CONST_INFO_LENGTH + SONGLIGHT_PACKAGE_NUM + mp3DataLen); i++) {
            bCheck += frameBuffer[i];
            // GrandarLogUtils.d(TAG, "SendMp3Time frameBuffer[i]="+
            // String.format("%2x", frameBuffer[i])+" i= "+i);
        }
        frameBuffer[CONST_INFO_LENGTH + SONGLIGHT_PACKAGE_NUM + mp3DataLen] = bCheck;
        // GrandarLogUtils.d(TAG, "SendMp3Time bCheck="+ bCheck);
        sendtoUdpServerTask(frameBuffer);
        //sendByteHandle(frameBuffer); // 发送数据
    }

    void sendRemoteControlFrame(byte[] cmd, byte[] data) {
        short len = (short) (data != null ? data.length : 0);

        byte[] buff = new byte[2 + (data != null ? data.length : 0)
                + cmd.length];
        System.arraycopy(cmd, 0, buff, 0, cmd.length);
        buff[cmd.length] = (byte) (len & 0x00FF);
        buff[cmd.length + 1] = (byte) ((len >> 8) & 0x00FF);
        if (data != null && data.length > 0) {
            System.arraycopy(data, 0, buff, cmd.length + 2, data.length);
        }

        KLog.e("------>" + Arrays.toString(buff));
        StringBuffer msg = new StringBuffer();
        for (int i = 0; i < buff.length; i++) {
            msg.append(String.format(" %2x", buff[i]));
        }
        KLog.e("--->sendUdptoServer:" + msg.toString());
        if (GrandarLogUtils.DEBUG_ON_UDP) {
            KLog.e("sendtoUdpServerTask");
            sendtoUdpServerTask(buff);
        } else {
            KLog.e("sendtoServerTask");
            sendtoServerTask(buff);
        }
    }

}
