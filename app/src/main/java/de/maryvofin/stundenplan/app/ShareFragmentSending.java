package de.maryvofin.stundenplan.app;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import de.maryvofin.stundenplan.app.database.Database;

/**
 * Created by mark on 14.10.2015.
 */
public class ShareFragmentSending extends Fragment {

    BluetoothService bluetoothService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_sending, container, false);

        bluetoothService = new BluetoothService(getActivity());
        bluetoothService.startBluetoothAction();
        bluetoothService.enableDisover();
        bluetoothService.start();

        ShareFragmentSendingProfileListAdapter shareFragmentSendingProfileListAdapter = new ShareFragmentSendingProfileListAdapter(getActivity(), bluetoothService);
        ListView list = (ListView)view.findViewById(R.id.fragment_share_sending_list);
        list.setAdapter(shareFragmentSendingProfileListAdapter);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        bluetoothService.rejectConnections();
    }
}
