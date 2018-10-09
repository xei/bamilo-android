package com.bamilo.android.appmodule.bamiloapp.view.newfragments;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bamilo.android.appmodule.bamiloapp.adapters.AddressAdapter;
import com.bamilo.android.appmodule.bamiloapp.adapters.ISetDefaultAddress;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.helpers.address.GetFormDeleteAddressHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.address.SetDefaultShippingAddressHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.framework.service.objects.addresses.Address;
import com.bamilo.android.framework.service.objects.addresses.Addresses;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogGenericFragment;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseAddressesFragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by Arash on 1/22/2017.
 */

public abstract class NewBaseAddressesFragment extends NewBaseFragment  implements IResponseCallback, ISetDefaultAddress {

    private static final String TAG = BaseAddressesFragment.class.getSimpleName();
    protected Addresses mAddresses;
    RecyclerView mAddressView;
    private boolean mIsCheckout = false;
    private DialogGenericFragment dialogLogout;


    public NewBaseAddressesFragment(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, @LayoutRes int layoutResId, @StringRes int titleResId, @ConstantsCheckout.CheckoutType int titleCheckout, boolean isCheckout) {
        super(enabledMenuItems, action, layoutResId, titleResId, ADJUST_CONTENT, titleCheckout);
        mIsCheckout = isCheckout;
    }
   /* public NewBaseAddressesFragment(boolean isCheckout)
    {
        super();
        mIsCheckout = isCheckout;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /*
     * ############# REQUESTS #############
     */

    protected void triggerGetForm() {
        triggerGetAddresses();
    }

    protected abstract void triggerGetAddresses();

    /*

     */
    protected void showAddresses(Addresses addresses, int mSelectedAddressId) {
        // Save addresses
        mAddresses = addresses;
        Address tmp = new Address();
        ArrayList<Address> addressList = new ArrayList<>();
        tmp = addresses.getShippingAddress();
        tmp.setDefault(true);
        addressList.add(tmp);
        for (LinkedHashMap.Entry<String, Address> item : addresses.getAddresses().entrySet()) {
            Address otherAddress = item.getValue();
            otherAddress.setDefault(false);
            addressList.add(otherAddress);
        }


        AddressAdapter mAddressAdapter = new AddressAdapter(addressList, mIsCheckout, mSelectedAddressId, onClickDeleteAddressButton, this);
        mAddressAdapter.baseFragment = this;
        mAddressView.removeAllViews();
        mAddressView.setAdapter(mAddressAdapter);

        showFragmentContentContainer();

    }

    /**
     * Process the click on edit button.</br>
     * Gets the address id from view tag.
     */
    View.OnClickListener onClickDeleteAddressButton =  new View.OnClickListener() {
        @Override
        public void onClick(final View view) {

            dialogLogout = DialogGenericFragment.newInstance(true, false,
                    getString(R.string.delete_address_label),
                    getString(R.string.delete_address_question),
                    getString(R.string.no_label),
                    getString(R.string.yes_label),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v.getId() == R.id.button2) {
                                int addressId = (int) view.getTag();
                                // Print.i(TAG, "ON CLICK: EDIT ADDRESS " + addressId);
                                // Goto edit address
                                triggerDeleteAddressForm(addressId);
                            }
                            dialogLogout.dismiss();
                        }
                    });
            dialogLogout.show(getBaseActivity().getSupportFragmentManager(), null);



        }
    } ;

    public void setDefault(final int id)  {

            dialogLogout = DialogGenericFragment.newInstance(true, false,
                    getString(R.string.default_address_label),
                    getString(R.string.default_address_question),
                    getString(R.string.no_label),
                    getString(R.string.yes_label),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v.getId() == R.id.button2) {
                                int addressId = id;
                                // Print.i(TAG, "ON CLICK: EDIT ADDRESS " + addressId);
                                // Goto edit address
                                triggerDefaultAddressForm(addressId);
                            }
                            else {
                                showAddresses(mAddresses, -1);
                            }
                            dialogLogout.dismiss();
                        }
                    });
            dialogLogout.show(getBaseActivity().getSupportFragmentManager(), null);
    };

    /**
     * Trigger to get the address form
     */
    protected void triggerDeleteAddressForm(int mAddressId){
        triggerContentEventProgress(new GetFormDeleteAddressHelper(), GetFormDeleteAddressHelper.createBundle(mAddressId), this);
    }

    protected void triggerDefaultAddressForm(int mAddressId){
        triggerContentEventProgress(new SetDefaultShippingAddressHelper(), SetDefaultShippingAddressHelper.createBundle(mAddressId), this);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        /*if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }*/
        hideActivityProgress();

        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case SET_DEFAULT_SHIPPING_ADDRESS:
                showAddresses((Addresses) baseResponse.getContentData(), -1);
                break;
            case GET_DELETE_ADDRESS_FORM_EVENT:
                showAddresses((Addresses) baseResponse.getContentData(), -1);
                break;
            default:
                break;
        }
    }

    interface MyCallbackInterface {

        void setDefault(String result);
    }
}
