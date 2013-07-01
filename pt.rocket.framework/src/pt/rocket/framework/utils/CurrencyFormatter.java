package pt.rocket.framework.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

import pt.rocket.framework.R;
import android.content.Context;
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

    /**
     * Current currency of the shop.
     */
    private static String currencyCode;
    private static String currencyUnitPattern;
    private static Currency currency;
    private static NumberFormat formatter;
    private static Character currencyThousandsDelim;
    private static Integer currencyFractionCount;
    private static Character currencyFractionDelim;
    
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
        
        currency = Currency.getInstance(currCode);
        Log.d( TAG, "currency: currency code = " + currency.getCurrencyCode() + " fraction dicits = " + currency.getDefaultFractionDigits());
        formatter = getNumberFormatter();
    }
    
    public static void loadCurrencyInformation( Context context, String currCode ) {
    	String codes[] = context.getResources().getStringArray( R.array.formatter_currency_codes );
    	int i;
    	for( i = 0; i < codes.length; i++ ) {
    		if ( codes[i].equals( currCode))
    			break;
    	}

    	currencyThousandsDelim = context.getResources().getStringArray(R.array.formatter_currency_thousands_delim )[i].charAt(0);
    	currencyFractionCount = Integer.parseInt( context.getResources().getStringArray(R.array.formatter_currency_fraction_count)[i]);
    	
    	String fractionDelim = context.getResources().getStringArray(R.array.formatter_currency_fraction_delim)[i];
    	if ( !TextUtils.isEmpty(fractionDelim)) {
    		currencyFractionDelim = fractionDelim.charAt(0);
    	} else {
    		currencyFractionDelim = null;
    	}
    	
    	if ( i > codes.length ) {
    		throw new RuntimeException( "No currency information with corresponding unit pattern for this currency code found - fix this" );
    	}
    	
    	currencyUnitPattern = context.getResources().getStringArray(R.array.formatter_currency_unit_pattern)[i];
    }
    
    public static void setCurrency( String currencyCode ) {
    	currency = Currency.getInstance(currencyCode);
    }
    
    /**
     * Formats the currency based on the pre-defined values.
     * @param value
     * @return the formatted string
     */
    public static String formatCurrency(double value) {
    	return String.format( currencyUnitPattern, formatter.format(value));
    }
    
    /**
     * Formats a string containing a numeric value into the proper formatted currency of the country in question.
     * @param value
     * @return the formatted string
     */
    public static String formatCurrency(String value) {
        if ( !initialized )
        	throw new RuntimeException("currency converter not initialized" );
    	
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
    	if ( !initialized )
        	throw new RuntimeException("currency converter not initialized" );
    	
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
        dfs.setGroupingSeparator(currencyThousandsDelim);
        // dfs.setGroupingSeparator(',');
        if ( currencyFractionDelim != null) {
        	dfs.setMonetaryDecimalSeparator(currencyFractionDelim);
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
}
