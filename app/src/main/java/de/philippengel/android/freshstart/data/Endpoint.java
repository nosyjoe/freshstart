package de.philippengel.android.freshstart.data;

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
}
