package com.example.objection1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Iterator;

public class SOSButton implements LocationListener {
    MediaRecorder mediaRecorder;
    Context mContext;
    File newFile;
    Boolean recording = false;
    Double longitude;
    Double latitude;
    Notifications notifications;
    public final static String SCREEN_TOGGLE_TAG = "SCREEN_TOGGLE_TAG";

    SOSButton(Context context) {
        mContext = context;
    }

    public void toggleRecording(){
        notifications = new Notifications(mContext);
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
        String message = "I'm in trouble, help me here!!: https://www.google.com/maps/search/?api=1&query=" + String.valueOf(a.getLatitude()) + "%2C"+ String.valueOf(a.getLongitude());
        JSONObject object;
        String[] numbers = {};
        String text = "";
        try {
            String fileDir = mContext.getCacheDir() + "/emerContacts.json";
            File file = new File(fileDir);
            file.createNewFile();
            if (file.exists()) {
                InputStream fo = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fo));
                String line;
                while ((line = reader.readLine()) != null)
                    text+=line;
                System.out.println("file created: "+file);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        try{
            object = (JSONObject) new JSONTokener(text).nextValue();
            numbers = new String[object.length()];
            Iterator<String> k = object.keys();
            for (int i = 0 ; i < object.length(); i++){
                numbers[i] =  object.getString( k.next());
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        for(int i= 0; i < numbers.length; i++){
            Log.d("TAG", "sendMessage: "+ numbers[i]);
            sms.sendTextMessage(numbers[i],null, message , null,null);
        }
    }
}
