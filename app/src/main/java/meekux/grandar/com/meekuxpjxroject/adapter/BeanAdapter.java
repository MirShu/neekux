package meekux.grandar.com.meekuxpjxroject.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import meekux.grandar.com.meekuxpjxroject.utils.WeakReferenceUtil;


/**
 * com.fph.appnavigationlib.adapter.BeanAdapter.java
 * <p>
 * Created by wang on 2016上午11:09:56
 * <p>
 * Tips:用bean绑定的adapter
 */
public abstract class BeanAdapter<T> extends BaseAdapter {

    public List<T> mVaules = null;

    private final Object mLock = new Object();

    protected int mResource;

    protected int mDropDownResource;

    protected boolean mNotifyOnChange = true;

    public LayoutInflater mInflater;

    public Map<Integer, InViewClickListener<T>> canClickItem;

    public boolean isReuse = true;


    @SuppressWarnings("rawtypes")
    public Class jumpClazz;
    public String jumpKey;
    public String jumpAs;

    @SuppressWarnings("rawtypes")
    public Class getJumpClazz() {
        return jumpClazz;
    }

    public String getJumpKey() {
        return jumpKey;
    }

    public String getJumpAs() {
        return jumpAs;
    }

    @SuppressWarnings("rawtypes")
    public void setJump(Class jumpClazz, String jumpkey, String as) {
        this.jumpClazz = jumpClazz;
        this.jumpKey = jumpkey;
        this.jumpAs = as;
    }

    public BeanAdapter(Context context, int mResource, boolean isViewReuse) {
        super();
        this.mResource = mResource;
        isReuse = isViewReuse;
        this.mDropDownResource = mResource;
        mInflater = LayoutInflater.from(new WeakReferenceUtil<Context>(context).getWeakT());
        mVaules = new ArrayList<T>();
    }

    public BeanAdapter(Context context, int mResource) {
        this(context, mResource, true);
    }

    @SuppressWarnings({"unchecked", "hiding"})
    public <T> List<T> getValues() {
        return (List<T>) mVaules;
    }

