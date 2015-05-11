//package com.mobile.framework.objects;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import com.mobile.framework.rest.RestConstants;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
///**
// * Object that deals with the parsing of a homepage
// * @author sergiopereira
// */
//
//public class Homepage implements IJSONSerializable, Parcelable {
//
//    private int mId;
//    private String mTitle;
//    private boolean isDefault;
//    private ArrayList<TeaserSpecification> mTeaserSpecifications;
//    private String mLayout;
//
//    /**
//	 * Constructor
//	 */
//	public Homepage(){
//        mId = -1;
//        mTitle = "";
//        isDefault = false;
//        mTeaserSpecifications = new ArrayList<>();
//    }
//
//	/**
//	 *
//	 * @return homepage id
//	 */
//    public int getId() {
//        return mId;
//    }
//
//	/**
//	 *
//	 * @return homepage title
//	 */
//    public String getTitle() {
//        return mTitle;
//    }
//
//	/**
//	 *
//	 * @return true if default, false otherwise
//	 */
//    public boolean isDefault() {
//        return isDefault;
//    }
//
//	/**
//	 *
//	 * @return teaser specification
//	 */
//    public ArrayList<TeaserSpecification> getTeaserSpecification() {
//        return mTeaserSpecifications;
//    }
//
//    /**
//     *
//     * @return homepage layout
//	 */
//    public String getLayout() {
//        return mLayout;
//    }
//
//	@Override
//	public boolean initialize(JSONObject jsonObject) throws JSONException {
//        mId = jsonObject.getInt(RestConstants.JSON_HOMEPAGE_ID_TAG);
//        mTitle = jsonObject.getString(RestConstants.JSON_HOMEPAGE_TITLE_TAG);
//        isDefault = jsonObject.getInt(RestConstants.JSON_HOMEPAGE_DEFAULT_TAG) == 1;
//        mLayout = jsonObject.getString(RestConstants.JSON_HOMEPAGE_LAYOUT_TAG);
//        JSONArray dataArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
//        int size = dataArray.length();
//        for (int i = 0; i < size; ++i) {
//            mTeaserSpecifications.add(TeaserSpecification.parse(dataArray.getJSONObject(i)));
//        }
//		return true;
//	}
//
//	@Override
//	public JSONObject toJSON() {
//		return null;
//	}
//
//    /*
//     * ########### Parcelable ###########
//     */
//    /*
//     * (non-Javadoc)
//     * @see android.os.Parcelable#describeContents()
//     */
//	@Override
//	public int describeContents() {
//		return 0;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
//	 */
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(mId);
//        dest.writeString(mTitle);
//        dest.writeByte((byte) (isDefault ? 0x01 : 0x00));
//        if (mTeaserSpecifications == null) {
//            dest.writeByte((byte) (0x00));
//        } else {
//            dest.writeByte((byte) (0x01));
//            dest.writeList(mTeaserSpecifications);
//        }
//        dest.writeString(mLayout);
//    }
//
//	/**
//	 * Parcel constructor
//     * @param in - parcel
//     */
//	private Homepage(Parcel in) {
//        mId = in.readInt();
//        mTitle = in.readString();
//        isDefault = in.readByte() != 0x00;
//        if (in.readByte() == 0x01) {
//            mTeaserSpecifications = new ArrayList<>();
//            in.readList(mTeaserSpecifications, TeaserSpecification.class.getClassLoader());
//        } else {
//            mTeaserSpecifications = null;
//        }
//        mLayout = in.readString();
//    }
//
//	/**
//	 * Create parcelable
//	 */
//	public static final Parcelable.Creator<Homepage> CREATOR = new Parcelable.Creator<Homepage>() {
//        public Homepage createFromParcel(Parcel in) {
//            return new Homepage(in);
//        }
//
//        public Homepage[] newArray(int size) {
//            return new Homepage[size];
//        }
//    };
//
//}
