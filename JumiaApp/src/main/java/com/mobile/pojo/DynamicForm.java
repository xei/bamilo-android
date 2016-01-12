package com.mobile.pojo;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.components.absspinner.IcsAdapterView;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormInputType;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.TextUtils;
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
     * Fills a ContentValues with the values from the form.
     * Only used to submit the form.
     *
     * @return A hashmap containing the values from the form
     */
    public @NonNull ContentValues save() {
        ContentValues model = new ContentValues();
        for (DynamicFormItem control : this) {
            control.save(model);
        }
        return model;
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
