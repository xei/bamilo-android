package com.bamilo.android.appmodule.bamiloapp.utils.tracking.ga;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.bamilo.android.appmodule.bamiloapp.models.BaseEventModel;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.CustomDimensions;
import com.bamilo.android.appmodule.bamiloapp.interfaces.tracking.IEventTracker;
import com.bamilo.android.appmodule.bamiloapp.interfaces.tracking.IScreenTracker;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.R;

/**
 * Created by narbeh on 12/3/17.
 */

public final class GATracker implements IEventTracker, IScreenTracker {

    private static final int GA_DISPATCH_PERIOD = 60;
    private static GATracker instance = null;

    private Tracker mTracker;

    private String mUtmCampaign;
    private String mUtmMedium;
    private String mUtmSource;
    private String mUtmContent;
    private String mUtmTerm;

    protected GATracker() {}

    public static GATracker getInstance() {
        if(instance == null) {
            instance = new GATracker();
        }
        return instance;
    }

    @Override
    public String getTrackerName() {
        return "GATracker";
    }

    @Override
    public void setCampaignUrl(String campaignUrl) {
        setGACampaign(campaignUrl);
    }

    @Override
    public void trackScreenAndTiming(Context context, BaseScreenModel screenModel) {
        Tracker tracker = getTracker(context);

        boolean preInstallId = getCustomData(context).getBoolean(Constants.INFO_PRE_INSTALL);
        String simOperatorId = getCustomData(context).getString(Constants.INFO_SIM_OPERATOR);

        // Track screen
        trackScreen(tracker, screenModel, preInstallId, simOperatorId);

        // Track timing
        trackScreenTiming(tracker, screenModel, preInstallId, simOperatorId);
    }

    @Override
    public void trackScreen(Context context, BaseScreenModel screenModel) {
        Tracker tracker = getTracker(context);

        boolean preInstallId = getCustomData(context).getBoolean(Constants.INFO_PRE_INSTALL);
        String simOperatorId = getCustomData(context).getString(Constants.INFO_SIM_OPERATOR);

        trackScreen(tracker, screenModel, preInstallId, simOperatorId);
    }

    @Override
    public void trackScreenTiming(Context context, BaseScreenModel screenModel) {
        Tracker tracker = getTracker(context);

        boolean preInstallId = getCustomData(context).getBoolean(Constants.INFO_PRE_INSTALL);
        String simOperatorId = getCustomData(context).getString(Constants.INFO_SIM_OPERATOR);

        trackScreenTiming(tracker, screenModel, preInstallId, simOperatorId);
    }

