package de.philippengel.android.freshstart.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Created by philipp on 13.03.15.
 */
public class Storage {

    private final Context context;
    private final ObjectMapper mapper;
    private SharedPreferences spSettings;

    public Storage(@NonNull Context context, ObjectMapper customMapper) {
        if (context == null)
            throw new IllegalArgumentException("Context can't be null");
        this.context = context.getApplicationContext();
        mapper = customMapper != null ? customMapper : new ObjectMapper();
    }

    public Storage(@NonNull Context context) {
        this(context, null);
    }

    /**
     * Clears all the settings from the underlying SharedPreferences. Be careful! Can't be undone!
     */
    public void clearAll() {
        SharedPreferences settings = getSettings();
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }

    private void storeBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private void storeString(String key, String value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void storeLong(String key, Long value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putLong(key, value);
        editor.commit();
    }

    private SharedPreferences getSettings() {
        if (spSettings == null) {
            spSettings = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        }
        return spSettings;
    }

    private <T> T loadObjectFromJson(String key, Class<T> clazz) {
        return loadObjectFromJson(key, clazz, null);
    }

    private <T> T loadObjectFromJson(String key, JavaType javaType) {
        return loadObjectFromJson(key, null, javaType);
    }

    private <T> T loadObjectFromJson(String key, Class<T> clazz, JavaType javaType) {
        try {
            String jsonString = getSettings().getString(key, null);
            if (TextUtils.isEmpty(jsonString)) {
                return null;
            } else {
                if (clazz != null) {
                    return mapper.readValue(jsonString, clazz);
                } else {
                    return mapper.readValue(jsonString, javaType);
                }
            }
        } catch (JsonProcessingException e) {
            PLog.e(this, "Error loading root " + key + ", problem reading json: " + e.getMessage(), e);
        } catch (IOException e) {
            PLog.e(this, "Error loading " + key + ": " + e.getMessage(), e);
        }

        return null;
    }

    private void storeAsJson(String key, Object data) {
        try {
            String dataJson = mapper.writeValueAsString(data);
            storeString(key, dataJson);
        } catch (JsonProcessingException e) {
            PLog.e(this, "Error storing " + key + ", problem creating json: " + e.getMessage(), e);
        }
    }

}
