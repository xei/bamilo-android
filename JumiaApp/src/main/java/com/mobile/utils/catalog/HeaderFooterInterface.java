package com.mobile.utils.catalog;

import android.support.annotation.Nullable;
import android.view.View;

import com.mobile.newFramework.objects.catalog.Banner;

/**
 * Created by rsoares on 10/2/15.
 */
public interface HeaderFooterInterface {
    void showHeaderView();
    void hideHeaderView();
    void showFooterView();
    void hideFooterView();
    void setHeader(@Nullable Banner banner);
    void setHeader(@Nullable View banner);
}
