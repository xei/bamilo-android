package com.mobile.utils.home.holder;

import android.content.Context;
import android.view.View;

import com.mobile.app.BamiloApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.components.recycler.HorizontalListView;
import com.mobile.components.recycler.VerticalSpaceItemDecoration;
import com.mobile.helpers.teasers.GetRichRelevanceHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.service.objects.home.group.BaseTeaserGroupType;
import com.mobile.service.objects.product.RichRelevance;
import com.mobile.service.objects.product.pojo.ProductRegular;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.deeplink.TargetLink;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.view.R;

import java.util.ArrayList;

import de.akquinet.android.androlog.Log;

/**
 * Class used to represent a top seller teaser.
 */
public class HomeTopSellersTeaserHolder extends BaseTeaserViewHolder implements IResponseCallback {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();

    private static final int ITEMS_MARGIN = 6;

    public HorizontalListView horizontalListView;
    private final TextView sectionTitle;

    /**
     * Constructor
     */
    public HomeTopSellersTeaserHolder(Context context, View view, View.OnClickListener listener) {
        super(context, view, listener);
        // Get section title
        sectionTitle = (TextView) view.findViewById(R.id.home_teaser_top_seller_section_title);
        // Get horizontal container
        horizontalListView = (HorizontalListView) view.findViewById(R.id.home_teaser_top_sellers_horizontal_list);
        horizontalListView.addItemDecoration(new VerticalSpaceItemDecoration(ITEMS_MARGIN));
        // Validate orientation
        horizontalListView.enableRtlSupport(isRtl);
    }

    @Override
    public void onBind(BaseTeaserGroupType group) {
        if (horizontalListView.getAdapter() == null) {
            Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NULL");
            // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
            horizontalListView.setHasFixedSize(true);
            // Case top sellers
            if (group.hasData()) {
                if (TextUtils.isNotEmpty(group.getTitle())) sectionTitle.setText(group.getTitle());
                horizontalListView.setAdapter(new HomeTopSellersTeaserAdapter(group.getData(), mParentClickListener));
            }
            // Case rich relevance
            else if (CollectionUtils.isNotEmpty(group.getData())) {
                triggerGetRichRelevanceData(TargetLink.getIdFromTargetLink(group.getData().get(IntConstants.DEFAULT_POSITION).getTargetLink()));
            }
        } else {
            Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NOT NULL");
        }
    }

    /**
     * This method requests the rich relevant information of a specific key
     */
    private void triggerGetRichRelevanceData(String key) {
        if (TextUtils.isNotEmpty(key)) {
            BamiloApplication.INSTANCE.sendRequest(new GetRichRelevanceHelper(), GetRichRelevanceHelper.createBundle(key), this);
        }
    }

    /*
     * ################# RESPONSE #################
     */

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "SUCCESS RICH RELEVANCE");
        RichRelevance richRelevanceObject = (RichRelevance) baseResponse.getContentData();
        ArrayList<ProductRegular> richRelevanceTeaserObjects = richRelevanceObject.getRichRelevanceProducts();
        if (!CollectionUtils.isEmpty(richRelevanceTeaserObjects) && mParentClickListener != null && horizontalListView != null) {
            horizontalListView.setAdapter(new RichRelevanceAdapter(richRelevanceTeaserObjects, mParentClickListener, true));
            sectionTitle.setText(richRelevanceObject.getTitle());
        } else {
            onRequestError(baseResponse);
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "ERROR RICH RELEVANCE");
        if (horizontalListView != null) {
            ((View) horizontalListView.getParent()).setVisibility(View.GONE);
        }
    }
}