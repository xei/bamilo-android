package com.mobile.view.fragments.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.mobile.view.fragments.BaseFragmentRequester;

import java.util.EnumSet;

/**
 * Fragment used to show the online returns conditions.
 *
 * @author spereira
 */
public class OrderReturnConditionsFragment extends BaseFragmentRequester {

    private ViewGroup mContainer;

    /**
     * Empty constructor
     */
    public OrderReturnConditionsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ACCOUNT,
                R.layout._def_order_return_steps,
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
        // Set title
        ((TextView) view.findViewById(R.id.order_return_main_title)).setText(R.string.order_return_conditions_title);
        // Get container
        mContainer = (ViewGroup) view.findViewById(R.id.order_return_main_container);
        // Get button
        TextView button = (TextView) view.findViewById(R.id.order_return_main_button_ok);
        button.setText(R.string.ok_got_it);
        button.setOnClickListener(this);
        // Validate content static page
        if (TextUtils.isEmpty(this.mArgId)) {
            onClickNextStep();
        } else {
            triggerGetStaticPage();
        }
    }

    /*
     * ##### TRIGGERS #####
     */

    private void triggerGetStaticPage() {
        triggerContentEvent(new GetStaticPageHelper(), GetStaticPageHelper.createBundle(this.mArgId), this);
    }

    /*
     * ##### LISTENERS #####
     */

    /**
     * Start the order return steps.
     */
    protected void onClickNextStep() {
        super.onSwitchTo(FragmentType.ORDER_RETURN_STEPS).addArray(this.mArgArray).run();
    }

    @Override
    public void onClick(View view) {
        // Case next step
        if (view.getId() == R.id.order_return_main_button_ok) {
            onClickNextStep();
        } else {
            super.onClick(view);
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        triggerGetStaticPage();
    }

    /*
     * ##### REQUEST CALLBACK #####
     */

    @Override
    protected void onSuccessResponse(BaseResponse response) {
        // Show static page
        if (this.mContainer != null) {
            TextView text = (TextView) LayoutInflater.from(getBaseActivity()).inflate(R.layout._def_order_return_step_conditions, this.mContainer, false);
            text.setText(((StaticPage) response.getMetadata().getData()).getHtml());
            this.mContainer.addView(text);
        }
        // Show container
        showFragmentContentContainer();
    }

    @Override
    protected void onErrorResponse(BaseResponse response) {
        onClickNextStep();
    }

}
