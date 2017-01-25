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
import com.mobile.components.customfontviews.TextView;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.addresses.Address;
import com.mobile.newFramework.objects.addresses.Addresses;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.TrackerDelegator;
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

public abstract class NewBaseAddressesFragment extends NewBaseFragment  implements IResponseCallback {

    private static final String TAG = BaseAddressesFragment.class.getSimpleName();
    protected Addresses mAddresses;
    RecyclerView mAddressView;
    public NewBaseAddressesFragment()
    {
        super();
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
        // Get sections
        //mAddressView = (RecyclerView) view.findViewById(R.id.address_recycler_view);
        /*mBillingView = (ViewGroup) view.findViewById(R.id.checkout_addresses_section_billing);
        mOthersView = (ViewGroup) view.findViewById(R.id.checkout_addresses_section_other);
        // Set buttons
        view.findViewById(R.id.checkout_addresses_button_add_top).setOnClickListener(this);
        mButtonBottom = view.findViewById(R.id.checkout_addresses_button_add_bottom);
        mButtonBottom.setOnClickListener(this);*/
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
    protected void showAddresses(Addresses addresses) {
        // Save addresses
        mAddresses = addresses;
        ArrayList<Address> addressList = new ArrayList<Address>();
        Log.d("mAddresses", "" + mAddresses.getAddresses().size());
        addressList.add((Address)addresses.getShippingAddress());
        for (LinkedHashMap.Entry<String, Address> item : addresses.getAddresses().entrySet()) {
            Address otherAddress = (Address)item.getValue();
            addressList.add(otherAddress);
        }


        AddressAdapter mAddressAdapter = new AddressAdapter(addressList);
        mAddressAdapter.baseFragment = this;
        mAddressView.setAdapter(mAddressAdapter);

        // Same address
        /*boolean isSameAddress = addresses.hasDefaultShippingAndBillingAddress();
        // Set shipping container
        TextView shippingTitle = (TextView) mShippingView.findViewById(R.id.checkout_address_title);
        shippingTitle.setText(getString(isSameAddress ? R.string.address_shipping_billing_label : R.string.address_shipping_label));
        addAddress(mShippingView, addresses.getShippingAddress(), false);
        // Set billing container
        if (isSameAddress) {
            mBillingView.setVisibility(View.GONE);
        } else {
            TextView billingTitle = (TextView) mBillingView.findViewById(R.id.checkout_address_title);
            billingTitle.setText(getString(R.string.address_billing_label));
            addAddress(mBillingView, addresses.getBillingAddress(), false);
        }
        // Show other container
        if (CollectionUtils.isNotEmpty(addresses.getAddresses())) {
            TextView othersTitle = (TextView) mOthersView.findViewById(R.id.checkout_address_title);
            othersTitle.setText(getString(R.string.address_others_label));
            addAddresses(mOthersView, addresses.getAddresses());
        } else {
            mOthersView.setVisibility(View.GONE);
            mButtonBottom.setVisibility(View.GONE);
        }
        // Show container
        showFragmentContentContainer();*/
    }

}
