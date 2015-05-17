package de.philippengel.android.freshstart.requests;

import com.android.volley.VolleyError;

/**
 * @author Philipp Engel <philipp@filzip.com>
 */
public interface ResponseListener<T> {
    
    void onSuccess(T response);
    void onError(VolleyError error);
    
}
