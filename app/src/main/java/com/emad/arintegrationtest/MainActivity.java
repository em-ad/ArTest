package com.emad.arintegrationtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.jcoola.itfamiliar.ArActivitySplash;

public class MainActivity extends AppCompatActivity {

    TextView tvClickable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvClickable = findViewById(R.id.tvClickable);
        tvClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ArActivitySplash.class));
            }
        });
    }
}