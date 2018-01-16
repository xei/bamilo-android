package com.mobile.utils.tracking.ga;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobile.classes.models.BaseEventModel;
import com.mobile.classes.models.BaseScreenModel;
import com.mobile.classes.models.SimpleEventModel;
import com.mobile.constants.tracking.CustomDimensions;
import com.mobile.interfaces.tracking.IEventTracker;
import com.mobile.interfaces.tracking.IScreenTracker;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.DeviceInfoHelper;
import com.mobile.view.R;

/**
 * Created by narbeh on 12/3/17.
 */

public final class GATracker implements IEventTracker, IScreenTracker {

    private static final int GA_DISPATCH_PERIOD = 60;
    private static GATracker instance = null;

    private Tracker mTracker;

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

    }

    /*@Override
    public void trackEvent(Context context, String event, HashMap<String, Object> attributes) {

    }*/

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
        trackEvent(context, eventModel);
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

    private Tracker getTracker(Context context) {
        if (mTracker == null) {
            String mCurrentKey = context.getString(R.string.ga_trackingId);
            GoogleAnalytics mAnalytics = GoogleAnalytics.getInstance(context);
            mAnalytics.setLocalDispatchPeriod(GA_DISPATCH_PERIOD);
            mTracker = mAnalytics.newTracker(mCurrentKey);
        }

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

        mTracker.send(builder.build());
    }
}
