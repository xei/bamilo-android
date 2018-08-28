package com.bamilo.android.core.view;

import com.bamilo.android.core.service.model.data.home.BaseComponent;

import java.util.List;

/**
 * Created on 12/20/2017.
 */

public interface HomeView extends BaseView {
    void performHomeComponents(List<BaseComponent> components);
}
