package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.CurrencyFormatter;

import java.util.HashMap;
import java.util.Map;

/**
 * Shopping Cart Item used when an item is added to the shopping cart
 * 
 * @author GuilhermeSilva
 * 
 */
public class ShoppingCartItem implements IJSONSerializable {
//    private static final String JSON_IMAGE_TAG = "image";
//    private static final String JSON_PRODUCT_URL_TAG = "url";
//    private static final String JSON_CONFIG_SKU_TAG = "configSku";    
//    private static final String JSON_QUANTITY_TAG = "quantity";
//    private static final String JSON_CONFIG_ID = "configId";
//    private static final String JSON_NAME_TAG = "name";
//    private static final String JSON_STOCK_TAG = "stock";
//    private static final String JSON_SPECIAL_PRICE_TAG = "specialPrice";
//    private static final String JSON_PRICE_TAG = "unit_price";
//    private static final String JSON_TAX_AMOUNT_TAG = "tax_amount";
//    private static final String JSON_MAX_QUANTITY = "max_quantity";
//    private static final String JSON_VARIATION = "variation";
//
    // TODO: implement these tags
//    private static final String JSON_CART_RULE_DISPLAY_NAMES = "cart_rule_display_names";
//    private static final String JSON_SALES_ORDER_ITEM = "salesOrderItem";
//    private static final String JSON_CART_RULE_DISCOUNT = "cart_rule_discount";

    
    private String imageUrl;
    private String productUrl;
    private String configSKU;
    private String configSimpleSKU;
    private int quantity;
    private int maxQuantity;
    private String configId;
    private String name;
    private int stock;
    private String specialPrice;
    private Double savingPercentage;
    private String price;
    private double taxAmount;
    private Map<String, String> simpleData;
    private double cartRuleDiscount;
    private String variation;

    private double priceVal = 0;
    private double specialPriceVal = 0;    
    
    /**
     * @param simpleData registry
     */
    public ShoppingCartItem(HashMap<String, String> simpleData) {
        configSimpleSKU = null;
        this.simpleData = simpleData;
    }

    /**
     * @param configSimpleSKU of the product simple
     * @param simpleData registry
     */
    public ShoppingCartItem(String configSimpleSKU, HashMap<String, String> simpleData) {
        this.configSimpleSKU = configSimpleSKU;
        this.simpleData = simpleData;
    }

    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        priceVal = 0;
        specialPriceVal = 0;
        
        try {
            imageUrl = jsonObject.getString(RestConstants.JSON_ITEM_IMAGE_TAG);
            productUrl = jsonObject.optString(RestConstants.JSON_PRODUCT_URL_TAG);
            configSKU = jsonObject.getString(RestConstants.JSON_CONFIG_SKU_TAG);
            quantity = jsonObject.getInt(RestConstants.JSON_QUANTITY_TAG);
            maxQuantity = jsonObject.getInt(RestConstants.JSON_MAX_QUANTITY );
            configId = jsonObject.getString(RestConstants.JSON_CONFIG_ID);
            name = jsonObject.getString(RestConstants.JSON_ITEM_NAME_TAG);            
            stock = Integer.parseInt(jsonObject.getString(RestConstants.JSON_STOCK_TAG));

            
            if (!jsonObject.isNull(RestConstants.JSON_ITEM_PRICE_TAG)) {
                priceVal = jsonObject.getDouble(RestConstants.JSON_ITEM_PRICE_TAG);                
            }
            price = CurrencyFormatter.formatCurrency(priceVal);
            
            if (!jsonObject.isNull(RestConstants.JSON_ITEM_SPECIAL_PRICE_TAG) && jsonObject.getDouble(RestConstants.JSON_ITEM_SPECIAL_PRICE_TAG) > 0 ) {
                specialPriceVal = jsonObject.getDouble(RestConstants.JSON_ITEM_SPECIAL_PRICE_TAG);                
            }
            else {
                specialPriceVal = priceVal;
//                specialPrice = CurrencyFormatter.formatCurrency(0.0);
            }
            specialPrice = CurrencyFormatter.formatCurrency(specialPriceVal);
            
            taxAmount = jsonObject.optDouble(RestConstants.JSON_TAX_AMOUNT_TAG, 0);
            savingPercentage = 100 - specialPriceVal / priceVal * 100;
            
            // TODO: find out what these fields are for
            // jsonObject.getJSONArray(JSON_CART_RULE_DISPLAY_NAMES);            
            // jsonObject.getJSONObject(JSON_SALES_ORDER_ITEM);
            cartRuleDiscount = jsonObject.getDouble(RestConstants.JSON_CART_RULE_DISCOUNT );
            variation =  jsonObject.getString(RestConstants.JSON_VARIATION);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Initializes the shopping cart member variables
     * 
     * @param productSKU
     * @param simpleProductSKU
     * @param imageURL
     * @param productURL
     * @param productName
     * @param stock
     * @param specialPrice
     * @param price
     * @param quantity
     */
    public void initialize(String productSKU, String simpleProductSKU, String imageURL, String productURL, String productName, int stock, String specialPrice,
            String price, int quantity) {

        this.imageUrl = imageURL;
        this.productUrl = productURL;
        this.configSKU = productSKU;
        this.configSimpleSKU = simpleProductSKU;
        this.quantity = quantity;
        this.configId = "";
        this.name = productName;
        this.stock = stock;
        this.specialPrice = specialPrice;
        this.price = price;
    }

    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    /**
     * @return the imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * @return the productUrl
     */
    public String getProductUrl() {
        return productUrl;
    }

    /**
     * @return The product SKU (short form)
     */
    public String getConfigSKU() {
        return configSKU;
    }

    /**
     * @return The configuration/variant SKU of the product
     */
    public String getConfigSimpleSKU() {
        return configSimpleSKU;
    }
    
    /*
     * @param quantity of the product
     */
    public void setQuantity( int quantity ) {
    	this.quantity = quantity;
    }

    /**
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }
    
    /*
     * @param max quantity of the product
     */
    public void setMaxQuantity( int maxQuantity ) {
    	this.maxQuantity = maxQuantity;
    }
    
    /**
     * @return the maxQuantity
     */
    public int getMaxQuantity() {
    	return maxQuantity;
    }

    /**
     * @return the configId
     */
    public String getConfigId() {
        return configId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the stock
     */
    public int getStock() {
        return stock;
    }

    /**
     * @return the special price
     */
    public String getSpecialPrice() {
        return specialPrice;
    }

    /**
     * @return the special price
     */
    public Double getSpecialPriceVal() {
        return specialPriceVal;
    }
    
    
    /**
     * @return the price
     */
    public String getPrice() {
        return price;
    }

    /**
     * @return the price
     */
    public Double getPriceVal() {
        return priceVal;
    }
    
    /**
     * @return the taxAmount
     */
    public double getTaxAmount() {
        return taxAmount;
    }
    /** 
     * @return the savingPercentage
     */
    public double getSavingPercentage() {
        return savingPercentage;
    }
    
    public double getCartRuleDiscount() {
    	return cartRuleDiscount;
    }
    
    public void setCartRuleDiscount( double cartRuleDiscount ) {
    	this.cartRuleDiscount = cartRuleDiscount;
    }

    /**
     * @return the simpleData
     */
    public Map<String, String> getSimpleData() {
        return simpleData;
    }

    /**
     * @param simpleData the simpleData to set
     */
    public void setSimpleData(Map<String, String> simpleData) {
        this.simpleData = simpleData;
    }

	public String getVariation() {
		return variation;
	}

	public void setVariation(String variation) {
		this.variation = variation;
	}


    
    
}
