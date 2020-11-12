package crawler.util;

import org.apache.http.HttpStatus;

/**
 * Specific status codes.
 *
 */
public enum StatusCode {

    SC_SERVER_RESPONSE_TIMEOUT(580, "Response timed out"), SC_SERVER_RESPONSE_UNKNOWN(581,
            "Unknown error"), SC_MALFORMED_URI(582, "Malformed url"), SC_WRONG_CONTENT_TYPE(583,
            "Wrong content type"),SC_SERVER_REDIRECT_TO_NEW_DOMAIN(308, "Redirected to new domain");

    private final int code;
    private final String friendlyName;

    StatusCode(int theCode, String theFriendlyName) {
        code = theCode;
        friendlyName = theFriendlyName;
    }

    public int getCode() {
        return code;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public static String toFriendlyName(int code) {
        for (StatusCode s : StatusCode.values()) {
            if (s.getCode() == code) return s.getFriendlyName();
        }
        return String.valueOf(code);
    }

    /**
     * Is a status code ok?
     *
     * @param responseCode the code
     * @return true if it is ok
     */
    public static boolean isResponseCodeOk(Integer responseCode) {

        if (responseCode >= HttpStatus.SC_BAD_REQUEST) return false;
        return true;
    }
}
