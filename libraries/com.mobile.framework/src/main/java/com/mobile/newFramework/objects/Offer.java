package com.mobile.newFramework.objects;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.utils.CurrencyFormatter;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the offer
 *
 * @author Paulo Carvalho
 *
 */
public class Offer implements IJSONSerializable, Parcelable{

    private static final String TAG = LogTagHelper.create(Offer.class);

    private String sku;
    private String simpleSku;
    private String priceOffer;
    private double priceOfferDouble;
    private double priceOfferConverted;
    private String specialPriceOffer;
    private double specialPriceOfferDouble;
    private double specialPriceOfferConverted;
    private int maxDeliveryTime;
    private int minDeliveryTime;
    private Seller seller;

    /**
     * Complete product empty constructor.
     */
    public Offer() {

        sku ="";
        simpleSku = "";
        priceOffer = "";
        priceOfferDouble = 0.0;
        priceOfferConverted = 0.0;
        specialPriceOffer = "";
        specialPriceOfferDouble = 0.0;
        specialPriceOfferConverted = 0.0;
        maxDeliveryTime = 0;
        minDeliveryTime = 0;
        seller = new Seller();
    }

    /**
     * Complete product empty constructor.
     */
    public Offer(JSONObject offer) {

        sku ="";
        simpleSku = "";
        priceOffer = "";
        priceOfferDouble = 0.0;
        priceOfferConverted = 0.0;
        specialPriceOffer = "";
        specialPriceOfferDouble = 0.0;
        specialPriceOfferConverted = 0.0;
        maxDeliveryTime = 0;
        minDeliveryTime = 0;
        seller = new Seller();
        initialize(offer);
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

            JSONObject sellerObject = jsonObject.getJSONObject(RestConstants.JSON_SELLER_TAG);

            if(sellerObject != null)
                seller = new Seller(sellerObject);

            JSONObject productObject = jsonObject.getJSONObject(RestConstants.JSON_PRODUCT_TAG);

            if(productObject != null){

                sku = productObject.optString(RestConstants.JSON_SKU_TAG);

                String offerPriceJSON = productObject.optString(RestConstants.JSON_PRICE_TAG);

                if (!CurrencyFormatter.isNumber(offerPriceJSON)) {
                    offerPriceJSON = "0";
                }
                priceOfferDouble = Double.parseDouble(offerPriceJSON);
//                priceOffer = CurrencyFormatter.formatCurrency(offerPriceJSON); TODO

                priceOfferConverted = productObject.optDouble(RestConstants.JSON_PRICE_CONVERTED_TAG,0.0);

                String specialOfferPriceJSON = productObject.optString(RestConstants.JSON_SPECIAL_PRICE_TAG);

                if (!CurrencyFormatter.isNumber(specialOfferPriceJSON)) {
                    specialOfferPriceJSON = "0.0";
                }
                specialPriceOfferDouble = Double.parseDouble(specialOfferPriceJSON);
//                specialPriceOffer = CurrencyFormatter.formatCurrency(specialOfferPriceJSON); TODO

                specialPriceOfferConverted = productObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_CONVERTED_TAG,0.0);

                JSONArray simplesArray = productObject.optJSONArray(RestConstants.JSON_SIMPLES_TAG);

                minDeliveryTime = productObject.optInt(RestConstants.JSON_SELLER_MIN_DELIVERY_TAG);
                maxDeliveryTime = productObject.optInt(RestConstants.JSON_SELLER_MAX_DELIVERY_TAG);

                //Simple array it's only supposed to have one simple, is a "buy now" kind of product
                if(simplesArray != null && simplesArray.length() > 0){
                    JSONObject simpleObject = simplesArray.getJSONObject(0);
                    simpleSku = simpleObject.optString(RestConstants.JSON_SKU_TAG);

                }

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
        return null;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSimpleSku() {
        return simpleSku;
    }

    public void setSimpleSku(String simpleSku) {
        this.simpleSku = simpleSku;
    }

    public String getPriceOffer() {
        return priceOffer;
    }

    public void setPriceOffer(String priceOffer) {
        this.priceOffer = priceOffer;
    }

    public double getPriceOfferDouble() {
        return priceOfferDouble;
    }

    public void setPriceOfferDouble(double priceOfferDouble) {
        this.priceOfferDouble = priceOfferDouble;
    }

    public double getPriceOfferConverted() {
        return priceOfferConverted;
    }

    public void setPriceOfferConverted(double priceOfferConverted) {
        this.priceOfferConverted = priceOfferConverted;
    }

    public int getMaxDeliveryTime() {
        return maxDeliveryTime;
    }

    public void setMaxDeliveryTime(int maxDeliveryTime) {
        this.maxDeliveryTime = maxDeliveryTime;
    }

    public int getMinDeliveryTime() {
        return minDeliveryTime;
    }

    public void setMinDeliveryTime(int minDeliveryTime) {
        this.minDeliveryTime = minDeliveryTime;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public double getPriceForTracking() {
        if(specialPriceOfferDouble == 0.0){
            return priceOfferDouble;
        } else {
            return specialPriceOfferDouble;
        }
    }



    /*
     * ############ PARCELABLE ############
     */


    protected Offer(Parcel in) {
        sku = in.readString();
        simpleSku = in.readString();
        priceOffer = in.readString();
        priceOfferDouble = in.readDouble();
        priceOfferConverted = in.readDouble();
        specialPriceOffer = in.readString();
        specialPriceOfferDouble = in.readDouble();
        specialPriceOfferConverted = in.readDouble();
        maxDeliveryTime = in.readInt();
        minDeliveryTime = in.readInt();
        seller = (Seller) in.readValue(Seller.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sku);
        dest.writeString(simpleSku);
        dest.writeString(priceOffer);
        dest.writeDouble(priceOfferDouble);
        dest.writeDouble(priceOfferConverted);
        dest.writeString(specialPriceOffer);
        dest.writeDouble(specialPriceOfferDouble);
        dest.writeDouble(specialPriceOfferConverted);
        dest.writeInt(maxDeliveryTime);
        dest.writeInt(minDeliveryTime);
        dest.writeValue(seller);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Offer> CREATOR = new Parcelable.Creator<Offer>() {
        @Override
        public Offer createFromParcel(Parcel in) {
            return new Offer(in);
        }

        @Override
        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };


}
