package com.bamilo.android.framework.service.objects.catalog.filters;

import android.support.annotation.NonNull;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * This interface must be implemented by an object that has as purpose being one of many filter options.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/09/04
 *
 */
public interface MultiFilterOptionInterface extends FilterOptionInterface {
    void setSelected(boolean selected);
    @NonNull String getLabel();
    boolean isSelected();
    @NonNull String getVal();
}
