package com.bamilo.android.appmodule.bamiloapp.utils.deeplink;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.annotation.StringRes;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.ActivitiesWorkFlow;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.WarningFactory;
import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.CampaignsFragment;
import com.bamilo.android.framework.service.objects.home.TeaserCampaign;
import com.bamilo.android.framework.service.objects.home.group.BaseTeaserGroupType;
import com.bamilo.android.framework.service.objects.home.object.BaseTeaserObject;
import com.bamilo.android.framework.service.objects.home.type.TeaserGroupType;
import com.bamilo.android.framework.service.utils.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;


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
    public static final String SHOP_IN_SHOP = "shop_in_shop";
    public static final String EXTERNAL_LINK = "external_link";
    public static final String UNKNOWN = "unknown";
    private boolean isSubCategoryFilter = false;

    @StringDef({PDV, CATALOG, CATALOG_CATEGORY, CATALOG_BRAND, CATALOG_SELLER, CAMPAIGN,
            STATIC_PAGE, SHOP_IN_SHOP, UNKNOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {

    }

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

        @NonNull
        Bundle onTargetCampaign(String title, String id, TeaserGroupType mOrigin);
    }

    private static final String TAG = TargetLink.class.getSimpleName();

    // TARGET CONSTANTS
    private static final int TARGET_LINK_SIZE = 2;
    private static final int TARGET_TYPE_POSITION = 0;
    private static final int TARGET_ID_POSITION = 1;
    private static final String TARGET_LINK_DELIMITER = "::";
    private static final String SIMPLE_SKU_DELIMITER = "-";

    private final WeakReference<BaseActivity> mActivity;
    private String mTitle;
    private final String mTarget;
    private TeaserGroupType mOrigin;
    private OnAppendDataListener mAppendDataListener;
    private OnCampaignListener mCampaignListener;
    private boolean isToRemoveEntries = true;
    private boolean isToShowWarningError;

    /**
     * Constructor
     */
    public TargetLink(@NonNull WeakReference<BaseActivity> activity,
            @Type @Nullable String target) {
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
     * Add a title.
     */
    public TargetLink addTitle(@StringRes int title) {
        this.mTitle = mActivity.get().getString(title);
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
     * Disable the pop back stack entries until home.
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
    public TargetLink enableWarningErrorMessage() {
        this.isToShowWarningError = true;
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
            showWarningErrorMessage();
            return false;
        }

        // ##### Get type and value
        String type = targetLink[TARGET_TYPE_POSITION];
        String id = targetLink[TARGET_ID_POSITION];

        // ##### Handling external links
        if (type.equals(EXTERNAL_LINK)) {
            ActivitiesWorkFlow.startExternalWebActivity(mActivity.get(), id, null);
            return true;
        }

        // ##### Get fragment type
        FragmentType nextFragmentType = getFragmentType(type);
        if (nextFragmentType == FragmentType.UNKNOWN) {
            showWarningErrorMessage();
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
        if (isSubCategoryFilter) {
            bundle.putBoolean(ConstantsIntentExtra.SUB_CATEGORY_FILTER, true);
        }
        // ##### Append data
        if (mAppendDataListener != null) {
            mAppendDataListener.onAppendData(nextFragmentType, mTitle, id, bundle);
        }
        // ##### Switch fragment
        if (mActivity.get() != null) {
            mActivity.get().onSwitchFragment(nextFragmentType, bundle,
                    FragmentController.ADD_TO_BACK_STACK);
        }
        // Success
        return true;
    }

    /**
     * Method used to show the generic warning message.
     */
    private void showWarningErrorMessage() {
        if (isToShowWarningError) {
            try {
                mActivity.get().showWarning(WarningFactory.PROBLEM_FETCHING_DATA_ANIMATION);
            } catch (NullPointerException ignored) {
            }
        }
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
        // Case static page
        else if (TextUtils.equals(STATIC_PAGE, type)) {
            fragmentType = FragmentType.STATIC_PAGE;
        }
        // Case static page
        else if (TextUtils.equals(SHOP_IN_SHOP, type)) {
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
    private Bundle createCampaignBundle(OnCampaignListener listener, String title, String id,
            TeaserGroupType origin) {
        Bundle bundle;
        // Case listener
        if (listener != null) {
            bundle = listener.onTargetCampaign(title, id, origin);
        }
        // Case default
        else {
            bundle = new Bundle();
            bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, origin);
            bundle.putParcelableArrayList(CampaignsFragment.CAMPAIGNS_TAG,
                    createCampaignList(title, id));
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
    private static String[] splitLink(@Nullable String link) {
        return TextUtils.isEmpty(link) ? new String[]{}
                : TextUtils.split(link, TARGET_LINK_DELIMITER);
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
    public static ArrayList<TeaserCampaign> createCampaignList(
            @NonNull BaseTeaserGroupType campaignGroup) {
        ArrayList<TeaserCampaign> campaigns = new ArrayList<>();
        for (BaseTeaserObject teaser : campaignGroup.getData()) {
            campaigns.add(new TeaserCampaign(teaser.getTitle(),
                    TargetLink.getIdFromTargetLink(teaser.getTargetLink())));
        }
        return campaigns;
    }

    @Nullable
    public static String getSkuFromSimple(@Nullable String simple) {
        return TextUtils.isNotEmpty(simple) ? TextUtils.split(simple, SIMPLE_SKU_DELIMITER)[0]
                : null;
    }

    public TargetLink setIsSubCategoryFilter() {
        isSubCategoryFilter = true;
        return this;
    }

}
