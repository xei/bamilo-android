package com.mobile.components.viewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.mobile.components.infiniteviewpager.InfiniteViewPager;
import com.mobile.framework.R;

/**
 * Created by spereira on 4/27/15.
 * @author spereira
 */
public class PreviewViewPager extends InfiniteViewPager {

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
        // Show preview offset
        applyPreviewOffset();
        // Set page transformer
        applyTransformer();
    }

    private void applyTransformer() {
        if (pageTransformer == FADE_TRANSFORMER) {
            setPageTransformer(false, new FadePageTransformer(this));
        }
    }

    public void setPreviewOffset(int offset) {
        // Save offset value
        previewOffset = offset;
        // Show preview
        applyPreviewOffset();
    }

    private void applyPreviewOffset() {
        // Set preview offset
        if (previewOffset > NO_PREVIEW_OFFSET) {
            setOffscreenPageLimit(PREVIEW_OFFSCREEN_PAGE_LIMIT);
            //previewOffset = (int) (DeviceInfoHelper.getWidth(getContext()) * 0.075f); // 15%
            setPadding(previewOffset, getPaddingTop(), previewOffset, getPaddingBottom());
            setClipToPadding(false);
            //setClipChildren(false);
            //pager.setPageMargin(100);
        }
    }

}
