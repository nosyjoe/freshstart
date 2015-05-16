package de.philippengel.android.freshstart.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.philippengel.android.freshstart.R;
import de.philippengel.android.freshstart.data.model.Repository;
import de.philippengel.android.freshstart.ui.views.RepositoryRowView;

/**
 * @author Philipp Engel <philipp@filzip.com>
 */
public class RepositoriesAdapter extends ArrayAdapter<Repository> {
    
    private final LayoutInflater inflater;
    
    public RepositoriesAdapter(Context context) {
        super(context, R.layout.repository_row);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RepositoryRowView view;
        if (convertView instanceof RepositoryRowView) {
            view = (RepositoryRowView) convertView;
        } else {
            view = (RepositoryRowView) inflater.inflate(R.layout.repository_row, parent, false);
        }
        
        view.setRepository(getItem(position));
        
        return view;
    }
}
