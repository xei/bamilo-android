package com.bamilo.android.appmodule.bamiloapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.framework.service.objects.cart.PurchaseCartItem;
import com.bamilo.android.framework.service.objects.checkout.CartPackage;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by mohsen on 3/12/18.
 */

public class CheckoutPackagesListAdapter extends RecyclerView.Adapter<CheckoutPackagesListAdapter.CheckoutPackagesListViewHolder> {

    private static final int ITEM_SECTION_HEADER = 2;
    private static final int ITEM_ORDER_ITEM = 3;

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

        View view = null;

        switch (viewType) {
            case ITEM_SECTION_HEADER:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_package_section_header, viewGroup, false);
                break;
            case ITEM_ORDER_ITEM:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout._def_checkout_confirmation_product, viewGroup, false);
                break;
        }

        return new CheckoutPackagesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CheckoutPackagesListViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        Locale locale = new Locale("fa", "ir");

        if (viewType == ITEM_SECTION_HEADER) {
            CartPackage cartPackage = cartPackages.get(headerPositions.indexOf(position));

            holder.tvPackageTitle.setText(cartPackage.getTitle());

            if (TextUtils.isNotEmpty(cartPackage.getDeliveryTime())) {
                holder.tvPackageDeliveryTime.setText(String.format(locale, "زمان تحویل: %s", cartPackage.getDeliveryTime()));
            }

            if (cartPackage.getDeliveryType() != null
                    && cartPackage.getDeliveryType().getDropShipDescription() != null
                    && cartPackage.getDeliveryType().getDropShipDescription().length() != 0) {
                holder.dropShipDescriptionTextView.setText(cartPackage.getDeliveryType().getDropShipDescription());
                holder.dropShipDescriptionTextView.setVisibility(View.VISIBLE);
            } else {
                holder.dropShipDescriptionTextView.setVisibility(View.GONE);
            }

        } else if (viewType == ITEM_ORDER_ITEM) {
            Context context = holder.itemView.getContext();
            PurchaseCartItem cartItem = indexedItems.get(position);
            holder.brand.setText(cartItem.getBrand() != null ? cartItem.getBrand().getName() : "");
            if (Double.isNaN(cartItem.getSpecialPrice())) {
                holder.price.setText(CurrencyFormatter.formatCurrency(cartItem.getPrice()));
            } else {
                holder.price.setText(CurrencyFormatter.formatCurrency(cartItem.getSpecialPrice()));
            }
            holder.count.setText(String.format(mLocale, "%s: %d", context.getString(R.string.quantity_label), cartItem.getQuantity()));
            holder.product.setText(cartItem.getName());
            ImageManager.getInstance().loadImage(cartItem.getImageUrl().replace("-cart.jpg", "-catalog_grid_3.jpg"), holder.img, null, R.drawable.no_image_large, false);
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
        private TextView dropShipDescriptionTextView;

        // package cart item
        public TextView brand, product, price, count;
        ImageView img;

        CheckoutPackagesListViewHolder(View itemView) {
            super(itemView);

            tvPackageTitle = itemView.findViewById(R.id.tvPackageTitle);
            tvPackageDeliveryTime = itemView.findViewById(R.id.tvPackageDeliveryTime);
            dropShipDescriptionTextView = itemView.findViewById(R.id.rowPackageSectionHeader_xeiTextView_dropShipMessage);


            brand = itemView.findViewById(R.id.checkout_brand);
            count = itemView.findViewById(R.id.checkout_count);
            price = itemView.findViewById(R.id.checkout_price);
            product = itemView.findViewById(R.id.checkout_product);
            img = itemView.findViewById(R.id.checkout_product_image);
        }
    }
}
