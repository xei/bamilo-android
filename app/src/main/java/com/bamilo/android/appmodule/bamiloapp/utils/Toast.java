
package com.bamilo.android.appmodule.bamiloapp.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.TextView;
import com.bamilo.android.R;


public class Toast {

    public static final int LENGTH_LONG = android.widget.Toast.LENGTH_LONG;
    public static final int LENGTH_SHORT = android.widget.Toast.LENGTH_SHORT;

    public Toast() {
        // ...
    }

    public static android.widget.Toast makeText(@NonNull Context activity, @Nullable CharSequence s, int duration) {
        LayoutInflater mLayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mToastView = mLayoutInflater.inflate(R.layout.supertoast, null, false);
        TextView mMessageTextView = (TextView) mToastView.findViewById(R.id.message_textview);
        // SHOP: added empty space to prevent string from being cutted on burmese
        if (activity.getResources().getBoolean(R.bool.is_shop_specific)) {
            s = s + " ";
        }
        mMessageTextView.setText(s);
        android.widget.Toast toast = new android.widget.Toast(activity);
        toast.setView(mToastView);
        toast.setDuration(duration);
        return toast;
    }

    public static android.widget.Toast makeText(Context activity, int resId, int duration) {
        return Toast.makeText(activity, activity.getResources().getString(resId), duration);
    }


}
