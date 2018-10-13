package com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.recommended;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bamilo.android.appmodule.modernbamilo.util.extension.StringExtKt;
import com.emarsys.predict.RecommendedItem;
import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.RecommendationWidgetType;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.R;

import java.util.List;
import java.util.Map;

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
            mImage = view.findViewById(R.id.home_teaser_item_image);
            mProgress = view.findViewById(R.id.home_teaser_item_progress);
            mBrand = view.findViewById(R.id.brand);
            mName = view.findViewById(R.id.name);
            mPrice = view.findViewById(R.id.price);
            mOldPrice = view.findViewById(R.id.old_price);
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (this.recommendationWidgetType == RecommendationWidgetType.List) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recommends_item, parent, false));
        } else if (this.recommendationWidgetType == RecommendationWidgetType.Grid) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendation_grid_item, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendation_cart_item, parent, false));
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecommendedItem item = mDataSet.get(position);
        String sku = "" + item.getData().get("item");
        holder.mName.setText("" + item.getData().get("title"));

        Map<String, Object> data = item.getData();
        Object imageObject = data.get("image");
        if(imageObject != null) {
            try {
                ImageManager.getInstance().loadImage(imageObject.toString(), holder.mImage, holder.mProgress, R.drawable.no_image_large, false);
            } catch (Exception ignored) {
            }
        }

        holder.mBrand.setText("" + data.get("brand"));

        double price = (double) data.get("price");
        double special = (double) data.get("msrp");

        if (price != special) {
            holder.mPrice.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(price)));
            holder.mOldPrice.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(special)));
            holder.mOldPrice.setPaintFlags(holder.mOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mOldPrice.setVisibility(View.VISIBLE);
        } else {
            holder.mPrice.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(price)));
            holder.mOldPrice.setVisibility(View.INVISIBLE);
        }
        if (this.recommendationWidgetType == RecommendationWidgetType.Cart) {
            holder.mOldPrice.setVisibility(View.GONE);
            holder.mBrand.setVisibility(View.GONE);
            if (price != special) {
                holder.mPrice.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(price)));
            } else {
                holder.mPrice.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(special)));
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
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.isNotEmpty(mDataSet) ? mDataSet.size() : 0;
    }
}
