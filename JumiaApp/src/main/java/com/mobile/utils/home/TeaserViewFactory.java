package com.mobile.utils.home;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.newFramework.objects.home.object.BaseTeaserObject;
import com.mobile.newFramework.objects.home.type.TeaserGroupType;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.utils.home.holder.BaseTeaserViewHolder;
import com.mobile.utils.home.holder.HomeBrandTeaserHolder;
import com.mobile.utils.home.holder.HomeCampaignTeaserHolder;
import com.mobile.utils.home.holder.HomeFeaturedTeaserHolder;
import com.mobile.utils.home.holder.HomeMainTeaserHolder;
import com.mobile.utils.home.holder.HomeShopTeaserHolder;
import com.mobile.utils.home.holder.HomeShopWeekTeaserHolder;
import com.mobile.utils.home.holder.HomeSmallTeaserHolder;
import com.mobile.utils.home.holder.HomeTopSellersTeaserHolder;
import com.mobile.view.R;

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
            String title = !TextUtils.isEmpty(teaser.getName()) ? teaser.getName() : teaser.getTitle();
            view.setTag(R.id.target_title, title);
            view.setTag(R.id.target_type, teaser.getTargetType());
            view.setTag(R.id.target_url, teaser.getUrl());
            view.setTag(R.id.target_teaser_origin, teaser.getTeaserTypeId());
            // Set position of the clicked teaser, for tracking purpose
            view.setTag(R.id.target_list_position, position);
            view.setTag(R.id.target_sku, teaser.getSku());

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
