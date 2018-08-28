package com.bamilo.android.appmodule.bamiloapp.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.bamilo.android.core.service.model.data.itemtracking.History;
//import com.bamilo.android.framework.components.customfontviews.HoloFontLoader;
import com.bamilo.android.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemTrackingProgressBar extends View /*implements HoloFontLoader.FontStyleProvider*/ {
    private static Map<String, Integer> stateIconsMap;

    static {
        stateIconsMap = new HashMap<>();
        stateIconsMap.put(History.NAME_NEW + History.STATUS_SUCCESS, R.drawable.ic_order_created_success);
        stateIconsMap.put(History.NAME_APPROVED + History.STATUS_SUCCESS, R.drawable.ic_order_approved_success);
        stateIconsMap.put(History.NAME_DELIVERED + History.STATUS_SUCCESS, R.drawable.ic_order_delivered_success);
        stateIconsMap.put(History.NAME_RECEIVED + History.STATUS_SUCCESS, R.drawable.ic_order_received_success);
        stateIconsMap.put(History.NAME_SHIPPED + History.STATUS_SUCCESS, R.drawable.ic_order_shipped_success);
        stateIconsMap.put(History.NAME_NEW + History.STATUS_ACTIVE, R.drawable.ic_order_created_active);
        stateIconsMap.put(History.NAME_APPROVED + History.STATUS_ACTIVE, R.drawable.ic_order_approved_active);
        stateIconsMap.put(History.NAME_DELIVERED + History.STATUS_ACTIVE, R.drawable.ic_order_delivered_active);
        stateIconsMap.put(History.NAME_RECEIVED + History.STATUS_ACTIVE, R.drawable.ic_order_received_active);
        stateIconsMap.put(History.NAME_SHIPPED + History.STATUS_ACTIVE, R.drawable.ic_order_shipped_active);
    }

    private static final int DEFAULT_OVAL_RAD_DIP = 5;
    private static final int DEFAULT_BORDER_WIDTH_DIP = 3;
    private List<History> itemHistories;
    private int defaultNeutralColor;
    private int defaultSuccessColor;
    private int defaultFailureColor;
    private int ovalRadius;
    private int borderWidth;
    private int textSize;
    private String mFontFamily;
    private int mFontStyle;
    private TextPaint mTextPaint;
    private Paint mPaint;
    private boolean breakDownText;

    public ItemTrackingProgressBar(Context context) {
        super(context);
        init();
    }

    public ItemTrackingProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ItemTrackingProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        float dip = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        ovalRadius = (int) (DEFAULT_OVAL_RAD_DIP * dip);
        borderWidth = (int) (DEFAULT_BORDER_WIDTH_DIP * dip);
        textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());
        defaultNeutralColor = ContextCompat.getColor(getContext(), R.color.progress_neutral);
        defaultSuccessColor = ContextCompat.getColor(getContext(), R.color.progress_success);
        defaultFailureColor = ContextCompat.getColor(getContext(), R.color.progress_failure);


        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(defaultNeutralColor);
        mPaint.setStrokeWidth(borderWidth);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(defaultNeutralColor);
        mTextPaint.setTextSize(textSize);

        // TODO: 8/28/18 farshid
