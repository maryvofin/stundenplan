package de.maryvofin.stundenplan.app.modules.sharing;

import android.bluetooth.BluetoothDevice;

import java.util.List;

import de.maryvofin.stundenplan.app.database.Profile;

/**
 * Created by mark on 14.10.2015.
 */
public interface ProfileListReceivedListener {

    void profilesReceived(BluetoothDevice device, List<Profile> profiles);



}
