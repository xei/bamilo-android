package com.mobile.utils.deeplink;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

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

import de.akquinet.android.androlog.Log;

/**
 * Class used to jump for the next target link.
 */
public class TargetLink {

    // TARGET TYPES CONSTANTS
    public static final String PDV = "product_detail";
    public static final String CATALOG = "catalog";
    public static final String CATALOG_CATEGORY = "catalog_category";
    public static final String CATALOG_BRAND = "catalog_brand";
    public static final String CATALOG_SELLER = "catalog_seller";
    public static final String CAMPAIGN = "campaign";
    public static final String STATIC_PAGE = "static_page";
    public static final String UNKNOWN = "unknown";
    @StringDef({PDV, CATALOG, CATALOG_CATEGORY, CATALOG_BRAND, CATALOG_SELLER, CAMPAIGN, STATIC_PAGE, UNKNOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {}

    /**
     * Interface used to append some data.
     */
    public interface OnAppendDataListener {
        void onAppendData(FragmentType next, String title, String id, Bundle data);
    }

    /**
     * Interface used to intercept the campaign link.
     */
    public interface OnCampaignListener {
        @NonNull Bundle onTargetCampaign(String title, String id, TeaserGroupType mOrigin);
    }

    private static final String TAG = TargetLink.class.getSimpleName();

    // TARGET CONSTANTS
    private static final int TARGET_LINK_SIZE = 2;
    private static final int TARGET_TYPE_POSITION = 0;
    private static final int TARGET_ID_POSITION = 1;
    private static final String TARGET_LINK_DELIMITER = "::";

    private final WeakReference<BaseActivity> mActivity;
    private String mTitle;
    private final String mTarget;
    private TeaserGroupType mOrigin;
    private OnAppendDataListener mAppendDataListener;
    private OnCampaignListener mCampaignListener;
    private boolean isToRemoveEntries = true;

    /**
     * Constructor
     */
    public TargetLink(@NonNull WeakReference<BaseActivity> activity, @Type @Nullable String target) {
        this.mActivity = activity;
        this.mTarget = target;
    }

    /**
     * Add a title.
     */
    public TargetLink addTitle(@NonNull String title) {
        this.mTitle = title;
        return this;
    }

    /**
     * Add a click origin.
     */
    public TargetLink setOrigin(@NonNull TeaserGroupType origin) {
        this.mOrigin = origin;
        return this;
    }

    /**
     * Remove entries until home.
     */
    public TargetLink retainBackStackEntries() {
        this.isToRemoveEntries = false;
        return this;
    }

    /**
     * Add a listener to append more data.
     */
    public TargetLink addAppendListener(@NonNull OnAppendDataListener listener) {
        this.mAppendDataListener = listener;
        return this;
    }

    /**
     * Add a listener to build the bundle for campaign.
     */
    public TargetLink addCampaignListener(@NonNull OnCampaignListener listener) {
        this.mCampaignListener = listener;
        return this;
    }

    /**
     * Execute
     */
    public boolean run() {
        // ##### Split target link
        String[] targetLink = splitLink(mTarget);
        if (targetLink.length != TARGET_LINK_SIZE) {
            Log.w(TAG, "WARNING: INVALID TARGET LINK: " + mTarget);
            return false;
        }

        // ##### Get type and value
        String type = targetLink[TARGET_TYPE_POSITION];
        String id = targetLink[TARGET_ID_POSITION];
        // ##### Get fragment type
        FragmentType nextFragmentType = getFragmentType(type);
        if (nextFragmentType == FragmentType.UNKNOWN) {
            Print.w(TAG, "WARNING: UNKNOWN TARGET LINK: " + mTarget);
            return false;
        }
        // ##### Create bundle
        Bundle bundle;
        // Case generic target
        if (nextFragmentType == FragmentType.CAMPAIGNS) {
            bundle = createCampaignBundle(mCampaignListener, mTitle, id, mOrigin);
        } else {
            bundle = createBundle(mTitle, id, mOrigin);
        }
        Print.i(TAG, "TARGET LINK: TYPE:" + nextFragmentType + " TITLE:" + mTitle + " ID:" + id);
        // ##### Append data
        if (mAppendDataListener != null) {
            mAppendDataListener.onAppendData(nextFragmentType, mTitle, id, bundle);
        }
        // ##### Switch fragment
        if (mActivity.get() != null) {
            mActivity.get().onSwitchFragment(nextFragmentType, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
        // Success
        return true;
    }

    /**
     * Match the target type with a fragment type.
     */
    @NonNull
    private FragmentType getFragmentType(String type) {
        // Case unknown
        FragmentType fragmentType = FragmentType.UNKNOWN;
        // Case pdv
        if (TextUtils.equals(PDV, type)) {
            fragmentType = FragmentType.PRODUCT_DETAILS;
        }
        // Case catalog
        else if (TextUtils.equals(CATALOG, type)) {
            fragmentType = FragmentType.CATALOG;
        }// Case CATALOG_CATEGORY
        else if (TextUtils.equals(CATALOG_CATEGORY, type)) {
            fragmentType = FragmentType.CATALOG_CATEGORY;
        }
        // Case catalog brand
        else if (TextUtils.equals(CATALOG_BRAND, type)) {
            fragmentType = FragmentType.CATALOG_BRAND;
        }
        // Case catalog seller
        else if (TextUtils.equals(CATALOG_SELLER, type)) {
            fragmentType = FragmentType.CATALOG_SELLER;
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
     * Create a generic bundle( PDV, INNER_SHOP AND CATALOG).
     */
    @NonNull
    private Bundle createBundle(String title, String value, TeaserGroupType origin) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, title);
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, value);
        bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, origin);
        bundle.putBoolean(ConstantsIntentExtra.REMOVE_OLD_BACK_STACK_ENTRIES, isToRemoveEntries);
        return bundle;
    }

    /*
     * ############## CAMPAIGN METHODS ##############
     */

    /**
     * Create a bundle for campaigns, from listener(multi campaigns) or normal (single campaign).
     */
    @NonNull
    private Bundle createCampaignBundle(OnCampaignListener listener, String title, String id, TeaserGroupType origin) {
        Bundle bundle;
        // Case listener
        if (listener != null) {
            bundle = listener.onTargetCampaign(title, id, origin);
        }
        // Case default
        else {
            bundle = new Bundle();
            bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, origin);
            bundle.putParcelableArrayList(CampaignsFragment.CAMPAIGNS_TAG, createCampaignList(title, id));
        }
        return bundle;
    }

    /*
     * ############## STATIC METHODS ##############
     */

    /**
     * Split the target link.
     */
    @NonNull
    private static String[] splitLink(@NonNull String link) {
        return TextUtils.isEmpty(link) ? new String[]{} : TextUtils.split(link, TARGET_LINK_DELIMITER);
    }

    /**
     * Get id from target link.
     */
    @NonNull
    public static String getIdFromTargetLink(@NonNull String link) {
        String[] splitLink = splitLink(link);
        return splitLink.length == TARGET_LINK_SIZE ? splitLink[TARGET_ID_POSITION] : "";
    }


    /**
     * Create an array with a single campaign(HOME AND OTHERS).
     */
    @NonNull
    public static ArrayList<TeaserCampaign> createCampaignList(String title, String id) {
        ArrayList<TeaserCampaign> campaigns = new ArrayList<>();
        TeaserCampaign campaign = new TeaserCampaign(title, id);
        campaigns.add(campaign);
        return campaigns;
    }

    /**
     * Create an array with multi campaigns(HOME).
     */
    @NonNull
    public static ArrayList<TeaserCampaign> createCampaignList(@NonNull BaseTeaserGroupType campaignGroup) {
        ArrayList<TeaserCampaign> campaigns = new ArrayList<>();
        for (BaseTeaserObject teaser : campaignGroup.getData()) {
            campaigns.add(new TeaserCampaign(teaser.getTitle(), TargetLink.getIdFromTargetLink(teaser.getTargetLink())));
        }
        return campaigns;
    }

}