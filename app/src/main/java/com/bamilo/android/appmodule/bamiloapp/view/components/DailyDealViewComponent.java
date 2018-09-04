package com.bamilo.android.appmodule.bamiloapp.view.components;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.adapters.DailyDealProductListAdapter;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel;
import com.bamilo.android.appmodule.bamiloapp.view.widget.LimitedCountLinearLayoutManager;
import com.bamilo.android.appmodule.modernbamilo.util.storage.SharedPreferencesHelperKt;
import com.bamilo.android.framework.service.utils.TextUtils;
import java.util.List;
import java.util.Locale;

/**
 * Created on 10/15/2017.
 */

public class DailyDealViewComponent extends BaseViewComponent<DailyDealViewComponent.DealItem> {

    private Handler mHandler;
    private DealItem mDealItem;
    private View rootView;
    private TextView tvDealCountDown;
    private OnCountDownDealItemClickListener onCountDownDealItemClickListener;
    private Locale mLocale;
    private Runnable mCountDownRunnable = new Runnable() {
        @Override
        public void run() {
            if (!paused) {
                calculateRemainingTime();
                mHandler.postDelayed(this, 1000);
            }
        }
    };
    private boolean paused;

    public DailyDealViewComponent() {
        mHandler = new Handler();
        mLocale = new Locale("fa", "ir");
    }

    @Override
    public View getView(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.component_daily_deals, null);
        paused = true;
        if (mDealItem != null) {
            // Deal count-down prefs
            if (mDealItem.countDownRemainingSeconds > -1) {
                if (mDealItem.countDownRemainingSeconds
                        > System.currentTimeMillis() / 1000 - mDealItem.countDownStartTimeSeconds) {
                    tvDealCountDown = (TextView) rootView.findViewById(R.id.tvDealCountDown);
                    tvDealCountDown.setVisibility(View.VISIBLE);
                    if (TextUtils.validateColorString(mDealItem.countDownTextColor)) {
                        tvDealCountDown
                                .setTextColor(Color.parseColor(mDealItem.countDownTextColor));
                    }
                    start();
                } else {
                    rootView.setVisibility(View.GONE);
                    return rootView;
                }
            }

            // Component prefs
            rootView.setVisibility(View.VISIBLE);
            if (TextUtils.validateColorString(mDealItem.componentBackgroundColor)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    rootView.findViewById(R.id.llDailyDealsComponentContainer)
                            .setBackground(createComponentBackground(context,
                                    Color.parseColor(mDealItem.componentBackgroundColor)));
                } else {
                    rootView.findViewById(R.id.llDailyDealsComponentContainer)
                            .setBackgroundDrawable(createComponentBackground(context,
                                    Color.parseColor(mDealItem.componentBackgroundColor)));
                }
            }

            // Deal title prefs
            TextView tvDealTitle = (TextView) rootView.findViewById(R.id.tvDealTitle);
            tvDealTitle.setText(mDealItem.dealTitle);
            if (TextUtils.validateColorString(mDealItem.dealTitleColor)) {
                tvDealTitle.setTextColor(Color.parseColor(mDealItem.dealTitleColor));
            }

