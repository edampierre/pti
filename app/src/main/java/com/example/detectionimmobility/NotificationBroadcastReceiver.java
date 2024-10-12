package com.example.detectionimmobility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Notification action received", Toast.LENGTH_SHORT).show();
        
        //context.startService(new Intent(context, ImmobilityDetectionService.class));
    }
}
