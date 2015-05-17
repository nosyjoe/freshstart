package de.philippengel.android.freshstart.ui;


import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.internal.http.HttpMethod;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.philippengel.android.freshstart.FreshStartApp;
import de.philippengel.android.freshstart.R;
import de.philippengel.android.freshstart.data.GithubController;
import de.philippengel.android.freshstart.data.Session;
import de.philippengel.android.freshstart.data.model.RepositorySearchResponse;
import de.philippengel.android.freshstart.requests.DatabindRequest;
import de.philippengel.android.freshstart.requests.ResponseListener;
import de.philippengel.android.freshstart.ui.adapters.RepositoriesAdapter;
import de.philippengel.android.freshstart.util.PLog;

public class MainActivity extends Activity implements ResponseListener<RepositorySearchResponse> {

    @Inject
    Session session;
    @Inject
    GithubController githubController;
    
    @InjectView(R.id.list)
    ListView list;
    private RepositoriesAdapter repositoriesAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((FreshStartApp)getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        
        repositoriesAdapter = new RepositoriesAdapter(this);
        list.setAdapter(repositoriesAdapter);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    
        githubController.loadRepositories(this);
    }
    
    @Override
    public void onSuccess(RepositorySearchResponse response) {
        PLog.i(this, "Successfully loaded repos, total count: " + response.getTotalCount());
        repositoriesAdapter.clear();
        repositoriesAdapter.addAll(response.getRepositories());
    }
    
    @Override
    public void onError(VolleyError error) {
        PLog.e(this, "Error loading repositories: " + error);
    }
}
