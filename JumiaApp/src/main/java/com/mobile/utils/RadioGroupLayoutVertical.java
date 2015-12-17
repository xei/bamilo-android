package com.mobile.utils;

import android.content.ContentValues;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.PaymentInfo;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.HashMap;

public class RadioGroupLayoutVertical extends RadioGroup {
    private final static String TAG = RadioGroupLayoutVertical.class.getSimpleName();
    public static final int NO_DEFAULT_SELECTION = -1;

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

    public void setItems(ArrayList<String> items, HashMap<String, Form> map, HashMap<String, PaymentInfo> paymentInfoMap, int defaultSelected) {
        Print.d(TAG, "setItems: items size = " + items.size() + " defaultSelected = " + defaultSelected);
        mItems = items;
        formsMap = map;
        mPaymentInfo = paymentInfoMap;
        mDefaultSelected = defaultSelected;
        updateRadioGroup();
    }

    private void updateRadioGroup() {
        try {
            mGroup.removeAllViews();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        int idx;
        generatedForms = new HashMap<>();
        for (idx = 0; idx < mItems.size(); idx++) {
            Print.i(TAG, "code1subForms updateRadioGroup : " + mItems.get(idx) + " formsMap size : " + formsMap.size());
            if (formsMap.containsKey(mItems.get(idx))) {
                createRadioButton(idx, mPaymentInfo, true);
            } else if (mPaymentInfo != null && mPaymentInfo.size() > 0 && mPaymentInfo.containsKey(mItems.get(idx))) {
                createRadioButton(idx, mPaymentInfo, false);
            } else {
                Print.d(TAG, "code1subForms updateRadioGroup: inserting idx = " + idx + " name = " + mItems.get(idx));
                RadioButton button = (RadioButton) mInflater.inflate(R.layout.form_radiobutton, null, false);
                button.setId(idx);
                button.setText(mItems.get(idx));
                if (idx == mDefaultSelected){
                    button.setChecked(true);
                }
                RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                mGroup.addView(button, idx, layoutParams);
            }

        }

        if(mDefaultSelected != NO_DEFAULT_SELECTION){
            for(int i = 0; i < mGroup.getChildCount(); i++) {
                if(i == mDefaultSelected){
                    check(mGroup.getChildAt(i).getId());
                }
            }
        }

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
        final LinearLayout extras = (LinearLayout) container.findViewById(R.id.radio_extras_container);
        final RadioButton button = (RadioButton) container.findViewById(R.id.radio_shipping);

        if (addInnerForm) {
            Print.i(TAG, "code1subForms updateRadioGroup contains : " + mItems.get(idx));
            DynamicForm formGenerator = FormFactory.getSingleton().CreateForm(FormConstants.PAYMENT_DETAILS_FORM, mContext, formsMap.get(mItems.get(idx)));
            generatedForms.put(idx, formGenerator);
            extras.addView(formGenerator.getContainer());
            Print.d(TAG, "updateRadioGroup: inserting idx = " + idx + " name = " + mItems.get(idx));
        }

        // Hide first divider
        if (idx == 0) {
            container.findViewById(R.id.radio_divider).setVisibility(View.GONE);
        }

        RelativeLayout.LayoutParams mParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        container.setId(idx);
        container.setLayoutParams(mParams);
        
        if (paymentsInfoList != null && paymentsInfoList.size() > 0 && paymentsInfoList.containsKey(mItems.get(idx))) {
            TextView mTextView = (TextView) extras.findViewById(R.id.payment_text);
            String paymentText = paymentsInfoList.get(mItems.get(idx)).getText();
            if(TextUtils.isNotEmpty(paymentText)){
                mTextView.setText(paymentText);
                mTextView.setVisibility(View.VISIBLE);
            }
        }

        button.setText(mItems.get(idx));
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.checkout_shipping_item_height));
        layoutParams.setMargins(0, 0, getResources().getDimensionPixelSize(R.dimen.form_radiobutton_shipping_margin), 0);
        button.setText(mItems.get(idx));
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        // Set default
        button.setChecked(idx == mDefaultSelected);
        // Add radio option
        mGroup.addView(container);
    }

    public int getSelectedIndex() {
        int radioButtonID = mGroup.getCheckedRadioButtonId();
        View radioButton = mGroup.findViewById(radioButtonID);
        int idx = mGroup.indexOfChild(radioButton);
        Print.i(TAG, "code1validate radioButtonId : " + radioButtonID + " idx : " + idx);
        return idx;
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
                RadioButton button = (RadioButton) mGroup.getChildAt(idx).findViewById(R.id.radio_shipping);
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
                RadioButton button = (RadioButton) mGroup.getChildAt(idx).findViewById(R.id.radio_shipping);
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

}
