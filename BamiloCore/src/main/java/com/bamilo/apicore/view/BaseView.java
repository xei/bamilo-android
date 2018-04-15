package com.bamilo.apicore.view;

import com.bamilo.apicore.service.model.ServerResponse;

/**
 * Created on 12/20/2017.
 * All views should extend this
 */

public interface BaseView {
    void showMessage(String message);

    void showOfflineMessage();

    void showConnectionError();

    void showServerError(ServerResponse response);

    void toggleProgress(boolean show);

    void showRetry();
}
