package com.mobile.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * 
 * This class is a Grid component that expands own height.
 * The idea is use this component inside a ScrollView.
 * 
 * @author sergiopereira
 * 
 * @see http://stackoverflow.com/questions/4523609/grid-of-images-inside-scrollview 
 * 
 */
public class ExpandableGridViewComponent extends GridView {

	boolean expanded = false;
	int bottomMargin = 0;

	/**
	 * Class Constructor
	 */
	public ExpandableGridViewComponent(Context context) {
		super(context);
	}

	/**
	 * Class Constructor
	 */
	public ExpandableGridViewComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Class Constructor
	 */
	public ExpandableGridViewComponent(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
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
			params.height = getMeasuredHeight() + bottomMargin ;
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

    /**
	 * Get Expandable Value
	 * @return true if the view is expandaded
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
     * @param bottomMargin the bottomMargin to set
     */
    public void setBottomMargin(int bottomMargin) {
        this.bottomMargin = bottomMargin;
    }	
}
