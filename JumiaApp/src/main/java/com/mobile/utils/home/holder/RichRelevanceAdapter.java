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
import com.mobile.newFramework.objects.home.type.TeaserGroupType;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.ui.ProductUtils;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Rich Relevance adapter
 * @author Manuel Silva
 *
 */
public class RichRelevanceAdapter extends RecyclerView.Adapter<RichRelevanceAdapter.ViewHolder> {

    private final View.OnClickListener mOnClickListener;

    private final ArrayList<ProductRegular>  mDataSet;

    private final int mLayoutId;

    private boolean mIsTeaserRR = true;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImage;
        public View mProgress;
        public TextView mBrand;
        public TextView mName;
        public TextView mPrice;
        public TextView mDiscount;

        /**
         * Constructor
         *
         * @param view -  the view holder
         */
        public ViewHolder(View view, boolean isTeaser) {
            super(view);
            if(isTeaser){
                mImage = (ImageView) view.findViewById(R.id.home_teaser_item_image);
                mProgress = view.findViewById(R.id.home_teaser_item_progress);
                mBrand = (TextView) view.findViewById(R.id.home_teaser_top_sellers_brand);
                mName = (TextView) view.findViewById(R.id.home_teaser_top_sellers_name);
                mPrice = (TextView) view.findViewById(R.id.home_teaser_top_sellers_price);
            } else {
                mName = (TextView) view.findViewById(R.id.header_text);
                mImage = (ImageView) view.findViewById(R.id.image_view);
                mProgress = view.findViewById(R.id.image_loading_progress);
                mBrand = (TextView) view.findViewById(R.id.item_brand);
                mPrice = (TextView) view.findViewById(R.id.item_price);
                mDiscount = (TextView) view.findViewById(R.id.item_discount);
            }

        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     */
    public RichRelevanceAdapter( final ArrayList<ProductRegular>  rrProducts, final View.OnClickListener listener, final int layoutId, final boolean isTeaserRR) {
        mDataSet = rrProducts;
        mOnClickListener = listener;
        mLayoutId = layoutId;
        mIsTeaserRR =  isTeaserRR;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public RichRelevanceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false), mIsTeaserRR);
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
        ProductUtils.setPriceRules(item, holder.mPrice, holder.mDiscount);

        if(mIsTeaserRR){
            // Set listener and tags
            TeaserViewFactory.setRichRelevanceClickableView(holder.itemView, item, mOnClickListener, position, TeaserGroupType.TOP_SELLERS);
        } else {
            // Set tag
            holder.itemView.setTag(R.id.target_sku, item.getTarget());
            holder.itemView.setTag(R.id.target_rr_hash, item.getRichRelevanceClickHash());
            holder.itemView.setOnClickListener(mOnClickListener);
        }
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
