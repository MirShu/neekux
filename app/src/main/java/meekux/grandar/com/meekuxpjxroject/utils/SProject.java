package meekux.grandar.com.meekuxpjxroject.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiehj on 15-4-10.
 */
public class SProject
{
    public class SProject_PD_ACT    //单个场景信息
    {
        public String name;
        public int numAllFrame;
    };


    public    class SProject_PD_LIST   //播放列表信息
    {
        public String name;
        public int posStart;    //在总act列表中的起始位置
        public int num;         //在总act列表中的数目
    };

    public  class SProject_PK_ITEM   //PK单个区域信息
    {
        public String name;
        public float power;
        public float current;
        public int status;
        public int control;
    };

    public  class SProject_PD_PLAY    //场景播放信息
    {
        public int list;         //播放列表序号
        public int act;          //场景序号

        public int frameAll;
        public int frameNow;        //当前帧

        public int ctrl;            //0表示暂停，1表示正在播放
    };


    public   class SProject_MSG
    {
        public long timeStamp;   //此条发送消息的时间戳
        public int type;    //消息类型 0:文本 1:图片  2：语音  3：视频  4：故障报告
        public int dir;     //方向 0:收到   1：发送
        public String user;    //发送者
        public int level;   //警告级别
        public String time; //时间
        public String strMsg;  //消息内容
        public long appData;   //APP显示使用辅助变量,用来区分不同消息，类似于id号，唯一值。
    };

    public  class SProject_COLOR
    {
        public int r;
        public int g;
        public int b;
        public int w;
    };
    //基本信息
    public String name;
    public int numIp;
    public int numLight;

    public int statusPD;     //远程PK，PD地址
    public int statusPK;    //远程服务器是否在线

    //权限级别，0为预览，1除不可以开关灯外的其他功能  2为可以控制所有  -1代表超级管理员登陆，禁用所有权限
    public String strUserName;
    public String strPassword;
    public int level;

    //PK信息
    public List<SProject_PK_ITEM> listPK;
    public int modePK;

    //PD播放列表
    public List<SProject_PD_LIST> listPD;
    public List<SProject_PD_ACT> listAct;

    //PD当前播放
    public int modePD;
    public SProject_PD_PLAY pdPlay;

    //色彩
    public SProject_COLOR color;

    //消息
    public List<SProject_MSG> listMsg ;


    public SProject( )    //构造函数
    {
        listPD = new ArrayList<SProject_PD_LIST>();
        listAct = new ArrayList<SProject_PD_ACT>();
        listPK = new ArrayList<SProject_PK_ITEM>();

        pdPlay = new SProject_PD_PLAY();

        color = new SProject_COLOR();

        listMsg = new ArrayList<SProject_MSG>();
    }

    SProject_PD_LIST getNewList()
    {
        return new SProject_PD_LIST();
    }

    SProject_PD_ACT getNewAct()
    {
        return  new SProject_PD_ACT();
    }

    SProject_PK_ITEM getNewPKItem()
    {
        return new SProject_PK_ITEM();
    }

    SProject_MSG getNewMsg()
    {
        return  new SProject_MSG();
    }
}
