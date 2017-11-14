package meekux.grandar.com.meekuxpjxroject.view;

import android.content.Context;
import android.os.AsyncTask;

import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.socks.library.KLog;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MarketMusicBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.DeviceDbUtil;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.MarketMusicDbUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import meekux.grandar.com.meekuxpjxroject.songdata.database.MyConstant;
import meekux.grandar.com.meekuxpjxroject.songdata.database.MyUtil;

/**
 * Created by baixiaoming on 2017/3/21 16:52
 * Function
 */

public class EsptouchAsyncTask3 extends AsyncTask<String, Void, List<IEsptouchResult>> {
    private static final String TAG = "EsptouchAsyncTask3";
    private final Object mLock = new Object();
    private IEsptouchTask mEsptouchTask;
    private Context context;
    private boolean isCancelled;
//    private List<IEsptouchResult> arr = new ArrayList<>();


    public EsptouchAsyncTask3(Context context) {
        this.context = context;
        KLog.e(TAG, "开始处理");
    }

    public void setIsCancel(boolean isCancel) {
        isCancelled = isCancel;
    }

    // 0:不是正确的id 1：是正确的id 2：此socket不要close
    private String checkControllerID(byte[] buf, String ip) {
        KLog.e(TAG, "处理结果--buf------》》》》 :" +
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

    @Override
    protected List<IEsptouchResult> doInBackground(String... params) {
        int taskResultCount = -1;
        synchronized (mLock) {
            String apSsid = params[0];
            String apBssid = params[1];
            String apPassword = params[2];
            String isSsidHiddenStr = params[3];
            String taskResultCountStr = params[4];
            KLog.e(TAG, "处理结果--buf------》》》》 :" + apSsid + "---" + apBssid + "---"
                    + apPassword + "---" + isSsidHiddenStr + "---" + taskResultCountStr);
            boolean isSsidHidden = false;
            if (isSsidHiddenStr.equals("YES")) {
                isSsidHidden = true;
            }
            taskResultCount = Integer.parseInt(taskResultCountStr);
            mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword,
                    isSsidHidden, context);
            mEsptouchTask.setEsptouchListener(null);
        }
        List<IEsptouchResult> resultList = mEsptouchTask
                .executeForResults(taskResultCount);

        KLog.e("resultList--->" + resultList.size());
        List<IEsptouchResult> snList = new ArrayList<IEsptouchResult>();
        // 一键配置有成功的设备 则进行下一步
        for (int i = 0; i < resultList.size(); i++) {
            IEsptouchResult firstResult = resultList.get(i);
            if (!firstResult.isCancelled()) {
                if (firstResult.isSuc()) { // 获取成功 建立tcp连接 获取控制器id号
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        KLog.e(TAG + "异常----", e1.getMessage() + "...");
                    }
//                    for (IEsptouchResult resultInList : resultList) {
                    if (isCancelled) {
                        break;
                    }
                    Socket socket = new Socket();
                    try {
                        SocketAddress remoteAddr = new InetSocketAddress(
                                resultList.get(i).getInetAddress(),
                                MyConstant.TCPSERVERPORT);
                        String srvIp = resultList.get(i).getInetAddress()
                                .getHostAddress();
                        String sn = resultList.get(i).getSnString();
                        KLog.e(TAG, srvIp + " 客户端开始连接" + resultList.get(i).getSnString());
                        if (!socket.getTcpNoDelay()) {
                            socket.setTcpNoDelay(true);
                        }
                        socket.connect(remoteAddr, 3000);
                        KLog.e(TAG, srvIp + " 客户端连接成功!");
                        // 请求获取控制器id
                        OutputStream out = socket.getOutputStream();
                        out.write(MyConstant.addprotocol(MyConstant.GET_SN,
                                null));// 请求服务器发送控制器id
                        out.flush();

                        InputStream inputStream = socket.getInputStream();
                        byte[] buffer = new byte[256];
                        socket.setSoTimeout(1000);
                        int temp = 0;
                        while ((temp = inputStream.read(buffer)) != -1) {
                            KLog.e(TAG, "ClientThread mSrvIp:"
                                    + srvIp + " read temp:" + temp + "sn---" + sn);

                            if (true) {
                                // 检测是否是得到的15位控制器id
                                byte[] buff = new byte[temp];
                                System.arraycopy(buffer, 0, buff, 0, temp);
                                String result = checkControllerID(buff,
                                        srvIp);
                                if (result != null) {
                                    KLog.e(TAG, "得到结果---ClientThread mSrvIp:" + srvIp
                                            + " 读到控制器id" + result);
                                    resultList.get(i).setSnString(result);
                                    KLog.e(resultList);
                                    snList.add(resultList.get(i));
                                    break;
                                }
                            }
                        }
                    } catch (SocketException e) {
                        KLog.e(TAG,
                                "ClientThread SocketException e:" + e);
                    } catch (IOException e) {
                        KLog.e(TAG,
                                "ClientThread IOException e:" + e);
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
                            KLog.e(TAG, "线程结束ClientThread closesocket IOException e:"
                                    + e);
                        }
                    }
//                    }
                }
            }
        }

        return snList;
    }


    @Override
    protected void onPostExecute(List<IEsptouchResult> result) {
        if (isCancelled) {
            return;
        }
        if (result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                // 根据sn查询数据库是否存在已有的sn标识，如果没有则储存当前数据。
                MyDeviceBean data = DeviceDbUtil.getInstance().queryTimeBy(result.get(i).getSnString());
                // 存储所有设备
                if (data == null) {
                    MyDeviceBean bean = new MyDeviceBean();
                    String sn = result.get(i).getSnString().substring(0, 4);
                    bean.setSn(result.get(i).getSnString());
                    bean.setName(sn);
                    bean.setIp(result.get(i).getInetAddress().getHostAddress());
                    DeviceDbUtil.getInstance().saveTime(bean);
                } else {
                    data.setIp(result.get(i).getInetAddress().getHostAddress());
                    DeviceDbUtil.getInstance().updateTime(data);
                }
                MyDeviceBean datas = DeviceDbUtil.getInstance().queryTimeBy(result.get(i).getSnString());
                // 存储未来商场设备
                if (datas.getName() != null) {
                    MarketMusicBean bens = MarketMusicDbUtil.getInstance().queryTimeBy(datas.getName());
                    if (bens == null && datas.getName().substring(0, 1).equals("P") && datas.getName().substring(3, 4).equals("0") || datas.getName().substring(3, 4).equals("B")) {
                        MarketMusicBean bean = new MarketMusicBean();
                        bean.setSn(datas.getSn());
                        bean.setIp(datas.getIp());
                        bean.setName(datas.getName());
                        MarketMusicDbUtil.getInstance().saveTime(bean);
                    } else if (bens != null && datas.getName().substring(0, 1).equals("P") && datas.getName().substring(3, 4).equals("0") || datas.getName().substring(3, 4).equals("B")) {
                        bens.setIp(datas.getIp());
                        MarketMusicDbUtil.getInstance().updateTime(bens);
                    }
                }

            }
            // 成功
            if (listener != null) listener.backMsg(1);
        } else {
            // 失败
            if (listener != null) listener.backMsg(0);
        }
    }

    public interface EsptouchListener {
        void backMsg(int what);
    }

    private EsptouchListener listener;

    public void setEsptouchListener(EsptouchListener listener) {
        this.listener = listener;
    }
}
