package com.example.android.obesenec;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class DeviceListActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private ArrayList<String> zoznam;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothAdapter BA;
    private TextView sparovaneTv;
    private Typeface custom_font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        BA = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = BA.getBondedDevices();

        nastavViews();
        UkazZoznam();
        pridajListener();
    }

    // nastaví views
    private void nastavViews() {
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/KGBlankSpaceSketch.ttf");
        sparovaneTv = findViewById(R.id.sparovaneTv);
        sparovaneTv.setTypeface(custom_font);
    }

    // vypíše zoznam paired zariadeni
    private void UkazZoznam() {
        zoznam = new ArrayList();
        for (BluetoothDevice bt : pairedDevices) {
            zoznam.add(bt.getAddress() + "(" + bt.getName() + ")");
        }
        adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                zoznam) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView item = (TextView) super.getView(position, convertView, parent);
                item.setTypeface(custom_font);
                item.setTextColor(Color.parseColor("#FFFFFF"));
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                return item;
            }
        };
        ListView lv = (ListView) findViewById(R.id.listview_sparovane);
        lv.setAdapter(adapter);
    }

    // pridá onItemClickListner a po kliknuti vráti zariadenie
    private void pridajListener() {
        AdapterView.OnItemClickListener mMessageClickedHandler =
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView parent, View v,
                                            int position, long id) {
                        Intent intentNavrat = new Intent();
                        intentNavrat.putExtra("device", zoznam.get(position).substring(0, 17));
                        setResult(RESULT_OK, intentNavrat);
                        finish();
                    }
                };
        ListView myList = (ListView) findViewById(R.id.listview_sparovane);
        myList.setOnItemClickListener(mMessageClickedHandler);
    }
}
