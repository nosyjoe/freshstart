package de.philippengel.android.freshstart;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import de.philippengel.android.freshstart.dagger.BaseModule;
import de.philippengel.android.freshstart.util.PLog;

/**
 * Created by philipp on 13.03.15.
 */
public class FreshStartApp extends Application {
    
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .baseModule(new BaseModule(this))
                .build();
    
        PLog.setDebuggable(true);
        PLog.setLogTag("FreshStart");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


//    public ObjectGraph getObjectGraph() {
//        return objectGraph;
//    }

//    public static FreshStartApp get(Context context) {
//        return (FreshStartApp) context.getApplicationContext();
//    }
    
    
    public AppComponent getAppComponent() {
        return appComponent;
    }
}
