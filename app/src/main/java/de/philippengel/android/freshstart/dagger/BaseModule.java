package de.philippengel.android.freshstart.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.philippengel.android.freshstart.FreshStartApp;
import de.philippengel.android.freshstart.data.DataModule;
import de.philippengel.android.freshstart.ui.UiModule;

/**
 * Created by philipp on 17.03.15.
 */
@Module(
        includes = {
                DataModule.class,
                UiModule.class
        }
)
public final class BaseModule {

    private final FreshStartApp app;

    public BaseModule(FreshStartApp app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return app;
    }
}
