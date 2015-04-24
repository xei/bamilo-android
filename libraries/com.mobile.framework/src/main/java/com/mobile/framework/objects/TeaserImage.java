/**
 *
 */
package com.mobile.framework.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

/**
 * Defines an images of a given teaser.
 *
 * @author GuilhermeSilva
 */
public class TeaserImage implements IJSONSerializable, ITargeting, Parcelable {

    private int id = -1;
    private String width;
    private String height;
    private String format;
    private String imageUrl;
    private String navigationUrl;
    private String description;
    private TargetType targetType;
    private String imageTableUrl;

    /**
     *
     */
    public TeaserImage(JSONObject jsonObject) {
        initialize(jsonObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json
     * .JSONObject )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        id = jsonObject.optInt(RestConstants.JSON_ID_TAG, -1);
        JSONObject attributes = jsonObject.optJSONObject(RestConstants.JSON_TEASER_ATTRIBUTES_TAG);
        if (attributes == null) {
            return false;
        }
        setDescription(attributes.optString(RestConstants.JSON_TEASER_DESCRIPTION_TAG));
        targetType = TargetType.byValue(attributes.optString(RestConstants.JSON_TARGET_TYPE_TAG, TargetType.UNKNOWN.getValue()));

        // Get image list
        JSONArray imageList = attributes.optJSONArray(RestConstants.JSON_TEASER_IMAGES_TAG);
        if (imageList == null) {
            return false;
        }

        // Validate image structure
        JSONObject jsonImage = null;
        int size = imageList.length();
        for (int i = 0; i < size; i++) {
            // Validate device type
            jsonImage = imageList.optJSONObject(i);
            if (jsonImage == null) {
                return false;
            }
            /**
             * Get the device type tag, phone or tablet
             * The old version haven't the tag so return phone
             * @author sergiopereira
             */
            String device = jsonImage.optString(RestConstants.JSON_IMAGE_DEVICE_TYPE_TAG, RestConstants.JSON_PHONE_TAG);
            Log.d("IMAGE TEASER", "DEVICE: " + device);
            if (device.equalsIgnoreCase(RestConstants.JSON_PHONE_TAG)) {
                imageUrl = jsonImage.optString(RestConstants.JSON_TEASER_IMAGE_URL_TAG);
            } else if (device.equalsIgnoreCase(RestConstants.JSON_TABLET_TAG)) {
                imageTableUrl = jsonImage.optString(RestConstants.JSON_TEASER_IMAGE_URL_TAG);
            }
        }
        // Save the same data
        if (jsonImage == null) {
            return false;
        }
        navigationUrl = jsonImage.optString(RestConstants.JSON_TEASER_URL_TAG);
        format = jsonImage.optString(RestConstants.JSON_FORMAT_TAG);
        width = jsonImage.optString(RestConstants.JSON_WIDTH_TAG);
        height = jsonImage.optString(RestConstants.JSON_HEIGHT_TAG);

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    /**
     * @return the width
     */
    public String getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public String getHeight() {
        return height;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @return the imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * @return the navigationUrl
     */
    public String getNavigationUrl() {
        return navigationUrl;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.ITargetting#getTargetUrl()
     */
    @Override
    public String getTargetUrl() {
        return getNavigationUrl();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.ITargeting#getTargetType()
     */
    @Override
    public TargetType getTargetType() {
        return targetType;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.ITargeting#getTargetTitle()
     */
    @Override
    public String getTargetTitle() {
        return description;
    }

    /**
     * Get the image URL for tablet
     *
     * @return
     */
    public String getImageTableUrl() {
        return imageTableUrl;
    }


    /**
     * Set the image URL for tablet
     *
     * @param imageTableUrl
     */
    public void setImageTableUrl(String imageTableUrl) {
        this.imageTableUrl = imageTableUrl;
    }

    /**
     * ########### Parcelable ###########
     *
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
        dest.writeInt(id);
        dest.writeString(width);
        dest.writeString(height);
        dest.writeString(format);
        dest.writeString(imageUrl);
        dest.writeString(navigationUrl);
        dest.writeString(description);
        dest.writeValue(targetType);
    }

    /**
     * Parcel constructor
     *
     * @param in
     */
    private TeaserImage(Parcel in) {
        id = in.readInt();
        width = in.readString();
        height = in.readString();
        format = in.readString();
        imageUrl = in.readString();
        navigationUrl = in.readString();
        description = in.readString();
        targetType = (TargetType) in.readValue(TargetType.class.getClassLoader());
    }

    /**
     * Create parcelable
     */
    public static final Parcelable.Creator<TeaserImage> CREATOR = new Parcelable.Creator<TeaserImage>() {
        public TeaserImage createFromParcel(Parcel in) {
            return new TeaserImage(in);
        }

        public TeaserImage[] newArray(int size) {
            return new TeaserImage[size];
        }
    };

}
