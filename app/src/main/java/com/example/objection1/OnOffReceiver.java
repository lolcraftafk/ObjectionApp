package com.example.objection1;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Timer;

public class OnOffReceiver extends BroadcastReceiver implements LocationListener {

    public final static String SCREEN_TOGGLE_TAG = "SCREEN_TOGGLE_TAG";

    Integer count = 0;
    MediaRecorder mediaRecorder;
    Boolean recording = false;
    Context mContext;
    File newFile;
    Boolean timerDone = true;
    CountDownTimer timer;
    Notifications notifications;
    Double longitude;
    Double latitude;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        mContext = context;
        Log.d("sa", "onReceive: ");

        if (count == 1) {
            timerDone = false;
            timer = new CountDownTimer(10000, 1) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    timerDone = true;
                }
            }.start();
        }

        switch (action) {
            case Intent.ACTION_SCREEN_OFF:
                Log.d(SCREEN_TOGGLE_TAG, "Screen is turn off.");
                count++;
                break;
            case Intent.ACTION_SCREEN_ON:
                Log.d(SCREEN_TOGGLE_TAG, "Screen is turn on.");
                count++;
                break;
            default:
                break;
        }

        Log.d(SCREEN_TOGGLE_TAG, "onReceive: " + timerDone);
        if (count >= 5 && !timerDone) {
            count = 0;
            notifications = new Notifications(context);
            if(!recording) {
                startRecording();
                notifications.createNotification(1,true,"Recording...", "The emergency sequence was sent, recording.", "The emergency sequence was sent, recording. Your emergency contacts were notified.", NotificationCompat.PRIORITY_HIGH);
                sendMessage();
                Log.d(SCREEN_TOGGLE_TAG, "onReceive: Started recording");
            } else {
                stopRecording();
                notifications.cancelNotification(1);
                Log.d(SCREEN_TOGGLE_TAG, "onReceive: Stopped recording");
            }

        } else if (count >= 5 && timerDone) {
            count = 0;
        }
    }

    public void startRecording() {
        //Checks whether the app is already recording or not, if yes- stops, otherwise initiates another reocrding session.
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);

        String fileDir = mContext.getFilesDir() + "/objection_";

        File directory = new File(fileDir);
        File[] files = directory.listFiles();

        String dateTimeNow = LocalDateTime.now().toString();

        String newFileName = fileDir + dateTimeNow.replace(".", "_").replace(":", "_") + ".mp3";

        newFile = new File(newFileName);

        mediaRecorder.setOutputFile(newFile.getAbsolutePath());
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            mediaRecorder.prepare();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();

        Toast.makeText(mContext, "Recording started...", Toast.LENGTH_SHORT).show();

        recording = true;
        }

    public void stopRecording() {
        mediaRecorder.stop();
        recording = false;
        Toast.makeText(mContext, "Recording stopped...", Toast.LENGTH_SHORT).show();
    }

    public void onLocationChanged(@NonNull Location location) {
        longitude=location.getLongitude();
        latitude=location.getLatitude();

    }

    public void sendMessage() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location a = locationManager.getLastKnownLocation(locationManager.getAllProviders().get(0));
        SmsManager sms=SmsManager.getDefault();
        String message = "hey, I called the  right now: https://www.google.com/maps/search/?api=1&query=" + String.valueOf(a.getLatitude()) + "%2C"+ String.valueOf(a.getLongitude());
        Log.d("TAG", "sendMessage: " + message);
        sms.sendTextMessage("0507355597",null, message , null,null);
    }
}

