package com.example.android.obesenec;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.UUID;

public class NewGame extends AppCompatActivity {
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;

    public static final UUID MY_UUID = UUID.fromString("00112233-afac-1234-abcd-abcdef012345");

    private String hadane_slovo = "";
    private String pouzite_pismena = "";
    private int level = 0;
    private HashSet<String> list1;

    BluetoothDevice device2 = null;
    BluetoothAdapter BA;

    ListeningThread lt = null;
    ConnectingThread ct = null;
    ConnectedThread conThread = null;

    private Typeface custom_font;
    private TextView textView_hra;
    private TextView nespravnePismenaView;
    private Button zadajSlovo;
    private Button hadajSlovo;
    private Button koniecBtn;
    private EditText ed;
    private EditText edZadaj;
    private ImageView hangmanPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        BA = BluetoothAdapter.getDefaultAdapter();
        if (conThread != null) conThread.cancel();
        conThread = new ConnectedThread(mHandler);

        nastavViews();
    }

    // nastaví Views, fonts atď..
    private void nastavViews() {
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/KGBlankSpaceSketch.ttf");
        textView_hra = (TextView) findViewById(R.id.TextView_hra);
        textView_hra.setTypeface(custom_font);
        nespravnePismenaView = (TextView) findViewById(R.id.nespravnePismena);
        nespravnePismenaView.setTypeface(custom_font);
        zadajSlovo = (Button) findViewById(R.id.zadajSlovoBtn);
        zadajSlovo.setTypeface(custom_font);
        hadajSlovo = (Button) findViewById(R.id.button12);
        hadajSlovo.setTypeface(custom_font);
        ed = (EditText) findViewById(R.id.editText);
        ed.setTypeface(custom_font);
        edZadaj = (EditText) findViewById(R.id.editTextZadaj);
        edZadaj.setTypeface(custom_font);
        hangmanPic = (ImageView) findViewById(R.id.hangmanPic);
        list1 = new HashSet<String>();
        koniecBtn = (Button) findViewById(R.id.buttonNewGame);
        koniecBtn.setTypeface(custom_font);
    }

    // Handler
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TextView tv = (TextView) findViewById(R.id.TextView_hra);
            switch (msg.what) {
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);

                    if (hadane_slovo.equals("")) {
                        hadane_slovo = writeMessage;
                        textView_hra.setText(vypisNeznamu());
                    } else {
                        textView_hra.setText(skontrolujSpravu(writeMessage));
                    }
                    break;

                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    if (hadane_slovo.equals("")) {
                        hadane_slovo = readMessage;
                        textView_hra.setText(vypisNeznamu());
                    } else {
                        textView_hra.setText(skontrolujSpravu(readMessage));
                    }
                    break;
            }
        }
    };

    // vypíše _ podľa dĺžky hladaného slova
    public String vypisNeznamu() {
        String retazec = "";
        for (int i = 0; i < hadane_slovo.length(); i++) {
            retazec = retazec + " _ ";
        }
        return retazec;
    }

    // skontroluje spravu,
    public String skontrolujSpravu(String writeMessage) {
        String retazec = "";
        String pismeno = writeMessage;
        if (list1.contains(pismeno)) ;
        else {
            // ak pismeno sa v HashSete nenachádza... pridá ho do Stringu
            pouzite_pismena += writeMessage + ", ";
        }
        // pokiaľ je to pismeno čo sa nenachádza v hladanom slove
        if (!hadane_slovo.contains(pismeno)) {
            level++; // prida level
            nastavHangman(); // nastavi obrazok podla levelu
            // ak level == 8 .. hráč prehral
            if (level == 8) {
                koniecBtn.setVisibility(View.VISIBLE);
                zadajSlovo.setVisibility(View.GONE);
                hadajSlovo.setVisibility(View.GONE);
                return retazec = "Game Over!\nHladané slovo: " + hadane_slovo;
            }
        }
        // vypiše do TextView použiré pismena
        nespravnePismenaView.setText("Použité písmena: \n" + pouzite_pismena);
        // ak sa v HashSete nenachadza, tak ho tam prida
        list1.add(writeMessage);
        boolean trafil = false;
        // prechadza hladanym slovom a HashSetom, podla toho vypise pismeno alebo _
        for (int i = 0; i < hadane_slovo.length(); i++) {
            String znak = hadane_slovo.substring(i, i + 1);
            for (String s : list1) {
                if (znak.equals(s)) {
                    retazec = retazec + s;
                    trafil = true;
                    break;
                }
            }
            if (trafil == false) {
                retazec = retazec + " _ ";
            } else {
                trafil = false;
            }
        }
        // ak String retazec obsahuje hladane slovo, tak hrač vyhral
        if (retazec.equals(hadane_slovo)) {
            retazec = "Správne! : " + retazec;
            koniecBtn.setVisibility(View.VISIBLE);
            zadajSlovo.setVisibility(View.GONE);
            hadajSlovo.setVisibility(View.GONE);
        }
        return retazec;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 9:
                Bundle vrateneData = data.getExtras();
                String MAC = vrateneData.getString("device");
                device2 = BA.getRemoteDevice(MAC);
                // Initiate a connection request in a separate thread
                if (ct != null) ct.cancel();
                ct = new ConnectingThread(BluetoothAdapter.getDefaultAdapter(), device2, NewGame.MY_UUID, conThread, mHandler);
                ct.start();
                break;
        }
    }

    // odošle pismeno na kontrolu
    public void odosliMsgClick(View view) {
        String s = ed.getText().toString().toLowerCase().trim();
        ed.setText("");
        byte[] writeBuf = s.getBytes();
        if (conThread == null)
            Toast.makeText(this, "neni vlakno", Toast.LENGTH_SHORT).show();
        else
            conThread.write(writeBuf);
    }

    // Zadanie hádaneho slova
    public void zadajSlovo(View view) {
        String s = edZadaj.getText().toString().toLowerCase().trim();
        edZadaj.setText("");
        byte[] writeBuf = s.getBytes();
        if (conThread == null)
            Toast.makeText(this, "neni vlakno", Toast.LENGTH_SHORT).show();
        else {
            zadajSlovo.setVisibility(View.GONE);
            edZadaj.setVisibility(View.GONE);
            conThread.write(writeBuf);
        }
    }

    // pridelenie menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_newgame, menu);
        return true;
    }

    // položky menu
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newGame_server:
                if (lt != null) lt.cancel();
                lt = new ListeningThread(BluetoothAdapter.getDefaultAdapter(), NewGame.MY_UUID, conThread, mHandler);
                lt.start();

                zadajSlovo.setVisibility(View.GONE);
                hadajSlovo.setVisibility(View.VISIBLE);
                ed.setVisibility(View.VISIBLE);
                edZadaj.setVisibility(View.GONE);
                return true;
            case R.id.newGame_pripoj:
                Intent z = new Intent(this, DeviceListActivity.class);
                startActivityForResult(z, 9);

                zadajSlovo.setVisibility(View.VISIBLE);
                ed.setVisibility(View.GONE);
                edZadaj.setVisibility(View.VISIBLE);
                hadajSlovo.setVisibility(View.GONE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // nastavuje obrazok Hangmana podla levelu
    private void nastavHangman() {
        switch (level) {
            case 0:
                hangmanPic.setImageResource(R.drawable.h0);
                break;
            case 1:
                hangmanPic.setImageResource(R.drawable.h1);
                break;
            case 2:
                hangmanPic.setImageResource(R.drawable.h2);
                break;
            case 3:
                hangmanPic.setImageResource(R.drawable.h3);
                break;
            case 4:
                hangmanPic.setImageResource(R.drawable.h4);
                break;
            case 5:
                hangmanPic.setImageResource(R.drawable.h5);
                break;
            case 6:
                hangmanPic.setImageResource(R.drawable.h6);
                break;
            case 7:
                hangmanPic.setImageResource(R.drawable.h7);
                break;
            case 8:
                hangmanPic.setImageResource(R.drawable.h8);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lt != null) lt.cancel();
        if (ct != null) ct.cancel();
        if (conThread != null) conThread.cancel();
    }

    // metoda na ukoncenie aktivity
    public void koniecHry(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
