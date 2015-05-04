package com.mobile.components.viewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.mobile.framework.R;

/**
 * Created by spereira on 4/27/15.
 * @author spereira
 */
public class PreviewViewPager extends ViewPager {

    public static final String TAG = PreviewViewPager.class.getSimpleName();

    private static final int NO_PREVIEW_OFFSET = 0;

    private static final int PREVIEW_OFFSCREEN_PAGE_LIMIT = 2;

    // These values must be the same used in PreviewViewPagerStyleable_transformer
    private static final int NONE_TRANSFORMER = 0;
    private static final int FADE_TRANSFORMER = 1;

    private int previewOffset;

    private int pageTransformer;

    /**
     * Constructor
     */
    public PreviewViewPager(Context context) {
        super(context);
    }

    /**
     * Constructor with attrs
     */
    public PreviewViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Initialize view pager
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PreviewViewPagerStyleable);
        // Get preview offset
        previewOffset = a.getDimensionPixelSize(R.styleable.PreviewViewPagerStyleable_previewOffset, NO_PREVIEW_OFFSET);
        // Get page transformer
        pageTransformer = a.getInt(R.styleable.PreviewViewPagerStyleable_transformer, NONE_TRANSFORMER);
        a.recycle();
        // Set view pager with preview offset and page transformer
        setViewPager();
    }

    /**
     * Set the view pager using values from attrs
     */
    private void setViewPager() {
        // Set preview offset
        if (previewOffset > NO_PREVIEW_OFFSET) {
            setOffscreenPageLimit(PREVIEW_OFFSCREEN_PAGE_LIMIT);
            //float mLandscapeMarginFromWidth = DeviceInfoHelper.getWidth(getContext()) * 0.075f;
            setPadding(previewOffset, getPaddingTop(), previewOffset, getPaddingBottom());
            setClipToPadding(false);
            //setClipChildren(false);
            //pager.setPageMargin(100);
        }
        // Set page transformer
        if (pageTransformer == FADE_TRANSFORMER) {
            setPageTransformer(false, new FadePageTransformer(this));
        }
    }

}