    public void add(T one) {
        if (one == null) {
            return;
        }
        synchronized (mLock) {
            mVaules.add(one);
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }

    public void addAll(List<T> ones) {
        if (ones == null || ones.size() == 0) {
            return;
        }
        synchronized (mLock) {
            mVaules.addAll(ones);
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }


    public void insert(int index, T one) {
        if (one == null) {
            return;
        }
        synchronized (mLock) {
            if (index < 0) index = 0;
            if (index < mVaules.size() && index >= 0)
                mVaules.add(index, one);
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }

    /**
     * @deprecated {@link #notifyItemOnIndex(AbsListView listView, int position){  }
     */
    public void notifyItemOnIndex(int index, T one) {
        setNotifyOnChange(false);
        remove(index);
        setNotifyOnChange(true);
        insert(index, one);
    }

    ;


    @SuppressWarnings("unused")
    public void notifyItemOnIndex(AbsListView listView, int position) {
        if (listView != null) {
            int firstVisible = listView.getFirstVisiblePosition();
            for (int i = 0; i <= listView.getLastVisiblePosition(); i++) {
                if (getTItem(position) == listView.getItemAtPosition(i)) {
                    View view = listView.getChildAt(i - firstVisible);
                    getView(i, view, listView);
                    break;
                }
            }
        }
//		if (listView != null) {
//			int start = listView.getFirstVisiblePosition();
//			for (int i = start, j = listView.getLastVisiblePosition(); i <= j; i++)
//				if (id == getItemId(i)) {
//					View view = listView.getChildAt(i - start);
//					getView(i, view, listView);
//					break;
//				}
//		}
    }


    /**
     * @param index
     * @deprecated
     */
    public void remove(int index) {
        removeMore(index);
    }

    public void removeMore(Integer... index) {
        synchronized (mLock) {
            for (int i : index) {
                if (i < mVaules.size() && i >= 0) {
                    mVaules.remove(i);
                }
            }
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }

    public void removeTMore(List<T> index) {
        synchronized (mLock) {
            for (T i : index) {
                if (mVaules.contains(i)) {
                    mVaules.remove(i);
                }
            }
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }

    public void clear() {
        synchronized (mLock) {
            mVaules.clear();
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }

    public int getCount() {
        return mVaules == null ? 0 : mVaules.size();
    }

    public Object getItem(int position) {
        if (position < mVaules.size() && position >= 0) {
            return mVaules.get(position);
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "hiding"})
    public <T> T getTItem(int position) {
        if (position < mVaules.size() && position >= 0) {
            return (T) mVaules.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getTItemId(int position) {

        return position + "";
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, mResource, parent);
    }

    public View getView(int position, View convertView, int mResource, ViewGroup parent) {
        ViewHolderWithBind viewHolderWithBind = null;
        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
            viewHolderWithBind = new ViewHolderWithBind(new BindChildValue(convertView));
            convertView.setTag(viewHolderWithBind);
        } else {
            viewHolderWithBind = (ViewHolderWithBind) convertView.getTag();
        }
        bindView(convertView, position, mVaules.get(position), viewHolderWithBind.bindChildValueImp);
        bindInViewListener(convertView, position, mVaules.get(position));
        bindInViewLogListener(convertView, position, mVaules.get(position));
        return convertView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(mDropDownResource, parent, false);
        } else {
            view = convertView;
        }
        bindView(view, position, mVaules.get(position), new BindChildValue(view));
        bindInViewListener(view, position, mVaules.get(position));
        bindInViewLogListener(view, position, mVaules.get(position));
        return view;
    }

    public abstract void bindView(View itemV, int position, T value, BindChildValueImp bind);

    private void bindInViewListener(final View itemV, final Integer position,
                                    final T valuesMap) {
        if (canClickItem != null) {
            for (Integer key : canClickItem.keySet()) {
                View inView = itemV.findViewById(key);
                final InViewClickListener<T> inviewListener = canClickItem
                        .get(key);
                if (inView != null && inviewListener != null) {
                    inView.setOnClickListener(new OnClickListener() {

                        public void onClick(View v) {
                            inviewListener.OnClickListener(itemV, v, position,
                                    valuesMap);
                        }
                    });
                }
            }
        }
    }
    private void bindInViewLogListener(final View itemV, final Integer position,
                                    final T valuesMap) {
        if (canClickItem != null) {
            for (Integer key : canClickItem.keySet()) {
                View inView = itemV.findViewById(key);
                final InViewClickListener<T> inviewListener = canClickItem
                        .get(key);
                if (inView != null && inviewListener != null) {
                    inView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            inviewListener.OnClickLongListener(itemV, view, position,
                                    valuesMap);
                            return false;
                        }
                    });

                }
            }
        }
    }
    /**
     * @deprecated
     */
    @Deprecated
    @SuppressLint("UseSparseArrays")
    public void setOnInViewClickListener(Integer key,
                                         InViewClickListener<T> inViewClickListener) {
        setOnInViewClickListener(inViewClickListener, key);
    }

    @SuppressLint("UseSparseArrays")
    public void setOnInViewClickListener(InViewClickListener<T> inViewClickListener, Integer... key) {
        for (Integer k : key) {
            if (canClickItem == null)
                canClickItem = new HashMap<Integer, InViewClickListener<T>>();
            canClickItem.put(k, inViewClickListener);
        }
    }

    public void setmDropDownResource(int mDropDownResource) {
        this.mDropDownResource = mDropDownResource;
    }

    public int getmDropDownResource() {
        return mDropDownResource;
    }


    /**
     * com.fph.appnavigationlib.adapter.BeanAdapter.java
     * <p>
     * Created by wang on 2016年6月12日下午4:05:06
     * <p>
     * Tips:
     */
    public static class ViewHolderWithBind {

        BindChildValueImp bindChildValueImp;

        public ViewHolderWithBind(BindChildValueImp bindChildValueImp) {
            this.bindChildValueImp = bindChildValueImp;
        }

    }


    /**
     * viewholder
     */
    public static class ViewHolder {
        Map<Integer, View> views;
        View itemV;

        public static ViewHolder getHolder(View itemV) {
            ViewHolder viewHolder = (ViewHolder) itemV.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder();
                itemV.setTag(viewHolder);
                viewHolder.itemV = itemV;
            }
            return viewHolder;
        }

        public View getView(Integer id) {
            View v = views.get(id);
            if (v == null) {
                v = itemV.findViewById(id);
                if (v != null) {
                    views.put(id, v);
                }
            }
            return v;
        }

        private ViewHolder() {
            super();
            views = new HashMap<Integer, View>();
        }

    }

}
