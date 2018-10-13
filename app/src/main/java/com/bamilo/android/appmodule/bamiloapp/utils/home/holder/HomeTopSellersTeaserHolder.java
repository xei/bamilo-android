package com.bamilo.android.appmodule.bamiloapp.utils.home.holder;

import android.content.Context;
import android.view.View;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import android.widget.TextView;
import com.bamilo.android.framework.components.recycler.HorizontalListView;
import com.bamilo.android.framework.components.recycler.VerticalSpaceItemDecoration;
import com.bamilo.android.appmodule.bamiloapp.helpers.teasers.GetRichRelevanceHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.framework.service.objects.home.group.BaseTeaserGroupType;
import com.bamilo.android.framework.service.objects.product.RichRelevance;
import com.bamilo.android.framework.service.objects.product.pojo.ProductRegular;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.TargetLink;
import com.bamilo.android.appmodule.bamiloapp.utils.home.TeaserViewFactory;
import com.bamilo.android.R;

import java.util.ArrayList;


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
        sectionTitle = view.findViewById(R.id.home_teaser_top_seller_section_title);
        // Get horizontal container
        horizontalListView = view.findViewById(R.id.home_teaser_top_sellers_horizontal_list);
        horizontalListView.addItemDecoration(new VerticalSpaceItemDecoration(ITEMS_MARGIN));
        // Validate orientation
        horizontalListView.enableRtlSupport(isRtl);
    }

    @Override
    public void onBind(BaseTeaserGroupType group) {
        if (horizontalListView.getAdapter() == null) {
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

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
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
        if (horizontalListView != null) {
            ((View) horizontalListView.getParent()).setVisibility(View.GONE);
        }
    }
}