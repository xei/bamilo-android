package com.mobile.utils.product;

import android.content.Context;
import android.graphics.Paint;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.service.objects.product.pojo.ProductRegular;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.shop.CurrencyFormatter;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Class used to fill the grid catalog.<br>
 * Can be used to add a header and footer view.
 *
 * @author sergiopereira
 */
public class RelatedProductsAdapter extends ArrayAdapter<ProductRegular> {

    private boolean isOddSize = false;
    /**
     * Constructor
     */
    public RelatedProductsAdapter(Context context, int textViewResourceId, ArrayList<ProductRegular> data) {
        super(context, textViewResourceId, data);
        /**
         * This is the solution to avoid the silver background
         * when the grid has an odd size.
         */
        isOddSize = CollectionUtils.sizeIsOdd(data);
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

        if(position == (getCount() - 1) && isOddSize){ // If last position and odd hide all the views. Just show the white Background.
            hideViewsForPlacebo(holder);
        } else {
            // Get item
            ProductRegular item = getItem(position);
            // Set name
            holder.name.setText(item.getName());
            // Set brand
            holder.brand.setText(item.getBrandName());
            // Set image
            //RocketImageLoader.instance.loadImage(item.getImageUrl(), holder.image, holder.progress, R.drawable.no_image_small);
            ImageManager.getInstance().loadImage(item.getImageUrl(), holder.image, holder.progress, R.drawable.no_image_large);
            // Set prices
            setProductPrice(holder, item);
            // Set tag
            holder.itemView.setTag(R.id.target_sku, item.getTarget());
        }

        // Return convert view
        return convertView;
    }

    @Override
    public int getCount() {
        // If odd number of items, return one more item to show placebo.
        return  (super.getCount() % 2 == 0) ? super.getCount() : super.getCount() + 1;
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

    private void hideViewsForPlacebo(ProductViewHolder holder){
        holder.name.setVisibility(View.INVISIBLE);
        holder.image.setVisibility(View.INVISIBLE);
        holder.progress.setVisibility(View.INVISIBLE);
        holder.brand.setVisibility(View.INVISIBLE);
        holder.price.setVisibility(View.INVISIBLE);
        holder.discount.setVisibility(View.INVISIBLE);
    }
}
