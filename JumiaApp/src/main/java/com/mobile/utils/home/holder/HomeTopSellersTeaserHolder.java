package com.mobile.utils.home.holder;

import android.content.Context;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.components.recycler.HorizontalListView;
import com.mobile.helpers.teasers.GetRichRelevanceHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.home.group.BaseTeaserGroupType;
import com.mobile.newFramework.objects.home.object.BaseTeaserObject;
import com.mobile.newFramework.objects.home.object.TeaserRichRelevanceObject;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.view.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import de.akquinet.android.androlog.Log;

/**
 *
 */
public class HomeTopSellersTeaserHolder extends BaseTeaserViewHolder {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();

    public HorizontalListView horizontalListView;

    /**
     * Constructor
     */
    public HomeTopSellersTeaserHolder(Context context, View view, View.OnClickListener listener) {
        super(context, view, listener);
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
                Print.i(TAG, "NEED RICH RELEVANT REQUEST");
                //FIXME should be switched to target type and key
                getRichRelevanceData(group.getData().get(0).getUrl());
            }

        } else {
            Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NOT NULL");
        }
    }

    /**
     * This method requests the rich relevant information of a specific hash
     * @param hash
     */
    private void getRichRelevanceData(String hash){

        //FIXME parsing section below should be removed
        String[] parts = hash.split("\\?request=");
        String parameters = parts[1];
        Print.i(TAG, "parameters:" + parameters);
        String urlEnconded = null;
        try {
            urlEnconded = URLDecoder.decode(parameters, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JumiaApplication.INSTANCE.sendRequest(new GetRichRelevanceHelper(), GetRichRelevanceHelper.createBundle(urlEnconded), new IResponseCallback() {
            @Override
            public void onRequestComplete(BaseResponse baseResponse) {
                Print.i(TAG, "SUCCESS RICH RELEVANCE");
                TeaserRichRelevanceObject topSellers = (TeaserRichRelevanceObject) baseResponse.getMetadata().getData();
                if(topSellers != null){
                    ArrayList<BaseTeaserObject> topSellersTeaserObjects = topSellers.getRichTeaserGroupType().getData();
                    if(!CollectionUtils.isEmpty(topSellersTeaserObjects) && mParentClickListener != null && horizontalListView != null){
                        horizontalListView.setAdapter(new HomeTopSellersTeaserAdapter(topSellersTeaserObjects, mParentClickListener));
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