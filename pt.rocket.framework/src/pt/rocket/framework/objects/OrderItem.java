package pt.rocket.framework.objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.LogTagHelper;

/**
 * 
 * @author Paulo Carvalho
 *
 */
public class OrderItem implements IJSONSerializable, Parcelable {

	public final static String TAG = LogTagHelper.create(OrderItem.class);

	private int mProductQuantity;

	private double mProductUnitPrice;

	private String mProductSku;
	
	private String mProductSimpleSku;
	
	private double mProductTotal;
	
	private String mProductName;
	
	/**
	 * OrderItem empty constructor.
	 */
	public OrderItem() {
	    mProductQuantity = 0;
	    mProductUnitPrice = 0.0d;
	    mProductSku = "";
	    mProductSimpleSku = "";
	    mProductTotal = 0.0;
	    mProductName = "";
	}

	/**
	 * OrderItem empty constructor.
	 * 
	 * @throws JSONException
	 */
	public OrderItem(JSONObject jsonObject) throws JSONException {
	    mProductQuantity = 0;
	    mProductUnitPrice = 0.0d;
	    mProductSku = "";
	    mProductSimpleSku = "";
	    mProductTotal = 0.0;
	    mProductName = "";
		initialize(jsonObject);
	}


	public int getmProductQuantity() {
        return mProductQuantity;
    }

    public void setmProductQuantity(int mProductQuantity) {
        this.mProductQuantity = mProductQuantity;
    }

    public double getmProductUnitPrice() {
        return mProductUnitPrice;
    }

    public void setmProductUnitPrice(double mProductUnitPrice) {
        this.mProductUnitPrice = mProductUnitPrice;
    }

    public String getmProductSku() {
        return mProductSku;
    }

    public void setmProductSku(String mProductSku) {
        this.mProductSku = mProductSku;
    }

    public String getmProductSimpleSku() {
        return mProductSimpleSku;
    }

    public void setmProductSimpleSku(String mProductSimpleSku) {
        this.mProductSimpleSku = mProductSimpleSku;
    }

    public double getmProductTotal() {
        return mProductTotal;
    }
    
    public String getmProductTotalString() {
        String total = "";
        
        total = String.valueOf(mProductTotal);
        return total;
    }

    public void setmProductTotal(double mProductTotal) {
        this.mProductTotal = mProductTotal;
    }

    public String getmProductName() {
        return mProductName;
    }

    public void setmProductName(String mProductName) {
        this.mProductName = mProductName;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
	 * )
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {

	    mProductQuantity = jsonObject.optInt(RestConstants.JSON_QUANTITY_TAG);
	    mProductUnitPrice = jsonObject.optDouble(RestConstants.JSON_ORDER_UNIT_PRICE_TAG);
	    mProductSku = jsonObject.optString(RestConstants.JSON_ORDER_CONF_SKU_TAG);
	    mProductSimpleSku = jsonObject.optString(RestConstants.JSON_SKU_TAG);
	    mProductTotal = jsonObject.optDouble(RestConstants.JSON_ORDER_TOTAL_TAG);
	    mProductName = jsonObject.optString(RestConstants.JSON_NAME_TAG);

		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		// TODO
		return null;
	}
	
    protected OrderItem(Parcel in) {
        mProductQuantity = in.readInt();
        mProductUnitPrice = in.readDouble();
        mProductSku = in.readString();
        mProductSimpleSku = in.readString();
        mProductTotal = in.readDouble();
        mProductName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mProductQuantity);
        dest.writeDouble(mProductUnitPrice);
        dest.writeString(mProductSku);
        dest.writeString(mProductSimpleSku);
        dest.writeDouble(mProductTotal);
        dest.writeString(mProductName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<OrderItem> CREATOR = new Parcelable.Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

}