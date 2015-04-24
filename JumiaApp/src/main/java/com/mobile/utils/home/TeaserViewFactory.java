package com.mobile.utils.home;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.components.ExpandableGridViewComponent;
import com.mobile.components.customfontviews.TextView;
import com.mobile.components.recycler.HorizontalListView;
import com.mobile.framework.objects.home.group.BaseTeaserGroupType;
import com.mobile.framework.objects.home.object.BaseTeaserObject;
import com.mobile.framework.utils.DeviceInfoHelper;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Created by spereira on 4/22/15.
 */
public class TeaserViewFactory {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();

    public static View onBindView(Context context, BaseTeaserGroupType group, ViewGroup parent, View.OnClickListener listener) {
        switch (group.getType()) {
            case MAIN_TEASERS:
                MainTeaser teaser = new MainTeaser(context, LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_main, parent, false));
                teaser.setOnClickListener(listener);
                teaser.bind(group);
                return teaser.itemView;
            case SMALL_TEASERS:
                SmallTeaser smallTeaser = new SmallTeaser(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_small, parent, false));
                smallTeaser.setOnClickListener(listener);
                smallTeaser.bind(context, group);
                return smallTeaser.itemView;
            case CAMPAIGN_TEASERS:
                CampaignTeaser campaignTeaser = new CampaignTeaser(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_campaign, parent, false));
                campaignTeaser.setOnClickListener(listener);
                campaignTeaser.bind(context, group);
                return campaignTeaser.itemView;
            case SHOP_TEASERS:
                ShopTeaser shopTeaser = new ShopTeaser(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_shop, parent, false));
                shopTeaser.setOnClickListener(listener);
                shopTeaser.bind(context, group);
                return shopTeaser.itemView;
            case SHOP_WEEK_TEASERS:
                ShopWeekTeaser shopWeekTeaser = new ShopWeekTeaser(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_shop_week, parent, false));
                shopWeekTeaser.setOnClickListener(listener);
                shopWeekTeaser.bind(context, group);
                return shopWeekTeaser.itemView;
            case FEATURED_STORES:
                FeaturedTeaser featuredTeaser = new FeaturedTeaser(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_featured_stores, parent, false));
                featuredTeaser.setOnClickListener(listener);
                featuredTeaser.bind(context, group);
                return featuredTeaser.itemView;
            case BRAND_TEASERS:
                BrandTeaser brandTeaser = new BrandTeaser(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_brand, parent, false));
                brandTeaser.setOnClickListener(listener);
                brandTeaser.bind(context, group);
                return brandTeaser.itemView;
            case TOP_SELLERS:
                TopSellersTeaser topSellersTeaser = new TopSellersTeaser(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_top_sellers, parent, false));
                topSellersTeaser.setOnClickListener(listener);
                topSellersTeaser.bind(context, group);
                return topSellersTeaser.itemView;
            case UNKNOWN:
            default:
                return new EmptyViewHolder(new View(context)).itemView;
        }

    }

    public static abstract class TeaserViewHolder extends RecyclerView.ViewHolder {

        public Context mContext;

        public TeaserViewHolder(View itemView) {
            super(itemView);
        }

        public TeaserViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
        }

        public abstract void bind(BaseTeaserGroupType group);

        public abstract void setOnClickListener(View.OnClickListener listener);
    }

    public static class MainTeaser extends TeaserViewHolder {
        // Views
        public ViewPager pager;
        public CirclePageIndicator indicator;
        private View.OnClickListener onClickListener;

        // Constructor
        public MainTeaser(View view) {
            super(view);
            //pager = (ViewPager) view.findViewById(R.id.home_teaser_main_pager);
            //indicator = (CirclePageIndicator) view.findViewById(R.id.home_teaser_main_indicator);
        }

        public MainTeaser(Context context, View itemView) {
            super(context, itemView);
            pager = (ViewPager) itemView.findViewById(R.id.home_teaser_main_pager);
            pager.setOffscreenPageLimit(2);
            //pager.setPageMargin(100);
            //pager.setPadding(200, 0, 200, 0);
            float width = DeviceInfoHelper.getWidth(context) * 0.15f;
            pager.setPadding((int)width, 0, (int)width, 0);
            //pager.setPadding((int)width, 0, 0, 0);
            //pager.setHorizontalFadingEdgeEnabled(true);
            //pager.setFadingEdgeLength(100);
            pager.setClipToPadding(false);
            //If hardware acceleration is enabled, you should also remove
            // clipping on the pager for its children.
            pager.setClipChildren(false);
            indicator = (CirclePageIndicator) itemView.findViewById(R.id.home_teaser_main_indicator);

            pager.setPageTransformer(false, new ViewPager.PageTransformer() {
                private static final float MIN_ALPHA = 0.4f;
                @Override
                public void transformPage(View page, float position) {
                    Log.i(TAG,"PAGE POSITION: " + position);
                    // TODO
                    position = position - 0.21031746F;
                    if(position <= -1.0F || position >= 1.0F) {
                        page.setAlpha(MIN_ALPHA);
                    } else if( position == 0.0F ) {
                        page.setAlpha(1.0F);
                    } else {
                        // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                        float alpha = 1.0F - Math.abs(position);
                        page.setAlpha(alpha < MIN_ALPHA ? MIN_ALPHA : alpha);
                    }
                }
            });
        }

        @Override
        public void bind(BaseTeaserGroupType group) {
            if (pager.getAdapter() == null) {
                Log.i(TAG, "MAIN_TEASERS: ADAPTER IS NULL");
                // Create adapter
                HomeMainTeaserAdapter adapter = new HomeMainTeaserAdapter(mContext, group.getData(), onClickListener);
                // Add adapter to pager
                pager.setAdapter(adapter);
                // Add pager to indicator
                indicator.setViewPager(pager);
            } else {
                Log.i(TAG, "MAIN_TEASERS: ADAPTER IS NOT NULL");
            }
        }

        @Override
        public void setOnClickListener(View.OnClickListener listener) {
            onClickListener = listener;
        }

    }

    public static class SmallTeaser extends RecyclerView.ViewHolder {
        // Data
        public HorizontalListView horizontal;
        private View.OnClickListener onClickListener;

        public SmallTeaser(View view) {
            super(view);
            horizontal = (HorizontalListView) view.findViewById(R.id.home_teaser_small_horizontal_list);
        }

        public void bind(Context context, BaseTeaserGroupType group) {
            if (horizontal.getAdapter() == null) {
                // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
                horizontal.setHasFixedSize(true);
                // RTL
                Boolean isRTL = context.getResources().getBoolean(R.bool.is_bamilo_specific);
                if (isRTL) horizontal.enableReverseLayout();
                // Set adapter
                horizontal.setAdapter(new HomeSmallTeaserAdapter(group.getData(), onClickListener));
            } else {
                Log.i(TAG, "SMALL_TEASERS: ADAPTER IS NOT NULL");
            }
        }

        public void setOnClickListener(View.OnClickListener listener) {
            onClickListener = listener;
        }
    }

    public static class CampaignTeaser extends RecyclerView.ViewHolder {
        // Data
        private View.OnClickListener onClickListener;
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

        public void bind(Context context, BaseTeaserGroupType group) {
            Log.i(TAG, "ON BIND CAMPAIGN_TEASERS: TAG IS NULL");
            BaseTeaserObject campaign = group.getData().get(0);
            RocketImageLoader.instance.loadImage(campaign.getImagePhone(), image, null, R.drawable.no_image_large);
            title.setText(campaign.getTitle());
            timer.setText("OO:OO:OO");
            sub.setText(campaign.getSubTitle());
            setClickableView(container, campaign);
            setClickableView(more, campaign);
        }

        private void setClickableView(View view, BaseTeaserObject teaser) {
            if (onClickListener != null) {
                view.setTag(R.id.target_title, teaser.getTitle());
                view.setTag(R.id.target_type, teaser.getTargetType());
                view.setTag(R.id.target_url, teaser.getUrl());
                view.setOnClickListener(onClickListener);
            }
        }

        public void setOnClickListener(View.OnClickListener listener) {
            onClickListener = listener;
        }
    }

    public static class ShopTeaser extends RecyclerView.ViewHolder {
        // Data
        private View.OnClickListener onClickListener;
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

        public void bind(Context context, BaseTeaserGroupType group) {
            if (itemView.getTag(R.id.target_type) == null) {
                Log.i(TAG, "SHOP_TEASERS: TAG IS NULL");
                itemView.setTag(R.id.target_type, group.getType());
                for (int i = 0; i < group.getData().size(); i++) {
                    BaseTeaserObject object = group.getData().get(i);
                    ViewGroup parent;
                    switch (i) {
                        case 0:
                            parent = left;
                            break;
                        case 1:
                            parent = middle;
                            break;
                        case 2:
                        default:
                            parent = right;
                            break;
                    }
                    ((TextView) parent.findViewById(R.id.home_teaser_shop_title)).setText(object.getTitle());
                    ((TextView) parent.findViewById(R.id.home_teaser_shop_sub_title)).setText(object.getSubTitle());
                    RocketImageLoader.instance.loadImage(object.getImagePhone(), (ImageView) parent.findViewById(R.id.home_teaser_item_image), parent.findViewById(R.id.home_teaser_item_progress), R.drawable.no_image_large);
                    setClickableView(parent, object);
                }
            } else {
                Log.i(TAG, "SHOP_TEASERS: TAG IS NOT NULL");
            }
        }

        private void setClickableView(View view, BaseTeaserObject teaser) {
            if (onClickListener != null) {
                view.setTag(R.id.target_title, teaser.getTitle());
                view.setTag(R.id.target_type, teaser.getTargetType());
                view.setTag(R.id.target_url, teaser.getUrl());
                view.setOnClickListener(onClickListener);
            }
        }

        public void setOnClickListener(View.OnClickListener listener) {
            onClickListener = listener;
        }

    }

    public static class ShopWeekTeaser extends RecyclerView.ViewHolder {
        // Data
        private View.OnClickListener onClickListener;
        public View container;
        public ImageView left;
        public ViewGroup leftC;
        public ViewGroup rightC;
        public View leftP;
        public ImageView right;
        public View rightP;

        public ShopWeekTeaser(View view) {
            super(view);
            container = view.findViewById(R.id.home_teaser_shop_week_container);
            leftC = (ViewGroup) view.findViewById(R.id.home_teaser_shop_week_image_left);
            left = (ImageView) leftC.findViewById(R.id.home_teaser_item_image);
            leftP = leftC.findViewById(R.id.home_teaser_item_progress);
            rightC = (ViewGroup) view.findViewById(R.id.home_teaser_shop_week_image_right);
            right = (ImageView) rightC.findViewById(R.id.home_teaser_item_image);
            rightP = rightC.findViewById(R.id.home_teaser_item_progress);
        }

        public void bind(Context context, BaseTeaserGroupType group) {
            if (itemView.getTag(R.id.target_type) == null) {
                Log.i(TAG, "SHOP_WEEK_TEASERS: TAG IS NULL");
                itemView.setTag(R.id.target_type, group.getType());
                BaseTeaserObject leftX = group.getData().get(0);
                BaseTeaserObject rightX = group.getData().get(1);
                RocketImageLoader.instance.loadImage(leftX.getImagePhone(), left, leftP, R.drawable.no_image_large);
                RocketImageLoader.instance.loadImage(rightX.getImagePhone(), right, rightP, R.drawable.no_image_large);
                setClickableView(leftC, leftX);
                setClickableView(rightC, rightX);
            } else {
                Log.i(TAG, "SHOP_WEEK_TEASERS: TAG IS NOT NULL");
            }
        }

        private void setClickableView(View view, BaseTeaserObject teaser) {
            if (onClickListener != null) {
                view.setTag(R.id.target_title, teaser.getTitle());
                view.setTag(R.id.target_type, teaser.getTargetType());
                view.setTag(R.id.target_url, teaser.getUrl());
                view.setOnClickListener(onClickListener);
            }
        }


        public void setOnClickListener(View.OnClickListener listener) {
            onClickListener = listener;
        }
    }


    public static class FeaturedTeaser extends RecyclerView.ViewHolder {
        // Data
        public ExpandableGridViewComponent container;
        private View.OnClickListener onClickListener;

        public FeaturedTeaser(View view) {
            super(view);
            container = (ExpandableGridViewComponent) view.findViewById(R.id.home_teaser_featured_stores_container_grid);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            onClickListener = listener;
        }

        public void bind(Context context, BaseTeaserGroupType group) {
            if(container.getAdapter() == null) {
                Log.i(TAG, "FEATURED_TEASER: ADAPTER IS NULL");
                // Expand grid view
                container.setExpanded(true);
                // Set adapter
                container.setAdapter(new HomeFeaturedTeaserAdapter2(context, R.layout._def_home_teaser_featured_stores_item, group.getData()));
            } else {
                Log.i(TAG, "FEATURED_TEASER: ADAPTER IS NOT NULL");
            }
        }
    }

    public static class BrandTeaser extends RecyclerView.ViewHolder {
        public HorizontalListView horizontal;
        private View.OnClickListener onClickListener;

        public BrandTeaser(View view) {
            super(view);
            horizontal = (HorizontalListView) view.findViewById(R.id.home_teaser_brand_horizontal_list);
        }

        public void bind(Context context, BaseTeaserGroupType group) {
            if (horizontal.getAdapter() == null) {
                Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NULL");
                // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
                horizontal.setHasFixedSize(true);
                // RTL
                Boolean isRTL = context.getResources().getBoolean(R.bool.is_bamilo_specific);
                if (isRTL) horizontal.enableReverseLayout();
                // Set adapter
                horizontal.setAdapter(new HomeBrandTeaserAdapter(group.getData(), onClickListener));
            } else {
                Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NOT NULL");
            }
        }

        public void setOnClickListener(View.OnClickListener listener) {
            onClickListener = listener;
        }
    }

    public static class TopSellersTeaser extends RecyclerView.ViewHolder {
        // Data
        public HorizontalListView horizontal;
        private View.OnClickListener onClickListener;

        public TopSellersTeaser(View view) {
            super(view);
            horizontal = (HorizontalListView) view.findViewById(R.id.home_teaser_top_sellers_horizontal_list);
        }

        public void bind(Context context, BaseTeaserGroupType group) {
            if (horizontal.getAdapter() == null) {
                Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NULL");
                // RecyclerView
                // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
                horizontal.setHasFixedSize(true);
                // RTL
                Boolean isRTL = context.getResources().getBoolean(R.bool.is_bamilo_specific);
                if (isRTL) horizontal.enableReverseLayout();
                // Set adapter
                horizontal.setAdapter(new HomeTopSellersTeaserAdapter(group.getData(), onClickListener));
            } else {
                Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NOT NULL");
            }
        }

        public void setOnClickListener(View.OnClickListener listener) {
            onClickListener = listener;
        }
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View view) {
            super(view);
        }
    }


}