            // More items button
            if (TextUtils.isNotEmpty(mDealItem.moreOptionsTitle) &&
                    TextUtils.isNotEmpty(mDealItem.moreOptionsTargetLink)) {
                TextView tvDealMoreItems = (TextView) rootView.findViewById(R.id.tvDealMoreItems);
                tvDealMoreItems.setVisibility(View.VISIBLE);
                tvDealMoreItems.setText(mDealItem.moreOptionsTitle);
                if (TextUtils.validateColorString(mDealItem.moreOptionsTitleColor)) {
                    tvDealMoreItems.setTextColor(Color.parseColor(mDealItem.moreOptionsTitleColor));
                }
                tvDealMoreItems.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCountDownDealItemClickListener != null) {
                            onCountDownDealItemClickListener
                                    .onMoreButtonClicked(v, mDealItem.moreOptionsTargetLink);
                        }
                        if (mPage != null) {
                            String category = String.format(Locale.US, "%s+%s_%d_%d", mPage,
                                    ComponentType.DailyDeal.toString(),
                                    mDealItem.dealProducts.get(mInstanceIndex).teaserId,
                                    mInstanceIndex);

                            String action = EventActionKeys.TEASER_TAPPED;
                            String label = mDealItem.moreOptionsTargetLink;
                            SimpleEventModel sem = new SimpleEventModel(category, action, label,
                                    SimpleEventModel.NO_VALUE);
                            TrackerManager
                                    .trackEvent(v.getContext(), EventConstants.TeaserTapped, sem);
                            SharedPreferencesHelperKt
                                    .setHomePageItemsPurchaseTrack(v.getContext(), category, label,
                                            true);
                        }
                    }
                });
            }

            // Product list
            DailyDealProductListAdapter adapter = new DailyDealProductListAdapter(
                    mDealItem.dealProducts);
            adapter.setOnDealProductItemClickListener(
                    (v, product, position) -> {
                        if (onCountDownDealItemClickListener != null) {
                            onCountDownDealItemClickListener.onProductItemClicked(v, product);
                        }

                        if (mPage != null) {
                            String category = String.format(Locale.US, "%s+%s_%d_%d", mPage,
                                    ComponentType.DailyDeal.toString(), product.teaserId,
                                    position);
                            String action = EventActionKeys.TEASER_TAPPED;
                            String label = product.sku;
                            SimpleEventModel sem = new SimpleEventModel(category, action, label,
                                    SimpleEventModel.NO_VALUE);
                            TrackerManager
                                    .trackEvent(v.getContext(), EventConstants.TeaserTapped,
                                            sem);
                            SharedPreferencesHelperKt
                                    .setHomePageItemsPurchaseTrack(v.getContext(), category,
                                            label, true);
                        }
                    });

            RecyclerView rvDealProducts = rootView.findViewById(R.id.rvDealProducts);
            rvDealProducts.setAdapter(adapter);
            LimitedCountLinearLayoutManager layoutManager = new LimitedCountLinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL, false,
                    context.getResources().getInteger(R.integer.daily_deals_visible_items_count));
            layoutManager.setLastItemVisiblePartAmount(0.25F);
            rvDealProducts.setLayoutManager(layoutManager);
        }
        return rootView;
    }

    private GradientDrawable createComponentBackground(Context context, int color) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(color);
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = Math.max(hsv[2] - .1F, 0);
        int borderColor = Color.HSVToColor(hsv);
        final int STROKE_WIDTH = 1;
        shape.setStroke(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, STROKE_WIDTH,
                        context.getResources().getDisplayMetrics()),
                borderColor);

        return shape;
    }

    private String calculateRemainingTime() {
        if (tvDealCountDown != null) {
            long secondsSpent =
                    System.currentTimeMillis() / 1000 - mDealItem.countDownStartTimeSeconds;
            if (secondsSpent < mDealItem.countDownRemainingSeconds) {
                long secondsRemaining = mDealItem.countDownRemainingSeconds - secondsSpent;
                long hours = secondsRemaining / 3600;
                long minutes = secondsRemaining / 60 % 60;
                long seconds = secondsRemaining % 60;
                String remainingText = String
                        .format(mLocale, "%02d:%02d:%02d", hours, minutes, seconds);
                tvDealCountDown.setText(remainingText);
            } else {
                rootView.setVisibility(View.GONE);
                pause();
            }
        }
        return null;
    }

    @Override
    public void setContent(DealItem content) {
        this.mDealItem = content;
    }

    public void pause() {
        paused = true;
    }

    public void start() {
        if (paused) {
            paused = false;
            mHandler.post(mCountDownRunnable);
        }
    }

    public OnCountDownDealItemClickListener getOnCountDownDealItemClickListener() {
        return onCountDownDealItemClickListener;
    }

    public void setOnCountDownDealItemClickListener(
            OnCountDownDealItemClickListener onCountDownDealItemClickListener) {
        this.onCountDownDealItemClickListener = onCountDownDealItemClickListener;
    }

    public static class DealItem {

        // Component background color
        public String componentBackgroundColor;

        // Component title
        public String dealTitle;
        public String dealTitleColor;

        // More options
        public String moreOptionsTitle;
        public String moreOptionsTitleColor;
        public String moreOptionsTargetLink;

        // Deal count-down
        public String countDownTextColor;
        public long countDownRemainingSeconds;
        public long countDownStartTimeSeconds; // shows since when the count down started

        // Products to show into the body
        public List<Product> dealProducts;
    }

    public static class Product {

        public String thumb;
        public String sku;
        public String name;
        public String brand;
        public int maxSavingPercentage;
        public long price;
        public long oldPrice;
        public boolean hasStock;
        public int teaserId;
    }

    public interface OnCountDownDealItemClickListener {

        void onMoreButtonClicked(View v, String targetLink);

        void onProductItemClicked(View v, Product product);
    }
}
