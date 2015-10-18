package de.maryvofin.stundenplan.app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import de.maryvofin.stundenplan.app.database.Database;
import de.maryvofin.stundenplan.app.database.Profile;

/**
 * Created by mark on 14.10.2015.
 */
public class ShareFragmentReceivingProfileListAdapter extends ArrayAdapter<Profile> {

    Activity activity;

    public ShareFragmentReceivingProfileListAdapter(Activity activity, List entries) {
        super(activity, R.layout.view_share_sending_list_element, entries);

        this.activity = activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        if (convertView == null) convertView = inflater.inflate(R.layout.view_share_sending_list_element, parent, false);



        return convertView;
    }
}
