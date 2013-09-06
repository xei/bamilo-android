package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.LogTagHelper;
import de.akquinet.android.androlog.Log;

/**
 * Defines a teaser as it is represented in the mobile api.
 * 
 * @author GuilhermeSilva
 * 
 */
public abstract class TeaserSpecification<T extends ITargeting> implements
		IJSONSerializable {
	private final static String TAG = LogTagHelper
			.create(TeaserSpecification.class);

	public enum TeaserGroupType {
		MAIN_ONE_SLIDE(0), STATIC_BANNER(1), PRODUCT_LIST(2), CATEGORIES(3), BRANDS_LIST(4), UNKNOWN(
				-1);

		public final int value;

		/**
		 * 
		 */
		private TeaserGroupType(int value) {
			this.value = value;
		}

		public static TeaserGroupType byValue(int value) {
			for (TeaserGroupType type : TeaserGroupType.values()) {
				if (type.value == value)
					return type;
			}
			return UNKNOWN;
		}
	}

	public static TeaserSpecification<?> parse(JSONObject jsonObject) {
		TeaserGroupType type = TeaserGroupType.byValue(jsonObject.optInt(
				RestConstants.JSON_GROUP_TYPE_TAG, -1));
		TeaserSpecification<?> teaserSpecification = UnknownTeaserGroup.INSTANCE;
		Log.i(TAG, "code1 inside types : "+type);
		switch (type) {
		case MAIN_ONE_SLIDE:
		case STATIC_BANNER:
			teaserSpecification = new ImageTeaserGroup(type);
			break;
		case PRODUCT_LIST:
			teaserSpecification = new ProductTeaserGroup();
			break;
		case CATEGORIES:
			teaserSpecification = new CategoryTeaserGroup();
			break;
		case BRANDS_LIST:
			teaserSpecification = new BrandsTeaserGroup();
			break;
		}
		teaserSpecification.initialize(jsonObject);
		return teaserSpecification;
	}

//	protected static final String JSON_ATTRIBUTES_TAG = "attributes";
//	protected static final String JSON_DESCRIPTION_TAG = "description";
//	protected static final String JSON_IMAGE_URL_TAG = "image_url";
//	protected static final String JSON_IMAGES_TAG = "image_list";
//	protected static final String JSON_GROUP_TYPE_TAG = "group_type";
//	protected static final String JSON_GROUP_TITLE_TAG = "group_title";
//	protected static final String JSON_TARGET_TAG = "target_type";

	protected String title;
	private final TeaserGroupType type;
	private final ArrayList<T> teasers;

	/**
	 * TeaserSpecification empty constructor.
	 */
	public TeaserSpecification(TeaserGroupType type) {
		this.type = type;
		teasers = new ArrayList<T>();
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
}
