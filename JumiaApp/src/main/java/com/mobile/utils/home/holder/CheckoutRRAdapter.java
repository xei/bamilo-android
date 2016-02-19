package com.mobile.utils.home.holder;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.home.object.BaseTeaserObject;
import com.mobile.newFramework.objects.home.object.TeaserTopSellerObject;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Rich Relevance adapter
 * @author Manuel Silva
 *
 */
public class CheckoutRRAdapter extends RecyclerView.Adapter<CheckoutRRAdapter.ViewHolder> {

    private final View.OnClickListener mOnClickListener;

    private final ArrayList<ProductRegular>  mDataSet;

    /**
     * Provide a reference to the views for each data item.<br>
     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br>
     * @author sergiopereira
     *
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public TextView mBrand;
        public ImageView mImage;
        public TextView mPrice;
        public View mProgress;
        public TextView mDiscount;

        /**
         * Constructor
         *
         * @param view -  the view holder
         */
        public ViewHolder(View view) {
            super(view);
            mName = (TextView) view.findViewById(R.id.header_text);
            mImage = (ImageView) view.findViewById(R.id.image_view);
            mProgress = view.findViewById(R.id.image_loading_progress);
            mBrand = (TextView) view.findViewById(R.id.item_brand);
            mPrice = (TextView) view.findViewById(R.id.item_price);
            mDiscount = (TextView) view.findViewById(R.id.item_discount);
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     */
    public CheckoutRRAdapter( ArrayList<ProductRegular>  rrProducts, View.OnClickListener listener) {
        mDataSet = rrProducts;
        mOnClickListener = listener;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public CheckoutRRAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_checkout_rr_item, parent, false));
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get item
        ProductRegular item = mDataSet.get(position);
        // Set name
        holder.mName.setText(item.getName());
        // Set brand
        holder.mBrand.setText(item.getBrand());
        // Set image
        RocketImageLoader.instance.loadImage(item.getImageUrl(), holder.mImage, holder.mProgress, R.drawable.no_image_small);
        // Set prices
        setProductPrice(holder, item);
        // Set tag
        holder.itemView.setTag(R.id.target_sku, item.getTarget());
        holder.itemView.setTag(R.id.target_rr_hash, item.getRichRelevanceClickHash());
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemCount()
     */
    @Override
    public int getItemCount() {
        return CollectionUtils.isNotEmpty(mDataSet) ? mDataSet.size() : 0;
    }


    /**
     * Set the product price.
     *
     * @param holder - the view holder
     * @param item   - the product
     */
    private void setProductPrice(ViewHolder holder, ProductRegular item) {
        // Case discount
        if (item.hasDiscount()) {
            holder.mDiscount.setText(CurrencyFormatter.formatCurrency(item.getSpecialPrice()));
            holder.mPrice.setText(CurrencyFormatter.formatCurrency(item.getPrice()));
            holder.mPrice.setPaintFlags(holder.mPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        // Case normal
        else {
            holder.mDiscount.setText(CurrencyFormatter.formatCurrency(item.getPrice()));
            holder.mPrice.setText("");
        }
    }
}
