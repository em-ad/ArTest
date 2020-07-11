
// jcoola technologies, copyright @2013-2020
// www.jcoola.com
// -----------------------------------------

package com.jcoola.ar.unitybinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.jcoola.ar.OverrideUnityActivity;

public class UnityActivityContainer extends OverrideUnityActivity {

    String consumerFullClassName;

    // Setup activity layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        handleIntent(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
        setIntent(intent);
    }

    void handleIntent(Intent intent) {
        if (intent == null || intent.getExtras() == null) return;
        if (intent.getExtras().containsKey("consumer"))
            consumerFullClassName = intent.getExtras().getString("consumer");
    }

    @Override
    protected void showMainActivity(String property) {
        try {
            Intent intent = new Intent(UnityActivityContainer.this, Class.forName(consumerFullClassName));
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("setColor", property);
            startActivity(intent);
        } catch (Exception ex) {
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_ESCAPE) {
            mUnityPlayer.unload();
        } else return super.onKeyDown(keyCode, event);
        return true;
    }

    @Override
    public void onUnityPlayerUnloaded() {
        showMainActivity("");
        finish();
    }

    @Override
    public void onUnityPlayerQuitted() {
        finish();
    }


}