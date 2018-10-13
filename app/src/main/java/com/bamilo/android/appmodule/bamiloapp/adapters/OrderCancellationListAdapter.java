package com.bamilo.android.appmodule.bamiloapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.bamilo.android.core.service.model.data.itemtracking.CancellationReason;
import com.bamilo.android.core.service.model.data.itemtracking.CompleteOrder;
import com.bamilo.android.core.service.model.data.itemtracking.Package;
import com.bamilo.android.core.service.model.data.itemtracking.PackageItem;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mohsen on 1/29/18.
 */

public class OrderCancellationListAdapter extends
        RecyclerView.Adapter<OrderCancellationListAdapter.OrderCancellationListViewHolder> {

    private List<PackageItem> items;
    private List<CancellationReason> reasons;
    private Map<String, Integer> selectedItemsCount;
    private Map<String, String> selectedItemsCancellationReason;
    private PromptSpinnerAdapter spinnerAdapter;

    public OrderCancellationListAdapter(CompleteOrder completeOrder, String defaultItemId) {
        items = new ArrayList<>();
        selectedItemsCount = new HashMap<>();
        selectedItemsCancellationReason = new HashMap<>();
        if (completeOrder != null && CollectionUtils.isNotEmpty(completeOrder.getPackages())) {
            for (Package p : completeOrder.getPackages()) {
                if (CollectionUtils.isNotEmpty(p.getPackageItems())) {
                    items.addAll(p.getPackageItems());
                }
            }

            if (defaultItemId != null) {
                selectedItemsCount.put(defaultItemId, 1);
                for (PackageItem item : items) {
                    if (item.getId().equals(defaultItemId)) {
                        items.remove(item);
                        items.add(0, item);
                        break;
                    }
                }
            }
            reasons = completeOrder.getCancellation().getReasons();
        }
    }

    @Override
    public OrderCancellationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderCancellationListViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_cancellation_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(OrderCancellationListViewHolder holder, int position,
            List<Object> payloads) {
        if (payloads != null && !payloads.isEmpty()) {
            PackageItem item = items.get(position);
            Object payload = payloads.get(0);
            if (payload instanceof Integer) {
                Integer selectedCount = (Integer) payload;
                if (Math.abs(selectedCount) < item.getQuantity()) {
                    holder.imgQuantityPlus.setEnabled(true);
                } else {
                    holder.imgQuantityPlus.setEnabled(false);
                }
                if (Math.abs(selectedCount) > 1) {
                    holder.imgQuantityMinus.setEnabled(true);
                } else {
                    holder.imgQuantityMinus.setEnabled(false);
                }

                if (selectedCount < 0) {
                    selectedCount *= -1;
                }
                holder.tvProductQuantity.setText(String.valueOf(selectedCount));

                if (holder.checkBox.isChecked()) {
                    holder.spinnerCancellationReasons.setVisibility(View.VISIBLE);
                } else {
                    holder.spinnerCancellationReasons.setVisibility(View.GONE);
                    holder.tvCancellationReasonError.setVisibility(View.GONE);
                }
            } else if (payload instanceof CancellationReasonChangePayload) {
                CancellationReasonChangePayload pl = (CancellationReasonChangePayload) payload;
                holder.tvCancellationReasonError.setVisibility(
                        pl.getOption() == CancellationReasonChangePayload.REASON_NOT_SELECTED ?
                                View.VISIBLE : View.GONE);
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(final OrderCancellationListViewHolder holder, int position) {
        PackageItem item = items.get(position);
        Context context = holder.itemView.getContext();
        if (!item.getCancellation().isCancelable()) {
            holder.viewNotCancelable.setVisibility(View.VISIBLE);
            holder.tvNotCancelableReason.setVisibility(View.VISIBLE);
            holder.tvNotCancelableReason.setText(item.getCancellation().getNotCancelableReason());
            holder.checkBox.setEnabled(false);
            holder.itemView.setOnClickListener(null);
        } else {
            holder.viewNotCancelable.setVisibility(View.GONE);
            holder.tvNotCancelableReason.setVisibility(View.GONE);
            holder.checkBox.setEnabled(true);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.checkBox.toggle();
                }
            });
        }

        Integer selectedCount = selectedItemsCount.get(item.getId());
        holder.checkBox.setChecked(selectedCount != null && selectedCount != -1);
        if (spinnerAdapter == null) {
            spinnerAdapter = new PromptSpinnerAdapter(context, R.layout.form_spinner_item, reasons,
                    context.getString(R.string.prompt_choose_cancellation_reason));
            spinnerAdapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        }
        holder.spinnerCancellationReasons.setAdapter(spinnerAdapter);
        holder.spinnerCancellationReasons
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position,
                            long l) {
                        if (position > 0) {
                            CancellationReason reason = reasons.get(position - 1);
                            PackageItem item = items.get(holder.getAdapterPosition());
                            selectedItemsCancellationReason.put(item.getId(), reason.getId());
                            notifyItemChanged(holder.getAdapterPosition(),
                                    new CancellationReasonChangePayload(
                                            CancellationReasonChangePayload.REASON_SELECTED));
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
        if (holder.checkBox.isChecked()) {
            holder.spinnerCancellationReasons.setVisibility(View.VISIBLE);
        } else {
            holder.spinnerCancellationReasons.setVisibility(View.GONE);
        }
        if (selectedCount == null) {
            selectedCount = 1;
        }
        selectedCount = Math.abs(selectedCount);

        if (!item.getCancellation().isCancelable()) {
            holder.tvProductQuantity.setText(String.valueOf(item.getQuantity()));
        } else {
            holder.tvProductQuantity.setText(String.valueOf(selectedCount));
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                PackageItem item = items.get(holder.getAdapterPosition());
                Integer count = selectedItemsCount.get(item.getId());
                if (count == null) {
                    count = 1;
                }
                if (checked) {
                    count = Math.abs(count);
                } else {
                    count = -Math.abs(count);
                }
                selectedItemsCount.put(item.getId(), count);
                notifyItemChanged(holder.getAdapterPosition(),
                        selectedItemsCount.get(item.getId()));
            }
        });
        if (Math.abs(selectedCount) < item.getQuantity()) {
            holder.imgQuantityPlus.setEnabled(true);
        } else {
            holder.imgQuantityPlus.setEnabled(false);
        }
        if (Math.abs(selectedCount) > 1) {
            holder.imgQuantityMinus.setEnabled(true);
        } else {
            holder.imgQuantityMinus.setEnabled(false);
        }
        View.OnClickListener onQuantityChangeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageItem item = items.get(holder.getAdapterPosition());
                Integer count = selectedItemsCount.get(item.getId());
                boolean unselected = true;
                if (count == null) {
                    count = 1;
                } else if (count > 0) {
                    unselected = false;
                }
                count = Math.abs(count);
                if (view.getId() == R.id.imgNumberPickerPlus) {
                    if (count < item.getQuantity()) {
                        ++count;
                    }
                } else {
                    if (count > 1) {
                        --count;
                    }
                }
                selectedItemsCount
                        .put(item.getId(), unselected ? -Math.abs(count) : Math.abs(count));
                notifyItemChanged(holder.getAdapterPosition(),
                        selectedItemsCount.get(item.getId()));
            }
        };
        holder.imgQuantityPlus.setOnClickListener(onQuantityChangeClickListener);
        holder.imgQuantityMinus.setOnClickListener(onQuantityChangeClickListener);
        holder.tvProductName.setText(item.getName());
        if (TextUtils.isNotEmpty(item.getImage())) {
            try {
                ImageManager.getInstance().loadImage(item.getImage(), holder.imgProductThumb, null,
                        R.drawable.no_image_large, false);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public int getItemCount() {
        if (CollectionUtils.isNotEmpty(items)) {
            return items.size();
        }
        return 0;
    }

    public List<String> getCancelingItemsList() {
        List<String> result = new ArrayList<>();
        for (String key : selectedItemsCount.keySet()) {
            Integer count = selectedItemsCount.get(key);
            if (count != null && count > 0) {
                result.add(key);
            }
        }
        return result;
    }

    public Integer getCancelingItemQuantity(String itemId) {
        Integer count = selectedItemsCount.get(itemId);
        if (count != null && count > 0) {
            return count;
        }
        return 0;
    }

    public String getSelectedItemCancellationReason(String itemId) {
        String reason = selectedItemsCancellationReason.get(itemId);
        if (TextUtils.isNotEmpty(reason)) {
            return reason;
        }
        return null;
    }

    private void notifyItemToSelectCancellationReason(int position) {
        notifyItemChanged(position, new CancellationReasonChangePayload(
                CancellationReasonChangePayload.REASON_NOT_SELECTED));
    }

    public void notifyItemToSelectCancellationReason(String itemId) {
        int i = 0;
        for (PackageItem item : items) {
            if (item.getId().equals(itemId)) {
                notifyItemToSelectCancellationReason(i);
                break;
            }
            i++;
        }
    }

    public PackageItem getPackageItemByItemId(String itemId) {
        if (TextUtils.isNotEmpty(itemId)) {
            for (PackageItem packageItem : items) {
                if (packageItem.getId().equals(itemId)) {
                    return packageItem;
                }
            }
        }
        return null;
    }

    public static class OrderCancellationListViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        ImageView imgProductThumb;
        TextView tvProductName, tvProductQuantity,
                tvCancellationReasonError, tvNotCancelableReason;
        Spinner spinnerCancellationReasons;
        ImageView imgQuantityPlus, imgQuantityMinus;
        View viewNotCancelable;

        public OrderCancellationListViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            imgProductThumb = (ImageView) itemView.findViewById(R.id.imgProductThumb);
            tvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
            tvProductQuantity = (TextView) itemView.findViewById(R.id.tvProductQuantity);
            tvCancellationReasonError = (TextView) itemView
                    .findViewById(R.id.tvCancellationReasonError);
            tvNotCancelableReason = (TextView) itemView.findViewById(R.id.tvNotCancelableReason);
            spinnerCancellationReasons = (Spinner) itemView
                    .findViewById(R.id.spinnerCancellationReason);
            imgQuantityPlus = (ImageView) itemView.findViewById(R.id.imgNumberPickerPlus);
            imgQuantityMinus = (ImageView) itemView.findViewById(R.id.imgNumberPickerMinus);
            viewNotCancelable = itemView.findViewById(R.id.viewNotCancelable);
        }
    }

    static class CancellationReasonChangePayload {

        public static final int REASON_NOT_SELECTED = 0, REASON_SELECTED = 1;
        private int option;

        public CancellationReasonChangePayload(int option) {
            this.option = option;
        }

        public int getOption() {
            return option;
        }

        public void setOption(int option) {
            this.option = option;
        }
    }
}
