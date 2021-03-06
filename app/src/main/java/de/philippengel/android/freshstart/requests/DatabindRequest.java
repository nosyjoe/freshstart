/*
 * Scout24 Services GmbH, All rights reserved. Use is subject to license terms.
 */
package de.philippengel.android.freshstart.requests;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

/**
 * @author Philipp Engel <philipp@filzip.com>
 */
public class DatabindRequest<T> extends JsonRequest<T> {
    private static final String TAG = DatabindRequest.class.getSimpleName();
    
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
        defaultMapper = createObjectMapperInstance();
    }
    
    private final Map<String, String> headers = new HashMap<String, String>(DEFAULT_HEADER);
    protected static final ObjectMapper defaultMapper;
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private final JavaType javaType;
    protected ObjectMapper mapper;
    protected Object bodyData;
    private Class<?> bodyJsonView;
    private ResponseListener<T> responseListener;

    protected DatabindRequest(int method, String url, JavaType javaType) {
        super(method, url, null, null, null);
        this.headers.put(Constants.USER_AGENT_HEADER, "freshstart");
    
        if (method == Method.GET)
            setRetryPolicy(new DefaultRetryPolicy(INITIAL_TIMEOUT_MS, MAX_NUM_RETRIES, BACKOFF_MULTIPLIER));
        else
            setRetryPolicy(new DefaultRetryPolicy(INITIAL_TIMEOUT_MS, 0, 1.f));
        this.javaType = javaType;
        this.bodyJsonView = JsonViews.Transmit.class;
    }

    public static <T> DatabindRequest<T> get(String url, Class<T> clazz, ResponseListener<T> listener) {
        Builder<T> builder = new Builder<T>(Method.GET, url, clazz);
        builder.setListener(listener);
        return builder.build();
    }

    public static <T> DatabindRequest<T> put(String url, Class<T> clazz, Object bodyData, ResponseListener<T> listener) {
        Builder<T> builder = new Builder<T>(Method.PUT, url, clazz);
        builder.setListener(listener);
        builder.setBody(bodyData);
        return builder.build();
    }

    /**
     * Creates a POST Jackson-Databind reuqest. It automatically converts the bodyData object to Json if it is not null
     * and not a String. String values will be passed directly as body value.
     *
     * @param url           the url to post to
     * @param bodyData      the body to send with the post. A string will be passed directly, non-null objects will be
     *                      converted to JSON first
     * @param clazz         the result class type
     * @param listener      the responseListener to receive the result
     * @param <T>           the type of the result in the success case
     * @return the request instance
     */
    public static <T> DatabindRequest<T> post(String url, Class<T> clazz, Object bodyData, ResponseListener<T> listener) {
        Builder<T> builder = new Builder<T>(Method.POST, url, clazz);
        builder.setListener(listener);
        builder.setBody(bodyData);
        return builder.build();
    }

    /**
     * Creates a DELETE Jackson-Databind reuqest. It automatically converts the bodyData object to Json if it is not null
     * and not a String. String values will be passed directly as body value.
     *
     * @param url           the url to post to
     * @param clazz         the result class type
     * @param listener      the responseListener to receive the successful result
     * @param <T>           the type of the result in the success case
     * @return the request instance
     */
    public static <T> DatabindRequest<T> delete(String url, Class<T> clazz, ResponseListener<T> listener) {
        Builder<T> builder = new Builder<T>(Method.POST, url, clazz);
        builder.setListener(listener);
        return builder.build();
    }
    
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }
    
    @Override
    protected void deliverResponse(T response) {
        if (this.responseListener != null) {
            responseListener.onSuccess(response);
        }
    }
    
    @Override
    public void deliverError(VolleyError error) {
//        logRequest();
        
        if (this.responseListener != null) {
            responseListener.onError(error);
        }
    }
    
    @Override
    public byte[] getBody() {
        if (bodyData instanceof byte[]) {
            return (byte[]) bodyData;
        } else {
            String objectString = bodyData != null ? objectAsJson(bodyData) : null;
            try {
                return objectString != null ? objectString.getBytes(PROTOCOL_CHARSET) : EMPTY_BYTE_ARRAY;
            } catch (UnsupportedEncodingException uee) {
                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                        objectString, PROTOCOL_CHARSET);
                return null;
            }
        }
    }
    
    public void setListener(ResponseListener<T> listener) {
        this.responseListener = listener;
    }
    
    public void addAllHeaders(Map<? extends String, ? extends String> map) {
        headers.putAll(map);
    }
    
    public String addHeader(String key, String value) {
        return headers.put(key, value);
    }
    
    public void setBodyData(Object bodyData) {
        this.bodyData = bodyData;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
//        logRequest();

        try {
            String charset = HttpHeaderParser.parseCharset(response.headers);
            String contentEncoding = response.headers.get("Content-Encoding");

            String jsonString;

            if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
                InputStream stream = new ByteArrayInputStream(response.data);
                stream = new GZIPInputStream(stream);
                // https://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
                jsonString = new Scanner(stream, charset).useDelimiter("\\A").next();
            } else {
                jsonString = new String(response.data, charset);
            }

            T value;
            if (jsonString.trim().length() == 0) {
                value = null;
            } else {
                value = getMapper().readValue(jsonString, javaType);
            }

            return Response.success(value, HttpHeaderParser.parseCacheHeaders(response));
        } catch (JsonParseException e) {
            logError(e);
            return Response.error(new ParseError(e));
        } catch (JsonMappingException e) {
            logError(e);
            return Response.error(new ParseError(e));
        } catch (IOException e) {
            logError(e);
            return Response.error(new NetworkError(e));
        }
    }

