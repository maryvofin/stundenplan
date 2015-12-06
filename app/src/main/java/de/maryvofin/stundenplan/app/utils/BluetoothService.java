package de.maryvofin.stundenplan.app.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.maryvofin.stundenplan.app.database.Database;
import de.maryvofin.stundenplan.app.database.Profile;
import de.maryvofin.stundenplan.app.modules.sharing.ProfileListReceivedListener;

/**
 * Created by mark on 14.10.2015.
 */
public class BluetoothService extends Thread{

    public final static int REQUEST_ENABLE_BT = 1;

    BluetoothAdapter bluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
    Activity activity;
    String serviceName = "Stundenplan";
    UUID serviceUUID = UUID.fromString("49446b76-7264-11e5-9d70-feff819cdc9f");
    BluetoothServerSocket bluetoothServerSocket = null;
    ProfileListReceivedListener profileListReceivedListener = null;


    public BluetoothService(Activity activity) {
        this.activity = activity;
    }

    public void setProfileListReceivedListener(ProfileListReceivedListener profileListReceivedListener) {
        this.profileListReceivedListener = profileListReceivedListener;
    }

    public boolean startBluetoothAction() {


        if(bluetoothAdapter == null) return false;

        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            activity.startActivityForResult(enableBluetooth,REQUEST_ENABLE_BT);
        }

        return true;
    }

    public void enableDisover() {
        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        activity.startActivityForResult(enableBluetooth,REQUEST_ENABLE_BT);
    }

    public void endBluetoothAction() {

    }

    public void acceptConnections() throws IOException {
        if(bluetoothServerSocket == null) {
            bluetoothServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(serviceName,serviceUUID);
        }

        BluetoothSocket bluetoothSocket = bluetoothServerSocket.accept();
        if(bluetoothSocket != null) {
            BluetoothServiceWorker bluetoothServiceWorker = new BluetoothServiceWorker(this,bluetoothSocket);
            bluetoothServiceWorker.start();
        }

    }

    public void manageIncomingConnection(BluetoothSocket socket) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            List<Profile> profiles = new LinkedList<>();
            for(Profile p: Database.getInstance().getProfiles().getProfiles()) {
                if(p.isShareAllowed()) profiles.add(p);
            }
            oos.writeObject(profiles);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rejectConnections() {
        if(bluetoothServerSocket != null) try {
            bluetoothServerSocket.close();
        } catch (IOException e) {

        }
    }

    public void searchProfiles() {
        if(bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        queryDevice(device);
                    }
                }
            };
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            activity.registerReceiver(broadcastReceiver,filter);
        }
    }

    void queryDevice(BluetoothDevice device) {
        try {
            BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(serviceUUID);
            socket.connect();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            List<Profile> receivedList = (List<Profile>)in.readObject();
            in.close();
            socket.close();
            if(profileListReceivedListener != null) profileListReceivedListener.profilesReceived(device,receivedList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            while(true) {
                acceptConnections();
            }
        } catch (IOException e) {

        }
    }
}
