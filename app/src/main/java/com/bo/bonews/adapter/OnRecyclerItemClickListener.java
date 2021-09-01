package com.bo.bonews.adapter;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class OnRecyclerItemClickListener implements RecyclerView.OnItemTouchListener{
    private GestureDetectorCompat mGestureDetector;
    private RecyclerView rlView;

    public OnRecyclerItemClickListener(RecyclerView rlView){
        this.rlView = rlView;
        mGestureDetector = new GestureDetectorCompat(rlView.getContext(),new ItemTouchHelperGestureListener());
    }
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e){
            View child = rlView.findChildViewUnder(e.getX(),e.getY());
            if (child != null){
                RecyclerView.ViewHolder vh = rlView.getChildViewHolder(child);
                int position = rlView.getChildLayoutPosition(child);
                onItemClick(vh,position);
            }
            return true;
        }
    }
    public abstract void onItemClick(RecyclerView.ViewHolder vh, int position);
    public abstract void onItemLongClick(RecyclerView.ViewHolder vh);
}
