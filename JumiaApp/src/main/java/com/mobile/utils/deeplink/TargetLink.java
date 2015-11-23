package com.mobile.utils.deeplink;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;

import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.objects.home.TeaserCampaign;
import com.mobile.newFramework.objects.home.group.BaseTeaserGroupType;
import com.mobile.newFramework.objects.home.object.BaseTeaserObject;
import com.mobile.newFramework.objects.home.type.TeaserGroupType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.BaseActivity;
import com.mobile.view.fragments.CampaignsFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * TODO
 */
public class TargetLink {

    private static final String TAG = TargetLink.class.getSimpleName();
    // TARGET CONSTANTS
    private static final int TARGET_LINK_SIZE = 2;
    private static final int TARGET_TYPE_POSITION = 0;
    private static final int TARGET_VALUE_POSITION = 1;
    private static final String TARGET_LINK_DELIMITER = "::";
    // TARGET TYPES CONSTANTS
    public static final String PDV = "product_detail";
    public static final String CATALOG = "catalog";
    public static final String CATALOG_BRAND = "catalog_brand";
    public static final String CATALOG_SELLER = "catalog_seller";
    public static final String CAMPAIGN = "campaign";
    public static final String STATIC_PAGE = "static_page";
    public static final String UNKNOWN = "unknown";
    @StringDef({PDV, CATALOG, CATALOG_BRAND, CATALOG_SELLER, CAMPAIGN, STATIC_PAGE, UNKNOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {}

    /**
     * TODO
     */
    public interface TargetLinkDataListener {
        void onAppendTargetData(FragmentType type, Bundle data);
    }

    /**
     * TODO
     */
    public interface TargetInterceptListener {
        @NonNull Bundle onTargetCampaign(String title, String id, TeaserGroupType mOrigin);
    }

    /**
     * TODO
     */
    public static class Builder {

        private WeakReference<BaseActivity> mActivity;
        private String mTitle;
        private String mTarget;
        private TeaserGroupType mOrigin;
        private TargetLinkDataListener mListener;
        private TargetInterceptListener mCampaignListener;

        public Builder(@NonNull WeakReference<BaseActivity> activity, @Type @Nullable String target) {
            this.mActivity = activity;
            this.mTarget = target;
        }

        public Builder addTitle(@NonNull String title) {
            this.mTitle = title;
            return this;
        }

        public Builder addOrigin(@NonNull TeaserGroupType origin) {
            this.mOrigin = origin;
            return this;
        }

        public Builder appendData(@NonNull TargetLinkDataListener listener) {
            this.mListener = listener;
            return this;
        }

        public Builder interceptCampaign(@NonNull TargetInterceptListener listener) {
            this.mCampaignListener = listener;
            return this;
        }

        public boolean run() {
            // Split target link
            String[] targetLink = splitLink(mTarget);
            if(targetLink.length != TARGET_LINK_SIZE) {
                Log.w(TAG, "WARNING: INVALID TARGET LINK: " + mTarget);
                return false;
            }
            // Get type and value
            String type = targetLink[TARGET_TYPE_POSITION];
            String value = targetLink[TARGET_VALUE_POSITION];
            // Get fragment type
            FragmentType fragmentType = getFragmentType(type);
            if(fragmentType == FragmentType.UNKNOWN) {
                Print.w(TAG, "WARNING: UNKNOWN TARGET LINK: " + mTarget);
                return false;
            }
            // Create bundle
            Bundle bundle;
            // Case generic target
            if(fragmentType != FragmentType.CAMPAIGNS) {
                bundle = createGenericFragmentBundle(mTitle, value, mOrigin);
            }
            // Case campaign fragment other
            else if (mCampaignListener != null) {
                bundle = mCampaignListener.onTargetCampaign(mTitle, value, mOrigin);
            } else {
                bundle = createCampaignFragmentBundle(mTitle, value, mOrigin);
            }
            Print.i(TAG, "TARGET LINK: TYPE:" + fragmentType + " TITLE:" + mTitle + " ID:" + value);
            // Append data
            if (mListener != null) {
                mListener.onAppendTargetData(fragmentType, bundle);
            }
            // Switch fragment
            mActivity.get().onSwitchFragment(fragmentType, bundle, FragmentController.ADD_TO_BACK_STACK);
            // Success
            return true;
        }
    }

    /**
     * TODO
     */
    @NonNull
    public static String getIdFromTargetLink(@NonNull String link){
        String[] splitLink = splitLink(link);
        return splitLink.length == TARGET_LINK_SIZE ? splitLink[TARGET_VALUE_POSITION] : "";
    }

    /**
     * TODO
     */
    @NonNull
    private static String[] splitLink(@NonNull String link){
        // Case empty
        if(TextUtils.isEmpty(link)) {
            Log.w(TAG, "WARNING: TARGET LINK IS EMPTY");
            return new String[]{};
        }
        // Case invalid split
        return TextUtils.split(link, TARGET_LINK_DELIMITER);
    }

    /**
     * TODO
     */
    private static FragmentType getFragmentType(String type) {
        // Case unknown
        FragmentType fragmentType = FragmentType.UNKNOWN;
        // Case pdv
        if (TextUtils.equals(PDV, type)) {
            fragmentType = FragmentType.PRODUCT_DETAILS;
        }
        // Case catalog
        else if (TextUtils.equals(CATALOG, type)) {
            fragmentType = FragmentType.CATALOG;
        }
        // Case catalog brand
        else if (TextUtils.equals(CATALOG_BRAND, type)) {
            fragmentType = FragmentType.CATALOG; // TODO
        }
        // Case catalog seller
        else if (TextUtils.equals(CATALOG_SELLER, type)) {
            fragmentType = FragmentType.CATALOG; // TODO
        }
        // Case campaign
        else if (TextUtils.equals(CAMPAIGN, type)) {
            fragmentType = FragmentType.CAMPAIGNS;
        }
        // Case campaign
        else if (TextUtils.equals(STATIC_PAGE, type)) {
            fragmentType = FragmentType.INNER_SHOP;
        }
        return fragmentType;
    }

    /**
     * TODO
     */
    @NonNull
    private static Bundle createGenericFragmentBundle(String title, String value, TeaserGroupType origin) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, title);
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, value);
        bundle.putSerializable(ConstantsIntentExtra.ORIGIN_TRACKING_TYPE, origin);
        return bundle;
    }

    /**
     * TODO
     */
    @NonNull
    private static Bundle createCampaignFragmentBundle(String title, String id, TeaserGroupType origin) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.ORIGIN_TRACKING_TYPE, origin);
        bundle.putParcelableArrayList(CampaignsFragment.CAMPAIGNS_TAG, createCampaignList(title, id));
        return bundle;
    }



    @NonNull
    public static ArrayList<TeaserCampaign> createCampaignList(String title, String id) {
        ArrayList<TeaserCampaign> campaigns = new ArrayList<>();
        TeaserCampaign campaign = new TeaserCampaign(title, id);
        campaigns.add(campaign);
        return campaigns;
    }

    @NonNull
    public static ArrayList<TeaserCampaign> createCampaignList(BaseTeaserGroupType campaignGroup) {
        ArrayList<TeaserCampaign> campaigns = new ArrayList<>();
        for (BaseTeaserObject teaser : campaignGroup.getData()) {
            campaigns.add(new TeaserCampaign(teaser.getTitle(), TargetLink.getIdFromTargetLink(teaser.getTargetLink())));
        }
        return campaigns;
    }

}
