package meekux.grandar.com.meekuxpjxroject.fragment;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import meekux.grandar.com.meekuxpjxroject.R;

/**
 * Author:SeanLim
 * Created by Time on: 2017/9/12
 * 类名：RecyAdapter
 */
public class RecyAdapter extends RecyclerView.Adapter<RecyAdapter.ViewHolder> {
    private int item_layout;
    private List<String> mDataList;
    private List<Integer> mInts;
    private boolean isFirstSpecial;

    public RecyAdapter(int item_layout, List<String> dataList) {
        this.item_layout = item_layout;
        mDataList = dataList;
        mInts = Arrays.asList(R.mipmap.icon_light_circle, R.mipmap.icon_light_circle,
                R.mipmap.icon_light_circle, R.mipmap.icon_light_circle,
                R.mipmap.icon_light_circle, R.mipmap.icon_light_circle);
    }

    public RecyAdapter(int item_layout, List<String> dataList, boolean isFirstSpecial) {
        this(item_layout, dataList);
        this.isFirstSpecial = isFirstSpecial;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String string = mDataList.get(position);
//        if (isFirstSpecial && position == 0) {
//            holder.itemView.setBackgroundColor(Color.argb(0, 0, 0, 0)); //0完全透明  255不透明
//            holder.mTextView.setText("灯泡");
//            holder.mImageView.setImageResource(R.mipmap.icon_light_circle);
//        } else {
        holder.itemView.setBackgroundColor(Color.argb(0, 0, 0, 0));
//        holder.mTextView.setText(string);
        holder.mImageView.setImageResource(mInts.get(position % mInts.size()));
//        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public List<String> getDataList() {
        return mDataList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ImageView mImageView;
        CheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_item);
            mImageView = itemView.findViewById(R.id.img_item);
            checkBox = itemView.findViewById(R.id.checkBoxTop);
        }
    }
}
