package com.bamilo.android.appmodule.bamiloapp.utils;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class CheckableRelativeLayout extends RelativeLayout implements Checkable {
	
	public final static String TAG = CheckableRelativeLayout.class.getSimpleName();
	
	private boolean isChecked;

	public CheckableRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean isChecked() {
		return isChecked;
	}

	@Override
	public void setChecked(boolean checked) {
		isChecked = checked;

		int[] states = new int[1];
		if (isChecked)
			states[0] = android.R.attr.state_checked;
		else
			states[0] = - android.R.attr.state_checked;

		Drawable d = getBackground();
		if ( d != null )
		    d.setState(states);
		
		setSelected(checked);
	}

	@Override
	public void toggle() {
		setChecked(!isChecked);
	}
}
