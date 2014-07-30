package pt.rocket.framework.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.utils.LogTagHelper;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;


/**
 * 
 * @author sergiopereira
 * @see https://wiki.jira.rocket-internet.de/display/NAFAMZ/Filters
 *
 */
public class CatalogFilter implements IJSONSerializable, Parcelable {

	public static final String TAG = LogTagHelper.create(CatalogFilter.class);
	
	private ArrayList<CatalogFilterOption> mFilterOptions;

	private CatalogFilterOption mFilterOption;

	private String mId;

	private String mName;

	private boolean mMulti;
	
	private int[] mRangeValues = null;
	
	private boolean isRangeWithDiscount = false;

	private SparseArray<CatalogFilterOption> mSelectedOption;

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

	/*
	 * (non-Javadoc)
	 * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		// Get id
		mId = jsonObject.getString("id");
		// Get name
		mName = jsonObject.getString("name");
		// Get multi
		mMulti = jsonObject.optBoolean("multi");
		
		//Log.d(TAG, "FILTER: " + mId + " " + mName + " " + mMulti);

        // Init array
        mFilterOptions = new ArrayList<CatalogFilterOption>();
		
		// Get options
		JSONArray jsonOptions = jsonObject.optJSONArray("option");
		if(jsonOptions != null) {
			// Get options
			for (int i = 0; i < jsonOptions.length(); i++) {
				// Get json option
				JSONObject jsonOption = jsonOptions.getJSONObject(i);
				// Create filter option
				CatalogFilterOption filterOption = new CatalogFilterOption(jsonOption);
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
			// Get json option
			JSONObject json = jsonObject.optJSONObject("option");
			// Create filter option
			mFilterOption = new CatalogFilterOption(json);
		}
		
		return true;
	}
	
	/**
	 * Method used to sort the brands 
	 * @author sergiopereira
	 */
    public class AlphabeticComparator implements Comparator<CatalogFilterOption> {
        @Override
        public int compare(CatalogFilterOption o1, CatalogFilterOption o2) {
            return o1.getLabel().compareToIgnoreCase(o2.getLabel());
        }
    }
    
    
    /**
     * Method used to create sections for brands
     * @param objects
     * @return ArrayList<FilterOption>
     * @author sergiopereira
     */
    private static ArrayList<CatalogFilterOption> createSections(ArrayList<CatalogFilterOption> objects){
        // Create new list with sections
        ArrayList<CatalogFilterOption> listSections = new ArrayList<CatalogFilterOption>();
        // Init char
        char savedChar = 0;
        // For each option
        for (CatalogFilterOption option : objects) {
            // Get the first char
            char currentChar = option.getLabel().charAt(0);
            // If different the current
            if(currentChar != savedChar) {
                // Create the section item
                savedChar = currentChar;
                CatalogFilterOption optionSection = new CatalogFilterOption();
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
	
	public ArrayList<CatalogFilterOption> getFilterOptions() {
		return mFilterOptions;
	}

	public CatalogFilterOption getFilterOption() {
		return mFilterOption;
	}
	
	public SparseArray<CatalogFilterOption> getSelectedOption() {
		return this.mSelectedOption;
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
	
	public void setFilterOptions(ArrayList<CatalogFilterOption> mFilterOptions) {
		this.mFilterOptions = mFilterOptions;
	}

	public void setFilterOption(CatalogFilterOption mFilterOption) {
		this.mFilterOption = mFilterOption;
	}
	
	public void setSelectedOption(SparseArray<CatalogFilterOption> selectedOption) {
		this.mSelectedOption = selectedOption;
	}
	
	public void cleanSelectedOption() {
        for(int i = 0; i < mSelectedOption.size(); i++) mSelectedOption.valueAt(i).setSelected(false);
		this.mSelectedOption.clear();
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
		return (mSelectedOption != null && mSelectedOption.size() >0) ? true : false;
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
//	    if (null != mFilterOptions && 0 < mFilterOptions.size()) {
//	        dest.writeInt(mFilterOptions.size());
////	        dest.writeTypedList(mFilterOptions);
//	        for(int i = 0; i < mFilterOptions.size(); i++){
//	            Log.d(TAG, " ------------ >[WRITE] Filter Option #" + i + " -> " + mFilterOptions.get(i).toString());
//	            dest.writeParcelable(mFilterOptions.get(i), flags);
//	        }
//	    } else {
//	        dest.writeInt(-1);
//	    }
//		dest.writeParcelable(mFilterOption, flags);		
//		dest.writeString(mId);
//		dest.writeString(mName);
//		dest.writeByte((byte)(mMulti ? 1 : 0));
//		dest.writeByte((byte)(isRangeWithDiscount ? 1 : 0));
//
//        if (null != mRangeValues) {
//            Log.d(TAG, " ------------ >[WRITE] Range Values ARRAY SIZE ->" + mRangeValues.length);
//            dest.writeInt(mRangeValues.length);
//            dest.writeIntArray(mRangeValues);
//        } else {
//            Log.d(TAG, " ------------ >[WRITE] Range Values ARRAY SIZE ->" + -1);
//            dest.writeInt(-1);
//        }

        Bundle out = new Bundle();
        out.putParcelableArrayList("1", mFilterOptions);
        out.putParcelable("2", mFilterOption);
        out.putString("3", mId);
        out.putString("4", mName);
        out.putBoolean("5", mMulti);
        out.putBoolean("6", isRangeWithDiscount);
        out.putIntArray("7", mRangeValues);
        
        dest.writeBundle(out);
	}

	/**
	 * Constructor with parcel
	 * 
	 * @param in
	 */
	private CatalogFilter(Parcel in) {	    
//	    mFilterOptions = new ArrayList<CatalogFilterOption>();
//        	    
////	    in.readTypedList(mFilterOptions, CatalogFilterOption.CREATOR);
//	    int listSize = in.readInt();
//	    Log.d(TAG, " ------------ >[READ] LIST SIZE ->" + listSize);
//        for (int i = 0; i < listSize; i++) {
//            Log.d(TAG, " ------------ >[READ] LIST ITEM #" + i);
//            try {
//                mFilterOptions.add((CatalogFilterOption) in.readParcelable(CatalogFilterOption.class.getClassLoader()));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//	    
////	    if (0 < listSize) {
////	        in.readTypedList(mFilterOptions, CatalogFilterOption.CREATOR);
////	    }
//	    Log.d(TAG, " ------------ >[READ] ArrayList SIZE ->" + mFilterOptions.size());
//	    
//		mFilterOption = in.readParcelable(CatalogFilterOption.class.getClassLoader());
//		mId = in.readString();
//		mName = in.readString();		
//		mMulti = in.readByte() == 1;
//		isRangeWithDiscount = in.readByte() == 1;
//		
//        int arraySize = in.readInt();
//        Log.d(TAG, " ------------ >[READ] Range Values ARRAY SIZE ->" + arraySize);
//        if (0 < arraySize) {
//            mRangeValues = new int[arraySize];
//            in.readIntArray(mRangeValues);
//        } else {
//            mRangeValues = null;
//        }
        
        Bundle inBundle = in.readBundle();
        
        mFilterOptions = inBundle.getParcelableArrayList("1");
        mFilterOption = inBundle.getParcelable("2");
        mId = inBundle.getString("3");
        mName = inBundle.getString("4");
        mMulti = inBundle.getBoolean("5");
        isRangeWithDiscount = inBundle.getBoolean("6");
        mRangeValues = inBundle.getIntArray("7");        
        
	}

	public CatalogFilter(String id, String name, Boolean isMulti, ArrayList<CatalogFilterOption> options) {
		this.mId = id;
		this.mName = name;
		this.mMulti = isMulti;
		this.mFilterOptions = options;
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
