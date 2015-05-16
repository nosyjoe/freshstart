package de.philippengel.android.freshstart.data.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * @author Philipp Engel <philipp@filzip.com>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Owner {
    private String avatar_url;
    private String events_url;
    private String gravatar_id;
    private int id;
    private String login;
    private String repos_url;
    private boolean site_admin;
    private String type;
    private String url;
    
    public String getAvatarUrl(){
        return this.avatar_url;
    }

    public String getEventsUrl(){
        return this.events_url;
    }
    
    public String getGravatarId(){
        return this.gravatar_id;
    }

    public Number getId(){
        return this.id;
    }
    public String getLogin(){
        return this.login;
    }

    public String getReposUrl(){
        return this.repos_url;
    }
    
    public boolean getSiteAdmin(){
        return this.site_admin;
    }

    public String getType(){
        return this.type;
    }
    
    public String getUrl(){
        return this.url;
    }
}

