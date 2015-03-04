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

package com.mobile.framework.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.CurrencyFormatter;
import com.mobile.framework.utils.LogTagHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.akquinet.android.androlog.Log;

/**
 * Class that manages the full representation of a given product.
 * 
 * @author GuilhermeSilva
 * 
 */
public class CompleteProduct implements IJSONSerializable, Parcelable {

	private static final String TAG = LogTagHelper.create(CompleteProduct.class);

	private String sku;
	private String name;
	private String idCatalogConfig;
	private String attributeSetId;
	private String activatedAt;
	private String url;
	private String description;
	private String ratingsUrl;
	private String price;
	private String brand;
	private double priceDouble;
	private String specialPrice;
	private double specialPriceDouble;
	private Double maxSavingPercentage;
	private Double ratingsAverage;
	private Integer ratingsCount;
	private Integer reviewsCount;
	private ArrayList<String> categories;
	private HashMap<String, String> attributes;
	private HashMap<String, String> shipmentData;
	private ArrayList<ProductSimple> simples;
	private ArrayList<String> imageList;
	private ArrayList<Variation> variations;
	private ArrayList<String> known_variations;
	private boolean isNew;
	private double mPriceConverted;
	private double mSpecialPriceConverted;
	private String mSizeGuideUrl;
	private ProductBundle productBundle;
	private boolean hasSeller;
	private boolean hasBundle;
	private Seller seller;
	private double minPriceOfferDouble;
	private String minPriceOffer;
	private double minPriceOfferConverted;
	private int totalOffers;
    private ArrayList<RelatedProduct> relatedProducts;
	
//	private int simpleSkuPosition;

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
		known_variations = new ArrayList<String>();
		url = "";
		description = "";
		specialPrice = CurrencyFormatter.formatCurrency("0");
		maxSavingPercentage = 0.0;
		ratingsAverage = 0.0;
		ratingsCount = 0;
		reviewsCount = 0;
		isNew = false;
		mPriceConverted = 0d;
		mSpecialPriceConverted = 0d;
		mSizeGuideUrl = "";
		productBundle = null;
		hasSeller = false;
		hasBundle = false;
		seller = new Seller();
		minPriceOfferDouble = 0.0;
		minPriceOffer = "";
		minPriceOfferConverted = 0.0;
		totalOffers = 0;
        relatedProducts = new ArrayList<RelatedProduct>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
	 * )
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) {
		try {
		    
			JSONObject dataObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);

			sku = dataObject.getString(RestConstants.JSON_SKU_TAG);
			name = dataObject.getString(RestConstants.JSON_PROD_NAME_TAG);
			brand = dataObject.getString(RestConstants.JSON_BRAND_TAG);
			idCatalogConfig = dataObject.getString(RestConstants.JSON_ID_CATALOG_CONFIG_TAG);
			attributeSetId = dataObject.getString(RestConstants.JSON_ATTRIBUTE_SET_ID_TAG);
			activatedAt = dataObject.getString(RestConstants.JSON_ACTIVATED_AT_TAG);
			description = dataObject.optString(RestConstants.JSON_DESCRIPTION_TAG, "");
			url = dataObject.optString(RestConstants.JSON_PROD_URL_TAG, "");
			mSizeGuideUrl = dataObject.optString(RestConstants.JSON_SIZE_GUIDE_URL_TAG);
			// Throw JSONException if JSON_PRICE_TAG is not present
			String priceJSON = dataObject.getString(RestConstants.JSON_PRICE_TAG);
			if (!CurrencyFormatter.isNumber(priceJSON)) {
				throw new JSONException("Price is not a number!");
			}
			priceDouble = Double.parseDouble(priceJSON);
			price = CurrencyFormatter.formatCurrency(priceJSON);
			mPriceConverted = dataObject.optDouble(RestConstants.JSON_PRICE_CONVERTED_TAG, 0d);

			String specialPriceJSON = dataObject.optString(RestConstants.JSON_SPECIAL_PRICE_TAG);
			if (!CurrencyFormatter.isNumber(specialPriceJSON)) {
				specialPriceJSON = priceJSON;
			}
			specialPriceDouble = Double.parseDouble(specialPriceJSON);

			specialPrice = CurrencyFormatter.formatCurrency(specialPriceJSON);
			mSpecialPriceConverted = dataObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_CONVERTED_TAG, 0d);

			String maxSavingPercentageJSON = dataObject.optString(RestConstants.JSON_MAX_SAVING_PERCENTAGE_TAG);
			if (CurrencyFormatter.isNumber(maxSavingPercentageJSON)) {
				maxSavingPercentage = Double.parseDouble(maxSavingPercentageJSON);
			} else {
				maxSavingPercentage = 0d;
			}
			// TODO: ratings need to be completed
			
          JSONObject ratingsSummaryObject = dataObject.optJSONObject(RestConstants.JSON_RATINGS_SUMMARY_TAG);

          if(ratingsSummaryObject != null){
              ratingsAverage = ratingsSummaryObject.optDouble(RestConstants.JSON_RATINGS_AVERAGE_TAG, .0);
              ratingsCount = ratingsSummaryObject.optInt(RestConstants.JSON_RATINGS_TOTAL_TAG, 0);
              reviewsCount = ratingsSummaryObject.optInt(RestConstants.JSON_REVIEWS_TOTAL_TAG, 0);
          }
            
