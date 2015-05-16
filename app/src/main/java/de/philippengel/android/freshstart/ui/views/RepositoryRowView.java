package de.philippengel.android.freshstart.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.philippengel.android.freshstart.FreshStartApp;
import de.philippengel.android.freshstart.R;
import de.philippengel.android.freshstart.data.model.Repository;

/**
 * @author Philipp Engel <philipp@filzip.com>
 */
public class RepositoryRowView extends RelativeLayout {
    
    @InjectView(R.id.image)
    ImageView image;
    @InjectView(R.id.title)
    TextView titleView;
    @InjectView(R.id.desc)
    TextView descView;
    @InjectView(R.id.facts)
    TextView factsView;
    
    @Inject
    Picasso picasso;
    private Repository repository;
    
    public RepositoryRowView(Context context) {
        super(context);
    }
    
    public RepositoryRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public RepositoryRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        FreshStartApp.get(getContext()).getObjectGraph().inject(this);
        ((FreshStartApp)getContext().getApplicationContext()).getAppComponent().inject(this);
        ButterKnife.inject(this);
    }
    
    public void setRepository(Repository repository) {
        this.repository = repository;
        updateAppearance();
    }
    
    private void updateAppearance() {
        picasso.load(repository.getOwner().getAvatarUrl()).into(image);
        titleView.setText(repository.getFullName());
        descView.setText(repository.getName());
        factsView.setText("Stars: " + repository.getStargazersCount());
    }
}
