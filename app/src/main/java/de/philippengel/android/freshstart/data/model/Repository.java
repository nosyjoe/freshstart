package de.philippengel.android.freshstart.data.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * @author Philipp Engel <philipp@filzip.com>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Repository {
    
    private Owner owner;
    private long id;
    private String name;
    private String fullName;
    private int stargazersCount;
    
    public Owner getOwner() {
        return owner;
    }
    
    public long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public int getStargazersCount() {
        return stargazersCount;
    }
}
