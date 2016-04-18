package com.mobile.view.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mobile.components.recycler.DividerItemDecoration;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.interfaces.OnProductViewHolderClickListener;
import com.mobile.newFramework.objects.orders.OrderActions;
import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.newFramework.objects.product.Variation;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.ui.UIUtils;
import com.mobile.utils.ui.VariationProductsGridAdapter;
import com.mobile.utils.ui.VariationProductsGridView;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * */
public class ReturnCallFragment extends BaseFragment {

    protected static final String TAG = ReturnCallFragment.class.getSimpleName();

    private OrderTrackerItem orderItem;
    private String orderNumber;

    private TextView mTitleView;
    private TextView mOrderNumberView;
    private TextView mBody1View;
    private TextView mBody2View;
    private View mCallView;
    private View mSoppingView;


    /**
     * Create and return a new instance.
     *
     * @param bundle - arguments
     */
    public static ReturnCallFragment getInstance(Bundle bundle) {
        ReturnCallFragment returnCallFragment = new ReturnCallFragment();
        returnCallFragment.setArguments(bundle);
        return returnCallFragment;
    }


    /**
     * Empty constructor
     */
    public ReturnCallFragment() {
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
            orderItem = arguments.getParcelable(ConstantsIntentExtra.DATA);
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
        mCallView = view.findViewById(R.id.btn_call_now);
        mSoppingView = view.findViewById(R.id.btn_continue_shopping);

        mCallView.setOnClickListener(this);
        mSoppingView.setOnClickListener(this);

        onValidateState();
    }

    private void onValidateState() {
        // Validate the sate
        if (orderItem != null && CollectionUtils.isNotEmpty(orderItem.getOrderActions()) && TextUtils.isNotEmpty(orderNumber)){
            fillLayout();
        } else {
            showUnexpectedErrorWarning();
        }
    }

    private void fillLayout(){
        OrderActions orderActions = orderItem.getOrderActions().get(IntConstants.DEFAULT_POSITION);
        mTitleView.setText(orderActions.getTitle());
        mOrderNumberView.setText(orderNumber);
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
