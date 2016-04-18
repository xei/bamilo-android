package com.mobile.view.fragments;

import android.os.Bundle;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.configs.GetStaticPageHelper;
import com.mobile.newFramework.objects.statics.StaticPage;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * Fragment used to show the online returns conditions.
 * @author spereira
 */
public class OrderReturnConditionsFragment extends BaseFragmentRequester {

    private TextView mPageView;

    /**
     * New instance SessionTermsFragment.
     * @param bundle The arguments
     * @return SessionTermsFragment
     */
    public static OrderReturnConditionsFragment getInstance(Bundle bundle) {
        OrderReturnConditionsFragment termsFragment = new OrderReturnConditionsFragment();
        termsFragment.setArguments(bundle);
        return termsFragment;
    }

    /**
     * Empty constructor
     */
    public OrderReturnConditionsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ACCOUNT,
                R.layout._def_order_return_conditions,
                R.string.my_orders_label,
                NO_ADJUST_CONTENT);
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
        // Get cms
        mPageView = (TextView) view.findViewById(R.id.order_return_conditions_text_cms);
        // Get button
        view.findViewById(R.id.order_return_conditions_button_ok).setOnClickListener(this);
        // Get static page
        // Validate content
        if(TextUtils.isEmpty(mContentId)) {
            goToOrderReturnReason();
        } else {
            triggerStaticPage();
        }
    }

    /*
     * ##### TRIGGERS #####
     */

    private void triggerStaticPage() {
        triggerContentEvent(new GetStaticPageHelper(), GetStaticPageHelper.createBundle(mContentId), this);
    }

    /*
     * ##### SWITCH #####
     */
    private void goToOrderReturnReason() {
        new UISwitcher(getBaseActivity(), FragmentType.ORDER_RETURN_REASON).run();
    }

    /*
     * ##### LISTENERS #####
     */

    @Override
    public void onClick(View view) {
        // Case next step
        if (view.getId() == R.id.order_return_conditions_button_ok) {
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
        // Show static page
        mPageView.setText(((StaticPage) response.getMetadata().getData()).getHtml());
        // Show container
        showFragmentContentContainer();
    }

    @Override
    protected void onErrorResponse(BaseResponse response) {
        goToOrderReturnReason();
    }

}
