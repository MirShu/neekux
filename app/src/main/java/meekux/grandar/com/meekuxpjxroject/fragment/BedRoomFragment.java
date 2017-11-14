package meekux.grandar.com.meekuxpjxroject.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.utils.DataTools;
import meekux.grandar.com.meekuxpjxroject.utils.ItemBean;
import meekux.grandar.com.meekuxpjxroject.utils.ItemTouchHelperAdapter;
import meekux.grandar.com.meekuxpjxroject.utils.RecyclerItemClickListener;
import meekux.grandar.com.meekuxpjxroject.utils.SimpleItemTouchHelperCallback;

/**
 * Author:SeanLim
 * Created by Time on: 2017/9/12
 * 类名：BedRoomFragment   卧室
 */
public class BedRoomFragment extends Fragment {
    private RecyclerView minerecycleView;
    private List<ItemBean> mineData = new ArrayList<>();
    private List<ItemBean> otherData = new ArrayList<>();
    private ItemTouchHelper.Callback callback;
    private ItemTouchHelper touchHelper;
    private ItemTouchHelper.Callback othercallback;
    private ItemTouchHelper othertouchHelper;
    private MyAdapter otherAdapter;
    private RecyclerView other_recycleView;
    private MyAdapter mineAdapter;
    private TextView edit;
    boolean isediting = false;
    private int screenWidth;
    private int recycleview_width;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bed_room, null);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        recycleview_width = (screenWidth - DataTools.dip2px(getActivity(), 60)) / 3;
        initView();
        initData();
        return view;
    }

    private void initView() {
        minerecycleView = view.findViewById(R.id.mine_recycleView);
        edit = view.findViewById(R.id.edit);
        mineAdapter = new MyAdapter(mineData, 0);
        minerecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        minerecycleView.setAdapter(mineAdapter);
        minerecycleView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        callback = new SimpleItemTouchHelperCallback(mineAdapter);

        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(minerecycleView);

        other_recycleView = view.findViewById(R.id.other_recycleView);
        otherAdapter = new MyAdapter(otherData, 1);
        other_recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        other_recycleView.setAdapter(otherAdapter);
        other_recycleView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        othercallback = new SimpleItemTouchHelperCallback(otherAdapter);
        othertouchHelper = new ItemTouchHelper(othercallback);
        othertouchHelper.attachToRecyclerView(other_recycleView);

        edit.setOnClickListener(view1 -> {
            if (isediting) {
                edit.setText("编辑");
                for (int i = 0; i < mineData.size(); i++) {
                    mineData.get(i).setSelect(false);
                }
            } else {
                edit.setText("完成");
                for (int i = 0; i < mineData.size(); i++) {
                    mineData.get(i).setSelect(true);
                }
            }
            isediting = !isediting;
            mineAdapter.notifyDataSetChanged();

        });


        minerecycleView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, RecyclerView.ViewHolder childViewViewHolder) {

            }

            @Override
            public void onLongClick(View view, int posotion, RecyclerView.ViewHolder childViewViewHolder) {
                if (posotion != 0 && posotion != 1) {
//                    for (int i = 0; i < mineData.size(); i++) {
//                        mineData.get(i).setSelect(true);
//                    }
//                    isediting = true;
//                    edit.setText("完成");
//                    mineAdapter.allSelect();
                    touchHelper.startDrag(childViewViewHolder);
                }

            }
        }));


        other_recycleView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position, RecyclerView.ViewHolder childViewViewHolder) {

                final ImageView moveImageView = getView(view);
                if (moveImageView != null) {
                    TextView newTextView = view.findViewById(R.id.channel_tv);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final ItemBean channel = otherAdapter.getItem(position);
                    if (isediting) {
                        channel.setSelect(true);
                    } else {
                        channel.setSelect(false);
                    }
                    mineAdapter.addItem(channel);
                    new Handler().postDelayed(() -> {
                        try {
                            int[] endLocation = new int[2];
                            RecyclerView.LayoutManager mine_layoutManager = minerecycleView.getLayoutManager();
                            if (mine_layoutManager instanceof LinearLayoutManager) {
                                LinearLayoutManager linearManager = (LinearLayoutManager) mine_layoutManager;
                                int lastItemPosition = linearManager.findLastVisibleItemPosition();
                                minerecycleView.getChildAt(lastItemPosition).getLocationInWindow(endLocation);
                                MoveAnim(moveImageView, startLocation, endLocation);
                                otherAdapter.deleteItem(position);
                            }
                        } catch (Exception localException) {
                        }
                    }, 50L);
                }
            }

            @Override
            public void onLongClick(View view, int posotion, RecyclerView.ViewHolder childViewViewHolder) {

            }
        }));
    }

    private void initData() {
        for (int i = 0; i < 20; i++) {
            mineData.add(new ItemBean("灯泡" + i, false));
        }
        for (int i = 20; i < 30; i++) {
            otherData.add(new ItemBean("灯带" + i, false));
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements ItemTouchHelperAdapter {
        private List<ItemBean> mData;
        private int type;

        public MyAdapter(List<ItemBean> list, int type) {
            this.mData = list;
            this.type = type;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(getActivity()).inflate(R.layout.item_list, parent, false);
            MyViewHolder vh = new MyViewHolder(root);
            //为Item设置点击事件
            return vh;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.channel_tv.setText(mData.get(position).getText());
            if (type == 1) {
                holder.delete_tv.setVisibility(View.GONE);
            } else if (type == 0) {
                if (position == 0 || position == 1) {
                    holder.delete_tv.setVisibility(View.GONE);
                } else if (mData.get(position).isSelect()) {
                    holder.delete_tv.setVisibility(View.VISIBLE);
                } else {
                    holder.delete_tv.setVisibility(View.GONE);
                }
            }
            ViewGroup.LayoutParams params = holder.channel_rl.getLayoutParams();
            params.width = recycleview_width;
            holder.channel_rl.setLayoutParams(params);

            holder.delete_tv.setOnClickListener(view1 -> {
                if (type == 0) {
                    ItemBean positionItemBean = mineAdapter.getItem(position);
                    positionItemBean.setSelect(false);
                    mineAdapter.deleteItem(position);
                    otherAdapter.addItem(positionItemBean);
                }
            });
        }

        @Override
        public int getItemCount() {
            //注意:这里最少.有一个,因为有多了一个添加按钮
            return null == mData ? 0 : mData.size();
        }


        public ItemBean getItem(int position) {
            return mData.get(position);
        }

        public void addItem(ItemBean channel) {
            mData.add(channel);
            notifyDataSetChanged();
        }

        public void allSelect() {
            for (int i = 0; i < mData.size(); i++) {
                mData.get(i).setSelect(true);
            }
            notifyDataSetChanged();
        }

        public void addItem(int position, ItemBean channel) {
            mData.add(position, channel);
            notifyDataSetChanged();
        }

        public void deleteItem(int position) {
            mData.remove(position);
            notifyDataSetChanged();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView channel_tv;
            private ImageView delete_tv;
            private LinearLayout channel_rl;

            public MyViewHolder(View itemView) {
                super(itemView);
                channel_tv = itemView.findViewById(R.id.channel_tv);
                delete_tv = itemView.findViewById(R.id.delete_tv);
                channel_rl = itemView.findViewById(R.id.channel_rl);
            }
        }

        //        移动处理
        @Override
        public void onItemMove(RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            if (type == 0) {
                int fromPosition = source.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
//                Toast.makeText(MainActivity.this, "fromPosition>>" + fromPosition + ">>toPosition>>" + toPosition, Toast.LENGTH_LONG).show();
                if (toPosition == 0 || toPosition == 1) {

                } else {
                    if (fromPosition < mData.size() && toPosition < mData.size()) {
                        //交换数据位置
//                        Collections.swap(mData, fromPosition, toPosition);
                        ItemBean itemBean = mData.get(fromPosition);
                        mData.remove(fromPosition);
                        mData.add(toPosition, itemBean);
                        //刷新位置交换
                        notifyItemMoved(fromPosition, toPosition);
                    }
                }
                //移动过程中移除view的放大效果
                onItemClear(source);
            }
        }

        //        移除
        @Override
        public void onItemDissmiss(RecyclerView.ViewHolder source) {
            if (type == 0) {
                int position = source.getAdapterPosition();
                mData.remove(position); //移除数据
                notifyItemRemoved(position);//刷新数据移除
            }
        }

        //        放大
        @Override
        public void onItemSelect(RecyclerView.ViewHolder viewHolder) {
            if (type == 0) {
                //当拖拽选中时放大选中的view
                int position = viewHolder.getAdapterPosition();
                viewHolder.itemView.setScaleX(1.2f);
                viewHolder.itemView.setScaleY(1.2f);
            }
        }

        //   恢复
        @Override
        public void onItemClear(RecyclerView.ViewHolder viewHolder) {
            //拖拽结束后恢复view的状态
            if (type == 0) {
                viewHolder.itemView.setScaleX(1.0f);
                viewHolder.itemView.setScaleY(1.0f);
            }
        }
    }

    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(getActivity());
        iv.setImageBitmap(cache);
        return iv;
    }

    private void MoveAnim(View moveView, int[] startLocation, int[] endLocation
    ) {

        int[] initLocation = new int[2];
        moveView.getLocationInWindow(initLocation);
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            private boolean isMove;

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                isMove = false;
            }
        });
    }

    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) getActivity().getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(getActivity());
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }

}
