package meekux.grandar.com.meekuxpjxroject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socks.library.KLog;

import java.net.Socket;
import java.util.ArrayList;

import meekux.grandar.com.meekuxpjxroject.GlobalApplication;
import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.activity.ColorActivity;
import meekux.grandar.com.meekuxpjxroject.songdata.database.GrandarUtils;
import meekux.grandar.com.meekuxpjxroject.songdata.database.MyConstant;


/**
 * Created by baixiaoming on 2017/3/17 9:06
 * Function 灯列表适配器
 */

public class LampListAdapte extends BaseAdapter implements View.OnClickListener {
    private ArrayList<String> mydeviceBean;
    private ArrayList<String> allDeviceBean;
    private Context context;
    private  ArrayList<String > arrayList;
    public LampListAdapte(Context context, ArrayList<String> lampDeviceList, ArrayList<String> mydeviceBean) {
        this.context = context;
        this.mydeviceBean = lampDeviceList;
        this.allDeviceBean = mydeviceBean;
        arrayList= new ArrayList<String>();
    }
    @Override
    public int getCount() {
        return mydeviceBean.size();
    }
    @Override
    public Object getItem(int i) {
        return mydeviceBean.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView( final int i, View converview, ViewGroup viewGroup) {
        if(converview==null){
            converview = View.inflate(context,R.layout.globelight,null);
        }
        LinearLayout lefttextviewbackground = (LinearLayout) converview.findViewById(R.id.lefttextviewbackground);
        ImageView img = (ImageView) converview.findViewById(R.id.openlight);
        LinearLayout open= (LinearLayout) converview.findViewById(R.id.openlightlinearlayout);
        LinearLayout close = (LinearLayout)converview.findViewById(R.id.closelightlinearlayout);
        lefttextviewbackground.setOnClickListener(this);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String sss = mydeviceBean.get(i);
                        Socket socket = GlobalApplication.getInstance().getDevice(sss);
                            if (socket != null) {
                                byte[] data = new byte[1];
                                data[0] = 50;
                                KLog.e("----------------->");
                                GrandarUtils.sendFrameOff(MyConstant.POWER_ON, data,sss);
                            }
                    }
                }).start();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String sssssssss = mydeviceBean.get(i);
                        Socket socket = GlobalApplication.getInstance().getDevice(sssssssss);
                            if (socket != null) {
                                byte[] data=new byte[1];
                                data[0]=50;
                                KLog.e("----------------->");
                                //线条灯的sn
                                    GrandarUtils.sendFrameOff(MyConstant.POWER_OFF, data,sssssssss);

                            }

                    }
                }).start();
            }
        });
        lefttextviewbackground.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                String sssssssssss = mydeviceBean.get(i);
                arrayList.add(sssssssssss);
                intent.putStringArrayListExtra("sn", arrayList);
                intent.setClass(context, ColorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        return converview;
    }

    @Override
    public void onClick(View view) {

    }
    static class ViewHolder
    {
        public ImageView img;
        public TextView title;
        public TextView info;
    }

}
