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
///**
// * Class that represents a group of image teasers
// * @author nutzer2
// *
// */
//@Deprecated
//public class TeaserGroupImages extends TeaserSpecification<TeaserImage> {
//
//	/**
//	 * @param type
//	 */
//	public TeaserGroupImages(TeaserGroupType type) {
//		super(type);
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
//	protected TeaserImage parseData(JSONObject object) {
//		return new TeaserImage(object);
//	}
//
//	/**
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
//    /**
//     * ########### Parcelable ###########
//     * @author sergiopereira
//     */
//
//	/**
//	 * Parcel constructor
//	 * @param in
//	 */
//	public TeaserGroupImages(Parcel in) {
//		super(in);
//	}
//
//	/**
//	 * Create parcelable
//	 */
//	public static final Parcelable.Creator<TeaserGroupImages> CREATOR = new Parcelable.Creator<TeaserGroupImages>() {
//        public TeaserGroupImages createFromParcel(Parcel in) {
//            return new TeaserGroupImages(in);
//        }
//
//        public TeaserGroupImages[] newArray(int size) {
//            return new TeaserGroupImages[size];
//        }
//    };
//
//}
