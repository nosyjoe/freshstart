package de.philippengel.android.freshstart.data;

import com.android.volley.RequestQueue;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Philipp Engel <philipp@filzip.com>
 */
@Module(
//        complete = false,
//        library = true
)
public class ApiModule {

    @Provides
    @Singleton
    Session provideSession(RequestQueue queue) {
        return new Session(queue);
    }
    
    @Provides
    GithubController provideGithubController(Session session) {
        return new GithubController(session);
    }

}
