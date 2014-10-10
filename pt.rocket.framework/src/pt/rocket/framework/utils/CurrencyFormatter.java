package pt.rocket.framework.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

import pt.rocket.framework.Darwin;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import de.akquinet.android.androlog.Log;

/**
 * Static class responsible for formatting the currency.
 * @author GuilhermeSilva
 *
 */
public class CurrencyFormatter {
	private final static String TAG = LogTagHelper.create( CurrencyFormatter.class );
	
	public final static String EURO_CODE = "EUR";

    /**
     * Current currency of the shop.
     */
    private static String currencyCode;
    private static String currencyUnitPattern;
    private static Currency currency;
    private static NumberFormat formatter;
    private static String currencyThousandsDelim;
    private static int currencyFractionCount;
    private static String currencyFractionDelim;
    
    /**
     * Placement of the server 
     */
    private static Locale apiLocale;
    private static boolean initialized = false;
    private static boolean isLocaleAvailable;
    
    /**
     * Initializes the Currency formatter.
     * This function accesses the content data in order to access the currency language and country of the currency.
     */
    public static void initialize( Context context, String currCode) {
    	currencyCode = currCode;
        apiLocale = Locale.US;
        initialized = true;
        isLocaleAvailable = isLocaleAvailable( Locale.getDefault());
        loadCurrencyInformation( context, currCode );
        
        if ( TextUtils.isEmpty( currCode ))
        	throw new RuntimeException( "Currency code is empty or null - fix this" );
        
        
        if(currCode.equalsIgnoreCase("USH")){
        	currCode = "UGX";
        }
        currency = Currency.getInstance(currCode);
        Log.d( TAG, "currency: currency code = " + currency.getCurrencyCode() + " fraction dicits = " + currency.getDefaultFractionDigits());
        formatter = getNumberFormatter();
    }
    
    public static void loadCurrencyInformation( Context context, String currCode ) {
    	SharedPreferences sharedPrefs = context.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    	

    	currencyThousandsDelim = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_THOUSANDS_SEP, ",");
    	currencyFractionCount = sharedPrefs.getInt(Darwin.KEY_SELECTED_COUNTRY_NO_DECIMALS, 0);
    	currencyFractionDelim = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_DECIMALS_SEP, ".");
    	
    	currencyUnitPattern = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, ".");
    }
    
    
    
    public static void setCurrency( String currencyCode ) {
    	currency = Currency.getInstance(currencyCode);
    }
    
