package pt.rocket.framework.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.utils.LogTagHelper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import de.akquinet.android.androlog.Log;


/**
 * 
 * @author sergiopereira
 * @see https://wiki.jira.rocket-internet.de/display/NAFAMZ/Filters
 *
 */
public class CatalogFilter implements IJSONSerializable, Parcelable {

	private static final String TAG = LogTagHelper.create(CatalogFilter.class);
	
	private ArrayList<FilterOption> mFilterOptions;

	private FilterOption mFilterOption;

	private String mId;

	private String mName;

	private boolean mMulti;
	
	private int mSelectedOption = -1;
	
	private int[] mRangeValues = null;
	
	private boolean isRangeWithDiscount = false;

	private SparseArray<FilterOption> mSelectedOption2;

	/**
	 * ########### CONSTRUCTOR ###########  
	 */
	
	/**
	 * Empty constructor
	 * @throws JSONException 
	 */
	public CatalogFilter(JSONObject jsonObject) throws JSONException { 
		initialize(jsonObject);
	}

	/**
	 * ############### IJSON ###############
	 */

	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		// Get id
		mId = jsonObject.getString("id");
		// Get name
		mName = jsonObject.getString("name");
		// Get multi
		mMulti = jsonObject.getBoolean("multi");
		
		Log.d(TAG, "FILTER: " + mId + " " + mName + " " + mMulti);
		
		// Get options
		JSONArray jsonOptions = jsonObject.optJSONArray("option");
		if(jsonOptions != null) {
			// Init array
			mFilterOptions = new ArrayList<FilterOption>();
			// Get options
			for (int i = 0; i < jsonOptions.length(); i++) {
				// Get json option
				JSONObject jsonOption = jsonOptions.getJSONObject(i);
				// Create filter option
				FilterOption filterOption = new FilterOption(jsonOption);
				// Save filter option
				mFilterOptions.add(filterOption);
			}
			
			// Validate if is a brand filter and create everything is necessary
			if(mId.contains("brand")) {
		        // Sort alphabetic
		        Collections.sort(mFilterOptions, new AlphabeticComparator());
		        // Create filter sections
		        mFilterOptions = createSections(mFilterOptions);
			}
			
			
			
		}else if(jsonObject.optJSONObject("option") != null) {
			Log.d(TAG, "FILTER: PRICE : " + jsonObject.optJSONObject("option"));
			// Get json option
			JSONObject json = jsonObject.optJSONObject("option");
			// Create filter option
			mFilterOption = new FilterOption(json);
		}
		
