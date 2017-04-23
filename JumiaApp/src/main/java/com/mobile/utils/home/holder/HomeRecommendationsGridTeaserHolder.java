package com.mobile.utils.home.holder;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.emarsys.predict.RecommendedItem;
import com.mobile.components.customfontviews.TextView;
import com.mobile.components.recycler.VerticalSpaceItemDecoration;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.view.R;

import java.util.List;

import de.akquinet.android.androlog.Log;

/**
 * Class used to represent a top seller teaser.
 */
public class HomeRecommendationsGridTeaserHolder /*extends BaseTeaserViewHolder implements IResponseCallback*/ {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();
    public final View itemView;

    private static final int ITEMS_MARGIN = 6;
    private final Context mContext;
    private  View.OnClickListener mParentClickListener;

    public RecyclerView recyclerView;
    private final TextView sectionTitle;


    /**
     * Constructor
     */
    public HomeRecommendationsGridTeaserHolder(Context context, View view, View.OnClickListener listener) {
       // super(context, view, listener);
        // Get section title
        itemView = view;
        mParentClickListener = listener;
        mContext = context;

        sectionTitle = (TextView) view.findViewById(R.id.home_teaser_recommendation_section_title);
        // Get horizontal container
        recyclerView = (RecyclerView) view.findViewById(R.id.recommendedListView);
        //recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(ITEMS_MARGIN));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        // Validate orientation
    }

    //@Override
    public void onBind(List<RecommendedItem> items) {
        if (recyclerView.getAdapter() == null) {
            Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NULL");
            // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);
            // Case top sellers
            if (items != null && items.size()>0) {
                //if (TextUtils.isNotEmpty(group.getTitle())) sectionTitle.setText(group.getTitle());
                recyclerView.setAdapter(new HomeRecommendationsTeaserAdapter(items, mParentClickListener));
            }
            /*// Case rich relevance
            else if (CollectionUtils.isNotEmpty(group.getData())) {
                triggerGetRichRelevanceData(TargetLink.getIdFromTargetLink(group.getData().get(IntConstants.DEFAULT_POSITION).getTargetLink()));
            }*/
        } else {
            Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NOT NULL");
        }
    }

    /**
     * This method requests the rich relevant information of a specific key
     */
   /* private void triggerGetRichRelevanceData(String key) {
        if (TextUtils.isNotEmpty(key)) {
            JumiaApplication.INSTANCE.sendRequest(new GetRichRelevanceHelper(), GetRichRelevanceHelper.createBundle(key), this);
        }
    }*/

    /*
     * ################# RESPONSE #################
     */
    /*
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "SUCCESS RICH RELEVANCE");
        RichRelevance richRelevanceObject = (RichRelevance) baseResponse.getContentData();
        ArrayList<ProductRegular> richRelevanceTeaserObjects = richRelevanceObject.getRichRelevanceProducts();
        if (!CollectionUtils.isEmpty(richRelevanceTeaserObjects) && mParentClickListener != null && recyclerView != null) {
            recyclerView.setAdapter(new RichRelevanceAdapter(richRelevanceTeaserObjects, mParentClickListener, true));
            sectionTitle.setText(richRelevanceObject.getTitle());
        } else {
            onRequestError(baseResponse);
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "ERROR RICH RELEVANCE");
        if (recyclerView != null) {
            ((View) recyclerView.getParent()).setVisibility(View.GONE);
        }
    }*/
}