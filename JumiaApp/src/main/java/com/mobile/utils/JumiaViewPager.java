package com.mobile.utils;
//package com.mobile.utils;
//
//import java.lang.reflect.Field;
//
//import android.content.Context;
//import android.support.v4.view.ViewPager;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.animation.DecelerateInterpolator;
//import android.widget.Scroller;
//
//public class JumiaViewPager extends ViewPager {
//
//    private boolean isPagingEnabled = true;
//    public JumiaViewPager(Context context) {
//        super(context);
//        // TODO Auto-generated constructor stub
//    }
//
//    public JumiaViewPager(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        // TODO Auto-generated constructor stub
//        setJumiaScroller(true);
//        this.isPagingEnabled = true;
//    }
//
//    @Override
//    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
//        if (v != this && v instanceof ViewPager) {
//            return true;
//        }
//        return super.canScroll(v, checkV, dx, x, y);
//    }
//    
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (this.isPagingEnabled) {
//            return super.onTouchEvent(event);
//        }
//
//        return false;
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        if (this.isPagingEnabled) {
//            return super.onInterceptTouchEvent(event);
//        }
//
//        return false;
//    }
//
//    public void setPagingEnabled(boolean b) {
//        this.isPagingEnabled = b;
//    }
//    
//    
//    public void toggleJumiaScroller(boolean animated) {
//        setJumiaScroller(animated);
//    }
//
//    private void setJumiaScroller(boolean animated)
//    {
//        try
//        {
//            Class<?> viewpager = ViewPager.class;
//            Field scroller = viewpager.getDeclaredField("mScroller");
//            scroller.setAccessible(true);
//            if (animated)
//                scroller.set(this, new JumiaScrollerAnimated(getContext()));
//            else
//                scroller.set(this, new JumiaScroller(getContext()));
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//// define scroller effect frist and last fragment 
//    public class JumiaScroller extends Scroller
//    {
//        public JumiaScroller(Context context)
//        {
//            super(context, new DecelerateInterpolator());
//        }
//
//        @Override
//        public synchronized void startScroll(int startX, int startY, int dx, int dy, int duration)
//        {   
//            super.startScroll(startX, startY, dx, dy, 0 ); // 0 seconds
//    
//        }
//    }
// // define scroller effect for default fragments
//    public class JumiaScrollerAnimated extends Scroller
//    {
//        public  JumiaScrollerAnimated(Context context)
//        {
//            super(context, new DecelerateInterpolator());
//        }
//
//        @Override
//        public synchronized void startScroll(int startX, int startY, int dx, int dy, int duration)
//        {   
//            super.startScroll(startX, startY, dx, dy,200 ); // 200 ms
//           
//        }
//    }
//}
