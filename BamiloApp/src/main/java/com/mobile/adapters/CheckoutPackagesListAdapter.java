package com.mobile.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.service.objects.cart.PurchaseCartItem;
import com.mobile.service.objects.checkout.CartPackage;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.shop.CurrencyFormatter;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by mohsen on 3/12/18.
 */

public class CheckoutPackagesListAdapter extends RecyclerView.Adapter<CheckoutPackagesListAdapter.CheckoutPackagesListViewHolder> {
    private static final int ITEM_SECTION_HEADER = 2, ITEM_ORDER_ITEM = 3;

    private List<CartPackage> cartPackages;
    private ArrayList<Integer> headerPositions;
    private HashMap<Integer, PurchaseCartItem> indexedItems;
    private int count;
    private Locale mLocale = new Locale("fa");

    public CheckoutPackagesListAdapter(List<CartPackage> cartPackages) {
        this.cartPackages = cartPackages;
        calculateItemCount(cartPackages);
    }

    @Override
    public CheckoutPackagesListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        @LayoutRes int layoutId;
        if (viewType == ITEM_SECTION_HEADER) {
            layoutId = R.layout.row_package_section_header;
        } else {
            layoutId = R.layout._def_checkout_confirmation_product;
        }
        return new CheckoutPackagesListViewHolder(LayoutInflater
                .from(viewGroup.getContext())
                .inflate(layoutId, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(CheckoutPackagesListViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == ITEM_SECTION_HEADER) {
            int index = headerPositions.indexOf(position);
            CartPackage p = cartPackages.get(index);
            holder.tvPackageTitle.setText(p.getTitle());
            if (TextUtils.isNotEmpty(p.getDeliveryTime())) {
                holder.tvPackageDeliveryTime.setText(p.getDeliveryTime());
            }
        } else if (viewType == ITEM_ORDER_ITEM) {
            Context context = holder.itemView.getContext();
            PurchaseCartItem cartItem = indexedItems.get(position);
            holder.brand.setText(cartItem.getBrand() != null ? cartItem.getBrand().getName() : "");
            holder.price.setText(CurrencyFormatter.formatCurrency(cartItem.getPrice()));
            holder.count.setText(String.format(mLocale, "%s: %d", context.getString(R.string.quantity_label), cartItem.getQuantity()));
            holder.product.setText(cartItem.getName());
            //RocketImageLoader.instance.loadImage(carditem.getImageUrl().replace("-cart.jpg","-catalog_grid_3.jpg"), holder.img, null, R.drawable.no_image_small);
            ImageManager.getInstance().loadImage(cartItem.getImageUrl().replace("-cart.jpg","-catalog_grid_3.jpg"), holder.img, null, R.drawable.no_image_large, false);
        }
    }

    @Override
    public int getItemCount() {
        return this.count;
    }

    @Override
    public int getItemViewType(int position) {
        if (headerPositions.contains(position)) {
            return ITEM_SECTION_HEADER;
        } else {
            return ITEM_ORDER_ITEM;
        }
    }

    private void calculateItemCount(List<CartPackage> cartPackages) {
        headerPositions = new ArrayList<>();
        indexedItems = new HashMap<>();
        int count = 0;
        for (CartPackage p : cartPackages) {
            // index of package title item
            headerPositions.add(count);
            count++; // package title item
            for (int index = 0; index < p.getProducts().size(); index++) {
                indexedItems.put(count + index, p.getProducts().get(index));
            }
            count += p.getProducts().size();
        }
        this.count = count;
    }

    public List<CartPackage> getCartPackages() {
        return cartPackages;
    }

    public void setCartPackages(List<CartPackage> cartPackages) {
        this.cartPackages = cartPackages;
        calculateItemCount(cartPackages);
    }

    public static class CheckoutPackagesListViewHolder extends RecyclerView.ViewHolder {
        // package section header
        TextView tvPackageTitle, tvPackageDeliveryTime;

        // package cart item
        public TextView brand, product, price, count;
        ImageView img;

        public CheckoutPackagesListViewHolder(View itemView) {
            super(itemView);

            tvPackageTitle = (TextView) itemView.findViewById(R.id.tvPackageTitle);
            tvPackageDeliveryTime = (TextView) itemView.findViewById(R.id.tvPackageDeliveryTime);


            brand = (com.mobile.components.customfontviews.TextView) itemView.findViewById(R.id.checkout_brand);
            count = (com.mobile.components.customfontviews.TextView) itemView.findViewById(R.id.checkout_count);
            price= (com.mobile.components.customfontviews.TextView) itemView.findViewById(R.id.checkout_price);
            product = (com.mobile.components.customfontviews.TextView) itemView.findViewById(R.id.checkout_product);
            img = (ImageView) itemView.findViewById(R.id.checkout_product_image);
        }
    }
}
