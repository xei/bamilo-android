package com.mobile.utils.ui;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.interfaces.OnViewHolderClickListener;
import com.mobile.newFramework.objects.product.pojo.ProductBundle;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.objects.product.pojo.ProductSimple;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Class used to fill the grid/list of bundles in a combo on combo page.<br>
 *
 * @author sergiopereira
 * @modified alexandrapires
 */
public class ComboGridAdapter extends RecyclerView.Adapter<ComboGridAdapter.ProductViewHolder> implements OnClickListener{


    private ArrayList<ProductBundle> mDataSet;

    private Context mContext;

    private int mLastPosition = -1;

    private String productSku;

    private OnViewHolderClickListener mOnViewHolderClicked;



    /**
     * Provide a reference to the views for each data item.<br>
     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br>
     * @author sergiopereira
     *
     */
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        // Data
        public TextView name;
        public TextView brand;
        public ImageView image;
        public View progress;
        public RatingBar rating;
        public TextView discount;
        public TextView price;
        public TextView percentage;
        public TextView reviews;
        public CheckBox cbItem;
        public TextView variation;

        /**
         * Constructor
         * @param view -  the view holder
         */
        public ProductViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.item_title);
            brand = (TextView) view.findViewById(R.id.item_brand);

            image = (ImageView) view.findViewById(R.id.image_view);
            progress = view.findViewById(R.id.image_loading_progress);
            //see if is fashion or not
            rating = (RatingBar) view.findViewById(R.id.product_detail_product_rating);
            reviews = (TextView) view.findViewById(R.id.product_detail_product_rating_count);

            price = (TextView) view.findViewById(R.id.pdv_text_price);
            discount = (TextView) view.findViewById(R.id.pdv_text_special_price);
            percentage = (TextView) view.findViewById(R.id.pdv_text_discount);

            cbItem = (CheckBox) view.findViewById(R.id.item_check);

            brand = (TextView) view.findViewById(R.id.item_brand);

            variation = (TextView) view.findViewById(R.id.choosen_variation);

        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     * @param context - the application context
     * @param data - the array lisl
     */
    public ComboGridAdapter(Context context, ArrayList<ProductBundle> data,String productSku) {
        mContext = context;
        mDataSet = data;
        this.productSku = productSku;
    }

    /**
     * update a bundle in the arrays; necessary for updating viewholder in case of a chosen simple
     * @param bundle - productBundle
     *
     */
    public void setItemInArray(ProductBundle productBundle)
    {
        for(int i=0; i< mDataSet.size(); i++)
        {
            if(mDataSet.get(i).getSku().equals(productBundle.getSku()))
                mDataSet.set(i,productBundle);
        }
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.pdv_combo_item_list, parent, false));
    }


    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemCount()
     */
    @Override
    public int getItemCount() {
        // Return the size of your dataset (invoked by the layout manager)
        return mDataSet == null ? 0 : mDataSet.size();
    }


    public ArrayList<ProductBundle> getItems()
    {
        return mDataSet;
    }

    /*
       * (non-Javadoc)
       * @see android.support.v7.widget.RecyclerView.Adapter#onViewDetachedFromWindow(android.support.v7.widget.RecyclerView.ViewHolder)
       */
    @Override
    public void onViewDetachedFromWindow(ProductViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        // Cancel the animation for detached views
        holder.itemView.clearAnimation();
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {

        if(holder == null)
            holder = new ProductViewHolder(LayoutInflater.from(mContext).inflate(R.layout.pdv_combo_item_list,null,false));
        // Set animation
        setAnimation(holder, position);

        // Get item
        ProductBundle item = mDataSet.get(position);
        // Set name
        holder.name.setText(item.getName());
        // Set brand
        holder.brand.setText(item.getBrand());
        // Set image
        RocketImageLoader.instance.loadImage(item.getImageUrl(), holder.image, holder.progress, R.drawable.no_image_small);

        // Set rating and reviews
        setSpecificViewForListLayout(holder, item);
        // Set prices
        setProductPrice(holder, item);
        //set selection
        holder.cbItem.setChecked(item.isChecked());
        //set variation if has multiple variations and there is a selected variation
        if(item.hasMultiSimpleVariations() && item.getSelectedSimple() != null)
        {
            ProductSimple productSimple = item.getSelectedSimple();
            holder.variation.setText(productSimple.getVariationValue());
            holder.variation.setVisibility(View.VISIBLE);
        }

        // Set the parent layout
        holder.itemView.setTag(R.id.position, position);
        holder.itemView.setOnClickListener(this);
    }




    /**
     * Set the product price.
     * @param holder - the view holder
     * @param item - the product
     */
    private void setProductPrice(ProductViewHolder holder, ProductRegular item) {
        // Case discount
        if(item.hasDiscount()) {
            holder.discount.setText(CurrencyFormatter.formatCurrency(item.getSpecialPrice()));
            holder.price.setText(CurrencyFormatter.formatCurrency(item.getPrice()));
            holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.percentage.setText(String.format(mContext.getString(R.string.format_discount_percentage), item.getMaxSavingPercentage()));
            holder.percentage.setVisibility(View.VISIBLE);
        }
        // Case normal
        else {
            holder.discount.setText(CurrencyFormatter.formatCurrency(item.getPrice()));
            holder.price.setText("");
            holder.percentage.setVisibility(View.GONE);
        }
    }

    /**
     * Validate and set views from list layout.
     * @param holder - the view holder
     * @param item - the product
     */
    private void setSpecificViewForListLayout(ProductViewHolder holder, ProductRegular item) {
        // Validate list views
        if(holder.rating != null && holder.reviews != null) {
            // Show rating
            if (item.getAvgRating() > 0) {
                holder.rating.setRating((float) item.getAvgRating());
                holder.rating.setVisibility(View.VISIBLE);
                int count = item.getTotalReviews();
                String string = mContext.getResources().getQuantityString(R.plurals.numberOfRatings, count, count);
                holder.reviews.setText(string);
            }
            // Hide rating
            else {
                holder.rating.setVisibility(View.INVISIBLE);
                holder.reviews.setText("");
            }
        }
    }

    /**
     * Set an animation for new items.
     * @param holder - the view holder
     * @param position - the current position
     */
    private void setAnimation(ProductViewHolder holder, int position) {
        if(position > mLastPosition) {
            //Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.entry_up_from_bottom);
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_fade_in);
            //Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_bottom);
            holder.itemView.startAnimation(animation);
            mLastPosition = position;
        }
    }




    /**
     * Get the product from the current data.
     * @param position - the respective product position
     * @return Product or null
     */
    public ProductBundle getItem(int position) {
        return CollectionUtils.isEmpty(mDataSet) ?  null : mDataSet.get(position);
    }

    /**
     * Set the listener the click on view holder.
     * @param listener - the listener
     */
    public void setOnViewHolderClickListener(OnViewHolderClickListener listener) {
        this.mOnViewHolderClicked = listener;
    }



    @Override
    public void onClick(View view) {
        if (mOnViewHolderClicked != null) {
            // position
            int position = (Integer) view.getTag(R.id.position);
            ProductBundle productBundle = mDataSet.get(position);

            if(!productSku.equals(productBundle.getSku())) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.item_check);

                cb.setChecked(!cb.isChecked());

                mOnViewHolderClicked.onViewHolderClick(this, position);
            }

        }


    }



}
