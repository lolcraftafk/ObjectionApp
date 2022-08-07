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

public class OnOffReceiver extends BroadcastReceiver{


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
    SOSButton sosButton;

    OnOffReceiver(SOSButton a){
        sosButton = a;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        mContext = context;



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
                count++;
                break;
            case Intent.ACTION_SCREEN_ON:
                count++;
                break;
            default:
                break;
        }

        if (count >= 5 && !timerDone) {
            count = 0;
            sosButton.toggleRecording();
        } else if (count >= 5 && timerDone) {
            count = 0;
        }
    }

}

