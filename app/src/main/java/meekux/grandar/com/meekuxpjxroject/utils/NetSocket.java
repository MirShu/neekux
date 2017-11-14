package meekux.grandar.com.meekuxpjxroject.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import meekux.grandar.com.meekuxpjxroject.MainActivity;

/**
 * Created by xiehj on 15-4-22.
 */
public class NetSocket {
    static final String ipLOCAL = "10.0.2.2";

    //static final String ipCLOUD = "139.224.32.109";
    static final String ipCLOUD = "192.168.0.1";
    static final int portCMD = 2222;  //UDP指令通信端口
    static final int portFILE = 2222; //TCP文件传输端口

    class DataSend {
        public char cmd;             //0:查询项目列表；1：查询PD播放列表；2：查询PK组；3：控制场景播放状态
        //4：控制PK状态;5:色彩调光；6：查询当前PD状态；7：查询当前PK状态
        //8:发送文字消息;E:发送登录信息；F:心跳包
        public long timeStamp;       //时间戳
        public int idSend;           //包编号
        public long timeSend;        //最后一次发送时间，单位毫秒
        public int needResend;       //如果没有收到反馈，则需要重发的次数
        public int recvReply;        //标记是否收到反馈
        public int sendCount;        //标记已经发送的次数


        public DatagramPacket packetSend;

        public DataSend(char cmd, int needResend) {
            this.cmd = cmd;
            this.timeStamp = System.currentTimeMillis() + staBaseTimeStamp;

            this.idSend = staID++;
            this.timeSend = 0;

            this.needResend = needResend;
            recvReply = 0;
            sendCount = 0;
        }

        public void createPacket(byte[] buf, int len) {
            packetSend = new DatagramPacket(buf, len);
            packetSend.setAddress(ipDest);
            packetSend.setPort(portCMD);    //云服务器端口
            packetSend.setData(buf);
            packetSend.setLength(len);
        }
    }

    MainActivity mainActivity;       //主activity对象
    public SProject sproject = null;       //赋值MainActivity中的变量


    public List<DataSend> listDataSend = new ArrayList<DataSend>();

    static public long staBaseTimeStamp = 0;

    static public int staID = 1;

    final static public int MAX_PRE_RECV_ID = 50;

    static public long preTimeStamp[] = new long[MAX_PRE_RECV_ID];
    static public int preRecvID[] = new int[MAX_PRE_RECV_ID];      //最近收到的APP的指令序号，保存起来，以避免处理相同命令

    public int m_posPreRecvDataID = 0;

    final static public int NUM_RESEND_NORMAL = 5;      //一般命令重发的最大次数


    private DatagramSocket udpSocket;
    private InetAddress ipDest;


    private static Handler mSendThreadHandler;   //发送线程

    private boolean bRecvPDPlayList = false;     //是否收到PD播放列表
    private boolean bRecvPKList = false;         //是否收到PK区域列表


