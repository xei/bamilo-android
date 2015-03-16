package com.mobile.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.mobile.framework.utils.LogTagHelper;
import com.mobile.view.R;

import java.util.ArrayList;

import de.akquinet.android.androlog.Log;

public class RadioGroupLayout extends LinearLayout {
    private final static String TAG = LogTagHelper.create(RadioGroupLayout.class);

    public interface OnRadioGroupSelected {
        public void onRadioGroupItemSelected(int position);
    }

    public static final int NO_DEFAULT_SELECTION = -1;

    private ArrayList<String> mItems;
    private int mDefaultSelected;
    private RadioGroup mGroup;
    private LayoutInflater mInflater;

    public RadioGroupLayout(Context context) {
        super(context);
        init();
    }

    public RadioGroupLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressLint("NewApi")
    public RadioGroupLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
        mGroup = (RadioGroup) mInflater.inflate(R.layout.form_radiogroup, this, false);
        this.addView(mGroup);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mGroup.setOnCheckedChangeListener(listener);
    }

    public void setItems(ArrayList<String> items, int defaultSelected) {
        Log.d(TAG, "setItems: items size = " + items.size() + " defaultSelected = " + defaultSelected);
        mItems = items;
        mDefaultSelected = defaultSelected;
        updateRadioGroup();
    }

    
    /**
     * TODO: Validate this method to use the R.layout.form_radiobutton and not button.setPadding()
     */
    private void updateRadioGroup() {
        try {
            mGroup.removeAllViews();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        
        int idx;
        for (idx = 0; idx < mItems.size(); idx++) {
            Log.d(TAG, "updateRadioGroup: inserting idx = " + idx + " name = " + mItems.get(idx));
            RadioButton button = (RadioButton) mInflater.inflate(R.layout.form_radiobutton, mGroup, false);
            button.setId(idx);
            button.setText(mItems.get(idx));
            if (idx == mDefaultSelected) button.setChecked(true);
            mGroup.addView(button, idx);
        }

    }

    public int getSelectedIndex() {
        int radioButtonID = mGroup.getCheckedRadioButtonId();
        View radioButton = mGroup.findViewById(radioButtonID);

        return mGroup.indexOfChild(radioButton);
    }

    public String getItemByIndex(int idx) {
        if (mItems == null)
            return null;
        if( idx < 0 )
            return null;
        return mItems.get(idx);
    }

    public void setSelection(int idx) {
        if(idx>=0){
            RadioButton button = (RadioButton) mGroup.getChildAt(idx);
            button.setChecked(true);    
            Log.i(TAG, "code1select :RadioButton "+idx);
        }
        
    }

}
