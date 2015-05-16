package de.philippengel.android.freshstart.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.philippengel.android.freshstart.util.OkHttpStack;

/**
 * @author Philipp Engel <philipp@filzip.com>
 */
@Module(
        includes = ApiModule.class
)
public class DataModule {

    private static final String SHARED_PREFS_FILE = "fresh_start_prefs";
    
    @Provides @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient();
    }
    
    @Provides @Singleton
    HttpStack provideOkHttpStack(OkHttpClient client) {
        return new OkHttpStack(client);
    }

    @Provides @Singleton
    SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
    }

    @Provides @Singleton
    RequestQueue provideRequestQueue(Application app, HttpStack stack) {
        return Volley.newRequestQueue(app, stack);
    }
    
    @Provides @Singleton
    Picasso providePicasso(Application app, OkHttpClient client) {
        return new Picasso.Builder(app).downloader(new OkHttpDownloader(client)).build();
    }
    
}
