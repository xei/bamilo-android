/**
 * 
 */
package com.bamilo.android.framework.components.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.bamilo.android.framework.components.absspinner.IcsSpinner;

/**
 * <code>IcsSpinner</code> that allows to call method <code>onDetachedFromWindow()</code> which will
 * dismiss the Spinner popup programatically
 * 
 * @author Andre Lopes
 * 
 */
public class DismissibleSpinner extends IcsSpinner {
    public DismissibleSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DismissibleSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void dismiss() {
        onDetachedFromWindow();
    }
}
