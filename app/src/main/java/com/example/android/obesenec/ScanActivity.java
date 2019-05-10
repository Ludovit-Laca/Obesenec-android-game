package com.example.android.obesenec;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class ScanActivity extends AppCompatActivity {

    private BluetoothAdapter BA;
    private ArrayList<String> zoznam = new ArrayList();
    private ArrayList<BluetoothDevice> zoznamDev = new ArrayList();
    private ArrayAdapter adapter;

    private Typeface custom_font;
    private Button scanBtn;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                showToast("Začínam hľadať ... ");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                showToast("... Koniec vyhľadávania");
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                showToast("Nájdené");
                zoznam.add(device.getName());
                zoznamDev.add(device);
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        nastavViews();
        pripojAdapter();
        pripojOnItemClickListener();
    }

    // nastaví View a font
    private void nastavViews() {
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/KGBlankSpaceSketch.ttf");
        scanBtn = (Button) findViewById(R.id.button5);
        scanBtn.setTypeface(custom_font);
    }

    // vytvorí bond
    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            showToast("Chyba!");
        }
    }

    // po kliknutí na nájdené zariadenie zavolá metodu pairDevice
    private void pripojOnItemClickListener() {
        AdapterView.OnItemClickListener mMessageClickedHandler =
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView parent,
                                            View v,
                                            int position,
                                            long id) {
                        BA.cancelDiscovery();
                        pairDevice(zoznamDev.get(position));
                    }
                };
        ListView lv = (ListView) findViewById(R.id.listView3);
        lv.setOnItemClickListener(mMessageClickedHandler);
    }

    // pripoji adapter, nastavi font a prepoji s ListView
    private void pripojAdapter() {
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, zoznam) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView item = (TextView) super.getView(position, convertView, parent);
                item.setTypeface(custom_font);
                item.setTextColor(Color.parseColor("#FFFFFF"));
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                return item;
            }
        };
        ListView lv = (ListView) findViewById(R.id.listView3);
        lv.setAdapter(adapter);
    }

    public void skenujOkolieClick(View view) {
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, filter);
        BA = BluetoothAdapter.getDefaultAdapter();
        BA.startDiscovery();
    }

    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    // toast
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
