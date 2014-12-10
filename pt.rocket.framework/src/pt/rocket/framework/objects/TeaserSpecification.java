package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.LogTagHelper;
import android.os.Parcel;
import android.os.Parcelable;
import de.akquinet.android.androlog.Log;

/**
 * Defines a teaser as it is represented in the mobile api.
 * 
 * @author GuilhermeSilva
 * 
 */
public abstract class TeaserSpecification<T extends ITargeting> implements IJSONSerializable, Parcelable {
    
	private final static String TAG = LogTagHelper.create(TeaserSpecification.class);

    protected String title;
    
    private TeaserGroupType type;
    
    private ArrayList<T> teasers;
    
	public static TeaserSpecification<?> parse(JSONObject jsonObject) {
		TeaserGroupType type = TeaserGroupType.byValue(jsonObject.optInt(RestConstants.JSON_GROUP_TYPE_TAG, -1));
		TeaserSpecification<?> teaserSpecification = UnknownTeaserGroup.INSTANCE;
		switch (type) {
		case MAIN_ONE_SLIDE:
		case STATIC_BANNER:
			teaserSpecification = new TeaserGroupImages(type);
			break;
		case PRODUCT_LIST:
			teaserSpecification = new TeaserGroupProducts();
			break;
		case CATEGORIES:
			teaserSpecification = new TeaserGroupCategories();
			break;
		case BRANDS_LIST:
			teaserSpecification = new TeaserGroupBrands();
			break;
		case TOP_BRANDS_LIST:
			teaserSpecification = new TeaserGroupTopBrands();
			break;
		case CAMPAIGNS_LIST:
			teaserSpecification = new TeaserGroupCampaigns();
			break;
		default:
			Log.w(TAG, "Unidentified TeaserGroupType: " + type.toString());
			break;
		}
		teaserSpecification.initialize(jsonObject);
		return teaserSpecification;
	}

	/**
	 * TeaserSpecification empty constructor.
	 */
	public TeaserSpecification(TeaserGroupType type) {
		this.type = type;
		this.teasers = new ArrayList<T>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
	 * )
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) {

			title = jsonObject.optString(RestConstants.JSON_GROUP_TITLE_TAG);
			JSONArray teasersData = jsonObject.optJSONArray(RestConstants.JSON_DATA_TAG);
			if (teasersData == null) {
				Log.w(TAG, "No data tag found in " + jsonObject);
				return false;
			}
			for (int i = 0; i < teasersData.length(); i++) {
				JSONObject teaserData = teasersData.optJSONObject(i);
				if (teaserData != null) {
					teasers.add(parseData(teaserData));
				}
			}

		return true;
	}

	protected abstract T parseData(JSONObject object);

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		return jsonObject;
	}

	/**
	 * @return the type
	 */
	public TeaserGroupType getType() {
		return type;
	}

	/**
	 * @return the teasers
	 */
	public ArrayList<T> getTeasers() {
		return teasers;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	
    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */
	
	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	    dest.writeString(title);
	    dest.writeValue(type);
	    dest.writeList(teasers);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	public TeaserSpecification(Parcel in) {	    
		this(TeaserGroupType.UNKNOWN);
		Log.d(TAG, " ---- < READ TEASER SPECIFICATION > -----");
		title = in.readString();
		type = (TeaserGroupType) in.readValue(TeaserGroupType.class.getClassLoader());
		in.readList(teasers, ITargeting.class.getClassLoader());
	}
	

}
