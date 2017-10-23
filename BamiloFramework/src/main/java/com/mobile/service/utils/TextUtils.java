package com.mobile.service.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;

import com.mobile.service.objects.cart.PurchaseCartItem;
import com.mobile.service.objects.cart.PurchaseEntity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    private static String[] EMPTY_STRING_ARRAY = new String[]{};

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * Returns true if the string is not null and 0-length.
     *
     * @param str the string to be examined
     * @return true if str is not null and zero length
     */
    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    /**
     * Returns true if the sub string is part of string.
     *
     * @param str the string to be examined
     * @param sub the sub string to find
     * @return true if str is not null and zero length
     */
    public static boolean contains(@Nullable String str, @NonNull String sub) {
        return !isEmpty(str) && str.contains(sub);
    }

    /**
     * Returns true if the first string is equals to second string ignoring case.
     */
    public static boolean equalsIgnoreCase(String a, String b) {
        return isNotEmpty(a) && a.equalsIgnoreCase(b);
    }

    /**
     * Returns true if a and b are equal, including if they are both null.
     * <p><i>Note: In platform versions 1.1 and earlier, this method only worked well if
     * both the arguments were instances of String.</i></p>
     *
     * @param a first CharSequence to check
     * @param b second CharSequence to check
     * @return true if a and b are equal
     */
    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * String.split() returns [''] when the string to be split is empty. This returns []. This does
     * not remove any empty strings from the result. For example split("a,", ","  ) returns {"a", ""}.
     *
     * @param text       the string to split
     * @param expression the regular expression to match
     * @return an array of strings. The array will be empty if text is empty
     * @throws NullPointerException if expression or text is null
     */
    public static String[] split(String text, String expression) {
        if (text.length() == 0) {
            return EMPTY_STRING_ARRAY;
        } else {
            return text.split(expression, -1);
        }
    }

    /**
     * Constructs a String based on placeholder with HTML support.
     *
     * @param text        The normal text.
     * @param placeHolder The placeholder String.
     * @return A spanned String.
     */
    public static Spanned placeHolderText(String placeHolder, String text) {
        String textEncoded = htmlEncode(text);
        String placeHolderText = String.format(placeHolder, textEncoded);
        return Html.fromHtml(placeHolderText);
    }

    /**
     * Html-encode the string.
     *
     * @param s the string to be encoded
     * @return the encoded string
     */
    public static String htmlEncode(String s) {
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;"); //$NON-NLS-1$
                    break;
                case '>':
                    sb.append("&gt;"); //$NON-NLS-1$
                    break;
                case '&':
                    sb.append("&amp;"); //$NON-NLS-1$
                    break;
                case '\'':
                    //http://www.w3.org/TR/xhtml1
                    // The named character reference &apos; (the apostrophe, U+0027) was introduced in
                    // XML 1.0 but does not appear in HTML. Authors should therefore use &#39; instead
                    // of &apos; to work as expected in HTML 4 user agents.
                    sb.append("&#39;"); //$NON-NLS-1$
                    break;
                case '"':
                    sb.append("&quot;"); //$NON-NLS-1$
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Strip the escaped html two times to return a displayable html.
     *
     * @param html The escaped html
     * @return String
     */
    public static String stripHtml(String html) {
        return Html.fromHtml(Html.fromHtml(html).toString()).toString();
    }

    /**
     * Remove escaped line breaks
     */
    public static String unEscape(String literal) {
        return literal.replaceAll("\\\\n", "\n");
    }

    public static String joinCartItemSKUes(PurchaseEntity purchaseEntity) {
        String csv = "";
        if (purchaseEntity == null) return csv;
        ArrayList<PurchaseCartItem> items = purchaseEntity.getCartItems();
        if (items == null || items.size() == 0) return csv;
        for (PurchaseCartItem item : items) {
            csv = csv.concat(item.getSku()).concat(",");
        }
        csv = csv.substring(0, csv.length() - 1);
        return csv;
    }

    public static String getResourceString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    public static String getResourceString(Context context, int resId, Integer... args) {
        String str = context.getResources().getString(resId);
        return String.format(str, args);
    }

    public static boolean validateColorString(String color) {
        if (isEmpty(color)) {
            return false;
        }
        String HEX_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        Pattern pattern = Pattern.compile(HEX_PATTERN);
        Matcher matcher = pattern.matcher(color);
        return matcher.matches();
    }
}
