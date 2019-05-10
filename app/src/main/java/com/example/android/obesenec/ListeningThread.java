package com.example.android.obesenec;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.util.UUID;

public class ListeningThread extends Thread {

    private final BluetoothServerSocket bluetoothServerSocket;
    private ConnectedThread comThread;
    private Handler mHandler;

    public ListeningThread(BluetoothAdapter BA, UUID uuid, ConnectedThread ci, Handler ih) {
        BluetoothServerSocket temp = null;
        comThread = ci;
        mHandler = ih;
        try {
            temp = BA.listenUsingRfcommWithServiceRecord("Obesenec", uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bluetoothServerSocket = temp;
    }

    public void run() {
        BluetoothSocket bluetoothSocket;
        // This will block while listening until a BluetoothSocket is returned
        // or an exception occurs
        while (true) {
            try {
                bluetoothSocket = bluetoothServerSocket.accept();
            } catch (IOException e) {
                break;
            }

            // If a connection is accepted
            if (bluetoothSocket != null) {
                // Manage the connection in a separate thread
                comThread.setSocket(bluetoothSocket);
                comThread.start();
                try {
                    bluetoothServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    // Cancel the listening socket and terminate the thread
    public void cancel() {
        try {
            bluetoothServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
