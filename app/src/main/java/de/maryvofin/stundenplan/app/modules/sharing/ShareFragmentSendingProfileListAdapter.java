package de.maryvofin.stundenplan.app.modules.sharing;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import de.maryvofin.stundenplan.app.utils.BluetoothService;
import de.maryvofin.stundenplan.app.R;
import de.maryvofin.stundenplan.app.database.Database;
import de.maryvofin.stundenplan.app.database.Profile;

/**
 * Created by mark on 14.10.2015.
 */
public class ShareFragmentSendingProfileListAdapter extends ArrayAdapter<Profile> {


    Activity activity;
    BluetoothService bluetoothService;

    public ShareFragmentSendingProfileListAdapter(Activity activity, BluetoothService bluetoothService) {
        super(activity, R.layout.view_share_sending_list_element,Database.getInstance().getProfiles().getProfiles());

        this.activity = activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        if(convertView == null) convertView = inflater.inflate(R.layout.view_share_sending_list_element,parent,false);


        CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.view_share_sending_list_element_checkbox);
        TextView text = (TextView)convertView.findViewById(R.id.view_share_sending_list_element_textview);

        checkBox.setChecked(Database.getInstance().getProfiles().getProfiles().get(position).isShareAllowed());
        text.setText(Database.getInstance().getProfiles().getProfiles().get(position).getName());

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Database.getInstance().getProfiles().getProfiles().get(position).setShareAllowed(isChecked);
                Database.getInstance().updateProfiles(activity);
            }
        });

        return convertView;
    }


}
