package com.example.objection1;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

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
import android.view.MenuItem;
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
    String[] menuNoPages = { "login", "signup", "emrn", "lawyer", "done", "recordings", "chat"};
    WebView myWebView;
    BottomNavigationView nav;
    //On creation of main activity, finds WebView element, sets it up and starts it
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nav = findViewById(R.id.bottom_nav);

        myWebView = (WebView) findViewById(R.id.webview);
        ChatManager chatManager = new ChatManager(this, myWebView);

        WebSettings settings = myWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Boolean menu = true;
                for(int i = 0; i < menuNoPages.length; i++){
                    if (url.contains(menuNoPages[i]) || "https://objection1.herokuapp.com/".contains(url)) {
                        menu = false;
                    }
                    if(url.contains("login")){

                    }
                    if(url.contains("chat")){
                        chatManager.sendMessage("Hello world!", "0507355597");

                    }

                }
                if(menu){
                    nav.setVisibility(View.VISIBLE);
                } else{
                    nav.setVisibility(View.INVISIBLE);
                }
            };
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());

                return false;
            }
        });


        myWebView.loadUrl("https://objection1.herokuapp.com/");

        myWebView.addJavascriptInterface(new WebViewJSInterface(this, myWebView), "Android");

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

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                myWebView.goBack();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        Log.d(OnOffReceiver.SCREEN_TOGGLE_TAG, "onCreate: screenOnOffReceiver is registered.");

    }


    public void getHelp(MenuItem v){
        myWebView.loadUrl("https://objection1.herokuapp.com/emrn");

    }

    public void lawyersList(MenuItem v){
        Toast.makeText(this,"Coming Soon...",Toast.LENGTH_SHORT).show();
    }

    public void recordingsList(MenuItem v){
        myWebView.loadUrl("https://objection1.herokuapp.com/recordings");

    }

    public void lawLearnList(MenuItem v){
        Toast.makeText(this,"Coming Soon...",Toast.LENGTH_SHORT).show();
    }

    public void menuButton(MenuItem v){
        myWebView.loadUrl("https://objection1.herokuapp.com/settings");

    }
}