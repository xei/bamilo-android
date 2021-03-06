package com.bamilo.android.core.view;

import com.bamilo.android.core.service.model.EventType;
import com.bamilo.android.core.service.model.ServerResponse;

/**
 * Created on 12/20/2017.
 * All views should extend this
 */

public interface BaseView {
    void showMessage(EventType eventType, String message);

    void showOfflineMessage(EventType eventType);

    void showConnectionError(EventType eventType);

    void showServerError(EventType eventType, ServerResponse response);

    void toggleProgress(EventType eventType, boolean show);

    void showRetry(EventType eventType);
}
