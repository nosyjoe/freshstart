package de.philippengel.android.freshstart.data;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import javax.inject.Inject;

/**
 * @author Philipp Engel <philipp@filzip.com>
 */
public class Endpoint {

    private final RequestQueue queue;

    @Inject
    public Endpoint(RequestQueue queue) {
        this.queue = queue;
    }

    public RequestQueue getQueue() {
        return queue;
    }
    
    public <T> void addToQueue(Request<T> request) {
        Request<T> add = getQueue().add(request);
    }
    
    public void cancelAll(RequestQueue.RequestFilter filter) {
        getQueue().cancelAll(filter);
    }
    
    public void cancelAll(Object tag) {
        getQueue().cancelAll(tag);
    }
}
