package com.mobile.newFramework.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

/**
 * Created by rsoares on 6/12/15.
 */
public class TextViewUtils {

//    public static final String ROBOTO_REGULAR = "raw/roboto_regular.ttf";
//    public static final String ROBOTO_BOLD = "roboto_bold.ttf";
//    public static final string ROBOTO_M

    public static SpannableString setSpan(String first, String second, int firstColor, int secondColor){
        SpannableString spannableString = new SpannableString(first + second);
        spannableString.setSpan(new ForegroundColorSpan(firstColor), 0, first.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(secondColor), first.length(), first.length() + second.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

//    public static SpannableString setSpan(Context context, String first, String second, int firstColor, int secondColor, String firstFont, String secondFont){
//        SpannableString spannableString = setSpan(first,second,firstColor,secondColor);
//        Typeface robotoRegular = Typeface.createFromAsset(context.getAssets(), firstFont);
//        Typeface robotoBold = Typeface.createFromAsset(context.getAssets(), secondFont);
//
////        TypefaceSpan robotoRegularSpan = new CustomTypefaceSpan("", robotoRegular);
////        TypefaceSpan robotoBoldSpan = new CustomTypefaceSpan("", robotoBold);
//
////        spannableString.setSpan();
//        return spannableString;
//    }
}
