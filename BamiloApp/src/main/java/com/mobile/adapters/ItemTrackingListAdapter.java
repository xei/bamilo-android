package com.mobile.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobile.service.objects.orders.Package;
import com.mobile.service.objects.orders.PackageItem;
import com.mobile.service.objects.orders.PackagedOrder;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.output.Print;
import com.mobile.service.utils.shop.CurrencyFormatter;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created on 10/28/2017.
 */

public class ItemTrackingListAdapter extends RecyclerView.Adapter<ItemTrackingListAdapter.ItemTrackingViewHolder> {
    private static final int ITEM_LIST_HEADER = 1, ITEM_SECTION_HEADER = 2, ITEM_ORDER_ITEM = 3,
            ITEM_LIST_FOOTER = 4, ITEM_CMS_MESSAGE = 5;
    private PackagedOrder packagedOrder;
    private List<Integer> headerPositions;
    private HashMap<Integer, PackageItem> indexedItems;
    private HashMap<Integer, Boolean> itemsReviewButtonVisibility;
    private int count;
    private OnItemTrackingListClickListener onItemTrackingListClickListener;

    public ItemTrackingListAdapter(PackagedOrder packagedOrder) {
        this.packagedOrder = packagedOrder;
        calculateItemCount(packagedOrder);
    }

    private void calculateItemCount(PackagedOrder packagedOrder) {
        headerPositions = new ArrayList<>();
        indexedItems = new HashMap<>();
        itemsReviewButtonVisibility = new HashMap<>();
        int count = 0;
        if (TextUtils.isNotEmpty(packagedOrder.getCms().trim())) {
            count++; // cms message item
        }
        count++; // header item
        for (Package p : packagedOrder.getPackages()) {
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

    @Override
    public ItemTrackingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        return new ItemTrackingViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(final ItemTrackingViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        Locale locale = new Locale("fa", "ir");
        Context context = holder.itemView.getContext();
        if (viewType == ITEM_LIST_HEADER) {
            holder.tvOrderNumberValue.setText(packagedOrder.getOrderId());
            holder.tvOrderCostValue.setText(CurrencyFormatter.formatCurrency(packagedOrder.getTotalCost()));
            holder.tvOrderDateValue.setText(packagedOrder.getCreationDate());
            holder.tvOrderQuantityValue.setText(String.format(locale, "%d %s",
                    packagedOrder.getProductsCount(),
                    context.getString(R.string.product_quantity_unit)));
        } else if (viewType == ITEM_CMS_MESSAGE) {
            if (TextUtils.isNotEmpty(packagedOrder.getCms().trim())) {
                holder.rlCMSMessage.setVisibility(View.VISIBLE);
                holder.tvCMSMessage.setText(packagedOrder.getCms());
            } else {
                holder.rlCMSMessage.setVisibility(View.GONE);
            }
        } else if (viewType == ITEM_LIST_FOOTER) {
            holder.tvRecipientValue.setText(String.format(locale, "%s %s", packagedOrder.getCustomerFirstName(), packagedOrder.getCustomerLastName()));
            holder.tvDeliveryAddressValue.setText(packagedOrder.getShippingAddress().getAddress());
            holder.tvShipmentCostValue.setText(packagedOrder.getDeliveryCost() == 0 ? context.getString(R.string.free_label) :
                    CurrencyFormatter.formatCurrency(packagedOrder.getDeliveryCost()));
            holder.tvPaymentMethodValue.setText(packagedOrder.getPaymentMethodName());
        } else if (viewType == ITEM_SECTION_HEADER) {
            int index = headerPositions.indexOf(position);
            Package p = packagedOrder.getPackages().get(index);
            holder.tvPackageTitle.setText(p.getTitle());
            holder.tvPackageDeliveryTime.setText(p.getCalculatedDeliveryTime());
        } else if (viewType == ITEM_ORDER_ITEM) {
            PackageItem item = indexedItems.get(position);
            holder.tvProductName.setText(item.getName());
            holder.tvProductPrice.setText(CurrencyFormatter.formatCurrency(item.getPrice()));
            holder.imgProductThumb.setOnClickListener(null);
            if (item.getImage() != null) {
                try {
                    holder.imgProductThumb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onItemTrackingListClickListener != null) {
                                PackageItem item = indexedItems.get(holder.getAdapterPosition());
                                onItemTrackingListClickListener.onProductThumbClickListener(v, item);
                            }
                        }
                    });
                    ImageManager.getInstance().loadImage(item.getImage(), holder.imgProductThumb, null, R.drawable.no_image_large, false);
                } catch (Exception e) {
                    Print.d(e.getMessage());
                }
            }
            String propertyFormat = "%s : %s\n";
            StringBuilder productProperties = new StringBuilder();
            productProperties.append(String.format(locale, "%s : %d\n", context.getString(R.string.quantity_label), item.getQuantity()));
            productProperties.append(String.format(locale, propertyFormat, context.getString(R.string.brand_label), item.getBrand()));
            productProperties.append(String.format(locale, propertyFormat, context.getString(R.string.seller_label), item.getSeller()));
            productProperties.append(String.format(locale, propertyFormat, context.getString(R.string.fee_label), CurrencyFormatter.formatCurrency(item.getPrice())));
            if (TextUtils.isNotEmpty(item.getColor().trim())) {
                productProperties.append(String.format(locale, propertyFormat, context.getString(R.string.color_label), item.getColor()));
            }
            if (TextUtils.isNotEmpty(item.getSize().trim())) {
                productProperties.append(String.format(locale, propertyFormat, context.getString(R.string.size_label), item.getSize()));
            }
            holder.tvProductDetails.setText(productProperties.substring(0, productProperties.length() - 1));

