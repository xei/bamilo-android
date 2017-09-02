package com.mobile.service.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.service.forms.PaymentMethodForm;
import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.objects.product.RichRelevance;
import com.mobile.service.objects.product.pojo.ProductRegular;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.utils.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that represents the checkout finish step.
 */
public class CheckoutFinish implements IJSONSerializable, Parcelable {

    private PaymentMethodForm mPaymentMethodForm;
    private String mPaymentUrl;
    private String mOrderNumber;
    private RichRelevance mRichRelevance;
    private ArrayList<ProductRegular> mRelatedProducts;
    private String mOrdersCount;

    /**
     * Empty constructor
     */
    @SuppressWarnings("unused")
    public CheckoutFinish() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Get order number
        mOrderNumber = jsonObject.getString(RestConstants.ORDER_NR);
        // Get order count
        mOrdersCount = jsonObject.optString(RestConstants.ORDERS_COUNT);

        /**
         * TODO : Improve the parser for Related products and Recommended products.
         * It's always used the RichRelevance structure.
         */
        // Related products
        JSONArray relatedProductsJsonArray = jsonObject.optJSONArray(RestConstants.RELATED_PRODUCTS);
        if (relatedProductsJsonArray != null && CollectionUtils.isNotEmpty(relatedProductsJsonArray)) {
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

        // Validate payment content
        if (jsonObject.has(RestConstants.PAYMENT)) {
            // Form
            mPaymentMethodForm = new PaymentMethodForm();
            mPaymentMethodForm.initialize(jsonObject);
            // Order
            if (jsonObject.has(RestConstants.ORDER_NR)) {
                mOrderNumber = jsonObject.optString(RestConstants.ORDER_NR);
            }
            // Get url
            JSONObject jsonPayment = jsonObject.optJSONObject(RestConstants.PAYMENT);
            if (jsonPayment != null && jsonPayment.length() > 0) {
                String paymentUrl = jsonPayment.optString(RestConstants.URL);
                if (paymentUrl != null) {
                    mPaymentUrl = paymentUrl;
                }
            }
        }


        return true;
    }

    public String getOrderNumber() {
        return mOrderNumber;
    }

    public String getOrdersCount() {
        return mOrdersCount;
    }

    public PaymentMethodForm getPaymentMethodForm() {
        return mPaymentMethodForm;
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
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mOrderNumber);
        dest.writeString(mOrdersCount);
        dest.writeValue(mPaymentMethodForm);
        dest.writeString(mPaymentUrl);
        dest.writeList(mRelatedProducts);
        dest.writeParcelable(mRichRelevance, flags);
    }

    private CheckoutFinish(Parcel in) {
        mOrderNumber = in.readString();
        mOrdersCount = in.readString();
        mPaymentMethodForm = (PaymentMethodForm) in.readValue(PaymentMethodForm.class.getClassLoader());
        mPaymentUrl = in.readString();
        mRelatedProducts = new ArrayList<>();
        in.readList(mRelatedProducts, ProductRegular.class.getClassLoader());
        mRichRelevance = in.readParcelable(RichRelevance.class.getClassLoader());
    }

    public static final Creator<CheckoutFinish> CREATOR = new Creator<CheckoutFinish>() {
        public CheckoutFinish createFromParcel(Parcel in) {
            return new CheckoutFinish(in);
        }

        public CheckoutFinish[] newArray(int size) {
            return new CheckoutFinish[size];
        }
    };


}
