package cz.muni.fi.a2p06.stolencardatabase;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by robert on 14.3.2017.
 */

public class CarListListener extends RecyclerView.SimpleOnItemTouchListener {

    interface OnCarListClickListener {
        void onItemClick(View view, int position);
    }

    private GestureDetector mGestureDetector;
    private OnCarListClickListener mListener;

    public CarListListener(Context context, final RecyclerView recyclerView, OnCarListClickListener carListClickListener) {
        mListener = carListClickListener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mListener != null) {
                    mListener.onItemClick(child, recyclerView.getChildAdapterPosition(child));
                }
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (mGestureDetector != null) {
            boolean result = mGestureDetector.onTouchEvent(e);
            return result;
        }

        return false;
    }

}
