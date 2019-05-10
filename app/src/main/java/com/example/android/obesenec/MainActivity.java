package com.example.android.obesenec;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tx;
    private Button button_novahra;
    private Button prepoj;
    private Typeface custom_font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nastavViews();
    }

    // nastavi Views
    private void nastavViews() {
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/KGBlankSpaceSketch.ttf");

        tx = (TextView) findViewById(R.id.textview1);
        button_novahra = (Button) findViewById(R.id.button_novahra);
        prepoj = (Button) findViewById(R.id.button_prepoj);

        tx.setTypeface(custom_font);
        button_novahra.setTypeface(custom_font);
        prepoj.setTypeface(custom_font);
    }

    // otvorí aktivitu pre nastavenie bluetooth
    public void prepojZariadenia(View view) {
        startActivity(new Intent(MainActivity.this, PrepojZariadenia.class));
    }

    // spusti aktivitu NewGame
    public void zacniNovuHru(View view) {
        startActivity(new Intent(MainActivity.this, NewGame.class));
    }
}
