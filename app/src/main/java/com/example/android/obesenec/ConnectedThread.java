package com.example.android.obesenec;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ConnectedThread extends Thread {

    private BluetoothSocket socket;
    private InputStream inStream;
    private OutputStream outStream;
    private final Handler mHandler;

    public ConnectedThread(Handler ih) {
        mHandler = ih;
    }

    public void setSocket(BluetoothSocket s) {
        socket = s;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e("", "temp sockets not created", e);
        }
        inStream = tmpIn;
        outStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;
        // Keep listening to the InputStream while connected
        while (true) {
            try {
                // Read from the InputStream
                bytes = inStream.read(buffer);
                // Send the obtained bytes to the UI Activity
                mHandler.obtainMessage(NewGame.MESSAGE_READ, bytes, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e("", "disconnected", e);
                break;
            }
        }
    }

    /**
     * Write to the connected OutStream.
     *
     * @param buffer The bytes to write
     */
    public void write(byte[] buffer) {
        try {
            outStream.write(buffer);
            // Share the sent message back to the UI Activity
            mHandler.obtainMessage(NewGame.MESSAGE_WRITE, -1, -1, buffer)
                    .sendToTarget();
        } catch (IOException e) {
            Log.e("", "Exception during write", e);
        }
    }

    public void cancel() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            Log.e("", "close() of connect socket failed", e);
        }
    }
}