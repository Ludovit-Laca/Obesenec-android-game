package com.example.android.obesenec;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.util.UUID;

class ConnectingThread extends Thread {

    private final BluetoothSocket bluetoothSocket;
    private final BluetoothDevice device2;
    private BluetoothAdapter BA;
    private ConnectedThread comThread;
    private Handler mHandler;

    public ConnectingThread(BluetoothAdapter iBA, BluetoothDevice dev2, UUID uuid, ConnectedThread ci, Handler ih) {
        BluetoothSocket temp = null;
        device2 = dev2;
        BA = iBA;
        comThread = ci;
        mHandler = ih;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            temp = dev2.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bluetoothSocket = temp;
    }

    public void run() {
        // Cancel any discovery as it will slow down the connection
        BA.cancelDiscovery();
        try {
            // This will block until it succeeds in connecting to the device
            // through the bluetoothSocket or throws an exception
            bluetoothSocket.connect();
        } catch (IOException connectException) {
            connectException.printStackTrace();
            try {
                bluetoothSocket.close();
            } catch (IOException closeException) {
                closeException.printStackTrace();
            }
        }

        // Code to manage the connection in a separate thread
        /*
            manageBluetoothConnection(bluetoothSocket);
        */
        comThread.setSocket(bluetoothSocket);
        comThread.start();
    }

    // Cancel an open connection and terminate the thread
    public void cancel() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