//    /**
//     * Formats the currency based on the pre-defined values.
//     * @param value
//     * @return the formatted string
//     */
//    public static String formatCurrency(double value) {
//    	if (!initialized) throw new RuntimeException("currency converter not initialized");
//    	
//    	// String result = String.valueOf(value);
//    	String result = new BigDecimal(value).toString();
//
//    	String noFraction;
//    	String fraction;
//    	if(result.contains(".")){
//    		StringTokenizer tokens = new StringTokenizer(result, ".");
//    		noFraction = tokens.nextToken();
//    		fraction = tokens.nextToken();
//    	} else {
//    		noFraction = result;
//    		fraction =null;
//    	}
//    	
//    	if(noFraction.length()>3){
//	    	String thousands = noFraction.substring(noFraction.length()-3, noFraction.length());
//	    	String other = noFraction.substring(0, noFraction.length()-3);
//	    
//	    	if(currencyFractionCount == 0)
//	    		result = other+currencyThousandsDelim+thousands;
//	    	else { 
//	    		
//	    		while(fraction.length()<currencyFractionCount){
//	    			fraction+="0";
//	    		}
//	    		result = other+currencyThousandsDelim+thousands+currencyFractionDelim+fraction;
//	    	}
//    	} else {
//    		
//    		if(currencyFractionCount == 0)
//    			result = noFraction;
//	    	else { 
//	    		while(fraction.length()<currencyFractionCount){
//	    			fraction+="0";
//	    		}
//	    		
//	    		result = noFraction+currencyFractionDelim+fraction;
//	    	}
//    		
//    	}
//
//    	result = String.format(currencyUnitPattern, result);
//    	
//    	return result;
//    }
    
    /**
     * This Function restrieves the double value of a previously formatted value with currency and locale options.
     * @param value
     * @return
     */
    public static double getValueDouble(String value ) {
    	double result = 0;
    	if(currencyFractionCount == 0)
    		value = value.replace(",", "");
    	else { 
    		if(value.contains(",") && (""+currencyFractionDelim).equalsIgnoreCase(",") && !value.contains(".")){
    			value = value.replace(",", ".");
    		} else if(value.contains(",") && (""+currencyFractionDelim).equalsIgnoreCase(",") && value.contains(".")) {
    			value = value.replace(".", "");
    			value = value.replace(",", ".");
    		} else if(value.contains(",")){
    			value = value.replace(",", "");
    		}
    	}
    	try {
    		result = Double.parseDouble(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return result;
    }
    
    /**
     * This Function restrieves the double value of a previously formatted value with currency and locale options.
     * @param value
     * @return
     */
    public static String getCleanValue(String value ) {
    	value = value.trim();
    	String result = "";
    	if(currencyFractionCount == 0)
    		value = value.replace(",", "");
    	else { 
    		if(value.contains(",") && (""+currencyFractionDelim).equalsIgnoreCase(",") && !value.contains(".")){
    			value = value.replace(",", ".");
    		} else if(value.contains(",") && (""+currencyFractionDelim).equalsIgnoreCase(",") && value.contains(".")) {
    			value = value.replace(".", "");
    			value = value.replace(",", ".");
    		} else if(value.contains(",")){
    			value = value.replace(",", "");
    		}
    	}
    	result = value;
    	return result;
    }
    
    /**
     * Formats a string containing a numeric value into the proper formatted
     * currency of the country in question.
     * 
     * @param value
     * @return the formatted string
     */
    public static String formatCurrency(String value) {
        if (!initialized) throw new RuntimeException("currency converter not initialized");
    	
        try {
            
            NumberFormat format = NumberFormat.getInstance(apiLocale);
            Number number = format.parse(value);

            Double valueDouble = number.doubleValue();            
        	return String.format( currencyUnitPattern, formatter.format(valueDouble));

        
        } catch (NumberFormatException e) {
            //In case of bad formatting, return the parsed value with no crrency sign
        	Log.e( TAG, "bad formatting for value = " + value, e );
            return value + "";
        } catch (ParseException e) {
        	Log.d( TAG, "parse exception: cannot parse value = " + value, e );
            return value + "";
        }
    }
    
    private static NumberFormat getNumberFormatter() {
        if (!initialized) throw new RuntimeException("currency converter not initialized");
    	
    	NumberFormat currencyFormat;
    	if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
    		currencyFormat = createNumberFormatter();
    	} else {
    		currencyFormat = DecimalFormat.getCurrencyInstance();
    	}
    	
        currencyFormat.setMaximumFractionDigits( currencyFractionCount );
        currencyFormat.setMinimumFractionDigits(currencyFractionCount);
        currencyFormat.setGroupingUsed(true);
        DecimalFormatSymbols dfs = ((DecimalFormat) currencyFormat).getDecimalFormatSymbols();
        dfs.setCurrencySymbol( "" );
        dfs.setGroupingSeparator(currencyThousandsDelim.charAt(0));
        // dfs.setGroupingSeparator(',');
        if ( currencyFractionDelim != null) {
        	dfs.setMonetaryDecimalSeparator(currencyFractionDelim.charAt(0));
        }
        ((DecimalFormat)currencyFormat).setDecimalFormatSymbols( dfs );
    	        
        return currencyFormat;
    }
    
	private static NumberFormat createNumberFormatter() {
		Log.d( TAG, "createNumberFormatter for android 2.x");
		NumberFormat formatter;
		Locale requiredLocale = Locale.getDefault();
		Locale formatterLocale;
		if (!isLocaleAvailable) {
			Log.d( TAG, "required locale " + requiredLocale.getCountry() + " is not available - fallback to backup");
			if (requiredLocale.getCountry().equals("ID") || requiredLocale.getCountry().equals("PH")) {
				formatterLocale = Locale.US;
			} else {
				formatterLocale = Locale.GERMAN;
			}
		} else {
			formatterLocale = Locale.getDefault();
		}
		formatter = DecimalFormat.getCurrencyInstance(formatterLocale);

		return formatter;
	}

	public static String getCurrencyCode() {
    	return currencyCode;
    }
    
    public static boolean isLocaleAvailable( Locale localeRequired) {
    	Locale[] availableLocales = DecimalFormat.getAvailableLocales();
    	// Log.d( TAG, "isLocaleAvailable country = " + localeRequired + " available locale number = " + availableLocales.length);
    	for( Locale loc: availableLocales) {
			// Log.d( TAG, "localeRequired = " + localeRequired.getCountry() + " crrLocale = " + loc.getCountry());
    		if ( loc.getCountry().equalsIgnoreCase( localeRequired.getCountry())) {
    			return true;
    		}
    	}
    	
    	return false;
    }

    /**
     * Test if text is number
     * @param text
     * @see http://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-a-numeric-type-in-java?answertab=active#tab-top
     * @return true or false
     */
    public static boolean isNumber(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
