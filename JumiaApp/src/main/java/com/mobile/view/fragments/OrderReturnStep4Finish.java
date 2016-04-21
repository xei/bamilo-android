package com.mobile.view.fragments;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.components.customfontviews.TextView;
import com.mobile.helpers.configs.GetStaticPageHelper;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.Toast;
import com.mobile.view.R;

/**
 * Fragment used to show the online returns reason.
 * @author spereira
 */
public class OrderReturnStep4Finish extends BaseFragmentRequester {

    private ViewGroup mContainer;

    /**
     * Empty constructor
     */
    public OrderReturnStep4Finish() {
        super(IS_NESTED_FRAGMENT, R.layout._def_order_return_steps);
    }

    /*
     * ##### LIFECYCLE #####
     */

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i("ON VIEW CREATED");
        ((TextView) view.findViewById(R.id.order_return_main_title)).setText(R.string.order_return_finish_title);
        // Get container
        mContainer = (ViewGroup) view.findViewById(R.id.order_return_main_container);
        mContainer.setBackgroundColor(ContextCompat.getColor(getBaseActivity(), R.color.orange_1));
        // Get button
        ((TextView) view.findViewById(R.id.order_return_main_button_ok)).setText(R.string.send_label);
        view.findViewById(R.id.order_return_main_button_ok).setOnClickListener(this);
    }

    /*
     * ##### TRIGGERS #####
     */

    private void triggerStaticPage() {
        triggerContentEvent(new GetStaticPageHelper(), GetStaticPageHelper.createBundle(mId), this);
    }

    /*
     * ##### SWITCH #####
     */
    private void goToOrderReturnReason() {
        Toast.makeText(getBaseActivity(), "SEND DATA AND FINISH", Toast.LENGTH_SHORT).show();
    }

    /*
     * ##### LISTENERS #####
     */

    @Override
    public void onClick(View view) {
        // Case next step
        if (view.getId() == R.id.order_return_main_button_ok) {
            goToOrderReturnReason();
        } else {
            super.onClick(view);
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        triggerStaticPage();
    }

    /*
     * ##### REQUEST CALLBACK #####
     */

    @Override
    protected void onSuccessResponse(BaseResponse response) {
        // Show container
        showFragmentContentContainer();
    }

    @Override
    protected void onErrorResponse(BaseResponse response) {
        goToOrderReturnReason();
    }

}
