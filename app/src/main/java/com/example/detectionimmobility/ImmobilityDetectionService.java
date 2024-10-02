package com.example.detectionimmobility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification notification = this.createNotification();
        startForeground(1001, notification);

        this.beepHelper = new BeepHelper();

        this.immobilityDetector = new ImmobilityDetector(this.getApplicationContext());
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
        super.onDestroy();

        /*Intent broadcastIntent = new Intent(this, RestartBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);*/

        immobilityDetector.stopListening();

        Log.d(SERVICE_NAME, "Service Detroyed !");
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

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =  PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);


        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             builder = new Notification.Builder(this, CHANNELID);
        } else {
            builder = new Notification.Builder(this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return builder
                    .setContentText("")
                    .setContentTitle("Detection Immobilité Active !")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
                    .build();
        } else {
            return builder
                    .setContentText("")
                    .setContentTitle("Detection Immobilité Active !")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentIntent(pendingIntent)
                    .build();
        }
    }

    @Override
    public void onMotionDetected() {
        this.beepHelper.beep(100);
    }
}