//    private void logRequest() {
//        if (Tools.isDebuggable(app)) {
//            String method = "UNKNOWN";
//            switch (getMethod()) {
//                case Method.DELETE:
//                    method = "DELETE";
//                    break;
//                case Method.PATCH:
//                    method = "PATCH";
//                    break;
//                case Method.PUT:
//                    method = "PUT";
//                    break;
//                case Method.GET:
//                    method = "GET";
//                    break;
//                case Method.POST:
//                    method = "POST";
//                    break;
//            }
//
//            String body = getBody() != null ? new String(getBody()) : "null";
//            Log.d(TAG, "Request " + method + " " + getUrl() + " body: " + body);
//        }
//    }

    private void logError(Exception e) {
        Log.e(TAG, e.getClass().getSimpleName() + " while processing response: " + e.getMessage(), e);
    }

    protected String objectAsJson(Object toConvert) {
        if (toConvert == null || toConvert instanceof String) {
            return null;
        } else {
            try {
                if (bodyJsonView != null) {
                    return getMapper().writerWithView(bodyJsonView).writeValueAsString(toConvert);
                } else {
                    return getMapper().writeValueAsString(toConvert);
                }
            } catch (JsonProcessingException e) {
                Log.e(TAG, "Error serializing body to string: " + e.getMessage(), e);
                throw new IllegalArgumentException(e);
            }
        }
    }

    protected ObjectMapper getMapper() {
        return mapper != null ? mapper : defaultMapper;
    }

    public SerializationConfig getSerializationConfig() {
        createMapperIfNeccessary();
        return mapper.getSerializationConfig();
    }

    public DeserializationConfig getDeserializationConfig() {
        createMapperIfNeccessary();
        return mapper.getDeserializationConfig();
    }

    public void setSerializationInclusion(JsonInclude.Include include) {
        createMapperIfNeccessary();
        mapper.setSerializationInclusion(include);
    }

    public void withBodyJsonView(Class<?> view) {
        createMapperIfNeccessary();
        this.bodyJsonView = view;
    }

    private void createMapperIfNeccessary() {
        if (mapper == null) {
            mapper = createObjectMapperInstance();
        }
    }

    private static ObjectMapper createObjectMapperInstance() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDateFormat(new ISO8601DateFormat());
        objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        return objectMapper;
    }

    public JavaType getJavaType() {
        return javaType;
    }
    
    public static final class Builder<T> {
        private final int method;
        private final String url;
        private final JavaType javaType;
        private final HashMap<String, String> headers;
        private Object body;
        private ResponseListener<T> listener;
        
        public Builder(int method, String url, JavaType javaType) {
            this.method = method;
            this.url = url;
            this.javaType = javaType;
            this.headers = new HashMap<String, String>();
        }
        
        public Builder(int method, String url, Class<T> clazz) {
            this(method, url, TypeFactory.defaultInstance().uncheckedSimpleType(clazz));
        }
        
        public Builder<T> setBody(Object body) {
            this.body = body;
            return this;
        }
        
        public Builder<T> addHeader(String name, String value) {
            headers.put(name, value);
            return this;
        }
        
        public Builder<T> setListener(ResponseListener<T> listener) {
            this.listener = listener;
            return this;
        }
        
        public DatabindRequest<T> build() {
            if (listener == null) {
                throw new IllegalStateException("Listener must be set ");
            }
            
            DatabindRequest<T> req = new DatabindRequest<T>(method, url, javaType);
            req.setListener(listener);
            
            if (body != null) {
                req.setBodyData(body);
            }
            req.addAllHeaders(headers);
            return req;
        }
        
    }

}
