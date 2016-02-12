package com.mobile.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;

import java.util.ArrayList;

public class RadioGroupLayout extends LinearLayout {

    private final static String TAG = RadioGroupLayout.class.getSimpleName();

    public static final int NO_DEFAULT_SELECTION = -1;
    private ArrayList<?> mItems;
    private ArrayList<?> mItemsKeys;
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

    public void setItems(ArrayList<?> items, int defaultSelected, ArrayList<?> itemsKeys) {
        Print.d(TAG, "setItems: items size = " + items.size() + " defaultSelected = " + defaultSelected);
        mItems = items;
        mDefaultSelected = defaultSelected;
        mItemsKeys = itemsKeys;
        if(mItemsKeys != null) updateRadioGroupWithIcons();
        else updateRadioGroup();
    }
    
    /**
     *
     */
    private void updateRadioGroup() {
        try {
            mGroup.removeAllViews();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        for (int idx = 0; idx < mItems.size(); idx++) {
            //Log.d(TAG, "updateRadioGroup: inserting idx = " + idx + " name = " + mItems.get(idx));
            RadioButton button = (RadioButton) mInflater.inflate(R.layout.form_radiobutton, mGroup, false);
            button.setId(idx);
            button.setText(mItems.get(idx).toString());

            if (idx == mDefaultSelected) button.setChecked(true);
            mGroup.addView(button, idx);
        }
    }

    /**
     *
     */
    private void updateRadioGroupWithIcons() {
        try {
            mGroup.removeAllViews();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        for (int idx = 0; idx < mItems.size(); idx++) {
            RadioButton button = (RadioButton) mInflater.inflate(R.layout.form_radiobutton, mGroup, false);
            UIUtils.setDrawableLeftByString(button, DynamicFormItem.ICON_PREFIX + mItemsKeys.get(idx).toString().toLowerCase());
            button.setId(idx);
            button.setText(mItems.get(idx).toString());
            button.setTextColor(ContextCompat.getColor(getContext(),R.color.white));
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
        String result = null;
        if (CollectionUtils.isNotEmpty(mItems) && idx >= 0) {
            result = mItems.get(idx).toString();
        }
        return result;
    }

    public void setSelection(int idx) {
        if (idx >= 0 && idx < mItems.size()) {
            RadioButton button = (RadioButton) mGroup.getChildAt(idx);
            button.setChecked(true);
        }
    }

    public String getSelectedFieldValue() {
        return getItemByIndex(getSelectedIndex());
    }

}
