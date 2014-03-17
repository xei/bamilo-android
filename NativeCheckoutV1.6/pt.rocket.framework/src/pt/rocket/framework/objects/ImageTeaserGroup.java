/**
 * 
 */
package pt.rocket.framework.objects;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that represents a group of image teasers
 * @author nutzer2
 * 
 */
public class ImageTeaserGroup extends TeaserSpecification<TeaserImage> {

	/**
	 * @param type
	 */
	public ImageTeaserGroup(TeaserGroupType type) {
		super(type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.objects.TeaserSpecification#parseData(org.json.JSONObject
	 * )
	 */
	@Override
	protected TeaserImage parseData(JSONObject object) {
		return new TeaserImage(object);
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
		return super.describeContents();
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
	}
	
    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	public ImageTeaserGroup(Parcel in) {
		super(in);
	}
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<ImageTeaserGroup> CREATOR = new Parcelable.Creator<ImageTeaserGroup>() {
        public ImageTeaserGroup createFromParcel(Parcel in) {
            return new ImageTeaserGroup(in);
        }

        public ImageTeaserGroup[] newArray(int size) {
            return new ImageTeaserGroup[size];
        }
    };

}
