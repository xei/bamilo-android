/**
 * CompleteProduct.java
 * Complete PRoduct class. Represents the complete product used in the products detials activity.
 * 
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */

package pt.rocket.framework.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.ImageResolutionHelper;
import pt.rocket.framework.utils.LogTagHelper;
import de.akquinet.android.androlog.Log;

/**
 * Class that manages the full representation of a given product.
 * @author GuilhermeSilva
 *
 */
public class CompleteProduct implements IJSONSerializable {
	
	private static final String TAG = LogTagHelper.create( CompleteProduct.class);
	
//    private static final String JSON_SKU_TAG = "sku";
//    private static final String JSON_NAME_TAG = "name";
//    private static final String JSON_ID_CATALOG_CONFIG_TAG = "id_catalog_config";
//    private static final String JSON_ATTRIBUTE_SET_ID_TAG = "attribute_set_id";
//    private static final String JSON_ACTIVATED_AT_TAG = "activated_at";
//    private static final String JSON_MAX_PRICE_TAG = "max_price";
//    private static final String JSON_PRICE_TAG = "price";
//    private static final String JSON_BRAND_TAG = "brand";
//    private static final String JSON_CATEGORIES_TAG = "categories";
//    private static final String JSON_ATTRIBUTES_TAG = "attributes";
//    private static final String JSON_SIMPLES_TAG = "simples";
//    private static final String JSON_IMAGE_LIST_TAG = "image_list";
//    private static final String JSON_URL_TAG = "url";
//    private static final String JSON_DESCRIPTION_TAG = "description";
//    private static final String JSON_SHORT_DESC_TAG = "short_description";
//    private static final String JSON_MAX_SAVING_PERCENTAGE_TAG = "max_saving_percentage";
//    private static final String JSON_MAX_SPECIAL_PRICE_TAG = "max_special_price";
//    private static final String JSON_SPECIAL_PRICE_TAG = "special_price";
//
//    private static final String JSON_RATINGS_TOTAL_TAG = "ratings_total";
//    private static final String JSON_RATINGS_TOTAL_SUM_TAG = "sum";
//    private static final String JSON_RATINGS_TOTAL_AVG_TAG = "avr";
//    
//    private static final String JSON_VARIATIONS_TAG = "variations";
    
    private String sku;
    private String name;
    private String idCatalogConfig;
    private String attributeSetId;
    private String activatedAt;
    private String url;
    private String description;
    private String ratingsUrl;

    private String price;
    private String maxPrice;
    private String brand;
    
    private double priceDouble;
    private double maxPriceDouble;

    private String specialPrice;
    private String maxSpecialPrice;
    private Double maxSavingPercentage;
    private Double ratingsAverage;
    private Integer ratingsCount;

    private ArrayList<String> categories;

    private HashMap<String, String> attributes;

    private HashMap<String, String> shipmentData;

    private ArrayList<ProductSimple> simples;
    
    private ArrayList<String> imageList;

