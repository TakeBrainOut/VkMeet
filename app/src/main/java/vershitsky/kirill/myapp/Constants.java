package vershitsky.kirill.myapp;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Вершицкий on 02.04.2015.
 */
public final class Constants {
    public static final int SUCCESS_RESULT = 0;

    public static final int FAILURE_RESULT = 1;

    public static final String PACKAGE_NAME = "vershitsky.kirill.myapp";

    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    public static final String ADRESS_DATA_KEY = "ADRESS_KEY";

    public static final String APP_USER_KEY = "APP_USER_KEY";
    public static final String LOCALITY_EXTRA = "LOCALITY_EXTRA";
    public static final String USER_PHOTO = "USER_PHOTO";
    public static final String SEARCH_TYPE_EXTRA  = "SEARCH_TYPE_EXTRA";
    public static final String SEARCH_VIEW_URL_EXTRA = "SEARCH_VIEW_URL_EXTRA";

    public static final String COUNTRY = "COUNTRY";
    public static final String REGION = "REGION";
    public static final String LOCALITY = "LOCALITY";
    public static final String[] SEARCH_TYPE = {COUNTRY, REGION, LOCALITY};

    public static final String UNKNOWN = "UNKNOWN";

    public static String viewUrlRequest(String viewUrl, String... params){
        String fullViewURL = "";
        try {
            fullViewURL = viewUrl +  "?key=[" + URLEncoder.encode(TextUtils.join(",", getParamsInCommas(params)) + "]", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fullViewURL;
    }

    public static String[] getParamsInCommas(String[] params) {
        for (int i = 0; i < params.length; i++)
            params[i] = "\"" + params[i] + "\"";
        return params;
    }
}
