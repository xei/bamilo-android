package com.mobile.view.newfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.mobile.adapters.AddressAdapter;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.address.GetMyAddressesHelper;
import com.mobile.helpers.checkout.GetStepAddressesHelper;
import com.mobile.helpers.checkout.SetStepAddressesHelper;
import com.mobile.newFramework.objects.addresses.Addresses;
import com.mobile.newFramework.objects.checkout.MultiStepAddresses;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;
import com.mobile.view.fragments.CheckoutAddressesFragment;

import java.util.EnumSet;

/**
 * Created by Arash on 1/22/2017.
 */

public class NewMyAccountAddressesFragment extends NewBaseAddressesFragment {

    private static final String TAG = NewMyAccountAddressesFragment.class.getSimpleName();

    private View mCheckoutTotalBar;
    private FloatingActionButton fabNewAddress;
    private int mSelectedAddress;

    public NewMyAccountAddressesFragment() {
        super(false);
    }

    /*
    * ############# LIFE CYCLE #############
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       /* View view =super.onCreateView(inflater, container, savedInstanceState);
        ViewStub contentContainer = (ViewStub) view.findViewById(R.id.content_container);
        contentContainer.setLayoutResource(R.layout.new_checkout_my_addresses);
        view = contentContainer.inflate();*/
        View view = inflater.inflate(R.layout.new_my_account_addresses, container, false);
        //mAddressView = (RecyclerView) view.findViewById(R.id.address_recycler_view);
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
       // mAddressView.setLayoutManager(mLayoutManager);
        mAddressView = (RecyclerView) view.findViewById(R.id.address_recycler_view);
        mAddressView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mAddressView.setLayoutManager(llm);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        //mCheckoutTotalBar = view.findViewById(R.id.address_continue);
        fabNewAddress = (FloatingActionButton) view.findViewById(R.id.fab_new_address);
        fabNewAddress.setOnClickListener(onClickCreateAddressButton);
        getBaseActivity().updateBaseComponents(EnumSet.of(MyMenuItem.UP_BUTTON_BACK), NavigationAction.MY_ACCOUNT_MY_ADDRESSES, R.string.my_addresses, ConstantsCheckout.NO_CHECKOUT);

        mSelectedAddress = -1;

    }



    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
            triggerGetForm();
    }

    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }


    /**
     * Process the click on add button.
     */
    View.OnClickListener onClickCreateAddressButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getBaseActivity().onSwitchFragment(FragmentType.MY_ACCOUNT_CREATE_ADDRESS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    };


    /**
     * ############# REQUESTS #############
     */

    @Override
    protected void triggerGetAddresses() {
        triggerContentEvent(new GetMyAddressesHelper(), null, this);
    }

    /*
     * Trigger to set the billing form
     */

    private void triggerSetMultiStepAddresses(int billing, int shipping) {
        Print.d(TAG, "TRIGGER SET BILLING");
        triggerContentEvent(new SetStepAddressesHelper(), SetStepAddressesHelper.createBundle(billing, shipping), this);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        /*if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }*/
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        switch (eventType) {
            case GET_CUSTOMER_ADDRESSES_EVENT:
                super.showAddresses((Addresses) baseResponse.getContentData(), -1);
                break;
            case GET_DELETE_ADDRESS_FORM_EVENT:
                triggerGetAddresses();
                break;
            case SET_DEFAULT_SHIPPING_ADDRESS:
                triggerGetAddresses();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        /*if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            Print.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return;
        }*/
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        Print.d(TAG, "ON ERROR EVENT: " + eventType + " " + errorCode);
        switch (eventType) {
            case GET_MULTI_STEP_ADDRESSES:
                // Show retry
                super.showFragmentErrorRetry();
                break;
            case SET_MULTI_STEP_ADDRESSES:
                /*if (errorCode == ErrorCode.REQUEST_ERROR) {
                    showWarningErrorMessage(baseResponse.getValidateMessage());
                } else {
                    super.showUnexpectedErrorWarning();
                }
                showFragmentContentContainer();*/
                break;
            default:
                break;
        }
    }
}
