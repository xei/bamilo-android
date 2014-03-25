package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
	 * 
	 * @author sergiopereira
	 *
	 */
	public class CatalogFilterOption implements IJSONSerializable, Parcelable {

		private String mId;
		
		private String mLabel;
		
		private String mValue;
		
		private String mCount;
		
		private String mHex;
		
		private String mImg;
		
		private int mMax = -1;
		
		private int mMin = -1;
		
		private int mInterval = 0;
		
		private boolean isSelected;
		
		private boolean isSectionBrand;


		public CatalogFilterOption(JSONObject jsonObject) throws JSONException {
			initialize(jsonObject);
		}
		
		public CatalogFilterOption() { 
			isSectionBrand = false;
		}
		

		@Override
		public boolean initialize(JSONObject jsonOption) throws JSONException {
			// Get id
			mId = jsonOption.optString("id");
			// Get label
			mLabel = jsonOption.optString("label");
			// Get value
			mValue = jsonOption.optString("val");
			// Get products count
			mCount = jsonOption.optString("products_count");
			// Get hex value
			mHex = jsonOption.optString("hex_value");
			// Get image url
			mImg = jsonOption.optString("image_url");
			// Get max
			mMax = jsonOption.optInt("max");
			// Get min
			mMin = jsonOption.optInt("min");
			// Get interval
			mInterval = jsonOption.optInt("interval");
			// Set selected
			isSelected = false;
			// Set 
			isSectionBrand = false;

//			Log.d(TAG, "FILTER OPTION:" +
//					" ID:" + mId + 
//					" LABEL:" + mLabel + 
//					" VAL:" + mValue + 
//					" COUNT:" + mCount + 
//					" HEX:" + mHex + 
//					" IMG:" + mImg + 
//					" MAX:" + mMax + 
//					" MIN:" + mMin + 
//					" INTERVAL:" + mInterval);
			
			return true;
		}

		@Override
		public JSONObject toJSON() {
			// TODO Auto-generated method stub
			return null;
		}
		
		/**
		 * ########### GETTERS ###########  
		 */

		
		public String getId() {
			return mId;
		}


		public String getLabel() {
			return mLabel;
		}


		public String getValue() {
			return mValue;
		}


		public String getCount() {
			return mCount;
		}


		public String getHex() {
			return mHex;
		}


		public String getImg() {
			return mImg;
		}


		public int getMax() {
			return mMax;
		}


		public int getMin() {
			return mMin;
		}


		public int getInterval() {
			return mInterval;
		}
		
		public boolean isSelected() {
			return isSelected;
		}
		
		public boolean isSectionItem() {
			return isSectionBrand;
		}

		
		/**
		 * ########### SETTERS ###########  
		 */

		public void setId(String mId) {
			this.mId = mId;
		}


		public void setLabel(String mLabel) {
			this.mLabel = mLabel;
		}


		public void setValue(String mValue) {
			this.mValue = mValue;
		}


		public void setCount(String mCount) {
			this.mCount = mCount;
		}


		public void setHex(String mHex) {
			this.mHex = mHex;
		}


		public void setImg(String mImg) {
			this.mImg = mImg;
		}


		public void setMax(int mMax) {
			this.mMax = mMax;
		}


		public void setMin(int mMin) {
			this.mMin = mMin;
		}


		public void setInterval(int mInterval) {
			this.mInterval = mInterval;
		}

		
		public void setSelected(Boolean bool) {
			this.isSelected = bool;
		}
		
		public void setSectionBrand(Boolean bool) {
			this.isSectionBrand = bool;
		}

		/**
		 * ############### Parcelable ###############
		 */

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable#describeContents()
		 */
		@Override
		public int describeContents() {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
		 */
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(mId);
			dest.writeString(mLabel);
			dest.writeString(mValue);
			dest.writeString(mCount);			
			dest.writeString(mHex);
			dest.writeString(mImg);
			dest.writeInt(mMax);
			dest.writeInt(mMin);
			dest.writeInt(mInterval);
			dest.writeBooleanArray(new boolean[] {isSelected, isSectionBrand});
		}

		/**
		 * Constructor with parcel
		 * 
		 * @param in
		 */
		private CatalogFilterOption(Parcel in) {
			mId = in.readString();
			mLabel = in.readString();
			mValue = in.readString();
			mCount = in.readString();
			mHex = in.readString();
			mImg = in.readString();
			mMax = in.readInt();
			mMin = in.readInt();
			mInterval = in.readInt();
			in.readBooleanArray(new boolean[] {isSelected, isSectionBrand});
		}


		/**
		 * The creator
		 */
		public static final Parcelable.Creator<CatalogFilterOption> CREATOR = new Parcelable.Creator<CatalogFilterOption>() {
			public CatalogFilterOption createFromParcel(Parcel in) {
				return new CatalogFilterOption(in);
			}

			public CatalogFilterOption[] newArray(int size) {
				return new CatalogFilterOption[size];
			}
		};
		
	}