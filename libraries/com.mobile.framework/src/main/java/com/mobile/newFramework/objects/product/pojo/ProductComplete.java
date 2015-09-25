package com.mobile.newFramework.objects.product.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.product.BundleList;
import com.mobile.newFramework.objects.product.Seller;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that manages the full representation of a given product.
 *
 * @author sergiopereira
 */
public class ProductComplete extends ProductMultiple {

    private static final String TAG = ProductComplete.class.getSimpleName();

    private String mDescription;
    private String mShortDescription;
    private ArrayList<String> mImageList;
    private BundleList mProductBundle;
    private boolean hasSeller;
    private boolean hasBundle;
    private Seller mSeller;
    private double mMinPriceOffer;
    private double mMinPriceOfferConverted;
    private int mTotalOffers;
    private ArrayList<ProductRegular> mRelatedProducts;
    private ArrayList<ProductSpecification> mProductSpecs;
    private String mShareUrl;
    private boolean isFashion;
    private boolean hasVariations;

    /**
     * Complete product empty constructor.
     */
    public ProductComplete() {
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
            // Fashion PDV
            isFashion = TextUtils.equals(jsonObject.optString(RestConstants.VERTICAL), RestConstants.FASHION);
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
                    mImageList.add(imageJsonObject.getString(RestConstants.URL));
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
                mMinPriceOffer = offers.optDouble(RestConstants.JSON_OFFERS_MIN_PRICE_TAG);
                mMinPriceOfferConverted = offers.optDouble(RestConstants.JSON_OFFERS_MIN_PRICE_CONVERTED_TAG);
                mTotalOffers = offers.optInt(RestConstants.JSON_TOTAL_TAG);
            }
            // Related products
            JSONArray relatedProductsJsonArray = jsonObject.optJSONArray(RestConstants.JSON_RELATED_PRODUCTS);
            if (relatedProductsJsonArray != null && relatedProductsJsonArray.length() > 0) {
                mRelatedProducts = new ArrayList<>();
                for (int i = 0; i < relatedProductsJsonArray.length(); i++) {
                    ProductRegular relatedProduct = new ProductRegular();
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
                    ProductSpecification prodSpecs = new ProductSpecification();
                    prodSpecs.initialize(specificationsArray.getJSONObject(i));
                    mProductSpecs.add(prodSpecs);
                }
            }
            // Variations
            JSONObject variationsObject = jsonObject.optJSONObject(RestConstants.JSON_VARIATIONS_TAG);
            hasVariations = variationsObject != null && variationsObject.length() > 0;
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

    public String getShortDescription() {
        return mShortDescription;
    }

    public boolean hasVariations() {
        return hasVariations;
    }

    public BundleList getProductBundle() {
        return mProductBundle;
    }

    public void setProductBundle(BundleList mProductBundle) { this.mProductBundle = mProductBundle; }

    public boolean hasSeller() {
        return hasSeller;
    }

    public boolean hasBundle() {
        return hasBundle;
    }

    public Seller getSeller() {
        return mSeller;
    }

    public ArrayList<ProductSpecification> getProductSpecifications() {
        return mProductSpecs;
    }

    public ArrayList<ProductRegular> getRelatedProducts() {
        return mRelatedProducts;
    }

    public String getShareUrl() {
        return mShareUrl;
    }

    public boolean isFashion() {
        return isFashion;
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
        //dest.writeList(mVariations);
        dest.writeString(mDescription);
        dest.writeByte((byte) (hasSeller ? 1 : 0));
        dest.writeByte((byte) (hasBundle ? 1 : 0));
        dest.writeParcelable(mSeller, flags);
        dest.writeParcelable(mProductBundle, flags);
        dest.writeDouble(mMinPriceOffer);
        dest.writeDouble(mMinPriceOfferConverted);
        dest.writeInt(mTotalOffers);
        dest.writeList(mProductSpecs);
        dest.writeString(mShortDescription);
        dest.writeByte((byte) (isFashion ? 1 : 0));
        dest.writeByte((byte) (hasVariations ? 1 : 0));
    }

    private ProductComplete(Parcel in) {
        super(in);
        mImageList = new ArrayList<>();
        in.readList(mImageList, null);
        mDescription = in.readString();
        hasSeller = in.readByte() == 1;
        hasBundle = in.readByte() == 1;
        mSeller = in.readParcelable(Seller.class.getClassLoader());
        mProductBundle = in.readParcelable(BundleList.class.getClassLoader());
        mMinPriceOffer = in.readDouble();
        mMinPriceOfferConverted = in.readDouble();
        mTotalOffers = in.readInt();
        mProductSpecs = new ArrayList<>();
        in.readList(mProductSpecs, ProductSpecification.class.getClassLoader());
        mShortDescription = in.readString();
        isFashion = in.readByte() == 1;
        hasVariations = in.readByte() == 1;
    }

    public static final Parcelable.Creator<ProductComplete> CREATOR = new Parcelable.Creator<ProductComplete>() {
        public ProductComplete createFromParcel(Parcel in) {
            return new ProductComplete(in);
        }

        public ProductComplete[] newArray(int size) {
            return new ProductComplete[size];
        }
    };

}
