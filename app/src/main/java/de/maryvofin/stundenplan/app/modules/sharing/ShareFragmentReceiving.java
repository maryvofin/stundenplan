package de.maryvofin.stundenplan.app.modules.sharing;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

import de.maryvofin.stundenplan.app.utils.BluetoothService;
import de.maryvofin.stundenplan.app.R;
import de.maryvofin.stundenplan.app.database.Profile;

/**
 * Created by mark on 14.10.2015.
 */
public class ShareFragmentReceiving extends Fragment implements ProfileListReceivedListener {

    BluetoothService bluetoothService;
    List listEntries = new LinkedList();
    View view;
    ShareFragmentReceivingProfileListAdapter adapter;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_share_receiving, container, false);

            bluetoothService = new BluetoothService(getActivity());
            bluetoothService.searchProfiles();

            adapter = new ShareFragmentReceivingProfileListAdapter(getActivity(), listEntries);
            setAdapter();

            return view;
        }

    @Override
    public synchronized void profilesReceived(BluetoothDevice device, List<Profile> profiles) {
        listEntries.add(device);
        for(Profile p: profiles) listEntries.add(p);
        setAdapter();
    }

    public void setAdapter() {
        ((ListView)view.findViewById(R.id.fragment_share_receiving_list)).setAdapter(adapter);
    }
}
