package com.mobile.newFramework.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;

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
public class CompleteProduct extends NewProductAddableToCart implements IJSONSerializable {

    private static final String TAG = CompleteProduct.class.getSimpleName();

    private String description;
    private Double ratingsAverage;
    private Integer ratingsCount;
    private Integer reviewsCount;
    private String categories;
    private HashMap<String, String> attributes;
    private HashMap<String, String> shipmentData;
    //private ArrayList<ProductSimple> simples;
    private ArrayList<String> imageList;
    private ArrayList<Variation> variations;
    private ArrayList<String> known_variations;
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

    /**
     * Complete product empty constructor.
     */
    public CompleteProduct() {
        super();
        attributes = new HashMap<>();
        shipmentData = new HashMap<>();
        //simples = new ArrayList<>();
        imageList = new ArrayList<>();
        variations = new ArrayList<>();
        known_variations = new ArrayList<>();
        description = "";
        ratingsAverage = 0.0;
        ratingsCount = 0;
        reviewsCount = 0;
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
            // Base
            super.initialize(jsonObject);
            // Complete
            categories = jsonObject.getString(RestConstants.JSON_CATEGORIES_TAG);
            hasBundle = jsonObject.optBoolean(RestConstants.JSON_HAS_BUNDLE_TAG);
            // Rating
            JSONObject ratingsSummaryObject = jsonObject.optJSONObject(RestConstants.JSON_RATINGS_SUMMARY_TAG);
            if (ratingsSummaryObject != null) {
                ratingsAverage = ratingsSummaryObject.optDouble(RestConstants.JSON_RATINGS_AVERAGE_TAG, .0);
                ratingsCount = ratingsSummaryObject.optInt(RestConstants.JSON_RATINGS_TOTAL_TAG, 0);
                reviewsCount = ratingsSummaryObject.optInt(RestConstants.JSON_REVIEWS_TOTAL_TAG, 0);
            }
            // Attributes
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
//            // Simples
//            simples.clear();
//            JSONArray simpleArray = jsonObject.getJSONArray(RestConstants.JSON_SIMPLES_TAG);
//            for (int i = 0; i < simpleArray.length(); ++i) {
//                ProductSimple simple = new ProductSimple();
//                JSONObject simpleObject = simpleArray.getJSONObject(i);
//                simple.initialize(simpleObject);
//                simples.add(simple);
//            }
            // Images
            imageList.clear();
            JSONArray imageArray = jsonObject.optJSONArray(RestConstants.JSON_IMAGE_LIST_TAG);
            if (null != imageArray) {
                for (int i = 0; i < imageArray.length(); ++i) {
                    JSONObject imageJsonObject = imageArray.getJSONObject(i);
                    imageList.add(imageJsonObject.getString("url"));
                }
            }
            // Uniques
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
            // Seller
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
                    offerPriceJSON = ""+mPrice;
                }
                minPriceOfferDouble = Double.parseDouble(offerPriceJSON);
                minPriceOffer = offerPriceJSON;
                minPriceOfferConverted = offers.optDouble(RestConstants.JSON_OFFERS_MIN_PRICE_CONVERTED_TAG, 0);
                totalOffers = offers.optInt(RestConstants.JSON_TOTAL_TAG, 0);
            }
            // Related products
            JSONArray relatedProductsJsonArray = jsonObject.optJSONArray(RestConstants.JSON_RELATED_PRODUCTS);
            if (relatedProductsJsonArray != null) {
                for (int i = 0; i < relatedProductsJsonArray.length(); i++) {
                    RelatedProduct relatedProduct = new RelatedProduct();
                    JSONObject relatedProductJsonObject = relatedProductsJsonArray.optJSONObject(i);
                    if (relatedProductJsonObject != null && relatedProduct.initialize(relatedProductJsonObject)) {
                        relatedProducts.add(relatedProduct);
                    }
                }
            }
            // PDV bucket info
            JSONObject summaryObject = jsonObject.optJSONObject(RestConstants.JSON_SUMMARY_TAG);
            if (summaryObject != null) {
                description = summaryObject.optString(RestConstants.JSON_DESCRIPTION_TAG, "");
                mShortDescription = summaryObject.optString(RestConstants.JSON_SHORT_DESC_TAG, "");
            }
            // Specs
            JSONArray specificationsArray = jsonObject.optJSONArray(RestConstants.JSON_SPECIFICATIONS_TAG);
            if (specificationsArray != null && specificationsArray.length() > 0) {
                for (int i = 0; i < specificationsArray.length(); i++) {
                    ProductDetailsSpecification prodSpecs = new ProductDetailsSpecification();
                    prodSpecs.initialize(specificationsArray.getJSONObject(i));
                    mProductSpecs.add(prodSpecs);
                }
            }
            // Variations
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
            Print.e(TAG, "Error initializing the complete product", e);
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
     * @return the categories
     */
    public String getCategories() {
        return categories;
    }

    public String[] getCategoriesList(){
        return categories.split(",");
    }

    /**
     * @return the attributes
     */
    public HashMap<String, String> getAttributes() {
        return attributes;
    }

//    /**
//     * @return the simples
//     */
//    public ArrayList<ProductSimple> getSimples() {
//        return simples;
//    }

    /**
     * @return the imageList
     */
    public ArrayList<String> getImageList() {
        return imageList;
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
     * @param attributes the attributes to set
     */
    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * @param imageList the imageList to set
     */
    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    /**
     * @return the ratings average
     */
    public Double getRatingsAverage() {
        return ratingsAverage;
    }

    public Integer getRatingsCount() {
        return ratingsCount;
    }

    public String getShortDescription() {
        return mShortDescription;
    }

    public ArrayList<Variation> getVariations() {
        return variations;
    }

    public ArrayList<String> getKnownVariations() {
        return known_variations;
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

    public boolean hasBundle() {
        return hasBundle;
    }

    public Integer getReviewsCount() {
        return reviewsCount;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public String getMinPriceOffer() {
        return minPriceOffer;
    }

    public int getTotalOffers() {
        return totalOffers;
    }

    public ArrayList<ProductDetailsSpecification> getProductSpecifications() {
        return mProductSpecs;
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
        dest.writeString(categories);
        dest.writeMap(attributes);
        dest.writeMap(shipmentData);
        //dest.writeList(simples);
        dest.writeList(imageList);
        dest.writeList(variations);
        dest.writeString(description);
        dest.writeDouble(ratingsAverage);
        dest.writeInt(ratingsCount);
        dest.writeInt(reviewsCount);
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
        categories = in.readString();

        attributes = new HashMap<>();
        in.readMap(attributes, String.class.getClassLoader());

        shipmentData = new HashMap<>();
        in.readMap(shipmentData, String.class.getClassLoader());

//        simples = new ArrayList<>();
//        in.readList(simples, ProductSimple.class.getClassLoader());

        imageList = new ArrayList<>();
        in.readList(imageList, null);

        variations = new ArrayList<>();
        in.readList(variations, Variation.class.getClassLoader());

        description = in.readString();
        ratingsAverage = in.readDouble();
        ratingsCount = in.readInt();
        reviewsCount = in.readInt();
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
