package com.mobile.pojo;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormInputType;
import com.mobile.newFramework.forms.IFormField;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;
import com.mobile.view.fragments.BaseFragment;

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
    private int lastID = 0x7f096000;
    private final Form form;
    private WeakReference<View.OnClickListener> mClickListener;
    private WeakReference<IResponseCallback> mRequestCallBack;
    private WeakReference<BaseActivity> mFragmentActivity;
    private WeakReference<CompoundButton.OnCheckedChangeListener> mCheckedChangeListener;
    private WeakReference<BaseFragment> mParentFragment;

    /**
     * The constructor for the DynamicForm.<br>
     * This is a ViewGroup that will hold the form.
     */
    public DynamicForm(@NonNull Context context, @NonNull Form form) {
        this.controls = new LinkedHashMap<>();
        this.base = buildBase(context);
        this.form = form;
    }

    /**
     * Create the view group.
     */
    @NonNull
    private ViewGroup buildBase(@NonNull Context context) {
        LinearLayoutCompat base = new LinearLayoutCompat(context);
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        base.setOrientation(LinearLayoutCompat.VERTICAL);
        base.setLayoutParams(params);
        return base;
    }

    /**
     * Show vertical dividers.
     */
    @NonNull
    public DynamicForm showDividers(@LinearLayoutCompat.DividerMode int showDividers) {
        ((LinearLayoutCompat) base).setDividerDrawable(ContextCompat.getDrawable(base.getContext(), R.drawable._gen_divider_horizontal_black_300));
        ((LinearLayoutCompat) base).setShowDividers(showDividers);
        return this;
    }

    /**
     * Add a margin top.
     */
    @NonNull
    public DynamicForm addMarginTop(@DimenRes int dimension) {
        int marginTop = base.getContext().getResources().getDimensionPixelSize(dimension);
        LinearLayoutCompat.LayoutParams params = (LinearLayoutCompat.LayoutParams) base.getLayoutParams();
        params.setMargins(params.leftMargin, marginTop, params.rightMargin, params.bottomMargin);
        base.requestLayout();
        return this;
    }

    /**
     * Build the form with each form item.
     */
    @NonNull
    public DynamicForm build() {
        for (IFormField entry : form.getFields()) {
            entry.setFormType(form.getType());
            Print.i(TAG, "FORM ITEM KEY:" + entry.getKey() + " TYPE:" + entry.getInputType());
            this.addControl(DynamicFormItem.newInstance(this, base.getContext(), entry));
        }
        return this;
    }

    /**
     * Adds a control to the dynamic form
     * 
     * @param ctrl
     *            An instance of a DynamicFormItem to be added to the form
     */
    private void addControl(DynamicFormItem ctrl) {
        View controlView = ctrl.getControl();
        if (null != controlView) {
            controls.put(ctrl.getKey(), ctrl);
            base.addView(ctrl.getControl(), base.getLayoutParams());
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

    public void setInitialValue(@NonNull FormInputType formType, @NonNull Object value) {
        for (DynamicFormItem dynamicFormItem : this) {
            if (dynamicFormItem.getType() == formType) {
                dynamicFormItem.setValue(value);
            }
        }
    }

    /*
     * ########## BASE ACTIVITY ##########
     */

    public DynamicForm addParentActivity(BaseActivity activity) {
        mFragmentActivity = new WeakReference<>(activity);
        return this;
    }

    public BaseActivity getParentActivity() {
        return mFragmentActivity.get();
    }

    public boolean hasParentActivity() {
        return  mFragmentActivity != null && mFragmentActivity.get() != null;
    }

    /*
     * ########## BASE FRAGMENT ##########
     */

    public DynamicForm addParentFragment(BaseFragment fragment) {
        mParentFragment = new WeakReference<>(fragment);
        return this;
    }

    public boolean isFragmentUIActive() {
        return  mParentFragment != null && mParentFragment.get() != null && mParentFragment.get().isFragmentUIActive();
    }

    public void triggerContentEvent(final SuperBaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        if(isFragmentUIActive()) {
            JumiaApplication.INSTANCE.sendRequest(helper, args, responseCallback);
        }
    }

    public void showActivityProgress() {
        if(isFragmentUIActive() && mParentFragment.get().getBaseActivity() != null) {
            mParentFragment.get().getBaseActivity().showProgress();
        }
    }

    public void hideActivityProgress() {
        if(isFragmentUIActive() && mParentFragment.get().getBaseActivity() != null) {
            mParentFragment.get().getBaseActivity().dismissProgress();
        }
    }

    /*
     * ########## CLICK LISTENER ##########
     */

    public DynamicForm addOnClickListener(View.OnClickListener listener) {
        mClickListener = new WeakReference<>(listener);
        return this;
    }

    public void onClick(View view) {
        if (mClickListener != null && mClickListener.get() != null) {
            mClickListener.get().onClick(view);
        }
    }

    /*
     * ########## CHECKED CHANGE LISTENER ##########
     */

    public DynamicForm addCheckedChangeListener(@NonNull CompoundButton.OnCheckedChangeListener listener) {
        mCheckedChangeListener = new WeakReference<>(listener);
        return this;
    }

    public void onCheckedChangeListener(CompoundButton buttonView, boolean isChecked) {
        if (mCheckedChangeListener != null && mCheckedChangeListener.get() != null) {
            mCheckedChangeListener.get().onCheckedChanged(buttonView, isChecked);
        }
    }

    /*
     * ########## RESPONSE CALLBACK ##########
     */

    public DynamicForm addRequestCallBack(IResponseCallback requestCallBack) {
        mRequestCallBack = new WeakReference<>(requestCallBack);
        return this;
    }

    public void onRequestComplete(BaseResponse baseResponse) {
        if(mRequestCallBack != null && mRequestCallBack.get() != null) {
            mRequestCallBack.get().onRequestComplete(baseResponse);
        }
    }

    public void onRequestError(BaseResponse baseResponse) {
        if(mRequestCallBack != null && mRequestCallBack.get() != null) {
            mRequestCallBack.get().onRequestError(baseResponse);
        }
    }

    /*
     * ########## NEWSLETTER FORM ##########
     */

    /**
     * Show all views performing the click or not
     */
    public void showAll(boolean performClick) {
        for (DynamicFormItem item : this) {
            item.getControl().setVisibility(View.VISIBLE);
            if (item instanceof IDynamicFormItemField && performClick) {
                ((IDynamicFormItemField) item).select();
            }
        }
    }

    /**
     * Hide all views
     */
    public void hideAll() {
        for (DynamicFormItem item : this) {
            item.getControl().setVisibility(View.GONE);
        }
    }

}
