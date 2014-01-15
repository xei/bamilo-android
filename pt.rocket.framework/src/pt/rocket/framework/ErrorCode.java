package pt.rocket.framework;

/**
 * Error codes used in the response events.
 * 
 * @author GuilhermeSilva, Ralph Holland-Moritz
 * 
 */
public enum ErrorCode {

	NO_ERROR(0),

	UNKNOWN_ERROR(-1), NO_NETWORK(-2),

	CONNECT_ERROR(-3), TIME_OUT(-4),

	ERROR_PARSING_SERVER_DATA(-5), HTTP_PROTOCOL(-6), IO(-7), EMPTY_ENTITY(-8), HTTP_STATUS(-9), REQUEST_ERROR(-10), INTERNAL_ERROR(
			-101), REQUIRES_USER_INTERACTION(-201), SERVER_IN_MAINTENANCE(-503);

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
			return true;
		}
		return false;
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
