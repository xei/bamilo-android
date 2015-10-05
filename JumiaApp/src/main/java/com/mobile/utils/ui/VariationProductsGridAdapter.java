package com.mobile.utils.ui;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.mobile.interfaces.OnViewHolderClickListener;
import com.mobile.newFramework.objects.product.Variation;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Adapter to fill grid for variation products in variation section
 * @author alexandrapires
 */
public class VariationProductsGridAdapter extends RecyclerView.Adapter<ProductListViewHolder> implements OnClickListener {

    private ArrayList<Variation> mDataSet;

    private Context mContext;

    private OnViewHolderClickListener mOnViewHolderClicked;

    /**
     * Provide a suitable constructor (depends on the kind of data)
     * @param context - the application context
     * @param data - list of product regular data
     */
    public VariationProductsGridAdapter(Context context, ArrayList<Variation> data) {
        mContext = context;
        mDataSet = data;
    }



    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public ProductListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ProductListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gen_product_list, parent, false));
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


    /*
      * (non-Javadoc)
      * @see android.support.v7.widget.RecyclerView.Adapter#onViewDetachedFromWindow(android.support.v7.widget.RecyclerView.ViewHolder)
      */
    @Override
    public void onViewDetachedFromWindow(ProductListViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        // Cancel the animation for detached views
        holder.itemView.clearAnimation();
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(ProductListViewHolder holder, int position) {
        // Set animation
   //     setAnimation(holder, position);
        // Get item
        Variation item = mDataSet.get(position);
        // Set name
        holder.name.setText(item.getName());
        // Set brand
        holder.brand.setText(item.getBrand());
        // Set is new image
        // Set image
        RocketImageLoader.instance.loadImage(item.getImage(), holder.image, holder.progress, R.drawable.no_image_small);
        // Set prices
        setProductPrice(holder, item);
        // Set the parent layout
        holder.itemView.setTag(R.id.position, position);
        holder.itemView.setOnClickListener(this);

        UIUtils.showOrHideViews(View.GONE, holder.percentage, holder.ratingContainer, holder.favourite);

    }
    
    /**
     * Set the product price.
     * @param holder - the view holder
     * @param item - the product
     */
    private void setProductPrice(ProductListViewHolder holder, Variation item) {
        // Case discount
        if(item.hasDiscount()) {
            holder.discount.setText(CurrencyFormatter.formatCurrency(item.getSpecialPrice()));
            holder.price.setText(CurrencyFormatter.formatCurrency(item.getPrice()));
            holder.price.setPaintFlags( holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        // Case normal
        else {
            holder.discount.setText(CurrencyFormatter.formatCurrency(item.getPrice()));
            holder.price.setText("");
        }
    }
    




    /**
     * Get the product from the current data.
     * @param position - the respective product position
     * @return Product or null
     */
    public Variation getItem(int position) {
        return CollectionUtils.isEmpty(mDataSet) ?  null : mDataSet.get(position);
    }
    
    /**
     * Set the listener the click on view holder.
     * @param listener - the listener
     */
    public void setOnViewHolderClickListener(OnViewHolderClickListener listener) {
        this.mOnViewHolderClicked = listener;
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Case other sent to listener
        if (mOnViewHolderClicked != null) {
            // position
            int position = (Integer) view.getTag(R.id.position);
            mOnViewHolderClicked.onViewHolderClick(this, position);

        }
    }
}
