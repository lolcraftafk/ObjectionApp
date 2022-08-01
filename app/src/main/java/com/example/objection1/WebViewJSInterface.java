package com.example.objection1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.JsonReader;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class WebViewJSInterface {

    //Initiates the necessary variables
    Context mContext;
    File newFile;
    Boolean recording = false;
    MediaRecorder mediaRecorder;
    Uri mediaStoreUri;
    String locationsPath;
    WebView myWebview;

    File[] recordings;
    MediaPlayer mediaPlayer;
    Boolean playing = false;

    public String messageContent;

    //Constructor for JS interface and passing context from MainActivity
    WebViewJSInterface(Context c, WebView webView) {
        mContext = c;
        myWebview = webView;
    }

    // Starts the recording processes
    @JavascriptInterface
    public void startRecording() {
        //Checks whether the app is already recording or not, if yes- stops, otherwise initiates another reocrding session.
        mediaRecorder = new MediaRecorder();
        if (recording) {
            File location = new File(locationsPath);
            mediaRecorder.stop();
            recording = false;
            Toast.makeText(mContext, "Recording stopped...", Toast.LENGTH_SHORT).show();

            addMedia(newFile.getName(), newFile.getAbsolutePath());
            Properties locations = new Properties();
            if(location.exists()) {

                try (InputStream io = new FileInputStream(location)){
                    locations.loadFromXML(io);
                    locations.setProperty(newFile.getAbsolutePath(), mediaStoreUri.getPath());
                } catch (IOException e) {
                    throw new RuntimeException();
                }

            } else {
                OutputStream io;
                try{
                    location.createNewFile();
                    locations.setProperty(newFile.getAbsolutePath(), mediaStoreUri.getPath());
                    io = new FileOutputStream(location);
                    locations.storeToXML(io, "Added location");
                } catch (IOException e) {
                    throw new RuntimeException();
                }

            }
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




    //Opens the recordings Activity
    @JavascriptInterface
    public void openRecordings() {
        Intent recordings = new Intent(mContext, VoiceRecordingsActivity.class);
        mContext.startActivity(recordings);
    }

    //Asks for the required permissions
    @JavascriptInterface
    public void askForPermissions() {
        ActivityCompat.requestPermissions(new MainActivity(), new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.SEND_SMS}, 0);
    }

    @JavascriptInterface
    public void getRecordings(){
        File dir = new File(mContext.getFilesDir().getAbsolutePath());
        recordings = dir.listFiles();
        Log.d("TAG", "getRecordings: Getting recordings " + recordings.length);
        for (int i = 0; i < recordings.length; i++){
            myWebview.post(new Runnable() {
                @Override
                public void run() {
                    myWebview.loadUrl("javascript:addRecording()");
                }
            });
        }
    }

    @JavascriptInterface
    public void mediaPlayer(int i){
        if(playing){
            mediaPlayer.stop();
            playing = false;
        } else {
            mediaPlayer = MediaPlayer.create(mContext, Uri.parse(recordings[i].getAbsolutePath()));
            mediaPlayer.start();
            playing = true;
        }
    }
    @JavascriptInterface
    public void recieveMessages(String message){
        Log.d("TAG", "recieveMessages: HMNMN");
        messageContent = message;
        Log.d(message, "recieveMessages: " + message);
    }
    //Adds local app media to "content://" files
    public void addMedia(String name, String path) {
        ContentValues content = new ContentValues(3);
        content.put(MediaStore.Audio.AudioColumns.TITLE, name);
        content.put(MediaStore.Audio.AudioColumns.DATE_ADDED, LocalDateTime.now().toString()); content.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
        content.put(MediaStore.Audio.Media.DATA, path);

        ContentResolver resolver = mContext.getContentResolver();

        mediaStoreUri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, content);
        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mediaStoreUri));
    }


}
