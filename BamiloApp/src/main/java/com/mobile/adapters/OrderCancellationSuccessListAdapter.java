package com.mobile.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.service.utils.TextUtils;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;

import java.util.List;
import java.util.Locale;

/**
 * Created by mohsen on 2/3/18.
 */

public class OrderCancellationSuccessListAdapter extends RecyclerView.Adapter<OrderCancellationSuccessListAdapter.SuccessListViewHolder> {

    private List<CanceledProductItem> productItems;

    public OrderCancellationSuccessListAdapter(List<CanceledProductItem> productItems) {
        this.productItems = productItems;
    }

    @Override
    public SuccessListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SuccessListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cancellation_success_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SuccessListViewHolder holder, int position) {
        CanceledProductItem item = productItems.get(position);
        if (TextUtils.isNotEmpty(item.getImage())) {
            try {
                ImageManager.getInstance().loadImage(item.getImage(), holder.imgProductThumb, null,
                        R.drawable.no_image_large, false);
            } catch (Exception ignored) {
            }
        }
        holder.tvProductName.setText(item.getName());
        Context context = holder.itemView.getContext();
        holder.tvProductQuantity.setText(String.format(Locale.getDefault(), "%s %d",
                context.getString(R.string.order_cancellation_item_quantity_label),
                item.getQuantity()));
    }

    @Override
    public int getItemCount() {
        if (productItems == null) {
            return 0;
        }
        return productItems.size();
    }

    public List<CanceledProductItem> getProductItems() {
        return productItems;
    }

    public void setProductItems(List<CanceledProductItem> productItems) {
        this.productItems = productItems;
    }

    public static class SuccessListViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProductThumb;
        TextView tvProductName, tvProductQuantity;

        public SuccessListViewHolder(View itemView) {
            super(itemView);
            imgProductThumb = (ImageView) itemView.findViewById(R.id.imgProductThumb);
            tvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
            tvProductQuantity = (TextView) itemView.findViewById(R.id.tvProductQuantity);
        }
    }

    public static class CanceledProductItem {
        private String sku;
        private String image;
        private String name;
        private long quantity;

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getQuantity() {
            return quantity;
        }

        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }
    }
}
