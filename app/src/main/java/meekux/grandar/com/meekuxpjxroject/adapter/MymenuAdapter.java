package meekux.grandar.com.meekuxpjxroject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.entity.Menu;

/**
 * Created by xuqunwang on 2017/3/1.
 */

public class MymenuAdapter extends HolderAdapter<Menu, MymenuAdapter.ViewHolder> {

    List<Menu> listDate;
    public MymenuAdapter(Context context, List<Menu> listData) {
        super(context, listData);
        this.listDate=listData;
    }

    @Override
    public View buildConvertView(LayoutInflater layoutInflater, Menu t, int position) {
        return inflate(R.layout.menu_itemlayout);
    }

    @Override
    public MymenuAdapter.ViewHolder buildHolder(View convertView, Menu t, int position) {
        MymenuAdapter.ViewHolder holder = new MymenuAdapter.ViewHolder();
        holder.menutitle = (TextView) convertView.findViewById(R.id.menu_title);
        holder.menuimage = (ImageView) convertView.findViewById(R.id.menu_image);
        return holder;
    }

    @Override
    public void bindViewDatas(MymenuAdapter.ViewHolder holder, Menu m, int position) {

        holder.menutitle.setText(m.getTitle()[position]);
        holder.menuimage.setImageResource(m.getMeunimageid()[position]);
    }

    @Override
    public int getCount() {
        return listDate.get(0).getMeunimageid().length;
    }


    public static class ViewHolder {

        TextView menutitle;
        ImageView menuimage;
    }
}
