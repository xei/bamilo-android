package com.mobile.extlibraries.emarsys.predict.recommended;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.emarsys.predict.RecommendedItem;
import com.mobile.components.customfontviews.TextView;
import com.mobile.extlibraries.emarsys.predict.RecommendationWidgetType;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.shop.CurrencyFormatter;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;

import java.util.List;

/**
 * @author sergiopereira
 */
public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.ViewHolder> {

    private final View.OnClickListener mOnClickListener;

    private final List<RecommendedItem> mDataSet;
    private RecommendationWidgetType recommendationWidgetType;

    /**
     * Provide a reference to the views for each data item.<br>
     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br>
     *
     * @author sergiopereira
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Views
        private final ImageView mImage;
        private final View mProgress;
        private final TextView mBrand;
        private final TextView mName;
        private final TextView mPrice;
        private final TextView mOldPrice;

        /**
         * Constructor
         */
        public ViewHolder(View view) {
            super(view);
            mImage = (ImageView) view.findViewById(R.id.home_teaser_item_image);
            mProgress = view.findViewById(R.id.home_teaser_item_progress);
            mBrand = (TextView) view.findViewById(R.id.brand);
            mName = (TextView) view.findViewById(R.id.name);
            mPrice = (TextView) view.findViewById(R.id.price);
            mOldPrice = (TextView) view.findViewById(R.id.old_price);
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     */
    public RecommendationsAdapter(List<RecommendedItem> teasers, View.OnClickListener listener, RecommendationWidgetType recommendationWidgetType) {
        mDataSet = teasers;
        mOnClickListener = listener;
        this.recommendationWidgetType = recommendationWidgetType;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public RecommendationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (this.recommendationWidgetType == RecommendationWidgetType.List) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recommends_item, parent, false));
        } else if (this.recommendationWidgetType == RecommendationWidgetType.Grid) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendation_grid_item, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendation_cart_item, parent, false));
        }

    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get item
        RecommendedItem item = (RecommendedItem) mDataSet.get(position);
        String sku = "" + item.getData().get("item");
        holder.mName.setText("" + item.getData().get("title"));

        // Set image
        //RocketImageLoader.instance.loadImage("" + item.getData().get("image"), holder.mImage, holder.mProgress, R.drawable.no_image_large);
        ImageManager.getInstance().loadImage(item.getData().get("image").toString(), holder.mImage, holder.mProgress, R.drawable.no_image_large, false);

        // Set brand
        holder.mBrand.setText("" + item.getData().get("brand"));
        // Set name
        // Set price
        double price = (double) item.getData().get("price");
        double special = (double) item.getData().get("msrp");
        if (price != special) {
            holder.mPrice.setText(CurrencyFormatter.formatCurrency(price));
            holder.mOldPrice.setText(CurrencyFormatter.formatCurrency(special));
            holder.mOldPrice.setPaintFlags(holder.mOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mOldPrice.setVisibility(View.VISIBLE);
        } else {
            holder.mPrice.setText(CurrencyFormatter.formatCurrency(price));
            holder.mOldPrice.setVisibility(View.INVISIBLE);
        }
        if (this.recommendationWidgetType == RecommendationWidgetType.Cart) {
            holder.mOldPrice.setVisibility(View.GONE);
            holder.mBrand.setVisibility(View.GONE);
            if (price != special) {
                holder.mPrice.setText(CurrencyFormatter.formatCurrency(price));
            } else {
                holder.mPrice.setText(CurrencyFormatter.formatCurrency(special));
            }
        }
        holder.mName.setTag(R.id.sku, sku);
        holder.mImage.setTag(R.id.sku, sku);
        holder.mBrand.setTag(R.id.sku, sku);
        holder.mPrice.setTag(R.id.sku, sku);
        holder.mOldPrice.setTag(R.id.sku, sku);

        holder.mName.setOnClickListener(mOnClickListener);
        holder.mImage.setOnClickListener(mOnClickListener);
        holder.mBrand.setOnClickListener(mOnClickListener);
        holder.mPrice.setOnClickListener(mOnClickListener);
        holder.mOldPrice.setOnClickListener(mOnClickListener);


        // Set listener and tags
        //TeaserViewFactory.setClickableView(holder.itemView, item, mOnClickListener, position);
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
