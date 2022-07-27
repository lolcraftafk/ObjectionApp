package com.example.objection1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public TextView textView;

    Button getHelpButton;
    Button recordingsButton;
    Button lawyersButton;
    Button learnLawButton;
    Button menuButton;

    WebView myWebView;

    //On creation of main activity, finds WebView element, sets it up and starts it
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = (WebView) findViewById(R.id.webview);

        menuButton = findViewById(R.id.menuButton);
        learnLawButton = findViewById(R.id.learnLawButton);
        lawyersButton = findViewById(R.id.lawyersButton);
        recordingsButton  = findViewById(R.id.recordingsButton);
        getHelpButton = findViewById(R.id.getHelpButton);

        WebSettings settings = myWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        });


        myWebView.loadUrl("https://objection1.herokuapp.com/");

        myWebView.addJavascriptInterface(new WebViewJSInterface(this), "Android");

        IntentFilter intentFilter = new IntentFilter();

        // Add network connectivity change action.
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");

        // Set broadcast receiver priority.
        intentFilter.setPriority(100);

        // Create a network change broadcast receiver.
        OnOffReceiver reciever = new OnOffReceiver();

        // Register the broadcast receiver with the intent filter object.
        registerReceiver(reciever, intentFilter);


        Log.d(OnOffReceiver.SCREEN_TOGGLE_TAG, "onCreate: screenOnOffReceiver is registered.");


    }

    public void getHelp(View v){
        myWebView.loadUrl("https://objection1.herokuapp.com/emrn");
    }

    public void lawyersList(View v){
        Toast.makeText(this,"Coming Soon...",Toast.LENGTH_SHORT);
    }

    public void recordingsList(View v){
        Intent recordings = new Intent(this, VoiceRecordingsActivity.class);
        startActivity(recordings);
    }

    public void lawLearnList(View v){
        Toast.makeText(this,"Coming Soon...",Toast.LENGTH_SHORT);
    }

    public void menuButton(View v){
        Toast.makeText(this,"Coming Soon...",Toast.LENGTH_SHORT);
    }
    //
}