package com.bamilo.android.framework.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

import com.bamilo.android.R;

/**
 * This class is a Grid component that expands own height.
 * The idea is use this component inside a ScrollView.
 *
 * @author sergiopereira
 * @see "http://stackoverflow.com/questions/4523609/grid-of-images-inside-scrollview"
 */
public class ExpandedGridViewComponent extends GridView {

    boolean expanded = false;
    int bottomMargin = 0;

    /**
     * Class Constructor
     */
    public ExpandedGridViewComponent(Context context) {
        super(context);
    }

    /**
     * Class Constructor
     */
    public ExpandedGridViewComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Class Constructor
     */
    public ExpandedGridViewComponent(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * Initialize view pager
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandedGridViewStyleable);
        // Get expanded value
        expanded = a.getBoolean(R.styleable.ExpandedGridViewStyleable_expand, false);
        a.recycle();
    }

    /*
     * Calculate the Grid height and expand it
     *
     * TODO - Create a method to calculate the Grid width
     *
     * (non-Javadoc)
     * @see android.widget.GridView#onMeasure(int, int)
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // HACK! TAKE THAT ANDROID!
        if (isExpanded()) {
            // Calculate entire height by providing a very large height hint.
            // But do not use the highest 2 bits of this integer; those are
            // reserved for the MeasureSpec mode.
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
            //
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight() + bottomMargin;
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * Get Expandable Value
     *
     * @return true if the view is expanded
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * Set Expandable Value
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }


    /**
     * Set a bottom margin to take into account when calculating the heigh of the grid
     *
     * @param bottomMargin the bottomMargin to set
     */
    @SuppressWarnings("unused")
    public void setBottomMargin(int bottomMargin) {
        this.bottomMargin = bottomMargin;
    }
}
