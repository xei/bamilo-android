package com.bamilo.android.appmodule.bamiloapp.utils.home.holder;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import com.emarsys.predict.RecommendedItem;
import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.RecommendationWidgetType;
import com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.recommended.RecommendationsAdapter;
import com.bamilo.android.appmodule.bamiloapp.utils.home.TeaserViewFactory;
import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity;
import com.bamilo.android.R;
import java.util.List;

/**
 * Class used to represent a top seller teaser.
 */
public class HomeRecommendationsGridTeaserHolder /*extends BaseTeaserViewHolder implements IResponseCallback*/ {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();
    public final View itemView;

    private static final int ITEMS_MARGIN = 6;
    private final Context mContext;
    private OnClickListener mParentClickListener;

    public RecyclerView recyclerView;
    private final TextView sectionTitle;
    private OnClickListener mOnMoreClickListener;


    /**
     * Constructor
     */
    public HomeRecommendationsGridTeaserHolder(Context context, View view,
            OnClickListener listener) {
        // super(context, view, listener);
        // Get section title
        itemView = view;
        mParentClickListener = listener;
        mContext = context;

        sectionTitle = view.findViewById(R.id.home_teaser_recommendation_section_title);
        // Get horizontal container
        recyclerView = view.findViewById(R.id.recommendedListView);
        //recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(ITEMS_MARGIN));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
    }

    //@Override
    public void onBind(List<RecommendedItem> items) {
        if (recyclerView.getAdapter() == null) {
            // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);
            // Case top sellers
            if (items != null && items.size() > 0) {
                //if (TextUtils.isNotEmpty(group.getTitle())) sectionTitle.setText(group.getTitle());
                recyclerView.setAdapter(new RecommendationsAdapter(items, onClickListener,
                        RecommendationWidgetType.Grid));
            }
            /*// Case rich relevance
            else if (CollectionUtils.isNotEmpty(group.getData())) {
                triggerGetRichRelevanceData(TargetLink.getIdFromTargetLink(group.getData().get(IntConstants.DEFAULT_POSITION).getTargetLink()));
            }*/
        } else {
        }

        itemView.findViewById(R.id.home_teaser_recommendation_section_more).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnMoreClickListener.onClick(itemView);
                    }
                });
    }

    public void setOnMoreClickListener(OnClickListener onMoreClickListener) {
        mOnMoreClickListener = onMoreClickListener;
    }

    OnClickListener onClickListener = new OnClickListener() {
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