package meekux.grandar.com.meekuxpjxroject.songdata.database;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Enumeration;

public class MyConstant {

    //other 其它定义，另外消息长度为60个汉字，utf-8中定义一个汉字占3个字节，所以消息长度为180bytes
    //文件长度为30个汉字，所以总长度为90个字节
    public static int brightness;
    public static final String MULTICAST_IP = "239.9.9.1";
    public static final int PORT = 5760;
    public static final int TCPSERVERPORT = 6495;  //服务器端口号
    public static final int UDPSERVERPORT = 6496;  //udp服务器端口号
    public static final int UDP_BUFFSIZE = 1024;
    public static final int TCP_BUFFSIZE = 1024;
    public static String actor = null;
    //public static boolean recvfile = false;
    public static final byte[] FRAME_HEAD = {(byte) 0xAA, (byte) 0x55};
    //baby协议
    public static final byte[] POWER_ON = {(byte) 0xAA, (byte) 0x55, 0x01, 0x01}; //开灯
    public static final byte[] POWER_OFF = {(byte) 0xAA, (byte) 0x55, 0x01, 0x02}; //关灯
    public static final byte[] NIGHT_LIGHT = {(byte) 0xAA, (byte) 0x55, 0x01, 0x03}; //夜灯
    // 注释部分是落地灯协议
//    public static final byte[] POWER_ON = {(byte) 0xAA, (byte) 0x55,'I','G', 0x01, 0x01}; //开灯
//    public static final byte[] POWER_OFF = {(byte) 0xAA, (byte) 0x55,'I','G', 0x01, 0x02}; //关灯
//    public static final byte[] NIGHT_LIGHT = {(byte) 0xAA, (byte) 0x55,'I','G', 0x01, 0x03}; //夜灯
    public static final byte[] CHANGE_BIGHTNESS = {(byte) 0xAA, (byte) 0x55, 0x01, 0x04}; //亮度调节
    public static final byte[] LIGHT_FRAME = {(byte) 0xAA, (byte) 0x55, 0x01, 0x05}; //单光效控制
    public static final byte[] REQ_SEND_LIGHT_FRAME = {(byte) 0xAA, (byte) 0x55, 0x01, 0x06}; //申请发送单光效

    //baby专用
    public static final byte[] GET_SN = {(byte) 0xAA, (byte) 0x55, 0x01, 0x0F}; //获取灯体SN号
    //落地灯
//    public static final byte[] GET_SN = {(byte) 0xAA, (byte) 0x55, 0x01, 0x0F}; //获取灯体SN号

    public static final int FRAME_HEAD_LENGTH = 2;
    public static final int CMD_LENGTH = 2;
    public static final int BYTES_OF_FRAME_LENGTH = 2; //使用2个字节来存储数据帧的长度

    //远程控制广播消息
    public static final String chatAction = "com.igoo.igoobaby.launcher.service.chatAction";
    //连接成功进入主页面
    public static final String switchtomain = "com.grandar.igoo.igooproject.service.switchtomain";
    public static final String mainExit = "com.grandar.igoo.igooproject.service.mainExit";
    public static final String initExit = "com.igoo.igoobaby.launcher.service.initExit";
    public static final String ACTION_RC_SOCKET_CLOSE = "com.igoo.igoobaby.launcher.service.socket_close_action";
    public static final String ACTION_RC_SERVER_CMD = "com.igoo.igoobaby.launcher.service.server_cmd";
    public static final String ACTION_RC_SERVER_CMD_PARAM = "param";
    public static final byte ACTION_RC_SERVER_CMD_POWER_ON = 0x01;
    public static final byte ACTION_RC_SERVER_CMD_POWER_OFF = 0x02;
    public static final byte ACTION_RC_SERVER_CMD_NIGHT_LIGHT = 0x03;
    public static final byte ACTION_RC_SERVER_CMD_LIGHT = 0x04;

    public static final int BT_SPEAKER_DELAY_TIME = 50; //蓝牙音响播放音乐 默认光效延时

    // 得到本机ip地址 wifi
    public static byte[] getLocalHostIp(Context context) {
        byte[] ipaddress = new byte[4];
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        ipaddress[0] = (byte) (ipInt & 0xFF);
        ipaddress[1] = (byte) ((ipInt >> 8) & 0xFF);
        ipaddress[2] = (byte) ((ipInt >> 16) & 0xFF);
        ipaddress[3] = (byte) ((ipInt >> 24) & 0xFF);
        return ipaddress;
    }

    // 得到本机ip地址
    public static String getLocalIp() {
        String ipaddress = null;
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();  // 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                while (inet.hasMoreElements())  // 遍历每一个接口绑定的所有ip
                {
                    InetAddress ip = inet.nextElement();
                    //原始写法过时
                    if (!ip.isLoopbackAddress() && ip instanceof Inet4Address) {
                        return ipaddress = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipaddress;
    }

    public static void checkDir(String dir) {
        File fileDir = new File(dir);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
    }

    public static void checkFiles(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /////////////////////////////////////////////////////////byte 与 String互转/////////////////////////////////////////////////////////////////
    final static int SIZE = 4096;

    //short转byte
    public static void putShort(byte b[], short s, int index) {
        b[index + 1] = (byte) (s >> 8);
        b[index + 0] = (byte) (s >> 0);
    }

    //byte转short
    public static short getShort(byte[] b, int index) {
        return (short) (((b[index + 1] << 8) | b[index + 0] & 0xff));
    }
/*
    //byte 数组与 int 的相互转换
  	public static int byteArrayToInt(byte[] b) {
  	    return   b[3] & 0xFF |
  	            (b[2] & 0xFF) << 8 |
  	            (b[1] & 0xFF) << 16 |
  	            (b[0] & 0xFF) << 24;
  	}

  	public static byte[] intToByteArray(int a) {
	    return new byte[] {
	        (byte) ((a >> 24) & 0xFF),
	        (byte) ((a >> 16) & 0xFF),
	        (byte) ((a >> 8) & 0xFF),
	        (byte) (a & 0xFF)
	    };
	}

    //int转byte
    public static void putInt(byte[] bb, int x, int index) {
		bb[index + 3] = (byte) (x >> 24);
		bb[index + 2] = (byte) (x >> 16);
		bb[index + 1] = (byte) (x >> 8);
		bb[index + 0] = (byte) (x >> 0);
	}

    //byte转int
    public static int getInt(byte[] bb, int index) {
		return (int) ((((bb[index + 3] & 0xff) << 24)
				| ((bb[index + 2] & 0xff) << 16)
				| ((bb[index + 1] & 0xff) << 8) | ((bb[index + 0] & 0xff) << 0)));
	}*/

    //long转 byte
    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, x);
        return buffer.array();
    }

    //byte转 long
    public static long bytesToLong(byte[] bytes, int index) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, index, 8);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    //协议封装
    public static byte[] addprotocol(byte[] cmd, byte[] data) {
        short len = (short) (data != null ? data.length : 0);
        byte[] sendbuff = new byte[len + cmd.length + 2];
        System.arraycopy(cmd, 0, sendbuff, 0, cmd.length);
        sendbuff[cmd.length] = (byte) (len & 0x00FF);
        sendbuff[cmd.length + 1] = (byte) ((len >> 8) & 0x00FF);
        if (len > 0) {
            System.arraycopy(data, 0, sendbuff, (cmd.length + 2), data.length);
        }
        return sendbuff;
    }
}
