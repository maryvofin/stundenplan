package de.maryvofin.stundenplan.app;

import android.bluetooth.BluetoothSocket;

/**
 * Created by mark on 14.10.2015.
 */
public class BluetoothServiceWorker extends Thread {

    BluetoothService bluetoothService;
    BluetoothSocket bluetoothSocket;

    public BluetoothServiceWorker(BluetoothService bluetoothService, BluetoothSocket bluetoothSocket) {
        this.bluetoothService = bluetoothService;
        this.bluetoothSocket = bluetoothSocket;
    }

    @Override
    public void run() {
        bluetoothService.manageIncomingConnection(bluetoothSocket);
    }
}
