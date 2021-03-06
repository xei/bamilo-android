package com.bamilo.android.framework.service.utils.shop;

import android.content.Context;
import android.content.SharedPreferences;

import com.bamilo.android.framework.service.Darwin;
import com.bamilo.android.framework.service.utils.TextUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

/**
 * Static class responsible for formatting the currency.
 *
 * @author GuilhermeSilva
 */
public class CurrencyFormatter {

    private final static String TAG = CurrencyFormatter.class.getSimpleName();

    public final static String EURO_CODE = "EUR";

    /**
     * Current currency of the shop.
     */
    private static String currencyCode;
    private static String currencyUnitPattern;
    private static NumberFormat formatter;
    private static String currencyThousandsDelim;
    private static int currencyFractionCount;
    private static String currencyFractionDelim;

    /**
     * Placement of the server
     */
    private static Locale apiLocale;
    private static boolean initialized = false;

    /**
     * Initializes the Currency formatter. This function accesses the content data in order to
     * access the currency language and country of the currency.
     */
    public static void initialize(Context context, String currCode) {
        currencyCode = currCode;
        apiLocale = Locale.US;
        initialized = true;
        // Load currency configurations
        loadCurrencyInformation(context);

        if (TextUtils.isEmpty(currCode)) {
            throw new RuntimeException("Currency code is empty or null - fix this");
        }

        // Currency fallbacks
        // Case Uganda (Jumia)
        if (currCode.equalsIgnoreCase("USH")) {
            currCode = "UGX";
        }
        // Case Iran (Bamilo)
        else if (currCode.equalsIgnoreCase("ريال")) {
            currCode = "IRR";
        }

        Currency currency = Currency.getInstance(currCode);
        formatter = getNumberFormatter();

    }

    /**
     * Load currency info from country configurations.
     */
    public static void loadCurrencyInformation(Context context) {
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        currencyThousandsDelim = sharedPrefs
                .getString(Darwin.KEY_SELECTED_COUNTRY_THOUSANDS_STEP, ",");
        currencyFractionCount = sharedPrefs.getInt(Darwin.KEY_SELECTED_COUNTRY_NO_DECIMALS, 0);
        currencyFractionDelim = sharedPrefs
                .getString(Darwin.KEY_SELECTED_COUNTRY_DECIMALS_STEP, ".");
        currencyUnitPattern = sharedPrefs
                .getString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, ".");
    }

    public static String formatCurrency(double value) {
        if (!initialized) {
            throw new RuntimeException("Currency converter not initialized");
        }
        try {
            return formatCurrencyPattern(formatter.format(value));
        } catch (NumberFormatException e) {
            //In case of bad formatting, return the parsed value with no currency sign
            return value + "";
        }
    }

    public static String formatCurrency(String value, boolean showCurrency) {
        if (!showCurrency) {
            try {
                NumberFormat format = NumberFormat.getInstance(apiLocale);
                Number number = format.parse(value);
                Double valueDouble = number.doubleValue();

                return formatter.format(valueDouble);

            } catch (Exception e) {
                return value;
            }
        }

        return formatCurrency(value);
    }

    /**
     * Formats a string containing a numeric value into the proper formatted currency of the country
     * in question.
     *
     * @return the formatted string
     */
    public static String formatCurrency(String value) {
        if (!initialized) {
            throw new RuntimeException("Currency converter not initialized");
        }
        if (!TextUtils.isEmpty(value)) {
            try {
                NumberFormat format = NumberFormat.getInstance(apiLocale);
                Number number = format.parse(value);
                Double valueDouble = number.doubleValue();
                return formatCurrencyPattern(formatter.format(valueDouble));
            } catch (NumberFormatException e) {
                //In case of bad formatting, return the parsed value with no currency sign
            } catch (ParseException ignored) {
            }
        }
        return value;
    }

    /**
     * Get the currency formatter.
     *
     * @return NumberFormat
     */
    private static NumberFormat getNumberFormatter() {
        if (!initialized) {
            throw new RuntimeException("Currency converter not initialized");
        }
        // Get the universal number format
        NumberFormat currencyFormat = DecimalFormat.getCurrencyInstance(Locale.US);
        currencyFormat.setRoundingMode(RoundingMode.HALF_UP);
        currencyFormat.setMaximumFractionDigits(currencyFractionCount);
        currencyFormat.setMinimumFractionDigits(currencyFractionCount);
        currencyFormat.setGroupingUsed(true);
        DecimalFormatSymbols dfs = ((DecimalFormat) currencyFormat).getDecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setGroupingSeparator(currencyThousandsDelim.charAt(0));
        if (currencyFractionDelim != null) {
            dfs.setMonetaryDecimalSeparator(currencyFractionDelim.charAt(0));
        }
        ((DecimalFormat) currencyFormat).setDecimalFormatSymbols(dfs);
        return currencyFormat;
    }


    /**
     * Get the number format.<br> Case build version <= 2.x returns the universal number format
     * (Locale.US).<br> Otherwise returns the number format for the current Locale.<br>
     *
     * @return NumberFormat
     * @author GuilhermeSilva
     */
    @SuppressWarnings("unused")
    private static NumberFormat getNumberFormat() {
        return DecimalFormat.getCurrencyInstance();
    }

    public static String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Test if text is number
     *
     * @return true or false
     * @see "http://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-a-numeric-type-in-java?answertab=active#tab-top"
     */
    public static boolean isNumber(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String formatCurrencyPattern(String toFormat) {
        return String.format(currencyUnitPattern, toFormat);
    }

}