//          JSONObject ratingsTotalObject = dataObject.optJSONObject(RestConstants.JSON_RATINGS_TOTAL_TAG);
//          if (ratingsTotalObject != null) {
//              ratingsAverage = ratingsTotalObject.optDouble(RestConstants.JSON_RATINGS_TOTAL_AVG_TAG, .0);
//              ratingsCount = ratingsTotalObject.optInt(RestConstants.JSON_RATINGS_TOTAL_SUM_TAG, 0);
//          }
//          
            
            
            
			
			/*
			if (maxSavingPercentage.equals(0D) && !price.equals(specialPrice)) {
				maxSavingPercentage = (double) Math.round(specialPriceDouble * 100 / priceDouble);
			}
			*/

			categories.clear();
			JSONArray categoriesArray = dataObject.getJSONArray(RestConstants.JSON_CATEGORIES_TAG);
			for (int i = 0; i < categoriesArray.length(); ++i) {
				categories.add(categoriesArray.getString(i));
			}
			// attributes
			attributes.clear();
			JSONObject attributesObject = dataObject.optJSONObject(RestConstants.JSON_PROD_ATTRIBUTES_TAG);

			if (attributesObject != null) {
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

				// String simpleSKU =
				// simple.getAttributes().get(RestConstants.JSON_SKU_TAG);

				simples.add(simple);
			}
			// image_list
			imageList.clear();
			JSONArray imageArray = dataObject.optJSONArray(RestConstants.JSON_IMAGE_LIST_TAG);
			if (null != imageArray) {
				for (int i = 0; i < imageArray.length(); ++i) {
					JSONObject imageJsonObject = imageArray.getJSONObject(i);
					imageList.add(getImageUrl(imageJsonObject.getString("url")));
				}
			}
			if (dataObject.has(RestConstants.JSON_PROD_UNIQUES_TAG)
					&& dataObject.optJSONObject(RestConstants.JSON_PROD_UNIQUES_TAG) != null
					&& dataObject.optJSONObject(RestConstants.JSON_PROD_UNIQUES_TAG).optJSONObject(
							RestConstants.JSON_ATTRIBUTES_TAG) != null) {

				JSONObject uniquesObject = dataObject.optJSONObject(RestConstants.JSON_PROD_UNIQUES_TAG).optJSONObject(
						RestConstants.JSON_ATTRIBUTES_TAG);

				@SuppressWarnings("rawtypes")
				Iterator iterUniques = uniquesObject.keys();
				while (iterUniques.hasNext()) {
					String key = (String) iterUniques.next();
					String value = "";

					if (uniquesObject.has(key)) {
						value = uniquesObject.getString(key);
					}
					if (value != null && !value.equalsIgnoreCase("")) {
						this.known_variations.add(value);
					}

				}

			}
			isNew = dataObject.optBoolean(RestConstants.JSON_IS_NEW_TAG, false);

	        hasBundle = dataObject.optBoolean(RestConstants.JSON_HAS_BUNDLE_TAG, false);
	        hasSeller = dataObject.optBoolean(RestConstants.JSON_HAS_SELLER_TAG, false);
	        if(hasSeller){
	            JSONObject sellerObject = dataObject.optJSONObject(RestConstants.JSON_SELLER_TAG);
	            if(sellerObject != null){
	                 seller = new Seller(sellerObject);
	            }
	        }
	        
	        //Offers
	        JSONObject offers = dataObject.optJSONObject(RestConstants.JSON_OFFERS_TAG);
	        
	        if(offers != null){
	            
	            String offerPriceJSON = offers.optString(RestConstants.JSON_OFFERS_MIN_PRICE_TAG);
	            
	            if (!CurrencyFormatter.isNumber(offerPriceJSON)) {
	                offerPriceJSON = priceJSON;
	                }
	            minPriceOfferDouble = Double.parseDouble(offerPriceJSON);

	            minPriceOffer = CurrencyFormatter.formatCurrency(offerPriceJSON);
	            
	            
	            minPriceOfferConverted = offers.optDouble(RestConstants.JSON_OFFERS_MIN_PRICE_CONVERTED_TAG, 0);
	            totalOffers = offers.optInt(RestConstants.JSON_TOTAL_TAG, 0);
	        }

            // Handle related products

            JSONArray relatedProductsJsonArray = dataObject.optJSONArray(RestConstants.JSON_RELATED_PRODUCTS);
            if(relatedProductsJsonArray != null){
                for(int i = 0; i<relatedProductsJsonArray.length();i++){
                    RelatedProduct relatedProduct = new RelatedProduct();
                    JSONObject relatedProductJsonObject = relatedProductsJsonArray.optJSONObject(i);
                    if(relatedProductJsonObject != null && relatedProduct.initialize(relatedProductJsonObject)){
                        getRelatedProducts().add(relatedProduct);
                    }
                }
            }

			JSONObject variationsObject = dataObject.optJSONObject(RestConstants.JSON_VARIATIONS_TAG);
			if (variationsObject == null)
				return true;
			@SuppressWarnings("rawtypes")
			Iterator iter = variationsObject.keys();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				JSONObject variationObject = variationsObject.getJSONObject(key);
				Variation variation = new Variation();
				variation.initialize(key, variationObject);
				this.variations.add(variation);
			}


		} catch (JSONException e) {

			Log.e(TAG, "Error initializing the complete product", e);
			return false;
		}
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
	 * @return the special price as a Double
	 */
	public double getSpecialPriceAsDouble() {
		return specialPriceDouble;
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
	 * 
	 * @param url
	 * @return
	 */
	private String getImageUrl(String url) {
		// String modUrl = ImageResolutionHelper.replaceResolution(url);
		// if(modUrl != null)
		// return modUrl;
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

	public ArrayList<String> getKnownVariations() {
		return known_variations;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	/**
	 * @return the mPriceConverted
	 */
	public double getPriceConverted() {
		return mPriceConverted;
	}

	/**
	 * @param priceConverted the mPriceConverted to set
	 */
	public void setPriceConverted(double priceConverted) {
		this.mPriceConverted = priceConverted;
	}

	/**
	 * @return the mSpecialPriceConverted
	 */
	public double getSpecialPriceConverted() {
		return mSpecialPriceConverted;
	}

	/**
	 * @param specialPriceConverted the mSpecialPriceConverted to set
	 */
	public void setSpecialPriceConverted(double specialPriceConverted) {
		this.mSpecialPriceConverted = specialPriceConverted;
	}

	/**
	 * Return the paid price for tracking.
	 * 
	 * @return double
	 * @author sergiopereira
	 */
	public double getPriceForTracking() {
		//Log.i(TAG, "ORIGIN PRICE VALUES: " + priceDouble + " " + specialPriceDouble);
		//Log.i(TAG, "PRICE VALUE FOR TRACKING: " + mPriceConverted + " " + mSpecialPriceConverted);
		return mSpecialPriceConverted > 0 ? mSpecialPriceConverted : mPriceConverted;
	}


//    public int getSimpleSkuPosition() {
//        return simpleSkuPosition;
//    }
//
//    public void setSimpleSkuPosition(int simpleSkuPosition) {
//        this.simpleSkuPosition = simpleSkuPosition;
//    }
//	
	
	/**
	 * Get size guide URL
	 * @return
	 */
	public String getSizeGuideUrl() {
	    return mSizeGuideUrl;
	}
	
	/**
	 * Set size guide
	 * @return
	 */
	public boolean hasSizeGuide() {
	    return TextUtils.isEmpty(mSizeGuideUrl) ? false : true;
	}
	
    public ProductBundle getProductBundle() {
        return productBundle;
    }

    public void setProductBundle(ProductBundle productBundle) {
        this.productBundle = productBundle;
    }

    public boolean hasSeller() {
        return hasSeller;
    }

    public void setHasSeller(boolean hasSeller) {
        this.hasSeller = hasSeller;
    }

    public boolean isHasBundle() {
        return hasBundle;
    }

    public void setHasBundle(boolean hasBundle) {
        this.hasBundle = hasBundle;
    }
    
    public Integer getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(Integer reviewsCount) {
        this.reviewsCount = reviewsCount;
    }
    
    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public double getMinPriceOfferDouble() {
        return minPriceOfferDouble;
    }

    public void setMinPriceOfferDouble(double minPriceOffer) {
        this.minPriceOfferDouble = minPriceOfferDouble;
    }

    public String getMinPriceOffer() {
        return minPriceOffer;
    }

    public void setMinPriceOffer(String minPriceOffer) {
        this.minPriceOffer = minPriceOffer;
    }
    
    public double getMinPriceOfferConverted() {
        return minPriceOfferConverted;
    }

    public void setMinPriceOfferConverted(double minPriceOfferConverted) {
        this.minPriceOfferConverted = minPriceOfferConverted;
    }

    public int getTotalOffers() {
        return totalOffers;
    }

    public void setTotalOffers(int totalOffers) {
        this.totalOffers = totalOffers;
    }
    
    
    
    /*
     * ############ PARCELABLE ############
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
		dest.writeList(categories);
		dest.writeMap(attributes);
		dest.writeMap(shipmentData);
		dest.writeList(simples);
		dest.writeList(imageList);
		dest.writeList(variations);
		dest.writeString(url);
		dest.writeString(description);
		dest.writeString(specialPrice);
		dest.writeDouble(maxSavingPercentage);
		dest.writeDouble(ratingsAverage);
		dest.writeInt(ratingsCount);
		dest.writeInt(reviewsCount);
		dest.writeDouble(mPriceConverted);
		dest.writeDouble(mSpecialPriceConverted);
		dest.writeString(mSizeGuideUrl);
		dest.writeByte((byte) (isNew ? 1 : 0));
		dest.writeByte((byte) (hasSeller ? 1 : 0));
		dest.writeByte((byte) (hasBundle ? 1 : 0));
		dest.writeParcelable(seller, flags);
		dest.writeParcelable(productBundle, flags);
		dest.writeDouble(minPriceOfferDouble);
		dest.writeString(minPriceOffer);
		dest.writeDouble(minPriceOfferConverted);
		dest.writeInt(totalOffers);
	}

	private CompleteProduct(Parcel in) {

		categories = new ArrayList<String>();
		in.readList(categories, String.class.getClassLoader());

		attributes = new HashMap<String, String>();
		in.readMap(attributes, String.class.getClassLoader());

		shipmentData = new HashMap<String, String>();
		in.readMap(shipmentData, String.class.getClassLoader());

		simples = new ArrayList<ProductSimple>();
		in.readList(simples, ProductSimple.class.getClassLoader());

		imageList = new ArrayList<String>();
		in.readList(imageList, null);

		variations = new ArrayList<Variation>();
		in.readList(variations, Variation.class.getClassLoader());

		url = in.readString();
		description = in.readString();
		specialPrice = in.readString();
		maxSavingPercentage = in.readDouble();
		ratingsAverage = in.readDouble();
		ratingsCount = in.readInt();
		reviewsCount = in.readInt();
		mPriceConverted = in.readDouble();
		mSpecialPriceConverted = in.readDouble();
		mSizeGuideUrl = in.readString();
		isNew = in.readByte() == 1;
		hasSeller = in.readByte() == 1;
		hasBundle = in.readByte() == 1;
		seller = in.readParcelable(Seller.class.getClassLoader());
		productBundle = in.readParcelable(ProductBundle.class.getClassLoader());
		minPriceOfferDouble = in.readDouble();
		minPriceOffer = in.readString();
        minPriceOfferConverted = in.readDouble();
        totalOffers = in.readInt();
	}

	public static final Parcelable.Creator<CompleteProduct> CREATOR = new Parcelable.Creator<CompleteProduct>() {
		public CompleteProduct createFromParcel(Parcel in) {
			return new CompleteProduct(in);
		}

		public CompleteProduct[] newArray(int size) {
			return new CompleteProduct[size];
		}
	};

    public ArrayList<RelatedProduct> getRelatedProducts() {
        return relatedProducts;
    }
}
