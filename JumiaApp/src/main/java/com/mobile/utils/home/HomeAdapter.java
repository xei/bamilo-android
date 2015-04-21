package com.mobile.utils.home;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.components.HorizontalListView;
import com.mobile.components.customfontviews.TextView;
import com.mobile.framework.objects.home.group.BaseTeaserType;
import com.mobile.framework.objects.home.object.BaseTeaserObject;
import com.mobile.framework.objects.home.type.EnumTeaserGroupType;
import com.mobile.interfaces.OnViewHolderClickListener;
import com.mobile.utils.catalog.CatalogGridView;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;
import com.viewpagerindicator.CirclePageIndicator;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

/**
 * Class used to fill the grid catalog.<br>
 * Can be used to add a header and footer view.
 *
 * @author sergiopereira
 */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnClickListener {

    public static final String TAG = HomeAdapter.class.getSimpleName();

    private ArrayList<BaseTeaserType> mGroupTeasers;

    private Context mContext;

    private OnViewHolderClickListener mOnViewHolderClicked;

    public static class MainTeaser extends RecyclerView.ViewHolder {
        // Views
        public ViewPager pager;
        public CirclePageIndicator indicator;
        // Constructor
        public MainTeaser(View view) {
            super(view);
            pager = (ViewPager) view.findViewById(R.id.home_teaser_main_pager);
            indicator = (CirclePageIndicator) view.findViewById(R.id.home_teaser_main_indicator);
        }
    }

    public static class SmallTeaser extends RecyclerView.ViewHolder {
        // Data
        public HorizontalListView horizontal;
        public SmallTeaser(View view) {
            super(view);
            horizontal = (HorizontalListView) view.findViewById(R.id.home_teaser_small_horizontal_list);
        }
    }

    public static class CampaignTeaser extends RecyclerView.ViewHolder {
        // Data
        public View container;
        public ImageView image;
        public TextView title;
        public TextView timer;
        public TextView sub;
        public TextView more;
        public CampaignTeaser(View view) {
            super(view);
            container = view.findViewById(R.id.home_teaser_campaign_container);
            image = (ImageView) view.findViewById(R.id.home_teaser_campaign_image);
            title = (TextView) view.findViewById(R.id.home_teaser_campaign_title);
            timer = (TextView) view.findViewById(R.id.home_teaser_campaign_timer);
            sub = (TextView) view.findViewById(R.id.home_teaser_campaign_subtitle);
            more = (TextView) view.findViewById(R.id.home_teaser_campaign_more);
        }
    }

    public static class ShopTeaser extends RecyclerView.ViewHolder {
        // Data
        public ViewGroup container;
        public ViewGroup left;
        public ViewGroup middle;
        public ViewGroup right;
        public ShopTeaser(View view) {
            super(view);
            container = (ViewGroup) view.findViewById(R.id.home_teaser_shop_container);
            left = (ViewGroup) view.findViewById(R.id.home_teaser_shop_image_left);
            middle = (ViewGroup) view.findViewById(R.id.home_teaser_shop_image_middle);
            right = (ViewGroup) view.findViewById(R.id.home_teaser_shop_image_right);
        }
    }

    public static class ShopWeekTeaser extends RecyclerView.ViewHolder {
        // Data
        public View container;
        public ImageView left;
        public ImageView right;
        public ShopWeekTeaser(View view) {
            super(view);
            container = view.findViewById(R.id.home_teaser_shop_week_container);
            left = (ImageView) view.findViewById(R.id.home_teaser_shop_week_image_left);
            right = (ImageView) view.findViewById(R.id.home_teaser_shop_week_image_right);
        }
    }



    public static class FeaturedTeaser extends RecyclerView.ViewHolder {
        // Data
        public CatalogGridView container;
        public FeaturedTeaser(View view) {
            super(view);
            container = (CatalogGridView) view.findViewById(R.id.home_teaser_featured_stores_container_grid);
        }
    }

    public static class BrandTeaser extends RecyclerView.ViewHolder {
        public HorizontalListView horizontal;
        public BrandTeaser(View view) {
            super(view);
            horizontal = (HorizontalListView) view.findViewById(R.id.home_teaser_brand_horizontal_list);
        }
    }

    public static class TopSellersTeaser extends RecyclerView.ViewHolder {
        // Data
        public HorizontalListView horizontal;
        public TopSellersTeaser(View view) {
            super(view);
            horizontal = (HorizontalListView) view.findViewById(R.id.home_teaser_top_sellers_horizontal_list);
        }
    }

    /**
     * Provide a reference to the views for each data item.<br>
     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br>
     *
     * @author sergiopereira
     */
    public static class EmptyViewHolder extends RecyclerView.ViewHolder {

        /**
         * Constructor
         *
         * @param view -  the view holder
         */
        public EmptyViewHolder(View view) {
            super(view);
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     *
     * @param context - the application context
     * @param data    - the array lisl
     */
    public HomeAdapter(Context context, ArrayList<BaseTeaserType> data) {
        mContext = context;
        mGroupTeasers = data;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        EnumTeaserGroupType[] types = EnumTeaserGroupType.values();
        EnumTeaserGroupType current = types[viewType];
        // Get enum from ordinal
        switch (current) {
            case MAIN_TEASERS:
                return new MainTeaser(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_main, parent, false));
            case SMALL_TEASERS:
                return new SmallTeaser(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_small, parent, false));
            case CAMPAIGN_TEASERS:
                return new CampaignTeaser(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_campaign, parent, false));
            case SHOP_TEASERS:
                return new ShopTeaser(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_shop, parent, false));
            case SHOP_WEEK_TEASERS:
                return new ShopWeekTeaser(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_shop_week, parent, false));
            case FEATURED_STORES:
                //return new FeaturedTeaser(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_featured_stores, parent, false));
                return new FeaturedTeaser(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_featured_stores_2, parent, false));
            case BRAND_TEASERS:
                return new BrandTeaser(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_brand, parent, false));
            case TOP_SELLERS:
                return new TopSellersTeaser(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_top_sellers, parent, false));
            case UNKNOWN:
            default:
                return new EmptyViewHolder(new View(mContext));
        }
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)
     */
    @Override
    public int getItemViewType(int position) {
        return mGroupTeasers.get(position).getType().ordinal();
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemCount()
     */
    @Override
    public int getItemCount() {
        // Return the size of your data set (invoked by the layout manager)
        return CollectionUtils.isEmpty(mGroupTeasers) ? 0 : mGroupTeasers.size();
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get teaser group type
        BaseTeaserType group = mGroupTeasers.get(position);
        Log.i(TAG, "GROUP TEASER: " + group.getType().toString() + " HAS DATA: " + group.hasData());
        // Get enum from ordinal
        switch (group.getType()) {
            case MAIN_TEASERS:
                MainTeaser mainTeaserHolder = ((MainTeaser) holder);
                if(mainTeaserHolder.pager.getAdapter() == null) {
                    Log.i(TAG, "MAIN_TEASERS: ADAPTER IS NULL");
                    // Create adapter
                    HomeMainTeaserAdapter adapter = new HomeMainTeaserAdapter(mContext, group.getData());
                    // Add adapter to pager
                    mainTeaserHolder.pager.setAdapter(adapter);
                    // Add pager to indicator
                    mainTeaserHolder.indicator.setViewPager(mainTeaserHolder.pager);
                } else {
                    Log.i(TAG, "MAIN_TEASERS: ADAPTER IS NOT NULL");
                }
                break;
            case SMALL_TEASERS:
                SmallTeaser smallTeaserHolder = ((SmallTeaser) holder);
                if(smallTeaserHolder.horizontal.getAdapter() == null) {
                    Log.i(TAG, "SMALL_TEASERS: ADAPTER IS NULL");
                    // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
                    smallTeaserHolder.horizontal.setHasFixedSize(true);
                    // RTL
                    Boolean isRTL = mContext.getResources().getBoolean(R.bool.is_bamilo_specific);
                    if (isRTL) smallTeaserHolder.horizontal.enableReverseLayout();
                    // Set adapter
                    smallTeaserHolder.horizontal.setAdapter(new HomeSmallTeaserAdapter(group.getData()));
                } else {
                    Log.i(TAG, "SMALL_TEASERS: ADAPTER IS NOT NULL");
                }
                break;
            case CAMPAIGN_TEASERS:
                Log.i(TAG, "ON BIND VIEW: CAMPAIGN_TEASERS");
                CampaignTeaser campaignTeaser = ((CampaignTeaser) holder);
                BaseTeaserObject campaign = group.getData().get(0);
                RocketImageLoader.instance.loadImage(campaign.getImagePhone(), campaignTeaser.image);
                campaignTeaser.title.setText(campaign.getTitle());
                campaignTeaser.timer.setText("OO:OO:OO");
                campaignTeaser.sub.setText(campaign.getSubTitle());
                break;
            case SHOP_TEASERS:
                Log.i(TAG, "ON BIND VIEW: SHOP_TEASERS");
                ShopTeaser shopTeaser = ((ShopTeaser) holder);
                for (int i = 0; i < group.getData().size(); i++) {
                    BaseTeaserObject object = group.getData().get(i);
                    ViewGroup parent;
                    switch (i) {
                        case 0:
                            parent = shopTeaser.left;
                            break;
                        case 1:
                            parent = shopTeaser.middle;
                            break;
                        case 2:
                        default:
                            parent = shopTeaser.right;
                            break;
                    }
                    ((TextView) parent.findViewById(R.id.home_teaser_shop_title)).setText(object.getTitle());
                    ((TextView) parent.findViewById(R.id.home_teaser_shop_sub_title)).setText(object.getSubTitle());
                    RocketImageLoader.instance.loadImage(object.getImagePhone(), (ImageView) parent.findViewById(R.id.home_teaser_shop_image));
                }
                break;
            case SHOP_WEEK_TEASERS:
                ShopWeekTeaser shopWeekTeaser = ((ShopWeekTeaser) holder);
                BaseTeaserObject leftX = group.getData().get(0);
                BaseTeaserObject rightX = group.getData().get(1);
                RocketImageLoader.instance.loadImage(leftX.getImagePhone(), shopWeekTeaser.left);
                RocketImageLoader.instance.loadImage(rightX.getImagePhone(), shopWeekTeaser.right);
                break;
            case BRAND_TEASERS:
                BrandTeaser brandTeaser = ((BrandTeaser) holder);
                if(brandTeaser.horizontal.getAdapter() == null) {
                    Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NULL");
                    // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
                    brandTeaser.horizontal.setHasFixedSize(true);
                    // RTL
                    Boolean isRTL = mContext.getResources().getBoolean(R.bool.is_bamilo_specific);
                    if (isRTL) brandTeaser.horizontal.enableReverseLayout();
                    // Set adapter
                    brandTeaser.horizontal.setAdapter(new HomeBrandTeaserAdapter(group.getData()));
                } else {
                    Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NOT NULL");
                }
                break;
            case FEATURED_STORES:
                FeaturedTeaser featuredTeaser = ((FeaturedTeaser) holder);
                if(featuredTeaser.container.getAdapter() == null) {

                    Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NULL");
                    // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
                    featuredTeaser.container.setGridLayoutManager(2);
                    featuredTeaser.container.setHasFixedSize(true);
                    // Set adapter
                    featuredTeaser.container.setAdapter(new HomeFeaturedTeaserAdapter(group.getData()));
                } else {
                    Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NOT NULL");
                }
                /*
                LayoutInflater mInflater = LayoutInflater.from(mContext);
                featuredTeaser.container.setColumnCount(2);
                featuredTeaser.container.setRowCount(2);

                //for (int i = 0; i < mGroupTeasers.size(); i++) {
                    //BaseTeaserType mGroupTeaser = mGroupTeasers.get(0);
                    View view = mInflater.inflate(R.layout._def_catalog_item_list_rounded, featuredTeaser.container, false);
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.rowSpec = GridLayout.spec(0, 1);
                    params.columnSpec = GridLayout.spec(0, 1);
                    view.setLayoutParams(params);
                    featuredTeaser.container.addView(view);//, params);
                //}

                //BaseTeaserType mGroupTeaser01 = mGroupTeasers.get(1);
                View view01 = mInflater.inflate(R.layout._def_catalog_item_list_rounded, featuredTeaser.container, false);
                GridLayout.LayoutParams params01 = new GridLayout.LayoutParams();
                params01.rowSpec = GridLayout.spec(1, 1);
                params01.columnSpec = GridLayout.spec(1, 1);
                view01.setLayoutParams(params01);
                featuredTeaser.container.addView(view01);//), params01);
                */



//                featuredTeaser.container.removeAllViews();
//                LayoutInflater mInflater = LayoutInflater.from(mContext);
//
//
//                int total = 12;
//                int column = 2;
//                int row = total / column;
//                featuredTeaser.container.setColumnCount(column);
//                featuredTeaser.container.setRowCount(row + 1);
//                for(int i =0, c = 0, r = 0; i < total; i++, c++) {
//                    if(c == column) {
//                        c = 0;
//                        r++;
//                    }
//                    View view = mInflater.inflate(R.layout._def_home_teaser_featured_stores_item, featuredTeaser.container, false);
//                    //ImageView oImageView = new ImageView(mContext);
//                    //oImageView.setImageResource(R.drawable.ic_gridview);
//                    GridLayout.LayoutParams param = new GridLayout.LayoutParams(view.getLayoutParams());
//                    //GridLayout.LayoutParams param = new GridLayout.LayoutParams();
//                    //param.width = GridLayout.LayoutParams.MATCH_PARENT;
//                    //param.height = GridLayout.LayoutParams.WRAP_CONTENT;
//                    //param.rightMargin = 5;
//                    //param.topMargin = 5;
//                    //param.setGravity(Gravity.CENTER);
//                    param.columnSpec = GridLayout.spec(c);
//                    param.rowSpec = GridLayout.spec(r);
//                    view.setLayoutParams(param);
//                    featuredTeaser.container.addView(view);
//                }



                break;
            case TOP_SELLERS:
                TopSellersTeaser topSellersTeaser = ((TopSellersTeaser) holder);
                if(topSellersTeaser.horizontal.getAdapter() == null) {
                    Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NULL");
                    // RecyclerView
                    // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
                    topSellersTeaser.horizontal.setHasFixedSize(true);
                    // RTL
                    Boolean isRTL = mContext.getResources().getBoolean(R.bool.is_bamilo_specific);
                    if (isRTL) topSellersTeaser.horizontal.enableReverseLayout();
                    // Set adapter
                    topSellersTeaser.horizontal.setAdapter(new HomeTopSellersTeaserAdapter(mContext, group.getData()));
                } else {
                    Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NOT NULL");
                }
                break;
            case UNKNOWN:
            default:
                //((EmptyViewHolder) holder).container.setBackgroundResource(R.color.material_blue_grey_800);
                break;
        }
    }


    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)

    @Override
    public void onBindViewHolder(EmptyViewHolder holder, int position) {
        // Set animation
        setAnimation(holder, position);
        // Case header
        if(isHeader(position)) return;
        // Case footer
        if(isFooter(position)) return;
        // Get real position
        position = getRealPosition(position);
        // Get item
        Product item = mDataSet.get(position);
        // Set name
        holder.name.setText(item.getName());
        // Set brand
        holder.brand.setText(item.getBrand());
        // Set is new image
        holder.recent.setSelected(item.getAttributes().isNew());
        // Set image
        RocketImageLoader.instance.loadImage(item.getFirstImageURL(), holder.image, holder.progress, R.drawable.no_image_small);
        // Set is favorite image
        setFavourite(holder, item, position);
        // Set rating and reviews
        setSpecificViewForListLayout(holder, item);
        // Set prices
        setProductPrice(holder, item);
        // Set the parent layout
        holder.itemView.setTag(R.id.position, position);
        holder.itemView.setOnClickListener(this);
    }
    */


    /**
     * Get the product from the current data.
     *
     * @param position - the respective product position
     * @return BaseTeaserType or null
     */
    public BaseTeaserType getItem(int position) {
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
