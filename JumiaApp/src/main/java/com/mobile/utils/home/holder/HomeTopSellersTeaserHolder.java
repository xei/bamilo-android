package com.mobile.utils.home.holder;

import android.content.Context;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.components.recycler.HorizontalListView;
import com.mobile.helpers.teasers.GetRichRelevanceHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.home.group.BaseTeaserGroupType;
import com.mobile.newFramework.objects.product.RichRelevance;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.deeplink.TargetLink;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.view.R;

import java.util.ArrayList;

import de.akquinet.android.androlog.Log;

/**
 *
 */
public class HomeTopSellersTeaserHolder extends BaseTeaserViewHolder {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();

    public HorizontalListView horizontalListView;
    private TextView sectionTitle;
    /**
     * Constructor
     */
    public HomeTopSellersTeaserHolder(Context context, View view, View.OnClickListener listener) {
        super(context, view, listener);
        // Get section title
        sectionTitle = (TextView) view.findViewById(R.id.home_teaser_top_seller_section_title);
        // Get horizontal container
        horizontalListView = (HorizontalListView) view.findViewById(R.id.home_teaser_top_sellers_horizontal_list);
        // Validate orientation
        horizontalListView.enableRtlSupport(isRtl);
    }

    @Override
    public void onBind(BaseTeaserGroupType group) {
        if (horizontalListView.getAdapter() == null) {
            Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NULL");
            // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
            horizontalListView.setHasFixedSize(true);
            // Set adapter
            if(group.hasData()){
                horizontalListView.setAdapter(new HomeTopSellersTeaserAdapter(group.getData(), mParentClickListener));
            } else {
                if(CollectionUtils.isNotEmpty(group.getData()))
                    getRichRelevanceData(TargetLink.getIdFromTargetLink(group.getData().get(0).getTargetLink()));
            }
        } else {
            Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NOT NULL");
        }
    }

    /**
     * This method requests the rich relevant information of a specific key
     * @param key
     */
    private void getRichRelevanceData(String key){

        if(TextUtils.isEmpty(key))
            return;

        JumiaApplication.INSTANCE.sendRequest(new GetRichRelevanceHelper(), GetRichRelevanceHelper.createBundle(key), new IResponseCallback() {
            @Override
            public void onRequestComplete(BaseResponse baseResponse) {
                Print.i(TAG, "SUCCESS RICH RELEVANCE");
                RichRelevance richRelevanceObject = (RichRelevance) baseResponse.getContentData();

                if(richRelevanceObject != null){
                    ArrayList<ProductRegular> richRelevanceTeaserObjects = richRelevanceObject.getRichRelevanceProducts();
                    if(!CollectionUtils.isEmpty(richRelevanceTeaserObjects) && mParentClickListener != null && horizontalListView != null){
                        horizontalListView.setAdapter(new HomeRichRelevanceTeaserAdapter(richRelevanceTeaserObjects, mParentClickListener));
                        sectionTitle.setText(richRelevanceObject.getTitle());
                    } else {
                        onRequestError(baseResponse);
                    }
                } else {
                    onRequestError(baseResponse);
                }

            }
            @Override
            public void onRequestError(BaseResponse baseResponse) {
                Print.i(TAG, "ERROR RICH RELEVANCE");
                if(horizontalListView != null)
                    ((View)horizontalListView.getParent()).setVisibility(View.GONE);
            }
        });
    }
}