    public NetSocket(MainActivity activity)    //构造函数，将主activity传
    {
        mainActivity = activity;
        sproject = mainActivity.sproject;

        try {
            udpSocket = new DatagramSocket();   //本地
            //udpSocket.setBroadcast(true);

            try {
                if (mainActivity.isEmulator() == true) {
                    ipDest = InetAddress.getByName(ipLOCAL);  //模拟器用本地IP地址

                    //Toast toast = Toast.makeText(mainActivity, "模拟器", Toast.LENGTH_SHORT);
                    //toast.show();
                } else {
                    ipDest = InetAddress.getByName(ipCLOUD);  //公网云服务器IP地址
                    //Toast toast = Toast.makeText(mainActivity, "手机", Toast.LENGTH_SHORT);
                    //toast.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        new UDPSendThread().start();        //启动发送线程

        new UDPRecvThread().start();        //启动接受线程
    }


    public boolean isInit()   //判断是否初始化好
    {
        if (sproject.name == null)
            return false;
        else
            return true;
    }


    public void replyMsg(long timeStamp, int id)   // 反馈指令，表示数据已经收到
    {
        DataSend dataSend = new DataSend('R', 0);

        String str = String.format("<%d:%d:R:0>\r\n", timeStamp, id);

        dataSend.createPacket(str.getBytes(), str.length());

        //为了提高效率，此处直接发送,并且命令号也改成8
        //  Message toSend = mSendThreadHandler.obtainMessage();
        ///  toSend.what = 8;   //表示增加发送包
        //  toSend.obj = dataSend;
        //  mSendThreadHandler.sendMessage( toSend );

        sendData(dataSend);   //加入发送列表
    }

    public void processReplyMsg(String msg) {
        int pos1;
        int pos2;

        long timeStamp = 0;
        int id = 0;

        pos1 = msg.indexOf("<", 0);
        pos2 = msg.indexOf(">", 0);

        if (pos1 == -1 || pos2 == -1)
            return;

        pos1 = pos1 + 1;

        pos2 = msg.indexOf(":", pos1);
        String strTimeStamp = msg.substring(pos1, pos2);

        timeStamp = Long.parseLong(strTimeStamp);

        pos1 = pos2 + 1;
        pos2 = msg.indexOf(":", pos1);
        String strID = msg.substring(pos1, pos2);
        id = Integer.parseInt(strID);

        pos1 = pos2 + 1;
        pos2 = msg.indexOf(":", pos1);
        String strDir = msg.substring(pos1, pos2);

        pos1 = pos2 + 1;
        pos2 = msg.indexOf(">", pos1);
        String strReply = msg.substring(pos1, pos2);

        if (strDir.equals("S") == true)    //服务器发送过来的指令
        {
            if (strReply.equals("1") == true)   //表示需要反馈,直接反馈数据到服务器，表示收到指令
            {
                replyMsg(timeStamp, id);
            }
        } else if (strDir.equals("R") == true)   //服务器反馈过来的指令,通过此可知道什么指令已经发送成功
        {
            Message toSend = mSendThreadHandler.obtainMessage();
            toSend.what = 2;
            toSend.obj = id;

            mSendThreadHandler.sendMessage(toSend);

            return;
        } else {
            return;
        }

        String strData = msg.substring(pos2 + 1);

        if (strData.length() < 5 || strData.charAt(0) != 'S' || strData.charAt(1) != '-' || strData.charAt(2) != '>' || strData.charAt(3) != 'P')
            return;


        for (int i = 0; i < MAX_PRE_RECV_ID; i++) {
            if (preTimeStamp[i] == timeStamp && preRecvID[i] == id) {
                //Log.i("processReplyMsg",String.format("重复指令:"+msg,id));
                //Toast toast = Toast.makeText(mainActivity.getApplicationContext(), String.format("重复指令",id), Toast.LENGTH_SHORT);
                //toast.show();
                return;      //找到重复指令，则直接退出
            }
        }

        preTimeStamp[m_posPreRecvDataID] = timeStamp;
        preRecvID[m_posPreRecvDataID] = id;    //保存指令号

        m_posPreRecvDataID++;
        if (m_posPreRecvDataID >= MAX_PRE_RECV_ID)
            m_posPreRecvDataID = 0;


        switch (strData.charAt(4))    //命令号
        {
            case '0':
                processReplyProjectList(strData);  //解析项目列表
                break;
            case '1': {
                processReplyPDList(strData);       //解析PD播放列表
                bRecvPDPlayList = true;
            }
            break;
            case '2': {
                staBaseTimeStamp = timeStamp - System.currentTimeMillis(); //收到此命令时进行一次与服务器序号同步

                processReplyPKList(strData);       //解析PK控制区域列表

                bRecvPKList = true;
            }
            break;
            case '3':                               //服务器反馈过来的PD列表项播放控制是否成功收到

                break;
            case '4':                               //服务器反馈过来的PK控制是否成功收到

                break;
            case '5':
                processNowColor(strData);          //解析手动调色
                break;
            case '6': {
                if (bRecvPDPlayList == true) {
                    try {
                        processReplyPDStatus(strData);     //解析当前PD播放信息
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case '7': {
                if (bRecvPKList == true)
                    processReplyPKStatus(strData);     //解析当前PK状态信息
            }
            break;
            case '8':
                processChatMsg(strData, timeStamp);           //解析报警信息
                Log.i("processChatMsg", msg);
                break;
            case 'E':
                processLevel(strData);             //解析服务器分配过来的权限
                break;
        }
    }

    public void sendData(DataSend dataSend) {
        Message toSend = mSendThreadHandler.obtainMessage();
        toSend.what = 1;   //表示增加发送包
        toSend.obj = dataSend;
        mSendThreadHandler.sendMessage(toSend);
    }

    public void sendRequestProjectList()    //发送项目列表请求命令
    {
        DataSend dataSend = new DataSend('0', Integer.MAX_VALUE);

        String str = String.format("<%d:%d:S:1>P->S0\r\n", dataSend.timeStamp, dataSend.idSend);

        dataSend.createPacket(str.getBytes(), str.length());

        sendData(dataSend);   //加入发送列表
    }

    public void processReplyProjectList(String msg)   //处理反馈的项目列表，注意msg已经去除了末尾的回车换行符
    {
        int i = 0;
        int pos1 = 0;
        int pos2 = 0;
        int len = 0;

        List<String> listPrj = new ArrayList<String>();
        int selPrj = 0;

        pos1 = 6;

        while (true)       //解析出每个项目
        {
            pos2 = msg.indexOf(",", pos1);

            if (pos2 == -1) {
                pos2 = msg.length();
                listPrj.add(msg.substring(pos1, pos2));

                break;
            } else {
                listPrj.add(msg.substring(pos1, pos2));
            }

            pos1 = pos2 + 1;
        }


        //mainActivity.openSelProjectDialog(listPrj);
    }

    public void sendRequestPDList() {
        if (isInit() == false)
            return;

        DataSend dataSend = new DataSend('1', Integer.MAX_VALUE);

        String str = String.format("<%d:%d:S:1>P->S1;PRJ=%s\r\n", dataSend.timeStamp, dataSend.idSend, sproject.name);

//        dataSend.createPacket(str.getBytes(),str.length());

        try {
            byte[] buf = str.getBytes("GB2312");
            dataSend.createPacket(buf, buf.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendData(dataSend);   //加入发送列表
    }

    public void processReplyPDList(String msg)   //处理反馈的PD播放列表，注意msg已经去除了末尾的回车换行符
    {
        int pos1 = 0;
        int pos2 = 0;
        int posEnd = 0;

        pos1 = 6;

        sproject.listPD.clear();
        sproject.listAct.clear();

        while (true)       //解析出每个项目
        {
            SProject.SProject_PD_LIST pdlist;

            pos2 = msg.indexOf(":", pos1);          //1:寻找播放列表名

            if (pos2 == -1)   //表示余下没有播放列表了
            {
                // pos2 = msg.length();
                //listPrj.add(msg.substring(pos1, pos2));

                break;
            } else {
                pdlist = sproject.getNewList();
                pdlist.name = new String(msg.substring(pos1, pos2));
                pdlist.posStart = sproject.listAct.size();

                sproject.listPD.add(pdlist);
            }

            pos1 = pos2 + 1;

            posEnd = msg.indexOf(";", pos1);          //2:找到此列表结束符“;”,如果没有则代表此为最后一个列表
            if (posEnd == -1) {
                posEnd = msg.length();
            }


            //2:寻找此播放列表下的ACT
            int countAct = 0;
            while (true) {
                SProject.SProject_PD_ACT pdAct;

                if (pos1 > posEnd)
                    break;

                pos2 = msg.indexOf(",", pos1);

                if (pos2 == -1) {
                    pos2 = posEnd; //msg.length();

                    pdAct = sproject.getNewAct();
                    pdAct.name = new String(msg.substring(pos1, pos2));
                    sproject.listAct.add(pdAct);

                    countAct++;
                } else {
                    if (pos2 > posEnd)
                        pos2 = posEnd;

                    pdAct = sproject.getNewAct();
                    pdAct.name = new String(msg.substring(pos1, pos2));
                    sproject.listAct.add(pdAct);

                    countAct++;
                }

                pos1 = pos2 + 1;
            }

            pdlist.num = sproject.listAct.size() - pdlist.posStart;

            String strReName = String.format("%s  (%d)", pdlist.name, countAct);
            pdlist.name = strReName;
        }

        if (mainActivity.manulcontrol != null)
            mainActivity.manulcontrol.createData(sproject);
        Message toMain = mainActivity.mMainHandler.obtainMessage();
        toMain.what = 0xa2;
        mainActivity.mMainHandler.sendMessage(toMain);       //通知主线程，可以请求PK列表了
    }

    public void sendRequestPKList() {
        if (isInit() == false)
            return;

        DataSend dataSend = new DataSend('2', Integer.MAX_VALUE);

        String str = String.format("<%d:%d:S:1>P->S2;PRJ=%s\r\n", dataSend.timeStamp, dataSend.idSend, sproject.name);

//        dataSend.createPacket(str.getBytes(),str.length());

        try {
            byte[] buf = str.getBytes("GB2312");
            dataSend.createPacket(buf, buf.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendData(dataSend);   //加入发送列表
    }


    public void processReplyPKList(String msg)   //处理反馈的PK列表，注意msg已经去除了末尾的回车换行符
    {
        int i = 0;
        int pos1 = 0;
        int pos2 = 0;
        int len = 0;

        pos1 = 6;

        sproject.listPK.clear();    //先删除原先所有内部数据

        while (true)       //解析出每个项目
        {
            pos2 = msg.indexOf(",", pos1);

            if (pos2 == -1) {
                pos2 = msg.length();

                SProject.SProject_PK_ITEM pkItem = sproject.getNewPKItem();
                pkItem.name = new String(msg.substring(pos1, pos2));

                sproject.listPK.add(pkItem);

                break;
            } else {
                SProject.SProject_PK_ITEM pkItem = sproject.getNewPKItem();
                pkItem.name = new String(msg.substring(pos1, pos2));

                sproject.listPK.add(pkItem);
            }

            pos1 = pos2 + 1;
        }

//
//        if(mainActivity.powerF!=null)
//            mainActivity.powerF.createData();

        Message toMain = mainActivity.mMainHandler.obtainMessage();
        toMain.what = 0xb0;
        mainActivity.mMainHandler.sendMessage(toMain);       //通知主线程，可以启动发心跳包定时器了。
    }


    public void sendXintiao()    //发送心跳包
    {
        if (isInit() == false)
            return;

        DataSend dataSend = new DataSend('F', 0);  //无需反馈
        String str = String.format("<%d:%d:S:0>P->SF;PRJ=%s\r\n", dataSend.timeStamp, dataSend.idSend, sproject.name);

        try {
            byte[] buf = str.getBytes("GB2312");
            dataSend.createPacket(buf, buf.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendData(dataSend);   //加入发送列表
    }

    public void processNowColor(String msg) {
        int i = 0;
        int pos1 = 0;
        int pos2 = 0;
        int len = 0;

        pos1 = 6;

        pos1 = msg.indexOf("R=", pos1);
        pos2 = msg.indexOf(",", pos1);

        if (pos1 == -1 || pos2 == -1) {
            return;
        }

        String str = msg.substring(pos1 + 2, pos2);

        sproject.color.r = Integer.parseInt(str);

        pos1 = pos2 + 1;
        pos1 = msg.indexOf("G=", pos1);
        pos2 = msg.indexOf(",", pos1);

        str = msg.substring(pos1 + 2, pos2);
        sproject.color.g = Integer.parseInt(str);

        pos1 = pos2 + 1;
        pos1 = msg.indexOf("B=", pos1);
        pos2 = msg.indexOf(",", pos1);

        str = msg.substring(pos1 + 2, pos2);
        sproject.color.b = Integer.parseInt(str);

        pos1 = pos2 + 1;
        pos1 = msg.indexOf("W=", pos1);
        pos2 = msg.length();

        str = msg.substring(pos1 + 2, pos2);
        sproject.color.w = Integer.parseInt(str);


//        if(mainActivity.manulcontrol!=null)
//            mainActivity.manulcontrol.updateData(sproject);
    }


    public void processReplyPDStatus(String msg)   //处理反馈的PD播放信息，注意msg已经去除了末尾的回车换行符
    {
        int i = 0;
        int pos1 = 0;
        int pos2 = 0;
        int len = 0;

        pos1 = 6;

        pos1 = msg.indexOf("STA=", pos1);
        pos2 = msg.indexOf(";", pos1);

        if (pos1 == -1 || pos2 == -1) {
            return;
        }

        String str = msg.substring(pos1 + 4, pos2);
        sproject.statusPD = Integer.parseInt(str);

        pos1 = pos2 + 1;
        pos1 = msg.indexOf("MODE=", pos1);
        pos2 = msg.indexOf(";", pos1);

        str = msg.substring(pos1 + 5, pos2);
        sproject.modePD = Integer.parseInt(str);

        pos1 = pos2 + 1;
        pos1 = msg.indexOf("LIST=", pos1);
        pos2 = msg.indexOf(",", pos1);

        str = msg.substring(pos1 + 5, pos2);
        sproject.pdPlay.list = Integer.parseInt(str) - 1;   //转换成从0开始

        pos1 = pos2 + 1;
        pos1 = msg.indexOf("NODE=", pos1);
        pos2 = msg.indexOf(",", pos1);

        str = msg.substring(pos1 + 5, pos2);
        sproject.pdPlay.act = Integer.parseInt(str) - 1;

        pos1 = pos2 + 1;
        pos1 = msg.indexOf("CTRL=", pos1);
        pos2 = msg.indexOf(",", pos1);

        str = msg.substring(pos1 + 5, pos2);
        sproject.pdPlay.ctrl = Integer.parseInt(str);


        pos1 = pos2 + 1;
        pos1 = msg.indexOf("ALL=", pos1);
        pos2 = msg.indexOf(",", pos1);

        str = msg.substring(pos1 + 4, pos2);
        sproject.pdPlay.frameAll = Integer.parseInt(str);

        pos1 = pos2 + 1;
        pos1 = msg.indexOf("NOW=", pos1);
        pos2 = msg.length();

        str = msg.substring(pos1 + 4, pos2);
        sproject.pdPlay.frameNow = Integer.parseInt(str);

        if (mainActivity.manulcontrol != null)
            mainActivity.manulcontrol.updateData(sproject);
    }


    public void processReplyPKStatus(String msg)   //处理反馈的PK状态信息，注意msg已经去除了末尾的回车换行符
    {
        int i = 0;
        int pos1 = 0;
        int pos2 = 0;
        int len = 0;

        pos1 = 6;

        pos1 = msg.indexOf("STA=", pos1);
        pos2 = msg.indexOf(";", pos1);

        if (pos1 == -1 || pos2 == -1) {
            return;
        }

        String str = msg.substring(pos1 + 4, pos2);
        sproject.statusPK = Integer.parseInt(str);

        pos1 = pos2 + 1;
        pos1 = msg.indexOf("MODE=", pos1);
        pos2 = msg.indexOf(";", pos1);


        str = msg.substring(pos1 + 5, pos2);
        sproject.modePK = Integer.parseInt(str);


        while (true) {
            pos1 = pos2 + 1;

            pos2 = msg.indexOf(":", pos1);
            if (pos2 == -1)
                break;
            else {
                str = msg.substring(pos1, pos2);
                i = Integer.parseInt(str);

                if (i < 1 || i > sproject.listPK.size())  //超出范围
                    break;

                i = i - 1;    //因为协议是从1开始编号，内存是从0开始编号
            }

            pos1 = pos2 + 1;
            pos2 = msg.indexOf(",", pos1);
            str = msg.substring(pos1, pos2);
            sproject.listPK.get(i).status = Integer.parseInt(str);

            pos1 = pos2 + 1;
            pos2 = msg.indexOf(",", pos1);
            str = msg.substring(pos1, pos2);
            sproject.listPK.get(i).current = Float.parseFloat(str);

            pos1 = pos2 + 1;
            pos2 = msg.indexOf(";", pos1);
            if (pos2 == -1)
                pos2 = msg.length();
            str = msg.substring(pos1, pos2);
            sproject.listPK.get(i).power = Float.parseFloat(str);
        }
        if (mainActivity.manulcontrol != null)
            mainActivity.manulcontrol.updateData(sproject);
    }

    public void sendPlayPD(int mode, int list, int act, int ctrl)    //控制当前PD状态
    {
        if (isInit() == false)
            return;


        DataSend dataSend = new DataSend('3', NUM_RESEND_NORMAL);

        String str = String.format("<%d:%d:S:1>P->S3;PRJ=%s;MODE=%d,LIST=%d,ACT=%d,CTRL=%d\r\n", dataSend.timeStamp, dataSend.idSend, sproject.name, mode, list + 1, act + 1, ctrl);

        try {
            byte[] buf = str.getBytes("GB2312");
            dataSend.createPacket(buf, buf.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendData(dataSend);   //加入发送列表
    }


    public void sendPlayPK(int mode, int node, int ctrl) {
        if (isInit() == false)
            return;

        DataSend dataSend = new DataSend('4', NUM_RESEND_NORMAL);

        String str = String.format("<%d:%d:S:1>P->S4;PRJ=%s;MODE=%d,NODE=%d,CTRL=%d\r\n", dataSend.timeStamp, dataSend.idSend, sproject.name, mode, node + 1, ctrl);

        try {
            byte[] buf = str.getBytes("GB2312");
            dataSend.createPacket(buf, buf.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendData(dataSend);   //加入发送列表
    }

    public void sendPlayColor(int r, int g, int b, int w) {
        if (isInit() == false)
            return;

        DataSend dataSend = new DataSend('5', NUM_RESEND_NORMAL);

        String str = String.format("<%d:%d:S:1>P->S5;PRJ=%s;R=%d,G=%d,B=%d,W=%d\r\n", dataSend.timeStamp, dataSend.idSend, sproject.name, r, g, b, w);

        try {
            byte[] buf = str.getBytes("GB2312");
            dataSend.createPacket(buf, buf.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendData(dataSend);   //加入发送列表
    }

    public long sendLastChatMsgTimeStamp(long timeStamp) {
        if (isInit() == false)
            return 0;

        DataSend dataSend = new DataSend('9', 0);  //暂时无需重复发送，否则服务器可能多次返回大量相同历史消息

        String str = String.format("<%d:%d:S:0>P->S9;PRJ=%s;TIMESTAMP=%d\r\n", dataSend.timeStamp, dataSend.idSend, sproject.name, timeStamp);

        try {
            byte[] buf = str.getBytes("GB2312");
            dataSend.createPacket(buf, buf.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendData(dataSend);   //加入发送列表

        return dataSend.timeStamp;
    }


    public long sendChatMsg(String strSender, int type, int level, String strTime, String strMsg) {
        if (isInit() == false)
            return 0;

        DataSend dataSend = new DataSend('8', NUM_RESEND_NORMAL);

        String str = String.format("<%d:%d:S:1>P->S8;PRJ=%s;SENDER=%s,TYPE=%d,LEVEL=%d,TIME=%s,MSG=%s\r\n", dataSend.timeStamp, dataSend.idSend, sproject.name, strSender, type, level, strTime, strMsg);

        try {
            byte[] buf = str.getBytes("GB2312");
            dataSend.createPacket(buf, buf.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendData(dataSend);   //加入发送列表

        return dataSend.timeStamp;
    }

    public void processChatMsg(String msg, long timeStamp) {
        int i = 0;
        int pos1 = 0;
        int pos2 = 0;
        int len = 0;

        pos1 = 6;

        pos1 = msg.indexOf("SENDER=", pos1);
        pos2 = msg.indexOf(",", pos1);

        if (pos1 == -1 || pos2 == -1) {
            return;
        }


        String str = msg.substring(pos1 + 7, pos2);

        SProject.SProject_MSG msgItem = sproject.getNewMsg();
        msgItem.user = new String(str);

        pos1 = pos2 + 1;
        pos1 = msg.indexOf("TYPE=", pos1);
        pos2 = msg.indexOf(",", pos1);
        str = msg.substring(pos1 + 5, pos2);
        msgItem.type = Integer.parseInt(str);

        pos1 = pos2 + 1;
        pos1 = msg.indexOf("LEVEL=", pos1);
        pos2 = msg.indexOf(",", pos1);
        str = msg.substring(pos1 + 6, pos2);
        msgItem.level = Integer.parseInt(str);

        pos1 = pos2 + 1;
        pos1 = msg.indexOf("TIME=", pos1);
        pos2 = msg.indexOf(",", pos1);
        str = msg.substring(pos1 + 5, pos2);
        msgItem.time = new String(str);

        pos1 = pos2 + 1;
        pos1 = msg.indexOf("MSG=", pos1);
        pos2 = msg.length();
        str = msg.substring(pos1 + 4, pos2);
        msgItem.strMsg = new String(str);

        if (msgItem.user.equals("PK") == true)
            msgItem.dir = 1;
        else
            msgItem.dir = 0;

        msgItem.timeStamp = timeStamp;


//        if(msgItem.type==FragmentHistory.TYPE_PICTURE || msgItem.type==FragmentHistory.TYPE_VOICE)  //路径转化，因为传过来的只是文件名
//        {
//            msgItem.strMsg = MainActivity.getFileStoragePath() + "/" + msgItem.strMsg;
//        }
//
//        msgItem.appData = System.currentTimeMillis();   //标记此条消息，主要用于显示时作为一个唯一标记
//
//        sproject.listMsg.add(msgItem);
//
//        mainActivity.dbManager.addMsg(msgItem);    //添加记录到数据库
//
//
//        if(mainActivity.historyF!=null)
//        {
//            mainActivity.historyF.updateData(msgItem);
//        }
//        else
//        {
//            if(sproject.listMsg.size()>100)   //超过100条消息，则删除最前面一条。
//            {
//                sproject.listMsg.remove(0);
//            }
//        }
//
//        mainActivity.utility.vibrate();
    }


    public void sendLogin(String strName, String strPassword) {
        if (isInit() == false)
            return;


        DataSend dataSend = new DataSend('E', NUM_RESEND_NORMAL);

        String str = String.format("<%d:%d:S:1>P->SE;PRJ=%s;NAME=%s,PASSWORD=%s\r\n", dataSend.timeStamp, dataSend.idSend, sproject.name, strName, strPassword);

        try {
            byte[] buf = str.getBytes("GB2312");
            dataSend.createPacket(buf, buf.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendData(dataSend);   //加入发送列表
    }


    public void processLevel(String msg)   //获取权限数值
    {
        int i = 0;
        int pos1 = 0;
        int pos2 = 0;
        int len = 0;

        pos1 = 6;

        pos1 = msg.indexOf("LEVEL=", pos1);
        pos2 = msg.length();

        if (pos1 == -1 || pos2 == -1) {
            return;
        }


        String str = msg.substring(pos1 + 6, pos2);
        sproject.level = Integer.parseInt(str);

        if (sproject.level == 0) {
            Toast toast = Toast.makeText(mainActivity, "未获取到权限，用户名或密码错误!", Toast.LENGTH_LONG);
            toast.show();
        } else if (sproject.level == -1) {
            Toast toast = Toast.makeText(mainActivity, "系统维护中，将取消所有权限!", Toast.LENGTH_LONG);
            toast.show();
        } else {
            Toast toast = Toast.makeText(mainActivity, "成功获取相应权限！", Toast.LENGTH_SHORT);
            toast.show();
        }


//        mainActivity.utility.vibrate();
//
//        mainActivity.progressDialog.dismiss();
    }


    class UDPSendThread extends Thread {
        private static final String SEND_TAG = "SendThread";

        public void sendData(DataSend dataSend) {
//            if(dataSend.cmd=='R')
//                Log.i( SEND_TAG, String.format("Reply:%d",dataSend.idSend) );
//            else
//                Log.i( SEND_TAG, String.format("sendData:%d",dataSend.idSend) );


            if (dataSend.cmd != '8' && dataSend.needResend > 0)   //如果有相同类型的命令，则需要删除之前的重发的命令，报警消息除外
            {
                DataSend ds;
                for (int i = 0; i < listDataSend.size(); i++) {
                    ds = listDataSend.get(i);
                    if (dataSend.cmd == ds.cmd) {
                        listDataSend.remove(i);
                        ds = null;
                        i--;
                    }
                }
            }

            if (dataSend.needResend > 0) {
                listDataSend.add(dataSend);   //如果需要反馈则加入发送列表，用来监测是否收到反馈，如果没有，则需要重发
                dataSend.timeSend = System.currentTimeMillis();
                dataSend.sendCount++;
            }

            try {
                udpSocket.send(dataSend.packetSend);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (dataSend.needResend == 0)
                dataSend = null;
        }

        public void replyMsg(DataSend dataSend) {
            try {
                udpSocket.send(dataSend.packetSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void delData(int id) {
            int count = listDataSend.size();
            for (int i = 0; i < count; i++) {
                if (listDataSend.get(i).idSend == id) {
                    listDataSend.remove(i);
                    break;
                }
            }

            Log.i(SEND_TAG, String.format("delData:%d", id));

        }


        public void checkToReSend() {
            int i = 0;
            long nowTime = System.currentTimeMillis();


            for (i = 0; i < listDataSend.size(); i++) {
                DataSend dataSend = listDataSend.get(i);

                if (nowTime - dataSend.timeSend > 500)  //超过500ms需要重发
                {
                    try {
                        udpSocket.send(dataSend.packetSend);

                        Log.i(SEND_TAG, String.format("reSendData:%d", dataSend.idSend));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    dataSend.timeSend = System.currentTimeMillis();
                    dataSend.sendCount++;

                    if (dataSend.sendCount > dataSend.needResend)  //超过规定的重发次数则取消
                    {
                        Log.i(SEND_TAG, String.format("timeout:%d", dataSend.idSend));
                        listDataSend.remove(i);
                        dataSend = null;
                        i--;
                    }
                }
            }

            mSendThreadHandler.sendEmptyMessageDelayed(9, 300);
        }

        public void run() {
            // 初始化消息循环队列，需要在Handler创建之前
            Looper.prepare();

            Log.i(SEND_TAG, "UDPSendThread:run ");

            //System.currentTimeMillis();


            mSendThreadHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    //  Log.i( SEND_TAG, "Execute an incoming message from the main thread - " + (String) msg.obj );

                    switch (msg.what) {
                        case 0: //
                            break;
                        case 1:
                            sendData((DataSend) msg.obj);
                            break;
                        case 2:
                            delData((int) msg.obj);
                            break;
                        case 8:
                            replyMsg((DataSend) msg.obj);
                            break;
                        case 9:
                            checkToReSend();
                            break;
                    }
                }
            };


            Message toMain = mainActivity.mMainHandler.obtainMessage();
            toMain.what = 0xa0;

            mainActivity.mMainHandler.sendMessage(toMain);       //通知主线程，我发送线程已经正常工作


            mSendThreadHandler.sendEmptyMessageDelayed(9, 500);  //启动内部类似定时器的功能


            // 启动子线程消息循环队列
            Looper.loop();    //此为阻塞函数，内部一直在检测消息，如有消息则调用上面的handleMessage函数进行处理
        }
    }


    class UDPRecvThread extends Thread {
        private static final String RECV_TAG = "RecvThread";

        private DatagramPacket packetRecv;

        public void run() {
            Log.i(RECV_TAG, "UDPServerThread:run ");

            byte data[] = new byte[3000];
            packetRecv = new DatagramPacket(data, data.length);

            while (true)        //不停接受数据包，阻塞式
            {
                for (int i = 0; i < 3000; i++) {
                    data[i] = 0;
                }


                packetRecv.setLength(data.length);    //这里需要设置初始大小，否则会沿用上一包数据长度

                try {
                    udpSocket.receive(packetRecv);

                    String message = new String(packetRecv.getData(), "GB2312").trim();  //注意这里的trim去除了末尾的回车换行

                    Message toMain = mainActivity.mMainHandler.obtainMessage();
                    toMain.what = 1;   //表示数据接受
                    toMain.obj = message;

                    //toMain.getData().putString("dst",packetRecv.getAddress().getHostAddress());//利用msg内部的bundle传递数据

                    mainActivity.mMainHandler.sendMessage(toMain);  //收到数据后直接传递给MainActiivity,而不是自己处理。
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
