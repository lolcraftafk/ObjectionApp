package com.example.objection1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

public class VoiceRecordingsActivity extends AppCompatActivity {

    //Initiates necessary variables
    LinearLayout main;
    MediaPlayer mediaPlayer;
    File[] recordings;
    Context context;
    Boolean playing = false;

    //On opening of activity, creates a new media player, gets the main table, adds views for each file, and adds relevant functionality
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recordings);
        context = getApplicationContext();

        mediaPlayer = new MediaPlayer();

        main = findViewById(R.id.RecordingsList);

        File Dir = new File(getFilesDir().getAbsolutePath());
        recordings = Dir.listFiles();

        for (int i = 0; i < recordings.length; i ++ ) {

            LinearLayout row = new LinearLayout(context);
            TextView label = new TextView(this);
            Button play = new Button(this);
            Button share = new Button(this);

            play.setText("Play " + i);
            play.setTextColor(getColor(R.color.black));

            share.setText("Share " + i);
            share.setTextColor(getColor(R.color.black));

            play.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    if(playing){
                        mediaPlayer.stop();
                        playing = false;
                    } else {
                        Button button = (Button) v;
                        String a = (String) button.getText();
                        mediaPlayer = MediaPlayer.create(context, Uri.parse(recordings[Integer.parseInt(a.replace("Play ",""))].getAbsolutePath()));
                        mediaPlayer.start();
                        playing = true;
                    }

                }
            });

            // On click of share button - starts new intent with file in extras
            // TODO : Add JSON parser, and add public "content://" with file access for share
            share.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Intent sendIntent = new Intent();
                    Button button = (Button) v;
                    String a = (String) button.getText();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    String publicPath = sharedPref.getString(recordings[Integer.parseInt(a.replace("Share ",""))].getAbsolutePath(),"");
                    Log.i("TAG", publicPath);
                    Uri uri = Uri.parse("content://" + publicPath);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.setType("audio/mp3");
                    startActivity(sendIntent);
                }
            });

            label.setText(recordings[i].getName());
            label.setTextColor(Color.BLACK);

            row.addView(label);
            row.addView(play);
            row.addView(share);

            main.addView(row);
        }

    }

}