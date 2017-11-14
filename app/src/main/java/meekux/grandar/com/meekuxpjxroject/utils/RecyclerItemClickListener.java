package meekux.grandar.com.meekuxpjxroject.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Author:SeanLim
 * Created by Time on: 2017/9/8
 * 类名：RecyclerItemClickListener
 */
public class RecyclerItemClickListener implements  RecyclerView.OnItemTouchListener {

    private View childView;
    private RecyclerView touchView;
    private RecyclerView.ViewHolder viewHolder;
    GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, final OnItemClickListener mListener) {
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent ev) {
                if (childView != null && mListener != null) {
                    mListener.onItemClick(childView, touchView.getChildPosition(childView),viewHolder);
                }
                return true;
            }
            @Override
            public void onLongPress(MotionEvent ev) {
                if (childView != null && mListener != null) {
                    mListener.onLongClick(childView, touchView.getChildPosition(childView),viewHolder);
                }
            }
        });
    }



    public interface OnItemClickListener {
        public void onItemClick(View view, int position, RecyclerView.ViewHolder viewHolder);
        public void onLongClick(View view, int posotion, RecyclerView.ViewHolder viewHolder);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        mGestureDetector.onTouchEvent(motionEvent);
        childView = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        touchView = recyclerView;
        if (childView!=null){
            viewHolder = recyclerView.getChildViewHolder(childView);
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
