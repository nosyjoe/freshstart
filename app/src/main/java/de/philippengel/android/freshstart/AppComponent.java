package de.philippengel.android.freshstart;

import android.app.Activity;
import android.app.Application;
import android.view.View;

import javax.inject.Singleton;

import dagger.Component;
import de.philippengel.android.freshstart.dagger.BaseModule;
import de.philippengel.android.freshstart.ui.MainActivity;
import de.philippengel.android.freshstart.ui.views.RepositoryRowView;

/**
 * @author Philipp Engel <philipp@filzip.com>
 */
@Singleton
@Component(modules = BaseModule.class)
public interface AppComponent {
    void inject(FreshStartApp app);
    void inject(MainActivity activity);
    void inject(RepositoryRowView view);
}
