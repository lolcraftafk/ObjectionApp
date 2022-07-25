package com.example.objection1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Timer;

public class OnOffReceiver extends BroadcastReceiver {

    public final static String SCREEN_TOGGLE_TAG = "SCREEN_TOGGLE_TAG";
    Integer count = 0;
    MediaRecorder mediaRecorder;
    Boolean recording = false;
    Context mContext;
    File newFile;
    Boolean timerDone = true;
    CountDownTimer timer;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        mContext = context;
        Log.d("sa", "onReceive: ");
        if(Intent.ACTION_SCREEN_OFF.equals(action))
        {
            Log.d(SCREEN_TOGGLE_TAG, "Screen is turn off.");
            count++;
        }else if(Intent.ACTION_SCREEN_ON.equals(action))
        {
            Log.d(SCREEN_TOGGLE_TAG, "Screen is turn on.");
            count++;
        }
        if(count >= 3 && !timerDone){
            startRecording();
            count = 0;

            Log.d(SCREEN_TOGGLE_TAG, "onReceive: Started recording");
        } else if (count == 1){
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
    }
        public void startRecording() {
            //Checks whether the app is already recording or not, if yes- stops, otherwise initiates another reocrding session.
            mediaRecorder = new MediaRecorder();

            if (recording) {
                mediaRecorder.stop();
                recording = false;
                Toast.makeText(mContext, "Recording stopped...", Toast.LENGTH_SHORT).show();
//                addMedia(newFile.getName(), newFile.getAbsolutePath());
//                Properties locations = new Properties();
//                if(location.exists()) {
//
//                    try (InputStream io = new FileInputStream(location)){
//                        locations.loadFromXML(io);
//                        locations.setProperty(newFile.getAbsolutePath(), mediaStoreUri.getPath());
//                    } catch (IOException e) {
//                        throw new RuntimeException();
//                    }
//
//                } else {
//                    OutputStream io;
//                    try{
//                        location.createNewFile();
//                        locations.setProperty(newFile.getAbsolutePath(), mediaStoreUri.getPath());
//                        io = new FileOutputStream(location);
//                        locations.storeToXML(io, "Added location");
//                    } catch (IOException e) {
//                        throw new RuntimeException();
//                    }
//
//                }
            } else {
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
        }
    }

