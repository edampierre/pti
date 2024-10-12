package com.example.detectionimmobility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.detectionimmobility.ImmobilityDetector.ImmobilityDetector;
import com.example.detectionimmobility.ImmobilityDetector.MotionDetectedCallback;

public class ImmobilityDetectionService extends Service implements MotionDetectedCallback {

    private ImmobilityDetector immobilityDetector;
    private BeepHelper beepHelper;
    private final String SERVICE_NAME = "PTI_SERVICE";

    public ImmobilityDetectionService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.immobilityDetector = new ImmobilityDetector(this.getApplicationContext());
        this.beepHelper = new BeepHelper();

        Notification notification = this.createNotification();
        startForeground(1001, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        this.immobilityDetector.registerMotionCallback(this);
        this.immobilityDetector.startListening();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        immobilityDetector.stopListening();

        super.onDestroy();
    }

    private Notification createNotification() {
        final String CHANNELID = "Immobility Detection Channel ID";
        NotificationChannel channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    CHANNELID,
                    CHANNELID,
                    NotificationManager.IMPORTANCE_HIGH
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent =  PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadcastIntent = new Intent(getApplicationContext(), NotificationBroadcastReceiver.class);
        PendingIntent pendingBroadcastIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             builder = new NotificationCompat.Builder(this, CHANNELID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return builder
                    .setContentText("")
                    .setContentTitle("Detection Immobilité Active !")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .addAction(0, "I'm alive", pendingBroadcastIntent)
                    .build();
        } else {
            return builder
                    .setContentText("")
                    .setContentTitle("Detection Immobilité Active !")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentIntent(pendingIntent)
                    .addAction(0, "I'm alive", pendingBroadcastIntent)
                    .build();
        }
    }

    @Override
    public void onMotionDetected() {
        this.beepHelper.beep(100);
    }
}