package de.philippengel.android.freshstart.data;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import javax.inject.Inject;

import de.philippengel.android.freshstart.data.model.GithubUrls;
import de.philippengel.android.freshstart.requests.DatabindRequest;
import de.philippengel.android.freshstart.requests.ResponseListener;
import de.philippengel.android.freshstart.util.PLog;


/**
 * @author Philipp Engel <philipp@filzip.com>
 */
public class Session extends Endpoint implements ResponseListener<GithubUrls> {

    private String authToken;

    @Inject
    public Session(RequestQueue queue) {
        super(queue);
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(authToken);
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
    
    public void login(String username, String password) {
        DatabindRequest<GithubUrls> urlsRq = DatabindRequest.get("https://api.github.com/",
                GithubUrls.class, this);
        getQueue().add(urlsRq);
    }
    
    @Override
    public void onSuccess(GithubUrls urls) {
        PLog.d(this, "urls!");
        PLog.d(this, "currentuserurl: " + urls.getCurrentUserUrl());
    }
    
    @Override
    public void onError(VolleyError error) {
        PLog.d(this, "urls - ERROR");
    }
}
