package com.mobile.utils.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.mobile.framework.objects.home.group.BaseTeaserGroupType;
import com.mobile.framework.objects.home.type.TeaserGroupType;
import com.mobile.interfaces.OnViewHolderClickListener;
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

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

/**
 * Class used to fill the grid catalog.<br>
 * Can be used to add a header and footer view.
 *
 * @author sergiopereira
 */
public class HomeAdapter extends RecyclerView.Adapter<BaseTeaserViewHolder> implements OnClickListener {

    public static final String TAG = HomeAdapter.class.getSimpleName();

    private final TeaserGroupType[] mTypes;

    private final LayoutInflater mInflater;

    private ArrayList<BaseTeaserGroupType> mGroupTeasers;

    private Context mContext;

    private OnViewHolderClickListener mOnViewHolderClicked;


//    public static class MainTeaser extends BaseTeaserViewHolder {
//        // Views
//        public ViewPager pager;
//        public CirclePageIndicator indicator;
//        // Constructor
//        public MainTeaser(View view) {
//            super(view);
//            pager = (ViewPager) view.findViewById(R.id.home_teaser_main_pager);
//            indicator = (CirclePageIndicator) view.findViewById(R.id.home_teaser_main_indicator);
//        }
//
//        public void bind(Context context, BaseTeaserGroupType group){
//            if(pager.getAdapter() == null) {
//                Log.i(TAG, "MAIN_TEASERS: ADAPTER IS NULL");
//                // Create adapter
//                HomeMainTeaserAdapter adapter = new HomeMainTeaserAdapter(context, group.getData(), null);
//                // Add adapter to pager
//                pager.setAdapter(adapter);
//                // Add pager to indicator
//                indicator.setViewPager(pager);
//            } else {
//                Log.i(TAG, "MAIN_TEASERS: ADAPTER IS NOT NULL");
//            }
//        }
//    }
//
//    public static class SmallTeaser extends BaseTeaserViewHolder {
//        // Data
//        public HorizontalListView horizontal;
//        public SmallTeaser(View view) {
//            super(view);
//            horizontal = (HorizontalListView) view.findViewById(R.id.home_teaser_small_horizontal_list);
//        }
//        public void bind(Context context, BaseTeaserGroupType group){
//            if(horizontal.getAdapter() == null) {
//                Log.i(TAG, "SMALL_TEASERS: ADAPTER IS NULL");
//                // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
//                horizontal.setHasFixedSize(true);
//                // RTL
//                Boolean isRTL = context.getResources().getBoolean(R.bool.is_bamilo_specific);
//                if (isRTL) horizontal.enableReverseLayout();
//                // Set adapter
//                horizontal.setAdapter(new HomeSmallTeaserAdapter(group.getData(), null));
//            } else {
//                Log.i(TAG, "SMALL_TEASERS: ADAPTER IS NOT NULL");
//            }
//        }
//    }
//
//    public static class CampaignTeaser extends BaseTeaserViewHolder {
//        // Data
//        public View container;
//        public ImageView image;
//        public TextView title;
//        public TextView timer;
//        public TextView sub;
//        public TextView more;
//        public CampaignTeaser(View view) {
//            super(view);
//            container = view.findViewById(R.id.home_teaser_campaign_container);
//            image = (ImageView) view.findViewById(R.id.home_teaser_campaign_image_container);
//            title = (TextView) view.findViewById(R.id.home_teaser_campaign_title);
//            timer = (TextView) view.findViewById(R.id.home_teaser_campaign_timer);
//            sub = (TextView) view.findViewById(R.id.home_teaser_campaign_subtitle);
//            more = (TextView) view.findViewById(R.id.home_teaser_campaign_more);
//        }
//
//        public void bind(Context context, BaseTeaserGroupType group) {
//            Log.i(TAG, "ON BIND CAMPAIGN_TEASERS: TAG IS NULL");
//            BaseTeaserObject campaign = group.getData().get(0);
//            //if (itemView.getTag(R.id.target_type) == null) {
//            //    Log.i(TAG, "CAMPAIGN_TEASERS: TAG IS NULL");
//            //itemView.setTag(R.id.target_type, group.getType());
//                RocketImageLoader.instance.loadImage(campaign.getImage(), image);
//                title.setText(campaign.getTitle());
//                timer.setText("OO:OO:OO");
//                sub.setText(campaign.getSubTitle());
//            //} else {
//            //    Log.i(TAG, "CAMPAIGN_TEASERS: TAG IS NOT NULL");
//            //}
//        }
//    }
//
//    public static class ShopTeaser extends BaseTeaserViewHolder {
//        // Data
//        public ViewGroup container;
//        public ViewGroup left;
//        public ViewGroup middle;
//        public ViewGroup right;
//        public ShopTeaser(View view) {
//            super(view);
//            container = (ViewGroup) view.findViewById(R.id.home_teaser_shop_container);
//            left = (ViewGroup) view.findViewById(R.id.home_teaser_shop_image_left);
//            middle = (ViewGroup) view.findViewById(R.id.home_teaser_shop_image_middle);
//            right = (ViewGroup) view.findViewById(R.id.home_teaser_shop_image_right);
//        }
//
//        public void bind(Context context, BaseTeaserGroupType group){
//            if (itemView.getTag(R.id.target_type) == null) {
//                Log.i(TAG, "SHOP_TEASERS: TAG IS NULL");
//                itemView.setTag(R.id.target_type, group.getType());
//                for (int i = 0; i < group.getData().size(); i++) {
//                    BaseTeaserObject object = group.getData().get(i);
//                    ViewGroup parent;
//                    switch (i) {
//                        case 0:
//                            parent = left;
//                            break;
//                        case 1:
//                            parent = middle;
//                            break;
//                        case 2:
//                        default:
//                            parent = right;
//                            break;
//                    }
//                    ((TextView) parent.findViewById(R.id.home_teaser_shop_title)).setText(object.getTitle());
//                    ((TextView) parent.findViewById(R.id.home_teaser_shop_sub_title)).setText(object.getSubTitle());
//                    RocketImageLoader.instance.loadImage(object.getImage(), (ImageView) parent.findViewById(R.id.home_teaser_item_image));
//                }
//            } else {
//                Log.i(TAG, "SHOP_TEASERS: TAG IS NOT NULL");
//            }
//        }
//    }
//
//    public static class ShopWeekTeaser extends BaseTeaserViewHolder {
//        // Data
//        public View container;
//        public ImageView left;
//        public ImageView right;
//        public ShopWeekTeaser(View view) {
//            super(view);
//            container = view.findViewById(R.id.home_teaser_shop_week_container);
//            View leftC = view.findViewById(R.id.home_teaser_shop_week_image_left);
//            View rightC = view.findViewById(R.id.home_teaser_shop_week_image_right);
//            left = (ImageView) leftC.findViewById(R.id.home_teaser_item_image);
//            right = (ImageView) rightC.findViewById(R.id.home_teaser_item_image);
//        }
//
//        public void bind(Context context, BaseTeaserGroupType group) {
//            if (itemView.getTag(R.id.target_type) == null) {
//                Log.i(TAG, "SHOP_WEEK_TEASERS: TAG IS NULL");
//                itemView.setTag(R.id.target_type, group.getType());
//                BaseTeaserObject leftX = group.getData().get(0);
//                BaseTeaserObject rightX = group.getData().get(1);
//                RocketImageLoader.instance.loadImage(leftX.getImage(), left);
//                RocketImageLoader.instance.loadImage(rightX.getImage(), right);
//            } else {
//                Log.i(TAG, "SHOP_WEEK_TEASERS: TAG IS NOT NULL");
//            }
//        }
//    }
//
//
//
//    public static class FeaturedTeaser extends BaseTeaserViewHolder {
//        // Data
//        public ExpandableGridViewComponent container;
//        public FeaturedTeaser(View view) {
//            super(view);
//            container = (ExpandableGridViewComponent) view.findViewById(R.id.home_teaser_featured_stores_container_grid);
//        }
//
//        public void bind(Context context, BaseTeaserGroupType group){
//            if(container.getAdapter() == null) {
//                Log.i(TAG, "FEATURED_TEASER: ADAPTER IS NULL");
//                // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
//                //horizontalListView.setGridLayoutManager(2);
//                //horizontalListView.setHasFixedSize(false);
//                container.setExpanded(true);
//                // Set adapter
//                //horizontalListView.setAdapter(new HomeFeaturedTeaserAdapter(group.getData(), null));
//                container.setAdapter(new HomeFeaturedTeaserAdapter(context, R.layout._def_home_teaser_featured_stores_item, group.getData(), null));
//            } else {
//                Log.i(TAG, "FEATURED_TEASER: ADAPTER IS NOT NULL");
//            }
//                /*
//                LayoutInflater mInflater = LayoutInflater.from(mContext);
//                featuredTeaser.horizontalListView.setColumnCount(2);
//                featuredTeaser.horizontalListView.setRowCount(2);
//
//                //for (int i = 0; i < mGroupTeasers.size(); i++) {
//                    //BaseTeaserGroupType mGroupTeaser = mGroupTeasers.get(0);
//                    View view = mInflater.inflate(R.layout._def_catalog_item_list_rounded, featuredTeaser.horizontalListView, false);
//                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
//                    params.rowSpec = GridLayout.spec(0, 1);
//                    params.columnSpec = GridLayout.spec(0, 1);
//                    view.setLayoutParams(params);
//                    featuredTeaser.horizontalListView.addView(view);//, params);
//                //}
//
//                //BaseTeaserGroupType mGroupTeaser01 = mGroupTeasers.get(1);
//                View view01 = mInflater.inflate(R.layout._def_catalog_item_list_rounded, featuredTeaser.horizontalListView, false);
//                GridLayout.LayoutParams params01 = new GridLayout.LayoutParams();
//                params01.rowSpec = GridLayout.spec(1, 1);
//                params01.columnSpec = GridLayout.spec(1, 1);
//                view01.setLayoutParams(params01);
//                featuredTeaser.horizontalListView.addView(view01);//), params01);
//                */
//
//
//
////                featuredTeaser.horizontalListView.removeAllViews();
////                LayoutInflater mInflater = LayoutInflater.from(mContext);
////
////
////                int total = 12;
////                int column = 2;
////                int row = total / column;
////                featuredTeaser.horizontalListView.setColumnCount(column);
////                featuredTeaser.horizontalListView.setRowCount(row + 1);
////                for(int i =0, c = 0, r = 0; i < total; i++, c++) {
////                    if(c == column) {
////                        c = 0;
////                        r++;
////                    }
////                    View view = mInflater.inflate(R.layout._def_home_teaser_featured_stores_item, featuredTeaser.horizontalListView, false);
////                    //ImageView oImageView = new ImageView(mContext);
////                    //oImageView.setImageResource(R.drawable.ic_gridview);
////                    GridLayout.LayoutParams param = new GridLayout.LayoutParams(view.getLayoutParams());
////                    //GridLayout.LayoutParams param = new GridLayout.LayoutParams();
////                    //param.width = GridLayout.LayoutParams.MATCH_PARENT;
////                    //param.height = GridLayout.LayoutParams.WRAP_CONTENT;
////                    //param.rightMargin = 5;
////                    //param.topMargin = 5;
////                    //param.setGravity(Gravity.CENTER);
////                    param.columnSpec = GridLayout.spec(c);
////                    param.rowSpec = GridLayout.spec(r);
////                    view.setLayoutParams(param);
////                    featuredTeaser.horizontalListView.addView(view);
////                }
//
//        }
//    }
//
//    public static class BrandTeaser extends BaseTeaserViewHolder {
//        public HorizontalListView horizontal;
//        public BrandTeaser(View view) {
//            super(view);
//            horizontal = (HorizontalListView) view.findViewById(R.id.home_teaser_brand_horizontal_list);
//        }
//
//        public void bind(Context context, BaseTeaserGroupType group){
//            if(horizontal.getAdapter() == null) {
//                Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NULL");
//                // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
//                horizontal.setHasFixedSize(true);
//                // RTL
//                Boolean isRTL = context.getResources().getBoolean(R.bool.is_bamilo_specific);
//                if (isRTL) horizontal.enableReverseLayout();
//                // Set adapter
//                horizontal.setAdapter(new HomeBrandTeaserAdapter(group.getData(), null));
//            } else {
//                Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NOT NULL");
//            }
//        }
//    }
//
//    public static class TopSellersTeaser extends BaseTeaserViewHolder {
//        // Data
//        public HorizontalListView horizontal;
//        public TopSellersTeaser(View view) {
//            super(view);
//            horizontal = (HorizontalListView) view.findViewById(R.id.home_teaser_top_sellers_horizontal_list);
//        }
//
//        public void bind(Context context, BaseTeaserGroupType group){
//            if(horizontal.getAdapter() == null) {
//                Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NULL");
//                // RecyclerView
//                // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
//                horizontal.setHasFixedSize(true);
//                // RTL
//                Boolean isRTL = context.getResources().getBoolean(R.bool.is_bamilo_specific);
//                if (isRTL) horizontal.enableReverseLayout();
//                // Set adapter
//                horizontal.setAdapter(new HomeTopSellersTeaserAdapter(group.getData(), null));
//            } else {
//                Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NOT NULL");
//            }
//        }
//    }
//
//    /**
//     * Provide a reference to the views for each data item.<br>
//     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br>
//     *
//     * @author sergiopereira
//     */
//    public static class EmptyViewHolder extends BaseTeaserViewHolder {
//
//        /**
//         * Constructor
//         *
//         * @param view -  the view holder
//         */
//        public EmptyViewHolder(View view) {
//            super(view);
//        }
//    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     *
     * @param context - the application context
     * @param data    - the array lisl
     */
    public HomeAdapter(Context context, ArrayList<BaseTeaserGroupType> data) {
        mContext = context;
        mGroupTeasers = data;
        mTypes = TeaserGroupType.values();
        mInflater = LayoutInflater.from(context);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public BaseTeaserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TeaserGroupType current = mTypes[viewType];
        OnClickListener listener = null;
        switch (current) {
            case MAIN_TEASERS:
                return new HomeMainTeaserHolder(parent.getContext(), mInflater.inflate(R.layout._def_home_teaser_main, parent, false), listener);
            case SMALL_TEASERS:
                return new HomeSmallTeaserHolder(parent.getContext(), mInflater.inflate(R.layout._def_home_teaser_small, parent, false), listener);
            case CAMPAIGN_TEASERS:
                return new HomeCampaignTeaserHolder(parent.getContext(), mInflater.inflate(R.layout._def_home_teaser_campaign, parent, false), listener);
            case SHOP_TEASERS:
                return new HomeShopTeaserHolder(parent.getContext(), mInflater.inflate(R.layout._def_home_teaser_shop, parent, false), listener);
            case SHOP_WEEK_TEASERS:
                return new HomeShopWeekTeaserHolder(parent.getContext(), mInflater.inflate(R.layout._def_home_teaser_shop_week, parent, false), listener);
            case FEATURED_STORES:
                return new HomeFeaturedTeaserHolder(parent.getContext(), mInflater.inflate(R.layout._def_home_teaser_featured_stores, parent, false), listener);
            case BRAND_TEASERS:
                return new HomeBrandTeaserHolder(parent.getContext(), mInflater.inflate(R.layout._def_home_teaser_brand, parent, false), listener);
            case TOP_SELLERS:
                return new HomeTopSellersTeaserHolder(parent.getContext(), mInflater.inflate(R.layout._def_home_teaser_top_sellers, parent, false), listener);
            case UNKNOWN:
            default:
                //return new EmptyViewHolder(new View(mContext));
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mGroupTeasers.get(position).getType().ordinal();
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.isEmpty(mGroupTeasers) ? 0 : mGroupTeasers.size();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        Log.i(TAG, "ON DETACHED FROM RECYCLER VIEW");
    }

    @Override
    public void onViewDetachedFromWindow(BaseTeaserViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Log.i(TAG, "ON DETACHED FROM WINDOW: " + holder.getClass().getSimpleName());
    }

    @Override
    public void onViewRecycled(BaseTeaserViewHolder holder) {
        super.onViewRecycled(holder);
        Log.i(TAG, "ON VIEW RECYCLED: " + holder.getClass().getSimpleName());
    }

    @Override
    public void onViewAttachedToWindow(BaseTeaserViewHolder holder) {
        Log.i(TAG, "ON VIEW ATTACHED TO WINDOW: " + holder.getClass().getSimpleName());
    }

    @Override
    public void onBindViewHolder(BaseTeaserViewHolder holder, int position) {
        Log.i(TAG, "ON BIND VIEW : " + holder.getClass().getSimpleName());
        // Get teaser group type
        BaseTeaserGroupType group = mGroupTeasers.get(position);
        Log.i(TAG, "ON BIND GROUP TEASER: " + group.getType().toString() + " HAS DATA: " + group.hasData());
        holder.onBind(group);

//        // Get enum from ordinal
//        switch (group.getType()) {
//            case MAIN_TEASERS:
//                holder.onBind(group);
//                break;
//            case SMALL_TEASERS:
//                HomeSmallTeaserHolder smallTeaserHolder = ((HomeSmallTeaserHolder) holder);
//                holder.onBind(group);
//                break;
//            case CAMPAIGN_TEASERS:
//                HomeCampaignTeaserHolder campaignTeaser = ((HomeCampaignTeaserHolder) holder);
//                holder.onBind(group);
//                break;
//            case SHOP_TEASERS:
//                HomeShopTeaserHolder shopTeaser = ((HomeShopTeaserHolder) holder);
//                shopTeaser.onBind(group);
//                break;
//            case SHOP_WEEK_TEASERS:
//                HomeShopWeekTeaserHolder shopWeekTeaser = ((HomeShopWeekTeaserHolder) holder);
//                holder.onBind(group);
//                break;
//            case BRAND_TEASERS:
//                HomeBrandTeaserHolder brandTeaser = ((HomeBrandTeaserHolder) holder);
//                holder.onBind(group);
//                break;
//            case FEATURED_STORES:
//                HomeFeaturedTeaserHolder featuredTeaser = ((HomeFeaturedTeaserHolder) holder);
//                holder.onBind(group);
//                break;
//            case TOP_SELLERS:
//                HomeTopSellersTeaserHolder topSellersTeaser = ((HomeTopSellersTeaserHolder) holder);
//                holder.onBind(group);
//                break;
//            case UNKNOWN:
//            default:
//                break;
//        }
    }

    /**
     * Get the product from the current data.
     *
     * @param position - the respective product position
     * @return BaseTeaserGroupType or null
     */
    public BaseTeaserGroupType getItem(int position) {
        return CollectionUtils.isEmpty(mGroupTeasers) ? null : mGroupTeasers.get(position);
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Case other sent to listener
        if (mOnViewHolderClicked != null)
            mOnViewHolderClicked.onViewHolderClick(this, (Integer) view.getTag(R.id.position));
    }

}
