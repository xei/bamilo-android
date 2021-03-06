package com.bamilo.android.appmodule.bamiloapp.utils.home;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bamilo.android.framework.service.objects.home.object.BaseTeaserObject;
import com.bamilo.android.framework.service.objects.home.object.TeaserTopSellerObject;
import com.bamilo.android.framework.service.objects.home.type.TeaserGroupType;
import com.bamilo.android.framework.service.objects.product.pojo.ProductRegular;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;
import com.bamilo.android.appmodule.bamiloapp.utils.home.holder.BaseTeaserViewHolder;
import com.bamilo.android.appmodule.bamiloapp.utils.home.holder.HomeBrandTeaserHolder;
import com.bamilo.android.appmodule.bamiloapp.utils.home.holder.HomeCampaignTeaserHolder;
import com.bamilo.android.appmodule.bamiloapp.utils.home.holder.HomeFeaturedTeaserHolder;
import com.bamilo.android.appmodule.bamiloapp.utils.home.holder.HomeMainTeaserHolder;
import com.bamilo.android.appmodule.bamiloapp.utils.home.holder.HomeNewsletterTeaserHolder;
import com.bamilo.android.appmodule.bamiloapp.utils.home.holder.HomeShopTeaserHolder;
import com.bamilo.android.appmodule.bamiloapp.utils.home.holder.HomeShopWeekTeaserHolder;
import com.bamilo.android.appmodule.bamiloapp.utils.home.holder.HomeSmallTeaserHolder;
import com.bamilo.android.appmodule.bamiloapp.utils.home.holder.HomeTopSellersTeaserHolder;
import com.bamilo.android.R;

import java.util.ArrayList;

/**
 * Class used to create each teaser view
 */
public class TeaserViewFactory {

    public static final String TAG = TeaserViewFactory.class.getSimpleName();

    public static final int DEFAULT_POSITION = -1;

    /**
     * Create a teaser view holder
     * @param inflater The inflater
     * @param type The group type
     * @param parent The container
     * @param listener The click listener
     * @return BaseTeaserViewHolder or null
     */
    public static BaseTeaserViewHolder onCreateViewHolder(LayoutInflater inflater, TeaserGroupType type, ViewGroup parent, View.OnClickListener listener) {
        switch (type) {
            case MAIN_TEASERS:
                return new HomeMainTeaserHolder(inflater.getContext(), inflater.inflate(R.layout.home_teaser_main, parent, false), listener);
            case SMALL_TEASERS:
                return new HomeSmallTeaserHolder(inflater.getContext(), inflater.inflate(R.layout.home_teaser_small, parent, false), listener);
            case FORM_NEWSLETTER:
                return new HomeNewsletterTeaserHolder(inflater.getContext(), inflater.inflate(R.layout.home_teaser_newsletter, parent, false), listener);
            case CAMPAIGNS:
                return new HomeCampaignTeaserHolder(inflater.getContext(), inflater.inflate(R.layout.home_teaser_campaign, parent, false), listener);
            case SHOP_TEASERS:
                return new HomeShopTeaserHolder(inflater.getContext(), inflater.inflate(R.layout.home_teaser_shop, parent, false), listener);
            case SHOP_OF_WEEK:
                return new HomeShopWeekTeaserHolder(inflater.getContext(), inflater.inflate(R.layout.home_teaser_shop_week, parent, false), listener);
            case FEATURED_STORES:
                return new HomeFeaturedTeaserHolder(inflater.getContext(), inflater.inflate(R.layout.home_teaser_featured_stores, parent, false), listener);
            case BRAND_TEASERS:
                return new HomeBrandTeaserHolder(inflater.getContext(), inflater.inflate(R.layout.home_teaser_brand, parent, false), listener);
            case TOP_SELLERS:
                return new HomeTopSellersTeaserHolder(inflater.getContext(), inflater.inflate(R.layout.home_teaser_top_sellers, parent, false), listener);
            case UNKNOWN:
            default:
                return null;
        }
    }

    /**
     * Method used to remove parent from view holder.
     * @param viewHolders The list of view holders.
     */
    public static void onDetachedViewHolder(ArrayList<BaseTeaserViewHolder> viewHolders) {
        if(CollectionUtils.isNotEmpty(viewHolders)) {
            for (BaseTeaserViewHolder viewHolder : viewHolders) {
                // Destroy view holder content
                viewHolder.onDestroy();
                // Remove view holder from parent
                ViewGroup parent = (ViewGroup) viewHolder.itemView.getParent();
                if (parent != null) {
                    parent.removeView(viewHolder.itemView);
                }
            }
        }
    }

    /**
     * Set a teaser clickable.
     * @param view The view
     * @param teaser The teaser
     * @param listener The callback
     */
    public static void setClickableView(View view, BaseTeaserObject teaser, View.OnClickListener listener, int position) {
        if (listener != null) {
            view.setTag(R.id.target_title, teaser.getTitle());
            view.setTag(R.id.target_link, teaser.getTargetLink());
            view.setTag(R.id.target_teaser_origin, teaser.getTeaserTypeId());
            // Set position of the clicked teaser, for tracking purpose
            view.setTag(R.id.target_list_position, position);

            if(teaser instanceof TeaserTopSellerObject){
                view.setTag(R.id.target_rr_hash, ((TeaserTopSellerObject) teaser).getRichRelevanceClickHash());
            }

            view.setOnClickListener(listener);
        }
    }
    /**
     * Set a teaser clickable for Rich Relevance products
     * @param view The view
     * @param product The product
     * @param listener The callback
     */
    public static void setRichRelevanceClickableView(View view, ProductRegular product, View.OnClickListener listener, int position, TeaserGroupType teaserGroupType) {
        if (listener != null) {
            view.setTag(R.id.target_title, product.getName());
            view.setTag(R.id.target_link, product.getTarget());

            if(teaserGroupType != null)
            view.setTag(R.id.target_teaser_origin,teaserGroupType.ordinal());

            // Set position of the clicked teaser, for tracking purpose
            view.setTag(R.id.target_list_position, position);
            view.setTag(R.id.target_rr_hash, product.getRichRelevanceClickHash());
            view.setOnClickListener(listener);
        }
    }

    /**
     * Get the offset value for home page.
     */
    public static int getViewHolderOffset(Context context) {
        TypedValue outValue = new TypedValue();
        context.getResources().getValue(R.dimen.home_offset_percentage, outValue, true);
        float margin = outValue.getFloat();
        return (int) (DeviceInfoHelper.getWidth(context) * margin);
    }
}
