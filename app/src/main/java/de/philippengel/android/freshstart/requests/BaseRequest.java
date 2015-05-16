package de.philippengel.android.freshstart.requests;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonRequest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Philipp Engel <philipp@filzip.com>
 */
public abstract class BaseRequest<T> extends JsonRequest<T> implements Serializable {
    /**
     * Charset for request.
     */
    protected static final String PROTOCOL_CHARSET = "utf-8";

    public static final int INITIAL_TIMEOUT_MS = 10000;
    public static final int MAX_NUM_RETRIES = 2;
    public static final float BACKOFF_MULTIPLIER = 1.f;

    private static final Map<String, String> DEFAULT_HEADER = new HashMap<String, String>(10);

    static {
        DEFAULT_HEADER.put(Constants.ACCEPT_HEADER, "application/json");
        DEFAULT_HEADER.put(Constants.ACCEPT_LANGUAGE_HEADER, Locale.getDefault().getLanguage());
        DEFAULT_HEADER.put(Constants.ACCEPT_ENCODING_HEADER, "gzip");
    }

    private final Map<String, String> headers = new HashMap<String, String>(DEFAULT_HEADER);

    public BaseRequest(int method, String url, String requestBody, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);



        if (headers != null)
            this.headers.putAll(headers);

        this.headers.put(Constants.USER_AGENT_HEADER, "freshstart");

        if (method == Method.GET)
            setRetryPolicy(new DefaultRetryPolicy(INITIAL_TIMEOUT_MS, MAX_NUM_RETRIES, BACKOFF_MULTIPLIER));
        else
            setRetryPolicy(new DefaultRetryPolicy(INITIAL_TIMEOUT_MS, 0, 1.f));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

}

