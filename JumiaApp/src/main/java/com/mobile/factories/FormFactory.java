package com.mobile.factories;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mobile.constants.FormConstants;
import com.mobile.newFramework.forms.Form;
import com.mobile.pojo.DynamicForm;
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
 * @modified spereira
 */
public class FormFactory {

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
    public DynamicForm create(@FormConstants.DynamicFormTypes int formType, @NonNull Context context, @NonNull Form form) {
        DynamicForm parent = null;
        switch (formType) {
            case FormConstants.LOGIN_FORM:
            case FormConstants.REGISTRATION_FORM:
            case FormConstants.FORGET_PASSWORD_FORM:
            case FormConstants.CHANGE_PASSWORD_FORM:
            case FormConstants.RATING_FORM:
                form.hideAsterisks(); // Used to hide asterisks because everything is mandatory
            case FormConstants.USER_DATA_FORM:
            case FormConstants.ADDRESS_EDIT_FORM:
            case FormConstants.ADDRESS_FORM:
                form.setType(formType);  // Used to show icons (LOGIN|REGISTER|USER_DATA)
                parent = new DynamicForm(context, form).addMarginTop(R.dimen.form_top_margin).build();
                break;
            case FormConstants.NEWSLETTER_UN_SUBSCRIBE_FORM:
            case FormConstants.NEWSLETTER_PREFERENCES_FORM:
                form.setType(formType);  // Used for dividers
                parent = new DynamicForm(context, form).addMiddleDivider().addEndDivider().build();
                break;
            case FormConstants.PAYMENT_DETAILS_FORM:
                parent = new DynamicForm(context, form).build();
                break;
            case FormConstants.ORDER_RETURN_REASON_FORM:
            case FormConstants.NEWSLETTER_FORM:
                form.setType(formType);
                form.hideAsterisks();// Used to hide asterisks because everything is mandatory
                parent = new DynamicForm(context, form).build();
                break;
        }
        return parent;
    }


}