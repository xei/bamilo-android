package com.mobile.newFramework.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class that manages the full representation of a given product.
 *
 * @author sergiopereira
 */
public class NewProductComplete extends NewProductAddableToCart {

    private static final String TAG = NewProductComplete.class.getSimpleName();

    private String mDescription;
    private String mShortDescription;
    private ArrayList<String> mImageList;
    private ArrayList<Variation> mVariations;
    private ProductBundle mProductBundle;
    private boolean hasSeller;
    private boolean hasBundle;
    private Seller mSeller;
    private double mMinPriceOfferDouble;
    private String mMinPriceOffer;
    private double mMinPriceOfferConverted;
    private int mTotalOffers;
    private ArrayList<NewProductPartial> mRelatedProducts;
    private ArrayList<ProductDetailsSpecification> mProductSpecs;
    private String mShareUrl;

    /**
     * Complete product empty constructor.
     */
    public NewProductComplete() {
        super();
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
            // Share url
            mShareUrl = jsonObject.optString(RestConstants.JSON_SHARE_URL_TAG);
            // Bundle
            hasBundle = jsonObject.optBoolean(RestConstants.JSON_HAS_BUNDLE_TAG);
            // Images
            JSONArray imageArray = jsonObject.optJSONArray(RestConstants.JSON_IMAGE_LIST_TAG);
            if (imageArray != null && imageArray.length() > 0) {
                mImageList = new ArrayList<>();
                for (int i = 0; i < imageArray.length(); ++i) {
                    JSONObject imageJsonObject = imageArray.getJSONObject(i);
                    mImageList.add(imageJsonObject.getString(RestConstants.JSON_URL_TAG));
                }
            }
            // Seller
            JSONObject sellerObject = jsonObject.optJSONObject(RestConstants.JSON_SELLER_TAG);
            if (sellerObject != null) {
                mSeller = new Seller(sellerObject);
                hasSeller = true;
            }
            //Offers
            JSONObject offers = jsonObject.optJSONObject(RestConstants.JSON_OFFERS_TAG);
            if (offers != null) {
                String offerPriceJSON = offers.optString(RestConstants.JSON_OFFERS_MIN_PRICE_TAG);
                if (!CurrencyFormatter.isNumber(offerPriceJSON)) {
                    offerPriceJSON = "" + mPrice;
                }
                mMinPriceOfferDouble = Double.parseDouble(offerPriceJSON);
                mMinPriceOffer = offerPriceJSON;
                mMinPriceOfferConverted = offers.optDouble(RestConstants.JSON_OFFERS_MIN_PRICE_CONVERTED_TAG, 0);
                mTotalOffers = offers.optInt(RestConstants.JSON_TOTAL_TAG);
            }
            // Related products
            JSONArray relatedProductsJsonArray = jsonObject.optJSONArray(RestConstants.JSON_RELATED_PRODUCTS);
            if (relatedProductsJsonArray != null && relatedProductsJsonArray.length() > 0) {
                mRelatedProducts = new ArrayList<>();
                for (int i = 0; i < relatedProductsJsonArray.length(); i++) {
                    NewProductPartial relatedProduct = new NewProductPartial();
                    if (relatedProduct.initialize(relatedProductsJsonArray.optJSONObject(i))) {
                        mRelatedProducts.add(relatedProduct);
                    }
                }
            }
            // Summary
            JSONObject summaryObject = jsonObject.optJSONObject(RestConstants.JSON_SUMMARY_TAG);
            if (summaryObject != null) {
                mDescription = summaryObject.optString(RestConstants.JSON_DESCRIPTION_TAG);
                mShortDescription = summaryObject.optString(RestConstants.JSON_SHORT_DESC_TAG);
            }
            // Specifications
            JSONArray specificationsArray = jsonObject.optJSONArray(RestConstants.JSON_SPECIFICATIONS_TAG);
            if (specificationsArray != null && specificationsArray.length() > 0) {
                mProductSpecs = new ArrayList<>();
                for (int i = 0; i < specificationsArray.length(); i++) {
                    ProductDetailsSpecification prodSpecs = new ProductDetailsSpecification();
                    prodSpecs.initialize(specificationsArray.getJSONObject(i));
                    mProductSpecs.add(prodSpecs);
                }
            }
            // Variations
            JSONObject variationsObject = jsonObject.optJSONObject(RestConstants.JSON_VARIATIONS_TAG);
            if (variationsObject != null) {
                mVariations = new ArrayList<>();
                @SuppressWarnings("rawtypes")
                Iterator keys = variationsObject.keys();
                while (keys.hasNext()) {
                    String sku = (String) keys.next();
                    JSONObject variationObject = variationsObject.getJSONObject(sku);
                    Variation variation = new Variation();
                    variation.initialize(sku, variationObject);
                    mVariations.add(variation);
                }
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

    public ArrayList<String> getImageList() {
        return mImageList;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setImageList(ArrayList<String> mImageList) {
        this.mImageList = mImageList;
    }

    public String getShortDescription() {
        return mShortDescription;
    }

    public ArrayList<Variation> getVariations() {
        return mVariations;
    }

    public ProductBundle getProductBundle() {
        return mProductBundle;
    }

    public void setProductBundle(ProductBundle mProductBundle) {
        this.mProductBundle = mProductBundle;
    }

    public boolean hasSeller() {
        return hasSeller;
    }

    public boolean hasBundle() {
        return hasBundle;
    }

    public Seller getSeller() {
        return mSeller;
    }

    public String getMinPriceOffer() {
        return mMinPriceOffer;
    }

    public int getTotalOffers() {
        return mTotalOffers;
    }

    public ArrayList<ProductDetailsSpecification> getProductSpecifications() {
        return mProductSpecs;
    }

    public ArrayList<NewProductPartial> getRelatedProducts() {
        return mRelatedProducts;
    }

    public String getShareUrl() {
        return mShareUrl;
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
        dest.writeList(mImageList);
        dest.writeList(mVariations);
        dest.writeString(mDescription);
        dest.writeByte((byte) (hasSeller ? 1 : 0));
        dest.writeByte((byte) (hasBundle ? 1 : 0));
        dest.writeParcelable(mSeller, flags);
        dest.writeParcelable(mProductBundle, flags);
        dest.writeDouble(mMinPriceOfferDouble);
        dest.writeString(mMinPriceOffer);
        dest.writeDouble(mMinPriceOfferConverted);
        dest.writeInt(mTotalOffers);
        dest.writeList(mProductSpecs);
        dest.writeString(mShortDescription);
    }

    private NewProductComplete(Parcel in) {
        super(in);
        mImageList = new ArrayList<>();
        in.readList(mImageList, null);
        mVariations = new ArrayList<>();
        in.readList(mVariations, Variation.class.getClassLoader());
        mDescription = in.readString();
        hasSeller = in.readByte() == 1;
        hasBundle = in.readByte() == 1;
        mSeller = in.readParcelable(Seller.class.getClassLoader());
        mProductBundle = in.readParcelable(ProductBundle.class.getClassLoader());
        mMinPriceOfferDouble = in.readDouble();
        mMinPriceOffer = in.readString();
        mMinPriceOfferConverted = in.readDouble();
        mTotalOffers = in.readInt();
        mProductSpecs = new ArrayList<>();
        in.readList(mProductSpecs, ProductDetailsSpecification.class.getClassLoader());
        mShortDescription = in.readString();
    }

    public static final Parcelable.Creator<NewProductComplete> CREATOR = new Parcelable.Creator<NewProductComplete>() {
        public NewProductComplete createFromParcel(Parcel in) {
            return new NewProductComplete(in);
        }

        public NewProductComplete[] newArray(int size) {
            return new NewProductComplete[size];
        }
    };

}
