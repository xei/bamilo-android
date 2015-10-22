package com.mobile.utils;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.mobile.controllers.PickupStationsAdapter;
import com.mobile.forms.ShippingMethod;
import com.mobile.forms.ShippingMethodForm;
import com.mobile.forms.ShippingMethodSubForm;
import com.mobile.newFramework.forms.PickUpStationObject;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ShippingRadioGroupList extends RadioGroup {

    private final static String TAG = ShippingRadioGroupList.class.getSimpleName();

    private ArrayList<String> mItems;
    private ShippingMethodForm mForm;
    private HashMap<String, ArrayList<ShippingMethodSubForm>> subForms;
    private String mDefaultSelected;
    private int mDefaultSelectedId;
    private LayoutInflater mInflater;

    public ShippingRadioGroupList(Context context) {
        super(context);
        init();
    }

    public ShippingRadioGroupList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
    }

    public void setItems(ShippingMethodForm form, String defaultSelected) {
        Print.d(TAG, "setItems: items size = " + form.key + " defaultSelected = " + defaultSelected);
        mForm = form;
        mItems = mForm.options;
        mDefaultSelected = defaultSelected;
        subForms = new HashMap<>();
        updateRadioGroup();
    }

    private void updateRadioGroup() {

        try {
            this.removeAllViews();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        int numberItems = mItems.size();
        for (int idx = 0; idx < numberItems; idx++) {

            Print.d(TAG, "updateRadioGroup: inserting idx = " + idx + " name = " + mItems.get(idx));

            /**
             * Global Container
             */
            final LinearLayout mLinearLayout = (LinearLayout) mInflater.inflate(R.layout.form_radiobutton_with_extra, this, false);

            // Hide first divider
            if (idx == 0) {
                mLinearLayout.findViewById(R.id.radio_divider).setVisibility(View.GONE);
            }

            /**
             * Button Container
             */
            final LinearLayout buttonContainer = (LinearLayout) mLinearLayout.findViewById(R.id.radio_container);

            /**
             * Extras Container
             */
            final LinearLayout extras = (LinearLayout) mLinearLayout.findViewById(R.id.extras);
            
            RelativeLayout.LayoutParams mParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            ArrayList<ShippingMethodSubForm> tmpSubForms = new ArrayList<>();

            /**
             * For each element verify if it has extras if so add them to the view
             */
            for (int i = 0; i < mForm.shippingMethodsSubForms.size(); i++) {
                // Get sub form
                ShippingMethodSubForm shippingSubForm = new ShippingMethodSubForm();
                shippingSubForm.shippingMethodSubFormHolder = mForm.shippingMethodsSubForms.get(i);

                Print.i(TAG, "code1generate subForms : " + shippingSubForm.shippingMethodSubFormHolder.scenario);

                if (shippingSubForm.shippingMethodSubFormHolder.scenario.equalsIgnoreCase(mItems.get(idx))) {
                    Print.i(TAG, "code1generate subForms : " + shippingSubForm.shippingMethodSubFormHolder.name);


                    tmpSubForms.add(shippingSubForm);

                    // Validate number of options
                    if(shippingSubForm.shippingMethodSubFormHolder.options.size() > 0){
                        shippingSubForm.generateForm(getContext(), extras);
                    } else{
                        shippingSubForm.dataControl = new View(getContext());
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(tmpSubForms)) {
                subForms.put(mItems.get(idx), tmpSubForms);
            } else {
                ShippingMethod shippingMethod = new ShippingMethod();
                shippingMethod.shippingMethodHolder = mForm.optionsShippingMethod.get(mItems.get(idx));
                View view = shippingMethod.generateForm(getContext());
                if(view != null){
                    extras.addView(view);
                }
            }

            mLinearLayout.setId(idx);
            mLinearLayout.setLayoutParams(mParams);

            final RadioButton button = (RadioButton) mInflater.inflate(R.layout.form_radiobutton_shipping, buttonContainer, false);
            button.setId(idx);
            ShippingMethod optionLabel = new ShippingMethod();
            optionLabel.shippingMethodHolder = mForm.optionsShippingMethod.get(mItems.get(idx));
            //Log.i(TAG, "options jsonobject label: " + optionLabel);
            button.setText(!TextUtils.isEmpty(optionLabel.getLabel()) ? optionLabel.getLabel() : mItems.get(idx));
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.checkout_shipping_item_height));
            
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickCheckBox((RadioButton) v, extras, mLinearLayout);
                }
            });

            buttonContainer.addView(button, layoutParams);

            this.addView(mLinearLayout);
            
            // Put the selected radio button from server or default 0
            if (mItems.get(idx).equalsIgnoreCase(mDefaultSelected) || idx == 0) {
                // Save selected position
                mDefaultSelectedId = idx;
                // Set as checked
                button.setChecked(true);
                // Process the click to show the extras if exist
                onClickCheckBox(button, extras, mLinearLayout);
            }
            
        }

    }
 
    /**
     * Process the click on radio button
     */
    private void onClickCheckBox(RadioButton button, View extras, View mLinearLayout) {
        if (button.isChecked()) {
            extras.setVisibility(View.VISIBLE);
            if (subForms.get(mItems.get(button.getId())) != null) {
                if (mItems.get(button.getId()).equalsIgnoreCase("pickupstation")) {
                    for (ShippingMethodSubForm element : subForms.get(mItems.get(button.getId()))) {
                        element.dataControl.setVisibility(View.VISIBLE);
                    }
                }
                    
            }
        } else {
            extras.setVisibility(View.GONE);
            if (subForms.get(mItems.get(button.getId())) != null) {
                if (mItems.get(button.getId()).equalsIgnoreCase("pickupstation")) {
                    for (ShippingMethodSubForm element : subForms.get(mItems.get(button.getId()))) {
                        element.dataControl.setVisibility(View.GONE);
                    }
                }
                
            }
        }
        setSelection(mLinearLayout.getId());
        this.check(mLinearLayout.getId());
    }


    public int getSelectedIndex() {
        int radioButtonID = this.getCheckedRadioButtonId();
        View radioButton = this.findViewById(radioButtonID);
        int idx = this.indexOfChild(radioButton);
        Print.i(TAG, "code1validate radioButtonId : " + radioButtonID + " idx : " + idx);
        return idx;
    }

