//package com.bamilo.android.appmodule.bamiloapp.utils.home.holder;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import com.emarsys.predict.RecommendedItem;
//import com.bamilo.android.framework.components.recycler.HorizontalListView;
//import com.bamilo.android.framework.components.recycler.VerticalSpaceItemDecoration;
//import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
//import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
//import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
//import com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.RecommendationWidgetType;
//import com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.recommended.RecommendationsAdapter;
//import com.bamilo.android.appmodule.bamiloapp.utils.home.TeaserViewFactory;
//import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity;
//import com.bamilo.android.R;
//import java.util.List;
//
//public class RecommendationsCartHolder {
//
//    private static final String TAG = TeaserViewFactory.class.getSimpleName();
//    public final View itemView;
//
//    private static final int ITEMS_MARGIN = 6;
//    private final Context mContext;
//
//    private HorizontalListView horizontalListView;
//    private OnClickListener mOnMoreItemClickListener;
//
//    public RecommendationsCartHolder(Context context, View view, OnClickListener listener) {
//
//        itemView = view;
//        mContext = context;
//
//        // Get horizontal container
//        horizontalListView = view.findViewById(R.id.recommendedListView);
//        horizontalListView.addItemDecoration(new VerticalSpaceItemDecoration(ITEMS_MARGIN));
//        // Validate orientation
//        horizontalListView.enableRtlSupport(true);
//
//        view.findViewById(R.id.home_teaser_recommendation_section_more).setOnClickListener(
//                v -> gotoRecommendationFragment(context));
//    }
//
//    private void gotoRecommendationFragment(Context context) {
//        if (context instanceof BaseActivity) {
//            ((BaseActivity) context)
//                    .onSwitchFragment(FragmentType.MORE_RELATED_PRODUCTS, null,
//                            FragmentController.ADD_TO_BACK_STACK);
//        }
//    }
//
//    //@Override
//    public void onBind(List<RecommendedItem> items) {
//        if (horizontalListView.getAdapter() == null) {
//            horizontalListView.setHasFixedSize(true);
//            if (items != null && items.size() > 0) {
//                horizontalListView.setAdapter(new RecommendationsAdapter(items, onClickListener,
//                        RecommendationWidgetType.Cart));
//            }
//        }
//    }
//
//
//    private OnClickListener onClickListener = new OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            String sku = (String) view.getTag(R.id.sku);
//            Bundle bundle = new Bundle();
//            bundle.putString(ConstantsIntentExtra.CONTENT_ID, sku);
//            bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
//            ((BaseActivity) mContext).onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle,
//                    FragmentController.ADD_TO_BACK_STACK);
//        }
//    };
//}