		return true;
	}
	
    public class AlphabeticComparator implements Comparator<FilterOption> {
        @Override
        public int compare(FilterOption o1, FilterOption o2) {
            return o1.getLabel().compareToIgnoreCase(o2.getLabel());
        }
    }
    
    
    /**
     * 
     * @param objects
     * @return
     */
    private static ArrayList<FilterOption> createSections(ArrayList<FilterOption> objects){
        // Create new list with sections
        ArrayList<FilterOption> listSections = new ArrayList<FilterOption>();
        // Init char
        char savedChar = 0;
        // For each option
        for (FilterOption option : objects) {
            // Get the first char
            char currentChar = option.getLabel().charAt(0);
            // If different the current
            if(currentChar != savedChar) {
                // Create the section item
                savedChar = currentChar;
                FilterOption optionSection = new FilterOption();
                optionSection.setSectionBrand(true);
                optionSection.setLabel(String.valueOf((char)(currentChar)).toUpperCase(Locale.getDefault()));
                listSections.add(optionSection);
                listSections.add(option);
            }else {
                listSections.add(option);   
            }
        }
        return listSections;
    }
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		return null;
	}
	

	/**
	 * ########### GETTERS ###########  
	 */
	
	public ArrayList<FilterOption> getFilterOptions() {
		return mFilterOptions;
	}

	public FilterOption getFilterOption() {
		return mFilterOption;
	}
	
	public int getSelectedOption() {
		return this.mSelectedOption;
	}
	
	public SparseArray<FilterOption> getSelectedOption2() {
		return this.mSelectedOption2;
	}
	
	public String getId() {
		return mId;
	}

	public String getName() {
		return mName;
	}
	
	public int getMinRangeValue(){
		return mRangeValues[0];
	}
	
	public int getMaxRangeValue(){
		return mRangeValues[1];
	}

	
	public boolean isRangeWithDiscount(){
		return isRangeWithDiscount;
	}
	
	/**
	 * ########### SETTERS ###########  
	 */
	
	public void setFilterOptions(ArrayList<FilterOption> mFilterOptions) {
		this.mFilterOptions = mFilterOptions;
	}

	public void setFilterOption(FilterOption mFilterOption) {
		this.mFilterOption = mFilterOption;
	}
	
	public void setSelectedOption(int position) {
		this.mSelectedOption = position;
	}
	
	public void setSelectedOption2(SparseArray<FilterOption> selectedOption) {
		this.mSelectedOption2 = selectedOption;
	}
	
	public void cleanSelectedOption() {
		this.mSelectedOption = -1;
	}
	
	public void setId(String id) {
		this.mId = id;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public void setMulti(boolean mMulti) {
		this.mMulti = mMulti;
	}
	
	public void setRangeValues(int min, int max){
		mRangeValues = new int[2];
		mRangeValues[0] = min;
		mRangeValues[1] = max;
	}
	
	public void cleanRangeValues() {
		mRangeValues = null;
		isRangeWithDiscount = false;
	}
	
	
	public void setRangeWithDiscount(boolean bool) {
		isRangeWithDiscount = bool;
	}
	
	/**
	 * ########### VALIDATOR ###########  
	 */
	
	public boolean hasRangeValues(){
		return (mRangeValues != null) ? true : false;
	}
	
	public boolean hasOptionSelected(){
		return (mSelectedOption != -1) ? true : false;
	}

	
	public boolean hasOptionSelected2(){
		return (mSelectedOption2 != null && mSelectedOption2.size() >0) ? true : false;
	}
	
	
	public boolean isMulti() {
		return mMulti;
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
	}

	/**
	 * Constructor with parcel
	 * 
	 * @param in
	 */
	private CatalogFilter(Parcel in) {
	}

	/**
	 * The creator
	 */
	public static final Parcelable.Creator<CatalogFilter> CREATOR = new Parcelable.Creator<CatalogFilter>() {
		public CatalogFilter createFromParcel(Parcel in) {
			return new CatalogFilter(in);
		}

		public CatalogFilter[] newArray(int size) {
			return new CatalogFilter[size];
		}
	};
	
	
	
	
	
	/**
	 * 
	 * @author sergiopereira
	 *
	 */
	public static class FilterOption implements IJSONSerializable, Parcelable {

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


		public FilterOption(JSONObject jsonObject) throws JSONException {
			initialize(jsonObject);
		}
		
		public FilterOption() { 
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

			Log.d(TAG, "FILTER OPTION:" +
					" ID:" + mId + 
					" LABEL:" + mLabel + 
					" VAL:" + mValue + 
					" COUNT:" + mCount + 
					" HEX:" + mHex + 
					" IMG:" + mImg + 
					" MAX:" + mMax + 
					" MIN:" + mMin + 
					" INTERVAL:" + mInterval);
			
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
			// TODO
		}

		/**
		 * Constructor with parcel
		 * 
		 * @param in
		 */
		private FilterOption(Parcel in) {
			// TODO
		}


		/**
		 * The creator
		 */
		public static final Parcelable.Creator<CatalogFilter> CREATOR = new Parcelable.Creator<CatalogFilter>() {
			public CatalogFilter createFromParcel(Parcel in) {
				return new CatalogFilter(in);
			}

			public CatalogFilter[] newArray(int size) {
				return new CatalogFilter[size];
			}
		};
		
	}

}
