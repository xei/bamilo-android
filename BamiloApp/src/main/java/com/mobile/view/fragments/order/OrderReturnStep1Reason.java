package com.mobile.view.fragments.order;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.components.absspinner.IcsSpinner;
import com.mobile.constants.FormConstants;
import com.mobile.helpers.order.GetReturnReasonFormHelper;
import com.mobile.service.forms.Form;
import com.mobile.service.forms.ReturnReasonForm;
import com.mobile.service.objects.addresses.FormListItem;
import com.mobile.service.objects.orders.OrderTrackerItem;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.pojo.ICustomFormField;
import com.mobile.pojo.fields.ListNumberField;
import com.mobile.utils.order.ReturnItemViewHolder;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Fragment used to show the return reasons.
 *
 * @author spereira
 */
public class OrderReturnStep1Reason extends OrderReturnStepBase {

    private final static String FORM_PLACEHOLDER = "__NAME__";
    private final static String FORM_QUANTITY_FIELD = RestConstants.QUANTITY;
    private ArrayList<OrderTrackerItem> mItems;
    private ArrayList<DynamicForm> mDynamicForms;
    private String mOrder;
    private int mRequestReasonsCounter;

    /**
     * Empty constructor
     */
    public OrderReturnStep1Reason() {
        super(OrderReturnStepsMain.REASON, R.string.order_return_reason_title, R.string.continue_label);
    }

    /*
     * ##### LIFECYCLE #####
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get order number
        mOrder = getOrderNumber();
        // Get order items
        mItems = getOrderItems();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i("ON VIEW CREATED");
        // Validate order items
        if (CollectionUtils.isNotEmpty(mItems)) {
            triggerGetReasonForm(mItems.size());
        } else {
            showFragmentErrorRetry();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Case rotation save state
        if (CollectionUtils.isNotEmpty(mItems) && CollectionUtils.sizeIsSame(mItems, mDynamicForms)) {
            for (int i = 0; i < mDynamicForms.size(); i++) {
                Bundle bundle = new Bundle();
                // Save form state
                mDynamicForms.get(i).saveFormState(bundle);
                // Save group state
                outState.putBundle(mItems.get(i).getSku(), bundle);
            }
        }
    }

    /**
     * Load the saved state
     */
    private void loadSavedFormState(@NonNull DynamicForm dynamicForm, @Nullable Bundle state, @NonNull String key) {
        if (state != null) {
            dynamicForm.loadSaveFormState(state.getBundle(key));
        }
    }

    /*
     * ##### LAYOUT #####
     */

    /**
     * Create and show the form.
     */
    private void loadForm(ReturnReasonForm forms, ArrayList<OrderTrackerItem> items) {
        // Remove all views from container
        if (mContainer.getChildCount() > 0) {
            mContainer.removeAllViews();
        }
        // View group to add each form
        ViewGroup group = (ViewGroup) LayoutInflater.from(getBaseActivity()).inflate(R.layout._def_order_return_step_reason, this.mContainer, false);
        // Used to save dynamic forms
        mDynamicForms = new ArrayList<>();
        // Create form for each item
        for (int i = 0; i < items.size(); i++) {
            // Get form
            Form form = forms.get(i);
            // Get item
            OrderTrackerItem item = items.get(i);
            // Get key to load saved state
            String key = item.getSku();
            // Create dynamic form
            DynamicForm dyForm = createDynamicForm(form, item);
            // Load saved state
            loadSavedFormState(dyForm, mSavedState, key);
            // Save dynamic form
            mDynamicForms.add(dyForm);
            // Add item and form view
            group.addView(dyForm.getContainer());
        }
        // Add group
        mContainer.addView(group);
    }

    /**
     * Create dynamic form with custom view for return quantity.
     */
    private DynamicForm createDynamicForm(@NonNull Form form, @NonNull OrderTrackerItem item) {
        // Create custom view
        ReturnItemViewHolder custom = new ReturnItemViewHolder(getContext(), mOrder, item).bind();
        // Create form view
        DynamicForm dyForm = new DynamicForm(getContext(), form)
                .addMarginTop(R.dimen.margin_padding_xs)
                .addMarginBottom(R.dimen.margin_padding_l)
                .addType(FormConstants.ORDER_RETURN_REASON_FORM)
                .addRequestCallBack(this)
                .build();
        // Validate form field
        DynamicFormItem dyFormItem = dyForm.getItemByKey(FORM_QUANTITY_FIELD);
        // Case quantity field
        if(dyFormItem instanceof ICustomFormField) {
            // Show quantity and add view
            custom.showQuantityButton(getWeakBaseActivity(), item.getDefaultOrderAction());
            ((ICustomFormField) dyFormItem).addCustomView(custom);
        }
        // Case default show item as header
        else {
            dyForm.addHeader(custom.getView());
        }
        // Return dynamic form
        return dyForm;
    }

