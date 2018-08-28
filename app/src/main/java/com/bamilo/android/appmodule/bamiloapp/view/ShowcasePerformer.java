package com.bamilo.android.appmodule.bamiloapp.view;

import android.app.Activity;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.utils.GestureListener;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.OnViewInflateListener;

import static android.widget.RelativeLayout.CENTER_HORIZONTAL;

/**
 * Created on 11/11/2017.
 */

public class ShowcasePerformer {

    public static FancyShowCaseView createSimpleCircleShowcase(final Activity activity, String showcaseName,
                                                               View view, final String message,
                                                               final String buttonTitle, int delay) {

        return createSimpleShowcase(activity, showcaseName, FocusShape.CIRCLE,
                view, message, buttonTitle, delay);
    }

    public static FancyShowCaseView createSimpleRectShowcase(final Activity activity, String showcaseName,
                                                             View view, final String message,
                                                             final String buttonTitle, int delay) {

        return createSimpleShowcase(activity, showcaseName, FocusShape.ROUNDED_RECTANGLE,
                view, message, buttonTitle, delay);
    }

    private static FancyShowCaseView createSimpleShowcase(final Activity activity, String showcaseName,
                                                          FocusShape showcaseShape,
                                                          final View view, final String message,
                                                          final String buttonTitle, int delay) {
        return new FancyShowCaseView.Builder(activity)
                .focusOn(view)
                .showOnce(showcaseName)
                .enableTouchOnFocusedView(true)
                .focusShape(showcaseShape)
                .delay(delay)
                .roundRectRadius((int) activity.getResources().getDimension(R.dimen.showcase_rect_round_radius))
                .customView(R.layout.showcase_simple_layout, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(@NonNull final View inflatedView) {
                        Button btnDismiss = (Button) inflatedView.findViewById(R.id.btnShowcaseDismiss);
                        btnDismiss.setText(buttonTitle);
                        btnDismiss.findViewById(R.id.btnShowcaseDismiss).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FancyShowCaseView.hideCurrent(activity);
                            }
                        });

                        TextView tvShowcaseTitle = (TextView) inflatedView.findViewById(R.id.tvShowcaseTitle);
                        tvShowcaseTitle.setText(message);

                        inflatedView.post(new Runnable() {
                            @Override
                            public void run() {
                                RelativeLayout.LayoutParams params =
                                        (RelativeLayout.LayoutParams) inflatedView.findViewById(R.id.llShowcase).getLayoutParams();
                                params.addRule(CENTER_HORIZONTAL);

                                Rect viewRect = new Rect();
                                view.getGlobalVisibleRect(viewRect);

                                int showcaseViewHeight = inflatedView.findViewById(R.id.llShowcase).getHeight();
                                int spacing = (int) inflatedView.getContext()
                                        .getResources().getDimension(R.dimen.showcase_view_spacing);

                                int viewBottomY = viewRect.height() + viewRect.top;

                                int showcaseViewBottomY = viewBottomY + spacing + showcaseViewHeight;
                                int showcaseMarginTop = viewBottomY + spacing;

                                if (showcaseViewBottomY > inflatedView.getHeight()) {
                                    showcaseMarginTop = viewRect.top - spacing - showcaseViewHeight;
                                }

                                params.topMargin = showcaseMarginTop;
                                inflatedView.findViewById(R.id.llShowcase).setLayoutParams(params);
                            }
                        });
                    }
                })
                .closeOnTouch(true)
                .build();
    }
}
