/*
 * Scout24 Services GmbH, All rights reserved. Use is subject to license terms.
 */
package de.philippengel.android.freshstart.requests;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.squareup.okhttp.internal.Util;

import org.apache.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import de.philippengel.android.freshstart.util.Tools;

/**
 * @author Philipp Engel <philipp@filzip.com>
 */
public class DatabindRequest<T> extends BaseRequest<T> {
    private static final String TAG = DatabindRequest.class.getSimpleName();

    protected static final ObjectMapper defaultMapper;
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private final JavaType javaType;

    protected ObjectMapper mapper;
    protected Object bodyData;

    static {
        defaultMapper = createObjectMapperInstance();
    }

    private Class<?> bodyJsonView;

    public DatabindRequest(int method, String url, JavaType javaType,
                            Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, null, null, listener, errorListener);
        this.javaType = javaType;
        this.bodyJsonView = JsonViews.Transmit.class;
    }

    public static <T> DatabindRequest<T> get(String url, Class<T> clazz,
                                             Response.Listener<T> listener, Response.ErrorListener errorListener) {
        JavaType javaType = TypeFactory.defaultInstance().uncheckedSimpleType(clazz);
        return new DatabindRequest<T>(Method.GET, url, javaType, listener, errorListener);
    }

    public static <T> DatabindRequest<T> put(String url, Class<T> clazz,
                                             Response.Listener<T> listener, Response.ErrorListener errorListener) {
        JavaType javaType = TypeFactory.defaultInstance().uncheckedSimpleType(clazz);
        return new DatabindRequest<T>(Method.PUT, url, javaType, listener, errorListener);
    }

    /**
     * Creates a POST Jackson-Databind reuqest. It automatically converts the bodyData object to Json if it is not null
     * and not a String. String values will be passed directly as body value.
     *
     * @param url           the url to post to
     * @param bodyData      the body to send with the post. A string will be passed directly, non-null objects will be
     *                      converted to JSON first
     * @param clazz         the result class type
     * @param listener      the listener to receive the successful result
     * @param errorListener the listener to be called in case of an error
     * @param <T>           the type of the result in the success case
     * @return the request instance
     */
    public static <T> DatabindRequest<T> post(String url, Class<T> clazz,
                                              Response.Listener<T> listener, Response.ErrorListener errorListener) {
        JavaType javaType = TypeFactory.defaultInstance().uncheckedSimpleType(clazz);
        return new DatabindRequest<T>(Method.POST, url, javaType, listener, errorListener);
    }

    /**
     * Creates a DELETE Jackson-Databind reuqest. It automatically converts the bodyData object to Json if it is not null
     * and not a String. String values will be passed directly as body value.
     *
     * @param url           the url to post to
     * @param bodyData      the body to send with the post. A string will be passed directly, non-null objects will be
     *                      converted to JSON first
     * @param clazz         the result class type
     * @param listener      the listener to receive the successful result
     * @param errorListener the listener to be called in case of an error
     * @param <T>           the type of the result in the success case
     * @return the request instance
     */
    public static <T> DatabindRequest<T> delete(String url, Class<T> clazz,
                                                Response.Listener<T> listener, Response.ErrorListener errorListener) {
        JavaType javaType = TypeFactory.defaultInstance().uncheckedSimpleType(clazz);
        return new DatabindRequest<T>(Method.DELETE, url, javaType, listener, errorListener);
    }

    public void setBodyData(Object bodyData) {
        this.bodyData = bodyData;
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

    @Override
    public void deliverError(VolleyError error) {
//        logRequest();

        int statusCode = error.networkResponse != null ? error.networkResponse.statusCode : -1;
        if (statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY || statusCode == HttpStatus.SC_BAD_REQUEST) {
//            ErrorResponse errorResponse = tryToDecodeError(error.networkResponse);
//            if (errorResponse != null) {
//                Log.d(TAG, "Decoded API error data: " + errorResponse.toString());
//                super.deliverError(new ApiError(error, errorResponse));
//            } else {
                super.deliverError(error);
//            }
        } else {
            super.deliverError(error);
        }
    }

//    private ErrorResponse tryToDecodeError(NetworkResponse response) {
//        try {
//            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
////            Log.d(TAG, "json response: " + jsonString);
//            return getMapper().readValue(jsonString, ErrorResponse.class);
//        } catch (Exception e) {
//            Log.d(TAG, "Error parsing error; " + e + ": " + e.getMessage(), e);
//            return null;
//        }
//    }

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

}
