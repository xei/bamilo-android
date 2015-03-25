package com.mobile.framework;

/**
 * Error codes used in the response events.
 * 
 * @author GuilhermeSilva, Ralph Holland-Moritz
 * 
 */
public enum ErrorCode {

	NO_ERROR(0),
	UNKNOWN_ERROR(-1), 
	NO_NETWORK(-2),
	CONNECT_ERROR(-3), 
	TIME_OUT(-4),
	ERROR_PARSING_SERVER_DATA(-5), 
	HTTP_PROTOCOL(-6), 
	IO(-7),
	SSL(-443),
	EMPTY_ENTITY(-8), 
	HTTP_STATUS(-9), 
	REQUEST_ERROR(-10),
	AUTO_COUNTRY_SELECTION(-11),
	INTERNAL_ERROR(-101), 
	REQUIRES_USER_INTERACTION(-201), 
	SERVER_IN_MAINTENANCE(-503), 
	NO_COUNTRIES_CONFIGS(-12),
	NO_COUNTRY_CONFIGS_AVAILABLE(-13); 
	

	public final int id;

	ErrorCode(int id) {
		this.id = id;
	}

	public static final ErrorCode byId(int id) {
		if (id > 0)
			return HTTP_STATUS;
		for (ErrorCode code : ErrorCode.values()) {
			if (id == code.id)
				return code;
		}
		return UNKNOWN_ERROR;
	}

	public boolean isNetworkError() {
		return isNetworkError(this);
	}

	public static boolean isNetworkError(ErrorCode error) {
		switch (error) {
		case NO_NETWORK:
		case SERVER_IN_MAINTENANCE:
		case CONNECT_ERROR:
		case TIME_OUT:
		case SSL:
		case IO:
		case HTTP_STATUS:
            return true;
        case AUTO_COUNTRY_SELECTION:
        case EMPTY_ENTITY:
        case ERROR_PARSING_SERVER_DATA:
        case HTTP_PROTOCOL:
        case INTERNAL_ERROR:
        case NO_COUNTRIES_CONFIGS:
        case NO_COUNTRY_CONFIGS_AVAILABLE:
        case NO_ERROR:
        case REQUEST_ERROR:
        case REQUIRES_USER_INTERACTION:
        case UNKNOWN_ERROR:
        default:
            return false;
		}
	}

	public boolean isClientError() {
		if (this == INTERNAL_ERROR || this == UNKNOWN_ERROR) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isServerError() {
		if ( this == ERROR_PARSING_SERVER_DATA ||
				this == HTTP_PROTOCOL ) {
 			return true;
		} else {
			return false;
		}
	}
}
