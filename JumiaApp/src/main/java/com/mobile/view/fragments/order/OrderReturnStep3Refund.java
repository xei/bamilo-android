package com.mobile.view.fragments.order;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.mobile.helpers.configs.GetStaticPageHelper;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;

/**
 * Fragment used to show the online returns reason.
 *
 * @author spereira
 */
public class OrderReturnStep3Refund extends OrderReturnStepBase {

    /**
     * Empty constructor
     */
    public OrderReturnStep3Refund() {
        super(OrderReturnStepsMain.REFUND, R.string.order_return_refund_title, R.string.continue_label);
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
        mContainer.setBackgroundColor(ContextCompat.getColor(getBaseActivity(), R.color.green_1));
    }

    /*
     * ##### TRIGGERS #####
     */

    private void triggerStaticPage() {
        triggerContentEvent(new GetStaticPageHelper(), GetStaticPageHelper.createBundle(mArgId), this);
    }

    /*
     * ##### LISTENERS #####
     */

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

    }

}

