package meekux.grandar.com.meekuxpjxroject.adapter.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author:SeanLim
 * Created by Time on: 2017/8/29
 * 类名：RecyclerAdapter
 */

public abstract class RecyclerAdapter<T> extends BaseRecyclerAdapter<RecyclerViewHolder> {

    protected Context context;

    protected LayoutInflater inflater;

    protected List<T> listData;

    protected final int itemLayoutId;

    private OnItemClickListener itemClickListener;

    private OnItemLongClickListener itemLongClickListener;

    private Map<Integer, Boolean> map = new HashMap<>();
    private int lastAnimatedPosition = -1;
    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    public RecyclerAdapter(Context context, List<T> listData, int itemLayoutId) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.listData = listData;
        this.itemLayoutId = itemLayoutId;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View view = this.inflater.inflate(itemLayoutId, parent, false);
        RecyclerViewHolder holder = new RecyclerViewHolder(view);
        return holder;
    }

    /**
     * @param position
     * @return
     */
    public T getItem(int position) {
        return listData.get(position);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position, boolean isItem) {
        convert(holder, getItem(position), position);
        holder.itemView.setOnClickListener((v) -> {

            if (itemClickListener != null) {
                itemClickListener.onClick(v, position);
            }
        });
        holder.itemView.setOnLongClickListener((v) -> {

            if (itemLongClickListener != null) {
                itemLongClickListener.onLongClick(v, position);
            }
            return false;
        });
    }

    public void insert(T object, int position) {
        insert(listData, object, position);
    }

    /**
     * @param helper
     * @param item
     * @param position
     */
    public abstract void convert(RecyclerViewHolder helper, T item, int position);

    @Override
    public int getAdapterItemViewType(int position) {
        return 0;
    }

    @Override
    public int getAdapterItemCount() {
        return listData.size();
    }

    @Override
    public RecyclerViewHolder getViewHolder(View view) {
        return new RecyclerViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setItemLongClickListener(OnItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(View parent, int position);
    }

    public interface OnItemLongClickListener {
        boolean onLongClick(View parent, int position);
    }
}