    /**
     * Validate all forms
     */
    private boolean validateForms() {
        boolean result = true;
        for (DynamicForm dynamicForm : mDynamicForms) {
            result &= dynamicForm.validate();
        }
        return result;
    }

    /**
     * Save all forms, performing the respective placeholders replacement.
     */
    private ContentValues saveForms() {
        ContentValues result = new ContentValues();
        // Filter form
        for (int i = 0; i < mDynamicForms.size(); i++) {
            // Get dynamic form
            DynamicForm form = mDynamicForms.get(i);
            // Get order item
            OrderTrackerItem item = mItems.get(i);
            // Get data
            ContentValues data = form.save();
            // Replace placeholders
            for (Map.Entry<String, Object> entry : data.valueSet()) {
                // Hammered to get reason label
                hammeredToGetReasonLabel(result, form, item.getSku());
                // Replace placeholder
                String key = entry.getKey().replace(FORM_PLACEHOLDER, item.getSku());
                // Save
                result.put(key, entry.getValue().toString());
            }
        }
        return result;
    }

    /*
     * ##### TRIGGERS #####
     */

    private void triggerGetReasonForm(int number) {
        // Reset reasons counter
        mRequestReasonsCounter = 0;
        // Get form
        triggerContentEvent(new GetReturnReasonFormHelper(), GetReturnReasonFormHelper.createBundle(number), this);
    }

    /*
     * ##### LISTENERS #####
     */

    @Override
    protected void onClickNextStep() {
        // Validate form
        if (validateForms()) {
            // Get data from forms
            ContentValues values = saveForms();
            // Save data
            super.saveSubmittedValues(values);
            // Next Step
            super.onClickNextStep();
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        // Validate order items
        if (CollectionUtils.isNotEmpty(getOrderItems())) {
            triggerGetReasonForm(getOrderItems().size());
        } else {
            showFragmentErrorRetry();
        }
    }

    /*
     * ##### REQUEST CALLBACK #####
     */

    @Override
    protected void onSuccessResponse(BaseResponse response) {
        // Get event
        EventType type = response.getEventType();
        // Case GET_RETURN_REASON_FORM
        if (type == EventType.GET_RETURN_REASON_FORM) {
            // Get form
            ReturnReasonForm forms = (ReturnReasonForm) response.getContentData();
            // Get items
            ArrayList<OrderTrackerItem> items = getOrderItems();
            // Validate data
            if (CollectionUtils.isNotEmpty(items) && CollectionUtils.sizeIsSame(items, forms)) {
                loadForm(forms, items);
            } else {
                showFragmentErrorRetry();
            }
        }
        // Case GET_RETURN_REASONS
        else if (type == EventType.GET_RETURN_REASONS) {
            // Inc counter
            mRequestReasonsCounter++;
            // Validate counter for each response
            if (mRequestReasonsCounter == CollectionUtils.size(getOrderItems())) {
                showFragmentContentContainer();
            }
        }
    }

    @Override
    protected void onErrorResponse(BaseResponse response) {
        // Case GET_RETURN_REASON_FORM
        // Case GET_RETURN_REASONS
        showFragmentErrorRetry();
    }


    /**
     * This hammered was added because all return steps are being saved in the app side.<br>
     * And we need save the selected labels to show in the finish step.
     * TODO: NAFAMZ-16058 - Hammered dded in v3.2
     */
    @SuppressWarnings("ConstantConditions")
    private void hammeredToGetReasonLabel(@NonNull ContentValues result, @NonNull DynamicForm form, @NonNull String sku) {
        try {
            // Save readable reason
            View view = form.getItemByKey(RestConstants.REASON).getDataControl();
            String label = ((FormListItem) ((IcsSpinner) view).getSelectedItem()).getLabel();
            result.put(RestConstants.REASON + sku, label);
            // Save selected quantity
            label = ((ListNumberField) form.getItemByKey(RestConstants.QUANTITY)).getValueFromCustomView();
            result.put(RestConstants.QUANTITY + sku, label);
        } catch (NullPointerException e) {
            Print.w("WARNING: NPE ON GET LABEL");
        }
    }

    /**
     * This hammered was added to get the selected quantity.
     * TODO: NAFAMZ-16058 - Hammered dded in v3.2
     */
    @Nullable
    public static String getQuantityValue(@Nullable ContentValues values, @NonNull String sku) {
        return values != null ? values.getAsString(RestConstants.QUANTITY + sku) : null;
    }

    /**
     * This hammered was added to get the label.
     * TODO: NAFAMZ-16058 - Hammered dded in v3.2
     */
    @Nullable
    public static String getReasonLabel(@Nullable ContentValues values, @NonNull String sku) {
        return values != null ? values.getAsString(RestConstants.REASON + sku) : null;
    }

}
