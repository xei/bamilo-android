package com.mobile.components.customfontviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.mobile.framework.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HoloFontLoader {
    
    public static FontCollector sFontCollector;
    public static Font sFontBold;
    //public static Font sFontBoldItalic;
    public static Font sFontItalic;
    public static Font sFontLight;
    public static Font sFontRegular;
    public static final int TEXT_STYLE_BLACK;
    public static final int TEXT_STYLE_BOLD;
    public static final int TEXT_STYLE_ITALIC;
    public static final int TEXT_STYLE_LIGHT;
    public static final int TEXT_STYLE_NORMAL;

    static {
        sFontStyleMapping = new HashMap<>();

        TEXT_STYLE_NORMAL = 0;
        TEXT_STYLE_BOLD = registerTextStyle("bold");
        TEXT_STYLE_ITALIC = registerTextStyle("italic");
        TEXT_STYLE_BLACK = registerTextStyle("black");
        TEXT_STYLE_LIGHT = registerTextStyle("light");
        // Create fonts
        //377 sFontRegular = new RobotoRawFont(R.raw.roboto_regular).setFontStyle(TEXT_STYLE_NORMAL);
        //377 sFontBold = new RobotoRawFont(R.raw.roboto_bold).setFontStyle(TEXT_STYLE_BOLD);
        //377 sFontItalic = new RobotoRawFont(R.raw.roboto_italic).setFontStyle(TEXT_STYLE_ITALIC);
        //377 sFontLight = new RobotoRawFont(R.raw.roboto_light).setFontStyle(TEXT_STYLE_LIGHT);


        sFontRegular = new BamiloRawFont(R.raw.bamilo_sans_regular).setFontStyle(TEXT_STYLE_NORMAL);
        sFontBold = new BamiloRawFont(R.raw.bamilo_sans_bold).setFontStyle(TEXT_STYLE_BOLD);
        sFontItalic = new BamiloRawFont(R.raw.bamilo_sans_ultra_light).setFontStyle(TEXT_STYLE_ITALIC);
        sFontLight = new BamiloRawFont(R.raw.bamilo_sans_light).setFontStyle(TEXT_STYLE_LIGHT);

        //sFontBoldItalic = new RobotoRawFont(R.raw.roboto_bolditalic).setFontStyle(TEXT_STYLE_BOLD | TEXT_STYLE_ITALIC);
        // Create collector
        sDefaultFont = sFontCollector = new FontCollector().allowAnyFontFamily();
        sFontCollector.register(sFontRegular).asDefaultFont();
        sFontCollector.register(sFontBold);
        sFontCollector.register(sFontItalic);
        sFontCollector.register(sFontLight);
        //sFontCollector.register(sFontBoldItalic);
    }


    public static void initFont(boolean isBurmese) {
        if (isBurmese) {
            // Create fonts
            sDefaultFont = sFontCollector = new FontCollector().allowAnyFontFamily();
            //377 sFontRegular = new RobotoRawFont(R.raw.burmese_regular).setFontStyle(TEXT_STYLE_NORMAL);
            //377 sFontBold = new RobotoRawFont(R.raw.burmese_regular).setFontStyle(TEXT_STYLE_BOLD);
            //377 sFontLight = new RobotoRawFont(R.raw.burmese_regular).setFontStyle(TEXT_STYLE_LIGHT);
            //377 sFontItalic = new RobotoRawFont(R.raw.burmese_regular).setFontStyle(TEXT_STYLE_ITALIC);

            sFontRegular = new BamiloRawFont(R.raw.bamilo_sans_regular).setFontStyle(TEXT_STYLE_NORMAL);
            sFontBold = new BamiloRawFont(R.raw.bamilo_sans_bold).setFontStyle(TEXT_STYLE_BOLD);
            sFontItalic = new BamiloRawFont(R.raw.bamilo_sans_ultra_light).setFontStyle(TEXT_STYLE_ITALIC);
            sFontLight = new BamiloRawFont(R.raw.bamilo_sans_light).setFontStyle(TEXT_STYLE_LIGHT);

            //sFontBoldItalic = new RobotoRawFont(R.raw.roboto_bolditalic).setFontStyle(TEXT_STYLE_BOLD | TEXT_STYLE_ITALIC);
            // Create collector
            sFontCollector.register(sFontRegular).asDefaultFont();
            sFontCollector.register(sFontBold);
            sFontCollector.register(sFontItalic);
            sFontCollector.register(sFontLight);
            //sFontCollector.register(sFontBoldItalic);
        }
    }
    
    
    private static final Map<String, Integer> sFontStyleMapping;
    private static Font sDefaultFont;
    private static List<String> sFontStyleKeys;
    private static int sNextTextStyleOffset = 0;

    private HoloFontLoader() {

    }

    public static <T extends View> T apply(T view, Font font) {
        if (view == null || font == null || view.isInEditMode()) {
            return view;
        }
        font.mContext = view.getContext();
        applyInternal(view, font);
        font.mContext = null;
        return view;
    }

    public static <T extends View> T applyDefaultFont(T view) {
        return apply(view, sDefaultFont);
    }

    private static void applyInternal(View view, Font font) {
        // TODO Validate if this is necessary
//        if (view instanceof ViewGroup) {
//            final ViewGroup vg = (ViewGroup) view;
//            final int childCount = vg.getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                applyInternal(vg.getChildAt(i), font);
//            }
//        }
        // Case View with interface FontStyleProvider
        if (view instanceof FontStyleProvider) {
            final FontStyleProvider provider = (FontStyleProvider) view;
            final int fontStyle = provider.getFontStyle();
            final String fontFamily = provider.getFontFamily();
            if (view.getTag(R.id.fontLoaderFont) == font
                    && equals(view.getTag(R.id.fontLoaderFontStyle), fontStyle)
                    && equals(view.getTag(R.id.fontLoaderFontFamily), fontFamily)) {
                return;
            }
            provider.setTypeface(font.getTypeface(fontFamily, fontStyle));
            view.setTag(R.id.fontLoaderFont, font);
            view.setTag(R.id.fontLoaderFontStyle, fontStyle);
            view.setTag(R.id.fontLoaderFontFamily, fontFamily);
        }
        // Case TextView
        else  if (view instanceof TextView) {
            ((TextView) view).setTypeface(font.getTypeface(font.mFontFamily, font.mFontStyle));
        }
    }

    private static boolean equals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    public static Object[] parseFontStyle(String string) {
        String fontFamily = null;
        int c = string.lastIndexOf('-');
        if (c > 0) {
            fontFamily = string.substring(0, c).toLowerCase(Locale.ENGLISH);
            string = string.substring(c + 1);
        }
        if (sFontStyleKeys == null) {
            sFontStyleKeys = new ArrayList<>(sFontStyleMapping.keySet());
        }
        int textStyle = TEXT_STYLE_NORMAL;
        for (int i = 0; i < sFontStyleKeys.size(); i++) {
            final String key = sFontStyleKeys.get(i);
            if (string.contains(key)) {
                textStyle |= sFontStyleMapping.get(key);
            }
        }
        return new Object[]{
                textStyle,
                fontFamily == null && textStyle == TEXT_STYLE_NORMAL ? string : fontFamily
        };
    }

    public static int registerTextStyle(String modifier) {
        if (sNextTextStyleOffset >= 32) {
            throw new IllegalStateException("Too much text styles!");
        }
        final int flag = 1 << sNextTextStyleOffset++;
        sFontStyleMapping.put(modifier.toLowerCase(Locale.ENGLISH), flag);
        sFontStyleKeys = null;
        return flag;
    }

    public interface FontStyleProvider {
        String getFontFamily();

        int getFontStyle();

        void setFontStyle(String fontFamily, int fontStyle);

        void setTypeface(Typeface typeface);
    }

    public static class Font implements Cloneable {
        private Context mContext;
        private String mFontFamily;
        private int mFontStyle;
        private boolean mLockModifing = false;
        private Typeface mTypeface;
        private boolean mTypefaceLoaded = false;

        public Font() {
        }

        public Font(Font font) {
            mContext = font.mContext;
            mFontStyle = font.mFontStyle;
            mTypeface = font.mTypeface;
            mTypefaceLoaded = font.mTypefaceLoaded;
            mFontFamily = font.mFontFamily;
        }

        protected final void assertContext() {
            if (mContext == null) {
                throw new IllegalStateException(
                        "Cannot load typeface without attaching font instance to FontLoader");
            }
        }

        protected final void assertModifing() {
            if (mLockModifing) {
                throw new IllegalStateException(
                        "Cannot modify typeface after attaching to FontCollector");
            }
        }

        public boolean available(Context context, String fontFamily, int fontStyle) {
            return mFontFamily == null ? fontFamily == null : mFontFamily.equals(fontFamily) && mFontStyle == fontStyle;
        }

        @Override
        public Font clone() {
            return new Font(this);
        }

        public final Context getContext() {
            return mContext;
        }

        public String getFontFamily() {
            return mFontFamily;
        }

        public Font setFontFamily(String fontFamily) {
            assertModifing();
            mFontFamily = fontFamily;
            return this;
        }

        public int getFontStyle() {
            return mFontStyle;
        }

        public Font setFontStyle(int fontStyle) {
            mFontStyle = fontStyle;
            return this;
        }

        protected Typeface getTypeface(String fontFamily, int fontStyle) {
            if (!mTypefaceLoaded) {
                mTypeface = loadTypeface();
                mTypefaceLoaded = true;
            }
            return mTypeface;
        }

        public Typeface loadTypeface() {
            return null;
        }

        public void lock() {
            mLockModifing = true;
        }

        protected final void resetTypeface() {
            mTypeface = null;
            mTypefaceLoaded = false;
        }
    }

    public static class FontCollector extends Font {
        private static final String DEFAULT_FONT_FAMILY = "FONT-FAMILY-DEFAULT";
        private final List<Font> mFonts;
        private boolean mAllowAnyFontFamily;
        private Font mDefaultFont;
        private Font mLastUsedFont;

        public FontCollector() {
            mFonts = new ArrayList<>();
        }

        public FontCollector(Font font) {
            super(font);
            if (font instanceof FontCollector) {
                FontCollector fontCollector = (FontCollector) font;
                mFonts = new ArrayList<>(fontCollector.mFonts);
                mAllowAnyFontFamily = fontCollector.mAllowAnyFontFamily;
                if (fontCollector.mDefaultFont != null) {
                    mDefaultFont = fontCollector.mDefaultFont.clone();
                }
            } else {
                mFonts = new ArrayList<>();
            }
        }

        public FontCollector allowAnyFontFamily() {
            mAllowAnyFontFamily = true;
            return this;
        }

        public FontCollector asDefaultFont() {
            mDefaultFont = mLastUsedFont;
            return this;
        }

        @Override
        public FontCollector clone() {
            return new FontCollector(this);
        }


        private Typeface getTypeface(Font font, String fontFamily, int fontStyle) {
            font.mContext = getContext();
            final Typeface typeface = font.getTypeface(fontFamily, fontStyle);
            font.mContext = null;
            return typeface;
        }

        @Override
        public boolean available(Context context, String fontFamily, int fontStyle) {
            final Font font = findFont(fontFamily, fontStyle);
            return font != null && font.available(context, fontFamily, fontStyle);
        }

        private Font findFont(String fontFamily, int fontStyle) {
            if (fontFamily == null) {
                fontFamily = DEFAULT_FONT_FAMILY;
            }
            for (int i = 0; i < mFonts.size(); i++) {
                Font font = mFonts.get(i);
                if ((mAllowAnyFontFamily || fontFamily.equals(font.mFontFamily))
                        && font.mFontStyle == fontStyle) {
                    return font;
                }
            }
            if (mDefaultFont != null) {
                mDefaultFont.mContext = getContext();
                return mDefaultFont;
            }
            return null;
        }

        @Override
        protected Typeface getTypeface(String fontFamily, int fontStyle) {
            final Font font = findFont(fontFamily, fontStyle);
            return font != null ? getTypeface(font, fontFamily, fontStyle) : null;
        }

        public FontCollector register(Font font) {
            if (font == null) {
                return this;
            }
            font.lock();
            mFonts.add(font);
            mLastUsedFont = font;
            return this;
        }

        public FontCollector unregister(Font font) {
            mFonts.remove(font);
            return this;
        }

        public FontCollector unregister(String fontFamily, int fontStyle) {
            for (int i = 0; i < mFonts.size(); i++) {
                final Font font = mFonts.get(i);
                if (HoloFontLoader.equals(fontFamily, font.mFontFamily) && font.mFontStyle == fontStyle) {
                    mFonts.remove(font);
                    return this;
                }
            }
            return this;
        }
    }

    public static class RawFont extends Font {
        private static long sApplicationInstallDate = -1;
        private int mRawResourceId;

        public RawFont(Font font) {
            super(font);
            if (font instanceof RawFont) {
                mRawResourceId = ((RawFont) font).mRawResourceId;
            }
        }

        public RawFont(int rawResourceId) {
            mRawResourceId = rawResourceId;
        }

        @Override
        public boolean available(Context context, String fontFamily, int fontStyle) {
            boolean result = false;
            try {
                final InputStream is = context.getResources().openRawResource(mRawResourceId);
                is.close();
                result = true;
            } catch (Exception e) {
            }
            return result && super.available(context, fontFamily, fontStyle);
        }

        @Override
        public RawFont clone() {
            return new RawFont(this);
        }

        @Override
        public Typeface loadTypeface() {
            assertContext();
            return loadRawTypeface();
        }

        @SuppressLint("NewApi")
        protected Typeface loadRawTypeface() {
            final File fontFile = new File(getContext().getCacheDir(), "font_0x" + Integer.toHexString(mRawResourceId));
            if (fontFile.exists()) {
                if (sApplicationInstallDate == -1) {
                    try {
                        final Context context = getContext();
                        final PackageInfo ai = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                        sApplicationInstallDate = Math.max(ai.lastUpdateTime, ai.firstInstallTime);

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        sApplicationInstallDate = System.currentTimeMillis();
                    }
                }
                if (fontFile.lastModified() < sApplicationInstallDate) {
                    fontFile.delete();
                    return loadTypeface(fontFile, false);
                }
                return loadTypeface(fontFile, true);
            }
            return loadTypeface(fontFile, false);
        }

        private Typeface loadTypeface(File file, boolean allowFileReusage) {
            if (file.exists() && allowFileReusage) {
                try {
                    return tryToLoadRawTypeface(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                InputStream is = getContext().getResources().openRawResource(mRawResourceId);
                OutputStream os = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int c;
                while ((c = is.read(buffer)) > 0) {
                    os.write(buffer, 0, c);
                }
                os.flush();
                os.close();
                is.close();
                return tryToLoadRawTypeface(file);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private Typeface tryToLoadRawTypeface(File file) throws Exception {
            Typeface typeface = Typeface.createFromFile(file);
            if (typeface == null) {
                throw new NullPointerException();
            }
            return typeface;
        }

    }

    private static final class RobotoRawFont extends RawFont {
        public RobotoRawFont(int rawResourceId) {
            super(rawResourceId);
            setFontFamily("roboto");
        }
    }

    private static final class BamiloRawFont extends RawFont {
        public BamiloRawFont(int rawResourceId) {
            super(rawResourceId);
            setFontFamily("bamilo");
        }
    }
}