package de.philippengel.android.freshstart.data.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Philipp Engel <philipp@filzip.com>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RepositorySearchResponse{
    private boolean incomplete_results;
    private List<Repository> items;
    @JsonProperty
    private int total_count;
    
    public boolean getIncompleteResults(){
        return this.incomplete_results;
    }
    
    public List<Repository> getRepositories(){
        return this.items;
    }

    public Number getTotalCount(){
        return this.total_count;
    }

}