    @Override
    public void trackEventSearchFiltered(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventRecommendationTapped(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventSearchBarSearched(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventViewProduct(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventSearch(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventSearchSuggestions(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventPurchased(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventPurchase(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventTeaserPurchased(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventTeaserTapped(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventAddToCart(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventRemoveFromCart(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventAddToWishList(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventRemoveFromWishList(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventAppOpened(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventLogout(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventLogin(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventSignup(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventCatalogViewChanged(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventCatalogSortChanged(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventCheckoutStart(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventCheckoutFinished(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventSpecificationTapped(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventDescriptionTapped(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventAddReviewTapped(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventRateTapped(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    @Override
    public void trackEventOtherSellersTapped(Context context, BaseEventModel eventModel) {
        trackEvent(context, eventModel);
    }

    private void setGACampaign(String campaignString) {
        // Clean data before every campaign tracking
        mUtmCampaign = null;
        mUtmMedium = null;
        mUtmSource = null;
        mUtmContent = null;
        mUtmTerm = null;
        if (!TextUtils.isEmpty(campaignString)) {
            String[] items = campaignString.split("&");
            for(String item :items) {
                String[] terms = item.split("=");
                if (terms.length != 2) continue;

                if (terms[0].toLowerCase().endsWith("utm_campaign")) {
                    mUtmCampaign = terms[1]; //getUtmParameter(campaignString, "utm_campaign=");
                } else if (terms[0].toLowerCase().endsWith("utm_source")) {
                    mUtmSource = terms[1];// getUtmParameter(campaignString, "utm_source=");
                } else if (terms[0].toLowerCase().endsWith("utm_medium")) {
                    mUtmMedium = terms[1]; // getUtmParameter(campaignString, "utm_medium=");
                } else if (terms[0].toLowerCase().endsWith("utm_content")) {
                    mUtmContent = terms[1]; // getUtmParameter(campaignString, "utm_medium=");
                } else if (terms[0].toLowerCase().endsWith("utm_term")) {
                    mUtmTerm = terms[1]; // getUtmParameter(campaignString, "utm_medium=");
                }
            }
            if (TextUtils.isEmpty(mUtmSource) && TextUtils.isNotEmpty(mUtmCampaign)) {
                mUtmSource = "push";
            }
            if (TextUtils.isEmpty(mUtmMedium) && TextUtils.isNotEmpty(mUtmCampaign)) {
                mUtmMedium = "referrer";
            }
        }
    }

    private void trackGACampaign() {
        //setting as empty string or a null object, will show on GA has "not set"
        if(mUtmCampaign != null){
            mTracker.set("&cn", mUtmCampaign);
        }
        if(mUtmSource != null){
            mTracker.set("&cs", mUtmSource);
        }
        if(mUtmMedium != null){
            mTracker.set("&cm", mUtmMedium);
        }
        if(mUtmContent != null){
            mTracker.set("&cc", mUtmContent);
        }
        if(mUtmTerm != null){
            mTracker.set("&ck", mUtmTerm);
        }
    }

    private Tracker getTracker(Context context) {
        if (mTracker == null) {
            String mCurrentKey = context.getString(R.string.ga_trackingId);
            GoogleAnalytics mAnalytics = GoogleAnalytics.getInstance(context);
            mAnalytics.setLocalDispatchPeriod(GA_DISPATCH_PERIOD);
            mTracker = mAnalytics.newTracker(mCurrentKey);
        }
        trackGACampaign();
        return mTracker;
    }

    private Bundle getCustomData(Context context) {
        return DeviceInfoHelper.getInfo(context);
    }

    private void trackScreen(Tracker tracker, BaseScreenModel screenModel, boolean preInstallId, String simOperatorId) {
        tracker.setScreenName(screenModel.screenName);

        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                .setCustomDimension(CustomDimensions.PRE_INSTALL_ID, String.valueOf(preInstallId))
                .setCustomDimension(CustomDimensions.SIM_OPERATOR_ID, simOperatorId);

        tracker.send(builder.build());
    }

    private void trackScreenTiming(Tracker tracker, BaseScreenModel screenModel, boolean preInstallId, String simOperatorId) {
        long duration = System.currentTimeMillis() - screenModel.loadBegin;
        HitBuilders.TimingBuilder builder = new HitBuilders.TimingBuilder()
                .setCategory(screenModel.category)
                .setValue(duration)
                .setVariable(screenModel.screenName)
                .setLabel(screenModel.label)
                .setCustomDimension(CustomDimensions.PRE_INSTALL_ID, String.valueOf(preInstallId))
                .setCustomDimension(CustomDimensions.SIM_OPERATOR_ID, simOperatorId);

        tracker.send(builder.build());
    }

    private void trackEvent(Context context, BaseEventModel eventModel) {
        Tracker tracker = getTracker(context);

        boolean preInstallId = getCustomData(context).getBoolean(Constants.INFO_PRE_INSTALL);
        String simOperatorId = getCustomData(context).getString(Constants.INFO_SIM_OPERATOR);

        if (eventModel instanceof SimpleEventModel) {
            trackSimpleEvent(tracker, (SimpleEventModel) eventModel, preInstallId, simOperatorId);
        }
    }

    private void trackSimpleEvent(Tracker tracker, SimpleEventModel simpleEventModel, boolean preInstallId, String simOperatorId) {
        HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder()
                .setCategory(simpleEventModel.category)
                .setAction(simpleEventModel.action)
                .setLabel(simpleEventModel.label)
                .setCustomDimension(CustomDimensions.PRE_INSTALL_ID, String.valueOf(preInstallId))
                .setCustomDimension(CustomDimensions.SIM_OPERATOR_ID, simOperatorId);
        if(simpleEventModel.value != SimpleEventModel.NO_VALUE){
            builder.setValue(simpleEventModel.value);
        }

        tracker.send(builder.build());
    }
}
