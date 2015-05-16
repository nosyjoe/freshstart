package de.philippengel.android.freshstart.data;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;

import de.philippengel.android.freshstart.data.model.RepositorySearchResponse;
import de.philippengel.android.freshstart.requests.DatabindRequest;

/**
 * @author Philipp Engel <philipp@filzip.com>
 */
public class GithubController {
    
    private final Session session;
    
    @Inject
    public GithubController(Session session) {
        this.session = session;
    }
    
    public void loadRepositories(Response.Listener<RepositorySearchResponse> successListener, Response.ErrorListener errorListener) {
        DatabindRequest<RepositorySearchResponse> repositorySearchResponseDatabindRequest =
                null;
        try {
            repositorySearchResponseDatabindRequest =
                    DatabindRequest.get("https://api.github.com/search/repositories?q=" +
                                    URLEncoder.encode("stars:>=10000", "utf-8") + "&sort=stars",
                            RepositorySearchResponse.class, successListener, errorListener);
            session.addToQueue(repositorySearchResponseDatabindRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
}
