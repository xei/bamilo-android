package com.mobile.newFramework.objects.product;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.mobile.framework.objects.BaseProduct;
import com.mobile.framework.objects.ProductDetailsSpecification;
import com.mobile.framework.objects.ProductSimple;
import com.mobile.framework.objects.Variation;
import com.mobile.framework.utils.CurrencyFormatter;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.Seller;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Class that manages the full representation of a given product.
 *
 * @author GuilhermeSilva
 */
public class CompleteProduct extends BaseProduct implements IJSONSerializable {

    private String idCatalogConfig;
    private String attributeSetId;
    private String activatedAt;
    private String description;
    private String ratingsUrl;
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
    private String mShortDescription;
    private ArrayList<ProductDetailsSpecification> mProductSpecs;

//	private int simpleSkuPosition;

    /**
     * Complete product empty constructor.
     */
    public CompleteProduct() {
        super();
        categories = new ArrayList<>();
        attributes = new HashMap<>();
        shipmentData = new HashMap<>();
        simples = new ArrayList<>();
        imageList = new ArrayList<>();
        variations = new ArrayList<>();
        known_variations = new ArrayList<>();
        description = "";
        specialPrice = "0";
        maxSavingPercentage = 0.0;
        ratingsAverage = 0.0;
        ratingsCount = 0;
        reviewsCount = 0;
        isNew = false;
        specialPriceConverted = 0d;
        mSizeGuideUrl = "";
        productBundle = null;
        hasSeller = false;
        hasBundle = false;
        seller = new Seller();
        minPriceOfferDouble = 0.0;
        minPriceOffer = "";
        minPriceOfferConverted = 0.0;
        totalOffers = 0;
        relatedProducts = new ArrayList<>();
        mShortDescription = "";
        mProductSpecs = new ArrayList<>();
    }


    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.OBJECT_DATA;
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

            sku = jsonObject.getString(RestConstants.JSON_SKU_TAG);
            name = jsonObject.getString(RestConstants.JSON_PROD_NAME_TAG);
            brand = jsonObject.getString(RestConstants.JSON_BRAND_TAG);
            idCatalogConfig = jsonObject.getString(RestConstants.JSON_ID_CATALOG_CONFIG_TAG);
            attributeSetId = jsonObject.getString(RestConstants.JSON_ATTRIBUTE_SET_ID_TAG);
            activatedAt = jsonObject.getString(RestConstants.JSON_ACTIVATED_AT_TAG);

            url = jsonObject.optString(RestConstants.JSON_PROD_URL_TAG, "");
            mSizeGuideUrl = jsonObject.optString(RestConstants.JSON_SIZE_GUIDE_URL_TAG);
            // Throw JSONException if JSON_PRICE_TAG is not present
            String priceJSON = jsonObject.getString(RestConstants.JSON_PRICE_TAG);
            if (!CurrencyFormatter.isNumber(priceJSON)) {
                throw new JSONException("Price is not a number!");
            }
            priceDouble = Double.parseDouble(priceJSON);
            price = priceJSON;
            priceConverted = jsonObject.optDouble(RestConstants.JSON_PRICE_CONVERTED_TAG, 0d);

            String specialPriceJSON = jsonObject.optString(RestConstants.JSON_SPECIAL_PRICE_TAG);
            if (!CurrencyFormatter.isNumber(specialPriceJSON)) {
                specialPriceJSON = priceJSON;
            }
            specialPriceDouble = Double.parseDouble(specialPriceJSON);

