package com.mobile.factories;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.util.LayoutDirection;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mobile.constants.FormConstants;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.IFormField;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.view.R;

/**
 * A Singleton factory for the creation of dynamic forms based on information returned by the framework <p/><br>
 *
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 *
 * @author Nuno Castro
 * @version 1.1
 *
 * 2012/06/15
 *
 * @modified ricardosoares
 */
public class FormFactory {

    private final static String TAG = FormFactory.class.getSimpleName();

    private static FormFactory sFactory = null;

    @SuppressWarnings("unused") // Icons used for forms by reflection (LINT NOT REMOVE)
    int[] icons = new int[]{
            R.drawable.ic_form_birthday,
            R.drawable.ic_form_email,
            R.drawable.ic_form_first_name,
            R.drawable.ic_form_gender,
            R.drawable.ic_form_national_id,
            R.drawable.ic_form_password,
            R.drawable.ic_form_phone,
            R.drawable.ic_form_female,
            R.drawable.ic_form_male
    };

    /**
     * The constructor is private to prevent the creation of the object
     */
    private FormFactory() {
        // ...
    }

    /**
     * Gets the Singleton for the factory
     *
     * @return The form factory
     */
    public static FormFactory getSingleton() {
        return null == sFactory ? sFactory = new FormFactory() : sFactory;
    }

    /**
     * Creates the form for a given type using a definition provided from the framework
     *
     * @param formType
     *            The type of form to create. This value should be placed on the FormConstants
     * @param context
     *            The context where the form is to be inserted
     * @param form
     *            The definition provided by the framework
     *
     * @return An instance of a DynamicForm with the form representation implemented
     */
    public DynamicForm CreateForm(int formType, Context context, Form form) {
        DynamicForm parent = null;
        //Print.i(TAG, "code1register CREATING FORM : " + formType);
        switch (formType) {
            case FormConstants.LOGIN_FORM:
            case FormConstants.REGISTRATION_FORM:
            case FormConstants.FORGET_PASSWORD_FORM:
            case FormConstants.CHANGE_PASSWORD_FORM:
                form.hideAsterisks(); // Used to hide asterisks because everything is mandatory
            case FormConstants.USER_DATA_FORM:
            case FormConstants.ADDRESS_EDIT_FORM:
            case FormConstants.ADDRESS_FORM:
                form.setType(formType);  // Used to show icons (LOGIN|REGISTER|USER_DATA)
                parent = createGenericForm(context, form, createParams(context, R.dimen.form_top_margin));
                break;
            case FormConstants.PAYMENT_DETAILS_FORM:
                parent = createGenericForm(context, form, createParams(context, R.dimen.form_no_top_margin));
                break;
            case FormConstants.NEWSLETTER_FORM:
                form.hideAsterisks();// Used to hide asterisks because everything is mandatory
                parent = createNewsletterForm(context, form, createParams(context, R.dimen.form_no_top_margin));
                break;
        }
        return parent;
    }



    /**
     * This is used as base to create the given form. Here all the controls are instantiated.
     *
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * @return n instance of a DynamicForm with the form representation implemented
     */
    private DynamicForm createGenericForm(Context context, Form form, ViewGroup.LayoutParams ctrlParams) {
        // Validate
        if(context == null){
            return null;
        }
        // Root view group
        LinearLayout viewGroup = new LinearLayout(context);
        LinearLayout.LayoutParams frmParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        viewGroup.setOrientation(LinearLayout.VERTICAL);
        viewGroup.setLayoutParams(frmParams);
        // Create dynamic form
        DynamicForm dynamicForm = new DynamicForm(viewGroup).setForm(form);
        // Create each form field
        for (IFormField entry : form.getFields()) {
            Print.d(TAG, "FORM ITEM KEY: " + entry.getKey() + " TYPE: " + entry.getInputType());
            DynamicFormItem dynamicFormItem = new DynamicFormItem(dynamicForm, context, entry);
            dynamicForm.addControl(dynamicFormItem, ctrlParams);
        }
        return dynamicForm;
    }


    /**
     */
    private DynamicForm createNewsletterForm(Context context, Form form, ViewGroup.LayoutParams ctrlParams) {
        // Validate
        if(context == null){
            return null;
        }
        // Root view group
        LinearLayout viewGroup = new LinearLayout(context);
        LinearLayout.LayoutParams frmParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        viewGroup.setOrientation(LinearLayout.VERTICAL);
        viewGroup.setLayoutParams(frmParams);
        // Create dynamic form
        DynamicForm dynamicForm = new DynamicForm(viewGroup).setForm(form);

        // Create each form field
        for (IFormField frmEntry : form.getFields()) {
            Print.d(TAG, "createGenericForm: " + frmEntry.getKey() + " inputType = " + frmEntry.getInputType());
            DynamicFormItem dynamicFormItem = new DynamicFormItem(dynamicForm, context, frmEntry);
            dynamicForm.addControl(dynamicFormItem, ctrlParams);

        }
        return dynamicForm;
    }

    private LinearLayout.LayoutParams createParams(@NonNull Context context, @DimenRes int dimension) {
        LinearLayout.LayoutParams ctrlParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ctrlParams.setMargins(0, context.getResources().getDimensionPixelSize(dimension), 0, 0);
        setRtl(ctrlParams, 0, 0);
        return ctrlParams;
    }

    /**
     * Prepare params for RTL.
     */
    private void setRtl(LinearLayout.LayoutParams ctrlParams, final int CTRLMARGIN_LEFT, final int CTRLMARGIN_RIGHT){
        //#RTL
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
            ctrlParams.setLayoutDirection(LayoutDirection.LOCALE);
            ctrlParams.setMarginStart(CTRLMARGIN_LEFT);
            ctrlParams.setMarginEnd(CTRLMARGIN_RIGHT);
        }
    }

}