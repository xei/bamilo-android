package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.LogTagHelper;
import android.os.Parcel;
import android.os.Parcelable;
import de.akquinet.android.androlog.Log;

/**
 * Class that manages the full representation of a given product bundle.
 * 
 * @author Paulo Carvalho
 * 
 */
public class ProductBundleSimple implements IJSONSerializable, Parcelable {

    private static final String TAG = LogTagHelper.create(ProductBundleSimple.class);

    private String simpleSku;
    private String simpleQuantity;
    private String simpleSize;
    private String simplePrice;
    private double simplePriceDouble;
    private double simplePriceConverted;
    private int productParentPos;


    /**
     * Complete product bundle empty constructor.
     */
    public ProductBundleSimple() {

        simpleSku = "";
        simpleQuantity = "";
        simpleSize = "";
        simplePrice = "";
        simplePriceDouble = 0.0;
        simplePriceConverted = 0.0;
        productParentPos = 0;

    }

    public ProductBundleSimple(int parentPos, JSONObject jsonObject) {

        simpleSku = "";
        simpleQuantity = "";
        simpleSize = "";
        simplePrice = "";
        simplePriceDouble = 0.0;
        simplePriceConverted = 0.0;
        productParentPos = parentPos;
        initialize(jsonObject);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {

            simpleSku = jsonObject.getString(RestConstants.JSON_SKU_TAG);
            simpleQuantity = jsonObject.getString(RestConstants.JSON_QUANTITY_TAG);
            simpleSize = jsonObject.getString(RestConstants.JSON_SIZE_TAG);
            
            String priceJSON = jsonObject.getString(RestConstants.JSON_PRICE_TAG);
            
            if (!CurrencyFormatter.isNumber(priceJSON)) {
                throw new JSONException("Price is not a number!");
            }
            simplePriceDouble = Double.parseDouble(priceJSON);
            simplePrice = CurrencyFormatter.formatCurrency(priceJSON);
            
            simplePriceConverted = jsonObject.getDouble(RestConstants.JSON_PRICE_CONVERTED_TAG);

        } catch (JSONException e) {

            Log.e(TAG, "Error initializing the complete product", e);
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return simpleSize;
    }
    
    
    
    
    public String getSimpleSku() {
        return simpleSku;
    }

    public void setSimpleSku(String simpleSku) {
        this.simpleSku = simpleSku;
    }

    public String getSimpleQuantity() {
        return simpleQuantity;
    }

    public void setSimpleQuantity(String simpleQuantity) {
        this.simpleQuantity = simpleQuantity;
    }

    public String getSimpleSize() {
        return simpleSize;
    }

    public void setSimpleSize(String simpleSize) {
        this.simpleSize = simpleSize;
    }

    public String getSimplePrice() {
        return simplePrice;
    }

    public void setSimplePrice(String simplePrice) {
        this.simplePrice = simplePrice;
    }

    public double getSimplePriceDouble() {
        return simplePriceDouble;
    }

    public void setSimplePriceDouble(double simplePriceDouble) {
        this.simplePriceDouble = simplePriceDouble;
    }

    public double getSimplePriceConverted() {
        return simplePriceConverted;
    }

    public void setSimplePriceConverted(double simplePriceConverted) {
        this.simplePriceConverted = simplePriceConverted;
    }


    public int getProductParentPos() {
        return productParentPos;
    }

    public void setProductParentPos(int productParentPos) {
        this.productParentPos = productParentPos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    /*
     * ############ PARCELABLE ############
     */

    protected ProductBundleSimple(Parcel in) {
        simpleSku = in.readString();
        simpleQuantity = in.readString();
        simpleSize = in.readString();
        simplePrice = in.readString();
        simplePriceDouble = in.readDouble();
        simplePriceConverted = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(simpleSku);
        dest.writeString(simpleQuantity);
        dest.writeString(simpleSize);
        dest.writeString(simplePrice);
        dest.writeDouble(simplePriceDouble);
        dest.writeDouble(simplePriceConverted);

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ProductBundleSimple> CREATOR = new Parcelable.Creator<ProductBundleSimple>() {
        @Override
        public ProductBundleSimple createFromParcel(Parcel in) {
            return new ProductBundleSimple(in);
        }

        @Override
        public ProductBundleSimple[] newArray(int size) {
            return new ProductBundleSimple[size];
        }
    };

}
