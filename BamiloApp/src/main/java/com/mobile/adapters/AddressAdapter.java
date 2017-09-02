package com.mobile.adapters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mobile.components.customfontviews.RadioButton;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.service.objects.addresses.AdapterItemSelection;
import com.mobile.service.objects.addresses.Address;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.view.R;
import com.mobile.view.fragments.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arash on 1/24/2017.
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> implements IResponseCallback {
    private List<Address> addressesList;
    private List<AdapterItemSelection> addressSelection;
    private static RadioButton lastChecked = null;
    private static int lastCheckedPos = 0;
    public BaseFragment baseFragment;
    private boolean mIsCheckout;
    private View.OnClickListener mOnClickDeleteAddressButton;
    private ISetDefaultAddress mSetDefaultAddress;



    public class AddressViewHolder extends RecyclerView.ViewHolder {
        public TextView name, street, phone;
        public RadioButton checkBox;
        public ImageView editButton;
        public ImageView deleteButton;
        public RelativeLayout root, editButton_Rl;
        public View vertical;
        public View buttons;

        public AddressViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.checkout_address_item_name);
            street = (TextView) view.findViewById(R.id.checkout_address_item_street);
            phone = (TextView) view.findViewById(R.id.checkout_address_item_phone);
            checkBox = (RadioButton) view.findViewById(R.id.checkout_address_item_radio_btn);
            editButton = (ImageView) view.findViewById(R.id.checkout_address_item_btn_edit);
            editButton_Rl = (RelativeLayout) view.findViewById(R.id.checkout_address_item_btn_edit_rl);
            deleteButton = (ImageView) view.findViewById(R.id.checkout_address_item_btn_delete);
            root = (RelativeLayout)view.findViewById(R.id.checkout_address_item_content);
            vertical = (View) view.findViewById(R.id.vertical_separator);
            buttons = view.findViewById(R.id.checkout_address_item_delete);
        }
    }


    public AddressAdapter(List<Address> addressesList, boolean isCheckout, int mSelectedAddressId,
                          View.OnClickListener onClickDeleteAddressButton, ISetDefaultAddress setDefaultAddress) {
        mIsCheckout = isCheckout;
        mOnClickDeleteAddressButton = onClickDeleteAddressButton;
        mSetDefaultAddress = setDefaultAddress;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_checkout_address_item, parent, false);

        AddressViewHolder holder = new AddressViewHolder(view);



        return holder;
    }



    @Override
    public void onBindViewHolder(AddressViewHolder holder, int position) {
        Address address = addressesList.get(position);
        holder.name.setText(String.format("%1s %2s", address.getFirstName(), address.getLastName()));
        holder.street.setText(address.getAddress());
        holder.phone.setText(address.getPhone());
        holder.editButton.setOnClickListener(onClickEditAddressButton);
        holder.editButton.setTag(address.getId());
        holder.editButton_Rl.setOnClickListener(onClickEditAddressButton);
        holder.editButton_Rl.setTag(address.getId());
        holder.deleteButton.setOnClickListener(mOnClickDeleteAddressButton);
        holder.deleteButton.setTag(address.getId());
        holder.buttons.setVisibility(View.VISIBLE);

        if (address.isDefault())
        {
            holder.buttons.setVisibility(View.INVISIBLE);
        }

        if (mIsCheckout) {
            holder.buttons.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.VISIBLE);
        }
            holder.checkBox.setChecked(addressSelection.get(position).isSelected());
            holder.checkBox.setTag(R.string.address_item_key_1, new Integer(position));
            holder.checkBox.setTag(R.string.address_item_key_2, address.getId());

            //for default check in first item
            if (/*position == 0 && addressSelection.get(0).isSelected() &&*/ holder.checkBox.isChecked()) {
                lastChecked = holder.checkBox;
                lastCheckedPos = position;
            }

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RadioButton cb = (RadioButton) v;
                    int clickedPos = ((Integer) cb.getTag(R.string.address_item_key_1)).intValue();

                    if (cb.isChecked()) {
                        if (lastChecked != null && clickedPos != lastCheckedPos) {
                            lastChecked.setChecked(false);
                            addressSelection.get(lastCheckedPos).setSelected(false);
                            lastChecked = cb;
                            lastCheckedPos = clickedPos;
                            if (!mIsCheckout)
                            {
                                mSetDefaultAddress.setDefault((int)(cb.getTag(R.string.address_item_key_2)));
                            }
                        }
                    } else
                        lastChecked = null;

                    addressSelection.get(clickedPos).setSelected(cb.isChecked());
                }
            });

       // }


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
            baseFragment.getBaseActivity().onSwitchFragment(mIsCheckout?FragmentType.CHECKOUT_EDIT_ADDRESS:FragmentType.MY_ACCOUNT_EDIT_ADDRESS, bundle, FragmentController.ADD_TO_BACK_STACK);
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



    @Override
    public void onRequestComplete(BaseResponse baseResponse) {

    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {

    }

}
