package com.bamilo.android.appmodule.bamiloapp.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.bamilo.android.appmodule.bamiloapp.factories.FormFactory;
import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.constants.FormConstants;
import com.bamilo.android.framework.service.forms.Form;
import com.bamilo.android.framework.service.forms.PaymentInfo;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicForm;
import com.bamilo.android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RadioGroupLayoutVertical extends RadioGroup {
    private final static String TAG = RadioGroupLayoutVertical.class.getSimpleName();

    private ArrayList<String> mItems;
    private HashMap<String, Form> formsMap;
    private HashMap<Integer, DynamicForm> generatedForms;
    private int mDefaultSelected;
    private RadioGroup mGroup;
    private LayoutInflater mInflater;
    Context mContext;
    private HashMap<String, PaymentInfo> mPaymentInfo;

    public RadioGroupLayoutVertical(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public RadioGroupLayoutVertical(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
        mGroup = this;
    }

    /**
     * Flag used to show the radio button with right position style
     */
    private boolean isRightPositionStyle = false;

    /**
     * Method used to set values from the options map with respective default value.<br>
     * - With right style
     */
    public void setItems(@NonNull Map<String, String> items, String defaultValue) {
        // Find the default value
        int defaultSelect = IntConstants.INVALID_POSITION;
        for (Map.Entry<String, String> entryValue : items.entrySet()) {
            defaultSelect++;
            if (TextUtils.equals(entryValue.getKey(), defaultValue)) {
                break;
            }
        }
        // set and show items
        setItems(new ArrayList<>(items.values()), new HashMap<>(), null, defaultSelect);
    }

    public void enableRightStyle() {
        isRightPositionStyle = true;
    }

    public void setItems(ArrayList<String> items, HashMap<String, Form> map, HashMap<String, PaymentInfo> paymentInfoMap, int defaultSelected) {
        mItems = items;
        formsMap = map;
        mPaymentInfo = paymentInfoMap;
        mDefaultSelected = defaultSelected;
        updateRadioGroup();
    }

    @SuppressLint("UseSparseArrays")
    private void updateRadioGroup() {
        try {
            mGroup.removeAllViews();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        int idx;
        generatedForms = new HashMap<>();
        for (idx = 0; idx < mItems.size(); idx++) {
            if (formsMap.containsKey(mItems.get(idx))) {
                createRadioButton(idx, mPaymentInfo, true);
            } else if (CollectionUtils.isNotEmpty(mPaymentInfo) && mPaymentInfo.containsKey(mItems.get(idx))) {
                createRadioButton(idx, mPaymentInfo, false);
            }
            // Generic radio button Layout
            else {
                final RadioButton button;
                if(isRightPositionStyle) {
                    button = (RadioButton) mInflater.inflate(R.layout._def_form_radio_button_right, this, false);
                    mGroup.addView(button, idx);
                } else {
                    button = (RadioButton) mInflater.inflate(R.layout.form_radiobutton, null, false);
                    LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    mGroup.addView(button, idx, layoutParams);
                }
                // Set the button
                button.setId(idx);
                button.setText(mItems.get(idx));
            }
        }
        // Set the default radio button checked
        checkDefaultOption(mDefaultSelected);
    }

    /**
     * Method used to check the default option.<br>
     * The runnable is a hack for Android Marshmallow (API 23)
     */
    private void checkDefaultOption(final int mDefaultSelected) {
        post(() -> {
            View child = getChildAt(mDefaultSelected);
            if (child != null) {
                check(child.getId());
            }
        });
    }

    /**
     * Create a Radio button with an extra LinearLayout for content
     * 
     * @param idx index of label
     * @param paymentsInfoList list of PaymentInfo items 
     * @param addInnerForm used to indicate if a inner Form should be created inside the <code>extras LinearLayout</code>
     */
    private void createRadioButton(int idx, HashMap<String, PaymentInfo> paymentsInfoList, boolean addInnerForm) {
        // Get views
        final LinearLayout container = (LinearLayout) mInflater.inflate(R.layout.form_radiobutton_with_extra, null, false);
        final LinearLayout extras = container.findViewById(R.id.radio_extras_container);
        final RadioButton button = container.findViewById(R.id.radio_shipping);

        if (addInnerForm) {
            DynamicForm formGenerator = FormFactory.create(FormConstants.PAYMENT_DETAILS_FORM, mContext, formsMap.get(mItems.get(idx)));
            generatedForms.put(idx, formGenerator);
            extras.addView(formGenerator.getContainer());
        }

        // Hide first divider
        if (idx == 0) {
            container.findViewById(R.id.radio_divider).setVisibility(View.GONE);
        }

        RelativeLayout.LayoutParams mParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        container.setId(idx);
        container.setLayoutParams(mParams);
        
        if (paymentsInfoList != null && paymentsInfoList.size() > 0 && paymentsInfoList.containsKey(mItems.get(idx))) {
            TextView mTextView = extras.findViewById(R.id.payment_text);
            String paymentText = paymentsInfoList.get(mItems.get(idx)).getText();
            if(TextUtils.isNotEmpty(paymentText)){
                mTextView.setText(paymentText);
                mTextView.setVisibility(View.VISIBLE);
            }
        }

        button.setText(mItems.get(idx));
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.checkout_shipping_item_height));
        layoutParams.setMargins(0, 0, getResources().getDimensionPixelSize(R.dimen.form_radiobutton_shipping_margin), 0);
        button.setText(mItems.get(idx));
        button.setOnClickListener(v -> {
            try {
                if (button.isChecked()) {
                    extras.setVisibility(View.VISIBLE);
                } else {
                    extras.setVisibility(View.GONE);
                }
            } catch (StackOverflowError e) {
                e.printStackTrace();
            }
            setSelection(container.getId());
            mGroup.check(container.getId());
        });

        // Set default
        button.setChecked(idx == mDefaultSelected);
        // Add radio option
        mGroup.addView(container);
    }

    public int getSelectedIndex() {
        int radioButtonID = mGroup.getCheckedRadioButtonId();
        View radioButton = mGroup.findViewById(radioButtonID);
        return mGroup.indexOfChild(radioButton);
    }

    public String getItemByIndex(int idx) {
        if (mItems == null)
            return null;
        if (idx < 0)
            return null;
        return mItems.get(idx);
    }

    public void setSelection(int idx) {
        if (idx >= 0) {
            if (mGroup.getChildAt(idx) instanceof RadioButton) {
                RadioButton button = (RadioButton) mGroup.getChildAt(idx);
                button.setChecked(true);
            }
            else if (mGroup.getChildAt(idx).findViewById(R.id.radio_shipping) instanceof RadioButton) {
                RadioButton button = mGroup.getChildAt(idx).findViewById(R.id.radio_shipping);
                button.setChecked(true);
            }
        }
    }
    
    public void setPaymentSelection(int idx) {
        if (idx >= 0) {
            if (mGroup.getChildAt(idx) instanceof RadioButton) {
                RadioButton button = (RadioButton) mGroup.getChildAt(idx);
                button.setChecked(true);
                setSelection(idx);
                mGroup.check(idx);
            }
            else if (mGroup.getChildAt(idx).findViewById(R.id.radio_shipping) instanceof RadioButton) {
                RadioButton button = mGroup.getChildAt(idx).findViewById(R.id.radio_shipping);
                button.setChecked(true);
                setSelection(idx);
                mGroup.check(idx);
            }
        }
    }

    public boolean validateSelected() {
        boolean result;
        if (mGroup.getChildAt(mGroup.getCheckedRadioButtonId()) instanceof RadioButton) {
            result = true;
        } else if(!(mGroup.getChildAt(mGroup.getCheckedRadioButtonId()) instanceof RadioButton) && !generatedForms.containsKey(mGroup.getCheckedRadioButtonId())){
            result = true;
        } else {
            result = generatedForms.get(mGroup.getCheckedRadioButtonId()).validate();
        }
        return result;
    }

    public String getErrorMessage() {
        return generatedForms.get(mGroup.getCheckedRadioButtonId()).getItem(0).getMessage();
    }

    public ContentValues getSubFieldParameters() {
        ContentValues result = null;
        if (generatedForms != null && generatedForms.get(mGroup.getCheckedRadioButtonId()) != null) {
            result = generatedForms.get(mGroup.getCheckedRadioButtonId()).save();
        }
        return result;
    }

    public String getSelectedFieldName() {
        return mItems.get(mGroup.getCheckedRadioButtonId());
    }

    public String getSelectedFieldValue() {
        return getItemByIndex(getSelectedIndex());
    }

    /**
     * Saves the sub field state (Payment Checkbox).
     */
    public void saveSubFieldState(@NonNull Bundle state) {
        if (generatedForms != null && generatedForms.get(mGroup.getCheckedRadioButtonId()) != null) {
            generatedForms.get(mGroup.getCheckedRadioButtonId()).saveFormState(state);
        }
    }

    /**
     * Loads the saved state (Payment Checkbox).
     */
    public void loadSubFieldState(@Nullable Bundle state) {
        if (generatedForms != null && generatedForms.get(mGroup.getCheckedRadioButtonId()) != null) {
            generatedForms.get(mGroup.getCheckedRadioButtonId()).loadSaveFormState(state);
        }
    }

}
