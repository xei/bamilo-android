package com.mobile.newFramework.forms;

/**
 * Class used to represent the new generic item from data set form field
 */
public class FormFieldOption extends FormField {

    public static final String TAG = FormFieldOption.class.getSimpleName();

    /**
     * Constructor
     */
    public FormFieldOption(Form form) {
        super(form);
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
