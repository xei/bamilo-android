/**
 * 
 */
package com.mobile.components.widget;

import com.mobile.components.absspinner.IcsSpinner;
import android.content.Context;
import android.util.AttributeSet;

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
