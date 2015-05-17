package de.philippengel.android.freshstart.data;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;

import de.philippengel.android.freshstart.data.model.RepositorySearchResponse;
import de.philippengel.android.freshstart.requests.DatabindRequest;
import de.philippengel.android.freshstart.requests.ResponseListener;

/**
 * @author Philipp Engel <philipp@filzip.com>
 */
public class GithubController {
    
    private final Session session;
    
    @Inject
    public GithubController(Session session) {
        this.session = session;
    }
    
    public void loadRepositories(ResponseListener<RepositorySearchResponse> listener) {
        try {
            String url = "https://api.github.com/search/repositories?q=" +
                    URLEncoder.encode("stars:>=10000", "utf-8") + "&sort=stars";
            DatabindRequest<RepositorySearchResponse> req = DatabindRequest.get(url, RepositorySearchResponse.class, listener);
            session.addToQueue(req);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
}
