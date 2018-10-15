package com.bamilo.android.framework.components.customfontviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;

import com.bamilo.android.appmodule.modernbamilo.customview.XeiTextView;

/**
 * https://github.com/AndroidDeveloperLB/AutoFitTextView
 */
public class AutoResizeTextView extends XeiTextView {
    private static final int NO_LINE_LIMIT = -1;
    private final RectF mAvailableSpaceRect = new RectF();
    private final SparseIntArray mTextCachedSizes = new SparseIntArray();
    private final SizeTester mSizeTester;
    private float mMaxTextSize;
    private float mSpacingMult = 1.0f;
    private float mSpacingAdd = 0.0f;
    private float mMinTextSize;
    private int mWidthLimit;
    private int mMaxLines;
    private boolean enableSizeCache = true;
    private boolean initialized = false;
    private TextPaint mPaint;

    private interface SizeTester {
        /**
         * @param suggestedSize  Size of text to be tested
         * @param availableSpace available space in which text must fit
         * @return an integer < 0 if after applying {@code suggestedSize} to
         * text, it takes less space than {@code availableSpace}, > 0
         * otherwise
         */
        int onTestSize(int suggestedSize, RectF availableSpace);
    }

    public AutoResizeTextView(final Context context) {
        this(context, null, 0);
    }

    public AutoResizeTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoResizeTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        // using the minimal recommended font size
        mMinTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
        mMaxTextSize = getTextSize();
        if (mMaxLines == 0)
            // no value was assigned during construction
            mMaxLines = NO_LINE_LIMIT;
        // prepare size tester:
        mSizeTester = new SizeTester() {
            final RectF textRect = new RectF();

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public int onTestSize(final int suggestedSize, final RectF availableSPace) {
                mPaint.setTextSize(suggestedSize);
                final String text = getText().toString();
                final boolean singleLine = getMaxLines() == 1;
                if (singleLine) {
                    textRect.bottom = mPaint.getFontSpacing();
                    textRect.right = mPaint.measureText(text);
                } else {
                    final StaticLayout layout = new StaticLayout(text, mPaint, mWidthLimit, Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, true);
                    // return early if we have more lines
                    if (getMaxLines() != NO_LINE_LIMIT && layout.getLineCount() > getMaxLines())
                        return 1;
                    textRect.bottom = layout.getHeight();
                    int maxWidth = -1;
                    for (int i = 0; i < layout.getLineCount(); i++)
                        if (maxWidth < layout.getLineRight(i) - layout.getLineLeft(i))
                            maxWidth = (int) layout.getLineRight(i) - (int) layout.getLineLeft(i);
                    textRect.right = maxWidth;
                }
                textRect.offsetTo(0, 0);
                if (availableSPace.contains(textRect))
                    // may be too small, don't worry we will find the best match
                    return -1;
                // else, too big
                return 1;
            }
        };
        initialized = true;
    }

    @Override
    public void setTypeface(final Typeface tf) {
        if (mPaint == null)
            mPaint = new TextPaint(getPaint());
        mPaint.setTypeface(tf);
        adjustTextSize();
        super.setTypeface(tf);
    }

    @Override
    public void setTextSize(final float size) {
        mMaxTextSize = size;
        mTextCachedSizes.clear();
        adjustTextSize();
    }

    @Override
    public void setMaxLines(final int maxlines) {
        super.setMaxLines(maxlines);
        mMaxLines = maxlines;
        reAdjust();
    }

    @Override
    public int getMaxLines() {
        return mMaxLines;
    }

    @Override
    public void setSingleLine() {
        super.setSingleLine();
        mMaxLines = 1;
        reAdjust();
    }

    @Override
    public void setSingleLine(final boolean singleLine) {
        super.setSingleLine(singleLine);
        if (singleLine)
            mMaxLines = 1;
        else mMaxLines = NO_LINE_LIMIT;
        reAdjust();
    }

    @Override
    public void setLines(final int lines) {
        super.setLines(lines);
        mMaxLines = lines;
        reAdjust();
    }

    @Override
    public void setTextSize(final int unit, final float size) {
        final Context c = getContext();
        Resources r;
        if (c == null)
            r = Resources.getSystem();
        else r = c.getResources();
        mMaxTextSize = TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
        mTextCachedSizes.clear();
        adjustTextSize();
    }

    @Override
    public void setLineSpacing(final float add, final float mult) {
        super.setLineSpacing(add, mult);
        mSpacingMult = mult;
        mSpacingAdd = add;
    }

    /**
     * Set the lower text size limit and invalidate the view
     *
     * @param minTextSize
     */
    public void setMinTextSize(final float minTextSize) {
        mMinTextSize = minTextSize;
        reAdjust();
    }

    private void reAdjust() {
        adjustTextSize();
    }

    private void adjustTextSize() {
        // This is a workaround for truncated text issue on ListView, as shown here: https://github.com/AndroidDeveloperLB/AutoFitTextView/pull/14
        // TODO think of a nicer, elegant solution.
//    post(new Runnable()
//    {
//    @Override
//    public void run()
//      {
        if (!initialized)
            return;
        final int startSize = (int) mMinTextSize;
        final int heightLimit = getMeasuredHeight() - getCompoundPaddingBottom() - getCompoundPaddingTop();
        mWidthLimit = getMeasuredWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
        if (mWidthLimit <= 0)
            return;
        mAvailableSpaceRect.right = mWidthLimit;
        mAvailableSpaceRect.bottom = heightLimit;
        superSetTextSize(startSize);
//      }
//    });
    }

    private void superSetTextSize(int startSize) {
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, efficientTextSizeSearch(startSize, (int) mMaxTextSize, mSizeTester, mAvailableSpaceRect));
    }

    /**
     * Enables or disables size caching, enabling it will improve performance
     * where you are animating a value inside TextView. This stores the font
     * size against getText().length() Be careful though while enabling it as 0
     * takes more space than 1 on some fonts and so on.
     *
     * @param enable enable font size caching
     */
    public void setEnableSizeCache(final boolean enable) {
        enableSizeCache = enable;
        mTextCachedSizes.clear();
        adjustTextSize();
    }

    private int efficientTextSizeSearch(final int start, final int end, final SizeTester sizeTester, final RectF availableSpace) {
        if (!enableSizeCache)
            return binarySearch(start, end, sizeTester, availableSpace);
        final String text = getText().toString();
        final int key = text == null ? 0 : text.length();
        int size = mTextCachedSizes.get(key);
        if (size != 0)
            return size;
        size = binarySearch(start, end, sizeTester, availableSpace);
        mTextCachedSizes.put(key, size);
        return size;
    }

    private int binarySearch(final int start, final int end, final SizeTester sizeTester, final RectF availableSpace) {
        int lastBest = start;
        int lo = start;
        int hi = end - 1;
        int mid = 0;
        while (lo <= hi) {
            mid = lo + hi >>> 1;
            final int midValCmp = sizeTester.onTestSize(mid, availableSpace);
            if (midValCmp < 0) {
                lastBest = lo;
                lo = mid + 1;
            } else if (midValCmp > 0) {
                hi = mid - 1;
                lastBest = hi;
            } else return mid;
        }
        // make sure to return last best
        // this is what should always be returned
        return lastBest;
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        super.onTextChanged(text, start, before, after);
        reAdjust();
    }

    @Override
    protected void onSizeChanged(final int width, final int height, final int oldwidth, final int oldheight) {
        mTextCachedSizes.clear();
        super.onSizeChanged(width, height, oldwidth, oldheight);
        if (width != oldwidth || height != oldheight)
            reAdjust();
    }
}