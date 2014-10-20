/**
 * 
 */
package pt.rocket.framework.objects;

/**
 * Container class to bundle input parameters to a conversion
 */
public class CurrencyConversionInput {

    private Double value;
    private String currencyCode;
    private String targetCurrencyCode;
    private String key;

    /**
     * Convenience constructor to initialize the conversion input container
     * @param value Input value to be converted
     * @param currencyCode Origin currency code from which the value is converted
     * @param targetCurrencyCode Target currency code to which the value is converted
     */
    public CurrencyConversionInput(Double value, String currencyCode, String targetCurrencyCode) {
        this(null, value, currencyCode, targetCurrencyCode);
    }

    /**
     * Convenience constructor to initialize the conversion input container
     * @param value Input value to be converted
     * @param currencyCode Origin currency code from which the value is converted
     * @param targetCurrencyCode Target currency code to which the value is converted
     */
    public CurrencyConversionInput(String key, Double value, String currencyCode, String targetCurrencyCode) {
        this.value = value;
        this.currencyCode = currencyCode;
        this.targetCurrencyCode = targetCurrencyCode;
        this.key = key;
    }
    
    
    /**
     * @return Conversion input value to be converted
     */
    public Double getValue() {
        return value;
    }

    /**
     * @return Origin currency code from which value is converted
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * @return Target currency code to which value is converted
     */
    public String getTargetCurrencyCode() {
        return targetCurrencyCode;
    }
    
    /**
     * @return Key to be used for this value
     */
    public String getKey() {
        return key;
    }
    
}
