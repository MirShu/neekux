package meekux.grandar.com.meekuxpjxroject.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socks.library.KLog;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.DeviceDbUtil;

import java.util.List;

import meekux.grandar.com.meekuxpjxroject.GlobalApplication;
import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.entity.SearchBean;
import meekux.grandar.com.meekuxpjxroject.utils.IConstant;
import meekux.grandar.com.meekuxpjxroject.utils.SharedPreferencesUtils;
import meekux.grandar.com.meekuxpjxroject.utils.ToastUtils;

/**
 * Created by zhaoyang on 2017/5/27.
 */

public class MySearchAdapter extends BaseAdapter {

    private Context context;
    private List<SearchBean> list;
    private String curSn;
    private boolean isConnected;

    public MySearchAdapter(Context context, List<SearchBean> list) {
        this.context = context;
        this.list = list;
        KLog.e(SharedPreferencesUtils.getinstance());
        if (SharedPreferencesUtils.getinstance().getStringValue(IConstant.LAMPSN) != null) {
            curSn = SharedPreferencesUtils.getinstance().getStringValue(IConstant.LAMPSN);
        }
        isConnected = SharedPreferencesUtils.getinstance().getBooleanValue(IConstant.ISCONNECT, false);
//        KLog.e("是否连接状态----" + isConnected + curSn);

    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_item_adapter_lamp, null);
            holder = new ViewHolder();
            holder.lamplistImg = convertView.findViewById(R.id.lamplist_img);
            holder.text_name = convertView.findViewById(R.id.lamplist_text_lamptype);
            holder.text_conn = convertView.findViewById(R.id.lamplist_text_contact);
            holder.text_type = convertView.findViewById(R.id.lamplist_text_content);
            holder.item_content = convertView.findViewById(R.id.item_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final SearchBean bean = list.get(position);
        holder.text_name.setText("明狐面板灯" + bean.getSn().substring(0, 3));
        KLog.e(bean.getSn() + "curSn" + curSn);
        if (curSn.equals(bean.getSn()) && isConnected) {
            holder.text_conn.setTextColor(context.getResources().getColor(R.color.whiteTextColor));
            holder.text_conn.setBackgroundResource(R.drawable.shape_corner_bg_red);
            holder.text_conn.setText(context.getResources().getString(R.string.connectnot));
        } else {
            holder.text_conn.setTextColor(context.getResources().getColor(R.color.greencolor));
            holder.text_conn.setBackgroundResource(R.drawable.shape_corner_bg_gray);
            holder.text_conn.setText(context.getResources().getString(R.string.connected));
        }
        MyDeviceBean deviceBean = DeviceDbUtil.getInstance().queryTimeBy(bean.getSn());
        if (deviceBean == null) {
            holder.text_type.setText("音乐智能灯");
        } else {
            holder.text_type.setText(deviceBean.getSn());
//            holder.text_type.setText(deviceBean.getName().substring(0, 4));
        }

        String sn = bean.getSn();
        if (!TextUtils.isEmpty(sn)) {
            String type = sn.substring(2, 3);
            String types = sn.substring(0, 1);
            if (type.equals("f")) {
                KLog.e("落地灯");
                holder.lamplistImg.setImageResource(R.mipmap.meekux_ld);
            } else if (type.equals("d")) {
                if (types.equals("L") && type.equals("d")) {
                    KLog.e("爱情台灯");
                    holder.lamplistImg.setImageResource(R.mipmap.meekux_love);
                } else {
                    KLog.e("台灯");
                    holder.lamplistImg.setImageResource(R.mipmap.meekux_td);
                }
            } else if (type.equals("c")) {
                holder.lamplistImg.setImageResource(R.mipmap.meekux_xd);
            } else if (types.equals("R")) {
                holder.lamplistImg.setImageResource(R.mipmap.meekux_dd);
                holder.text_name.setText("明狐线条灯" + bean.getSn().substring(0, 3));
            } else if (type.equals("m")) {
                holder.lamplistImg.setImageResource(R.mipmap.meekux_qb);
            } else {
                holder.lamplistImg.setImageResource(R.mipmap.meekux_ld);
            }
        }

        holder.text_conn.setOnClickListener(v -> {
            ToastUtils.show("curSn" + curSn + "\n" + "getSn" + bean.getSn());
            if (curSn.equals(bean.getSn()) && GlobalApplication.getInstance().getConnect()) {
                ToastUtils.show("设备已经连接");
            } else {
                ToastUtils.show("开始处理");
                if (imp != null) imp.click(position);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView text_name;
        TextView text_conn;
        TextView text_type;
        LinearLayout item_content;
        ImageView lamplistImg;
    }

    public interface OnSearchImp {
        void click(int position);
    }

    public void setOnSearchImp(OnSearchImp imp) {
        this.imp = imp;
    }

    private OnSearchImp imp;
}
