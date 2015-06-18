package com.mobile.utils.home.holder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.home.object.BaseTeaserObject;
import com.mobile.newFramework.objects.home.object.TeaserTopSellerObject;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

/**
 * 
 * @author sergiopereira
 *
 */
public class HomeTopSellersTeaserAdapter extends RecyclerView.Adapter<HomeTopSellersTeaserAdapter.ViewHolder> {

    private View.OnClickListener mOnClickListener;

    private ArrayList<BaseTeaserObject> mDataSet;

    /**
     * Provide a reference to the views for each data item.<br>
     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br>
     * @author sergiopereira
     *
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Views
        private ImageView mImage;
        private View mProgress;
        private TextView mBrand;
        private TextView mName;
        private TextView mPrice;

        /**
         * Constructor
         */
        public ViewHolder(View view) {
            super(view);
            mImage = (ImageView) view.findViewById(R.id.home_teaser_item_image);
            mProgress = view.findViewById(R.id.home_teaser_item_progress);
            mBrand = (TextView) view.findViewById(R.id.home_teaser_top_sellers_brand);
            mName = (TextView) view.findViewById(R.id.home_teaser_top_sellers_name);
            mPrice = (TextView) view.findViewById(R.id.home_teaser_top_sellers_price);
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     */
    public HomeTopSellersTeaserAdapter(ArrayList<BaseTeaserObject> teasers, View.OnClickListener listener) {
        mDataSet = teasers;
        mOnClickListener = listener;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public HomeTopSellersTeaserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_teaser_top_sellers_item, parent, false));
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get item
        TeaserTopSellerObject item = (TeaserTopSellerObject) mDataSet.get(position);
        // Set image
        RocketImageLoader.instance.loadImage(item.getImage(), holder.mImage, holder.mProgress, R.drawable.no_image_large);
        // Set brand
        holder.mBrand.setText(item.getBrand());
        // Set name
        holder.mName.setText(item.getTitle());
        // Set price
        double price = item.hasSpecialPrice() ? item.getSpecialPrice() : item.getPrice();
        holder.mPrice.setText(CurrencyFormatter.formatCurrency(String.valueOf(price)));
        // Set listener and tags
        TeaserViewFactory.setClickableView(holder.itemView, item, mOnClickListener);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemCount()
     */
    @Override
    public int getItemCount() {
        return CollectionUtils.isNotEmpty(mDataSet) ? mDataSet.size() : 0;
    }
    
}
