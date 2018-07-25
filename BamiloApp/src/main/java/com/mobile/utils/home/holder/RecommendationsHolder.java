package com.mobile.utils.home.holder;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.emarsys.predict.RecommendedItem;
import com.mobile.app.BamiloApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.components.recycler.HorizontalListView;
import com.mobile.components.recycler.VerticalSpaceItemDecoration;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.extlibraries.emarsys.predict.RecommendationWidgetType;
import com.mobile.extlibraries.emarsys.predict.recommended.RecommendationsAdapter;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;
import de.akquinet.android.androlog.Log;
import java.util.List;

/**
 * Class used to represent a top seller teaser.
 */
public class RecommendationsHolder /*extends BaseTeaserViewHolder implements IResponseCallback*/ {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();
    public final View itemView;

    private static final int ITEMS_MARGIN = 6;
    private final Context mContext;
    private View.OnClickListener mParentClickListener;

    private HorizontalListView horizontalListView;
    public OnClickListener mOnMoreItemClickListener;

    /**
     * Constructor
     */
    public RecommendationsHolder(Context context, View view, View.OnClickListener listener) {
        // super(context, view, listener);
        // Get section title
        itemView = view;
        mParentClickListener = listener;
        mContext = context;

        TextView sectionTitle = view.findViewById(R.id.home_teaser_recommendation_section_title);
        TextView sectionMore = view.findViewById(R.id.home_teaser_recommendation_section_more);

        // Get horizontal container
        horizontalListView = view.findViewById(R.id.recommendedListView);
        horizontalListView.addItemDecoration(new VerticalSpaceItemDecoration(ITEMS_MARGIN));
        // Validate orientation
        horizontalListView.enableRtlSupport(true);

        sectionMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoRecommendationFragment(v);
            }
        });
    }

    private static void gotoRecommendationFragment(View v) {
        if (v.getContext() instanceof BaseActivity) {
            ((BaseActivity) v.getContext())
                    .onSwitchFragment(FragmentType.MORE_RELATED_PRODUCTS, null,
                            FragmentController.ADD_TO_BACK_STACK);
        }
    }

    //@Override
    public void onBind(List<RecommendedItem> items) {
        if (horizontalListView.getAdapter() == null) {
            Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NULL");
            // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
            horizontalListView.setHasFixedSize(true);
            // Case top sellers
            if (items != null && items.size() > 0) {
                //if (TextUtils.isNotEmpty(group.getTitle())) sectionTitle.setText(group.getTitle());
                horizontalListView.setAdapter(new RecommendationsAdapter(items, onClickListener,
                        RecommendationWidgetType.List));
            }
        }
    }

    /**
     * This method requests the rich relevant information of a specific key
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String sku = (String) view.getTag(R.id.sku);
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_ID, sku);
            //bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, product.getBrandName() + " " + product.getName());
            bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
            // Goto PDV
            ((BaseActivity) mContext).onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle,
                    FragmentController.ADD_TO_BACK_STACK);
        }
    };
}