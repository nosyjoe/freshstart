package de.philippengel.android.freshstart.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Philipp Engel <philipp@filzip.com>
 */
public class GithubUrls {

    @JsonProperty("current_user_url")
    private String currentUserUrl;

    @JsonProperty("repository_search_url")
    private String repositorySearchUrl;

    public String getCurrentUserUrl() {
        return currentUserUrl;
    }
    
    public String getRepositorySearchUrl() {
        return repositorySearchUrl;
    }
}
