package com.bamilo.android.appmodule.bamiloapp.view.fragments.order;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.framework.service.objects.orders.OrderActions;
import com.bamilo.android.framework.service.objects.orders.OrderTrackerItem;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.output.Print;
import com.bamilo.android.appmodule.bamiloapp.preferences.CountryPersistentConfigs;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UIUtils;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseFragment;

import java.util.EnumSet;

/**
 * */
public class OrderReturnCallFragment extends BaseFragment {

    protected static final String TAG = OrderReturnCallFragment.class.getSimpleName();

    private OrderTrackerItem mOrderItem;
    private String orderNumber;

    private TextView mTitleView;
    private TextView mOrderNumberView;
    private TextView mBody1View;
    private TextView mBody2View;

    /**
     * Empty constructor
     */
    public OrderReturnCallFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.UNKNOWN,
                R.layout._def_return_call_layout,
                R.string.call_return_label,
                NO_ADJUST_CONTENT);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get data from arguments (Home/Categories/Deep link)
        Bundle arguments = getArguments();
        if (arguments != null) {
            Print.i(TAG, "ARGUMENTS: " + arguments);
            mOrderItem = arguments.getParcelable(ConstantsIntentExtra.DATA);
            orderNumber = arguments.getString(ConstantsIntentExtra.ARG_1);

        } else {
            showUnexpectedErrorWarning();
        }
    }

    /*
    * (non-Javadoc)
    * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
    */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");

        mTitleView = (TextView) view.findViewById(R.id.return_call_title);
        mOrderNumberView = (TextView) view.findViewById(R.id.return_call_order_number);
        mBody1View = (TextView) view.findViewById(R.id.return_call_body1);
        mBody2View = (TextView) view.findViewById(R.id.return_call_body2);
        TextView callView = (TextView) view.findViewById(R.id.btn_call_now);
        View soppingView = view.findViewById(R.id.btn_continue_shopping);

        if(DeviceInfoHelper.hasTelephony(getBaseActivity())){
            callView.setOnClickListener(this);
        } else {
            callView.setEnabled(false);
            callView.setText(getString(R.string.please_call_placeholder, CountryPersistentConfigs.getCountryPhoneNumber(getBaseActivity())));
        }

        soppingView.setOnClickListener(this);

        onValidateState();
    }

    private void onValidateState() {
        // Validate the sate
        if (mOrderItem != null && CollectionUtils.isNotEmpty(mOrderItem.getOrderActions()) && TextUtils.isNotEmpty(orderNumber)){
            fillLayout();
        } else {
            showUnexpectedErrorWarning();
        }
    }

    private void fillLayout(){
        OrderActions orderActions = mOrderItem.getOrderActions().get(IntConstants.DEFAULT_POSITION);
        mTitleView.setText(orderActions.getTitle());
        mOrderNumberView.setText(String.format(getString(R.string.order_number_placeholder),orderNumber));
        mBody1View.setText(orderActions.getBody1());
        mBody2View.setText(orderActions.getBody2());
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_call_now){
            UIUtils.onClickCallToOrder(getBaseActivity());
        } else if(view.getId() == R.id.btn_continue_shopping){
            // Goto home
            getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        } else {
            super.onClick(view);
        }

    }
}
