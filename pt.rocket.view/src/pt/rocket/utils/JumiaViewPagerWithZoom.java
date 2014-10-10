package pt.rocket.utils;

import pt.rocket.framework.utils.LogTagHelper;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import de.akquinet.android.androlog.Log;

public class JumiaViewPagerWithZoom extends ViewPager {
    private boolean isPagingEnabled = true;
    private String TAG = LogTagHelper.create(JumiaViewPagerWithZoom.class);

    public JumiaViewPagerWithZoom(Context context) {
        super(context);
    }

    public JumiaViewPagerWithZoom(Context context, AttributeSet attrs) {
        super(context, attrs);
        // setJumiaScroller(true);
        this.isPagingEnabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isPagingEnabled) {
            try {
                return super.onTouchEvent(event);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "catched IllegalArgumentException JumiaViewPagerWithZoom line 34.");
            }
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.isPagingEnabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }

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
}
