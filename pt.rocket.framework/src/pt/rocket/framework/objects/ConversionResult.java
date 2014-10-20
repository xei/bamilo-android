/**
 * 
 */
package pt.rocket.framework.objects;

/**
 * Conversion result class to carry the outputs of a conversion operation
 */
public class ConversionResult {

    private Error error;
    private Double value;
    private String key;

    /**
     * Overridden constructor to restrict to convenience constructor
     */
    private ConversionResult() {
    }

    /**
     * Convenience constructor taking a value and an optional error
     * @param value Output value from conversion
     * @param error Optional error occurred during conversion
     */
    public ConversionResult(Double value, Error error) {
        this(null, value, error);
    }

    /**
     * Convenience constructor taking a value and an optional error
     * @param value Output value from conversion
     * @param error Optional error occurred during conversion
     */
    public ConversionResult(String key, Double value, Error error) {
        this();
        this.value = value;
        this.error = error;
        this.key = key;
    }
    
    
    public Error getError() {
        return error;
    }

    public Double getValue() {
        return value;
    }
    
    public String getKey() {
        return key;
    }
}
