package com.bamilo.android.core.presentation;

/**
 * Created on 12/20/2017.
 * All presenters should extend this
 */

public interface BasePresenter<T> {
    void setView(T view);

    void destroy();
}