            Boolean visibility = itemsReviewButtonVisibility.get(holder.getAdapterPosition());
            if (visibility != null && visibility) {
                holder.imgArrowSeeMore.animate()
                        .rotation(180)
                        .setDuration(1)
                        .start();
                holder.tvProductDetails.setVisibility(View.VISIBLE);
                holder.btnReviewProduct.setVisibility(View.VISIBLE);
                if (item.getSku() == null) {
                    holder.tvItemIsOutOfStock.setVisibility(View.VISIBLE);
                    holder.btnReviewProduct.setEnabled(false);
                } else {
                    holder.tvItemIsOutOfStock.setVisibility(View.GONE);
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

            View.OnClickListener onCollapseClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean visibility = itemsReviewButtonVisibility.get(holder.getAdapterPosition());
                    if (visibility == null || !visibility) {
                        itemsReviewButtonVisibility.put(holder.getAdapterPosition(), true);
                    } else {
                        itemsReviewButtonVisibility.put(holder.getAdapterPosition(), false);
                    }
                    notifyItemChanged(holder.getAdapterPosition());
                }
            };
            holder.clPackagedOrderItem.setOnClickListener(onCollapseClickListener);
            holder.tvProductName.setOnClickListener(onCollapseClickListener);
            holder.imgArrowSeeMore.setOnClickListener(onCollapseClickListener);
            holder.btnReviewProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemTrackingListClickListener != null) {
                        PackageItem item = indexedItems.get(holder.getAdapterPosition());
                        onItemTrackingListClickListener.onReviewButtonClicked(v, item);
                    }
                }
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
            if (TextUtils.isNotEmpty(packagedOrder.getCms().trim())) {
                return ITEM_CMS_MESSAGE;
            } else {
                return ITEM_LIST_HEADER;
            }
        } else if (position == 1 && TextUtils.isNotEmpty(packagedOrder.getCms().trim())) {
            return ITEM_LIST_HEADER;
        } else if (position == count - 1) {
            return ITEM_LIST_FOOTER;
        } else if (headerPositions.contains(position)) {
            return ITEM_SECTION_HEADER;
        } else {
            return ITEM_ORDER_ITEM;
        }
    }

    public PackagedOrder getPackagedOrder() {
        return packagedOrder;
    }

    public void setPackagedOrder(PackagedOrder packagedOrder) {
        this.packagedOrder = packagedOrder;
        calculateItemCount(packagedOrder);
    }

    public OnItemTrackingListClickListener getOnItemTrackingListClickListener() {
        return onItemTrackingListClickListener;
    }

    public void setOnItemTrackingListClickListener(OnItemTrackingListClickListener onItemTrackingListClickListener) {
        this.onItemTrackingListClickListener = onItemTrackingListClickListener;
    }

    public static class ItemTrackingViewHolder extends RecyclerView.ViewHolder {
        // cms message item
        TextView tvCMSMessage;
        RelativeLayout rlCMSMessage;

        // header item
        TextView tvOrderNumberValue, tvOrderCostValue, tvOrderDateValue, tvOrderQuantityValue;

        // footer item
        TextView tvRecipientValue, tvDeliveryAddressValue, tvShipmentCostValue, tvPaymentMethodValue;


        // package section header
        TextView tvPackageTitle, tvPackageDeliveryTime;

        // order item
        ConstraintLayout clPackagedOrderItem;
        ImageView imgProductThumb, imgArrowSeeMore;
        TextView tvProductName, tvProductPrice;
        TextView tvProductDetails, tvItemIsOutOfStock;
        Button btnReviewProduct;


        public ItemTrackingViewHolder(View itemView) {
            super(itemView);

            tvCMSMessage = (TextView) itemView.findViewById(R.id.tvCMSMessage);
            rlCMSMessage = (RelativeLayout) itemView.findViewById(R.id.rlCMSMessage);

            tvOrderNumberValue = (TextView) itemView.findViewById(R.id.tvOrderNumberValue);
            tvOrderCostValue = (TextView) itemView.findViewById(R.id.tvOrderCostValue);
            tvOrderDateValue = (TextView) itemView.findViewById(R.id.tvOrderDateValue);
            tvOrderQuantityValue = (TextView) itemView.findViewById(R.id.tvOrderQuantityValue);

            tvRecipientValue = (TextView) itemView.findViewById(R.id.tvRecipientValue);
            tvDeliveryAddressValue = (TextView) itemView.findViewById(R.id.tvDeliveryAddressValue);
            tvShipmentCostValue = (TextView) itemView.findViewById(R.id.tvShipmentCostValue);
            tvPaymentMethodValue = (TextView) itemView.findViewById(R.id.tvPaymentMethodValue);

            tvPackageTitle = (TextView) itemView.findViewById(R.id.tvPackageTitle);
            tvPackageDeliveryTime = (TextView) itemView.findViewById(R.id.tvPackageDeliveryTime);

            clPackagedOrderItem = (ConstraintLayout) itemView.findViewById(R.id.clPackagedOrderItem);
            imgProductThumb = (ImageView) itemView.findViewById(R.id.imgProductThumb);
            imgArrowSeeMore = (ImageView) itemView.findViewById(R.id.imgArrowSeeMore);
            tvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
            tvProductPrice = (TextView) itemView.findViewById(R.id.tvProductPrice);
            tvProductDetails = (TextView) itemView.findViewById(R.id.tvProductDetails);
            tvItemIsOutOfStock = (TextView) itemView.findViewById(R.id.tvItemIsOutOfStockMsg);
            btnReviewProduct = (Button) itemView.findViewById(R.id.btnReviewProduct);
        }
    }

    public interface OnItemTrackingListClickListener {
        void onReviewButtonClicked(View v, PackageItem item);

        void onProductThumbClickListener(View v, PackageItem item);
    }
}
