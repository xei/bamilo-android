package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.ImageResolutionHelper;
import pt.rocket.framework.utils.LogTagHelper;

public class Variation implements IJSONSerializable {
	private static final String TAG = LogTagHelper.create(Variation.class);

//	private static final String JSON_LINK_TAG = "link";
//	private static final String JSON_IMAGE_TAG = "image";

	private String sku;
	private String link;
	private String image;

	public boolean initialize(String sku, JSONObject jsonObject) {
		this.sku = sku;
		try {
			link = jsonObject.getString(RestConstants.JSON_LINK_TAG);
			image = getImageUrl(jsonObject.getString(RestConstants.JSON_VARIATION_IMAGE_TAG));
		} catch (JSONException e) {
			Log.e(TAG, "Error initializing the variation ", e);
			return false;
		}

		return true;
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
		try {
			link = jsonObject.getString(RestConstants.JSON_LINK_TAG);
			image = getImageUrl(jsonObject.getString(RestConstants.JSON_VARIATION_IMAGE_TAG));
		} catch (JSONException e) {
			Log.e(TAG, "Error initializing the variation ", e);
		}
		return true;
	}
	
	   /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }
	
	public String getSKU() {
		return sku;
	}
	
	public void setSKU(String sku) {
		this.sku = sku;
	}

	public String getLink() {
		return link;
	}

	public String getImage() {
		return image;
	}

	private String getImageUrl(String url) {
		String modUrl = ImageResolutionHelper.replaceResolution(url);
		if(modUrl != null)
			return modUrl;
		return url;
	}
	
}
