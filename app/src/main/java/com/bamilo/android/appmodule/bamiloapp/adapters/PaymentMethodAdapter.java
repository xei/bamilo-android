package com.bamilo.android.appmodule.bamiloapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.view.newfragments.PaymentMethod;
import com.bamilo.android.appmodule.modernbamilo.util.extension.ImageViewExtKt;
import com.bamilo.android.framework.components.customfontviews.RadioButton;
import com.bamilo.android.framework.service.objects.addresses.AdapterItemSelection;
import com.bamilo.android.framework.service.pojo.BaseResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arash on 1/24/2017.
 */

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.MethodViewHolder> implements IResponseCallback {
    public interface IPaymentMethodAdapter {
        void paymentMethodSelected(int selectedId);
    }

    private List<PaymentMethod> methodsList;
    private List<AdapterItemSelection> methodSelection;
    private static RadioButton lastChecked = null;
    private static int lastCheckedPos = 0;
    public IPaymentMethodAdapter mFragmentBridge;

    public class MethodViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView name, text;
        public RadioButton checkBox;
        public ImageView method_logo;
        public RelativeLayout payment_logo;

        public MethodViewHolder(View view) {
            super(view);
            this.view = view;
            name = view.findViewById(R.id.checkout_payment_item_name);
            checkBox = view.findViewById(R.id.checkout_payment_item_radio_btn);
            method_logo = view.findViewById(R.id.method_logo);
            payment_logo = view.findViewById(R.id.payment_logo);
            text = view.findViewById(R.id.checkout_payment_item_text);
        }
    }

    public PaymentMethodAdapter(List<PaymentMethod> methodsList, int mSelectedMethodId) {
        this.methodsList = methodsList;
        this.methodSelection = new ArrayList<>();
        for (int i = 0; i < methodsList.size(); i++) {
            AdapterItemSelection tmp = new AdapterItemSelection();
            tmp.id = Integer.parseInt(methodsList.get(i).getId());
            if (i == 0 && mSelectedMethodId == -1) tmp.setSelected(true);
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
        String method_name = method.getTitle();
        holder.name.setText(method_name);
        holder.text.setText(method.getText());

//        if (method_name.contains("پارسیان")) {
//            holder.payment_logo.setVisibility(View.VISIBLE);
//            holder.method_logo.setImageResource(R.drawable.parsian_logo);
//        } else if (method_name.contains("سامان")) {
//            holder.payment_logo.setVisibility(View.VISIBLE);
//            holder.method_logo.setImageResource(R.drawable.saman_logo);
//        } else {
//            holder.payment_logo.setVisibility(View.GONE);
//        }

        holder.checkBox.setVisibility(View.VISIBLE);
        holder.checkBox.setChecked(methodSelection.get(position).isSelected());
        holder.checkBox.setTag(R.string.address_item_key_1, new Integer(position));
        holder.checkBox.setTag(R.string.address_item_key_2, method.getId());
        holder.checkBox.setTag(R.id.text_field, holder.text);
        ImageViewExtKt.loadImageFromNetwork(holder.method_logo, method.getIconUrl());

        //for default check in first item
        if (position == 0 && methodSelection.get(0).isSelected() && holder.checkBox.isChecked()) {
            lastChecked = holder.checkBox;
            lastCheckedPos = 0;
            if (holder.text.getText().equals("")) {
                holder.text.setVisibility(View.GONE);
            } else {
                holder.text.setVisibility(View.VISIBLE);
            }

            mFragmentBridge.paymentMethodSelected(getSelectedId());
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkBox.setChecked(true);
                RadioButton cb = holder.checkBox;
                int clickedPos = holder.getAdapterPosition();

                if (cb.isChecked()) {
                    if (lastChecked != null && clickedPos != lastCheckedPos) {
                        lastChecked.setChecked(false);
                        ((TextView) (lastChecked).getTag(R.id.text_field)).setVisibility(View.GONE);
                        methodSelection.get(lastCheckedPos).setSelected(false);
                        lastChecked = cb;
                        lastCheckedPos = clickedPos;
                        if (holder.text.getText().equals("")) {
                            holder.text.setVisibility(View.GONE);
                        } else {
                            holder.text.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    lastChecked = null;
                }

                methodSelection.get(clickedPos).setSelected(cb.isChecked());
                mFragmentBridge.paymentMethodSelected(getSelectedId());
            }
        });

    }

    public void clearSelection() {
        for (AdapterItemSelection method : methodSelection) {
            method.setSelected(false);
        }
        lastCheckedPos = -1;
        lastChecked = null;
    }

    public int getSelectedId() {
        if (methodSelection == null || methodSelection.size() == 0 || lastCheckedPos < 0) {
            return -1;
        }
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
