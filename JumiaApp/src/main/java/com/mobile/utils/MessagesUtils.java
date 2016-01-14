package com.mobile.utils;

import com.mobile.newFramework.utils.EventType;
import com.mobile.view.R;

/**
 * MessagesUtils
 * Fallback messages for each event type
 * Default will retrieve generic message
 *
 * @author Manuel Silva
 *
 */
public enum MessagesUtils {

    GENERIC_MESSAGE(R.string.error_occured, -1),

    ADD_ITEM_TO_SHOPPING_CART_EVENT(R.string.error_add_to_shopping_cart, R.string.added_to_shop_cart_dialog_text),

    ADD_ITEMS_TO_SHOPPING_CART_EVENT(R.string.some_products_not_added, R.string.added_bundle_to_shop_cart_dialog_text),

    ADD_PRODUCT_BUNDLE(R.string.some_products_not_added, R.string.added_bundle_to_shop_cart_dialog_text),

    ADD_PRODUCT_TO_WISH_LIST(R.string.error_occured, R.string.products_added_saved),

    REMOVE_PRODUCT_FROM_WISH_LIST(R.string.error_occured, R.string.products_removed_saved),

    EDIT_ADDRESS_EVENT(R.string.error_please_try_again, R.string.edit_address_success),

    FORGET_PASSWORD_EVENT(R.string.error_forgotpassword_title, R.string.forgotten_password_successtext),

    LOGIN_EVENT(R.string.error_login_title, R.string.succes_login);

    public final int errorMessageId;
    public final int successMessageId;

    /**
     * Constructor with error message and success message
     * @param errorMessageId The error message id
     * @param successMessageId The success message id
     */
    MessagesUtils(int errorMessageId, int successMessageId) {
        this.errorMessageId = errorMessageId;
        this.successMessageId = successMessageId;
    }

    public static int getSuccessMessageId(EventType eventType){
        if (eventType != null){
            for (MessagesUtils messageUtils  : MessagesUtils.values()) {
                if(messageUtils.toString().equalsIgnoreCase(eventType.toString())){
                    return messageUtils.successMessageId;
                }
            }
        }

        return GENERIC_MESSAGE.successMessageId;
    }

}
