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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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

    SOSButton sosButton;

    File[] recordings;
    MediaPlayer mediaPlayer;
    Boolean playing = false;
    Integer playingIndex = 0;

    ChatManager chatManager;
    //Constructor for JS interface and passing context from MainActivity
    WebViewJSInterface(Context c, WebView webView, ChatManager chatManager, SOSButton a) {
        mContext = c;
        myWebview = webView;
        this.chatManager = chatManager;
        sosButton = a;
    }

    // Starts the recording processes
    @JavascriptInterface
    public void startRecording() {
        sosButton.toggleRecording();
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
            if(playingIndex == i){
                mediaPlayer.stop();
                playing = false;
            } else {
                mediaPlayer.stop();
                playing = false;
                mediaPlayer = MediaPlayer.create(mContext, Uri.parse(recordings[i].getAbsolutePath()));
                mediaPlayer.start();
                playing = true;
            }
        } else {
            mediaPlayer = MediaPlayer.create(mContext, Uri.parse(recordings[i].getAbsolutePath()));
            mediaPlayer.start();
            playing = true;
        }
    }
    @JavascriptInterface
    public void recieveMessages(String message){
        Log.d(message, "recieveMessages: " + message);
        chatManager.message = message;

    }
    @JavascriptInterface
    public void receivePhoneNumbers(String message){
        message = message.replace("&#34;", "\"");
        Log.d("TAG", "receivePhoneNumbers: " + message);

        try {
            String fileDir = mContext.getCacheDir() + "/emerContacts.json";
            File file = new File(fileDir);
            file.createNewFile();
            if (file.exists()) {
                OutputStream fo = new FileOutputStream(file);
                fo.write(message.getBytes(StandardCharsets.UTF_8));
                fo.close();
                System.out.println("file created: "+file);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }
    @JavascriptInterface
    public void changeName(String name, Integer i){
        File dir = new File(mContext.getFilesDir().getAbsolutePath() + name);
        recordings[i].renameTo(dir);
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