    private ArrayList<Variation> variations;

    
    /**
     * Complete product empty constructor.
     */
    public CompleteProduct() {
        categories = new ArrayList<String>();

        attributes = new HashMap<String, String>();
        shipmentData = new HashMap<String, String>();

        simples = new ArrayList<ProductSimple>();
        imageList = new ArrayList<String>();
        variations = new ArrayList<Variation>();

        url = "";
        description = "";

        specialPrice = CurrencyFormatter.formatCurrency(0.0);
        maxSpecialPrice = CurrencyFormatter.formatCurrency(0.0);
        maxSavingPercentage = 0.0;
        
        ratingsAverage = 0.0;
        ratingsCount = 0;
    }

    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            JSONObject dataObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);

            sku = dataObject.getString(RestConstants.JSON_SKU_TAG);
            name = dataObject.getString(RestConstants.JSON_PROD_NAME_TAG);
            idCatalogConfig = dataObject.getString(RestConstants.JSON_ID_CATALOG_CONFIG_TAG);
            attributeSetId = dataObject.getString(RestConstants.JSON_ATTRIBUTE_SET_ID_TAG);
            activatedAt = dataObject.getString(RestConstants.JSON_ACTIVATED_AT_TAG);

            description = dataObject.optString(RestConstants.JSON_DESCRIPTION_TAG, "");
            url = dataObject.optString(RestConstants.JSON_PROD_URL_TAG, "");

            priceDouble = Double.parseDouble(dataObject.getString(RestConstants.JSON_PRICE_TAG));
            price = CurrencyFormatter.formatCurrency(priceDouble);
            maxPriceDouble = Double.parseDouble(dataObject.getString(RestConstants.JSON_MAX_PRICE_TAG));
            maxPrice = CurrencyFormatter.formatCurrency(maxPriceDouble);
            brand = dataObject.getString(RestConstants.JSON_BRAND_TAG);

            double specialPriceDouble = Double.parseDouble(dataObject.optString(RestConstants.JSON_SPECIAL_PRICE_TAG, "" + priceDouble));
            specialPrice = CurrencyFormatter.formatCurrency(specialPriceDouble);
            maxSpecialPrice = CurrencyFormatter.formatCurrency(Double.parseDouble(dataObject.optString(RestConstants.JSON_MAX_SPECIAL_PRICE_TAG, "" + maxPriceDouble)));
            maxSavingPercentage = Double.parseDouble(dataObject.optString(RestConstants.JSON_MAX_SAVING_PERCENTAGE_TAG, "0"));
            
            // TODO: ratings need to be completed
            JSONObject ratingsTotalObject = dataObject.optJSONObject(RestConstants.JSON_RATINGS_TOTAL_TAG);
            if(ratingsTotalObject != null) {            	
            	ratingsAverage = ratingsTotalObject.optDouble(RestConstants.JSON_RATINGS_TOTAL_AVG_TAG, .0);
            	ratingsCount = ratingsTotalObject.optInt( RestConstants.JSON_RATINGS_TOTAL_SUM_TAG, 0 );
            }

            if (maxSavingPercentage.equals(0) && !price.equals(specialPrice)) {
                maxSavingPercentage = (double) Math.round(specialPriceDouble * 100 / priceDouble);
            }

            categories.clear();
            JSONArray categoriesArray = dataObject.getJSONArray(RestConstants.JSON_CATEGORIES_TAG);
            for (int i = 0; i < categoriesArray.length(); ++i) {
                categories.add(categoriesArray.getString(i));
            }
            	
            	
            // attributes
            attributes.clear();
            
            
            JSONObject attributesObject = dataObject.optJSONObject(RestConstants.JSON_PROD_ATTRIBUTES_TAG);

            if(attributesObject != null) {
                JSONArray attributesNames = attributesObject.names();
                for (int i = 0; i < attributesNames.length(); ++i) {
                    String key = attributesNames.getString(i);
                    String value = attributesObject.getString(key);
                    attributes.put(key, value);
                }
            }
            	
            
            // simples
            simples.clear();
            JSONArray simpleArray = dataObject.getJSONArray(RestConstants.JSON_SIMPLES_TAG);

            for (int i = 0; i < simpleArray.length(); ++i) {
                ProductSimple simple = new ProductSimple();
                JSONObject simpleObject = simpleArray.getJSONObject(i);
                simple.initialize(simpleObject);
                
                String simpleSKU = simple.getAttributes().get(RestConstants.JSON_SKU_TAG);
                simples.add(simple);
            }

            // image_list
            imageList.clear();
            JSONArray imageArray = dataObject.getJSONArray(RestConstants.JSON_IMAGE_LIST_TAG);
            for (int i = 0; i < imageArray.length(); ++i) {
                JSONObject imageJsonObject = imageArray.getJSONObject(i);
                imageList.add(getImageUrl(imageJsonObject.getString("url")));
            }
            
            JSONObject variationsObject = dataObject.optJSONObject( RestConstants.JSON_VARIATIONS_TAG );
            if ( variationsObject == null)
            	return true;
            
            @SuppressWarnings("rawtypes")
			Iterator iter = variationsObject.keys();
            while( iter.hasNext()) {
            	String key = (String) iter.next();
            	JSONObject variationObject = variationsObject.getJSONObject( key );
            	Variation variation = new Variation();
            	variation.initialize(key, variationObject );
            	this.variations.add( variation );
            }
            
        } catch (JSONException e) {

            Log.e(TAG, "Error initializing the complete product", e);
            return false;
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

    /**
     * @return the sku
     */
    public String getSku() {
        return sku;
    }

    /**
     * Set the sku
     */
    public void setSku(String sku) {
        this.sku = sku;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     */
    public String getIdCatalogConfig() {
        return idCatalogConfig;
    }

    /**
     * @return the attributeSetId
     */
    public String getAttributeSetId() {
        return attributeSetId;
    }

    /**
     * @return the activatedAt
     */
    public String getActivatedAt() {
        return activatedAt;
    }

    /**
     * @return the price
     */
    public String getPrice() {
        return price;
    }

    /**
     * @return the price as a Double
     */
    public Double getPriceAsDouble() {
        return priceDouble;
    }
    
    /**
     * @return the maxPrice
     */
    public String getMaxPrice() {
        return maxPrice;
    }

    /**
     * @return the maxPrice as a Double
     */
    public Double getMaxPriceAsDouble() {
        return maxPriceDouble;
    }
    
    /**
     * @return the brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @return the categories
     */
    public ArrayList<String> getCategories() {
        return categories;
    }

    /**
     * @return the attributes
     */
    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    /**
     * @return the shipmentData
     */
    @Deprecated
    public HashMap<String, String> getShipmentData() {
        return shipmentData;
    }

    /**
     * @return the simples
     */
    public ArrayList<ProductSimple> getSimples() {
        return simples;
    }

    /**
     * @return the imageList
     */
    public ArrayList<String> getImageList() {
        return imageList;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get Better quality image.
     * @param url
     * @return
     */
	private String getImageUrl(String url) {
		String modUrl = ImageResolutionHelper.replaceResolution(url);
		if(modUrl != null)
			return modUrl;
		return url;
	}
    
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the ratings_Url
     */
    @Deprecated
    public String getRatingsUrl() {
        return ratingsUrl;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param idCatalogConfig
     *            the idCatalogConfig to set
     */
    public void setIdCatalogConfig(String idCatalogConfig) {
        this.idCatalogConfig = idCatalogConfig;
    }

    /**
     * @param attributeSetId
     *            the attributeSetId to set
     */
    public void setAttributeSetId(String attributeSetId) {
        this.attributeSetId = attributeSetId;
    }

    /**
     * @param activatedAt
     *            the activatedAt to set
     */
    public void setActivatedAt(String activatedAt) {
        this.activatedAt = activatedAt;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param price
     *            the price to set
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * @param maxPrice
     *            the maxPrice to set
     */
    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    /**
     * @param brand
     *            the brand to set
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * @param categories
     *            the categories to set
     */
    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    /**
     * @param attributes
     *            the attributes to set
     */
    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * @param shipmentData
     *            the shipmentData to set
     */
    public void setShipmentData(HashMap<String, String> shipmentData) {
        this.shipmentData = shipmentData;
    }

    /**
     * @param simples
     *            the simples to set
     */
    public void setSimples(ArrayList<ProductSimple> simples) {
        this.simples = simples;
    }

    /**
     * @param imageList
     *            the imageList to set
     */
    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    /**
     * @return the specialPrice
     */
    public String getSpecialPrice() {
        return specialPrice;
    }

    /**
     * @return the maxSpecialPrice
     */
    public String getMaxSpecialPrice() {
        return maxSpecialPrice;
    }

    /**
     * @return the maxSavingPercentage
     */
    public Double getMaxSavingPercentage() {
        return maxSavingPercentage;
    }

    /**
     * @return the ratings average
     */
    public Double getRatingsAverage() {
        return ratingsAverage;
    }
    
    /**
     * @return the ratings count
     * @return
     */
    public Integer getRatingsCount() {
    	return ratingsCount;
    }
    
    public String getShortDescription() {
    	return attributes.get(RestConstants.JSON_SHORT_DESC_TAG);
    }
    
	public boolean hasDiscount() {
		return specialPrice != null && !specialPrice.equalsIgnoreCase(price);
	}
	
	public ArrayList<Variation> getVariations() {
		return variations;
	}
}
