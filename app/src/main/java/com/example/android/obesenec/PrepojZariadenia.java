package com.example.android.obesenec;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PrepojZariadenia extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    private Typeface custom_font;
    private TextView mPairedTv;
    private ImageView mBlueIv;
    private Button mOnBtn, mOffBtn, mDiscoverBtn, mPairedBtn, mscanBtn;

    private BluetoothAdapter mBlueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepoj_zariadenia);
        setTitle(R.string.prepoj_zariadenia);

        nastavViews();
        nastavListeneri();
    }

    // nastavi id, image atd
    private void nastavViews() {
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/KGBlankSpaceSketch.ttf");

        mPairedTv = findViewById(R.id.pairedTv);
        mPairedTv.setTypeface(custom_font);
        mBlueIv = findViewById(R.id.bluetoothIv);
        mOnBtn = findViewById(R.id.onBtn);
        mOnBtn.setTypeface(custom_font);
        mOffBtn = findViewById(R.id.offBtn);
        mOffBtn.setTypeface(custom_font);
        mDiscoverBtn = findViewById(R.id.discoverableBtn);
        mDiscoverBtn.setTypeface(custom_font);
        mPairedBtn = findViewById(R.id.pairedBtn);
        mPairedBtn.setTypeface(custom_font);
        mscanBtn = findViewById(R.id.scanBtn);
        mscanBtn.setTypeface(custom_font);

        // adapter
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();

        // zisti či je bluetooth je k dispozícii
        if (mBlueAdapter == null) {
            showToast("Bluetooth nie je k dispozícii");
        } else {
            showToast("Bluetooth je k dispozícii");
        }

        // nastavi obrazok podla statusu (on/off)
        if (mBlueAdapter.isEnabled()) {
            mBlueIv.setImageResource(R.drawable.bluetooth_on);
        } else {
            mBlueIv.setImageResource(R.drawable.bluetooth_off);
        }
    }

    // nastavi onClick listeneri
    private void nastavListeneri() {

        // button na zapnutie
        mOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBlueAdapter.isEnabled()) {
                    showToast("Zapínam bluetooth...");
                    // intent na zapnutie bluetooth
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                } else {
                    showToast("Bluetooth je už zapnutý");
                }

            }
        });

        // viditelnost button
        mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBlueAdapter.isDiscovering() && mBlueAdapter.isEnabled()) {
                    showToast("Zviditelňujem tvoje zariadenie");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_DISCOVER_BT);
                } else {
                    showToast("Zapnite bluetooth pre zviditeľnenie zariadenia");
                }
            }
        });

        // button na vypnutie
        mOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBlueAdapter.isEnabled()) {
                    mBlueAdapter.disable();
                    showToast("Vypinám bluetooth");
                    mBlueIv.setImageResource(R.drawable.bluetooth_off);
                } else {
                    showToast("Bluetooth je už vypnutý");
                }
            }
        });

        // ziskaj sparovane
        mPairedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBlueAdapter.isEnabled()) {
                    // spusti aktivitu DeviceListActivity
                    startActivity(new Intent(PrepojZariadenia.this, DeviceListActivity.class));
                } else {
                    // bluetooth je vypnutý takže nemôźe ziskať zriadenia
                    showToast("Zapnite bluetooth pre ziskanie sparovaných zariadení");
                }
            }
        });

        // otvor Activity na skenovanie
        mscanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBlueAdapter.isEnabled()) {
                    startActivity(new Intent(PrepojZariadenia.this, ScanActivity.class));
                } else {
                    showToast("Zapnite bluetooth pre hľadanie nových zariadení");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    // bluetooth je zapnuty
                    mBlueIv.setImageResource(R.drawable.bluetooth_on);
                    showToast("Bluetooth is on");
                } else {
                    // pouźivateľ odmietol zapnutie bluetooth
                    showToast("Nemohol som zapnúť bluetooth");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // toast
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
