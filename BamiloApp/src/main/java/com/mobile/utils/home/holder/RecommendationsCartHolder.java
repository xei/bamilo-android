package com.mobile.utils.home.holder;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.emarsys.predict.RecommendedItem;
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

import java.util.List;

import de.akquinet.android.androlog.Log;

public class RecommendationsCartHolder  {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();
    public final View itemView;

    private static final int ITEMS_MARGIN = 6;
    private final Context mContext;

    public HorizontalListView horizontalListView;

    public RecommendationsCartHolder(Context context, View view, View.OnClickListener listener) {

        itemView = view;
        mContext = context;

        // Get horizontal container
        horizontalListView = (HorizontalListView) view.findViewById(R.id.recommendedListView);
        horizontalListView.addItemDecoration(new VerticalSpaceItemDecoration(ITEMS_MARGIN));
        // Validate orientation
        horizontalListView.enableRtlSupport(true);
    }

    //@Override
    public void onBind(List<RecommendedItem> items) {
        if (horizontalListView.getAdapter() == null) {
            horizontalListView.setHasFixedSize(true);
            if (items != null && items.size()>0) {
                horizontalListView.setAdapter(new RecommendationsAdapter(items, onClickListener, RecommendationWidgetType.Cart));
            }
        }
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String sku = (String)view.getTag(R.id.sku);
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_ID, sku);
            bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
            ((BaseActivity)mContext).onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    };
}