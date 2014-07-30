package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Object that deals with the parsing of a homepage
 * @author josedourado
 *
 */

public class Homepage implements IJSONSerializable, Parcelable{
	
	private int homepageId;
	private String homepageTitle;
	private boolean defaultHomepage;
	private ArrayList<TeaserSpecification<?>> teaserSpecifications;
	private String homepageLayout;
	
	/**
	 * Constructor
	 */
	public Homepage(){
		homepageId=-1;
		homepageTitle="";
		defaultHomepage=false;
		teaserSpecifications= new ArrayList<TeaserSpecification<?>>();
	}
	
	/**
	 * 
	 * @return homepage id
	 */
	public int getHomepageId(){
		return homepageId;
	}
	
	/**
	 * 
	 * @return homepage title
	 */
	public String getHomepageTitle(){
		return homepageTitle;
	}
	
	/**
	 * 
	 * @return true if default, false otherwise
	 */
	public boolean isDefaultHomepage(){
		return defaultHomepage;
	}
	
	/**
	 * 
	 * @return teaser specification
	 */
	public ArrayList<TeaserSpecification<?>> getTeaserSpecification(){
		return teaserSpecifications;
	}
	
	
	/**
	 * 
	 * @return homepage layout
	 */
	public String getHomepageLayout(){
	    return homepageLayout;
	}
	
	
	

	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		homepageId = jsonObject.getInt(RestConstants.JSON_HOMEPAGE_ID_TAG);
		homepageTitle=jsonObject.getString(RestConstants.JSON_HOMEPAGE_TITLE_TAG);
		defaultHomepage=jsonObject.getInt(RestConstants.JSON_HOMEPAGE_DEFAULT_TAG)==1?true:false;
		homepageLayout = jsonObject.getString(RestConstants.JSON_HOMEPAGE_LAYOUT_TAG);
		JSONArray dataArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
		int dataArrayLenght = dataArray.length();
//		ArrayList<TeaserSpecification<?>> teaserSpecifications = new ArrayList<TeaserSpecification<?>>();
		
		for (int i = 0; i < dataArrayLenght; ++i) { // XXX
			teaserSpecifications.add(TeaserSpecification.parse(dataArray.getJSONObject(i)));
		}
		return true;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}
	
    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */
    
    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	    dest.writeInt(homepageId);
	    dest.writeString(homepageTitle);
//	    dest.writeBooleanArray(new boolean[] {defaultHomepage});
	    dest.writeByte((byte) (defaultHomepage ? 1 : 0));
	    dest.writeList(teaserSpecifications);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	private Homepage(Parcel in) {
		homepageId = in.readInt();
		homepageTitle = in.readString();
		teaserSpecifications = new ArrayList<TeaserSpecification<?>>();
//		in.readBooleanArray(new boolean[] {defaultHomepage});
		defaultHomepage = in.readByte() == 1;
		teaserSpecifications = new ArrayList<TeaserSpecification<?>>();
		in.readList(teaserSpecifications, TeaserSpecification.class.getClassLoader());
    }
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<Homepage> CREATOR = new Parcelable.Creator<Homepage>() {
        public Homepage createFromParcel(Parcel in) {
//        	try {
//        		return new Homepage(in);
//			} catch (NullPointerException e) {
//				e.printStackTrace();
//			}
//            return null;
            return new Homepage(in);
        }

        public Homepage[] newArray(int size) {
            return new Homepage[size];
        }
    };

}
