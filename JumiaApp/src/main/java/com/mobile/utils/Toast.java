
package com.mobile.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.view.R;


public class Toast  {
    public static final int LENGTH_LONG = android.widget.Toast.LENGTH_LONG;
    public static final int LENGTH_SHORT = android.widget.Toast.LENGTH_SHORT;
    //private int mDuration = LENGTH_SHORT;
    //private static ViewGroup mViewGroup;
    private static View mToastView;
    private static LayoutInflater mLayoutInflater;
    //private Context mActivity;
    private static TextView mMessageTextView;
    //private static LinearLayout mRootLayout;
    
    public Toast() {
//        super(activity);
        
        //this.mActivity = activity;

      
    }
    
    public static  android.widget.Toast makeText(Context activity, CharSequence s, int duration) {
        
        mLayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        mViewGroup = (ViewGroup) activity.findViewById(android.R.id.content);

        mToastView = mLayoutInflater.inflate(R.layout.supertoast, null, false);
        
        mMessageTextView = (TextView) mToastView.findViewById(R.id.message_textview);

        //mRootLayout = (LinearLayout) mToastView.findViewById(R.id.root_layout);
        
        // SHOP: added empty space to prevent string from being cutted on burmese
        if(activity.getResources().getBoolean(R.bool.is_shop_specific))
            s = s + " ";
        
        mMessageTextView.setText(s);
        
        android.widget.Toast toast = new  android.widget.Toast(activity);
        
        toast.setView(mToastView);
//        toast.setGravity(Gravity.BOTTOM, 0, 100);
        
//        SuperToast toast = new SuperToast(activity);
        toast.setDuration(duration);
        
        return toast;
    }
    


    public static  android.widget.Toast makeText(Context activity, int resId, int duration) {
       return Toast.makeText(activity, activity.getResources().getString(resId), duration);
    }


}
