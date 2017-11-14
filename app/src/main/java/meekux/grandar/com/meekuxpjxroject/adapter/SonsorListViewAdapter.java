package meekux.grandar.com.meekuxpjxroject.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.entity.Sonsorroom;

/**
 * Created by liuqk on 2017/5/24.
 */

public class SonsorListViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Sonsorroom> sonsorrooms;
    private ImageView closelightimage1;
    private ArrayList<String> sn;

    public SonsorListViewAdapter(Context context, ArrayList<Sonsorroom> sonsorrooms) {
        this.context = context;
        this.sonsorrooms = sonsorrooms;

    }


    @Override
    public int getCount() {
        return sonsorrooms.size();
    }

    @Override
    public Object getItem(int i) {
        return sonsorrooms.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.sonsorlistviewitem, null);
            holder = new ViewHolder();
            holder.ll_view = convertView.findViewById(R.id.closelightlinearlayout1);
            holder.roomname = convertView.findViewById(R.id.homename);
            holder.roomnameadd = convertView.findViewById(R.id.homenameadd);
            closelightimage1 = convertView.findViewById(R.id.closelightimage1);
            holder.song = convertView.findViewById(R.id.openlightlinearlayout1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.roomname.setText(sonsorrooms.get(i).getRoomname());
        holder.roomnameadd.setText(sonsorrooms.get(i).getRoomnameadd());
        holder.song.setOnClickListener(view -> listener.volClick(i));
        holder.ll_view.setOnClickListener(view -> {
            if (listener != null)
                try {
                    listener.lightClick(i, closelightimage1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        });
        return convertView;
    }

    class ViewHolder {
        TextView roomname;
        TextView roomnameadd;
        LinearLayout ll_view;
        LinearLayout song;
    }

    public interface SensorServiceImp {
        void lightClick(int i, View view);

        void volClick(int i);
    }

    private SensorServiceImp listener;

    public void setSenImp(SensorServiceImp listener) {
        this.listener = listener;
    }
}