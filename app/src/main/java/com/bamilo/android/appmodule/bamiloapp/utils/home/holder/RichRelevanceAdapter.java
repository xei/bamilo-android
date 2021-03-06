package com.bamilo.android.appmodule.bamiloapp.utils.home.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bamilo.android.framework.service.objects.home.type.TeaserGroupType;
import com.bamilo.android.framework.service.objects.product.pojo.ProductRegular;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import com.bamilo.android.appmodule.bamiloapp.utils.home.TeaserViewFactory;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.R;
import java.util.ArrayList;

/**
 * Rich Relevance adapter
 *
 * @author Manuel Silva
 */
public class RichRelevanceAdapter extends RecyclerView.Adapter<RichRelevanceAdapter.ViewHolder> {

    private final View.OnClickListener mOnClickListener;

    private final ArrayList<ProductRegular> mDataSet;

    private boolean mIsTeaserRR = true;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImage;
        public View mProgress;
        public TextView mBrand;
        public TextView mName;
        public TextView mPrice;

        /**
         * Constructor
         *
         * @param view -  the view holder
         */
        public ViewHolder(View view, boolean isTeaser) {
            super(view);
            mImage = view.findViewById(R.id.home_teaser_item_image);
            mProgress = view.findViewById(R.id.home_teaser_item_progress);
            mBrand = view.findViewById(R.id.brand);
            mName = view.findViewById(R.id.name);
            mPrice = view.findViewById(R.id.price);

        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     */
    public RichRelevanceAdapter(final ArrayList<ProductRegular> rrProducts,
            final View.OnClickListener listener, final boolean isTeaserRR) {
        mDataSet = rrProducts;
        mOnClickListener = listener;
        mIsTeaserRR = isTeaserRR;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
            int viewType) {
        // Create a new view
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_teaser_top_sellers_item, parent, false), mIsTeaserRR);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get item
        ProductRegular item = mDataSet.get(position);
        // Set name
        holder.mName.setText(item.getName());
        // Set brand
        holder.mBrand.setText(item.getBrandName());
        // Set image
        ImageManager.getInstance().loadImage(item.getImageUrl(), holder.mImage, holder.mProgress,
                R.drawable.no_image_large, false);
        // Set price
        double price = item.hasDiscount() ? item.getSpecialPrice() : item.getPrice();
        holder.mPrice.setText(CurrencyFormatter.formatCurrency(String.valueOf(price)));

        if (mIsTeaserRR) {
            // Set listener and tags
            TeaserViewFactory.setRichRelevanceClickableView(holder.itemView, item, mOnClickListener,
                    position, TeaserGroupType.TOP_SELLERS);
        } else {
            // Set tag
            holder.itemView.setTag(R.id.target_sku, item.getTarget());
            holder.itemView.setTag(R.id.target_rr_hash, item.getRichRelevanceClickHash());
            holder.itemView.setOnClickListener(mOnClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.isNotEmpty(mDataSet) ? mDataSet.size() : 0;
    }
}