//    public String getItemByIndex(int idx) {
//        if (mItems == null)
//            return null;
//        if (idx < 0)
//            return null;
//        return mItems.get(idx);
//    }

    public void setSelection(int idx) {
        if (idx >= 0) {
            Print.i(TAG, "code1selection : id is : " + idx);
            if (this.getChildAt(idx).findViewById(R.id.radio_container).findViewById(idx) instanceof RadioButton) {
                RadioButton button = (RadioButton) this.getChildAt(idx).findViewById(R.id.radio_container).findViewById(idx);
                button.setChecked(true);
                Print.i(TAG, "code1selection : id is : " + idx + " second");
            }
            cleanOtherSelections(idx);
        }
    }
    
    
    public void setSubSelection(int groupId, int subId) throws NullPointerException, IndexOutOfBoundsException {
        if (subForms.containsKey(mItems.get(groupId)) && subForms.get(mItems.get(groupId)).size() > 0) {
            for (ShippingMethodSubForm element : subForms.get(mItems.get(groupId))) {
                if (element.shippingMethodSubFormHolder.options != null && element.shippingMethodSubFormHolder.options.size() > 0) {
                    element.icsSpinner.setSelection(subId);
                }
            }
        }
    }
    
    public int getSubSelection(int groupId) throws NullPointerException, IndexOutOfBoundsException {
        if (subForms.containsKey(mItems.get(groupId)) && subForms.get(mItems.get(groupId)).size() > 0) {
            for (ShippingMethodSubForm element : subForms.get(mItems.get(groupId))) {
                if (element.shippingMethodSubFormHolder.options != null && element.shippingMethodSubFormHolder.options.size() > 0) {
                    return element.icsSpinner.getSelectedItemPosition();
                }
            }
        }
        return -1;
    }

    private void cleanOtherSelections(int idx) {
        Print.i(TAG, "code1selection : id is : " + idx + " cleaning");
        for (int i = 0; i < this.getChildCount(); i++) {
            if (i != idx) {
                if (this.getChildAt(i).findViewById(R.id.radio_container).findViewById(i) instanceof RadioButton) {
                    RadioButton button = (RadioButton) this.getChildAt(i).findViewById(R.id.radio_container).findViewById(i);
                    button.setChecked(false);
                    this.getChildAt(i).findViewById(R.id.extras).setVisibility(View.GONE);
                    Print.i(TAG, "code1selection : id is : " + idx + " cleaning 2 : " + i);
                }
            }
        }
    }

//    public boolean validateSelected() {
//        boolean result = false;
//        this.getCheckedRadioButtonId();
//
//        return result;
//    }

    // public String getErrorMessage(){
    // String result = mContext.getString(R.string.register_required_text);
    //
    // result = ((DynamicFormItem)
    // generatedForms.get(this.getCheckedRadioButtonId()).getItem(0)).getMessage();
    //
    // return result;
    // }
    //
    // public ContentValues getSubFieldParameters(){
    // ContentValues result = null;
    // if(generatedForms != null && generatedForms.get(this.getCheckedRadioButtonId()) != null){
    // result = generatedForms.get(this.getCheckedRadioButtonId()).save();
    // }
    //
    //
    // return result;
    // }
    //
    public String getSelectedFieldName() {
        String result;
        if (this.getCheckedRadioButtonId() >= 0) {
            result = mItems.get(this.getCheckedRadioButtonId());
        } else {
            result = mItems.get(mDefaultSelectedId);
        }
        return result;
    }

    public ContentValues getValues() {
        Print.i(TAG, "code1values : adding valeus " + subForms.toString());
        ContentValues mContentValues = new ContentValues();
        int idx = this.getCheckedRadioButtonId();
        if (idx < 0) {
            idx = mDefaultSelectedId;
        }

        if (subForms.containsKey(mItems.get(idx)) && subForms.get(mItems.get(idx)).size() > 0) {
            PickUpStationObject selectedPickup = null;
            Print.i(TAG, "code1values : adding ");
            for (ShippingMethodSubForm element : subForms.get(mItems.get(idx))) {
                if (element.shippingMethodSubFormHolder.options != null && element.shippingMethodSubFormHolder.options.size() > 0) {
                    if(element.pickupStationsListView.getAdapter() instanceof PickupStationsAdapter){
                        selectedPickup = ((PickupStationsAdapter)element.pickupStationsListView.getAdapter()).getSelectedPickupStation();
                        
                    }
                    if(selectedPickup != null){
                        mContentValues.put(element.shippingMethodSubFormHolder.name, selectedPickup.getPickupStationId());
                    }
                    Print.i(TAG, "code1values : element.name : " + element.shippingMethodSubFormHolder.name);
                } else {
                    if (selectedPickup != null && selectedPickup.getRegions() != null && selectedPickup.getRegions().size() > 0) {
                        mContentValues.put(element.shippingMethodSubFormHolder.name, selectedPickup.getRegions().get(0).getId());
                    }
                }
            }
        }
        return mContentValues;

    }

}
