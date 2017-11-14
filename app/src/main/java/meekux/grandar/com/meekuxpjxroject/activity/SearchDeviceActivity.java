package meekux.grandar.com.meekuxpjxroject.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ListView;

import com.socks.library.KLog;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.subscribers.MyDialog;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.DeviceDbUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import meekux.grandar.com.meekuxpjxroject.GlobalApplication;
import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.adapter.MySearchAdapter;
import meekux.grandar.com.meekuxpjxroject.entity.SearchBean;
import meekux.grandar.com.meekuxpjxroject.songdata.database.GrandarUtils;
import meekux.grandar.com.meekuxpjxroject.songdata.database.MyConstant;
import meekux.grandar.com.meekuxpjxroject.songdata.database.MyUtil;
import meekux.grandar.com.meekuxpjxroject.utils.IConstant;
import meekux.grandar.com.meekuxpjxroject.utils.LogUtils;
import meekux.grandar.com.meekuxpjxroject.utils.SharedPreferencesUtils;
import meekux.grandar.com.meekuxpjxroject.utils.SharedPutils;
import meekux.grandar.com.meekuxpjxroject.utils.ToastUtils;
import meekux.grandar.com.meekuxpjxroject.view.TopView;

public class SearchDeviceActivity extends BaseActivity implements Serializable {
    private String curConnSn;
    private static final String TAG = "SearchDeviceActivity";
    @Bind(R.id.right_view)
    TopView mRightView;
    @Bind(R.id.search_device)
    ListView mSearchDevice;
    private List<SearchBean> list;
    private MySearchAdapter mDeviceAdapter;
    private boolean isBreak;
    private MyDialog dialog;
    private String newIp = "";
    private String connSn;
    private IntentFilter mFilter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    String str = (String) msg.obj;
                    KLog.e(str);
                    if (!TextUtils.isEmpty(str)) {
                        String[] strs = str.split(",");
                        String sn = strs[1];
                        String ip = strs[0];
                        String sn_id = sn.substring(0, 4);
                        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(sn)) return;
                        SearchBean bean = new SearchBean();
                        String substring = sn.substring(0, 4);
                        if (!substring.contains("f")) {
                            bean.setSn(sn);
                            bean.setIp(ip);
                            startTcpClientService(null, bean.getIp(), bean.getSn(), 2);
                            KLog.e("当前ip:" + bean.getIp() + "当前sn:" + bean.getSn());
                            list.add(bean);
                            mDeviceAdapter.notifyDataSetChanged();
                            // 存储所有设备
                            MyDeviceBean data = DeviceDbUtil.getInstance().queryTimeBy(sn);
                            if (data == null) {
                                MyDeviceBean beans = new MyDeviceBean();
                                beans.setSn(sn);
                                beans.setName(sn_id);
                                beans.setIp(ip);
                                DeviceDbUtil.getInstance().saveTime(beans);
                            } else {
                                data.setIp(ip);
                                data.setSn(sn);
                                data.setName(sn_id);
                                DeviceDbUtil.getInstance().updateTime(data);
                            }
                        }
                    }
                    break;
                case 1:
                    dialog.dismiss();
                    try {
                        String dSn = list.get(0).getSn();
                        SharedPutils sharedPutils = new SharedPutils();
                        sharedPutils.putString(getBaseContext(), "deviceSn", dSn);
                        KLog.e("snList储存:" + dSn);
                        for (SearchBean listSn : list) {
                            String snList = listSn.getSn();
                            SharedPutils sharedSnList = new SharedPutils();
                            sharedSnList.putList(getBaseContext(), "snList", list);
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                    break;
            }
        }
    };

    @Override
    protected int getResouseID() {
        return R.layout.activity_search_device;
    }

    @Override
    protected void init() {
        GlobalApplication.getInstance().addActivity(this);
        mFilter = new IntentFilter();
        mFilter.addAction(MyConstant.switchtomain);
        mFilter.addAction(MyConstant.ACTION_RC_SOCKET_CLOSE);
        mFilter.setPriority(1000);
        registerReceiver(myReceiver, mFilter);
        dialog = MyDialog.createDialog(this);
        dialog.show();
        curConnSn = SharedPreferencesUtils.getinstance().getStringValue(IConstant.LAMPSN);
        KLog.e(curConnSn);
        mRightView.setRightTitle(getResources().getString(R.string.search));

        //获取当前ip
        String ip = getlocalip();
        KLog.e(ip);
        if (TextUtils.isEmpty(ip)) {
            ToastUtils.show("请连接无线网");
        } else {
            //开始搜索
            newIp = ip.substring(0, ip.lastIndexOf(".") + 1);
            KLog.e(newIp);
            test(newIp);
        }
        list = new ArrayList<>();
        mDeviceAdapter = new MySearchAdapter(this, list);
        mSearchDevice.setAdapter(mDeviceAdapter);
    }

    @Override
    protected void click() {
        mRightView.setOnTopviewListener(() -> {
            if (!TextUtils.isEmpty(newIp)) {
                isBreak = false;
                dialog.show();
                list.clear();
                mDeviceAdapter.notifyDataSetChanged();
                test(newIp);
            } else {
                ToastUtils.show("请连接无线网");
            }
        });
        dialog.setOnDismissListener(dialog1 -> isBreak = true);
        mDeviceAdapter.setOnSearchImp(position -> {
//            SearchBean bean1 = list.get(position);
//            KLog.e(connSn);
//            KLog.e("开始同步" + bean1.getIp() + "..." + bean1.getSn());
//            connSn = bean1.getSn();
//            connIp = bean1.getIp();
//            startTcpClientService(null, bean1.getIp(), bean1.getSn(), 2);
            SearchBean bean1 = list.get(position);
            KLog.e(connSn);
            KLog.e("开始同步" + bean1.getIp() + "..." + bean1.getSn());
            connSn = bean1.getSn();
            startTcpClientService(null, bean1.getIp(), bean1.getSn(), 2);
            for (int i = 0; i < list.size(); i++) {
                MyDeviceBean myDeviceBean = DeviceDbUtil.getInstance().queryTimeBy(list.get(i).getSn());
                if (myDeviceBean == null) {
                    //不存在
                    myDeviceBean = new MyDeviceBean();
                    if (!TextUtils.isEmpty(list.get(i).getSn()) && !TextUtils.isEmpty(list.get(i).getIp())) {
                        myDeviceBean.setIp(list.get(i).getIp());
                        myDeviceBean.setSn(list.get(i).getSn());
                        myDeviceBean.setVersion(list.get(i).getVersion());
                        KLog.e("当前类别--->" + list.get(i).getSn());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        String date = sdf.format(new Date());
                        myDeviceBean.setDate(date);
                        DeviceDbUtil.getInstance().saveTime(myDeviceBean);
                        KLog.e("保存");
                    }

                } else {
                    myDeviceBean.setIp(list.get(i).getIp());
//                    KLog.e("类型--->" + myDeviceBean.getLampType());
                    DeviceDbUtil.getInstance().updateTime(myDeviceBean);
                    KLog.e("更新");
                }
            }
        });
    }

    private void connTest(String ip) {
        Socket socket = new Socket();
        try {
            SocketAddress remoteAddr = new InetSocketAddress(ip, MyConstant.TCPSERVERPORT);

            if (!socket.getTcpNoDelay()) {
                socket.setTcpNoDelay(true);
            }
            socket.connect(remoteAddr, 3000);
            KLog.e(ip + " 客户端连接成功!");
            // 请求获取控制器id
            OutputStream out = socket.getOutputStream();
            out.write(MyConstant.addprotocol(MyConstant.GET_SN,
                    null));// 请求服务器发送控制器id6
            out.flush();
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[256];
            socket.setSoTimeout(10000);   //Socket10秒未做处理既断开
            int temp = 0;
            while ((temp = inputStream.read(buffer)) != -1) {
                KLog.e("开始输出");
                // 检测是否是得到的15位控制器id
                byte[] buff = new byte[temp];
                System.arraycopy(buffer, 0, buff, 0, temp);
                String result = checkControllerID(buff
                );
                if (result != null) {
                    KLog.e(result);
                    Message msg = Message.obtain();
                    msg.obj = ip + "," + result;
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                }
            }
        } catch (SocketException e) {
            KLog.e("SocketException" + e.getMessage());
        } catch (IOException e) {
            KLog.e("IOException" + e.getMessage());
        } finally {
            try {
                if (!socket.isInputShutdown()) {
                    socket.shutdownInput();
                }
                if (!socket.isOutputShutdown()) {
                    socket.shutdownOutput();
                }
                socket.close();
            } catch (IOException e) {
                KLog.e("结果" + e.getMessage());
            }
        }
    }

    // 0:不是正确的id 1：是正确的id 2：此socket不要close
    private String checkControllerID(byte[] buf) {
        LogUtils.e(TAG, "处理结果--buf------》》》》 :" +
                buf.toString().length() + "...." + Arrays.toString(buf) + "..." + buf.length);
        if (buf.length < 20) return null;
        if ((buf[0] != (byte) 0xAA) && (buf[1] != (byte) 0x55)
                && (buf[0] != (byte) 0x0A) && (buf[1] != (byte) 0x0F))
            return null;

        byte[] typeBuf = new byte[5];
        byte[] versionBuf = new byte[4];
        byte[] idBuf = new byte[12];
        System.arraycopy(buf, 6, typeBuf, 0, typeBuf.length);
        System.arraycopy(buf, 11, versionBuf, 0, versionBuf.length);
        System.arraycopy(buf, 15, idBuf, 0, idBuf.length);
        String type = new String(typeBuf);
        //16进制byte数组转int
        int len = versionBuf.length;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            sb = sb.append(versionBuf[i]);
        }
        int version = Integer.parseInt(sb.toString(), 16);

        String controlId = MyUtil.HexToString(idBuf);

        KLog.e(TAG, "处理结果--checkControllerID controlId:" + controlId + " type:" + type + " version:" + version);
        KLog.e(TAG, "处理结果--:" + Arrays.toString(idBuf));
        return type + version + controlId;
    }

    private String getlocalip() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        if (ipAddress == 0) return "";
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }

    //falg表示不同界面收广播处理事件 0表示主页面  1落地  2表示搜索 3配置，4台灯，5吸顶
    private void startTcpClientService(Socket socket, String ip, String sn, int flag) {
        KLog.e(TAG, "startTcpClientService socket:" + socket
                + " ip:" + ip + " id:" + sn);
        List<MyDeviceBean> data = DeviceDbUtil.getInstance().queryTimeAll();
        GrandarUtils.startTcpClient(data, true);
    }


    DatagramSocket detectSocket;
    Timer timer;
    ArrayList<String> resultString = new ArrayList<>();

    private void test(final String ip) {
        resultString.clear();
        try {
            if (detectSocket == null) {
                detectSocket = new DatagramSocket();
            }
        } catch (Exception e) {

        }
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            //间隔三秒
            int flag = 0;

            @Override
            public void run() {
                if (flag == 3) {
                    mHandler.sendEmptyMessage(1);
                    timer.cancel();
                    timer = null;
                    flag = 0;
                }
                flag++;

                try {

                    byte[] buf = MyConstant.addprotocol(MyConstant.GET_SN,
                            null);
                    int packetPort = MyConstant.UDPSERVERPORT;
                    InetAddress hostAddress = InetAddress.getByName(ip + "255");
                    DatagramPacket out = new DatagramPacket(buf,
                            buf.length, hostAddress, packetPort);
                    detectSocket.send(out);

                } catch (Exception e) {

                }
            }
        }, 1000, 1500);
        new Thread(() -> {
            while (!isBreak) {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    if (packet != null) {
                        detectSocket.receive(packet);
                        byte[] buffer = packet.getData();
                        System.arraycopy(buffer, 0, buffer, 0, buffer.length);
                        String result = checkControllerID(buffer);
                        String resultIp = packet.getAddress() + "";
                        KLog.e("搜索到数据===========", "Received from" + result + "   " + packet.getAddress());
                        if (result != null) {
                            KLog.e(result);
                            String result_sn = result.substring(0, 1);
                            if (!resultString.contains(result)) {
                                resultString.add(result);
                                if (result_sn.equals("R") || result_sn.equals("A")) {
                                }
                                Message msg = Message.obtain();
                                msg.obj = resultIp.replace("/", "") + "," + result;
                                msg.what = 0;
                                mHandler.sendMessage(msg);

                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取当前ip
        String ip = getlocalip();
        newIp = ip.substring(0, ip.lastIndexOf(".") + 1);
        KLog.e(newIp);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            int flag = intent.getIntExtra("flag", -1);
            String sn = intent.getStringExtra("sn");
            String ip = intent.getStringExtra("ip");
            KLog.e("flag---" + flag + "--" + intent.getAction() + "--" + sn + "---" + ip);
            if (flag != 2) return;
            KLog.e("搜索连接成功的广播");
            if (intent.getAction().equals(MyConstant.switchtomain)) {
                KLog.e("连接成功");
                ToastUtils.show("连接成功");
                SharedPreferencesUtils.getinstance().setStringValue(IConstant.LAMPSN, sn);
                SharedPreferencesUtils.getinstance().setStringValue(IConstant.LAMPIP, ip);
                GlobalApplication.getInstance().setConnect(true);
                MyDeviceBean myDeviceBean = DeviceDbUtil.getInstance().queryTimeBy(sn);
                if (myDeviceBean == null) {
                    //不存在
                    myDeviceBean = new MyDeviceBean();
                    if (!TextUtils.isEmpty(sn) && !TextUtils.isEmpty(ip)) {
                        String sns = sn.substring(2, 3);
                        if (sns.equals("f")) {
                            myDeviceBean.setName("落地灯");
                        } else if (sns.equals("c")) {
                            myDeviceBean.setName("吸顶灯");
                        } else {
                            myDeviceBean.setName("台灯");
                        }
                        myDeviceBean.setIp(ip);
                        myDeviceBean.setSn(sn);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        String date = sdf.format(new Date());
                        myDeviceBean.setDate(date);
                        DeviceDbUtil.getInstance().saveTime(myDeviceBean);
                        KLog.e("保存");
                    }

                } else {
                    KLog.e(myDeviceBean.getSid());
                    myDeviceBean.setSid(myDeviceBean.getSid());
                    myDeviceBean.setName(myDeviceBean.getName());
                    myDeviceBean.setIp(ip);
                    myDeviceBean.setSn(myDeviceBean.getSn());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    String date = sdf.format(new Date());
                    myDeviceBean.setDate(date);
                    DeviceDbUtil.getInstance().updateTime(myDeviceBean);
                    KLog.e("更新");
                }
                GlobalApplication.getInstance().exit();
            } else {
                KLog.e("连接失败");
                GlobalApplication.getInstance().setConnect(false);
                ToastUtils.show("连接失败，请重试...");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }
}
