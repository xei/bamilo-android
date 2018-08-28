package com.bamilo.android.framework.components.widget.likebutton;

import android.widget.ImageView;

/**
 * Created by Farshid since 6/19/2018. contact farshidabazari@gmail.com
 */
public interface SparkEventListener {
    void onEvent(ImageView button, boolean buttonState);
    void onEventAnimationEnd(ImageView button, boolean buttonState);
    void onEventAnimationStart(ImageView button, boolean buttonState);
}