//        HoloFontLoader.applyDefaultFont(this);
    }

    public List<History> getItemHistories() {
        return itemHistories;
    }

    public void setItemHistories(List<History> itemHistories) {
        this.itemHistories = itemHistories;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if (itemHistories != null && itemHistories.size() > 0) {
            int baseDistance = calcBaseDistance(itemHistories, width - (2 * ovalRadius));
            int lineStartX = ovalRadius + (baseDistance / 2);
            int lineStopX = width - (2 * ovalRadius) - (baseDistance / 2);
            int lineY = height / 2 - (4 * ovalRadius);
            mPaint.setColor(defaultNeutralColor);
            canvas.drawLine(lineStartX, lineY, lineStopX, lineY, mPaint);

            int progress = 0;
            int progressSum;
            int multipliersSum = 0;
            for (int i = 0; i < itemHistories.size() - 1; i++) {
                History history = itemHistories.get(i);
                progress += history.getProgress() * history.getWidthMultiplier();
                multipliersSum += history.getWidthMultiplier();
            }
            progressSum = multipliersSum * 100;
            progress = (int) (progress * 100F / progressSum);

            for (History history : itemHistories) {
                float textWidth = mTextPaint.measureText(history.getNameFa());
                if (textWidth > (baseDistance - ovalRadius)) {
                    breakDownText = true;
                    break;
                }
            }


            if (progress > 0) {
                mPaint.setColor(defaultSuccessColor);
                int prgWidth = lineStopX - lineStartX;
                int progressStopX = lineStopX - (prgWidth * progress / 100);
                canvas.drawLine(lineStopX, lineY, progressStopX, lineY, mPaint);
                if (progress % (100 / multipliersSum) != 0) {
                    canvas.drawLine(progressStopX, lineY - ovalRadius, progressStopX, lineY + ovalRadius, mPaint);
                }
            }

            int lastActiveItemPosition = -1;
            for (int i = itemHistories.size() - 1; i >= 0; i--) {
                if (itemHistories.get(i).getStatus().equals(History.STATUS_ACTIVE) ||
                        itemHistories.get(i).getStatus().equals(History.STATUS_SUCCESS)) {
                    lastActiveItemPosition = i;
                    break;
                }
                if (itemHistories.get(i).getStatus().equals(History.STATUS_FAILED)) {
                    break;
                }
            }

            int cx = width - ovalRadius - (baseDistance / 2);
            int cy = height / 2 - (4 * ovalRadius);

            Integer drawableId = stateIconsMap.get(itemHistories.get(0).getName() + itemHistories.get(0).getStatus());
            if (drawableId != null && lastActiveItemPosition == 0
                    && itemHistories.get(0).getProgress() == 0) {
                Bitmap iconBitmap = BitmapFactory.decodeResource(getContext().getResources(),
                        drawableId);
                mPaint.setColor(defaultNeutralColor);
                canvas.drawBitmap(iconBitmap, cx - (iconBitmap.getWidth() / 2), cy - (iconBitmap.getHeight() / 2), mPaint);
            } else {
                mPaint.setColor(defaultNeutralColor);
                if (itemHistories.get(0).getStatus().equals(History.STATUS_SUCCESS)) {
                    mPaint.setColor(defaultSuccessColor);
                } else if (itemHistories.get(0).getStatus().equals(History.STATUS_FAILED)) {
                    mPaint.setColor(defaultFailureColor);
                }
                canvas.drawCircle(cx, cy, ovalRadius, mPaint);
            }

            canvas.save();
            mTextPaint.setColor(defaultNeutralColor);
            if (itemHistories.get(0).getStatus().equals(History.STATUS_SUCCESS)) {
                mTextPaint.setColor(defaultSuccessColor);
            } else if (itemHistories.get(0).getStatus().equals(History.STATUS_FAILED)) {
                mTextPaint.setColor(defaultFailureColor);
            }
            String displayName = itemHistories.get(0).getNameFa();
            if (breakDownText) {
                displayName = displayName.replaceAll(" ", "\n");
            }
            StaticLayout textBoundary = new StaticLayout(displayName, mTextPaint,
                    baseDistance - ovalRadius, Layout.Alignment.ALIGN_CENTER, 0.8f, 0.0f, false);
            canvas.translate(cx - textBoundary.getWidth() / 2, cy + (3 * ovalRadius));
            textBoundary.draw(canvas);
            canvas.restore();

            int i = 0;
            while (i < itemHistories.size() - 1) {
                History history = itemHistories.get(i + 1);
                cx = cx - (itemHistories.get(i).getWidthMultiplier() * baseDistance);

                drawableId = stateIconsMap.get(history.getName() + history.getStatus());
                if (drawableId != null && lastActiveItemPosition == i + 1
                        && history.getProgress() == 0) {
                    Bitmap iconBitmap = BitmapFactory.decodeResource(getContext().getResources(),
                            drawableId);
                    canvas.drawBitmap(iconBitmap, cx - (iconBitmap.getWidth() / 2), cy - (iconBitmap.getHeight() / 2), mPaint);
                } else {
                    mPaint.setColor(defaultNeutralColor);
                    if (history.getStatus().equals(History.STATUS_SUCCESS)) {
                        mPaint.setColor(defaultSuccessColor);
                    } else if (history.getStatus().equals(History.STATUS_FAILED)) {
                        mPaint.setColor(defaultFailureColor);
                    }
                    canvas.drawCircle(cx, cy, ovalRadius, mPaint);
                }


                mTextPaint.setColor(defaultNeutralColor);
                if (history.getStatus().equals(History.STATUS_SUCCESS)) {
                    mTextPaint.setColor(defaultSuccessColor);
                } else if (history.getStatus().equals(History.STATUS_FAILED)) {
                    mTextPaint.setColor(defaultFailureColor);
                }
                displayName = history.getNameFa();
                if (breakDownText) {
                    displayName = displayName.replaceAll(" ", "\n");
                }
                textBoundary = new StaticLayout(displayName, mTextPaint,
                        baseDistance - ovalRadius, Layout.Alignment.ALIGN_CENTER, .8f, 0.0f, false);
                canvas.save();
                canvas.translate(cx - textBoundary.getWidth() / 2, cy + (3 * ovalRadius));
                textBoundary.draw(canvas);
                canvas.restore();

                i++;
            }
        }
    }

    private int calcBaseDistance(List<History> itemHistories, int width) {
        int multipliersSum = 0;
        for (int i = 1; i < itemHistories.size(); i++) {
            multipliersSum += itemHistories.get(i).getWidthMultiplier();
        }
        multipliersSum++;
        return width / multipliersSum;
    }

//    @Override
//    public String getFontFamily() {
//        return mFontFamily;
//    }
//
//    @Override
//    public int getFontStyle() {
//        return mFontStyle;
//    }
//
//    @Override
//    public void setFontStyle(String fontFamily, int fontStyle) {
//        mFontFamily = fontFamily;
//        mFontStyle = fontStyle;
//    }
//
//    @Override
//    public void setTypeface(Typeface typeface) {
//        if (typeface != null) {
//            mTextPaint.setTypeface(typeface);
//        }
//    }
}
