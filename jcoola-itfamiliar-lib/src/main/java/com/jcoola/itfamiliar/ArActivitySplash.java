// jcoola technologies, copyright @2013-2020
// www.jcoola.com
// -----------------------------------------

package com.jcoola.itfamiliar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.ArCoreApk;
import com.jcoola.ar.unitybinding.UnityActivityContainer;

public class ArActivitySplash extends AppCompatActivity {

    private TextView textMessage;
    private TextView textCode;
    private AlertDialog dialog = null;

    boolean isUnityLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_activity_splash);
        checkArAvailability();
        initViews();
        handleIntent(getIntent());
    }

    private void initViews() {
        textMessage = (TextView) findViewById(R.id.textMessage);
        textCode = (TextView) findViewById(R.id.textCode);

        textMessage.setText(getApplicationContext().getResources().getString(R.string.ar_init_text));
        textCode.setText("");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
        setIntent(intent);
    }

    void handleIntent(Intent intent) {
        if (intent == null || intent.getExtras() == null) return;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) isUnityLoaded = false;
    }

    @Override
    public void onBackPressed() {
        finishActivity(1); //normal finish
        finish(); //in case ar is installing
    }

    public void loadUnity() {
        isUnityLoaded = true;
        Intent intent = new Intent(ArActivitySplash.this, UnityActivityContainer.class);
        intent.putExtra("consumer", getClass().getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        startActivityForResult(intent, 1);
    }

    public volatile ArCoreApk.Availability arAvailability;

    private void checkArAvailability() {

        arAvailability = ArCoreApk.getInstance().checkAvailability(ArActivitySplash.this);
        if (arAvailability.isTransient()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkArAvailability();
                }
            }, 100);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (ArSystem.activityStackNumber > 1) {
            ArSystem.activityStackNumber = 0;
            ArActivitySplash.this.finish();
            return;
        } else
            ArSystem.activityStackNumber = 1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            checkArAvailability();

            if (ArSystem.isFirstTimeRun(getApplicationContext()) && !isPackageInstalled("com.google.ar.core", getPackageManager())) {
                ArSystem.saveFirstTimeRun(getApplicationContext(), 1);

                textMessage.setText(getApplicationContext().getResources().getString(R.string.ar_installgpsar_text));
                textCode.setText("");

                installGooglePlayServicesForAR();
            } else {
                if (arAvailability == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE) {
                    notSupported();
                } else {
                    if (arAvailability == ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED
                            || arAvailability == ArCoreApk.Availability.UNKNOWN_CHECKING
                            || arAvailability == ArCoreApk.Availability.UNKNOWN_TIMED_OUT
                            || !isPackageInstalled("com.google.ar.core", getPackageManager())) {
                        textMessage.setText(getApplicationContext().getResources().getString(R.string.ar_installgpsar_text));
                        textCode.setText("");
                        installGooglePlayServicesForAR();
                    } else {
                        if (arAvailability != ArCoreApk.Availability.SUPPORTED_INSTALLED)
                            Toast.makeText(ArActivitySplash.this,
                                    "Warning: AR may not be supported on this phone!", Toast.LENGTH_LONG)
                                    .show();
                        ArSystem.activityStackNumber = 2;
                        loadUnity();
                    }
                }


            }
        } else {
            notSupportedRequiresAndroidN();
        }
    }

    private void notSupportedRequiresAndroidN() {
        textMessage.setText(getApplicationContext().getResources().getString(R.string.ar_requiresandroid7plus_text));
        textCode.setText("");
    }

    private void notSupported() {
        textMessage.setText(getApplicationContext().getResources().getString(R.string.ar_notsupported_text));
        textCode.setText("");
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void installGooglePlayServicesForAR() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.myAlert);
        dialog = builder.setCancelable(false)
                .setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert))
                .setPositiveButton("نصب از فروشگاه", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                                .setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.ar.core"));
                        ArSystem.isGoogleArInstalled = true;
                        Toast.makeText(ArActivitySplash.this,
                                getApplicationContext().getResources().getString(R.string.ar_installgpsar_text), Toast.LENGTH_LONG)
                                .show();
                        startActivity(goToMarket);
                    }
                }).setNegativeButton("فعلا نه", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ArActivitySplash.this, "بدون هسته ی واقعیت افزوده، اجرای این بخش مقدور نمیباشد", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).setTitle("نیازمند هسته اجرای واقعیت افزوده است")
                .setMessage("لطفا برای نصب هسته ی اجرای واقعیت افزوده به فروشگاه گوگل مراجعه کنید")
                .create();
        dialog.show();
    }
}
