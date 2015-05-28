package com.mobile.newFramework.objects.product;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.CurrencyFormatter;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.Offer;
import com.mobile.newFramework.objects.RequiredJson;

/**
 * Class that represents the product offers
 *
 * @author Paulo Carvalho
 *
 */
public class ProductOffers implements IJSONSerializable, Parcelable {

    private static final String TAG = LogTagHelper.create(ProductOffers.class);

    private ArrayList<Offer> offers;
    private double minPriceOfferDouble;
    private String minPriceOffer;
    private double minPriceOfferConverted;
    private int totalOffers;

    // private int simpleSkuPosition;

    /**
     * Complete product empty constructor.
     */
    public ProductOffers() {

        offers = new ArrayList<Offer>();
        minPriceOfferDouble = 0.0;
        minPriceOffer = "";
        minPriceOfferConverted = 0.0;
        totalOffers = 0;
    }

    public ProductOffers(JSONObject productOffers) {

        offers = new ArrayList<Offer>();
        minPriceOfferDouble = 0.0;
        minPriceOffer = "";
        minPriceOfferConverted = 0.0;
        totalOffers = 0;
        initialize(productOffers);
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

            String offerPriceJSON = jsonObject.optString(RestConstants.JSON_OFFERS_MIN_PRICE_TAG);

            if (!CurrencyFormatter.isNumber(offerPriceJSON)) {
                offerPriceJSON = "0";
            }
            minPriceOfferDouble = Double.parseDouble(offerPriceJSON);
//            minPriceOffer = CurrencyFormatter.formatCurrency(offerPriceJSON); TODO
            minPriceOfferConverted = jsonObject.optDouble(RestConstants.JSON_OFFERS_MIN_PRICE_CONVERTED_TAG, 0);
            totalOffers = jsonObject.optInt(RestConstants.JSON_TOTAL_TAG, 0);

            // Offers
            JSONArray offersArray = jsonObject.optJSONArray(RestConstants.JSON_DATA_TAG);

            for (int i = 0; i < offersArray.length(); i++) {
                JSONObject offerObject = offersArray.getJSONObject(i);
                Offer offer = new Offer(offerObject);
                offers.add(offer);
            }

        } catch (JSONException e) {

//            Log.e(TAG, "Error initializing the complete product", e);
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

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.OBJECT_DATA;
    }

    public ArrayList<Offer> getOffers() {
        return offers;
    }

    public void setOffers(ArrayList<Offer> offers) {
        this.offers = offers;
    }

    public double getPriceForTracking() {
        return minPriceOfferConverted;
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

        dest.writeList(offers);
        dest.writeDouble(minPriceOfferDouble);
        dest.writeString(minPriceOffer);
        dest.writeDouble(minPriceOfferConverted);
        dest.writeInt(totalOffers);
    }

    private ProductOffers(Parcel in) {

        offers = new ArrayList<Offer>();
        in.readList(offers, Offer.class.getClassLoader());

        minPriceOfferDouble = in.readDouble();
        minPriceOffer = in.readString();
        minPriceOfferConverted = in.readDouble();
        totalOffers = in.readInt();
    }

    public static final Parcelable.Creator<ProductOffers> CREATOR = new Parcelable.Creator<ProductOffers>() {
        public ProductOffers createFromParcel(Parcel in) {
            return new ProductOffers(in);
        }

        public ProductOffers[] newArray(int size) {
            return new ProductOffers[size];
        }
    };
}

