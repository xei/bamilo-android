///**
// *
// */
//package com.mobile.framework.objects;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import org.json.JSONObject;
//
//import de.akquinet.android.androlog.Log;
//
///**
// * Class used to represent a group of teaser campaigns
// * @author sergiopereira
// *
// */
//@Deprecated
//public class TeaserGroupCampaigns extends TeaserSpecification<TeaserCampaign> {
//
//	private static final String TAG = TeaserGroupCampaigns.class.getSimpleName();
//
//	/**
//	 * @param type
//	 */
//	public TeaserGroupCampaigns() {
//		super(TeaserGroupType.CAMPAIGNS_LIST);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see
//	 * com.mobile.framework.objects.TeaserSpecification#parseData(org.json.JSONObject
//	 * )
//	 */
//	@Override
//	protected TeaserCampaign parseData(JSONObject object) {
//		Log.d(TAG, "ON PARSE CAMPAIGN: " + object.toString());
//		// Get campaigns
//		TeaserCampaign campaign = new TeaserCampaign();
//		campaign.initialize(object);
//		return campaign;
//	}
//
//
//    /**
//     * ########### Parcelable ###########
//     * @author sergiopereira
//     */
//
//    /*
//     * (non-Javadoc)
//     * @see android.os.Parcelable#describeContents()
//     */
//	@Override
//	public int describeContents() {
//		return super.describeContents();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
//	 */
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		super.writeToParcel(dest, flags);
//	}
//
//
//    /**
//     * ########### Parcelable ###########
//     * @author sergiopereira
//     */
//
//	/**
//	 * Parcel constructor
//	 * @param in
//	 */
//	public TeaserGroupCampaigns(Parcel in) {
//		super(in);
//	}
//
//	/**
//	 * Create parcelable
//	 */
//	public static final Parcelable.Creator<TeaserGroupCampaigns> CREATOR = new Parcelable.Creator<TeaserGroupCampaigns>() {
//        public TeaserGroupCampaigns createFromParcel(Parcel in) {
//            return new TeaserGroupCampaigns(in);
//        }
//
//        public TeaserGroupCampaigns[] newArray(int size) {
//            return new TeaserGroupCampaigns[size];
//        }
//    };
//
//}
