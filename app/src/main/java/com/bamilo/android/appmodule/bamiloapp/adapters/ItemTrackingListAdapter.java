package com.bamilo.android.appmodule.bamiloapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.WarningFactory;
import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity;
import com.bamilo.android.appmodule.bamiloapp.view.widget.ItemTrackingProgressBar;
import com.bamilo.android.appmodule.modernbamilo.product.comment.submit.SubmitRateScreenKt;
import com.bamilo.android.appmodule.modernbamilo.util.extension.StringExtKt;
import com.bamilo.android.core.service.model.data.itemtracking.Cancellation;
import com.bamilo.android.core.service.model.data.itemtracking.CompleteOrder;
import com.bamilo.android.core.service.model.data.itemtracking.History;
import com.bamilo.android.core.service.model.data.itemtracking.Package;
import com.bamilo.android.core.service.model.data.itemtracking.PackageItem;
import com.bamilo.android.core.service.model.data.itemtracking.Refund;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created on 10/28/2017.
 */

public class ItemTrackingListAdapter extends
        RecyclerView.Adapter<ItemTrackingListAdapter.ItemTrackingViewHolder> {

    private static final int ITEM_LIST_HEADER = 1, ITEM_SECTION_HEADER = 2, ITEM_ORDER_ITEM = 3,
            ITEM_LIST_FOOTER = 4, ITEM_CMS_MESSAGE = 5;
    private boolean cancellationEnabled;
    private CompleteOrder completeOrder;
    private List<Integer> headerPositions;
    private HashMap<Integer, PackageItem> indexedItems;
    private HashMap<Integer, Boolean> itemsReviewButtonVisibility;
    private int count;
    private OnItemTrackingListClickListener onItemTrackingListClickListener;

    public ItemTrackingListAdapter(CompleteOrder completeOrder, boolean cancellationEnabled) {
        this.cancellationEnabled = cancellationEnabled;
        this.completeOrder = completeOrder;
        calculateItemCount(completeOrder);
    }

    @SuppressLint("UseSparseArrays")
    private void calculateItemCount(CompleteOrder completeOrder) {
        headerPositions = new ArrayList<>();
        indexedItems = new HashMap<>();
        itemsReviewButtonVisibility = new HashMap<>();
        int count = 0;
        if (TextUtils.isNotEmpty(completeOrder.getCms())) {
            count++; // cms message item
        }
        count++; // header item
        for (Package p : completeOrder.getPackages()) {
            // index of package title item
            headerPositions.add(count);
            count++; // package title item
            for (int index = 0; index < p.getPackageItems().size(); index++) {
                indexedItems.put(count + index, p.getPackageItems().get(index));
            }
            count += p.getPackageItems().size();
        }
        count++; // footer item
        this.count = count;
    }

    @NonNull
    @Override
    public ItemTrackingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @LayoutRes int layoutId = R.layout.row_item_tracking_list_header;
        if (viewType == ITEM_SECTION_HEADER) {
            layoutId = R.layout.row_package_section_header;
        } else if (viewType == ITEM_ORDER_ITEM) {
            layoutId = R.layout.row_package_order_item;
        } else if (viewType == ITEM_LIST_FOOTER) {
            layoutId = R.layout.row_item_tracking_list_footer;
        } else if (viewType == ITEM_CMS_MESSAGE) {
            layoutId = R.layout.row_item_tracking_cms_message;
        }
        return new ItemTrackingViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemTrackingViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        Locale locale = new Locale("fa", "ir");
        final Context context = holder.itemView.getContext();
        if (viewType == ITEM_LIST_HEADER) {
            holder.tvOrderNumberValue
                    .setText(StringExtKt.persianizeNumberString(completeOrder.getOrderNumber()));
            holder.tvOrderCostValue.setText(StringExtKt.persianizeDigitsInString(
                    CurrencyFormatter.formatCurrency(completeOrder.getGrandTotal())));
            holder.tvOrderDateValue.setText(completeOrder.getCreationDate());
            holder.tvOrderQuantityValue.setText(String.format(locale, "%d %s",
                    completeOrder.getTotalProductsCount(),
                    context.getString(R.string.product_quantity_unit)));
        } else if (viewType == ITEM_CMS_MESSAGE) {
            if (TextUtils.isNotEmpty(completeOrder.getCms())) {
                holder.rlCMSMessage.setVisibility(View.VISIBLE);
                holder.tvCMSMessage.setText(completeOrder.getCms());
            } else {
                holder.rlCMSMessage.setVisibility(View.GONE);
            }
        } else if (viewType == ITEM_LIST_FOOTER) {
            holder.tvRecipientValue.setText(String.format(locale, "%s %s",
                    completeOrder.getShippingAddress().getFirstName(),
                    completeOrder.getShippingAddress().getLastName()));
            holder.tvDeliveryAddressValue.setText(completeOrder.getShippingAddress().getAddress1());
            holder.tvShipmentCostValue.setText(StringExtKt.persianizeDigitsInString(
                    completeOrder.getPayment().getDeliveryCost() == 0 ? context
                            .getString(R.string.free_label) :
                            CurrencyFormatter
                                    .formatCurrency(completeOrder.getPayment().getDeliveryCost())));
            holder.tvPaymentMethodValue.setText(completeOrder.getPayment().getMethod());
        } else if (viewType == ITEM_SECTION_HEADER) {
            int index = headerPositions.indexOf(position);
            Package p = completeOrder.getPackages().get(index);
            holder.tvPackageTitle.setText(p.getTitle());
            if (!(p.getDelay() != null && p.getDelay().hasDelay()) && TextUtils
                    .isNotEmpty(p.getDeliveryTime())) {
                holder.tvPackageDeliveryTime.setText(
                        String.format(locale, "%s %s",
                                context.getString(R.string.pdv_delivery_time),
                                p.getDeliveryTime()));
            }
            if (p.getDelay() != null && TextUtils.isNotEmpty(p.getDelay().getReason())) {
                holder.tvPackageDeliveryDelayReason.setVisibility(View.VISIBLE);
                holder.tvPackageDeliveryDelayReason.setText(p.getDelay().getReason());
            } else {
                holder.tvPackageDeliveryDelayReason.setVisibility(View.GONE);
            }

            if (p.getDeliveryType() != null
                    && p.getDeliveryType().getDropShipDescription() != null
                    && p.getDeliveryType().getDropShipDescription().length() != 0) {
                holder.dropShipDescriptionTextView
                        .setText(p.getDeliveryType().getDropShipDescription());
                holder.dropShipDescriptionTextView.setVisibility(View.VISIBLE);
            } else {
                holder.dropShipDescriptionTextView.setVisibility(View.GONE);
            }
        } else if (viewType == ITEM_ORDER_ITEM) {
            final PackageItem item = indexedItems.get(position);
            ItemTrackingProgressBar itemTrackingProgressBar = holder.itemTrackingProgressBar;
            itemTrackingProgressBar.setItemHistories(item.getHistories());

            // Refund and Cancellation Reason
            if (item.getRefund() != null) {
                holder.rlCancellationReason.setVisibility(View.VISIBLE);
                Refund refund = item.getRefund();
                if (TextUtils.isNotEmpty(refund.getCancellationReason())) {
                    holder.tvItemCancellationReason.setVisibility(View.VISIBLE);
                    holder.tvItemCancellationReason
                            .setText(context.getString(R.string.item_tracking_cancellation_reason,
                                    refund.getCancellationReason()));
                } else {
                    holder.tvItemCancellationReason.setVisibility(View.GONE);
                }
                if (TextUtils.isNotEmpty(refund.getStatus())) {
                    holder.tvRefundMessage.setVisibility(View.VISIBLE);
                    holder.imgRefundStatus.setVisibility(View.VISIBLE);
                    holder.imgRefundStatus.setImageResource(
                            refund.getStatus().equals(Refund.STATUS_PENDING) ?
                                    R.drawable.ic_refund_pending :
                                    R.drawable.ic_refund_success);
                    String refundMessage = context.getString(R.string.item_tracking_refund_message,
                            TextUtils.isNotEmpty(refund.getCardNumber()) ? refund.getCardNumber()
                                    : context.getString(
                                            R.string.item_tracking_refund_used_card_number),
                            TextUtils.isNotEmpty(refund.getDate()) ? context
                                    .getString(R.string.item_tracking_refund_date, refund.getDate())
                                    : "",
                            context.getString(refund.getStatus().equals(Refund.STATUS_PENDING)
                                    ? R.string.item_tracking_refund_will_be_returned
                                    : R.string.item_tracking_refund_is_returned));
                    holder.tvRefundMessage.setText(refundMessage);
                } else {
                    holder.imgRefundStatus.setVisibility(View.GONE);
                    holder.tvRefundMessage.setVisibility(View.GONE);
                }
            } else {
                holder.rlCancellationReason.setVisibility(View.INVISIBLE);
            }

            holder.tvProductName.setText(item.getName());
            holder.tvProductPrice.setText(StringExtKt
                    .persianizeDigitsInString(CurrencyFormatter.formatCurrency(item.getPrice())));
            holder.imgProductThumb.setOnClickListener(null);
            if (cancellationEnabled && item.getCancellation() != null) {
                if (item.getCancellation().isCancelable()) {
                    holder.btnCancelItem.setVisibility(View.VISIBLE);
                    holder.btnCancelItem.setOnClickListener(view -> {
                        if (onItemTrackingListClickListener != null) {
                            PackageItem item1 = indexedItems.get(holder.getAdapterPosition());
                            onItemTrackingListClickListener
                                    .onCancelItemButtonClicked(view, item1);
                        }
                    });
                } else if (item.getCancellation().getNotCancelableReasonType() != null &&
                        !item.getCancellation().getNotCancelableReasonType()
                                .equals(Cancellation.REASON_TYPE_IS_CANCELED)
                        && CollectionUtils.isNotEmpty(item.getHistories()) &&
                        (item.getHistories().get(item.getHistories().size() - 1).getStatus()
                                .equals(History.STATUS_INACTIVE) ||
                                item.getHistories().get(item.getHistories().size() - 1).getStatus()
                                        .equals(History.STATUS_ACTIVE))) {
                    holder.btnCancelItem.setVisibility(View.VISIBLE);
                    holder.btnCancelItem.setOnClickListener(view -> {
                        String message;
                        switch (item.getCancellation().getNotCancelableReasonType()) {
                            case Cancellation.REASON_TYPE_HAS_CANCELLATION_REQUEST:
                                message = context.getString(
                                        R.string.order_cancellation_item_has_cancellation_error);
                                break;
                            case Cancellation.REASON_TYPE_IS_SHIPPED:
                                message = context
                                        .getString(R.string.order_cancellation_item_shipped_error);
                                break;
                            default:
                                message = context.getString(
                                        R.string.order_cancellation_item_is_not_cancelable);
                                break;
                        }
                        if (context instanceof BaseActivity) {
                            ((BaseActivity) context)
                                    .showWarningMessage(WarningFactory.ERROR_MESSAGE, message);
                        }
                    });
                } else {
                    holder.btnCancelItem.setVisibility(View.GONE);
                }
            }
            if (TextUtils.isNotEmpty(item.getImage())) {
                try {
                    holder.imgProductThumb.setOnClickListener(v -> {
                        if (onItemTrackingListClickListener != null) {
                            PackageItem item12 = indexedItems.get(holder.getAdapterPosition());
                            onItemTrackingListClickListener
                                    .onProductThumbClickListener(v, item12);
                        }
                    });
                    ImageManager.getInstance()
                            .loadImage(item.getImage(), holder.imgProductThumb, null,
                                    R.drawable.no_image_large, false);
                } catch (Exception e) {
                }
            }
            String propertyFormat = "%s : %s\n";
            StringBuilder productProperties = new StringBuilder();
            if (item.getQuantity() != 0) {
                productProperties.append(String
                        .format(locale, "%s : %d\n", context.getString(R.string.quantity_label),
                                item.getQuantity()));
            }
            if (TextUtils.isNotEmpty(item.getBrand())) {
                productProperties.append(String
                        .format(locale, propertyFormat, context.getString(R.string.brand_label),
                                item.getBrand()));
            }
            if (TextUtils.isNotEmpty(item.getSeller())) {
                productProperties.append(String
                        .format(locale, propertyFormat, context.getString(R.string.seller_label),
                                item.getSeller()));
            }
            if (item.getPrice() != 0) {
                productProperties.append(String
                        .format(locale, propertyFormat, context.getString(R.string.fee_label),
                                CurrencyFormatter.formatCurrency(item.getPrice())));
            }
            if (TextUtils.isNotEmpty(item.getFilters().getColor())) {
                productProperties.append(String
                        .format(locale, propertyFormat, context.getString(R.string.color_label),
                                item.getFilters().getColor()));
            }
            if (TextUtils.isNotEmpty(item.getFilters().getSize())) {
                productProperties.append(String
                        .format(locale, propertyFormat, context.getString(R.string.size_label),
                                item.getFilters().getSize()));
            }
            holder.tvProductDetails
                    .setText(StringExtKt.persianizeDigitsInString(
                            productProperties.substring(0, productProperties.length() - 1)));

            Boolean visibility = itemsReviewButtonVisibility.get(holder.getAdapterPosition());
            if (visibility != null && visibility) {
                holder.imgArrowSeeMore.animate()
                        .rotation(180)
                        .setDuration(1)
                        .start();
                holder.tvProductDetails.setVisibility(View.VISIBLE);
                holder.btnReviewProduct.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(item.getSku())) {
                    holder.tvItemIsOutOfStock.setVisibility(View.VISIBLE);
                    holder.btnReviewProduct.setVisibility(View.GONE);
                    holder.btnReviewProduct.setEnabled(false);
                } else {
                    holder.tvItemIsOutOfStock.setVisibility(View.GONE);
                    holder.btnReviewProduct.setVisibility(View.VISIBLE);
                    holder.btnReviewProduct.setEnabled(true);
                }
            } else {
                holder.imgArrowSeeMore.animate()
                        .rotation(0)
                        .setDuration(1)
                        .start();
                holder.tvProductDetails.setVisibility(View.GONE);
                holder.btnReviewProduct.setVisibility(View.GONE);
                holder.tvItemIsOutOfStock.setVisibility(View.GONE);
            }

            View.OnClickListener onCollapseClickListener = v -> {
                Boolean visibility1 = itemsReviewButtonVisibility
                        .get(holder.getAdapterPosition());
                if (visibility1 == null || !visibility1) {
                    itemsReviewButtonVisibility.put(holder.getAdapterPosition(), true);
                } else {
                    itemsReviewButtonVisibility.put(holder.getAdapterPosition(), false);
                }
                notifyItemChanged(holder.getAdapterPosition());
            };
            holder.clPackagedOrderItem.setOnClickListener(onCollapseClickListener);
            holder.tvProductName.setOnClickListener(onCollapseClickListener);
            holder.imgArrowSeeMore.setOnClickListener(onCollapseClickListener);
            holder.btnReviewProduct.setOnClickListener(v -> {
                PackageItem item13 = indexedItems.get(holder.getAdapterPosition());
                SubmitRateScreenKt.startSubmitRateActivity(context, item13.getSku());
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.count;
    }


    @Override
    public int getItemViewType(int position) {
        int count = getItemCount();
        if (position == 0) {
            if (TextUtils.isNotEmpty(completeOrder.getCms())) {
                return ITEM_CMS_MESSAGE;
            } else {
                return ITEM_LIST_HEADER;
            }
        } else if (position == 1 && TextUtils.isNotEmpty(completeOrder.getCms())) {
            return ITEM_LIST_HEADER;
        } else if (position == count - 1) {
            return ITEM_LIST_FOOTER;
        } else if (headerPositions.contains(position)) {
            return ITEM_SECTION_HEADER;
        } else {
            return ITEM_ORDER_ITEM;
        }
    }

    public CompleteOrder getCompleteOrder() {
        return completeOrder;
    }

    public void setOnItemTrackingListClickListener(
            OnItemTrackingListClickListener onItemTrackingListClickListener) {
        this.onItemTrackingListClickListener = onItemTrackingListClickListener;
    }

    static class ItemTrackingViewHolder extends RecyclerView.ViewHolder {

        // cms message item
        TextView tvCMSMessage;
        RelativeLayout rlCMSMessage;

        // header item
        TextView tvOrderNumberValue, tvOrderCostValue, tvOrderDateValue, tvOrderQuantityValue;
        private TextView dropShipDescriptionTextView;

        // footer item
        TextView tvRecipientValue, tvDeliveryAddressValue, tvShipmentCostValue, tvPaymentMethodValue;


        // package section header
        TextView tvPackageTitle, tvPackageDeliveryTime, tvPackageDeliveryDelayReason;

        // order item
        ConstraintLayout clPackagedOrderItem;
        ImageView imgProductThumb, imgArrowSeeMore;
        TextView tvProductName, tvProductPrice;
        TextView tvProductDetails, tvItemIsOutOfStock;
        Button btnReviewProduct, btnCancelItem;
        ItemTrackingProgressBar itemTrackingProgressBar;
        RelativeLayout rlCancellationReason;
        TextView tvItemCancellationReason, tvRefundMessage;
        ImageView imgRefundStatus;

        ItemTrackingViewHolder(View itemView) {
            super(itemView);

            tvCMSMessage = itemView.findViewById(R.id.tvCMSMessage);
            rlCMSMessage = itemView.findViewById(R.id.rlCMSMessage);

            tvOrderNumberValue = itemView.findViewById(R.id.tvOrderNumberValue);
            tvOrderCostValue = itemView.findViewById(R.id.tvOrderCostValue);
            tvOrderDateValue = itemView.findViewById(R.id.tvOrderDateValue);
            tvOrderQuantityValue = itemView.findViewById(R.id.tvOrderQuantityValue);

            tvRecipientValue = itemView.findViewById(R.id.tvRecipientValue);
            tvDeliveryAddressValue = itemView.findViewById(R.id.tvDeliveryAddressValue);
            tvShipmentCostValue = itemView.findViewById(R.id.tvShipmentCostValue);
            tvPaymentMethodValue = itemView.findViewById(R.id.tvPaymentMethodValue);

            tvPackageTitle = itemView.findViewById(R.id.tvPackageTitle);
            tvPackageDeliveryTime = itemView.findViewById(R.id.tvPackageDeliveryTime);
            tvPackageDeliveryDelayReason = itemView
                    .findViewById(R.id.tvPackageDeliveryDelayReason);

            dropShipDescriptionTextView = itemView
                    .findViewById(R.id.rowPackageSectionHeader_xeiTextView_dropShipMessage);

            clPackagedOrderItem = itemView
                    .findViewById(R.id.clPackagedOrderItem);
            itemTrackingProgressBar = itemView
                    .findViewById(R.id.itemTrackingProgressBar);
            rlCancellationReason = itemView
                    .findViewById(R.id.rlCancellationReason);
            tvItemCancellationReason = itemView
                    .findViewById(R.id.tvItemCancellationReason);
            tvRefundMessage = itemView.findViewById(R.id.tvRefundMessage);
            imgRefundStatus = itemView.findViewById(R.id.imgRefundStatus);
            imgProductThumb = itemView.findViewById(R.id.imgProductThumb);
            imgArrowSeeMore = itemView.findViewById(R.id.imgArrowSeeMore);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductDetails = itemView.findViewById(R.id.tvProductDetails);
            tvItemIsOutOfStock = itemView.findViewById(R.id.tvItemIsOutOfStockMsg);
            btnReviewProduct = itemView.findViewById(R.id.btnReviewProduct);
            btnCancelItem = itemView.findViewById(R.id.btnCancelItem);
        }
    }

    public interface OnItemTrackingListClickListener {

        void onCancelItemButtonClicked(View v, PackageItem item);

        void onProductThumbClickListener(View v, PackageItem item);
    }
}
