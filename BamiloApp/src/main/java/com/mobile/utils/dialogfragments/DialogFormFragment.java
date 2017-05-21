package com.mobile.utils.dialogfragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.service.forms.Form;
import com.mobile.pojo.DynamicForm;
import com.mobile.view.R;

/**
 * Class used to create a form dialog.
 *
 * @author sergiopereira
 */
public class DialogFormFragment extends DialogGenericFragment implements View.OnClickListener {

    private ViewGroup mFormContainer;
    private DynamicForm mDynamicForm;
    private Form mForm;
    private IDialogForm mSubmitCallback;

    /**
     * Interface
     */
    public interface IDialogForm {
        void onSubmitDialogForm(String action, ContentValues values);
    }

    /**
     * Empty constructor
     */
    public DialogFormFragment() {
        // ...
    }

    /**
     * Create a new instance
     */
    public static DialogFormFragment newInstance(@NonNull Form form, @NonNull IDialogForm submitCallback) {
        DialogFormFragment dialog = new DialogFormFragment();
        dialog.mForm = form;
        dialog.mSubmitCallback = submitCallback;
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_generic_form, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get form container
        mFormContainer = (ViewGroup) view.findViewById(R.id.form_container);
        // Get button container
        view.findViewById(R.id.button_cancel).setOnClickListener(this);
        view.findViewById(R.id.button_accept).setOnClickListener(this);
        // Get form
        showDynamicForm(mForm);
    }

    /**
     * Show form
     */
    private void showDynamicForm(Form form) {
        // Create dynamic form
        mDynamicForm = FormFactory.create(FormConstants.NEWSLETTER_UN_SUBSCRIBE_FORM, getContext(), form);
        // Show dynamic form
        mFormContainer.addView(mDynamicForm.getContainer());
    }

    /*
     * ####### LISTENERS #######
     */

    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Case button accept
        if (id == R.id.button_accept) {
            // Submit case valid form otherwise shows warnings
            if (mDynamicForm.validate()) {
                // Parent submit the form
                mSubmitCallback.onSubmitDialogForm(mDynamicForm.getForm().getAction(), mDynamicForm.save());
                // Dismiss
                dismissAllowingStateLoss();
            }
        }
        // Case button cancel
        else {
            dismissAllowingStateLoss();
        }
    }

}