            specialPrice = specialPriceJSON;
            specialPriceConverted = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_CONVERTED_TAG, 0d);

            String maxSavingPercentageJSON = jsonObject.optString(RestConstants.JSON_MAX_SAVING_PERCENTAGE_TAG);
            if (CurrencyFormatter.isNumber(maxSavingPercentageJSON)) {
                maxSavingPercentage = Double.parseDouble(maxSavingPercentageJSON);
            } else {
                maxSavingPercentage = 0d;
            }
            // TODO: ratings need to be completed

            JSONObject ratingsSummaryObject = jsonObject.optJSONObject(RestConstants.JSON_RATINGS_SUMMARY_TAG);

            if (ratingsSummaryObject != null) {
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
            JSONArray categoriesArray = jsonObject.getJSONArray(RestConstants.JSON_CATEGORIES_TAG);
            for (int i = 0; i < categoriesArray.length(); ++i) {
                categories.add(categoriesArray.getString(i));
            }
            // attributes
            attributes.clear();
            JSONObject attributesObject = jsonObject.optJSONObject(RestConstants.JSON_PROD_ATTRIBUTES_TAG);

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
            JSONArray simpleArray = jsonObject.getJSONArray(RestConstants.JSON_SIMPLES_TAG);

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
            JSONArray imageArray = jsonObject.optJSONArray(RestConstants.JSON_IMAGE_LIST_TAG);
            if (null != imageArray) {
                for (int i = 0; i < imageArray.length(); ++i) {
                    JSONObject imageJsonObject = imageArray.getJSONObject(i);
                    imageList.add(getImageUrl(imageJsonObject.getString("url")));
                }
            }
            if (jsonObject.has(RestConstants.JSON_PROD_UNIQUES_TAG)
                    && jsonObject.optJSONObject(RestConstants.JSON_PROD_UNIQUES_TAG) != null
                    && jsonObject.optJSONObject(RestConstants.JSON_PROD_UNIQUES_TAG).optJSONObject(
                    RestConstants.JSON_ATTRIBUTES_TAG) != null) {

                JSONObject uniquesObject = jsonObject.optJSONObject(RestConstants.JSON_PROD_UNIQUES_TAG).optJSONObject(
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
            isNew = jsonObject.optBoolean(RestConstants.JSON_IS_NEW_TAG, false);

            hasBundle = jsonObject.optBoolean(RestConstants.JSON_HAS_BUNDLE_TAG, false);
            hasSeller = jsonObject.optBoolean(RestConstants.JSON_HAS_SELLER_TAG, false);
            if (hasSeller) {
                JSONObject sellerObject = jsonObject.optJSONObject(RestConstants.JSON_SELLER_TAG);
                if (sellerObject != null) {
                    seller = new Seller(sellerObject);
                }
            }

            //Offers
            JSONObject offers = jsonObject.optJSONObject(RestConstants.JSON_OFFERS_TAG);

            if (offers != null) {

                String offerPriceJSON = offers.optString(RestConstants.JSON_OFFERS_MIN_PRICE_TAG);

                if (!CurrencyFormatter.isNumber(offerPriceJSON)) {
                    offerPriceJSON = priceJSON;
                }
                minPriceOfferDouble = Double.parseDouble(offerPriceJSON);

                minPriceOffer = offerPriceJSON;


                minPriceOfferConverted = offers.optDouble(RestConstants.JSON_OFFERS_MIN_PRICE_CONVERTED_TAG, 0);
                totalOffers = offers.optInt(RestConstants.JSON_TOTAL_TAG, 0);
            }

            // Handle related products
            JSONArray relatedProductsJsonArray = jsonObject.optJSONArray(RestConstants.JSON_RELATED_PRODUCTS);
            if (relatedProductsJsonArray != null) {
                for (int i = 0; i < relatedProductsJsonArray.length(); i++) {
                    RelatedProduct relatedProduct = new RelatedProduct();
                    JSONObject relatedProductJsonObject = relatedProductsJsonArray.optJSONObject(i);
                    if (relatedProductJsonObject != null && relatedProduct.initialize(relatedProductJsonObject)) {
                        getRelatedProducts().add(relatedProduct);
                    }
                }
            }
            // PDV bucket info
            JSONObject summaryObject = jsonObject.optJSONObject(RestConstants.JSON_SUMMARY_TAG);
            if (summaryObject != null) {
                description = summaryObject.optString(RestConstants.JSON_DESCRIPTION_TAG, "");
                mShortDescription = summaryObject.optString(RestConstants.JSON_SHORT_DESC_TAG, "");
            }

            JSONArray specificationsArray = jsonObject.optJSONArray(RestConstants.JSON_SPECIFICATIONS_TAG);
            if (specificationsArray != null && specificationsArray.length() > 0) {
                for (int i = 0; i < specificationsArray.length(); i++) {
                    ProductDetailsSpecification prodSpecs = new ProductDetailsSpecification();
                    prodSpecs.initialize(specificationsArray.getJSONObject(i));
                    mProductSpecs.add(prodSpecs);
                }
            }

            JSONObject variationsObject = jsonObject.optJSONObject(RestConstants.JSON_VARIATIONS_TAG);
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
            //Log.e(TAG, "Error initializing the complete product", e);
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
     * @return the special price as a Double
     */
    public double getSpecialPriceAsDouble() {
        return specialPriceDouble;
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
     * @param idCatalogConfig the idCatalogConfig to set
     */
    public void setIdCatalogConfig(String idCatalogConfig) {
        this.idCatalogConfig = idCatalogConfig;
    }

    /**
     * @param attributeSetId the attributeSetId to set
     */
    public void setAttributeSetId(String attributeSetId) {
        this.attributeSetId = attributeSetId;
    }

    /**
     * @param activatedAt the activatedAt to set
     */
    public void setActivatedAt(String activatedAt) {
        this.activatedAt = activatedAt;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * @param shipmentData the shipmentData to set
     */
    public void setShipmentData(HashMap<String, String> shipmentData) {
        this.shipmentData = shipmentData;
    }

    /**
     * @param simples the simples to set
     */
    public void setSimples(ArrayList<ProductSimple> simples) {
        this.simples = simples;
    }

    /**
     * @param imageList the imageList to set
     */
    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
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
     * @return
     */
    public Integer getRatingsCount() {
        return ratingsCount;
    }

    public String getShortDescription() {
        return mShortDescription;
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
     * Return the paid price for tracking.
     *
     * @return double
     * @author sergiopereira
     */
    public double getPriceForTracking() {
        //Log.i(TAG, "ORIGIN PRICE VALUES: " + priceDouble + " " + specialPriceDouble);
        //Log.i(TAG, "PRICE VALUE FOR TRACKING: " + priceConverted + " " + specialPriceConverted);
        return specialPriceConverted > 0 ? specialPriceConverted : priceConverted;
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
     *
     * @return
     */
    public String getSizeGuideUrl() {
        return mSizeGuideUrl;
    }

    /**
     * Set size guide
     *
     * @return
     */
    public boolean hasSizeGuide() {
        return TextUtils.isEmpty(mSizeGuideUrl);
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

    public boolean hasBundle() {
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

    public ArrayList<ProductDetailsSpecification> getProductSpecifications() {
        return mProductSpecs;
    }

    public void setProductSpecifications(ArrayList<ProductDetailsSpecification> specs) {
        this.mProductSpecs = specs;
    }

    public ArrayList<RelatedProduct> getRelatedProducts() {
        return relatedProducts;
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
        super.writeToParcel(dest, flags);
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
        dest.writeDouble(priceConverted);
        dest.writeDouble(specialPriceConverted);
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
        dest.writeList(mProductSpecs);
        dest.writeString(mShortDescription);
    }

    private CompleteProduct(Parcel in) {
        super(in);
        categories = new ArrayList<>();
        in.readList(categories, String.class.getClassLoader());

        attributes = new HashMap<>();
        in.readMap(attributes, String.class.getClassLoader());

        shipmentData = new HashMap<>();
        in.readMap(shipmentData, String.class.getClassLoader());

        simples = new ArrayList<>();
        in.readList(simples, ProductSimple.class.getClassLoader());

        imageList = new ArrayList<>();
        in.readList(imageList, null);

        variations = new ArrayList<>();
        in.readList(variations, Variation.class.getClassLoader());

        url = in.readString();
        description = in.readString();
        specialPrice = in.readString();
        maxSavingPercentage = in.readDouble();
        ratingsAverage = in.readDouble();
        ratingsCount = in.readInt();
        reviewsCount = in.readInt();
        priceConverted = in.readDouble();
        specialPriceConverted = in.readDouble();
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

        mProductSpecs = new ArrayList<>();
        in.readList(mProductSpecs, ProductDetailsSpecification.class.getClassLoader());

        mShortDescription = in.readString();
    }

    public static final Parcelable.Creator<CompleteProduct> CREATOR = new Parcelable.Creator<CompleteProduct>() {
        public CompleteProduct createFromParcel(Parcel in) {
            return new CompleteProduct(in);
        }

        public CompleteProduct[] newArray(int size) {
            return new CompleteProduct[size];
        }
    };


}
