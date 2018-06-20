package com.bamilo.modernbamilo.util.typography;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.Locale;


/**
 * Created by hamidreza on 2/14/16.
 */
public class TypeFaceHelper {

    private static final String TAG_DEBUG = TypeFaceHelper.class.getSimpleName();

    public static final int FONT_DEFAULT_LOCALE = 1;
    public static final int FONT_ROBOTO_REGULAR = 2;
    public static final int FONT_ROBOTO_BOLD = 3;
    public static final int FONT_IRAN_SANS_REGULAR = 4;
    public static final int FONT_IRAN_SANS_BOLD = 5;

    private static final String LANGUAGE_ENGLISH = "en";
    private static final String LANGUAGE_PERSIAN = "fa";

    private static TypeFaceHelper sInstance;

    private Typeface mRobotoRegularTypeface;
    private Typeface mRobotoBoldTypeface;
    private Typeface mIranSansRegularTypeface;
    private Typeface mIranSansBoldTypeface;

    private TypeFaceHelper() {
        // Private constructor prevents instantiation with "new" operator.
    }

    public static synchronized TypeFaceHelper getInstance(Context context) {
        if(sInstance == null) {
            sInstance = new TypeFaceHelper();
            sInstance.init(context);
        }
        return sInstance;
    }

    private void init(Context context) {
        mRobotoRegularTypeface = Typeface.createFromAsset(context.getAssets(), "typeface/Roboto-Regular.ttf");
        mRobotoBoldTypeface = Typeface.createFromAsset(context.getAssets(), "typeface/Roboto-Bold.ttf");
        mIranSansRegularTypeface = Typeface.createFromAsset(context.getAssets(), "typeface/IRANSansMobile.ttf");
        mIranSansBoldTypeface = Typeface.createFromAsset(context.getAssets(), "typeface/IRANSansMobile_Bold.ttf");
    }

    public Typeface getTypeFace(int font){
        switch (font) {
            case FONT_DEFAULT_LOCALE:
                return getDefaultFontBasedOnLocale();
            case FONT_ROBOTO_REGULAR:
                return mRobotoRegularTypeface;

            case FONT_ROBOTO_BOLD:
                return mRobotoBoldTypeface;

            case FONT_IRAN_SANS_REGULAR:
                return mIranSansRegularTypeface;

            case FONT_IRAN_SANS_BOLD:
                return mIranSansBoldTypeface;

            default:
                Log.e(TAG_DEBUG, "Can not get typeface: Invalid Font!");
                return null;
        }
    }

    private Typeface getDefaultFontBasedOnLocale() {
        String language = Locale.getDefault().getLanguage();
        switch (language) {
            case LANGUAGE_ENGLISH:
                return mRobotoRegularTypeface;
            case LANGUAGE_PERSIAN:
                return mIranSansRegularTypeface;
            default:
                // Using default typeface for other languages.
                return null;
        }
    }

}
