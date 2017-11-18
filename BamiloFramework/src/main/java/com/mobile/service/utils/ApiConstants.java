package com.mobile.service.utils;

public final class ApiConstants {
    public static final String GET_REGIONS_API_PATH = "form_field_call::customer/getaddressregions/";
    public static final String GET_CITIES_API_PATH = "form_field_call::customer/getaddresscities/region/%s/";
    public static final String USER_REGISTRATION_API_PATH = "form_submit::customer/create/";

    public static final String GET_ADDRESS_POST_CODES_API_PATH = "form_field_call::customer/getaddresspostcodes/city_id/%s/";
    public static final String CREATE_ADDRESS_API_PATH = "form_submit::customer/addresscreate/";
}
