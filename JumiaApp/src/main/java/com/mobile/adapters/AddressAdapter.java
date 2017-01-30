package com.mobile.adapters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.mobile.components.customfontviews.RadioButton;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.objects.addresses.AdapterItemSelection;
import com.mobile.newFramework.objects.addresses.Address;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;
import com.mobile.view.newfragments.NewBaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arash on 1/24/2017.
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder>  {
    private List<Address> addressesList;
    private List<AdapterItemSelection> addressSelection;
    private static RadioButton lastChecked = null;
    private static int lastCheckedPos = 0;
    public NewBaseFragment baseFragment;
    private boolean mIsCheckout;

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        public TextView name, street, phone;
        public RadioButton checkBox;
        public ImageView editButton;

        public AddressViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.checkout_address_item_name);
            street = (TextView) view.findViewById(R.id.checkout_address_item_street);
            phone = (TextView) view.findViewById(R.id.checkout_address_item_phone);
            checkBox = (RadioButton) view.findViewById(R.id.checkout_address_item_radio_btn);
            editButton = (ImageView) view.findViewById(R.id.checkout_address_item_btn_edit);
        }
    }


    public AddressAdapter(List<Address> addressesList, boolean isCheckout, int mSelectedAddressId) {
        mIsCheckout = isCheckout;
        this.addressesList = addressesList;
        this.addressSelection = new ArrayList<>();
        for (int i=0;i<addressesList.size(); i++)
        {
            AdapterItemSelection tmp = new AdapterItemSelection();
            tmp.id=addressesList.get(i).getId();
            if (i==0 && mSelectedAddressId == -1) tmp.setSelected(true);
            if (tmp.id == mSelectedAddressId) tmp.setSelected(true);
            addressSelection.add(tmp);
        }
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_checkout_address_item, parent, false);

        return new AddressViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, int position) {
        Address address = addressesList.get(position);
        holder.name.setText(String.format("%1s %2s", address.getFirstName(), address.getLastName()));
        holder.street.setText(address.getAddress());
        holder.phone.setText(address.getPhone());
        holder.editButton.setOnClickListener(onClickEditAddressButton);
        holder.editButton.setTag(address.getId());

        if (mIsCheckout) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(addressSelection.get(position).isSelected());
            holder.checkBox.setTag(new Integer(position));

            //for default check in first item
            if (position == 0 && addressSelection.get(0).isSelected() && holder.checkBox.isChecked()) {
                lastChecked = holder.checkBox;
                lastCheckedPos = 0;
            }

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RadioButton cb = (RadioButton) v;
                    int clickedPos = ((Integer) cb.getTag()).intValue();

                    if (cb.isChecked()) {
                        if (lastChecked != null && clickedPos != lastCheckedPos) {
                            lastChecked.setChecked(false);
                            addressSelection.get(lastCheckedPos).setSelected(false);
                        }

                        lastChecked = cb;
                        lastCheckedPos = clickedPos;
                    } else
                        lastChecked = null;

                    addressSelection.get(clickedPos).setSelected(cb.isChecked());
                }
            });

        }

    }

    /**
     * Process the click on edit button.</br>
     * Gets the address id from view tag.
     */
    View.OnClickListener onClickEditAddressButton =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int addressId = (int) view.getTag();
           // Print.i(TAG, "ON CLICK: EDIT ADDRESS " + addressId);
            // Goto edit address
            Bundle bundle = new Bundle();
            bundle.putInt(ConstantsIntentExtra.ARG_1, addressId);
            baseFragment.getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_EDIT_ADDRESS, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    } ;

    public int getSelectedId()
    {
        return addressSelection.get(lastCheckedPos).id;
    }

    @Override
    public int getItemCount() {
        return addressesList.size();
    }
}
