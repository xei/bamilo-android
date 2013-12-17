package pt.rocket.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;

public class JumiaCatalogViewPager extends ViewPager {

    public JumiaCatalogViewPager(Context context) {
        super(context);
    }

    public JumiaCatalogViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        return super.canScroll(v, checkV, dx, x, y);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ImageLoader.getInstance().pause();
        return super.onTouchEvent(event);
    }
    
    @Override
    public void setOffscreenPageLimit(int limit) {
        super.setOffscreenPageLimit(1);
    }
}
