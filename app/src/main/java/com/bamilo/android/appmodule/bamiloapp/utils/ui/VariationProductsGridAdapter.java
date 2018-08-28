package com.bamilo.android.appmodule.bamiloapp.utils.ui;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.bamilo.android.appmodule.bamiloapp.interfaces.OnProductViewHolderClickListener;
import com.bamilo.android.framework.service.objects.product.Variation;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.appmodule.bamiloapp.utils.product.UIProductUtils;
import com.bamilo.android.R;

import java.util.ArrayList;

/**
 * Adapter to fill grid for variation products in variation section
 * @author alexandrapires
 */
public class VariationProductsGridAdapter extends RecyclerView.Adapter<ProductListViewHolder> implements OnClickListener {

    private ArrayList<Variation> mDataSet;

    private OnProductViewHolderClickListener mOnViewHolderClicked;

    /**
     * Provide a suitable constructor (depends on the kind of data)
     * @param data - list of product regular data
     */
    public VariationProductsGridAdapter(ArrayList<Variation> data) {
        mDataSet = data;
    }

    @NonNull
    @Override
    public ProductListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gen_product_list, parent, false));
    }

    @Override
    public int getItemCount() {
        // Return the size of your dataset (invoked by the layout manager)
        return mDataSet == null ? 0 : mDataSet.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ProductListViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        // Cancel the animation for detached views
        holder.itemView.clearAnimation();
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListViewHolder holder, int position) {
        // Get item
        Variation item = mDataSet.get(position);
        // Set name
        holder.name.setText(item.getName());
        // Set brand
        holder.brand.setText(item.getBrand());
        // Set image
        ImageManager.getInstance().loadImage(item.getImage(), holder.image, holder.progress, R.drawable.no_image_large, false);
        // Set prices
        setProductPrice(holder, item);
        // Set the parent layout
        holder.itemView.setTag(R.id.position, position);
        holder.itemView.setOnClickListener(this);
        UIUtils.showOrHideViews(View.GONE, holder.percentage, holder.ratingContainer, holder.favourite, holder.newArrivalBadge);
        UIProductUtils.setShopFirst(item, holder.shopFirst);
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
    public void setOnViewHolderClickListener(OnProductViewHolderClickListener listener) {
        this.mOnViewHolderClicked = listener;
    }

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
