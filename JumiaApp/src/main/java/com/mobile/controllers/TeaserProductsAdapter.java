//package com.mobile.controllers;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//
//import com.mobile.components.customfontviews.TextView;
//import com.mobile.framework.objects.TeaserGroupProducts;
//import com.mobile.framework.objects.TeaserGroupProducts.TeaserProduct;
//import com.mobile.framework.objects.TeaserGroupType;
//import com.mobile.utils.imageloader.RocketImageLoader;
//import com.mobile.view.R;
//
//import org.apache.commons.collections4.CollectionUtils;
//
//import java.util.ArrayList;
//
///**
// *
// * @author sergiopereira
// *
// */
//@Deprecated
//public class TeaserProductsAdapter extends RecyclerView.Adapter<TeaserProductsAdapter.ViewHolder> {
//
//    private ArrayList<TeaserProduct> mDataset;
//    private boolean isTablet;
//    private TeaserGroupType mTeaserType;
//    private OnClickListener mParentClickListener;
//
//    /**
//     * Provide a reference to the views for each data item.<br>
//     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br>
//     * @author sergiopereira
//     *
//     */
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        // Data
//        public TextView mBrand;
//        public TextView mTitle;
//        private ImageView mImage;
//        private ProgressBar mProgress;
//        private TextView mPrice;
//        private View mContainer;
//
//        /**
//         * Constructor
//         * @param view
//         */
//        public ViewHolder(View view) {
//            super(view);
//            mContainer = view.findViewById(R.id.item_container);
//            mBrand = (TextView) view.findViewById(R.id.item_brand);
//            mTitle = (TextView) view.findViewById(R.id.item_title);
//            mImage = (ImageView) view.findViewById(R.id.image_view);
//            mProgress = (ProgressBar) view.findViewById(R.id.image_loading_progress);
//            mPrice = (TextView) view.findViewById(R.id.item_price);
//        }
//    }
//
//    /**
//     * Provide a suitable constructor (depends on the kind of data)
//     * @param context
//     * @param productTeaserGroup
//     * @param parentClickListener
//     * @author sergiopereira
//     */
//    public TeaserProductsAdapter(Context context, TeaserGroupProducts productTeaserGroup, OnClickListener parentClickListener) {
//        mDataset = productTeaserGroup.getTeasers();
//        mTeaserType = productTeaserGroup.getType();
//        mParentClickListener = parentClickListener;
//        isTablet = context.getResources().getBoolean(R.bool.isTablet);
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
//     */
//    @Override
//    public TeaserProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        // Create a new view
//        return new ViewHolder(LayoutInflater.from(parent.getContext()) .inflate(R.layout.product_item_small, parent, false));
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
//     */
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        // Replace the contents of a view (invoked by the layout manager)
//        // Get item
//        TeaserProduct item = mDataset.get(position);
//        // Set brand
//        holder.mBrand.setText(item.getBrand());
//        // Set title
//        holder.mTitle.setText(item.getName());
//        // Set image
//        String url = CollectionUtils.isNotEmpty(item.getImages()) ? item.getImages().get(0).getUrl() : "";
//        url = isTablet && CollectionUtils.isNotEmpty(item.getImagesTablet())? item.getImagesTablet().get(0).getUrl() : url;
//        RocketImageLoader.instance.loadImage(url, holder.mImage, holder.mProgress, R.drawable.no_image_large);
//        // Set price
//        String price = (!TextUtils.isEmpty(item.getSpecialPrice())) ? item.getSpecialPrice() : item.getPrice();
//        holder.mPrice.setText(price);
//        // Set listener and tags
//        holder.mContainer.setTag(R.id.origin_type, mTeaserType);
//        holder.mContainer.setTag(R.id.target_url, item.getTargetUrl());
//        holder.mContainer.setTag(R.id.target_type, item.getTargetType());
//        holder.mContainer.setTag(R.id.target_title, item.getTargetTitle());
//        holder.mContainer.setOnClickListener(mParentClickListener);
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see android.support.v7.widget.RecyclerView.Adapter#getItemCount()
//     */
//    @Override
//    public int getItemCount() {
//        // Return the size of your dataset (invoked by the layout manager)
//        return mDataset == null ? 0 : mDataset.size();
//    }
//
//}
