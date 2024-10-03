package com.example.detectionimmobility;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.detectionimmobility.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final String MYAPP = "PTI";

    PowerManager.WakeLock wakeLock;

    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.detectionimmobility.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        displayAndroidVersion();
        displayAndroidBatteryOptimisation();

        //handleBattery();
        handleWakeLock();

        if(!foregroundServiceRunning()) {
            serviceIntent = new Intent(this,  ImmobilityDetectionService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            }
        }
    }

    protected void displayAndroidVersion() {
        TextView androidVersionTextView = findViewById(R.id.androidVersion);
        androidVersionTextView.setText(String.valueOf(Build.VERSION.SDK_INT));
    }

    private void displayAndroidBatteryOptimisation() {
        TextView batterieOptimisationTextView = findViewById(R.id.batterieOptimisation);
        if(isBatteryOptimisationDisable()) {
            batterieOptimisationTextView.setText("Désactivée");
        } else {
            batterieOptimisationTextView.setText("Activée");
        }
    }


    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(ImmobilityDetectionService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        stopService(serviceIntent);
        wakeLock.release();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean handlePermissions( String permissionString, Integer myPermissionCode) {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissionString) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[1];
            permissions[0] = permissionString;

            ActivityCompat.requestPermissions(this, permissions, myPermissionCode);
            Log.i(MYAPP, "Asking for permission : " + permissionString);
            return true;
        } else {
            return false;
        }
    }

    private void handleWakeLock() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "DetectionImmobility::MyWakelockTag");
        wakeLock.acquire();
    }

    private void handleBattery() {
        Intent intent = new Intent();

        if(!this.isBatteryOptimisationDisable()) {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
        }
        this.startActivity(intent);
    }

    private boolean isBatteryOptimisationDisable() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm.isIgnoringBatteryOptimizations(getPackageName())) {
            return true;
        } else {
            return false;
        }
    }
}