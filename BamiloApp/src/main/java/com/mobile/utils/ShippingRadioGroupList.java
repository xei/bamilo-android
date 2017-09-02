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

import com.mobile.controllers.PickupStationsAdapter;
import com.mobile.forms.ShippingMethod;
import com.mobile.forms.ShippingMethodForm;
import com.mobile.forms.ShippingMethodSubForm;
import com.mobile.service.forms.PickUpStationObject;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ShippingRadioGroupList extends RadioGroup {

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

        // Get size
        int numberItems = mItems.size();
        for (int idx = 0; idx < numberItems; idx++) {
            // Get main container
            final LinearLayout container = (LinearLayout) mInflater.inflate(R.layout.form_radiobutton_with_extra, this, false);
            // Get info container
            final LinearLayout extras = (LinearLayout) container.findViewById(R.id.radio_extras_container);

            // For each element verify if it has extras if so add them to the view
            ArrayList<ShippingMethodSubForm> tmpSubForms = new ArrayList<>();
            for (int i = 0; i < mForm.shippingMethodsSubForms.size(); i++) {
                // Get sub form
                ShippingMethodSubForm shippingSubForm = new ShippingMethodSubForm();
                shippingSubForm.shippingMethodSubFormHolder = mForm.shippingMethodsSubForms.get(i);
                if (shippingSubForm.shippingMethodSubFormHolder.scenario.equalsIgnoreCase(mItems.get(idx))) {
                    tmpSubForms.add(shippingSubForm);
                    // Create shipping PUS option
                    if(CollectionUtils.isNotEmpty(shippingSubForm.shippingMethodSubFormHolder.options)){
                        shippingSubForm.generateForm(extras);
                    }
                    // Create shipping PUS dummy option
                    else{
                        shippingSubForm.dataControl = new View(getContext());
                    }
                }
            }
            // Case shipping PUS option
            if (CollectionUtils.isNotEmpty(tmpSubForms)) {
                subForms.put(mItems.get(idx), tmpSubForms);
            }
            // Case shipping regular option
            else {
                ShippingMethod shippingMethod = new ShippingMethod();
                shippingMethod.shippingMethodHolder = mForm.optionsShippingMethod.get(mItems.get(idx));
                shippingMethod.generateForm(extras);
            }

            // Get divider
            container.findViewById(R.id.radio_divider).setVisibility(idx == 0 ? View.GONE : View.VISIBLE);
            // Get radio button
            RadioButton button = (RadioButton) container.findViewById(R.id.radio_shipping);
            // Get label
            String label1 = mForm.optionsShippingMethod.get(mItems.get(idx)).label;
            String label2 = mItems.get(idx);
            // Set id used for click selection
            container.setId(idx);
            // Set text
            button.setText(!TextUtils.isEmpty(label1) ? label1 : label2);
            // Set click
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickCheckBox((RadioButton) view, extras, container);
                }
            });
            // Add the shipping view
            this.addView(container);

            // Put the selected radio button from server or default 0
            if (mItems.get(idx).equalsIgnoreCase(mDefaultSelected) || idx == 0) {
                // Save selected position
                mDefaultSelectedId = idx;
                // Set as checked
                button.setChecked(true);
                // Process the click to show the extras if exist
                onClickCheckBox(button, extras, container);
            }
        }
    }
 
    /**
     * Process the click on radio button
     */
    private void onClickCheckBox(RadioButton button, View extras, View container) {
        if (button.isChecked()) {
            extras.setVisibility(View.VISIBLE);
            if (subForms.get(mItems.get(container.getId())) != null) {
                if (mItems.get(container.getId()).equalsIgnoreCase("pickupstation")) {
                    for (ShippingMethodSubForm element : subForms.get(mItems.get(container.getId()))) {
                        element.dataControl.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            extras.setVisibility(View.GONE);
            if (subForms.get(mItems.get(container.getId())) != null) {
                if (mItems.get(container.getId()).equalsIgnoreCase("pickupstation")) {
                    for (ShippingMethodSubForm element : subForms.get(mItems.get(container.getId()))) {
                        element.dataControl.setVisibility(View.GONE);
                    }
                }
            }
        }
        setSelection(container.getId());
        this.check(container.getId());
    }

    public int getSelectedIndex() {
        int radioButtonID = this.getCheckedRadioButtonId();
        View radioButton = this.findViewById(radioButtonID);
        int idx = this.indexOfChild(radioButton);
        //Print.i(TAG, "code1validate radioButtonId : " + radioButtonID + " idx : " + idx);
        return idx;
    }

    private void setSelection(final int idx) {
        if (idx >= 0) {
            //Print.i(TAG, "code1selection : id is : " + idx);
            View view = this.getChildAt(idx).findViewById(R.id.radio_shipping);
            if (view instanceof RadioButton) {
                ((RadioButton) view).setChecked(true);
                //Print.i(TAG, "code1selection : id is : " + idx + " second");
            }
            cleanOtherSelections(idx);
        }
    }

    private void cleanOtherSelections(int idx) {
        //Print.i(TAG, "code1selection : id is : " + idx + " cleaning");
        for (int i = 0; i < this.getChildCount(); i++) {
            if (i != idx) {
                View view = this.getChildAt(i).findViewById(R.id.radio_shipping);
                if (view instanceof RadioButton) {
                    ((RadioButton) view).setChecked(false);
                    this.getChildAt(i).findViewById(R.id.radio_extras_container).setVisibility(View.GONE);
                    //Print.i(TAG, "code1selection : id is : " + idx + " cleaning 2 : " + i);
                }
            }
        }
    }

    /**
     * Set the selected radio button.
     * @param selection - The radio button position
     * @param subSelection - The PUS selection
     */
    public void setSelection(int selection, int subSelection) throws NullPointerException, IndexOutOfBoundsException {
        // Validate parent position
        if(selection != IntConstants.INVALID_POSITION) {
            // Get radio button
            View view = this.getChildAt(selection).findViewById(R.id.radio_shipping);
            if (view instanceof RadioButton) {
                view.performClick();
                // Validate sub position
                if (subSelection != IntConstants.INVALID_POSITION) {
                    setSubSelection(selection, subSelection);
                }
            }
        }
    }

    private void setSubSelection(int groupId, int subId) throws NullPointerException, IndexOutOfBoundsException {
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

    public int getSelectedPUS(int pos, int posSub) {
        return subForms.get(mItems.get(pos)).get(posSub).getSelectedPUS();
    }

    public void setSelectedPUS(int pos, int posSub, int selected) {
        subForms.get(mItems.get(pos)).get(posSub).setSelectedPUS(selected);
    }

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
        ContentValues mContentValues = new ContentValues();
        int idx = this.getCheckedRadioButtonId();
        if (idx < 0) {
            idx = mDefaultSelectedId;
        }
        if (subForms.containsKey(mItems.get(idx)) && subForms.get(mItems.get(idx)).size() > 0) {
            PickUpStationObject selectedPickup = null;
            //Print.i(TAG, "code1values : adding ");
            for (ShippingMethodSubForm element : subForms.get(mItems.get(idx))) {
                if (element.shippingMethodSubFormHolder.options != null && element.shippingMethodSubFormHolder.options.size() > 0) {
                    if (element.pickupStationsListView != null && element.pickupStationsListView.getAdapter() instanceof PickupStationsAdapter) {
                        selectedPickup = ((PickupStationsAdapter) element.pickupStationsListView.getAdapter()).getSelectedPickupStation();
                    }
                    if (selectedPickup != null) {
                        mContentValues.put(element.shippingMethodSubFormHolder.name, selectedPickup.getPickupStationId());
                    }
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
