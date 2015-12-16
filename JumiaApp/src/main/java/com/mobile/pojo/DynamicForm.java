package com.mobile.pojo;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.mobile.components.absspinner.IcsAdapterView;
import com.mobile.components.absspinner.IcsSpinner;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormInputType;
import com.mobile.newFramework.forms.IFormField;
import com.mobile.newFramework.objects.addresses.FormListItem;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.utils.RadioGroupLayout;
import com.mobile.view.R;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This Class defines the representation of a dynamic form
 * <p/>
 * <br>
 * 
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Nuno Castro
 * @version 1.01
 * 
 *          2012/06/15
 * 
 */
public class DynamicForm implements Iterable<DynamicFormItem> {

    public final static String TAG = DynamicForm.class.getSimpleName();

    private final ViewGroup base;
    private final HashMap<String, DynamicFormItem> controls;
    private final IcsAdapterView.OnItemSelectedListener itemSelected_listener;
    private int lastID = 0x7f096000;
    private Form form;
    private WeakReference<View.OnClickListener> mClickListener;
    private WeakReference<IResponseCallback> mRequestCallBack;

    /**
     * The constructor for the DynamicForm
     * 
     * @param base
     *            where the form is to be implemented. This is a ViewGroup
     *            (Layout) that will hold the form
     */
    public DynamicForm(ViewGroup base) {
        this.base = base;
        this.controls = new LinkedHashMap<>();
        this.itemSelected_listener = null;
    }

    /**
     * Adds a control to the dynamic form
     * 
     * @param ctrl
     *            An instance of a DynamicFormItem to be added to the form
     * @param params
     *            A LayoutParams instance to be used when inserting the control
     *            into the form
     */
    public void addControl(DynamicFormItem ctrl, ViewGroup.LayoutParams params) {
        View controlView = ctrl.getControl();
        if (null != controlView) {
            ctrl.setOnItemSelectedListener(itemSelected_listener);
            controls.put(ctrl.getKey(), ctrl);
            base.addView(ctrl.getControl(), params);
        }
    }

    /**
     * Adds a control to the dynamic form
     * 
     * @param ctrl
     *            An instance of a DynamicFormItem to be added to the form
     * @param params
     *            A LayoutParams instance to be used when inserting the control
     *            into the form
     */
    public void addGroupedControl(ViewGroup group, DynamicFormItem ctrl, ViewGroup.LayoutParams params) {
        View controlView = ctrl.getControl();
        if (null != controlView && null != group) {
            ctrl.setOnItemSelectedListener(itemSelected_listener);
            controls.put(ctrl.getKey(), ctrl);

            group.addView(controlView);
            if (null == base.findViewById(group.getId())) {
                base.addView(group, params);
            }
        }
    }

    /**
     * Gets one of the form controls with a specific index
     * 
     * @param index
     *            The index of the item to return
     * @return The instance of the DynamicFormItem that has the given key
     */
    public DynamicFormItem getItem(int index) {
        return controls.get(index);
    }

    @Nullable
    public DynamicFormItem getItemByKey(String key) {
        return controls.get(key);
    }

    /**
     * Gets the dynamic form container
     * 
     * @return The view that contains the form
     */
    public ViewGroup getContainer() {
        return base;
    }

    /**
     * Gets the next available id for the creation of controls
     * 
     * @return The next ID
     */
    public int getNextId() {
        return ++lastID;
    }

    /**
     * Get the form object that is return from the framework containing all the
     * information of the API
     * 
     * @return the form object
     */
    public Form getForm() {
        return form;
    }

    /**
     * Sets the local version of the framework form object
     * 
     * @param value
     *            the form object
     */
    public DynamicForm setForm(Form value) {
        form = value;
        return this;
    }

    /**
     * Validates the information on the form by querying all the controls and
     * calling their validation routines
     * 
     * @return If the form contains validates correctly (true: no errors; false:
     *         has errors)
     */
    public boolean validate() {
        boolean result = true;
        for (DynamicFormItem dynamicFormItem : this) {
            result &= dynamicFormItem.validate();
        }
        return result;
    }

