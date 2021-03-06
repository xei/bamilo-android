package com.bamilo.android.appmodule.bamiloapp.pojo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Interface for custom views to be used in dynamic form item.
 * @author spereira
 */
public interface ICustomFormFieldView {

    boolean validate();

    @Nullable String save();

    void load(@Nullable String value);

    @NonNull View getView();

}
