package crawler.util;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public final class HeaderUtil {

    private static final HeaderUtil INSTANCE = new HeaderUtil();

    /**
     * Create a new utils.
     */
    private HeaderUtil() {}

    /**
     * Get the instance.
     *
     * @return the singleton instance.
     */
    public static HeaderUtil getInstance() {
        return INSTANCE;
    }

    /**
     * Create headers from a string.
     *
     * @param headersAndValues by the header1:value1,header2:value2...
     * @return the Headers as a Set
     */
    public Map<String, String> createHeadersFromString(String headersAndValues) {

        if (headersAndValues == null || headersAndValues.isEmpty()) return Collections.emptyMap();

        final StringTokenizer token = new StringTokenizer(headersAndValues, "\n");

        final Map<String, String> theHeaders = new HashMap<String, String>(token.countTokens());

        while (token.hasMoreTokens()) {
            final String headerAndValue = token.nextToken();
            if (!headerAndValue.contains(":"))
                throw new IllegalArgumentException(
                        "Request headers wrongly configured, missing separator :" + headersAndValues);

            final String header = headerAndValue.substring(0, headerAndValue.indexOf(":"));
            final String value =
                    headerAndValue.substring(headerAndValue.indexOf(":") + 1, headerAndValue.length());
            theHeaders.put(header, value);
        }

        return theHeaders;
    }
}
