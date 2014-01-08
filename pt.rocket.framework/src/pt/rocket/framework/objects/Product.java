/**
 * @author GuilhermeSilva
 * @modified Manuel Silva
 * @version 1.5 - New Framework
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import pt.rocket.framework.interfaces.IJSONSerializable;
import pt.rocket.framework.rest.RestConstants;

/**
 * Class that represents the server side product. Contains id, name,
 * description, price(deprecated), stock(deprecated), list of images, brand and
 * list of category id.
 * 
 * @author GuilhermeSilva
 * @modified Manuel Silva
 * 
 */
public class Product implements IJSONSerializable, Parcelable {
	
	public final static String TAG = Product.class.getName();

    private String id;
    private ProductAttributes attributes;
    
    private ArrayList<Image> images;

    /**
     * simple product constructor.
     */
    public Product() {
        id = "";
        attributes = new ProductAttributes();
        images = new ArrayList<Image>();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the attributes
     */
    public ProductAttributes getAttributes() {
        return attributes;
    }

    /**
     * @return the images of the simple product.
     */
    public ArrayList<Image> getImages() {
        return images;
    }

    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            id = jsonObject.getString(RestConstants.JSON_ID_TAG);
            
            JSONObject attributesObject = jsonObject.optJSONObject(RestConstants.JSON_DATA_TAG);
            if(attributesObject != null){
                attributes.initialize(attributesObject);
            }
            
            JSONObject attributes2Object = jsonObject.optJSONObject(RestConstants.JSON_PROD_ATTRIBUTES_TAG);
            if(attributes2Object != null) {
                attributes.initialize(attributes2Object);
            }
            
            images.clear();
            JSONArray imageArray = jsonObject.optJSONArray(RestConstants.JSON_IMAGES_TAG);
            if (imageArray != null) {
                
                int imageArrayLenght = imageArray.length();
                for (int i = 0; i < imageArrayLenght; ++i) {
                    JSONObject imageObject = imageArray.getJSONObject(i);
                    Image image = new Image();
                    image.initialize(imageObject);
                    images.add(image);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.JSON_ID_TAG, id);
            jsonObject.put(RestConstants.JSON_DATA_TAG, attributes.toJSON());
            jsonObject.put(RestConstants.JSON_PROD_ATTRIBUTES_TAG, attributes.toJSON());
            
            JSONArray imageArray = new JSONArray();
            for(Image image : images) {
                imageArray.put(image.toJSON());
            }
            jsonObject.put(RestConstants.JSON_IMAGES_TAG, imageArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }

    /**
     * @return the product sku
     */
    public String getSKU() {
        return attributes.getSku();
    }

    /**
     * @return the product name
     */
    public String getName() {
        return attributes.getName();
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return attributes.getDescription();
    }

    /**
     * @return the price
     */
    public String getPrice() {
        return attributes.getPrice();
    }

    /**
     * @return the suggested price
     */
    public String getSuggestedPrice() {
        return attributes.getMaxPrice();
    }

    /**
     * @return the max saving percentage
     */
    public Double getMaxSavingPercentage() {
        return attributes.getMaxSavingPercentage();
    }

    /**
     * @return the rating.
     */
    public Double getRating() {
        return attributes.getRating();
    }
    
    public Integer getReviews() {
    	return attributes.getReviews();
    }

    public String getUrl() {
        return attributes.getUrl();
    }

    /**
     * @return the specialPrice
     */
    public String getSpecialPrice() {
        return attributes.getSpecialPrice();
    }

    /**
     * @return the maxSpecialPrice
     */
    public String getMaxSpecialPrice() {
        return attributes.getMaxSpecialPrice();
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);        
        dest.writeParcelable(attributes, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeList(images);
    }
    
    private Product(Parcel in) {
        id = in.readString();
        attributes = in.readParcelable(ProductAttributes.class.getClassLoader());
        images = new ArrayList<Image>();
        in.readList(images, Image.class.getClassLoader());
    }
    
    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
    
}
