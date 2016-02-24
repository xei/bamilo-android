package com.mobile.utils.pdv;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Class used to fill the grid catalog.<br>
 * Can be used to add a header and footer view.
 *
 * @author sergiopereira
 */
public class RelatedProductsAdapter extends ArrayAdapter<ProductRegular> {

    /**
     * Constructor
     */
    public RelatedProductsAdapter(Context context, int textViewResourceId, ArrayList<ProductRegular> data) {
        super(context, textViewResourceId, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdv_fragment_related_item, parent, false);
            holder = new ProductViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ProductViewHolder) convertView.getTag();
        }
        // Get item
        ProductRegular item = getItem(position);
        // Set name
        holder.name.setText(item.getName());
        // Set brand
        holder.brand.setText(item.getBrandName());
        // Set image
        RocketImageLoader.instance.loadImage(item.getImageUrl(), holder.image, holder.progress, R.drawable.no_image_small);
        // Set prices
        setProductPrice(holder, item);
        // Set tag
        holder.itemView.setTag(R.id.target_sku, item.getTarget());
        // Return convert view
        return convertView;
    }

    /**
     * Provide a reference to the views for each data item.<br>
     *
     * @author alexandrapires
     */
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView brand;
        public ImageView image;
        public TextView price;
        public View progress;
        public ImageView favourite;
        public TextView discount;

        /**
         * Constructor
         *
         * @param view -  the view holder
         */
        public ProductViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.header_text);
            image = (ImageView) view.findViewById(R.id.image_view);
            progress = view.findViewById(R.id.image_loading_progress);
            brand = (TextView) view.findViewById(R.id.item_brand);
            price = (TextView) view.findViewById(R.id.item_price);
            discount = (TextView) view.findViewById(R.id.item_discount);
        }
    }

    /**
     * Set the product price.
     *
     * @param holder - the view holder
     * @param item   - the product
     */
    private void setProductPrice(ProductViewHolder holder, ProductRegular item) {
        // Case discount
        if (item.hasDiscount()) {
            holder.discount.setText(CurrencyFormatter.formatCurrency(item.getSpecialPrice()));
            holder.price.setText(CurrencyFormatter.formatCurrency(item.getPrice()));
            holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        // Case normal
        else {
            holder.discount.setText(CurrencyFormatter.formatCurrency(item.getPrice()));
            holder.price.setText("");
        }
    }


}
