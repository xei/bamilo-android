package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductSimpleUnique implements IJSONSerializable {
    private final String JSON_SKU_TAG = "sku";
    private final String JSON_QUANTITY_TAG = "quantity";
    private final String JSON_VARIATION_TAG = "variation";

    private String sku;
    private int quantity;
    private String variation;

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
            sku = jsonObject.getString(JSON_SKU_TAG);
            quantity = jsonObject.getInt(JSON_QUANTITY_TAG);
            variation = jsonObject.optString(JSON_VARIATION_TAG);

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSON_SKU_TAG, sku);
            jsonObject.put(JSON_QUANTITY_TAG, quantity);
            jsonObject.put(JSON_VARIATION_TAG, variation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * @return the sku
     */
    public String getSku() {
        return sku;
    }

    /**
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @return the variation
     */
    public String getVariation() {
        return variation;
    }
}
