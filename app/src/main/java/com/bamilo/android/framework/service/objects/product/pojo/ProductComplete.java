package com.bamilo.android.framework.service.objects.product.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.objects.product.BundleList;
import com.bamilo.android.framework.service.objects.product.ImageUrls;
import com.bamilo.android.framework.service.objects.product.RichRelevance;
import com.bamilo.android.framework.service.objects.product.Seller;
import com.bamilo.android.framework.service.objects.product.Variation;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.TextUtils;

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
    private ArrayList<ImageUrls> mImageList;
    private BundleList mProductBundle;
    private boolean hasBundle;
    private Seller mSeller;
    private double mMinPriceOffer;
    private double mMinPriceOfferConverted;
    private boolean hasOffers;
    private int mTotalOffers;
    private ArrayList<ProductRegular> mRelatedProducts;
    private RichRelevance mRichRelevance;
    private ArrayList<ProductSpecification> mProductSpecs;
    private ArrayList<Variation> mProductVariations;
    private String mShareUrl;
    private boolean isFashion;
    private boolean isPreOrder;

    /**
     * Complete product empty constructor.
     */
    public ProductComplete() {
        super();
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
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
            //has offers
            hasOffers = false;
            // Share url
            mShareUrl = jsonObject.optString(RestConstants.SHARE_URL);
            // Bundle
            hasBundle = jsonObject.optBoolean(RestConstants.BUNDLE);
            // Images
            JSONArray imageArray = jsonObject.optJSONArray(RestConstants.IMAGE_LIST);
            if (imageArray != null && imageArray.length() > 0) {
                mImageList = new ArrayList<>();
                for (int i = 0; i < imageArray.length(); ++i) {
                    ImageUrls imageUrls = new ImageUrls();
                    imageUrls.initialize(imageArray.getJSONObject(i));
                    mImageList.add(imageUrls);
                }
            }
            // Seller
            JSONObject sellerObject = jsonObject.optJSONObject(RestConstants.SELLER_ENTITY);
            if (sellerObject != null) {
                mSeller = new Seller(sellerObject);
            }
            //Offers
            JSONObject offers = jsonObject.optJSONObject(RestConstants.OFFERS);
            if (offers != null) {
                mMinPriceOffer = offers.optDouble(RestConstants.MIN_PRICE);
                mMinPriceOfferConverted = offers.optDouble(RestConstants.MIN_PRICE_CONVERTED);
                mTotalOffers = offers.optInt(RestConstants.TOTAL);
                hasOffers = true;

            }
            // Related products
            JSONArray relatedProductsJsonArray = jsonObject.optJSONArray(RestConstants.RELATED_PRODUCTS);
            if (relatedProductsJsonArray != null && relatedProductsJsonArray.length() > 0) {
                mRelatedProducts = new ArrayList<>();
                for (int i = 0; i < relatedProductsJsonArray.length(); i++) {
                    ProductRegular relatedProduct = new ProductRegular();
                    if (relatedProduct.initialize(relatedProductsJsonArray.optJSONObject(i))) {
                        mRelatedProducts.add(relatedProduct);
                    }
                }
            }
            // Recommended products -> Rich Relevance
            JSONObject recommendedProductObject = jsonObject.optJSONObject(RestConstants.RECOMMENDED_PRODUCTS);
            if (recommendedProductObject != null) {
                mRichRelevance = new RichRelevance();
                mRichRelevance.initialize(recommendedProductObject);
            }
            // Summary
            JSONObject summaryObject = jsonObject.optJSONObject(RestConstants.SUMMARY);
            if (summaryObject != null) {
                mDescription = summaryObject.optString(RestConstants.DESCRIPTION);
                mShortDescription = summaryObject.optString(RestConstants.SHORT_DESCRIPTION);
            }
            // Specifications
            JSONArray specificationsArray = jsonObject.optJSONArray(RestConstants.SPECIFICATIONS);
            if (specificationsArray != null && specificationsArray.length() > 0) {
                mProductSpecs = new ArrayList<>();
                for (int i = 0; i < specificationsArray.length(); i++) {
                    ProductSpecification prodSpecs = new ProductSpecification();
                    prodSpecs.initialize(specificationsArray.getJSONObject(i));
                    mProductSpecs.add(prodSpecs);
                }
            }
            // Variations
            JSONArray variationsArray = jsonObject.optJSONArray(RestConstants.VARIATIONS);
            if (variationsArray != null && variationsArray.length() > 0) {
                mProductVariations = new ArrayList<>();
                for (int i = 0; i < variationsArray.length(); i++) {
                    Variation variation = new Variation();
                    variation.initialize(variationsArray.getJSONObject(i));
                    mProductVariations.add(variation);
                }
            }
            isPreOrder = Boolean.parseBoolean(jsonObject.optString(RestConstants.PRE_ORDER));

        } catch (JSONException e) {
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


    public ArrayList<ImageUrls> getImageList() {
        return mImageList;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getShortDescription() {
        return mShortDescription;
    }

    public boolean hasVariations() {
        return CollectionUtils.isNotEmpty(mProductVariations);
    }

    public BundleList getProductBundle() {
        return mProductBundle;
    }

    public void setProductBundle(BundleList mProductBundle) { this.mProductBundle = mProductBundle; }

    public boolean hasSeller() {
        return mSeller != null;
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

    public ArrayList<Variation> getProductVariations() {
        return mProductVariations;
    }

    public ArrayList<ProductRegular> getRelatedProducts() {
        // if Rich relevance is active and have products send those products
        if(mRichRelevance != null && CollectionUtils.isNotEmpty(mRichRelevance.getRichRelevanceProducts()))
            return mRichRelevance.getRichRelevanceProducts();
        else
            return mRelatedProducts;
    }

    public RichRelevance getRichRelevance() {
        return mRichRelevance;
    }

    public void setRichRelevance(RichRelevance richRelevance) {
        mRichRelevance = richRelevance;
    }

    public String getShareUrl() {
        return mShareUrl;
    }

    public boolean isFashion() {
        return isFashion;
    }

    public boolean hasOffers() { return hasOffers; }

    public double getMinPriceOffer() { return mMinPriceOffer;}

    public boolean isPreOrder() {
        return isPreOrder;
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
        dest.writeString(mDescription);
        dest.writeByte((byte) (hasBundle ? 1 : 0));
        dest.writeParcelable(mSeller, flags);
        dest.writeParcelable(mProductBundle, flags);
        dest.writeDouble(mMinPriceOffer);
        dest.writeDouble(mMinPriceOfferConverted);
        dest.writeInt(mTotalOffers);
        dest.writeList(mProductSpecs);
        dest.writeList(mProductVariations);
        dest.writeList(mRelatedProducts);
        dest.writeString(mShortDescription);
        dest.writeByte((byte) (isFashion ? 1 : 0));
        dest.writeByte((byte) (isPreOrder ? 1 : 0));
        dest.writeParcelable(mRichRelevance, flags);

    }

    private ProductComplete(Parcel in) {
        super(in);
        mImageList = new ArrayList<>();
        in.readList(mImageList, ImageUrls.class.getClassLoader());
        mDescription = in.readString();
        hasBundle = in.readByte() == 1;
        mSeller = in.readParcelable(Seller.class.getClassLoader());
        mProductBundle = in.readParcelable(BundleList.class.getClassLoader());
        mMinPriceOffer = in.readDouble();
        mMinPriceOfferConverted = in.readDouble();
        mTotalOffers = in.readInt();
        mProductSpecs = new ArrayList<>();
        in.readList(mProductSpecs, ProductSpecification.class.getClassLoader());
        mProductVariations = new ArrayList<>();
        in.readList(mProductVariations, Variation.class.getClassLoader());
        mRelatedProducts = new ArrayList<>();
        in.readList(mRelatedProducts, ProductRegular.class.getClassLoader());
        mShortDescription = in.readString();
        isFashion = in.readByte() == 1;
        isPreOrder = in.readByte() == 1;
        mRichRelevance = in.readParcelable(RichRelevance.class.getClassLoader());
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
