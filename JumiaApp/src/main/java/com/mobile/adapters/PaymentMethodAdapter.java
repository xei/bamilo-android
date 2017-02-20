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
import com.mobile.newFramework.objects.addresses.AdapterItemSelection;
import com.mobile.newFramework.objects.addresses.Address;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;
import com.mobile.view.fragments.BaseFragment;
import com.mobile.view.newfragments.PaymentMethod;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arash on 1/24/2017.
 */

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.MethodViewHolder> implements IResponseCallback {
    private List<PaymentMethod> methodsList;
    private List<AdapterItemSelection> methodSelection;
    private static RadioButton lastChecked = null;
    private static int lastCheckedPos = 0;
    public BaseFragment baseFragment;
    private ISetDefaultAddress mSetDefaultAddress;



    public class MethodViewHolder extends RecyclerView.ViewHolder {
        public TextView name, text;
        public RadioButton checkBox;
        public ImageView method_logo;

        public MethodViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.checkout_payment_item_name);
            checkBox = (RadioButton) view.findViewById(R.id.checkout_payment_item_radio_btn);
            method_logo = (ImageView) view.findViewById(R.id.method_logo);
            text = (TextView) view.findViewById(R.id.checkout_payment_item_text);
        }
    }


    public PaymentMethodAdapter(List<PaymentMethod> methodsList, int mSelectedMethodId) {

        this.methodsList = methodsList;
        this.methodSelection = new ArrayList<>();
        for (int i=0;i<methodsList.size(); i++)
        {
            AdapterItemSelection tmp = new AdapterItemSelection();
            tmp.id=Integer.parseInt(methodsList.get(i).getId());
            if (i==0 && mSelectedMethodId == -1) tmp.setSelected(true);
            if (tmp.id == mSelectedMethodId) tmp.setSelected(true);
            methodSelection.add(tmp);
        }
    }


    @Override
    public MethodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_checkout_payment_item, parent, false);

        MethodViewHolder holder = new MethodViewHolder(view);



        return holder;
    }



    @Override
    public void onBindViewHolder(final MethodViewHolder holder, int position) {
        PaymentMethod method = methodsList.get(position);
        holder.name.setText(method.getTitle());
        holder.text.setText(method.getText());

        String imageUrl = method.getImageUrl();
        Print.d("IMAGEURL", imageUrl);
        if (TextUtils.isNotEmpty(imageUrl)) {
            RocketImageLoader.instance.loadImage(imageUrl, holder.method_logo, null, 0);
        } else {
            holder.method_logo.setVisibility(View.GONE);
        }


        holder.checkBox.setVisibility(View.VISIBLE);
        holder.checkBox.setChecked(methodSelection.get(position).isSelected());
        holder.checkBox.setTag(R.string.address_item_key_1, new Integer(position));
        holder.checkBox.setTag(R.string.address_item_key_2, method.getId());
        holder.checkBox.setTag(R.id.text_field, holder.text);

        //for default check in first item
        if (position == 0 && methodSelection.get(0).isSelected() && holder.checkBox.isChecked()) {
            lastChecked = holder.checkBox;
            lastCheckedPos = 0;
            holder.text.setVisibility(View.VISIBLE);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton cb = (RadioButton) v;
                int clickedPos = ((Integer) cb.getTag(R.string.address_item_key_1)).intValue();

                if (cb.isChecked()) {
                    if (lastChecked != null && clickedPos != lastCheckedPos) {
                        lastChecked.setChecked(false);
                        ((TextView)(lastChecked).getTag(R.id.text_field)).setVisibility(View.GONE);
                        methodSelection.get(lastCheckedPos).setSelected(false);
                        lastChecked = cb;
                        lastCheckedPos = clickedPos;
                        holder.text.setVisibility(View.VISIBLE);
                    }
                } else
                    lastChecked = null;

                methodSelection.get(clickedPos).setSelected(cb.isChecked());
            }
        });

    }

    /**
     * Process the click on edit button.</br>
     * Gets the address id from view tag.
     */




    public int getSelectedId()
    {
        return methodSelection.get(lastCheckedPos).id;
    }

    @Override
    public int getItemCount() {
        return methodsList.size();
    }



    @Override
    public void onRequestComplete(BaseResponse baseResponse) {

    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {

    }

}