    /**
     * Fills a hashmap with the values from the form.
     * Only used to send the info of the form to the server.
     * @return A hashmap containing the values from the form
     */
    public ContentValues save() {
        ContentValues model = new ContentValues();
        for (DynamicFormItem control : this) {
            // Case metadata
            if (control != null && control.getType() == FormInputType.metadata) {
                control.addSubFormFieldValues(model);
                model.put(control.getName(), control.getValue());
            }
            // Case radio group vertical
            else if (null != control && control.getType() == FormInputType.radioGroup && control.isRadioGroupLayoutVertical()) {
                ContentValues mValues = control.getSubFormsValues();
                if (mValues != null) {
                    model.putAll(mValues);
                }
                model.put("name", control.getRadioGroupLayoutVerticalSelectedFieldName());
                model.put(control.getName(), control.getValue());
            }
            // Case list from api call
            else if (control != null && control.getType() == FormInputType.list ) {
                saveListSelection(control, model);
            }
            // Case related number
            else if (control != null && control.getType() == FormInputType.relatedNumber ) {
                // Get number
                String number = control.getValue();
                if (TextUtils.isNotEmpty(number)) {
                    model.put(control.getName(), number);
                    // Get related option
                    IFormField related = control.getEntry().getRelatedField();
                    // Validate related type
                    FormInputType relatedType = related.getInputType();
                    // Only send the related info if the main is filled

                    if(relatedType == FormInputType.radioGroup) {
                        RadioGroupLayout group = (RadioGroupLayout) control.getControl().findViewWithTag(DynamicFormItem.RELATED_RADIO_GROUP_TAG);
                        // Get selected position
                        int idx = group.getSelectedIndex();
                        // Get option value from related item
                        String value = related.getOptions().get(idx).getValue();
                        model.put(related.getName(), value);
                    }
                    else if (relatedType == FormInputType.list) {
                        IcsSpinner spinner = (IcsSpinner) control.getControl().findViewWithTag(DynamicFormItem.RELATED_LIST_GROUP_TAG);
                        FormListItem item = (FormListItem) spinner.getSelectedItem();
                        if (item != null) {
                            model.put(related.getName(), item.getValue());
                        }
                    }
                }

            }
            // Case ratings
            else if (null != control && control.getType() == FormInputType.rating) {
                RatingBar bar = (RatingBar) control.getControl().findViewWithTag(DynamicFormItem.RATING_BAR_TAG);
                Map<String, String> ratingMap = control.getEntry().getDateSetRating();
                if(CollectionUtils.isNotEmpty(ratingMap)){
                    for (int i = 0; i < ratingMap.size(); i++){
                        model.put(bar.getTag(R.id.rating_bar_id).toString(), (int) bar.getRating());
                    }
                }
            }
            // Case default
            else if (null != control && null != control.getValue()) {
                model.put(control.getName(), control.getValue());
            }
        }

        return model;
    }

    /**
     * Save value from form list item. (Regions and Cities)
     */
    private void saveListSelection(DynamicFormItem control, ContentValues model) {
        ViewGroup mRegionGroup = (ViewGroup) control.getControl();
        IcsSpinner spinner = (IcsSpinner) mRegionGroup.getChildAt(0);
        FormListItem mSelectedRegion = (FormListItem) spinner.getSelectedItem();
        if(mSelectedRegion != null)
            model.put(control.getName(), mSelectedRegion.getValue());
    }

    public int getSelectedValueIndex() {
        for (DynamicFormItem control : this) {
            if (null != control && control.getType() == FormInputType.radioGroup && control.isRadioGroupLayoutVertical()) {
                return control.getSubFormsSelectedIndex();
            }
        }
        return -1;
    }

    /**
     * Resets all the fields on the form to their original values.
     */
    @SuppressWarnings("unused")
    public void reset() {
        for (DynamicFormItem dynamicFormItem : this) {
            dynamicFormItem.resetValue();
        }
    }

    /**
     * Clears all the data on each control on the form.
     */
    public void clear() {
        for (DynamicFormItem dynamicFormItem : this) {
            dynamicFormItem.setValue(null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<DynamicFormItem> iterator() {
        return controls.values().iterator();
    }

    /**
     * Save the form state
     */
    public void saveFormState(@NonNull Bundle outState) {
        for (DynamicFormItem item : this) {
            item.saveState(outState);
        }
    }

    /**
     * Load the saved form state.
     */
    public void loadSaveFormState(@Nullable Bundle mFormSavedState) {
        if (mFormSavedState != null) {
            for (DynamicFormItem item : this) {
                item.loadState(mFormSavedState);
            }
        }
    }

    /**
     * This method checks if both passwords inserted match
     *
     * @return true if yes false if not
     */
    public boolean checkPasswords() {
        boolean result = true;
        String old = "";
        for (DynamicFormItem item : this) {
            // Case password
            if (item.getType() == FormInputType.password) {
                // Save first
                if (TextUtils.isEmpty(old)) {
                    old = item.getValue();
                }
                // Check first vs second
                else {
                    result = old.equals(item.getValue());
                    if (!result) {
                        item.showErrorMessage(base.getContext().getString(R.string.form_passwordsnomatch));
                    }
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Show all validate messages.
     */
    public void showValidateMessages(@Nullable Map map) {
        if (CollectionUtils.isNotEmpty(map)) {
            for (Object key : map.keySet()) {
                DynamicFormItem item = controls.get(key.toString());
                if(item != null) item.showErrorMessage(map.get(key).toString());
            }
        }
    }

    /**
     * Sets a click listener to a specific form type or to all case null.
     */
    public void setOnClickListener(View.OnClickListener listener) {
        mClickListener = new WeakReference<>(listener);
    }

    public void setInitialValue(@NonNull FormInputType formType, @NonNull Object value) {
        for (DynamicFormItem dynamicFormItem : this) {
            if (dynamicFormItem.getType() == formType) {
                dynamicFormItem.setValue(value);
            }
        }
    }

    public boolean hasClickListener() {
        return  mClickListener != null && mClickListener.get() != null;
    }

    public boolean hasResponseCallback() {
        return  mRequestCallBack != null && mRequestCallBack.get() != null;
    }

    public WeakReference<View.OnClickListener> getClickListener() {
        return mClickListener;
    }

    public WeakReference<IResponseCallback> getRequestCallBack() {
        return mRequestCallBack;
    }

    public void setRequestCallBack(IResponseCallback requestCallBack) {
        mRequestCallBack = new WeakReference<>(requestCallBack);
    }
}
