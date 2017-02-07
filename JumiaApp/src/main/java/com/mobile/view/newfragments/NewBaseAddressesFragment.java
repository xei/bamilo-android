package com.mobile.view.newfragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.adapters.AddressAdapter;
import com.mobile.adapters.ISetDefaultAddress;
import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.LogOut;
import com.mobile.helpers.address.GetFormDeleteAddressHelper;
import com.mobile.helpers.address.SetDefaultShippingAddressHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.addresses.Address;
import com.mobile.newFramework.objects.addresses.Addresses;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.view.R;
import com.mobile.view.fragments.BaseAddressesFragment;
import com.mobile.view.fragments.BaseFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Arash on 1/22/2017.
 */

public abstract class NewBaseAddressesFragment extends NewBaseFragment  implements IResponseCallback, ISetDefaultAddress {

    private static final String TAG = BaseAddressesFragment.class.getSimpleName();
    protected Addresses mAddresses;
    RecyclerView mAddressView;
    private boolean mIsCheckout = false;
    private DialogGenericFragment dialogLogout;


    public NewBaseAddressesFragment(boolean isCheckout)
    {
        super();
        mIsCheckout = isCheckout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");

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
        // Get addresses
        if(mAddresses == null) {
            triggerGetForm();
        } else {
            //showFragmentContentContainer();
        }
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
        ArrayList<Address> addressList = new ArrayList<Address>();
        Log.d("mAddresses", "" + mAddresses.getAddresses().size());
        addressList.add((Address)addresses.getShippingAddress());
        for (LinkedHashMap.Entry<String, Address> item : addresses.getAddresses().entrySet()) {
            Address otherAddress = (Address)item.getValue();
            addressList.add(otherAddress);
        }


        AddressAdapter mAddressAdapter = new AddressAdapter(addressList, mIsCheckout, mSelectedAddressId, onClickDeleteAddressButton, this);
        mAddressAdapter.baseFragment = this;
        mAddressView.setAdapter(mAddressAdapter);


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
                            dialogLogout.dismiss();
                        }
                    });
            dialogLogout.show(getBaseActivity().getSupportFragmentManager(), null);




    };

    /**
     * Trigger to get the address form
     */
    protected void triggerDeleteAddressForm(int mAddressId){
        triggerContentEvent(new GetFormDeleteAddressHelper(), GetFormDeleteAddressHelper.createBundle(mAddressId), this);
    }

    protected void triggerDefaultAddressForm(int mAddressId){
        triggerContentEvent(new SetDefaultShippingAddressHelper(), SetDefaultShippingAddressHelper.createBundle(mAddressId), this);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        /*if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }*/
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT_: " + eventType);
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
