package com.mobile.view.fragments.order;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.newFramework.objects.orders.OrderActions;
import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;
import com.mobile.view.fragments.BaseFragment;

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
            getBaseActivity().onBackPressed();
        } else {
            super.onClick(view);
        }

    }
}
