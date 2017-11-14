package meekux.grandar.com.meekuxpjxroject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


/**
 * 通用适配器（适合一些常规的适配器）
 *
 * @param <T,H> T：实体对象,H:ViewHolder
 * @author xuqunwang
 */
public abstract class HolderAdapter<T, H> extends AbstractAdapter<T> {

    public HolderAdapter(Context context, List<T> listData) {
        super(context, listData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        H holder = null;
        T t;
        if (listData.get(0) instanceof Menu) {
            t = listData.get(position);
        } else {
            t = listData.get(0);
        }

        if (convertView == null) {
            convertView = buildConvertView(layoutInflater, t, position);
            holder = buildHolder(convertView, t, position);

            convertView.setTag(holder);
        } else {
            holder = (H) convertView.getTag();
        }
        bindViewDatas(holder, t, position);

        return convertView;
    }

    /**
     * 建立convertView
     *
     * @param layoutInflater
     * @return
     */
    public abstract View buildConvertView(LayoutInflater layoutInflater, T t, int position);

    /**
     * 建立视图Holder
     *
     * @param convertView
     * @return
     */
    public abstract H buildHolder(View convertView, T t, int position);

    /**
     * 绑定数据
     *
     * @param holder
     * @param t
     * @param position
     */
    public abstract void bindViewDatas(H holder, T t, int position);


} 